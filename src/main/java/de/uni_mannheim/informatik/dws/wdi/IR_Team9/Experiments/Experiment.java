package de.uni_mannheim.informatik.dws.wdi.IR_Team9.Experiments;

import java.io.BufferedWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import de.uni_mannheim.informatik.dws.wdi.IR_Team9.Blocking.BLOCKERS;
import de.uni_mannheim.informatik.dws.wdi.IR_Team9.Comparators.MATCHING_RULES;
import de.uni_mannheim.informatik.dws.wdi.IR_Team9.model.Company;
import de.uni_mannheim.informatik.dws.wdi.IR_Team9.utils.Constants;
import de.uni_mannheim.informatik.dws.winter.matching.blockers.Blocker;
import de.uni_mannheim.informatik.dws.winter.matching.rules.MatchingRule;
import de.uni_mannheim.informatik.dws.winter.matching.rules.WekaMatchingRule;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;

public class Experiment extends AbstractExperiment{

    public Experiment(String ds1Name, String ds2Name, int experimentID, double thresh, int blockerID, int ruleID) throws Exception{
        super(ds1Name, ds2Name, experimentID, thresh);

        this.loadData(ds1Name, ds2Name);
        this.loadTrainTestData();

        this.setMatchingRule(MATCHING_RULES.getRuleByID(ruleID, thresh, 0.5, this.ds1, this.ds2, this.gsTrain), ruleID);
        this.setBlocker(BLOCKERS.getBlockerByID(blockerID), blockerID);
    }

    public Experiment(String ds1Name, String ds2Name, int experimentID, double thresh, double[] threshsForLMR, int blockerID, int ruleID) throws Exception{
        this(ds1Name, ds2Name, experimentID, thresh, blockerID, ruleID);
        this.threshsToEvaluateForLinearMatchingRules = threshsForLMR;
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



    public static void runForDatasetCombination(
        String ds1Name,
        String ds2Name, 
        int experimentID,
        double thresh,
        int blockerID,
        int ruleID,
        boolean redo,
        Set<String> conductedExp){
            runForDatasetCombination(ds1Name, ds2Name, experimentID, thresh, new double[]{}, blockerID, ruleID, redo, conductedExp);
        }


    /**
     * runs the specified experiment for a dataset combination
     * @param ds1Name
     * @param ds2Name
     * @param experimentID
     * @param thresh
     * @param threshs
     * @param blockerID
     * @param ruleID
     * @param redo
     * @param conductedExp
     */
    public static void runForDatasetCombination(
        String ds1Name,
        String ds2Name, 
        int experimentID,
        double thresh,
        double[] threshs,
        int blockerID,
        int ruleID,
        boolean redo,
        Set<String> conductedExp){

            logger.info("Conduction experiment " + getID(ds1Name, ds2Name, ruleID, blockerID, thresh));

            try{
                if(redo || !hasAlreadyRun(ds1Name, ds2Name, ruleID, blockerID, thresh, conductedExp)){
                    Experiment e = new Experiment(ds1Name, ds2Name, experimentID, thresh, threshs, blockerID, ruleID);
                    e.runExperiment();
                }else{
                    logger.info("Skipping, already conducted ...");
                }
            }catch(OutOfMemoryError oom){
                logger.warn("OOM exception thrown ..."+oom.getMessage());

                try(BufferedWriter bufferedWriter = Files.newBufferedWriter(Paths.get(Constants.getKaggleErrorLogPath()),StandardOpenOption.APPEND)){
                    bufferedWriter.write(getID(ds1Name, ds2Name, ruleID, blockerID, thresh)+LocalDateTime.now());
                    bufferedWriter.write("\n");
                }catch(Exception e){
                    logger.warn("unexpected exception when writing OOM error log");
                    e.printStackTrace();
                }
            }
            catch(Exception e){
                logger.warn("unexpected exception " + e.getMessage());
            }

    }

    /**
     * Runs the specified experiment for all datasets.
     * @param experimentID
     * @param thresh
     * @param blockerID
     * @param ruleID
     * @param redo
     * @param conductedExp
     */
    public static void runForAllDatasets(int experimentID, double thresh, int blockerID, int ruleID, boolean redo, Set<String> conductedExp){
        String dbpedia = "dbpedia";
        String forbes = "forbes";
        String dw = "dw";

       //dbpedia - forbes
        runForDatasetCombination(dbpedia, forbes, experimentID, thresh, blockerID, ruleID, redo, conductedExp);

        //dbpedia - dw
        runForDatasetCombination(dbpedia, dw, experimentID, thresh, blockerID, ruleID, redo, conductedExp);

        //dw - forbes
        runForDatasetCombination(dw, forbes, experimentID, thresh, blockerID, ruleID, redo, conductedExp);

        //dbpedia - kaggle
        runForDatasetCombination(dbpedia, "kaggle", experimentID, thresh, blockerID, ruleID, redo, conductedExp);

        //kaggle
        // for(int i = 1; i <=4; i++){
        //     runForDatasetCombination(dbpedia, Constants.getAggregateKagglePartitionedDSNamesByID(i), experimentID, thresh, blockerID, ruleID, redo, conductedExp);
        //     runForDatasetCombination(dw, Constants.getAggregateKagglePartitionedDSNamesByID(i), experimentID, thresh, blockerID, ruleID, redo, conductedExp);
        //     runForDatasetCombination(forbes, Constants.getAggregateKagglePartitionedDSNamesByID(i), experimentID, thresh, blockerID, ruleID, redo, conductedExp);
        // }
    }
    
    
    static void runMultThreshExperimentForWekas(List<Double> t_){
        int experimentID = 1;

        int blockerID = 10;

        for(int ruleID = 10; ruleID <= 11; ruleID++){
            int rID = ruleID;

            if(MATCHING_RULES.WEKA_RULE_IDS.contains(rID)){
                t_.forEach(thresh -> Experiment.runForDatasetCombination("dbpedia", "kaggle_f", experimentID, thresh, blockerID, rID, true, getConductedExperiments()));
            }
        }
    }


    public static void main(String[] args) throws Exception {

       

        double[] threshs = new double[]{0.7, 0.8, 0.875, 0.9};
        List<Double> t_ = new ArrayList<>(Arrays.asList(0.8, 0.875, 0.9));
        int experimentID = 1;

        runMultThreshExperimentForWekas(t_);

        // int blockerID = 10;

        // for(int ruleID = 9; ruleID <= 17; ruleID++){
        //     int rID = ruleID;

        //     Experiment.runForDatasetCombination("dw", "kaggle_f", experimentID, 0.85, threshs, blockerID, rID, true, getConductedExperiments());

        //     ruleID = ruleID == 6 ? 18 : ruleID;
        // }

        
        // for(int blockerID = 1; blockerID <= BLOCKERS.NUM_BLOCKERS; blockerID++){ //blockerID
        //     for(int ruleID = 8; ruleID <= MATCHING_RULES.NUM_MATCHING_RULES; ruleID++){
        //         runForAllDatasets(experimentID, thresh, blockerID, ruleID, false, getConductedExperiments());
        //         experimentID++;
        //     }
        // }   
    }
}
