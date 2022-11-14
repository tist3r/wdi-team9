package de.uni_mannheim.informatik.dws.wdi.IR_Team9.Comparators;

import de.uni_mannheim.informatik.dws.wdi.IR_Team9.model.Company;
import de.uni_mannheim.informatik.dws.winter.matching.algorithms.RuleLearner;
import de.uni_mannheim.informatik.dws.winter.matching.rules.LinearCombinationMatchingRule;
import de.uni_mannheim.informatik.dws.winter.matching.rules.MatchingRule;
import de.uni_mannheim.informatik.dws.winter.matching.rules.WekaMatchingRule;
import de.uni_mannheim.informatik.dws.winter.model.HashedDataSet;
import de.uni_mannheim.informatik.dws.winter.model.MatchingGoldStandard;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;

public class MATCHING_RULES {
    public static int NUM_MATCHING_RULES = 8;

    public static String mr1Description;
    public static String mr2Description;
    public static String mr3Description;
    public static String mr4Description;
    public static String mr5Description;
    public static String mr6Description;
    public static String mr7Description;
    public static String mr8Description;
    public static String mr9Description;
    public static String mr10Description;


    /**
     * 
     * @param id id of the matching rule
     * @param thresh matching threshold for positive matches
     * @param jaccardWeight jaccard weight for matching rule 5
     * @param ds1 ds1 for MR7
     * @param ds2 ds2 for MR7
     * @param gsTrain gold standard for MR7
     * @return the matching rule with the id
     * @throws IndexOutOfBoundsException when the specified id does not exist
     */
    public static MatchingRule<Company, Attribute> getRuleByID(
        int id,
        double thresh,
        double jaccardWeight,
        HashedDataSet<Company, Attribute> ds1,
        HashedDataSet<Company, Attribute> ds2,
        MatchingGoldStandard gsTrain) 
        throws IndexOutOfBoundsException{
        
            switch (id){
            case 1: return getMR1(thresh);
            case 2: return getMR2(thresh);
            case 3: return getMR3(thresh);
            case 4: return getMR4(thresh);
            case 5: return getMR5(thresh, jaccardWeight);
            case 6: return getMR6(thresh);
            case 7: return getMR7(thresh, ds1, ds2, gsTrain);
            case 8: return getMR8(thresh, ds1, ds2, gsTrain);
            default: throw new IndexOutOfBoundsException(String.format("Blocker with id %d does not exist, max is %d", id, NUM_MATCHING_RULES));
        }
    }


    /**
     * @param thresh final matching threshold
     * @return
     */
    public static MatchingRule<Company, Attribute> getMR1(double thresh){
        mr1Description = "basic matching rule with 3-gram jaccard, without frequent tokens.";

        LinearCombinationMatchingRule<Company, Attribute> rule = new LinearCombinationMatchingRule<>(thresh);
        try{
            rule.addComparator(new CompanyNameComparatorJaccardNgram(3, true), 1);
        }catch(Exception e){
            e.printStackTrace();
            System.exit(0);
        }

        return rule;
    }

    public static MatchingRule<Company, Attribute> getMR2(double thresh){
        mr2Description = "basic matching rule with 3-gram jaccard.";

        LinearCombinationMatchingRule<Company, Attribute> rule = new LinearCombinationMatchingRule<>(thresh);
        try{
            rule.addComparator(new CompanyNameComparatorJaccardNgram(3, false), 1);
        }catch(Exception e){
            e.printStackTrace();
            System.exit(0);
        }

        return rule;
    }

    public static MatchingRule<Company, Attribute> getMR3(double thresh){
        mr3Description = "basic matching rule with levensthein similarity, frequent tokens removed.";

        LinearCombinationMatchingRule<Company, Attribute> rule = new LinearCombinationMatchingRule<>(thresh);
        try{
            rule.addComparator(new CompanyNameComparatorLevenshtein(true), 1);
        }catch(Exception e){
            e.printStackTrace();
            System.exit(0);
        }

        return rule;
    }

    public static MatchingRule<Company, Attribute> getMR4(double thresh){
        mr4Description = "basic matching rule with levensthein similarity, frequent tokens not removed.";

        LinearCombinationMatchingRule<Company, Attribute> rule = new LinearCombinationMatchingRule<>(thresh);
        try{
            rule.addComparator(new CompanyNameComparatorLevenshtein(true), 1);
        }catch(Exception e){
            e.printStackTrace();
            System.exit(0);
        }

        return rule;
    }

