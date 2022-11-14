package de.uni_mannheim.informatik.dws.wdi.IR_Team9.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyException;
import java.util.HashSet;
import java.util.Set;

import au.com.bytecode.opencsv.CSVReader;
import de.uni_mannheim.informatik.dws.wdi.IR_Team9.Blocking.CompanyQgramBlocking;
import de.uni_mannheim.informatik.dws.wdi.IR_Team9.Comparators.CompanyNameComparatorJaccardNgram;
import de.uni_mannheim.informatik.dws.wdi.IR_Team9.model.Company;
import de.uni_mannheim.informatik.dws.wdi.IR_Team9.model.CompanyCSVCorrespondenceFormatter;
import de.uni_mannheim.informatik.dws.wdi.IR_Team9.model.CompanyXMLReader;
import de.uni_mannheim.informatik.dws.winter.matching.MatchingEngine;
import de.uni_mannheim.informatik.dws.winter.matching.blockers.StandardRecordBlocker;
import de.uni_mannheim.informatik.dws.winter.matching.rules.LinearCombinationMatchingRule;
import de.uni_mannheim.informatik.dws.winter.model.Correspondence;
import de.uni_mannheim.informatik.dws.winter.model.HashedDataSet;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;
import de.uni_mannheim.informatik.dws.winter.processing.Processable;

public class KaggleUtils {

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
        
        LinearCombinationMatchingRule<Company, Attribute> matchingRule = new LinearCombinationMatchingRule<>(0.85);

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


    public static void reduceKaggleXMLs(int startID, int endID) throws Exception{
        //check bounds for loop
        if(endID > Constants.getMaxKaggleID()){
            endID = Constants.getMaxKaggleID();
        }

        if(startID < 1){
            startID = 1;
        }

        //Load all but Kaggle Datasets into one Dataset
        HashedDataSet<Company, Attribute> allCompanies = new HashedDataSet<>();
		new CompanyXMLReader().loadFromXML(new File(Constants.getDatasetPath("dbpedia")), Constants.RECORD_PATH, allCompanies);
		new CompanyXMLReader().loadFromXML(new File(Constants.getDatasetPath("forbes")), Constants.RECORD_PATH, allCompanies);
        new CompanyXMLReader().loadFromXML(new File(Constants.getDatasetPath("dw")), Constants.RECORD_PATH, allCompanies);


        //Run a basic similarity function and only keep Kaggle entries that have at least 40% match
        HashedDataSet<Company, Attribute> kaggle;
        CompanyXMLReader cr = new CompanyXMLReader();
		

        
        
        String toPath;
        for(int i = startID; i <= endID; i++){
            System.out.println("[INFO ] Reducing file " + Constants.getDatasetPath("kaggle", i));

            kaggle = new HashedDataSet<>();
            cr.loadFromXML(new File(Constants.getDatasetPath("kaggle", i)), Constants.RECORD_PATH, kaggle);

            toPath = String.format("data/output/reducedKaggleSet/kaggle_%d_red.xml", i);
            runBasicIR(allCompanies, kaggle, toPath);
        }
    }

    /**
     * Aggregates reduced kaggle files into larger partitions.
     * @param numTargetPartitions number of desired final partitions
     */
    public static void combineReducedKaggleXMLs(int numTargetPartitions){
        int numReduced = Constants.getNumKaggleReducedPartitions(); //number of reduced partitons

        double xmlPerPartition = Math.ceil((1d*numReduced)/numTargetPartitions);

        int currID = 0;
        String line;

        for(int i = 1; i <= numTargetPartitions; i++){
            try(BufferedWriter bw = new BufferedWriter(new FileWriter(Constants.getKaggleAggregatedRedXMLPath(i),StandardCharsets.UTF_8))){
                for(int j = 1; j <= xmlPerPartition; j++){

                    currID++; //increase overall ID counter

                    System.out.println(String.format("[INFO ] Reading file %d ...", currID));

                    try(BufferedReader br = new BufferedReader(new FileReader(Constants.getKaggleReducedXMLPath(currID),StandardCharsets.UTF_8))){
                        //read file to StrinBuffer 
                        while((line = br.readLine()) != null){

                            //The first file writes opening tags
                            if(j == 1 && (line.contains("<Companies") || line.contains("xml version"))){
                                bw.write(line);
                                bw.write("\n");
                            }

                            //others ommit opening and closing tags. 
                            if(!line.contains("Companies") && !line.contains("xml version")  && currID<numReduced){
                                bw.write(line);
                                bw.write("\n");
                            }

                            //last writes closing tag
                            if((j == xmlPerPartition || currID == numReduced) && line.contains("</Companies>")){
                                bw.write(line);
                                bw.write("\n");
                            }
                        }
                    }catch(FileNotFoundException e){
                        System.out.println(String.format("[WARNING ] no file with the id %d ...", currID));
                    }
                }
            }catch(IOException e){
                e.printStackTrace();
            }
        }
    }

