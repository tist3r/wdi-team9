package de.uni_mannheim.informatik.dws.wdi.IR_Team9.Comparators;

import de.uni_mannheim.informatik.dws.wdi.IR_Team9.Preprocessing.StringPreprocessing;
import de.uni_mannheim.informatik.dws.wdi.IR_Team9.model.Company;
import de.uni_mannheim.informatik.dws.winter.model.Correspondence;
import de.uni_mannheim.informatik.dws.winter.model.Matchable;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;
import de.uni_mannheim.informatik.dws.winter.similarity.string.LevenshteinSimilarity;

public class CompanyNameComparatorLevenshtein extends AbstractT9Comparator{
    private LevenshteinSimilarity sim = new LevenshteinSimilarity();

    public CompanyNameComparatorLevenshtein(boolean rmFrequentTokens){
        this.rmFrequentTokens = rmFrequentTokens;
    }

    public CompanyNameComparatorLevenshtein(boolean rmFrequentTokens, float postProcessingThresh){
        this.rmFrequentTokens = rmFrequentTokens;
        this.postProcessingThresh = postProcessingThresh;
    }

    @Override
    public double compare(Company record1, Company record2, Correspondence<Attribute, Matchable> schemaCorrespondence) {
        // Preprocessing
        String name1 = record1.getName();
        String name2 = record2.getName();

        if(this.rmFrequentTokens){
            name1 = StringPreprocessing.removeFrequentToken(name1, true);
            name2 = StringPreprocessing.removeFrequentToken(name2, true);
        }

        name1 = StringPreprocessing.tokenBasicNormalization(name1, "", false);
        name2 = StringPreprocessing.tokenBasicNormalization(name2, "", false);


        Double similarity =  sim.calculate(name1,name2);
        Double postProcessedSimilarity = getPostProcessedSim(similarity);

        this.writeLog(record1, record2, similarity, postProcessedSimilarity, name1, name2);

        return postProcessedSimilarity;
    }
}