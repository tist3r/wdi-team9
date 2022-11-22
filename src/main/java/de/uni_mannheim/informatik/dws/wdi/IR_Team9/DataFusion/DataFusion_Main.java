package de.uni_mannheim.informatik.dws.wdi.IR_Team9.DataFusion;

import java.io.File;


import de.uni_mannheim.informatik.dws.wdi.IR_Team9.model.Company;
import de.uni_mannheim.informatik.dws.wdi.IR_Team9.model.CompanyXMLReader;
import de.uni_mannheim.informatik.dws.winter.model.FusibleDataSet;
import de.uni_mannheim.informatik.dws.winter.model.FusibleHashedDataSet;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;

public class DataFusion_Main {

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
		
		
		
		
	}

}
