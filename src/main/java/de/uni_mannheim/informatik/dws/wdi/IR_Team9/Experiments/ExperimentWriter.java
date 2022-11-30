package de.uni_mannheim.informatik.dws.wdi.IR_Team9.Experiments;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Scanner;

import au.com.bytecode.opencsv.CSVWriter;
import de.uni_mannheim.informatik.dws.wdi.IR_Team9.utils.Constants;
import de.uni_mannheim.informatik.dws.winter.model.Performance;

public class ExperimentWriter {

    /**
     * Appends to log file that can be specified
     * @param experiment
     * @param filename
     * @throws IOException
     */
    public static void appendToExperimentCSV(AbstractExperiment experiment, String filename) throws IOException {
        try(CSVWriter writer = new CSVWriter(new FileWriter(filename, true))){
            String[] values = format(experiment);
            writer.writeNext(values);
        }       
    }

    /**
     * Appends to regular experiment log file
     * @param experiment
     * @throws IOException
     */
    public static void appendToExperimentCSV(AbstractExperiment experiment) throws IOException {
        appendToExperimentCSV(experiment, Constants.getExperimentLogPath()); 
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
         * datetime conducted
         */

         return new String[]{
            experiment.toString()!=null?experiment.toString():"n/a",
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
            Integer.toString(experiment.noCorrespondences) != null? Integer.toString(experiment.noCorrespondences):"n/a",
            experiment.getDurationString() != null ? experiment.getDurationString() : "n/a",
            Double.toString(experiment.reductionRatio) != null? Double.toString(experiment.reductionRatio):"n/a",
            Integer.toString(experiment.blockedPairs)!= null ? Integer.toString(experiment.blockedPairs) : "n/a",
            timeStrOrNa(experiment.timeStarted),
            Integer.toString(experiment.noCorrespondencesTop1) != null ? Integer.toString(experiment.noCorrespondencesTop1) : "n/a",

            PerfStrOrNa(experiment.perfTestTop1, "P"),
            PerfStrOrNa(experiment.perfTestTop1, "R"),
            PerfStrOrNa(experiment.perfTestTop1, "F1")
        };
    }

    private static  String timeStrOrNa(LocalDateTime t){
        try{return t.toString();}catch(Exception e){return "n/a";}
    }

    private static String PerfStrOrNa(Performance p, String score){
        try{
            switch(score){
                case("P"): return Double.toString(p.getPrecision());
                case("R"): return Double.toString(p.getRecall());
                case("F1"): return Double.toString(p.getF1());
                default: return "n/a";
            }
        }catch(Exception e){
            return "n/a";
        }
    }

    public static void initializeFile(String filename) throws IOException{
        if(Files.exists(Paths.get(filename))){
            System.out.println("[WARNING ] Experiment file already exists. Overwrite? y/n ...");

            Scanner in = new Scanner(System.in);
            if(in.nextLine().matches("y")){
                System.out.println("[INFO ] Overwriting Experiment Log File...");

                try(CSVWriter writer = new CSVWriter(new FileWriter(filename))){
                    writer.writeNext(getHeaders());
                }
            }else{
                System.out.println("[INFO ] Aborting Overwrite...");
            }

            in.close();
        }else{
            System.out.println("[INFO ] Initializing Experiment Log File...");

            try(CSVWriter writer = new CSVWriter(new FileWriter(filename))){
                writer.writeNext(getHeaders());
            }
        }     
    }

    public static void initializeFile() throws IOException{
        initializeFile(Constants.getExperimentLogPath());
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
            "#blocked pairs",
            "dateTime",
            "#corr top1",
            "Precision top1",
            "Recall top1",
            "F1 top1"
        };
    }

    public static void main(String[] args) throws Exception{
        initializeFile("data/output/experiments/reevaluated_log.csv");
    }
}
