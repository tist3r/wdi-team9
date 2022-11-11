package de.uni_mannheim.informatik.dws.wdi.IR_Team9.Experiments;

import de.uni_mannheim.informatik.dws.winter.matching.rules.LinearCombinationMatchingRule;
import de.uni_mannheim.informatik.dws.winter.matching.blockers.StandardRecordBlocker;
import de.uni_mannheim.informatik.dws.wdi.IR_Team9.Blocking.CompanyQgramBlocking;
import de.uni_mannheim.informatik.dws.wdi.IR_Team9.Comparators.CompanyNameComparatorJaccardNgram;
import de.uni_mannheim.informatik.dws.wdi.IR_Team9.model.Company;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;

public class Experiment1 extends AbstractExperiment{

    public Experiment1(String ds1, String ds2, int experimentID) throws Exception{
        super(ds1, ds2, experimentID);
        this.initializeExperiment();
    }


    void setupMatchingRule(){
        this.MATCHING_RULE_ID = 1;

        LinearCombinationMatchingRule<Company, Attribute> r = new LinearCombinationMatchingRule<>(0.85);
        try{
            r.addComparator(new CompanyNameComparatorJaccardNgram(3, true), 1);
        }catch(Exception e){
            e.printStackTrace();
            System.exit(0);
        }
        
        this.rule = r;
        
        super.setupMatchingRule();
    }


    void setupBlocker(){
        this.BLOCKER_ID = 1;
        this.blocker = new StandardRecordBlocker<Company, Attribute>(new CompanyQgramBlocking(3));
        super.setupBlocker();
    }

    public static void main(String[] args) throws Exception {
        
        Experiment1 dbpedia_forbes = new Experiment1("dbpedia", "forbes", 1);

        dbpedia_forbes.runExperiment();

    }

    
}
