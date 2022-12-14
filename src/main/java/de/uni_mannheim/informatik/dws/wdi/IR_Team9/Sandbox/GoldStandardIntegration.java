package de.uni_mannheim.informatik.dws.wdi.IR_Team9.Sandbox;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import de.uni_mannheim.informatik.dws.wdi.IR_Team9.Blocking.BLOCKERS;
import de.uni_mannheim.informatik.dws.wdi.IR_Team9.Comparators.MATCHING_RULES;
import de.uni_mannheim.informatik.dws.wdi.IR_Team9.Experiments.Experiment;
import de.uni_mannheim.informatik.dws.wdi.IR_Team9.model.Company;
import de.uni_mannheim.informatik.dws.wdi.IR_Team9.model.CompanyCSVCorrespondenceFormatter;
import de.uni_mannheim.informatik.dws.wdi.IR_Team9.model.CompanyXMLReader;
import de.uni_mannheim.informatik.dws.wdi.IR_Team9.utils.Constants;
import de.uni_mannheim.informatik.dws.wdi.IR_Team9.utils.MultiSimCorrespondence;
import de.uni_mannheim.informatik.dws.winter.datafusion.CorrespondenceSet;
import de.uni_mannheim.informatik.dws.winter.matching.MatchingEngine;
import de.uni_mannheim.informatik.dws.winter.matching.blockers.Blocker;
import de.uni_mannheim.informatik.dws.winter.matching.rules.MatchingRule;
import de.uni_mannheim.informatik.dws.winter.model.Correspondence;
import de.uni_mannheim.informatik.dws.winter.model.FusibleHashedDataSet;
import de.uni_mannheim.informatik.dws.winter.model.HashedDataSet;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;
import de.uni_mannheim.informatik.dws.winter.model.io.CSVCorrespondenceFormatter;
import de.uni_mannheim.informatik.dws.winter.processing.Processable;
import de.uni_mannheim.informatik.dws.winter.utils.WinterLogManager;

public class GoldStandardIntegration{

    private static final Logger logger = WinterLogManager.activateLogger("default");
    static String dbpediaPath = "data/input/dbpedia.xml";
    static String forbesPath = "data/input/Forbes_results.xml";
    static String dataworldPath = "data/input/dataworld_ts.xml";
    static String kagglePath = "data/input/companies_shorted_results.xml";

    public static void standardIntegration(String pathToDataSet1, String pathToDataSet2, MatchingRule<Company,Attribute> rule, Blocker<Company, Attribute, Company, Attribute> blocker, String outputName) throws Exception{
        // loading data
        logger.info("*\tLoading datasets\t*");
        HashedDataSet<Company, Attribute> dataset1 = new HashedDataSet<>();
        HashedDataSet<Company, Attribute> dataset2 = new HashedDataSet<>();

        new CompanyXMLReader().loadFromXML(new File(pathToDataSet1), "/Companies/Company", dataset1);
        new CompanyXMLReader().loadFromXML(new File(pathToDataSet2), "/Companies/Company", dataset2);

        //--------
        //Identity Resolution
        //--------
        
        // Initialize Matching Engine
        MatchingEngine<Company, Attribute> engine = new MatchingEngine<>();

        //Execute the matching
		logger.info("*\tRunning identity resolution\t*");
		Processable<Correspondence<Company, Attribute>> correspondences = engine.runIdentityResolution(dataset1, dataset2, null, rule, blocker);

        // write the correspondences to the output file
		new CSVCorrespondenceFormatter().writeCSV(new File("data/output/gs/"+outputName+".csv"), correspondences);
        new CompanyCSVCorrespondenceFormatter().writeCSV(new File("data/output/namedgs/"+outputName+".csv"), correspondences);
    }


