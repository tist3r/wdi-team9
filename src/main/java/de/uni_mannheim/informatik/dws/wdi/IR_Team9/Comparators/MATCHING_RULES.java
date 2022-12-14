package de.uni_mannheim.informatik.dws.wdi.IR_Team9.Comparators;

import java.util.Arrays;
import java.util.List;

import de.uni_mannheim.informatik.dws.wdi.IR_Team9.model.Company;
import de.uni_mannheim.informatik.dws.winter.matching.algorithms.RuleLearner;
import de.uni_mannheim.informatik.dws.winter.matching.rules.LinearCombinationMatchingRule;
import de.uni_mannheim.informatik.dws.winter.matching.rules.MatchingRule;
import de.uni_mannheim.informatik.dws.winter.matching.rules.WekaMatchingRule;
import de.uni_mannheim.informatik.dws.winter.model.HashedDataSet;
import de.uni_mannheim.informatik.dws.winter.model.MatchingGoldStandard;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;

public class MATCHING_RULES {
    public static int NUM_MATCHING_RULES = 29;

    public static final List<Integer> WEKA_RULE_IDS = Arrays.asList(7,8,10,11,12,13,16,20,22,24,25,26,31);

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
                case 9: return getMR9(thresh);
                case 10: return getMR10(thresh, ds1, ds2, gsTrain);
                case 11: return getMR11(thresh, ds1, ds2, gsTrain);
                case 12: return getMR12(thresh, ds1, ds2, gsTrain);
                case 13: return getMR13(thresh, ds1, ds2, gsTrain);
                case 14: return getMR14(thresh);
                case 15: return getMR15(thresh);
                case 16: return getMR16(thresh, ds1, ds2, gsTrain);
                case 17: return getMR17(thresh);
                case 18: return getMR18(thresh);
                case 19: return getMR19(thresh);
                case 20: return getMR20(thresh, ds1, ds2, gsTrain);
                case 21: return getMR21(thresh);
                case 22: return getMR22(thresh, ds1, ds2, gsTrain);
                case 23: return getMR23(thresh);
                case 24: return getMR24(thresh, ds1, ds2, gsTrain);
                case 25: return getMR25(thresh, ds1, ds2, gsTrain);
                case 26: return getMR26(thresh, ds1, ds2, gsTrain);
                case 27: return getMR27(thresh);
                case 28: return getMR28(thresh);
                case 29: return getMR29(thresh);
                case 30: return getMR30(thresh);
                case 31: return getMR31(thresh, ds1, ds2, gsTrain);

