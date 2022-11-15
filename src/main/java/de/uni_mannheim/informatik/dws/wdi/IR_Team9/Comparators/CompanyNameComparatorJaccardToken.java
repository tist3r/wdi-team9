package de.uni_mannheim.informatik.dws.wdi.IR_Team9.Comparators;

import de.uni_mannheim.informatik.dws.wdi.IR_Team9.Preprocessing.StringPreprocessing;
import de.uni_mannheim.informatik.dws.wdi.IR_Team9.model.Company;
import de.uni_mannheim.informatik.dws.winter.model.Correspondence;
import de.uni_mannheim.informatik.dws.winter.model.Matchable;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;
import de.uni_mannheim.informatik.dws.winter.similarity.string.TokenizingJaccardSimilarity;

/*
 *For additional information on Similarity Measures in Winter refer to: https://github.com/olehmberg/winter/wiki/SimilarityMeasures 
 */
public class CompanyNameComparatorJaccardToken extends AbstractT9Comparator{

    //define Similarity measure
    private TokenizingJaccardSimilarity sim = new TokenizingJaccardSimilarity();

    public CompanyNameComparatorJaccardToken(float postProcessingThresh, boolean rmFrequentTokens) {
		this.postProcessingThresh = postProcessingThresh;
		this.rmFrequentTokens = rmFrequentTokens;
	}

	public CompanyNameComparatorJaccardToken() {
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

        // Further preprocessing is done by the TokenizingJAccardSImilarity Class
        Double similarity = sim.calculate(name1, name2);

		Double postProcessedSimilarity = this.getPostProcessedSim(similarity);

		this.writeLog(record1, record2, similarity, postProcessedSimilarity, name1, name2);

        return postProcessedSimilarity;
    }
    
    
    /*Some names have brackets with some value in it (uk) or (company). in these cases the similarity can be postprocessed */
    public static void main(String[] args) {
        //"Covanta","covanta corp"

        TokenizingJaccardSimilarity comp = new TokenizingJaccardSimilarity();
        System.out.println(comp.calculate("Shell","Royal Dutch Shell"));
    }
}
