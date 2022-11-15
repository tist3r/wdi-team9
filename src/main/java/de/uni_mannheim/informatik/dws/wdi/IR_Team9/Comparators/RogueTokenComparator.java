package de.uni_mannheim.informatik.dws.wdi.IR_Team9.Comparators;

import de.uni_mannheim.informatik.dws.wdi.IR_Team9.Preprocessing.StringPreprocessing;
import de.uni_mannheim.informatik.dws.wdi.IR_Team9.model.Company;
import de.uni_mannheim.informatik.dws.winter.model.Correspondence;
import de.uni_mannheim.informatik.dws.winter.model.Matchable;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;

public class RogueTokenComparator extends AbstractT9Comparator{

    private RogueTokenSimilarity sim = new RogueTokenSimilarity();
    private boolean boost = false;
    private float boostThresh = 1;


    public RogueTokenComparator() {
    }

    public RogueTokenComparator(float postProcessThresh) {
        this.postProcessingThresh = postProcessThresh;
    }

    public RogueTokenComparator(float postProcessThresh, boolean boost, float boostThresh) {
        this.postProcessingThresh = postProcessThresh;
        this.boost = boost;
        this.boostThresh = boostThresh;
    }
   
    @Override
    public double compare(Company record1, Company record2, Correspondence<Attribute, Matchable> schemaCorrespondence) {

        //Basic Preprocessing: removing punctuation and lowercasing
		String name1 = StringPreprocessing.removePunctuation(record1.getName()).toLowerCase();
        String name2 = StringPreprocessing.removePunctuation(record2.getName()).toLowerCase();

        Double similarity = sim.calculate(name1, name2);

		Double postProcessedSimilarity = this.boost(similarity);
        postProcessedSimilarity = this.getPostProcessedSim(similarity);

		this.writeLog(record1, record2, similarity, postProcessedSimilarity, name1, name2);

        return Math.min(postProcessedSimilarity,1);
    }

    private double boost(double sim){
        if(this.boost && sim >= this.boostThresh){
            return sim + Math.sqrt(sim)/10;
        }

        return sim;
    }


   public static void main(String[] args) {
        String s1 = "Shell";
        String s2 = "Royal Dutch Shell";

        RogueTokenSimilarity sim = new RogueTokenSimilarity();

        System.out.println(sim.calculate(s1, s2));
   }



    
}
