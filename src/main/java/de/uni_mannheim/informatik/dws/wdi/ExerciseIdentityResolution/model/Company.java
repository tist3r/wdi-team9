package de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.model;

import java.util.List;

import de.uni_mannheim.informatik.dws.winter.model.Matchable;

public class Company implements Matchable{

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

    protected String id;
	protected String provenance;


    //TODO: add all attributes
    private String name;
    private String url;
    private List<String> industries;
    private Integer yearFounded;
    private Integer currEmpEst;
    private Integer totalEmpEst;
    private Integer salesAmount;
    private String salesCurrency;
    private Integer profitAmount;
    private String profitCurrency;
    private Integer asset_Aount;
    private String assetCurrency;
    private Integer mvAmount;
    private String mvCurrency;
    private List<String> ceos;


    //Constructor
    public Company(String identifier, String provenance) {
		id = identifier;
		this.provenance = provenance;
	}



    //Getters and Setters

	@Override
	public String getIdentifier() {
		return id;
	}

	@Override
	public String getProvenance() {
		return provenance;
	}

    public String getName() {
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

    public Integer getYearFounded() {
        return this.yearFounded;
    }

    public void setYearFounded(Integer yearFounded) {
        this.yearFounded = yearFounded;
    }

    public Integer getCurrEmpEst() {
        return this.currEmpEst;
    }

    public void setCurrEmpEst(Integer currEmpEst) {
        this.currEmpEst = currEmpEst;
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

    public Integer getAsset_Aount() {
        return this.asset_Aount;
    }

    public void setAsset_Aount(Integer asset_Aount) {
        this.asset_Aount = asset_Aount;
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

    
}
