package de.uni_mannheim.informatik.dws.wdi.IR_Team9.Experiments;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import org.slf4j.Logger;

import com.github.andrewoma.dexx.collection.HashMap;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import de.uni_mannheim.informatik.dws.wdi.IR_Team9.Blocking.CompanyBlockingKeyByNameGenerator;
import de.uni_mannheim.informatik.dws.wdi.IR_Team9.Comparators.CompanyNameComparatorJaccardNgram;
import de.uni_mannheim.informatik.dws.wdi.IR_Team9.Comparators.CompanyNameComparatorLevenshtein;
import de.uni_mannheim.informatik.dws.wdi.IR_Team9.model.Company;
import de.uni_mannheim.informatik.dws.wdi.IR_Team9.model.CompanyCSVCorrespondenceFormatter;
import de.uni_mannheim.informatik.dws.wdi.IR_Team9.model.CompanyXMLReader;
import de.uni_mannheim.informatik.dws.wdi.IR_Team9.utils.CorrespondenceFile;
import de.uni_mannheim.informatik.dws.winter.matching.MatchingEngine;
import de.uni_mannheim.informatik.dws.winter.matching.blockers.Blocker;
import de.uni_mannheim.informatik.dws.winter.matching.blockers.StandardRecordBlocker;
import de.uni_mannheim.informatik.dws.winter.matching.rules.LinearCombinationMatchingRule;
import de.uni_mannheim.informatik.dws.winter.matching.rules.MatchingRule;
import de.uni_mannheim.informatik.dws.winter.model.Correspondence;
import de.uni_mannheim.informatik.dws.winter.model.HashedDataSet;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;
import de.uni_mannheim.informatik.dws.winter.model.io.CSVCorrespondenceFormatter;
import de.uni_mannheim.informatik.dws.winter.processing.Processable;
import de.uni_mannheim.informatik.dws.winter.processing.SysOutDatasetIterator;
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
    private static HashMap<String, CorrespondenceFile> readCorrespondenceFile(HashMap<String, CorrespondenceFile> correspondences, String path, Integer simId) throws Exception{
               
        try(CSVReader reader = new CSVReader(new FileReader(path))){
            for(String[] tokens : reader.readAll()){
                CorrespondenceFile cf = new CorrespondenceFile();
                cf.id1 = tokens[0];
                cf.id2 = tokens[1];
                cf.setSimAtID(simId, Double.parseDouble(tokens[2]));
                cf.name1 = tokens[3];
                cf.name2 = tokens[4];

                String key = cf.id1 + cf.id2;

                if (correspondences.containsKey(key)){
                    correspondences.get(key).setSimAtID(simId, Double.parseDouble(tokens[3]));
                }

                correspondences.put(key, cf);
            }
        }

        return correspondences;
    }

    /*
     * Read all correspondance files and write them to a combined one.
     */
    
    public static void makeCombinedCorrespondenceFile(String source1, String source2, String toPath) throws Exception{
        //read files
        String path1 = "data/output/namedgs/gs_"+source1+"_"+source2+"_1.csv";
        String path2 = "data/output/namedgs/gs_"+source1+"_"+source2+"_2.csv";
        String path3 = "data/output/namedgs/gs_"+source1+"_"+source2+"_3.csv";

        HashMap<String, CorrespondenceFile> correspondences = new HashMap<>();

        correspondences = readCorrespondenceFile(correspondences, path1, 1);
        correspondences = readCorrespondenceFile(correspondences, path2, 1);
        correspondences = readCorrespondenceFile(correspondences, path3, 3);

        System.out.println(correspondences.size());

        //write that out to a file again
        writeCombinedCSV(toPath, correspondences);
    }


    /*
     * Writes a CSV containing all basic similarity scores.
     * 
     */
    private static void writeCombinedCSV(String path, HashMap<String, CorrespondenceFile> correspondences) throws Exception{
        try(CSVWriter writer = new CSVWriter(new FileWriter(path))){

            for (CorrespondenceFile cf: correspondences.values()) {
                String[] values = {cf.id1, cf.id2, Double.toString(cf.sim1), Double.toString(cf.sim2), Double.toString(cf.sim3), cf.name1, cf.name2};
                writer.writeNext(values);
            }

        }
    }


    private static void calculateAllSimilarities(String source1, String source2, String path1, String path2, Blocker<Company, Attribute, Company, Attribute> blocker, MatchingRule<Company, Attribute> m1, MatchingRule<Company, Attribute> m2, MatchingRule<Company, Attribute> m3) throws Exception{
        standardIntegration(forbesPath, dbpediaPath, m1, blocker, "gs_"+source1+"_"+source2+"_1");
        standardIntegration(forbesPath, dbpediaPath, m2, blocker, "gs_"+source1+"_"+source2+"_2");
        standardIntegration(forbesPath, dbpediaPath, m3, blocker, "gs_"+source1+"_"+source2+"_3");
    }


    public static void main(String[] args) throws Exception{
        LinearCombinationMatchingRule<Company, Attribute> matchingRule1 = new LinearCombinationMatchingRule<>(0.7);
        LinearCombinationMatchingRule<Company, Attribute> matchingRule2 = new LinearCombinationMatchingRule<>(0.7);
        LinearCombinationMatchingRule<Company, Attribute> matchingRule3 = new LinearCombinationMatchingRule<>(0.7);

        matchingRule1.addComparator(new CompanyNameComparatorJaccardNgram(3), 1);
        matchingRule2.addComparator(new CompanyNameComparatorLevenshtein(), 1);
        matchingRule3.addComparator(new CompanyNameComparatorLevenshtein(), 0.5);
        matchingRule3.addComparator(new CompanyNameComparatorJaccardNgram(3), 0.5);

        StandardRecordBlocker<Company, Attribute> blocker = new StandardRecordBlocker<Company, Attribute>(new CompanyBlockingKeyByNameGenerator());

        // for (int i = 1; i <= 10; i++){
        //     inPath = "data/input/test/kaggle_"+Integer.toString(i)+".xml";
        //     gsi.standardIntegration(forbesPath, inPath, matchingRule3, blocker, "data/output/gs/gs_forbes_kaggle"+Integer.toString(i)+"_3.csv");
        // }

        //calculateAllSimilarities("dbpedia", "forbes", dbpediaPath, forbesPath, blocker, matchingRule1, matchingRule2, matchingRule3);
        makeCombinedCorrespondenceFile("dbpedia", "forbes", "data/output/combinedFiles/dbpedia_forbes.csv");




        /*
         * IDs without name:
         * -Kaggle_192622 (1)
         * -Kaggle_512292 (1)
         * -Kaggle_2599908 (4)
         */

        



    }
}