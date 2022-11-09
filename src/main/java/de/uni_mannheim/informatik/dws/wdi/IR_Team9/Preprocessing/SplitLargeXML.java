package de.uni_mannheim.informatik.dws.wdi.IR_Team9.Preprocessing;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.ClosedDirectoryStreamException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

import au.com.bytecode.opencsv.CSVReader;
import de.uni_mannheim.informatik.dws.wdi.IR_Team9.Blocking.CompanyQgramBlocking;
import de.uni_mannheim.informatik.dws.wdi.IR_Team9.Comparators.CompanyNameComparatorJaccardNgram;
import de.uni_mannheim.informatik.dws.wdi.IR_Team9.model.Company;
import de.uni_mannheim.informatik.dws.wdi.IR_Team9.model.CompanyCSVCorrespondenceFormatter;
import de.uni_mannheim.informatik.dws.wdi.IR_Team9.model.CompanyXMLReader;
import de.uni_mannheim.informatik.dws.wdi.IR_Team9.utils.Constants;
import de.uni_mannheim.informatik.dws.winter.matching.MatchingEngine;
import de.uni_mannheim.informatik.dws.winter.matching.blockers.StandardRecordBlocker;
import de.uni_mannheim.informatik.dws.winter.matching.rules.LinearCombinationMatchingRule;
import de.uni_mannheim.informatik.dws.winter.model.Correspondence;
import de.uni_mannheim.informatik.dws.winter.model.HashedDataSet;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;
import de.uni_mannheim.informatik.dws.winter.processing.Processable;

public class SplitLargeXML {

    private static final String FILENAME = "data/input/companies_shorted_results.xml"; //companies_shorted_results.xml
    private static Integer maxID;


