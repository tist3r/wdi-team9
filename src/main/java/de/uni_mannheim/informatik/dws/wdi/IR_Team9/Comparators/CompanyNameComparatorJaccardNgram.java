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
        this.postProcessingThresh = 0;
    }

    /**
     * Constructor with the option to specify a post processing threshold after which the similarity is reduced to zero.
     * This is especially helpful for classifiers, in order to not distort their learned matching rules.
     * @param n n-gram length
     * @param rmFrequentTokens if frequent tokens should be removed during pre-processing
     * @param postProcessingThresh
     */
    public CompanyNameComparatorJaccardNgram(int n, boolean rmFrequentTokens, float postProcessingThresh){
        this.simMeasure = new JaccardOnNGramsSimilarity(n);
        this.rmFrequentTokens = rmFrequentTokens;
        this.postProcessingThresh = postProcessingThresh;
    }
    

    @Override
    public double compare(Company record1, Company record2, Correspondence<Attribute, Matchable> schemaCorrespondence) {
        String name1 = record1.getName();
        String name2 = record2.getName();
        //preprocessing
        if(this.rmFrequentTokens){
            name1 = StringPreprocessing.removeFrequentTokens(name1, true);
            name2 = StringPreprocessing.removeFrequentTokens(name2, true);
        }

        name1 = StringPreprocessing.tokenBasicNormalization(name1, "", false);
        name2 = StringPreprocessing.tokenBasicNormalization(name2, "", false);

        //System.out.println(name1 + " " + name2);

        Double similarity =  simMeasure.calculate(name1,name2);

        Double postProcessedSimilarity = this.getPostProcessedSim(similarity);

        this.writeLog(record1, record2, similarity, postProcessedSimilarity, name1, name2);

        return postProcessedSimilarity;
    }    
}
