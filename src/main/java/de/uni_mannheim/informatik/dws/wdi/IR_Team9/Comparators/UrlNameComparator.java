package de.uni_mannheim.informatik.dws.wdi.IR_Team9.Comparators;

import de.uni_mannheim.informatik.dws.wdi.IR_Team9.model.Company;
import de.uni_mannheim.informatik.dws.winter.model.Correspondence;
import de.uni_mannheim.informatik.dws.winter.model.Matchable;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;

public class UrlNameComparator extends CompanyNameComparatorJaccardNgram{

    public UrlNameComparator(int n){
        super(n);

        this.setIncludeURL(true);
        this.setURLWeight(0.3);
    }

    public UrlNameComparator(int n, boolean rmFrequentTokens){
        super(n, rmFrequentTokens);
        this.setIncludeURL(true);
        this.setURLWeight(0.3);
    }


    public UrlNameComparator(int n, boolean rmFrequentTokens, float dropToZeroThresh){
        super(n, rmFrequentTokens,dropToZeroThresh);
        this.setIncludeURL(true);
        this.setURLWeight(0.3);
    }

    public UrlNameComparator(
        int n, 
        boolean rmFrequentTokens, 
        float dropToZeroThresh, 
        boolean boostAndPenalize,
        float boostThresh,
        float boostFactor,
        BOOST_FUNCTIONS boostFunction){

           super(n, rmFrequentTokens, dropToZeroThresh, boostAndPenalize, boostThresh, boostFactor, boostFunction);
           this.setIncludeURL(true);
           this.setURLWeight(0.3);
    }

    @Override
    public double compare(Company record1, Company record2, Correspondence<Attribute, Matchable> schemaCorrespondence) {
        double sim = super.compare(record1, record2, schemaCorrespondence);
        return sim;
    }
    
}