    private static void createNewFile(BufferedReader br, String filename, Integer fileID, Integer lineLimit, String firstLine, String openingTag, String lastTested) throws Exception{
        maxID = fileID;
        File file = new File(filename+"_"+Integer.toString(fileID)+".xml");

        Integer row = 0;
        boolean closedCompanyTag = false;

        //Buffered Writer
        try(BufferedWriter bw = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8))){

            bw.write(firstLine);
            bw.newLine();
            bw.write(openingTag);
            bw.newLine();
            bw.write(lastTested);
            bw.newLine();

            String line;

            while((line = br.readLine()) != null){

                if(row < lineLimit || !closedCompanyTag){
                    bw.write(line);
                    bw.newLine();

                    if(row > lineLimit){
                        closedCompanyTag = line.contains("</Company>");
                    }
                }else{
                    createNewFile(br, filename, fileID+1, lineLimit, firstLine, openingTag, line);
                    break;
                }
                row++;

                //System.out.println(line);
            }

            if(fileID != maxID){
                bw.write("</Companies>");
            }  
        }
    }

    public static void splitXML(String inFile, String outPath, String filenameRoot, Integer lineLimit) throws Exception{
        try (BufferedReader br = new BufferedReader(new FileReader(inFile, StandardCharsets.UTF_8))){
            String firstLine = br.readLine();
            String secondLine = br.readLine();

            createNewFile(br, outPath+filenameRoot, 1, lineLimit, firstLine, secondLine, "");
        }
    }

    public static Set<String> getDeduplicatedMathchesFromSource(String path, int idCol) throws Exception{
        HashSet<String> dedupIDs = new HashSet<>();
        try(CSVReader reader = new CSVReader(new FileReader(path))){
            String id;

            for(String[] line : reader.readAll()){
                id = line[idCol];
                dedupIDs.add(id);
            }
        }

        return dedupIDs;
    }

    public static void removeNonMatchesFromInputFile(String sourcePath, String corrPath, int sourceIDcol, String toPath) throws Exception{
        //if exists source path, take correspondences from there otherwise run IR
        System.out.println("[INFO ] Removing probable non-matches ...");
        if (Files.exists(Paths.get(corrPath))){
            Set<String> dedupIDs = getDeduplicatedMathchesFromSource(corrPath, sourceIDcol);

            System.out.println(String.format("[INFO ] got deduplicated set of size %d", dedupIDs.size()));

            //go through source and only keep elements with ID in this set
            try(BufferedReader br = new BufferedReader(new FileReader(sourcePath, StandardCharsets.UTF_8))){
                try(BufferedWriter bw = new BufferedWriter(new FileWriter(toPath, StandardCharsets.UTF_8))){
                    bw.write(br.readLine()); //first line
                    bw.newLine();
                    bw.write(br.readLine()); //root element
                    bw.newLine();

                    String openingTag = "<Company>";

                    String line;
                    String id;
                    boolean writeTillClosingTag = false;
                    while((line = br.readLine()) != null){

                        if(writeTillClosingTag){
                            bw.write(line);
                            bw.newLine();
                            writeTillClosingTag = !line.trim().matches("</Company>");
                        }      
                        else if (line.trim().matches("<ID>.*</ID>")){
                            id = line.substring(line.indexOf(">")+1, line.lastIndexOf("<"));
                            if(dedupIDs.contains(id)){
                                writeTillClosingTag = true;
                                bw.write(openingTag);
                                bw.newLine();
                                bw.write(line);
                                bw.newLine();
                            }
                        }
                    }

                    bw.write("</Companies>");
                }
            }

        }


    }

    /**
     * 
     * @param ds1 combined dataset of dw, forbes, dbpedia
     * @param ds2 kaggle partitioned dataset
     * @param fileID kaggle partition id
     * @param corrPathRoot path root for reduction correspondence files
     * @param toPath path to write the reduced XML to
     * @throws Exception
     */
    public static void reduceKaggleXML(HashedDataSet<Company, Attribute> ds1, HashedDataSet<Company, Attribute> ds2,
        Integer fileID, String corrPathRoot, 
        String toPath) throws Exception{

            String corrPath = String.format("%s%d.csv", corrPathRoot, fileID); //e.g. "data/output/reducedKaggleSet/1.csv"

            if (Files.exists(Paths.get(corrPath))){ //IR has already been run
                removeNonMatchesFromInputFile(Constants.getKagglePath(fileID), corrPath, 1, toPath);
            }
            
            else{// run IR
                runBasicIR(ds1, ds2, corrPath);
                removeNonMatchesFromInputFile(Constants.getKagglePath(fileID), corrPath, 1, toPath);
            }
    }

    /**
     * Runs basic IR with low threshhold to identify propably non-matching pairs that can be reduced from the input
     * 
     * @param ds1
     * @param ds2
     * @param toPath
     * @throws Exception
     */
    public static void runBasicIR(HashedDataSet<Company, Attribute> ds1, HashedDataSet<Company, Attribute> ds2, String toPath) throws Exception{
        System.out.println("[INFO ] Running basic IR ...");
        
        LinearCombinationMatchingRule<Company, Attribute> matchingRule = new LinearCombinationMatchingRule<>(0.3);

        matchingRule.addComparator(new CompanyNameComparatorJaccardNgram(3, true), 1);

        StandardRecordBlocker<Company, Attribute> blocker = new StandardRecordBlocker<Company, Attribute>(new CompanyQgramBlocking(4));

        //--------
        //Identity Resolution
        //--------
        
        // Initialize Matching Engine
        MatchingEngine<Company, Attribute> engine = new MatchingEngine<>();

        //Execute the matching
		Processable<Correspondence<Company, Attribute>> correspondences = engine.runIdentityResolution(ds1, ds2, null, matchingRule, blocker);

        // write the correspondences to the output file
        new CompanyCSVCorrespondenceFormatter().writeCSV(new File(toPath), correspondences);
    }



    public static void main(String[] args) throws Exception{

        // Integer lineLimit = 1000000;

        // splitXML(FILENAME, "data/input/test/", "kaggle", lineLimit);

        //Load all but Kaggle Datasets
        HashedDataSet<Company, Attribute> allCompanies = new HashedDataSet<>();
		new CompanyXMLReader().loadFromXML(new File(Constants.getDatasetPath("dbpedia")), Constants.RECORD_PATH, allCompanies);
		new CompanyXMLReader().loadFromXML(new File(Constants.getDatasetPath("forbes")), Constants.RECORD_PATH, allCompanies);
        new CompanyXMLReader().loadFromXML(new File(Constants.getDatasetPath("dw")), Constants.RECORD_PATH, allCompanies);


        //Run a basic similarity function and only keep Kaggle entries that have at least 40% match
        HashedDataSet<Company, Attribute> kaggle;
        CompanyXMLReader cr = new CompanyXMLReader();
		

        
        //removeNonMatchesFromInputFile("data/input/test/kaggle_1.xml", "data/output/reducedKaggleSet/1.csv", 1, "data/output/reducedKaggleSet/kaggle_1_red.xml");
        String toPath;
        for(int i = 1; i <= Constants.getMaxKaggleID(); i++){
            System.out.println("[INFO ] Reducing file " + Constants.getDatasetPath("kaggle", i));

            kaggle = new HashedDataSet<>();
            cr.loadFromXML(new File(Constants.getDatasetPath("kaggle", i)), Constants.RECORD_PATH, kaggle);

            toPath = String.format("data/output/reducedKaggleSet/kaggle_%d_red.xml", i);

            reduceKaggleXML(allCompanies, kaggle, i, "data/output/reducedKaggleSet/", toPath);
        }
        

    }

}