package de.uni_mannheim.informatik.dws.wdi.IR_Team9.Comparators;

import de.uni_mannheim.informatik.dws.wdi.IR_Team9.model.Company;
import de.uni_mannheim.informatik.dws.winter.matching.rules.comparators.Comparator;
import de.uni_mannheim.informatik.dws.winter.matching.rules.comparators.ComparatorLogger;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;

public abstract class AbstractT9Comparator implements Comparator<Company, Attribute> {

    boolean rmFrequentTokens = false;
    float postProcessingThresh = 0;
    ComparatorLogger comparisonLog;


    double getPostProcessedSim(double sim){
        if(sim < this.postProcessingThresh){
            return 0d;
        }
            
        return sim;
    }

    void writeLog(Company record1, Company record2, double similarity, double postProcessedSimilarity, String r1pp, String r2pp){
        if(this.comparisonLog != null){
            String className = getClass().getName();
            String comparatorName = className.substring(className.lastIndexOf('.') + 1);

			this.comparisonLog.setComparatorName(String.format("Comparator: %s - rmFreqTok %s - ppThresh - %f",comparatorName, this.rmFrequentTokens, this.postProcessingThresh));
		
			this.comparisonLog.setRecord1Value(record1.getName());
			this.comparisonLog.setRecord2Value(record2.getName());

            this.comparisonLog.setRecord1PreprocessedValue(r1pp);
            this.comparisonLog.setRecord2PreprocessedValue(r2pp);
    	
			this.comparisonLog.setSimilarity(Double.toString(similarity));
            this.comparisonLog.setPostprocessedSimilarity(Double.toString(postProcessedSimilarity));
		}
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
