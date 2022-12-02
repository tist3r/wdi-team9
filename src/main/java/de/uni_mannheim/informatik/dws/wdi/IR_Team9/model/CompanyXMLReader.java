package de.uni_mannheim.informatik.dws.wdi.IR_Team9.model;

import java.lang.NumberFormatException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.w3c.dom.Node;

import de.uni_mannheim.informatik.dws.winter.model.FusibleFactory;
import de.uni_mannheim.informatik.dws.winter.model.RecordGroup;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;
import de.uni_mannheim.informatik.dws.winter.model.io.XMLMatchableReader;
import org.apache.commons.lang3.StringUtils;


public class CompanyXMLReader extends XMLMatchableReader<Company, Attribute> implements FusibleFactory<Company, Attribute>{

    @Override
    public Company createModelFromElement(Node node, String provenanceInfo) {

        if(getValueFromChildElement(node, "ID") == null){
            System.out.println(getValueFromChildElement(node, "Name"));
        }
        
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
            company.setYearFounded(0);
        }
        
        company.setSizeRange(getValueFromChildElement(node, "Size_range"));
        company.setLocality(getValueFromChildElement(node, "Locality"));
        company.setCountry(getValueFromChildElement(node, "Country"));
        company.setLinkedinUrl(getValueFromChildElement(node, "Linkedin_url"));

        try{
            company.setCurrEmpEst((double) Integer.parseInt(getValueFromChildElement(node, "Current_employee_est")));
        } catch (NumberFormatException e){
            company.setCurrEmpEst(null);
        }

        try{
            company.setTotalEmpEst(Integer.parseInt(getValueFromChildElement(node, "Total_employee_est")));
        } catch (NumberFormatException e){
            company.setTotalEmpEst(null);
        }

        try{
            company.setSalesAmount(Integer.parseInt(getValueFromChildElement(node, "Sales")));
        } catch (NumberFormatException e){
            company.setSalesAmount(null);
        }

        try{
            company.setProfitAmount(Integer.parseInt(getValueFromChildElement(node, "Profits")));
        } catch (NumberFormatException e){
            company.setProfitAmount(null);
        }

        try{
            company.setMvAmount(Integer.parseInt(getValueFromChildElement(node, "Market_value")));
        } catch (NumberFormatException e){
            company.setMvAmount(null);
        }

        try{
            company.setAssetAmount(Integer.parseInt(getValueFromChildElement(node, "Assets")));
        } catch (NumberFormatException e){
            company.setAssetAmount(null);
        }

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


        //System.out.println(company.toString());
        return company;
    }

    @Override
    public Company createInstanceForFusion(RecordGroup<Company, Attribute> cluster) {
        	
        List<String> ids = new LinkedList<>();
        
        for (Company m : cluster.getRecords()) {
            ids.add(m.getIdentifier());
        }
        
        Collections.sort(ids);
        
        String mergedId = StringUtils.join(ids, '+');
        
        return new Company(mergedId, "fused");
	}
}
