package de.uni_mannheim.informatik.dws.wdi.IR_Team9.Comparators;

import de.uni_mannheim.informatik.dws.wdi.IR_Team9.Preprocessing.StringPreprocessing;
import de.uni_mannheim.informatik.dws.wdi.IR_Team9.model.Company;
import de.uni_mannheim.informatik.dws.winter.model.Correspondence;
import de.uni_mannheim.informatik.dws.winter.model.Matchable;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;
import de.uni_mannheim.informatik.dws.winter.similarity.string.JaccardOnNGramsSimilarity;


/*
 *For additional information on Similarity Measures in Winter refer to: https://github.com/olehmberg/winter/wiki/SimilarityMeasures 
 */
public class CompanyNameComparatorJaccardNgram extends AbstractT9Comparator{

    private JaccardOnNGramsSimilarity simMeasure;

    public CompanyNameComparatorJaccardNgram(int n){
            //define Similarity measure
            this.simMeasure = new JaccardOnNGramsSimilarity(n);
            this.rmFrequentTokens = false;
    }

    public CompanyNameComparatorJaccardNgram(int n, boolean rmFrequentTokens){
        this.simMeasure = new JaccardOnNGramsSimilarity(n);
        this.rmFrequentTokens = rmFrequentTokens;
    }


    public CompanyNameComparatorJaccardNgram(int n, boolean rmFrequentTokens, float dropToZeroThresh){
        this.simMeasure = new JaccardOnNGramsSimilarity(n);
        this.rmFrequentTokens = rmFrequentTokens;
        this.dropToZeroThresh = dropToZeroThresh;
    }

    public CompanyNameComparatorJaccardNgram(
        int n, 
        boolean rmFrequentTokens, 
        float dropToZeroThresh, 
        boolean boostAndPenalize,
        float boostThresh,
        float boostFactor,
        BOOST_FUNCTIONS boostFunction){

            this.simMeasure = new JaccardOnNGramsSimilarity(n);
            this.rmFrequentTokens = rmFrequentTokens;
            this.dropToZeroThresh = dropToZeroThresh;
            this.boostAndPenalize = boostAndPenalize;
            this.boostThresh = boostThresh;
            this.boostFunction = boostFunction;
            this.boostingFactor = boostFactor;
    }
    

    @Override
    public double compare(Company record1, Company record2, Correspondence<Attribute, Matchable> schemaCorrespondence) {


        /* PREPROCESSING */
        String name1 = record1.getName();
        String name2 = record2.getName();

        if(this.rmFrequentTokens){
            name1 = StringPreprocessing.removeFrequentTokens(name1, true);
            name2 = StringPreprocessing.removeFrequentTokens(name2, true);
        }

        name1 = StringPreprocessing.tokenBasicNormalization(name1, "", false);
        name2 = StringPreprocessing.tokenBasicNormalization(name2, "", false);


        /* CALCULATION */

        Double similarity =  simMeasure.calculate(name1,name2);

        /* POSTPROCESSING */

        Double postProcessedSimilarity = this.postProcessSim(similarity);

        /* DEBUG LOG */
        this.writeLog(record1, record2, similarity, postProcessedSimilarity, name1, name2);

        return postProcessedSimilarity;
    }    
}
