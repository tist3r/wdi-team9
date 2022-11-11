package de.uni_mannheim.informatik.dws.wdi.IR_Team9.Experiments;

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


    static void runAll(int experimentID, double thresh, int blockerID, int ruleID){

        try{
            Experiment e = new Experiment("dbpedia", "forbes", experimentID, thresh, blockerID, ruleID);
            e.runExperiment();
        }catch(Exception e){
            e.printStackTrace();
        }


        try{
            Experiment e = new Experiment("dbpedia", "dw", experimentID, thresh, blockerID, ruleID);
            e.runExperiment();
        }catch(Exception e){
            e.printStackTrace();
        }


        try{
            Experiment e = new Experiment("dw", "forbes", experimentID, thresh, blockerID, ruleID);
            e.runExperiment();
        }catch(Exception e){
            e.printStackTrace();
        }
        
    }   


    public static void main(String[] args) throws Exception {

        double thresh = 0.85;
        int experimentID = 1;

        for(int blockerID = 1; blockerID <= BLOCKERS.NUM_BLOCKERS; blockerID++){ //blockerID
            for(int ruleID = 1; ruleID <= MATCHING_RULES.NUM_MATCHING_RULES; ruleID++){
                runAll(experimentID, thresh, blockerID, ruleID);
                experimentID++;
            }
        }      

    }

    
}
