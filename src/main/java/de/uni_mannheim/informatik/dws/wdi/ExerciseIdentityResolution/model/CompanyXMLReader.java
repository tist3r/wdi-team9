package de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.model;

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
		company.setName(getValueFromChildElement(node, "Name"));
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


        try{
            company.setLatitude(Double.parseDouble(getValueFromChildElement(node, "Latitude")));
        } catch (NumberFormatException e){
            company.setLatitude(Double.NaN);
        }

        try{
            company.setLongitude(Double.parseDouble(getValueFromChildElement(node, "Longitude")));
        } catch (NumberFormatException e){
            company.setLongitude(Double.NaN);
        }

        
        company.setCeos(getListFromChildElement(node, "CEOs"));



        return company;
    }

    
}
