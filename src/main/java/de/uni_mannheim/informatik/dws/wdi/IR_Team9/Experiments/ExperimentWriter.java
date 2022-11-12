package de.uni_mannheim.informatik.dws.wdi.IR_Team9.Experiments;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import de.uni_mannheim.informatik.dws.wdi.IR_Team9.utils.Constants;

public class ExperimentWriter {

    public static void appendToExperimentCSV(AbstractExperiment experiment) throws IOException {
        try(CSVWriter writer = new CSVWriter(new FileWriter(Constants.getExperimentLogPath(), true))){
            String[] values = format(experiment);
            writer.writeNext(values);
        }       
    }


    /**
     * Reads experiment log IDs to a set.
     * @return the set of IDs
     */
    public static Set<String> getConductedExperiments(){

        Set<String> exp = new HashSet<String>();

        try(CSVReader reader = new CSVReader(new FileReader(Constants.getExperimentLogPath()))){
            List<String[]> lines = reader.readAll();

            

            for(String[] line : lines){
                exp.add(line[0]);
            }


        }catch(IOException e){
            e.printStackTrace();

        }

        return exp;
    }

    public static String[] format(AbstractExperiment experiment){
        /*
         * Experiment Number (Experiment is a matching rule blocker combination)
         * dataset 1
         * dataset 2
         * Matching Rule id
         * rule thresh
         * Blocker with params & KeyGenerator
         * precision (train)
         * recall (train)
         * f1 (train)
         * precision (test)
         * recall (test)
         * f1 (test)
         * #corr
         * Time
         * reduction ration
         * blocked pairs
         */

         return new String[]{
            experiment.toString(),
            experiment.ds1Name,
            experiment.ds2Name,
            Integer.toString(experiment.getMatchingRuleID()), //params in Matching rule file
            Double.toString(experiment.matchingThresh),
            Integer.toString(experiment.getBlockerID()), //params in Blocker file
            Double.toString(experiment.perfTrain.getPrecision()),
            Double.toString(experiment.perfTrain.getRecall()),
            Double.toString(experiment.perfTrain.getF1()),
            Double.toString(experiment.perfTest.getPrecision()),
            Double.toString(experiment.perfTest.getRecall()),
            Double.toString(experiment.perfTest.getF1()),
            Integer.toString(experiment.noCorrespondences),
            experiment.getDurationString(),
            Double.toString(experiment.reductionRatio),
            Integer.toString(experiment.blockedPairs)
        };
    }

    public static void initializeFile() throws IOException{
        if(Files.exists(Paths.get(Constants.getExperimentLogPath()))){
            System.out.println("[WARNING ] Experiment file already exists. Overwrite? y/n ...");

            Scanner in = new Scanner(System.in);
            if(in.nextLine().matches("y")){
                System.out.println("[INFO ] Overwriting Experiment Log File...");

                try(CSVWriter writer = new CSVWriter(new FileWriter(Constants.getExperimentLogPath()))){
                    writer.writeNext(getHeaders());
                }
            }else{
                System.out.println("[INFO ] Aborting Overwrite...");
            }

            in.close();
        }else{
            System.out.println("[INFO ] Initializing Experiment Log File...");

            try(CSVWriter writer = new CSVWriter(new FileWriter(Constants.getExperimentLogPath()))){
                writer.writeNext(getHeaders());
            }
        }     
    }
    
    static String[] getHeaders(){
        return new String[]{
            "Experiment ID",
            "Dataset 1",
            "Dataset 2",
            "Matching Rule ID",
            "Matching Rule Threshold",
            "Blocker ID",
            "Precision (Train)",
            "Recall (Train)",
            "F1 (Train)",
            "Precision (Test)",
            "Recall (Test)",
            "F1 (Test)",
            "#corr",
            "Duration",
            "Reduction ratio",
            "#blocked pairs"
        };
    }

    public static void main(String[] args) throws Exception{
        initializeFile();
    }
}
