package de.uni_mannheim.informatik.dws.wdi.IR_Team9.Experiments;

import de.uni_mannheim.informatik.dws.winter.matching.rules.LinearCombinationMatchingRule;
import de.uni_mannheim.informatik.dws.winter.matching.blockers.StandardRecordBlocker;
import de.uni_mannheim.informatik.dws.wdi.IR_Team9.Blocking.BLOCKERS;
import de.uni_mannheim.informatik.dws.wdi.IR_Team9.Blocking.CompanyQgramBlocking;
import de.uni_mannheim.informatik.dws.wdi.IR_Team9.Comparators.CompanyNameComparatorJaccardNgram;
import de.uni_mannheim.informatik.dws.wdi.IR_Team9.Comparators.MATCHING_RULES;
import de.uni_mannheim.informatik.dws.wdi.IR_Team9.model.Company;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;

public class Experiment1 extends AbstractExperiment{

    public Experiment1(String ds1, String ds2, int experimentID, double thresh) throws Exception{
        super(ds1, ds2, experimentID, thresh);
        this.initializeExperiment();
    }


    void setupMatchingRule(){
        this.MATCHING_RULE_ID = 1;
        this.rule = MATCHING_RULES.getMR1(this.matchingThresh);
        
        super.setupMatchingRule();
    }


    void setupBlocker(){
        this.BLOCKER_ID = 1;
        this.blocker = BLOCKERS.getBlocker1();
        super.setupBlocker();
    }


    static void runAll(double thresh) throws Exception{
        Experiment1 e1;

        e1 = new Experiment1("dbpedia", "forbes", 1, thresh);
        e1.runExperiment();

        e1 = new Experiment1("dbpedia", "dw", 1, thresh);
        e1.runExperiment();

        e1 = new Experiment1("dw", "forbes", 1, thresh);
        e1.runExperiment();
    }


    public static void main(String[] args) throws Exception {
        
        Experiment1.runAll(0.85);

    }

    
}
