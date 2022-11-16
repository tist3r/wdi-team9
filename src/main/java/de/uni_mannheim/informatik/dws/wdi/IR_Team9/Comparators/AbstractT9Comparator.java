package de.uni_mannheim.informatik.dws.wdi.IR_Team9.Comparators;

import de.uni_mannheim.informatik.dws.wdi.IR_Team9.model.Company;
import de.uni_mannheim.informatik.dws.winter.matching.rules.comparators.Comparator;
import de.uni_mannheim.informatik.dws.winter.matching.rules.comparators.ComparatorLogger;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;

/**
 * Class that implements common Comparator logic.
 * @author Thomas
 */
public abstract class AbstractT9Comparator implements Comparator<Company, Attribute> {

    //preprocessing flags
    boolean rmFrequentTokens = false;

    //postprocessing flags
    float dropToZeroThresh = 0;
    float boostThresh = 1;
    boolean boostAndPenalize = false;
    float boostingFactor = 2;
    BOOST_FUNCTIONS boostFunction = BOOST_FUNCTIONS.X3;

    //debug
    ComparatorLogger comparisonLog;


    //static
    public enum BOOST_FUNCTIONS {SQRT, EXP, X3};

    /**
     * Writes the comparison log for the comparator
     * @param record1
     * @param record2
     * @param similarity calculated similarity of the comparator
     * @param postProcessedSimilarity post processed similarity
     * @param r1pp preprocessed record name
     * @param r2pp preprocessed record name
     */
    void writeLog(Company record1, Company record2, double similarity, double postProcessedSimilarity, String r1pp, String r2pp){
        if(this.comparisonLog != null){
            String className = getClass().getName();
            String comparatorName = className.substring(className.lastIndexOf('.') + 1);

			this.comparisonLog.setComparatorName(String.format("Comparator: %s - rmFreqTok %s - ppThresh - %f",comparatorName, this.rmFrequentTokens, this.dropToZeroThresh));
		
			this.comparisonLog.setRecord1Value(record1.getName());
			this.comparisonLog.setRecord2Value(record2.getName());

            this.comparisonLog.setRecord1PreprocessedValue(r1pp);
            this.comparisonLog.setRecord2PreprocessedValue(r2pp);
    	
			this.comparisonLog.setSimilarity(Double.toString(similarity));
            this.comparisonLog.setPostprocessedSimilarity(Double.toString(postProcessedSimilarity));
		}
    }
    
    /**
     * Returns the postprocessed similarity - in this case 0 below a preset threshold.
     * @param sim the original similarity.
     * @return
     */
    double keepOrDropToZero(double sim){
        if(sim < this.dropToZeroThresh){
            return 0d;
        }
            
        return sim;
    }
    
    public double postProcessSim(double sim){
        if(this.boostAndPenalize){
            sim = this.boost(sim);
        }

        if(sim < this.dropToZeroThresh){
            sim = 0;
        }

        return Math.max(Math.min(sim,1),0); //keep similarity between 1 and 0
    }

    public double boost(double sim){
        switch(this.boostFunction){
            case EXP: return boostExp(sim, this.boostThresh, this.boostingFactor);
            case SQRT: return boostSqrt(sim, this.boostThresh, this.boostingFactor);
            case X3: return boostX3(sim, this.boostThresh, this.boostingFactor);

            default: return boostX3(sim, this.boostThresh, this.boostingFactor);
        }
    }
    
    public void setBoostingFactor(float boostingFactor){
        this.boostingFactor = boostingFactor;
    }

    public static double boostExp(double sim, double boostThresh, float boostingFactor){
        return sim + (Math.pow(2, sim - boostThresh)-1)/10*boostingFactor;
    }

    public static double boostSqrt(double sim, double boostThresh, float boostingFactor){
        return sim + (Math.sqrt(sim) - Math.sqrt(boostThresh))/5*boostingFactor;
    }
    
    public static double boostX3(double sim, double boostThresh, float boostingFactor){
        return sim + Math.pow(sim - boostThresh, 3)/2*boostingFactor;
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
