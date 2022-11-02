package de.uni_mannheim.informatik.dws.wdi.IR_Team9;

import java.io.File;

import org.slf4j.Logger;

import de.uni_mannheim.informatik.dws.wdi.IR_Team9.Blocking.CompanyBlockingKeyByNameGenerator;
import de.uni_mannheim.informatik.dws.wdi.IR_Team9.Comparators.CompanyNameUrlComparator;
import de.uni_mannheim.informatik.dws.wdi.IR_Team9.model.Company;
import de.uni_mannheim.informatik.dws.wdi.IR_Team9.model.CompanyXMLReader;
import de.uni_mannheim.informatik.dws.winter.matching.MatchingEngine;
import de.uni_mannheim.informatik.dws.winter.matching.blockers.NoBlocker;
import de.uni_mannheim.informatik.dws.winter.matching.blockers.StandardRecordBlocker;
import de.uni_mannheim.informatik.dws.winter.matching.rules.LinearCombinationMatchingRule;
import de.uni_mannheim.informatik.dws.winter.model.Correspondence;
import de.uni_mannheim.informatik.dws.winter.model.HashedDataSet;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;
import de.uni_mannheim.informatik.dws.winter.model.io.CSVCorrespondenceFormatter;
import de.uni_mannheim.informatik.dws.winter.processing.Processable;
import de.uni_mannheim.informatik.dws.winter.utils.WinterLogManager;


public class duplicate_detection_dbpedia {
    private static final Logger logger = WinterLogManager.activateLogger("default");

    public static void main(String[] args) throws Exception{
        // loading data
		logger.info("*\tLoading datasets\t*");
		HashedDataSet<Company, Attribute> dbPediaCompanies = new HashedDataSet<>();

		new CompanyXMLReader().loadFromXML(new File("data/input/dbpedia.xml"), "/Companies/Company", dbPediaCompanies);
     
        //--------
        //DUPLICATE DETECTION
        //--------

        //create matching rule
        LinearCombinationMatchingRule<Company, Attribute> matchingRule = new LinearCombinationMatchingRule<>(0.85);

        //add comparators (name and url)
        matchingRule.addComparator(new CompanyNameUrlComparator(0.95, 0.05, true, true), 1);

        // create a blocker (blocking strategy)
        //NoBlocker<Company, Attribute> blocker = new NoBlocker<>();
        StandardRecordBlocker<Company, Attribute> blocker = new StandardRecordBlocker<Company, Attribute>(new CompanyBlockingKeyByNameGenerator());

        // Initialize Matching Engine
		MatchingEngine<Company, Attribute> engine = new MatchingEngine<>();

		// Execute the matching
		logger.info("*\tRunning identity resolution\t*");
		Processable<Correspondence<Company, Attribute>> correspondences = engine.runDuplicateDetection(dbPediaCompanies, matchingRule, blocker);

        // write the correspondences to the output file
		new CSVCorrespondenceFormatter().writeCSV(new File("data/output/company_duplicates_dbpedia.csv"), correspondences);
    }   
}
