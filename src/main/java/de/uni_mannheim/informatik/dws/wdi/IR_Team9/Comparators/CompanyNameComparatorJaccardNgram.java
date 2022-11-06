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

    public CompanyNameComparatorJaccardNgram(int n){
            //define Similarity measure
            this.sim = new JaccardOnNGramsSimilarity(n);
    }
    

    @Override
    public double compare(Company record1, Company record2, Correspondence<Attribute, Matchable> schemaCorrespondence) {
        // Preprocessing
        String name1 = StringPreprocessing.tokenBasicNormalization(record1.getName(), "", false);
        String name2 = StringPreprocessing.tokenBasicNormalization(record2.getName(), "", false);


        Double similarity =  sim.calculate(name1,name2);
        
        //TODO: implement Logger

        if(this.comparisonLog != null){
			this.comparisonLog.setComparatorName(getClass().getName());
		
			this.comparisonLog.setRecord1Value(record1.getName().toString());
			this.comparisonLog.setRecord2Value(record2.getName().toString());
    	
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