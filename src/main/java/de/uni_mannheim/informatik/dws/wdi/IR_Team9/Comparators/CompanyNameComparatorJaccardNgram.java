package de.uni_mannheim.informatik.dws.wdi.IR_Team9.Comparators;

import de.uni_mannheim.informatik.dws.wdi.IR_Team9.Preprocessing.StringPreprocessing;
import de.uni_mannheim.informatik.dws.wdi.IR_Team9.model.Company;
import de.uni_mannheim.informatik.dws.winter.matching.rules.comparators.Comparator;
import de.uni_mannheim.informatik.dws.winter.matching.rules.comparators.ComparatorLogger;
import de.uni_mannheim.informatik.dws.winter.model.Correspondence;
import de.uni_mannheim.informatik.dws.winter.model.Matchable;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;
import de.uni_mannheim.informatik.dws.winter.similarity.string.JaccardOnNGramsSimilarity;


/*
 *For additional information on Similarity Measures in Winter refer to: https://github.com/olehmberg/winter/wiki/SimilarityMeasures 
 */
public class CompanyNameComparatorJaccardNgram implements Comparator<Company,Attribute>{

    private JaccardOnNGramsSimilarity sim;
    private ComparatorLogger comparisonLog;
    private boolean rmFrequentTokens;

    public CompanyNameComparatorJaccardNgram(int n){
            //define Similarity measure
            this.sim = new JaccardOnNGramsSimilarity(n);
            this.rmFrequentTokens = false;
    }

    public CompanyNameComparatorJaccardNgram(int n, boolean rmFrequentTokens){
        this.sim = new JaccardOnNGramsSimilarity(n);
        this.rmFrequentTokens = rmFrequentTokens;
        System.out.println("[INFO ] Removing frequent tokens from JaccardNgramComparator");
    }
    

    @Override
    public double compare(Company record1, Company record2, Correspondence<Attribute, Matchable> schemaCorrespondence) {
        String name1 = record1.getName();
        String name2 = record2.getName();
        //preprocessing
        if(this.rmFrequentTokens){
            name1 = StringPreprocessing.removeFrequentToken(name1, true);
            name2 = StringPreprocessing.removeFrequentToken(name2, true);
        }

        name1 = StringPreprocessing.tokenBasicNormalization(name1, "", false);
        name2 = StringPreprocessing.tokenBasicNormalization(name2, "", false);

        //System.out.println(name1 + " " + name2);


        Double similarity =  sim.calculate(name1,name2);
        
        //TODO: implement Logger

        if(this.comparisonLog != null){
			this.comparisonLog.setComparatorName(getClass().getName());
		
			this.comparisonLog.setRecord1Value(record1.getName());
			this.comparisonLog.setRecord2Value(record2.getName());

            this.comparisonLog.setRecord1PreprocessedValue(name1);
            this.comparisonLog.setRecord2PreprocessedValue(name2);
    	
			this.comparisonLog.setSimilarity(Double.toString(similarity));
		}
        return similarity;
    }

    @Override
	public ComparatorLogger getComparisonLog() {
		return this.comparisonLog;
	}

	@Override
	public void setComparisonLog(ComparatorLogger comparatorLog) {
		this.comparisonLog = comparatorLog;
	}

    
}
