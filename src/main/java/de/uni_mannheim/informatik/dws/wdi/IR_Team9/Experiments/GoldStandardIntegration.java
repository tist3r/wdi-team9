package de.uni_mannheim.informatik.dws.wdi.IR_Team9.Experiments;

import java.io.File;

import org.slf4j.Logger;

import de.uni_mannheim.informatik.dws.wdi.IR_Team9.Blocking.CompanyBlockingKeyByNameGenerator;
import de.uni_mannheim.informatik.dws.wdi.IR_Team9.Comparators.CompanyNameComparatorJaccardNgram;
import de.uni_mannheim.informatik.dws.wdi.IR_Team9.Comparators.CompanyNameComparatorLevenshtein;
import de.uni_mannheim.informatik.dws.wdi.IR_Team9.model.Company;
import de.uni_mannheim.informatik.dws.wdi.IR_Team9.model.CompanyXMLReader;
import de.uni_mannheim.informatik.dws.winter.matching.MatchingEngine;
import de.uni_mannheim.informatik.dws.winter.matching.blockers.Blocker;
import de.uni_mannheim.informatik.dws.winter.matching.blockers.StandardRecordBlocker;
import de.uni_mannheim.informatik.dws.winter.matching.rules.LinearCombinationMatchingRule;
import de.uni_mannheim.informatik.dws.winter.matching.rules.MatchingRule;
import de.uni_mannheim.informatik.dws.winter.model.Correspondence;
import de.uni_mannheim.informatik.dws.winter.model.HashedDataSet;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;
import de.uni_mannheim.informatik.dws.winter.model.io.CSVCorrespondenceFormatter;
import de.uni_mannheim.informatik.dws.winter.processing.Processable;
import de.uni_mannheim.informatik.dws.winter.utils.WinterLogManager;

public class GoldStandardIntegration{

    private static final Logger logger = WinterLogManager.activateLogger("default");
    static String dbpediaPath = "data/input/dbpedia.xml";
    static String forbesPath = "data/input/Forbes_results.xml";
    static String dataworldPath = "data/input/dataworld_ts.xml";
    static String kagglePath = "companies_shorted_results.xml";

    public void standardIntegration(String pathToDataSet1, String pathToDataSet2, MatchingRule<Company,Attribute> rule, Blocker<Company, Attribute, Company, Attribute> blocker, String outputPath) throws Exception{
        // loading data
        logger.info("*\tLoading datasets\t*");
        HashedDataSet<Company, Attribute> dataset1 = new HashedDataSet<>();
        HashedDataSet<Company, Attribute> dataset2 = new HashedDataSet<>();

        new CompanyXMLReader().loadFromXML(new File(pathToDataSet1), "/Companies/Company", dataset1);
        new CompanyXMLReader().loadFromXML(new File(pathToDataSet2), "/Companies/Company", dataset2);

        //--------
        //Identity Resolution
        //--------
        
        // Initialize Matching Engine
        MatchingEngine<Company, Attribute> engine = new MatchingEngine<>();

        //Execute the matching
		logger.info("*\tRunning identity resolution\t*");
		Processable<Correspondence<Company, Attribute>> correspondences = engine.runIdentityResolution(dataset1, dataset2, null, rule, blocker);

        // write the correspondences to the output file
		new CSVCorrespondenceFormatter().writeCSV(new File(outputPath), correspondences);
    }


    public static void main(String[] args) throws Exception{
        LinearCombinationMatchingRule<Company, Attribute> matchingRule1 = new LinearCombinationMatchingRule<>(0.75);
        LinearCombinationMatchingRule<Company, Attribute> matchingRule2 = new LinearCombinationMatchingRule<>(0.75);
        LinearCombinationMatchingRule<Company, Attribute> matchingRule3 = new LinearCombinationMatchingRule<>(0.75);

        matchingRule1.addComparator(new CompanyNameComparatorJaccardNgram(3), 1);
        matchingRule2.addComparator(new CompanyNameComparatorLevenshtein(), 1);
        matchingRule3.addComparator(new CompanyNameComparatorLevenshtein(), 0.5);
        matchingRule3.addComparator(new CompanyNameComparatorJaccardNgram(3), 0.5);

        StandardRecordBlocker<Company, Attribute> blocker = new StandardRecordBlocker<Company, Attribute>(new CompanyBlockingKeyByNameGenerator());

        GoldStandardIntegration gsi = new GoldStandardIntegration();

        gsi.standardIntegration(dbpediaPath, forbesPath, matchingRule1, blocker, "data/output/gs/gs_dbpedia_forbes_jac.csv");
    }
    
}