package de.uni_mannheim.informatik.dws.wdi.IR_Team9.Comparators;

import de.uni_mannheim.informatik.dws.wdi.IR_Team9.model.Company;
import de.uni_mannheim.informatik.dws.winter.model.Correspondence;
import de.uni_mannheim.informatik.dws.winter.model.Matchable;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;

public class LCSComparator extends AbstractT9Comparator{

    private LongestCommonSubsequenceSimilarity.NormalizationFlag normalizationFlag = LongestCommonSubsequenceSimilarity.NormalizationFlag.AVG;
    private LongestCommonSubsequenceSimilarity simMeasure;


    public LCSComparator() {
        this.simMeasure = new LongestCommonSubsequenceSimilarity(this.normalizationFlag);
    }

    public LCSComparator(LongestCommonSubsequenceSimilarity.NormalizationFlag normalizationFlag) {
        this.normalizationFlag = normalizationFlag;
        this.simMeasure = new LongestCommonSubsequenceSimilarity(this.normalizationFlag);
    }

    
    /**
     * 
     * @param normalizationFlag
     * @param boostAndPenalize
     * @param boostThresh
     * @param boostingFactor
     * @param boostFunction
     */
    public LCSComparator(
        LongestCommonSubsequenceSimilarity.NormalizationFlag normalizationFlag,
        float dropToZeroThresh, 
        boolean boostAndPenalize, 
        float boostThresh, 
        BOOST_FUNCTIONS boostFunction,
        float boostingFactor) {

            this.normalizationFlag = normalizationFlag;
            this.simMeasure = new LongestCommonSubsequenceSimilarity(this.normalizationFlag);
            this.dropToZeroThresh = dropToZeroThresh;
            this.boostAndPenalize =boostAndPenalize;
            this.boostThresh = boostThresh;
            this.boostingFactor = boostingFactor;
            this.boostFunction = boostFunction;
    }


    @Override
    public double compare(Company record1, Company record2, Correspondence<Attribute, Matchable> schemaCorrespondence) {

        /* PREPROCESSING */
        // no preprocessing here - done by the similarity class
        
        /* CALCULATION */
        double similarity = simMeasure.calculate(record1.getName(), record2.getName());
        

        /*POSTPROCESSING */
        double ppSimilarity = this.postProcessSim(similarity);
        
        /*DEBUG LOG */
        this.writeLog(record1, record2, similarity, ppSimilarity, record1.getName(), record2.getName());

        return ppSimilarity; //ensure return value is between 1 and 0
    }
    
}
