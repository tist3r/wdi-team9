package de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.model;

import org.w3c.dom.Node;

import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;
import de.uni_mannheim.informatik.dws.winter.model.io.XMLMatchableReader;

public class CompanyXMLReader extends XMLMatchableReader<Company, Attribute>{

    @Override
    public Company createModelFromElement(Node node, String provenanceInfo) {
        String id = getValueFromChildElement(node, "ID");

        // create the object with id and provenance information
		Company company = new Company(id, provenanceInfo);

        // fill the attributes
		company.setName(getValueFromChildElement(node, "Name"));
		company.setUrl(getValueFromChildElement(node, "Domain"));
        company.setIndustries(getListFromChildElement(node, "Industries"));
        company.setYearFounded(Integer.parseInt(getValueFromChildElement(node, "Year_founded")));
        company.setSizeRange(getValueFromChildElement(node, "Size_range"));
        company.setLocality(getValueFromChildElement(node, "Locality"));
        company.setCountry(getValueFromChildElement(node, "Country"));
        company.setLinkedinUrl(getValueFromChildElement(node, "Linkedin_url"));
        company.setCurrEmpEst(Integer.parseInt(getValueFromChildElement(node, "Current_employee_est")));
        company.setTotalEmpEst(Integer.parseInt(getValueFromChildElement(node, "Total_employee_est")));
        
        //TODO: Sales, Profit, etc.


        company.setSector(getValueFromChildElement(node, "Sector"));
        company.setGlobalRank(getValueFromChildElement(node, "Global_rank"));
        company.setLatitude(Double.parseDouble(getValueFromChildElement(node, "Latitude")));
        company.setLongitude(Double.parseDouble(getValueFromChildElement(node, "Longitude")));
        company.setCeos(getListFromChildElement(node, "CEOs"));



        return null;
    }

    
}
