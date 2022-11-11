package de.uni_mannheim.informatik.dws.wdi.IR_Team9.model;

import java.lang.NumberFormatException;

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
        String name = getValueFromChildElement(node, "Name");
		company.setName(name);
		company.setUrl(getValueFromChildElement(node, "Domain"));
        company.setIndustries(getListFromChildElement(node, "Industries"));

        try{
            company.setYearFounded(Integer.parseInt(getValueFromChildElement(node, "Year_founded")));
        } catch (NumberFormatException e){
            company.setYearFounded(null);
        }
        
        company.setSizeRange(getValueFromChildElement(node, "Size_range"));
        company.setLocality(getValueFromChildElement(node, "Locality"));
        company.setCountry(getValueFromChildElement(node, "Country"));
        company.setLinkedinUrl(getValueFromChildElement(node, "Linkedin_url"));

        try{
            company.setCurrEmpEst(Integer.parseInt(getValueFromChildElement(node, "Current_employee_est")));
        } catch (NumberFormatException e){
            company.setCurrEmpEst(null);
        }

        try{
            company.setTotalEmpEst(Integer.parseInt(getValueFromChildElement(node, "Total_employee_est")));
        } catch (NumberFormatException e){
            company.setTotalEmpEst(null);
        }
        
        //TODO: Sales, Profit, etc.


        company.setSector(getValueFromChildElement(node, "Sector"));
        company.setGlobalRank(getValueFromChildElement(node, "Global_rank"));


        if (getValueFromChildElement(node, "Latitude") != null){
            company.setLatitude(Double.parseDouble(getValueFromChildElement(node, "Latitude")));
        } else{
            company.setLatitude(Double.NaN);
        }

        if (getValueFromChildElement(node, "Longitude") != null){
            company.setLongitude(Double.parseDouble(getValueFromChildElement(node, "Longitude")));
        } else{
            company.setLongitude(Double.NaN);
        }
        
        
        company.setCeos(getListFromChildElement(node, "CEOs"));



        return company;
    }

    
}
