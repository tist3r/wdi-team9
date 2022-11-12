package de.uni_mannheim.informatik.dws.wdi.IR_Team9.Experiments;

import java.io.BufferedWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Set;

import de.uni_mannheim.informatik.dws.wdi.IR_Team9.Blocking.BLOCKERS;
import de.uni_mannheim.informatik.dws.wdi.IR_Team9.Comparators.MATCHING_RULES;
import de.uni_mannheim.informatik.dws.wdi.IR_Team9.model.Company;
import de.uni_mannheim.informatik.dws.winter.matching.blockers.Blocker;
import de.uni_mannheim.informatik.dws.winter.matching.rules.MatchingRule;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;

public class Experiment extends AbstractExperiment{

    public Experiment(String ds1, String ds2, int experimentID, double thresh, int blockerID, int ruleID) throws Exception{
        super(ds1, ds2, experimentID, thresh);

        this.loadData(ds1, ds2);
        this.loadTrainTestData();

        this.setMatchingRule(MATCHING_RULES.getRuleByID(ruleID, thresh, 0.5, this.ds1, this.ds2, this.gsTrain), ruleID);
        this.setBlocker(BLOCKERS.getBlockerByID(blockerID), blockerID);
    }


    @Override
    void setBlocker(Blocker<Company, Attribute, Company, Attribute> blocker, Integer blockerID) {
        this.BLOCKER_ID = blockerID;
        this.blocker = blocker;
        this.setupBlocker();
    }


    @Override
    void setMatchingRule(MatchingRule<Company, Attribute> rule, Integer ruleID) {
       this.MATCHING_RULE_ID = ruleID;
       this.rule = rule;
       this.setupMatchingRule();
    }


    static boolean hasAlreadyRun(String ds1Name, String ds2Name, int ruleID, int blockerID, double thresh, Set<String> conductedExp){
        String id = getID(ds1Name, ds2Name, ruleID, blockerID, thresh);

        return conductedExp.contains(id);
    }


    static void runAll(int experimentID, double thresh, int blockerID, int ruleID, boolean redo, Set<String> conductedExp){
        String dbpedia = "dbpedia";
        String forbes = "forbes";
        String dw = "dw";

        try{
            if(redo || !hasAlreadyRun(dbpedia, forbes, ruleID, blockerID, thresh, conductedExp)){
                Experiment e = new Experiment("dbpedia", "forbes", experimentID, thresh, blockerID, ruleID);
                e.runExperiment();
            }else{
                logger.info("Skipping, already conducted ...");
            }
            
        }catch(Exception e){
            e.printStackTrace();
        }


        try{
            if(redo || !hasAlreadyRun(dbpedia, dw, ruleID, blockerID, thresh, conductedExp)){
                Experiment e = new Experiment("dbpedia", "dw", experimentID, thresh, blockerID, ruleID);
                e.runExperiment();
            }else{
                logger.info("Skipping, already conducted ...");
            }
            
        }catch(Exception e){
            e.printStackTrace();
        }


        try{
            if(redo || !hasAlreadyRun(dw, forbes, ruleID, blockerID, thresh, conductedExp)){
                Experiment e = new Experiment("dw", "forbes", experimentID, thresh, blockerID, ruleID);
                e.runExperiment();
            }else{
                logger.info("Skipping, already conducted ...");
            }
            
        }catch(Exception e){
            e.printStackTrace();
        }
        
    }   


    public static void main(String[] args) throws Exception {

        // double thresh = 0.85;
        // int experimentID = 1;

        // for(int blockerID = 1; blockerID <= BLOCKERS.NUM_BLOCKERS; blockerID++){ //blockerID
        //     for(int ruleID = 1; ruleID <= MATCHING_RULES.NUM_MATCHING_RULES; ruleID++){
        //         runAll(experimentID, thresh, blockerID, ruleID, false, ExperimentWriter.getConductedExperiments());
        //         experimentID++;
        //     }
        // }   
        
        int[] rules = new int[]{1,2,5};
        String[] kaggleSets = new String[]{"kaggle_a_1","kaggle_a_2","kaggle_a_3","kaggle_a_4"};
        Experiment e;

        for(int rule = 0; rule < rules.length; rule++){
            for(int set = 0; set < kaggleSets.length; set++){
                try{
                    e = new Experiment("dbpedia", kaggleSets[set], 100, 0.2, 9, rules[rule]);
                    e.runExperiment();
                }catch(OutOfMemoryError oom){
                    oom.printStackTrace();

                    try(BufferedWriter bufferedWriter = Files.newBufferedWriter(Paths.get("data/logs/kaggleAggLog.txt"),StandardOpenOption.APPEND)){
                        bufferedWriter.write(kaggleSets[set] + Integer.toString(rules[rule]));
                    }
                }
            }
        }

        

    }

    
}
