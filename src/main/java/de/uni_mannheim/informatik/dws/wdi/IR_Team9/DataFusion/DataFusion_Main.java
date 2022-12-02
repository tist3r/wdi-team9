package de.uni_mannheim.informatik.dws.wdi.IR_Team9.DataFusion;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.Locale;

import de.uni_mannheim.informatik.dws.wdi.IR_Team9.DataFusion.evaluation.CountryEvaluationRule;
import de.uni_mannheim.informatik.dws.wdi.IR_Team9.DataFusion.evaluation.NameEvaluationRule;
import de.uni_mannheim.informatik.dws.wdi.IR_Team9.DataFusion.fusers.CountryFuser;
import de.uni_mannheim.informatik.dws.wdi.IR_Team9.DataFusion.fusers.NameFuser;
import de.uni_mannheim.informatik.dws.wdi.IR_Team9.model.Company;
import de.uni_mannheim.informatik.dws.wdi.IR_Team9.model.CompanyXMLFormatter;
import de.uni_mannheim.informatik.dws.wdi.IR_Team9.model.CompanyXMLReader;
import de.uni_mannheim.informatik.dws.wdi.IR_Team9.utils.Constants;
import de.uni_mannheim.informatik.dws.winter.datafusion.CorrespondenceSet;
import de.uni_mannheim.informatik.dws.winter.datafusion.DataFusionEngine;
import de.uni_mannheim.informatik.dws.winter.datafusion.DataFusionEvaluator;
import de.uni_mannheim.informatik.dws.winter.datafusion.DataFusionStrategy;
import de.uni_mannheim.informatik.dws.winter.model.DataSet;
import de.uni_mannheim.informatik.dws.winter.model.FusibleDataSet;
import de.uni_mannheim.informatik.dws.winter.model.FusibleHashedDataSet;
import de.uni_mannheim.informatik.dws.winter.model.RecordGroupFactory;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;
import de.uni_mannheim.informatik.dws.winter.utils.WinterLogManager;


public class DataFusion_Main {

	private static final org.slf4j.Logger logger = WinterLogManager.activateLogger("traceFile");

	
	public static void main(String[] args) throws Exception {
		
		FusibleDataSet<Company, Attribute> ds1 = new FusibleHashedDataSet<>();
		new CompanyXMLReader().loadFromXML (new File("data/input/dataworld_ts.xml"), Constants.RECORD_PATH, ds1);
		FusibleDataSet<Company, Attribute> ds2 = new FusibleHashedDataSet<>();
		new CompanyXMLReader().loadFromXML (new File("data/input/dbpedia.xml"), Constants.RECORD_PATH, ds2);
		FusibleDataSet<Company, Attribute> ds3 = new FusibleHashedDataSet<>();
		new CompanyXMLReader().loadFromXML (new File("data/input/Forbes_results.xml"), Constants.RECORD_PATH, ds3);
		FusibleDataSet<Company, Attribute> ds4 = new FusibleHashedDataSet<>();
		new CompanyXMLReader().loadFromXML (new File("data/input/kaggle_filtered.xml"), Constants.RECORD_PATH, ds4);
		
		
		ds1.setScore(1.0);
		ds2.setScore(1.0);
		ds3.setScore(2.0); //forbes has the highest credibility for sales value and thus gets the highest score
		ds4.setScore(1.0);

		
		java.time.format.DateTimeFormatter formatter = new DateTimeFormatterBuilder()
		         .appendPattern("yyyy-MM-dd")
		         .parseDefaulting(ChronoField.CLOCK_HOUR_OF_DAY, 0)
		         .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
		         .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
		         .toFormatter(Locale.ENGLISH);
		
		ds1.setDate(LocalDateTime.parse("2022-01-01", formatter));
		ds2.setDate(LocalDateTime.parse("2020-01-01", formatter));
		ds3.setDate(LocalDateTime.parse("2018-01-01", formatter));
		ds4.setDate(LocalDateTime.parse("2016-01-01", formatter));

		
		// load correspondences
		logger.info("*\tLoading correspondences\t*");
		CorrespondenceSet<Company, Attribute> correspondences = new CorrespondenceSet<>();
		correspondences.loadCorrespondences(new File("data/correspondences/21_10_85_dbpedia_kaggle_f/dbpedia_kaggle_f_corr.csv"),ds2, ds4);

		correspondences.loadCorrespondences(new File("data/correspondences/27_11_85_dbpedia_dw/dbpedia_dw_corr.csv"),ds2, ds1);

		correspondences.loadCorrespondences(new File("data/correspondences/5_10_9_dbpedia_kaggle_f/dbpedia_kaggle_f_corr.csv"),ds2, ds4);

		correspondences.loadCorrespondences(new File("data/correspondences/5_10_9_dw_kaggle_f/dw_kaggle_f_corr.csv"),ds1, ds4);

		correspondences.loadCorrespondences(new File("data/correspondences/5_10_9_forbes_kaggle_f/forbes_kaggle_f_corr.csv"),ds3, ds4);
		
		// write group size distribution
		correspondences.printGroupSizeDistribution();

		// load the gold standard
		logger.info("*\tEvaluating results\t*");
		DataSet<Company, Attribute> gs = new FusibleHashedDataSet<>();
		new CompanyXMLReader().loadFromXML(new File("data/goldstandard/gold.xml"), Constants.RECORD_PATH, gs);
		
		for(Company m : gs.get()) {
			logger.info(String.format("gs: %s", m.getIdentifier()));
		}
		
		// define the fusion strategy
		DataFusionStrategy<Company, Attribute> strategy = new DataFusionStrategy<>(new CompanyXMLReader());
		// write debug results to file
		strategy.activateDebugReport("data/output/debugResultsDatafusion.csv", -1, gs);
		
		// add attribute fusers
		strategy.addAttributeFuser(Company.NAME, new NameFuser(), new NameEvaluationRule());
		strategy.addAttributeFuser(Company.COUNTRY, new CountryFuser(), new CountryEvaluationRule());
		
		// create the fusion engine
		DataFusionEngine<Company, Attribute> engine = new DataFusionEngine<>(strategy);

		// print consistency report
		engine.printClusterConsistencyReport(correspondences, null);

		// print record groups sorted by consistency
		engine.writeRecordGroupsByConsistency(new File("data/output/recordGroupConsistencies.csv"), correspondences,
				null);

		// run the fusion
		logger.info("*\tRunning data fusion\t*");
		FusibleDataSet<Company, Attribute> fusedDataSet = engine.run(correspondences, null);
		// write the result
		new CompanyXMLFormatter().writeXML(new File("data/output/fused.xml"), fusedDataSet);

		// evaluate
		DataFusionEvaluator<Company, Attribute> evaluator = new DataFusionEvaluator<>(strategy,
				new RecordGroupFactory<Company, Attribute>());

		double accuracy = evaluator.evaluate(fusedDataSet, gs, null);

		logger.info(String.format("*\tAccuracy: %.2f", accuracy));
	}
		
	}

			
			