    public static boolean checkLastLineKaggleAggreagted(int id) throws Exception{
        String line;
        String prevLine ="";
        try(BufferedReader br = new BufferedReader(new FileReader(Constants.getKaggleAggregatedRedXMLPath(1)))){
            
            while((line = br.readLine()) != null){
                prevLine = line;
            }

            System.out.println(prevLine);
        }

        return prevLine.contains("</Companies>");
    }


    public static Set<String> keepPercentageOrAboveThresh(double thresh, double percentage, boolean debug) throws Exception{
        //read all correspondence file
            //keep and write to final file if
                //similarity above a certain threshold or a certain percentage flag is true

        System.out.println("Starting to get IDs");
        Set<String> toKeep = new HashSet<String>();
        String[] line;
        String kaggleID;
        double similarity;


        for(int i = 1; i <= 95; i++){
            try(CSVReader reader = new CSVReader(Files.newBufferedReader(Paths.get(String.format("%s%d.csv", Constants.getKaggleReducedXMLRootPath(),i)), StandardCharsets.ISO_8859_1))){
                //System.out.println("Reading file " + String.format("%s%d.csv", Constants.getKaggleReducedXMLRootPath(),i));
                while((line = reader.readNext())!=null){
                    //System.out.println("Reading file");
                    kaggleID = line[1];
                    similarity = Double.parseDouble(line[2]);

                    //System.out.println(kaggleID);
                    //System.out.println(Double.toString(similarity));

                    if(similarity >= thresh || Math.random() < percentage){
                        toKeep.add(kaggleID);
                    }

                }


            }catch(NoSuchFileException e){
                System.out.println(e.getMessage());
            }
        }
        System.out.println("Collected IDs, found " + Integer.toString(toKeep.size()));

        if(debug){
            try(BufferedWriter writer = Files.newBufferedWriter(Paths.get("debug.txt"),StandardCharsets.UTF_8)){
                for(String s : toKeep){
                    writer.write(s);
                    writer.write("\n");
                }
            }
        }

        return toKeep;
    }

    public static void writeIDsToKeepToFile(Set<String> toKeep) throws IOException{
        try(BufferedReader reader = Files.newBufferedReader(Paths.get(Constants.getDatasetPath("kaggle")),StandardCharsets.UTF_8)){
            try(BufferedWriter writer = Files.newBufferedWriter(Paths.get(Constants.getDatasetPath("_kaggle")),StandardCharsets.UTF_8)){
                writer.write(reader.readLine()); //first line
                writer.write("\n");
                writer.write(reader.readLine()); //root tag
                writer.write("\n");

                String line;
                String ppline; //preprocessed line
                String prevLine ="";
                String id;

                while((line = reader.readLine()) != null){
                    
                    if(line.contains("<ID>")){
                        ppline = line.trim().replace("<ID>", "");
                        id = ppline.substring(0, ppline.indexOf("<"));

                        if(toKeep.contains(id)){
                            writer.write(prevLine);
                            writer.write("\n");

                            do{
                                writer.write(line);
                                writer.write("\n");
                            }while(!(line = reader.readLine()).contains("</Company>"));

                            writer.write(line); //write closing tag
                            writer.write("\n");
                        }
                    }
                    prevLine = line;
                }

                //write closing loop tag
                writer.write(prevLine);

            }
        }catch(KeyException k){
            System.out.println(k.getMessage());
        }catch(NoSuchFileException e){
            System.out.println(e.getMessage());
        }

    }



    public static void main(String[] args) {
        // Integer lineLimit = 1000000;

        // splitXML(FILENAME, "data/input/test/", "kaggle", lineLimit);
        
        //removeNonMatchesFromInputFile("data/input/test/kaggle_1.xml", "data/output/reducedKaggleSet/1.csv", 1, "data/output/reducedKaggleSet/kaggle_1_red.xml");

        //reduceKaggleXMLs(1, Constants.getMaxKaggleID());

        //combineReducedKaggleXMLs(4);

        try{
            writeIDsToKeepToFile(keepPercentageOrAboveThresh(0.78, 0.15, true));
        }catch(Exception e){
            e.printStackTrace();
        }
        
        

    }

}