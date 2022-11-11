package de.uni_mannheim.informatik.dws.wdi.IR_Team9.Experiments;

import java.io.File;

import org.slf4j.Logger;

import de.uni_mannheim.informatik.dws.wdi.IR_Team9.Blocking.CompanyBlockingKeyByNameGenerator;
import de.uni_mannheim.informatik.dws.wdi.IR_Team9.Blocking.CompanyQgramBlocking;
import de.uni_mannheim.informatik.dws.wdi.IR_Team9.Comparators.CompanyNameComparatorJaccardNgram;
import de.uni_mannheim.informatik.dws.wdi.IR_Team9.Comparators.CompanyNameComparatorJaccardToken;
import de.uni_mannheim.informatik.dws.wdi.IR_Team9.Comparators.CompanyNameComparatorLevenshtein;
import de.uni_mannheim.informatik.dws.wdi.IR_Team9.model.Company;
import de.uni_mannheim.informatik.dws.wdi.IR_Team9.model.CompanyCSVCorrespondenceFormatter;
import de.uni_mannheim.informatik.dws.wdi.IR_Team9.model.CompanyXMLReader;
import de.uni_mannheim.informatik.dws.wdi.IR_Team9.utils.Constants;
import de.uni_mannheim.informatik.dws.wdi.IR_Team9.utils.LabeledMatchingGoldStandard;
import de.uni_mannheim.informatik.dws.winter.matching.MatchingEngine;
import de.uni_mannheim.informatik.dws.winter.matching.MatchingEvaluator;
import de.uni_mannheim.informatik.dws.winter.matching.algorithms.RuleLearner;
import de.uni_mannheim.informatik.dws.winter.matching.blockers.SortedNeighbourhoodBlocker;
import de.uni_mannheim.informatik.dws.winter.matching.blockers.StandardRecordBlocker;
import de.uni_mannheim.informatik.dws.winter.matching.rules.WekaMatchingRule;
import de.uni_mannheim.informatik.dws.winter.model.Correspondence;
import de.uni_mannheim.informatik.dws.winter.model.HashedDataSet;
import de.uni_mannheim.informatik.dws.winter.model.MatchingGoldStandard;
import de.uni_mannheim.informatik.dws.winter.model.Performance;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;
import de.uni_mannheim.informatik.dws.winter.model.io.CSVCorrespondenceFormatter;
import de.uni_mannheim.informatik.dws.winter.processing.Processable;
import de.uni_mannheim.informatik.dws.winter.utils.WinterLogManager;

public class IR_using_machine_learning {
	
	/*
	 * Logging Options:
	 * 		default: 	level INFO	- console
	 * 		trace:		level TRACE     - console
	 * 		infoFile:	level INFO	- console/file
	 * 		traceFile:	level TRACE	- console/file
	 *  
	 * To set the log level to trace and write the log to winter.log and console, 
	 * activate the "traceFile" logger as follows:
	 *     private static final Logger logger = WinterLogManager.activateLogger("traceFile");
	 *
	 */

	private static final Logger logger = WinterLogManager.activateLogger("default");
	
    public static void main( String[] args ) throws Exception
    {
		// loading data
		logger.info("*\tLoading datasets\t*");
		HashedDataSet<Company, Attribute> dbpedia = new HashedDataSet<>();
		new CompanyXMLReader().loadFromXML(new File(Constants.getDatasetPath("dbpedia")), Constants.RECORD_PATH, dbpedia);


		HashedDataSet<Company, Attribute> forbes = new HashedDataSet<>();
		new CompanyXMLReader().loadFromXML(new File(Constants.getDatasetPath("forbes")), Constants.RECORD_PATH, forbes);
		
		// load the training set
		MatchingGoldStandard gsTraining = new MatchingGoldStandard();
		System.out.println("[INFO ] Loading Train Data");
		gsTraining.loadFromCSVFile(new File(Constants.getTrainData("dbpedia", "forbes")));

		// create a matching rule
		String options[] = new String[] { "-S" };
		String modelType = "SimpleLogistic"; // use a logistic regression
		WekaMatchingRule<Company, Attribute> matchingRule = new WekaMatchingRule<>(0.85, modelType, options);
		matchingRule.activateDebugReport("data/output/debugResultsMatchingRule.csv", 1000, gsTraining);
		
		// add comparators
        matchingRule.addComparator(new CompanyNameComparatorJaccardNgram(3, true, 0.55f));
        matchingRule.addComparator(new CompanyNameComparatorJaccardNgram(3, false, 0.55f));
        matchingRule.addComparator(new CompanyNameComparatorJaccardNgram(4, true, 0.55f));
        matchingRule.addComparator(new CompanyNameComparatorJaccardNgram(4, false, 0.55f));
        matchingRule.addComparator(new CompanyNameComparatorLevenshtein(true,0.55f));
        matchingRule.addComparator(new CompanyNameComparatorLevenshtein(false,0.55f));
        matchingRule.addComparator(new CompanyNameComparatorJaccardToken(0.4f, false));
		
		// train the matching rule's model
		logger.info("*\tLearning matching rule\t*");
		RuleLearner<Company, Attribute> learner = new RuleLearner<>();
		learner.learnMatchingRule(dbpedia, forbes, null, matchingRule, gsTraining);
		logger.info(String.format("Matching rule is:\n%s", matchingRule.getModelDescription()));
		
		// create a blocker (blocking strategy)
		// StandardRecordBlocker<Company, Attribute> blocker = new StandardRecordBlocker<Company, Attribute>(new CompanyQgramBlocking(3));
		SortedNeighbourhoodBlocker<Company, Attribute, Attribute> blocker 
			= new SortedNeighbourhoodBlocker<>(new CompanyQgramBlocking(3), 100);

		blocker.collectBlockSizeData("data/output/debugResultsBlocking.csv", 100);
		
		// Initialize Matching Engine
		MatchingEngine<Company, Attribute> engine = new MatchingEngine<>();

		// Execute the matching
		logger.info("*\tRunning identity resolution\t*");
		Processable<Correspondence<Company, Attribute>> correspondences = engine.runIdentityResolution(
				dbpedia, forbes, null, matchingRule,
				blocker);

		// write the correspondences to the output file
		new CompanyCSVCorrespondenceFormatter().writeCSV(new File("data/output/dbpedia_forbes_corr_ml.csv"), correspondences);

		// load the gold standard (test set)
		logger.info("*\tLoading gold standard\t*");
		MatchingGoldStandard gsTest = new MatchingGoldStandard();
		System.out.println("[INFO ] Loading Test Data");
		gsTest.loadFromCSVFile(new File(Constants.getTestData("dbpedia", "forbes")));
		
		// evaluate your result
		logger.info("*\tEvaluating result\t*");
		MatchingEvaluator<Company, Attribute> evaluator = new MatchingEvaluator<Company, Attribute>();
		Performance perfTest = evaluator.evaluateMatching(correspondences, gsTest);

		evaluator.writeEvaluation(new File("data/output/evaluation"), correspondences, gsTest);

		
		// print the evaluation result
		logger.info("Company Test set evaluation");
		logger.info(String.format(
				"Precision: %.4f",perfTest.getPrecision()));
		logger.info(String.format(
				"Recall: %.4f",	perfTest.getRecall()));
		logger.info(String.format(
				"F1: %.4f",perfTest.getF1()));
    }
}