package de.uni_mannheim.informatik.dws.wdi.IR_Team9.Comparators;

import de.uni_mannheim.informatik.dws.wdi.IR_Team9.Preprocessing.StringPreprocessing;
import de.uni_mannheim.informatik.dws.wdi.IR_Team9.model.Company;
import de.uni_mannheim.informatik.dws.winter.model.Correspondence;
import de.uni_mannheim.informatik.dws.winter.model.Matchable;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;

/**
 * This class caters to the problem of the removed frequent tokens by calculation a token overlap.
 */
public class RogueTokenComparator extends AbstractT9Comparator{

    private RogueTokenSimilarity sim = new RogueTokenSimilarity();

    public RogueTokenComparator() {
    }

    public RogueTokenComparator(float dropToZeroThresh) {
        this.dropToZeroThresh = dropToZeroThresh;
    }

    public RogueTokenComparator(
        float dropToZeroThresh, 
        boolean boostAndPenalize, 
        float boostThresh, 
        BOOST_FUNCTIONS boostFunction, 
        float boostingFactor) {

            this.dropToZeroThresh = dropToZeroThresh;
            this.boostAndPenalize = boostAndPenalize;
            this.boostThresh = boostThresh;
            this.boostFunction = boostFunction;
            this.boostingFactor = boostingFactor;
    }

   
    @Override
    public double compare(Company record1, Company record2, Correspondence<Attribute, Matchable> schemaCorrespondence) {

        /* PREPROCESSING */
        //Basic Preprocessing: removing punctuation and lowercasing
		String name1 = StringPreprocessing.removePunctuation(record1.getName()).toLowerCase();
        String name2 = StringPreprocessing.removePunctuation(record2.getName()).toLowerCase();



        /* CALCULATION */
        Double similarity = sim.calculate(name1, name2);


        /*POSTPROCESSING */

        Double postProcessedSimilarity = this.postProcessSim(similarity);
        
        /* DEBUG LOG */
		this.writeLog(record1, record2, similarity, postProcessedSimilarity, name1, name2);

        return postProcessedSimilarity; //ensure return value is between 1 and 0
    }


   public static void main(String[] args) {
        String s1 = "Kobe Steel";
        String s2 = "Kobe Financial limited";

        RogueTokenSimilarity sim = new RogueTokenSimilarity();

        System.out.println(sim.calculate(s1, s2));
   }



    
}
