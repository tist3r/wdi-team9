package de.uni_mannheim.informatik.dws.wdi.IR_Team9.Experiments;


import de.uni_mannheim.informatik.dws.wdi.IR_Team9.Blocking.BLOCKERS;
import de.uni_mannheim.informatik.dws.wdi.IR_Team9.Comparators.MATCHING_RULES;

public class Experiment3 extends AbstractExperiment{

    public Experiment3(String ds1, String ds2, int experimentID, double thresh) throws Exception{
        super(ds1, ds2, experimentID, thresh);
        this.initializeExperiment();
    }


    void setupMatchingRule(){
        this.MATCHING_RULE_ID = 1;
        this.rule = MATCHING_RULES.getMR1(this.matchingThresh);
        
        super.setupMatchingRule();
    }


    void setupBlocker(){
        this.BLOCKER_ID = 3;
        this.blocker = BLOCKERS.getBlocker3();
        super.setupBlocker();
    }


    static void runAll(double thresh) throws Exception{
        Experiment3 e1;

        e1 = new Experiment3("dbpedia", "forbes", 1, thresh);
        e1.runExperiment();

        e1 = new Experiment3("dbpedia", "dw", 1, thresh);
        e1.runExperiment();

        e1 = new Experiment3("dw", "forbes", 1, thresh);
        e1.runExperiment();
    }


    public static void main(String[] args) throws Exception {
        
        Experiment3.runAll(0.85);

    }

    
}
