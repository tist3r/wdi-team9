package de.uni_mannheim.informatik.dws.wdi.IR_Team9.Comparators;

import de.uni_mannheim.informatik.dws.wdi.IR_Team9.model.Company;
import de.uni_mannheim.informatik.dws.winter.model.Correspondence;
import de.uni_mannheim.informatik.dws.winter.model.Matchable;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;

public class LCSComparator extends AbstractT9Comparator{

    private LongestCommonSubsequenceSimilarity.NormalizationFlag normalizationFlag = LongestCommonSubsequenceSimilarity.NormalizationFlag.AVG;
    private boolean boostAndPenalize = false;
    private LongestCommonSubsequenceSimilarity simMeasure;


    public LCSComparator() {
        this.simMeasure = new LongestCommonSubsequenceSimilarity(this.normalizationFlag);
    }

    public LCSComparator(LongestCommonSubsequenceSimilarity.NormalizationFlag normalizationFlag) {
        this.normalizationFlag = normalizationFlag;
        this.simMeasure = new LongestCommonSubsequenceSimilarity(this.normalizationFlag);
    }

    public LCSComparator(LongestCommonSubsequenceSimilarity.NormalizationFlag normalizationFlag, boolean boostAndPenalize) {
        this.normalizationFlag = normalizationFlag;
        this.simMeasure = new LongestCommonSubsequenceSimilarity(this.normalizationFlag);
    }


    @Override
    public double compare(Company record1, Company record2, Correspondence<Attribute, Matchable> schemaCorrespondence) {
        double similarity = simMeasure.calculate(record1.getName(), record2.getName());
        
        double ppSimilarity = boostOrPenalize(similarity);

        this.writeLog(record1, record2, similarity, ppSimilarity, record1.getName(), record2.getName());

        return Math.min(ppSimilarity,1);
    }


    private double boostOrPenalize(double sim){
        if(boostAndPenalize && sim > 0.6){
            return sim + Math.sqrt(sim)/10;
        }else if (boostAndPenalize && sim < 0.5){
            return sim - Math.sqrt(sim)/10;
        }

        return sim;
    }
    
}