    public static MatchingRule<Company, Attribute> getMR5(double thresh, double jaccardWeight){
        mr5Description = "basic matching rule with levensthein similarity and jaccard 3-grams, frequent tokens removed.";
        double levWeight = 1-jaccardWeight;

        LinearCombinationMatchingRule<Company, Attribute> rule = new LinearCombinationMatchingRule<>(thresh);
        try{
            rule.addComparator(new CompanyNameComparatorLevenshtein(true), levWeight);
            rule.addComparator(new CompanyNameComparatorJaccardNgram(3, true), jaccardWeight);
        }catch(Exception e){
            e.printStackTrace();
            System.exit(0);
        }

        return rule;
    }

    public static MatchingRule<Company, Attribute> getMR6(double thresh){
        mr6Description = "basic matching rule with levensthein similarity and jaccard 3-grams, frequent tokens removed. also Jaccard token with frequent tokens present.";

        LinearCombinationMatchingRule<Company, Attribute> rule = new LinearCombinationMatchingRule<>(thresh);
        try{
            rule.addComparator(new CompanyNameComparatorLevenshtein(true), 1d/3);
            rule.addComparator(new CompanyNameComparatorJaccardNgram(3, true), 1d/3);
            rule.addComparator(new CompanyNameComparatorJaccardToken(), 1d/3);
        }catch(Exception e){
            e.printStackTrace();
            System.exit(0);
        }

        return rule;
    }

    public static MatchingRule<Company, Attribute> getMR7(double thresh, HashedDataSet<Company, Attribute> ds1, HashedDataSet<Company, Attribute> ds2, MatchingGoldStandard gsTrain){
        mr7Description = "Weka logistic regression matching rule with postprocessing parameters";

        String options[] = new String[] { "-S" };
		String modelType = "SimpleLogistic"; // use a logistic regression
        WekaMatchingRule<Company, Attribute> rule = new WekaMatchingRule<>(thresh, modelType, options);

        // add comparators
        rule.addComparator(new CompanyNameComparatorJaccardNgram(3, false, 0.55f));
        rule.addComparator(new CompanyNameComparatorJaccardNgram(3, true, 0.55f));
        rule.addComparator(new CompanyNameComparatorJaccardNgram(4, true, 0.55f));
        rule.addComparator(new CompanyNameComparatorJaccardNgram(4, false, 0.55f));
        rule.addComparator(new CompanyNameComparatorLevenshtein(true,0.55f));
        rule.addComparator(new CompanyNameComparatorLevenshtein(false,0.55f));
        rule.addComparator(new CompanyNameComparatorJaccardToken(0.4f, false));

        // train the matching rule's model
		RuleLearner<Company, Attribute> learner = new RuleLearner<>();
		learner.learnMatchingRule(ds1, ds2, null, rule, gsTrain);

		//logger.info(String.format("Matching rule is:\n%s", rule.getModelDescription()));

        return rule;
    }


    public static MatchingRule<Company, Attribute> getMR8(double thresh, HashedDataSet<Company, Attribute> ds1, HashedDataSet<Company, Attribute> ds2, MatchingGoldStandard gsTrain){
        mr8Description = "Weka tree matching rule with postprocessing parameters";

        // create a matching rule & provide classifier, options
        String tree = "J48"; // new instance of tree
        String options[] = new String[1];
        options[0] = "-U";
        WekaMatchingRule<Company, Attribute> rule = new WekaMatchingRule<>(thresh, tree, options);

        // add comparators
        rule.addComparator(new CompanyNameComparatorJaccardNgram(3, false, 0.55f));
        rule.addComparator(new CompanyNameComparatorJaccardNgram(3, true, 0.55f));
        rule.addComparator(new CompanyNameComparatorJaccardNgram(4, true, 0.55f));
        rule.addComparator(new CompanyNameComparatorJaccardNgram(4, false, 0.55f));
        rule.addComparator(new CompanyNameComparatorLevenshtein(true,0.55f));
        rule.addComparator(new CompanyNameComparatorLevenshtein(false,0.55f));
        rule.addComparator(new CompanyNameComparatorJaccardToken(0.4f, false));

        // train the matching rule's model
		RuleLearner<Company, Attribute> learner = new RuleLearner<>();
		learner.learnMatchingRule(ds1, ds2, null, rule, gsTrain);

		//logger.info(String.format("Matching rule is:\n%s", rule.getModelDescription()));

        return rule;
    }





    
}