    /*
     * Method to read a single correspondence file and put the contents into a collection
     */
    private static void readCorrespondenceFile(Map<String, MultiSimCorrespondence> c, String path, Integer simId) {
        System.out.println("[INFO ] reading correspondence file: " + path);
        try(CSVReader reader = new CSVReader(new FileReader(path))){
            for(String[] tokens : reader.readAll()){

                MultiSimCorrespondence cf = new MultiSimCorrespondence();
                cf.id1 = tokens[0];
                cf.id2 = tokens[1];
                cf.setSimAtID(simId, Double.parseDouble(tokens[2]));
                cf.name1 = tokens[3];
                cf.name2 = tokens[4];

                String key = cf.id1 + cf.id2;

                //System.out.println(cf);

                if (c.containsKey(key)){
                    c.get(key).setSimAtID(simId, Double.parseDouble(tokens[2]));
                } else{
                    c.put(key, cf);
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        
    }

    /*
     * Read all correspondance files and write them to a combined one.
     */
    
    public static void makeCombinedCorrespondenceFile(String source1, String source2, String toPath, String blockerIndication) throws Exception{
        //read files
        String path1 = "data/output/namedgs/gs_"+source1+"_"+source2+"_"+blockerIndication+"_1.csv";
        String path2 = "data/output/namedgs/gs_"+source1+"_"+source2+"_"+blockerIndication+"_2.csv";
        String path3 = "data/output/namedgs/gs_"+source1+"_"+source2+"_"+blockerIndication+"_3.csv";

        Map<String, MultiSimCorrespondence> correspondences = new TreeMap<>();

        readCorrespondenceFile(correspondences, path1, 1);
        readCorrespondenceFile(correspondences, path2, 2);
        readCorrespondenceFile(correspondences, path3, 3);

        //System.out.println(correspondences.size());


        //write that out to a file again
        writeCombinedCSV(toPath, correspondences);
    }


    /*
     * Writes a CSV containing all basic similarity scores.
     * 
     */
    private static void writeCombinedCSV(String path, Map<String, MultiSimCorrespondence> correspondences) throws Exception{
        try(CSVWriter writer = new CSVWriter(new FileWriter(path))){

            for (MultiSimCorrespondence cf: correspondences.values()) {
                String[] values = {cf.id1, cf.id2, Double.toString(cf.sim1), Double.toString(cf.sim2), Double.toString(cf.sim3), cf.name1, cf.name2};
                writer.writeNext(values);
            }

        }
    }


    private static void calculateAllSimilarities(String source1, 
                                                String source2,
                                                String path1,
                                                String path2,
                                                Blocker<Company, Attribute, Company, Attribute> blocker,
                                                MatchingRule<Company, Attribute> m1,
                                                MatchingRule<Company, Attribute> m2,
                                                MatchingRule<Company, Attribute> m3,
                                                String blockerIndication)
                                        throws Exception{
        standardIntegration(path1, path2, m1, blocker, "gs_"+source1+"_"+source2+"_"+blockerIndication+"_1");
        standardIntegration(path1, path2, m2, blocker, "gs_"+source1+"_"+source2+"_"+blockerIndication+"_2");
        standardIntegration(path1, path2, m3, blocker, "gs_"+source1+"_"+source2+"_"+blockerIndication+"_3");
    }


    private static void kaggleIntegration(String otherDsName) throws Exception{
        int[] rules = new int[]{1,2,21};
        //String[] kaggleSets = Constants.getAggregateKagglePartitionedDSNames();
        String kagglePath = Constants.getDatasetPath("kaggle_f");

        String id;
        
        //for(int set = 0; set < kaggleSets.length; set++){

            Map<String, Integer> toBeCombined = new HashMap<>();

            for(int rule = 0; rule < rules.length; rule++){

                //run Experiment
                Experiment.runForDatasetCombination(
                    otherDsName,
                    //kaggleSets[set],
                    "kaggle_f",
                    100, 
                    0.2,
                    new double[]{0.5},
                    10, 
                    rules[rule], 
                    false, 
                    Experiment.getConductedExperiments());

                id = Experiment.getID(otherDsName, "kaggle_f", rules[rule], 10, 0.2);

                //write top k correspondences
                try{

                    logger.info("Making top k corresponcences");
                    MultiSimCorrespondence.writeTopKCorrespondences(
                        Constants.getExperimentCompanyCorrPath(otherDsName, "kaggle_f", id),
                        Constants.getSortedCorrespondencesPath(id, true),
                        10000,
                        true,
                        true);
                }catch(NoSuchFileException e){
                    e.printStackTrace();
                }

                //combine top k correspondences
                toBeCombined.put(id, rule+1);
            }

            //combine top k correspondences
            MultiSimCorrespondence.makeCombinedCorrespondenceFile(
                toBeCombined,
                Constants.getCombinedMultiSimFilePath(otherDsName, "kaggle_f",
                ""),
                true);

        //}

        
    }

    /**
     * Function to test if a certain dataset is able to be processed
     * @throws Exception
     */
    public static void testPartitions() throws Exception{

        CompanyXMLReader cr = new CompanyXMLReader();
        HashedDataSet<Company, Attribute> dbPedia = new HashedDataSet<>();

		cr.loadFromXML(new File(Constants.getDatasetPath("dbpedia")), Constants.RECORD_PATH, dbPedia);

        HashedDataSet<Company, Attribute> kaggle12 = new HashedDataSet<>();
        // cr.loadFromXML(new File(Constants.getDatasetPath("kaggle_a_1")), Constants.RECORD_PATH, kaggle12);
        // cr.loadFromXML(new File(Constants.getDatasetPath("kaggle_a_2")), Constants.RECORD_PATH, kaggle12);
        cr.loadFromXML(new File(Constants.getDatasetPath("_kaggle")), Constants.RECORD_PATH, kaggle12);


        Blocker<Company,Attribute,Company,Attribute> blocker = BLOCKERS.getBlocker9();

        MatchingEngine<Company, Attribute> engine = new MatchingEngine<>();

        Processable<Correspondence<Company, Attribute>> correspondences = engine.runIdentityResolution(dbPedia, kaggle12, null, MATCHING_RULES.getMR1(0.85), blocker);

    }


    public static void main(String[] args) throws Exception{
        // LinearCombinationMatchingRule<Company, Attribute> matchingRule1 = new LinearCombinationMatchingRule<>(0.5);
        // LinearCombinationMatchingRule<Company, Attribute> matchingRule2 = new LinearCombinationMatchingRule<>(0.5);
        // LinearCombinationMatchingRule<Company, Attribute> matchingRule3 = new LinearCombinationMatchingRule<>(0.5);

        // matchingRule1.activateDebugReport("data/output/IR_forbes_dbpedia_debug.csv", 3000);

        // matchingRule1.addComparator(new CompanyNameComparatorJaccardNgram(3, true), 1);
        // matchingRule2.addComparator(new CompanyNameComparatorLevenshtein(true), 1);
        // matchingRule3.addComparator(new CompanyNameComparatorLevenshtein(true), 0.5);
        // matchingRule3.addComparator(new CompanyNameComparatorJaccardNgram(3, true), 0.5);

        // //StandardRecordBlocker<Company, Attribute> blocker = new StandardRecordBlocker<Company, Attribute>(new CompanyBlockingKeyByNameGenerator());

        // StandardRecordBlocker<Company, Attribute> blocker = new StandardRecordBlocker<Company, Attribute>(new CompanyQgramBlocking(3));


        // String inPath;
        // String kaggleSource;
        // for (int i = 1; i <= 25; i++){

        //     kaggleSource = "kaggle_"+Integer.toString(i);
        //     inPath = "data/input/test/"+kaggleSource+".xml";

        //     calculateAllSimilarities("dbpedia", kaggleSource, dbpediaPath, inPath, blocker, matchingRule1, matchingRule2, matchingRule3);
        //     makeCombinedCorrespondenceFile("dbpedia", kaggleSource, "data/output/combinedFiles/dbpedia_"+kaggleSource+".csv");
        // }

        //for everything without kaggle
        // calculateAllSimilarities("dw", "forbes", dataworldPath, forbesPath, blocker, matchingRule1, matchingRule2, matchingRule3, "q");
        //makeCombinedCorrespondenceFile("dw", "forbes", "data/output/combinedFiles/dw_forbes_q.csv", "q");

        /*
         * IDs without name:
         * -Kaggle_192622 (1)
         * -Kaggle_512292 (1)
         * -Kaggle_2599908 (4)
         */

        // String[] dsNames = new String[] {"forbes", "dw"};
        // for(String dsName : dsNames){
        //     kaggleIntegration(dsName);
        // }

        //testPartitions();

        



    }
}