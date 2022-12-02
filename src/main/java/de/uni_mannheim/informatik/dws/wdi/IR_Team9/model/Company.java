package de.uni_mannheim.informatik.dws.wdi.IR_Team9.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.uni_mannheim.informatik.dws.winter.model.AbstractRecord;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;
import de.uni_mannheim.informatik.dws.winter.utils.StringUtils;

public class Company extends AbstractRecord<Attribute>{

    /* EXAMPLE RECORD
    <Company>
		<ID>dbpedia_3</ID>
		<Name>1st Constitution Bancorp</Name>
		<Domain>https://www.1stconstitution.com/</Domain>
		<Industries>
			<Industry>Bank</Industry>
		</Industries>
		<Year_founded>1989</Year_founded>
		<Current_employee_est>183</Current_employee_est>
		<Sales/>
		<Profits/>
		<Assets>
			<Amount>1079000000</Amount>
			<Currency>usDollar</Currency>
		</Assets>
		<CEOs>
			<CEO></CEO>
		</CEOs>
	</Company>
    */ 

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//protected String id;
    private Map<Attribute, Collection<String>> provenance = new HashMap<>();
    private Collection<String> recordProvenance;

    private String name;
    private String url;
    private List<String> industries;
    private Double yearFounded;
    private String sizeRange;
    private String locality;
    private String country;
    private String linkedinUrl;
    private Double currEmpEst;
    private Integer totalEmpEst;
    private Integer salesAmount;
    private String salesCurrency;
    private Integer profitAmount;
    private String profitCurrency;
    private Integer assetAmount;
    private String assetCurrency;
    private Integer mvAmount;
    private String mvCurrency;
    private String sector;
    private String globalRank;
    private double latitude;
    private double longitude;
    private List<String> ceos;


    
    //Constructor
    public Company(String identifier, String provenance) {
		super(identifier, provenance);

	}

    public void setRecordProvenance(Collection<String> provenance) {
		recordProvenance = provenance;
	}

	public Collection<String> getRecordProvenance() {
		return recordProvenance;
	}

	public void setAttributeProvenance(Attribute attribute,
			Collection<String> provenance) {
		this.provenance.put(attribute, provenance);
	}

	public Collection<String> getAttributeProvenance(String attribute) {
		return provenance.get(attribute);
	}

    //Getters and Setters
  
	@Override
	public String getIdentifier() {
		return this.id;
	}

	


    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }
   
    
    public String getName() {
        //System.out.println(this.getId());
        //if(this.name == null){System.out.println(this.getId());}
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<String> getIndustries() {
        return this.industries;
    }

    public void setIndustries(List<String> industries) {
        this.industries = industries;
    }

    public Double getYearFounded() {
        return this.yearFounded;
    }

    public void setYearFounded(double a) {
        this.yearFounded = a;
    }

    public String getSizeRange() {
        return this.sizeRange;
    }

    public void setSizeRange(String sizeRange) {
        this.sizeRange = sizeRange;
    }

    public String getLocality() {
        return this.locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public String getCountry() {
        return this.country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getLinkedinUrl() {
        return this.linkedinUrl;
    }

    public void setLinkedinUrl(String linkedinUrl) {
        this.linkedinUrl = linkedinUrl;
    }

    public Double getCurrEmpEst() {
        return this.currEmpEst;
    }

    public void setCurrEmpEst(Double double1) {
        this.currEmpEst = double1;
    }

    public Integer getTotalEmpEst() {
        return this.totalEmpEst;
    }

    public void setTotalEmpEst(Integer totalEmpEst) {
        this.totalEmpEst = totalEmpEst;
    }

    public Integer getSalesAmount() {
        return this.salesAmount;
    }

    public void setSalesAmount(Integer salesAmount) {
        this.salesAmount = salesAmount;
    }

    public String getSalesCurrency() {
        return this.salesCurrency;
    }

    public void setSalesCurrency(String salesCurrency) {
        this.salesCurrency = salesCurrency;
    }

    public Integer getProfitAmount() {
        return this.profitAmount;
    }

    public void setProfitAmount(Integer profitAmount) {
        this.profitAmount = profitAmount;
    }

    public String getProfitCurrency() {
        return this.profitCurrency;
    }

    public void setProfitCurrency(String profitCurrency) {
        this.profitCurrency = profitCurrency;
    }

    public Integer getAssetAmount() {
        return this.assetAmount;
    }

    public void setAssetAmount(Integer asset_Aount) {
        this.assetAmount = asset_Aount;
    }

    public String getAssetCurrency() {
        return this.assetCurrency;
    }

    public void setAssetCurrency(String assetCurrency) {
        this.assetCurrency = assetCurrency;
    }

    public Integer getMvAmount() {
        return this.mvAmount;
    }

    public void setMvAmount(Integer mvAmount) {
        this.mvAmount = mvAmount;
    }

    public String getMvCurrency() {
        return this.mvCurrency;
    }

    public void setMvCurrency(String mvCurrency) {
        this.mvCurrency = mvCurrency;
    }

    public String getSector() {
        return this.sector;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }

    public String getGlobalRank() {
        return this.globalRank;
    }

    public void setGlobalRank(String globalRank) {
        this.globalRank = globalRank;
    }

    public double getLatitude() {
        return this.latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return this.longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public List<String> getCeos() {
        return this.ceos;
    }

    public void setCeos(List<String> ceos) {
        this.ceos = ceos;
    }
    



    //Override methods

    @Override
    public String toString() {
        return String.format("[Company %s: %s / %s]", getIdentifier(), getName(), getUrl());
    }

    @Override
	public int hashCode() {
		return getIdentifier().hashCode();
	}

    @Override
	public boolean equals(Object obj) {
		if(obj instanceof Company){
			return this.getIdentifier().equals(((Company) obj).getIdentifier());
		}else
			return false;
	}


    public static final Attribute NAME = new Attribute("Name");
	public static final Attribute URL = new Attribute("URL");
	public static final Attribute COUNTRY = new Attribute("COUNTRY");
	public static final Attribute INDUSTRY = new Attribute("INDUSTRY");
	public static final Attribute SALES_AMOUNT = new Attribute("SALES_AMOUNT");
	public static final Attribute SALES_CURRENCY = new Attribute("SALES_CURRENCY");
	public static final Attribute CURRENT_EMPLOYEES = new Attribute("CURRENT_EMPLOYEES");
	public static final Attribute YEAR_FOUNDED = new Attribute("YEAR_FOUNDED");
	
    @Override
	public boolean hasValue(Attribute attribute) {
		if(attribute==NAME)
			return this.getName() != null && !this.getName().isEmpty();
		else if(attribute==URL)
			return this.getUrl() != null && !this.getUrl().isEmpty();
		else if(attribute==COUNTRY)
			return this.getCountry() != null && !this.getCountry().isEmpty();
		else
			return false;
	}

    public String getMergedAttributeProvenance(Attribute attribute) {
		Collection<String> prov = provenance.get(attribute);

		if (prov != null) {
			return StringUtils.join(prov, "+");
		} else {
			return "";
		}
	}



    
}