                default: throw new IndexOutOfBoundsException(String.format("Matching rule with id %d does not exist, max is %d", id, NUM_MATCHING_RULES));
        }
    }


    /**
     * @param thresh final matching threshold
     * @return
     */
    public static MatchingRule<Company, Attribute> getMR1(double thresh){
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


    public static MatchingRule<Company, Attribute> getMR9(double thresh){
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

    public static MatchingRule<Company, Attribute> getMR10(double thresh, HashedDataSet<Company, Attribute> ds1, HashedDataSet<Company, Attribute> ds2, MatchingGoldStandard gsTrain){
        String options[] = new String[] { "-S" };
		String modelType = "SimpleLogistic"; // use a logistic regression
        WekaMatchingRule<Company, Attribute> rule = new WekaMatchingRule<>(thresh, modelType, options);

        // add comparators
        rule.addComparator(new CompanyNameComparatorJaccardNgram(3, true, 0.55f));
        rule.addComparator(new CompanyNameComparatorLevenshtein(true,0.55f));
        rule.addComparator(new CompanyNameComparatorJaccardToken(0.4f, false));

        // train the matching rule's model
		RuleLearner<Company, Attribute> learner = new RuleLearner<>();
		learner.learnMatchingRule(ds1, ds2, null, rule, gsTrain);

		//logger.info(String.format("Matching rule is:\n%s", rule.getModelDescription()));

        return rule;
    }

    public static MatchingRule<Company, Attribute> getMR11(double thresh, HashedDataSet<Company, Attribute> ds1, HashedDataSet<Company, Attribute> ds2, MatchingGoldStandard gsTrain){
        //mr11Description = "Weka logistic regression matching rule with postprocessing parameters and fewer comparators";

        String options[] = new String[] { "-S" };
		String modelType = "SimpleLogistic"; // use a logistic regression
        WekaMatchingRule<Company, Attribute> rule = new WekaMatchingRule<>(thresh, modelType, options);

        // add comparators
        rule.addComparator(new CompanyNameComparatorJaccardNgram(3, true, 0.4f));
        rule.addComparator(new CompanyNameComparatorLevenshtein(true,0.4f));
        rule.addComparator(new CompanyNameComparatorJaccardToken(0.4f, false));

        // train the matching rule's model
		RuleLearner<Company, Attribute> learner = new RuleLearner<>();
		learner.learnMatchingRule(ds1, ds2, null, rule, gsTrain);

		//logger.info(String.format("Matching rule is:\n%s", rule.getModelDescription()));

        return rule;
    }

    public static MatchingRule<Company, Attribute> getMR12(double thresh, HashedDataSet<Company, Attribute> ds1, HashedDataSet<Company, Attribute> ds2, MatchingGoldStandard gsTrain){
        //mr11Description = "Weka logistic regression matching rule with postprocessing parameters and fewer comparators";

        String options[] = new String[] { "-S" };
		String modelType = "SimpleLogistic"; // use a logistic regression
        WekaMatchingRule<Company, Attribute> rule = new WekaMatchingRule<>(thresh, modelType, options);

        // add comparators
        rule.addComparator(new CompanyNameComparatorJaccardNgram(3, true, 0.62f));
        rule.addComparator(new CompanyNameComparatorLevenshtein(true,0.62f));
        rule.addComparator(new CompanyNameComparatorJaccardToken(0.5f, false));

        // train the matching rule's model
		RuleLearner<Company, Attribute> learner = new RuleLearner<>();
		learner.learnMatchingRule(ds1, ds2, null, rule, gsTrain);

		//logger.info(String.format("Matching rule is:\n%s", rule.getModelDescription()));

        return rule;
    }

    public static MatchingRule<Company, Attribute> getMR13(double thresh, HashedDataSet<Company, Attribute> ds1, HashedDataSet<Company, Attribute> ds2, MatchingGoldStandard gsTrain){
        //mr8Description = "Weka tree matching rule with postprocessing parameters";

        // create a matching rule & provide classifier, options
        String tree = "J48"; // new instance of tree
        String options[] = new String[1];
        options[0] = "-U";
        WekaMatchingRule<Company, Attribute> rule = new WekaMatchingRule<>(thresh, tree, options);

        // add comparators
        rule.addComparator(new CompanyNameComparatorJaccardNgram(3, true, 0.55f));
        rule.addComparator(new CompanyNameComparatorLevenshtein(true,0.55f));
        rule.addComparator(new CompanyNameComparatorJaccardToken(0.4f, false));

        // train the matching rule's model
		RuleLearner<Company, Attribute> learner = new RuleLearner<>();
		learner.learnMatchingRule(ds1, ds2, null, rule, gsTrain);

		//logger.info(String.format("Matching rule is:\n%s", rule.getModelDescription()));

        return rule;
    }


    public static MatchingRule<Company, Attribute> getMR14(double thresh){
        //mr9Description = "Linear Comb Matching rule removing frequent tokens for not token based, and leaving them for token based jaccard";

        LinearCombinationMatchingRule<Company, Attribute> rule = new LinearCombinationMatchingRule<>(thresh);
        try{
            rule.addComparator(new CompanyNameComparatorLevenshtein(true), 0.75/2);
            rule.addComparator(new CompanyNameComparatorJaccardNgram(4, true), 0.75/2);
            rule.addComparator(new CompanyNameComparatorJaccardToken(), 0.25);
        }catch(Exception e){
            e.printStackTrace();
            System.exit(0);
        }

        return rule;
    }

    public static MatchingRule<Company, Attribute> getMR15(double thresh){
        //mr9Description = "Linear Comb Matching rule removing frequent tokens for not token based, and leaving them for token based jaccard";

        LinearCombinationMatchingRule<Company, Attribute> rule = new LinearCombinationMatchingRule<>(thresh);
        try{
            rule.addComparator(new CompanyNameComparatorLevenshtein(true), 0.25);
            rule.addComparator(new CompanyNameComparatorJaccardNgram(3, true), 0.25);
            rule.addComparator(new CompanyNameComparatorJaccardToken(), 0.5);
        }catch(Exception e){
            e.printStackTrace();
            System.exit(0);
        }

        return rule;
    }


    public static MatchingRule<Company, Attribute> getMR16(double thresh, HashedDataSet<Company, Attribute> ds1, HashedDataSet<Company, Attribute> ds2, MatchingGoldStandard gsTrain){
        //mr11Description = "Weka logistic regression matching rule with postprocessing parameters and fewer comparators";

        String options[] = new String[] { "-S" };
		String modelType = "SimpleLogistic"; // use a logistic regression
        WekaMatchingRule<Company, Attribute> rule = new WekaMatchingRule<>(thresh, modelType, options);

        // add comparators
        rule.addComparator(new CompanyNameComparatorJaccardNgram(3, true, 0.62f));
        rule.addComparator(new CompanyNameComparatorLevenshtein(true,0.62f));
        rule.addComparator(new CompanyNameComparatorJaccardNgram(3, false, 0.7f));
        rule.addComparator(new CompanyNameComparatorLevenshtein(false,0.7f));
        rule.addComparator(new CompanyNameComparatorJaccardToken(0.5f, false));

        // train the matching rule's model
		RuleLearner<Company, Attribute> learner = new RuleLearner<>();
		learner.learnMatchingRule(ds1, ds2, null, rule, gsTrain);

		//logger.info(String.format("Matching rule is:\n%s", rule.getModelDescription()));

        return rule;
    }


    public static MatchingRule<Company, Attribute> getMR17(double thresh){
        //mr9Description = "Linear Comb Matching rule removing frequent tokens for not token based, and leaving them for token based jaccard";

        LinearCombinationMatchingRule<Company, Attribute> rule = new LinearCombinationMatchingRule<>(thresh);
        try{
            rule.addComparator(new CompanyNameComparatorLevenshtein(true), 0.3);
            rule.addComparator(new CompanyNameComparatorJaccardNgram(3, true), 0.3);
            rule.addComparator(new RogueTokenComparator(0.3f, true, 0.6f, AbstractT9Comparator.BOOST_FUNCTIONS.X3,2), 0.4);
        }catch(Exception e){
            e.printStackTrace();
            System.exit(0);
        }

        return rule;
    }

    public static MatchingRule<Company, Attribute> getMR18(double thresh){
        //mr9Description = "Linear Comb Matching rule removing frequent tokens for not token based, and leaving them for token based jaccard";

        LinearCombinationMatchingRule<Company, Attribute> rule = new LinearCombinationMatchingRule<>(thresh);
        try{
            rule.addComparator(new CompanyNameComparatorLevenshtein(true), 0.3);
            rule.addComparator(new CompanyNameComparatorJaccardNgram(3, true), 0.3);
            rule.addComparator(new RogueTokenComparator(), 0.4);
        }catch(Exception e){
            e.printStackTrace();
            System.exit(0);
        }

        return rule;
    }


    public static MatchingRule<Company, Attribute> getMR19(double thresh){
        //mr9Description = "Linear Comb Matching rule removing frequent tokens for not token based, and leaving them for token based jaccard";

        LinearCombinationMatchingRule<Company, Attribute> rule = new LinearCombinationMatchingRule<>(thresh);
        try{
            rule.addComparator(new CompanyNameComparatorJaccardNgram(4, true, 0.4f), 0.5); //takes care of order
            rule.addComparator(new RogueTokenComparator(0.3f, true, 0.7f, AbstractT9Comparator.BOOST_FUNCTIONS.X3, 2), 0.5); //takes care of missing tokens but stresses present ones
        }catch(Exception e){
            e.printStackTrace();
            System.exit(0);
        }

        return rule;
    }


    public static MatchingRule<Company, Attribute> getMR20(double thresh, HashedDataSet<Company, Attribute> ds1, HashedDataSet<Company, Attribute> ds2, MatchingGoldStandard gsTrain){
        //mr9Description = "Linear Comb Matching rule removing frequent tokens for not token based, and leaving them for token based jaccard";

        //mr11Description = "Weka logistic regression matching rule with postprocessing parameters and fewer comparators";

        String options[] = new String[] { "-S" };
		String modelType = "SimpleLogistic"; // use a logistic regression
        WekaMatchingRule<Company, Attribute> rule = new WekaMatchingRule<>(thresh, modelType, options);

        // add comparators
        rule.addComparator(new CompanyNameComparatorLevenshtein(true, 0.5f));
        rule.addComparator(new CompanyNameComparatorJaccardNgram(3, true, 0.5f));
        rule.addComparator(new CompanyNameComparatorJaccardNgram(4, true, 0.5f));
        rule.addComparator(new RogueTokenComparator(0.3f, true, 0.7f, AbstractT9Comparator.BOOST_FUNCTIONS.X3,2));

        // train the matching rule's model
		RuleLearner<Company, Attribute> learner = new RuleLearner<>();
		learner.learnMatchingRule(ds1, ds2, null, rule, gsTrain);

		//logger.info(String.format("Matching rule is:\n%s", rule.getModelDescription()));

        return rule;
    }

    public static MatchingRule<Company, Attribute> getMR21(double thresh){
        LinearCombinationMatchingRule<Company, Attribute> rule = new LinearCombinationMatchingRule<>(thresh);
        try{
            rule.addComparator(new CompanyNameComparatorJaccardNgram(3, true, 0.5f),0.3); //three a bit more robust to typos than 4
            rule.addComparator(new CompanyNameComparatorLevenshtein(true, 0.5f),0.3); //typos
            rule.addComparator(new LCSComparator(LongestCommonSubsequenceSimilarity.NormalizationFlag.AVG, 0.3f,true, 0.5f, AbstractT9Comparator.BOOST_FUNCTIONS.X3, 2),0.2); //Order
            rule.addComparator(new RogueTokenComparator(0.3f, true, 0.7f, AbstractT9Comparator.BOOST_FUNCTIONS.X3,2),0.2); //remediating the removal of frequent tokens
        }catch(Exception e){
            e.printStackTrace();
            System.exit(0);
        }

        return rule;
    }

    public static MatchingRule<Company, Attribute> getMR22(double thresh, HashedDataSet<Company, Attribute> ds1, HashedDataSet<Company, Attribute> ds2, MatchingGoldStandard gsTrain){
        //mr9Description = "Linear Comb Matching rule removing frequent tokens for not token based, and leaving them for token based jaccard";

        //mr11Description = "Weka logistic regression matching rule with postprocessing parameters and fewer comparators";

        String options[] = new String[] { "-S" };
		String modelType = "SimpleLogistic"; // use a logistic regression
        WekaMatchingRule<Company, Attribute> rule = new WekaMatchingRule<>(thresh, modelType, options);

        // add comparators
        rule.addComparator(new CompanyNameComparatorJaccardNgram(3, true, 0.5f)); //three a bit more robust to typos than 4
        rule.addComparator(new CompanyNameComparatorLevenshtein(true, 0.5f)); //typos
        rule.addComparator(new LCSComparator(LongestCommonSubsequenceSimilarity.NormalizationFlag.AVG, 0.3f,true, 0.5f, AbstractT9Comparator.BOOST_FUNCTIONS.X3, 2)); //Order
        rule.addComparator(new RogueTokenComparator(0.3f, true, 0.7f, AbstractT9Comparator.BOOST_FUNCTIONS.X3, 2)); //remediating the removal of frequent tokens

        // train the matching rule's model
		RuleLearner<Company, Attribute> learner = new RuleLearner<>();
		learner.learnMatchingRule(ds1, ds2, null, rule, gsTrain);

		//logger.info(String.format("Matching rule is:\n%s", rule.getModelDescription()));

        return rule;
    }


    public static MatchingRule<Company, Attribute> getMR23(double thresh){
        LinearCombinationMatchingRule<Company, Attribute> rule = new LinearCombinationMatchingRule<>(thresh);
        try{
            rule.addComparator(new CompanyNameComparatorJaccardNgram(3, true, 0.5f),0.3); //three a bit more robust to typos than 4
            rule.addComparator(new CompanyNameComparatorLevenshtein(true, 0.5f),0.15); //typos
            rule.addComparator(new LCSComparator(LongestCommonSubsequenceSimilarity.NormalizationFlag.AVG, 0.3f,true, 0.5f, AbstractT9Comparator.BOOST_FUNCTIONS.X3, 2),0.25); //Order
            rule.addComparator(new RogueTokenComparator(0.4f, true, 0.75f, AbstractT9Comparator.BOOST_FUNCTIONS.X3, 2),0.3); //remediating the removal of frequent tokens
        }catch(Exception e){
            e.printStackTrace();
            System.exit(0);
        }

        return rule;
    }


    public static MatchingRule<Company, Attribute> getMR24(double thresh, HashedDataSet<Company, Attribute> ds1, HashedDataSet<Company, Attribute> ds2, MatchingGoldStandard gsTrain){
        //mr8Description = "Weka tree matching rule with postprocessing parameters";

        // create a matching rule & provide classifier, options
        String tree = "J48"; // new instance of tree
        String options[] = new String[1];
        options[0] = "-R";
        //options[1] = "-C 0.35";
        WekaMatchingRule<Company, Attribute> rule = new WekaMatchingRule<>(thresh, tree, options);

        // add comparators
        rule.addComparator(new CompanyNameComparatorJaccardNgram(3, true, 0.5f)); //three a bit more robust to typos than 4
        rule.addComparator(new CompanyNameComparatorLevenshtein(true, 0.5f)); //typos
        rule.addComparator(new LCSComparator(LongestCommonSubsequenceSimilarity.NormalizationFlag.AVG, 0.3f,true, 0.5f, AbstractT9Comparator.BOOST_FUNCTIONS.X3, 2)); //Order
        rule.addComparator(new RogueTokenComparator(0.4f, true, 0.75f, AbstractT9Comparator.BOOST_FUNCTIONS.X3, 2)); //remediating the removal of frequent tokens

        // train the matching rule's model
		RuleLearner<Company, Attribute> learner = new RuleLearner<>();
		learner.learnMatchingRule(ds1, ds2, null, rule, gsTrain);

		//logger.info(String.format("Matching rule is:\n%s", rule.getModelDescription()));

        return rule;
    }

    public static MatchingRule<Company, Attribute> getMR25(double thresh, HashedDataSet<Company, Attribute> ds1, HashedDataSet<Company, Attribute> ds2, MatchingGoldStandard gsTrain){
        /* First Matching Rule that will use aggresive boosting to see if performance improvements can be generated */

        String options[] = new String[] { "-S" };
		String modelType = "SimpleLogistic"; // use a logistic regression
        WekaMatchingRule<Company, Attribute> rule = new WekaMatchingRule<>(thresh, modelType, options);


        // add comparators
        rule.addComparator(new CompanyNameComparatorJaccardNgram(3, true, 0.3f, true, 0.7f, 4.0f, AbstractT9Comparator.BOOST_FUNCTIONS.X3)); //three a bit more robust to typos than 4
        rule.addComparator(new CompanyNameComparatorLevenshtein(true, 0.3f, true, 0.7f, 4.0f, AbstractT9Comparator.BOOST_FUNCTIONS.X3)); //typos
        rule.addComparator(new LCSComparator(LongestCommonSubsequenceSimilarity.NormalizationFlag.AVG, 0.4f, true, 0.5f, AbstractT9Comparator.BOOST_FUNCTIONS.X3, 3)); //Order
        rule.addComparator(new RogueTokenComparator(0.4f, true, 0.5f, AbstractT9Comparator.BOOST_FUNCTIONS.X3, 3)); //remediating the removal of frequent tokens

        // train the matching rule's model
		RuleLearner<Company, Attribute> learner = new RuleLearner<>();
		learner.learnMatchingRule(ds1, ds2, null, rule, gsTrain);

		//logger.info(String.format("Matching rule is:\n%s", rule.getModelDescription()));

        return rule;
    }


    public static MatchingRule<Company, Attribute> getMR26(double thresh, HashedDataSet<Company, Attribute> ds1, HashedDataSet<Company, Attribute> ds2, MatchingGoldStandard gsTrain){
        /*Rule 25 as reduced error pruned tree */

        // create a matching rule & provide classifier, options
        String tree = "J48"; // new instance of tree
        String options[] = new String[1];
        options[0] = "-R";
        //options[1] = "-C 0.35";
        WekaMatchingRule<Company, Attribute> rule = new WekaMatchingRule<>(thresh, tree, options);

        // add comparators
        rule.addComparator(new CompanyNameComparatorJaccardNgram(3, true, 0.3f, true, 0.7f, 4.0f, AbstractT9Comparator.BOOST_FUNCTIONS.X3)); //three a bit more robust to typos than 4
        rule.addComparator(new CompanyNameComparatorLevenshtein(true, 0.3f, true, 0.7f, 4.0f, AbstractT9Comparator.BOOST_FUNCTIONS.X3)); //typos
        rule.addComparator(new LCSComparator(LongestCommonSubsequenceSimilarity.NormalizationFlag.AVG, 0.4f, true, 0.5f, AbstractT9Comparator.BOOST_FUNCTIONS.X3, 3)); //Order
        rule.addComparator(new RogueTokenComparator(0.4f, true, 0.5f, AbstractT9Comparator.BOOST_FUNCTIONS.X3, 3)); //remediating the removal of frequent tokens

        // train the matching rule's model
		RuleLearner<Company, Attribute> learner = new RuleLearner<>();
		learner.learnMatchingRule(ds1, ds2, null, rule, gsTrain);

		//logger.info(String.format("Matching rule is:\n%s", rule.getModelDescription()));

        return rule;
    }

    public static MatchingRule<Company, Attribute> getMR27(double thresh){
        LinearCombinationMatchingRule<Company, Attribute> rule = new LinearCombinationMatchingRule<>(thresh);
        try{
            rule.addComparator(new CompanyNameComparatorJaccardNgram(
                3,
                true, 
                0.4f, 
                true, 
                0.5f, 
                3, 
                AbstractT9Comparator.BOOST_FUNCTIONS.X3),0.3); //three a bit more robust to typos than 4
            rule.addComparator(new CompanyNameComparatorLevenshtein(true, 0.4f),0.3); //typos
            rule.addComparator(new LCSComparator(
                LongestCommonSubsequenceSimilarity.NormalizationFlag.MIN,
                0.3f,
                true,
                0.5f,
                AbstractT9Comparator.BOOST_FUNCTIONS.SQRT,
                4),0.2); //Order
            rule.addComparator(new RogueTokenComparator(
                0.3f, 
                true, 
                0.5f, 
                AbstractT9Comparator.BOOST_FUNCTIONS.X3,
                4),0.2); //remediating the removal of frequent tokens
        }catch(Exception e){
            e.printStackTrace();
            System.exit(0);
        }

        return rule;
    }


    public static MatchingRule<Company, Attribute> getMR28(double thresh){
        LinearCombinationMatchingRule<Company, Attribute> rule = new LinearCombinationMatchingRule<>(thresh);
        try{
            rule.addComparator(new CompanyNameComparatorLevenshtein(true), 0.3);
            rule.addComparator(new UrlNameComparator(3, true), 0.4);
            rule.addComparator(new RogueTokenComparator(
                0.3f, 
                true, 
                0.5f, 
                AbstractT9Comparator.BOOST_FUNCTIONS.X3,
                4),0.3); //remediating the removal of frequent tokens
        }catch(Exception e){
            e.printStackTrace();
            System.exit(0);
        }

        return rule;
    }

    public static MatchingRule<Company, Attribute> getMR29(double thresh){
       

        LinearCombinationMatchingRule<Company, Attribute> rule = new LinearCombinationMatchingRule<>(thresh);

        //rule.activateDebugReport("data/logs/debugResultsMatchingRule29.csv", 5000);

        try{
            rule.addComparator(new CompanyNameComparatorJaccardNgram(
                3,
                true, 
                0.4f, 
                true, 
                0.5f, 
                3, 
                AbstractT9Comparator.BOOST_FUNCTIONS.X3),0.2); //three a bit more robust to typos than 4
            rule.addComparator(new CompanyNameComparatorJaccardNgram(
                    3,
                    false, 
                    0.4f, 
                    true, 
                    0.5f, 
                    6, 
                    AbstractT9Comparator.BOOST_FUNCTIONS.X3),0.15); //three a bit more robust to typos than 4
            rule.addComparator(new CompanyNameComparatorLevenshtein(true, 0.4f),0.25); //typos
            rule.addComparator(new LCSComparator(
                LongestCommonSubsequenceSimilarity.NormalizationFlag.MIN,
                0.3f,
                true,
                0.5f,
                AbstractT9Comparator.BOOST_FUNCTIONS.SQRT,
                4),0.2); //Order
            rule.addComparator(new RogueTokenComparator(
                0.3f, 
                true, 
                0.5f, 
                AbstractT9Comparator.BOOST_FUNCTIONS.X3,
                4),0.2); //remediating the removal of frequent tokens
        }catch(Exception e){
            e.printStackTrace();
            System.exit(0);
        }

        return rule;
    }

        /**
     * @param thresh final matching threshold
     * @return
     */
    public static MatchingRule<Company, Attribute> getMR30(double thresh){
        LinearCombinationMatchingRule<Company, Attribute> rule = new LinearCombinationMatchingRule<>(thresh);
        try{
            rule.addComparator(new CompanyNameComparatorJaccardNgram(3, false), 0.4);
            rule.addComparator(new LCSComparator(
                LongestCommonSubsequenceSimilarity.NormalizationFlag.MIN,
                0.3f,
                true,
                0.5f,
                AbstractT9Comparator.BOOST_FUNCTIONS.SQRT,
                4),0.3); //Order
            rule.addComparator(new RogueTokenComparator(
                0.3f, 
                true, 
                0.5f, 
                AbstractT9Comparator.BOOST_FUNCTIONS.X3,
                4),0.3); //remediating the removal of frequent tokens
        }catch(Exception e){
            e.printStackTrace();
            System.exit(0);
        }

        return rule;
    }

    public static MatchingRule<Company, Attribute> getMR31(double thresh, HashedDataSet<Company, Attribute> ds1, HashedDataSet<Company, Attribute> ds2, MatchingGoldStandard gsTrain){
        /*Rule 25 as reduced error pruned tree */

        // create a matching rule & provide classifier, options
        String tree = "J48"; // new instance of tree
        String options[] = new String[1];
        options[0] = "-R";
        //options[1] = "-C 0.35";
        WekaMatchingRule<Company, Attribute> rule = new WekaMatchingRule<>(thresh, tree, options);

        // add comparators
        rule.addComparator(new CompanyNameComparatorJaccardNgram(3, true, 0.f, false, 0.7f, 4.0f, AbstractT9Comparator.BOOST_FUNCTIONS.X3)); //three a bit more robust to typos than 4
        rule.addComparator(new CompanyNameComparatorLevenshtein(true, 0.f, false, 0.7f, 4.0f, AbstractT9Comparator.BOOST_FUNCTIONS.X3)); //typos
        rule.addComparator(new LCSComparator(LongestCommonSubsequenceSimilarity.NormalizationFlag.AVG, 0.0f, false, 0.5f, AbstractT9Comparator.BOOST_FUNCTIONS.X3, 3)); //Order
        rule.addComparator(new RogueTokenComparator(0.0f, false, 0.5f, AbstractT9Comparator.BOOST_FUNCTIONS.X3, 3)); //remediating the removal of frequent tokens

        // train the matching rule's model
		RuleLearner<Company, Attribute> learner = new RuleLearner<>();
		learner.learnMatchingRule(ds1, ds2, null, rule, gsTrain);

		//logger.info(String.format("Matching rule is:\n%s", rule.getModelDescription()));

        return rule;
    }
    
}
