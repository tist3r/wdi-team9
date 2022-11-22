package de.uni_mannheim.informatik.dws.wdi.IR_Team9.DataFusion;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.Locale;


import de.uni_mannheim.informatik.dws.wdi.IR_Team9.model.Company;
import de.uni_mannheim.informatik.dws.wdi.IR_Team9.model.CompanyXMLReader;
import de.uni_mannheim.informatik.dws.winter.model.FusibleDataSet;
import de.uni_mannheim.informatik.dws.winter.model.FusibleHashedDataSet;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;
import de.uni_mannheim.informatik.dws.winter.utils.WinterLogManager;


public class DataFusion_Main {

	private static final org.slf4j.Logger Logger = WinterLogManager.activateLogger("traceFile");

	
	public static void main(String[] args) throws Exception {
		
		FusibleDataSet<Company, Attribute> ds1 = new FusibleHashedDataSet<>();
		new CompanyXMLReader().loadFromXML (new File("data/input/dataworld_ts.xm1"), "/companies/company", ds1);
		ds1.printDataSetDensityReport();
		FusibleDataSet<Company, Attribute> ds2 = new FusibleHashedDataSet<>();
		new CompanyXMLReader().loadFromXML (new File("data/input/dbpedia.xm1"), "/companies/company", ds2);
		ds2.printDataSetDensityReport();
		FusibleDataSet<Company, Attribute> ds3 = new FusibleHashedDataSet<>();
		new CompanyXMLReader().loadFromXML (new File("data/input/Forbes_results.xm1"), "/companies/company", ds3);
		ds3.printDataSetDensityReport();
		
		ds1.setScore(1.0);
		ds2.setScore(2.0);
		ds3.setScore(3.0);
		
		 // Date (e.g. last update)
		java.time.format.DateTimeFormatter formatter = new DateTimeFormatterBuilder()
		         .appendPattern("yyyy-MM-dd")
		         .parseDefaulting(ChronoField.CLOCK_HOUR_OF_DAY, 0)
		         .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
		         .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
		         .toFormatter(Locale.ENGLISH);
		
		ds1.setDate(LocalDateTime.parse("2022-01-01", formatter));
		ds2.setDate(LocalDateTime.parse("2020-01-01", formatter));
		ds3.setDate(LocalDateTime.parse("2018-01-01", formatter));
		
		 // load correspondences
		System.out.println("*\n*\tloading correspondences");
		
	}

			
			
}
