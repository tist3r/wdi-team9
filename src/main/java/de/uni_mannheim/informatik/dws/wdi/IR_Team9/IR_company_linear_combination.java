package de.uni_mannheim.informatik.dws.wdi.IR_Team9;

import java.io.File;

import org.slf4j.Logger;

import de.uni_mannheim.informatik.dws.wdi.IR_Team9.model.Company;
import de.uni_mannheim.informatik.dws.wdi.IR_Team9.model.CompanyXMLReader;
import de.uni_mannheim.informatik.dws.winter.model.HashedDataSet;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;
import de.uni_mannheim.informatik.dws.winter.utils.WinterLogManager;


public class IR_company_linear_combination {
    private static final Logger logger = WinterLogManager.activateLogger("default");

    public static void main(String[] args) throws Exception{
        // loading data
		logger.info("*\tLoading datasets\t*");
		HashedDataSet<Company, Attribute> dbPediaCompanies = new HashedDataSet<>();

		new CompanyXMLReader().loadFromXML(new File("data/input/dbpedia.xml"), "/Companies/Company", dbPediaCompanies);
        
    }   
}
