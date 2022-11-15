package de.uni_mannheim.informatik.dws.wdi.IR_Team9.Comparators;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import de.uni_mannheim.informatik.dws.wdi.IR_Team9.Preprocessing.StringPreprocessing;
import de.uni_mannheim.informatik.dws.wdi.IR_Team9.model.Company;
import de.uni_mannheim.informatik.dws.winter.model.Correspondence;
import de.uni_mannheim.informatik.dws.winter.model.Matchable;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;

public class RogueTokenComparator extends AbstractT9Comparator{

    private RogueTokenSimilarity sim = new RogueTokenSimilarity();


    public RogueTokenComparator() {
    }

    public RogueTokenComparator(float postProcessThresh) {
        this.postProcessingThresh = postProcessThresh;
    }
   
    @Override
    public double compare(Company record1, Company record2, Correspondence<Attribute, Matchable> schemaCorrespondence) {

        //Basic Preprocessing: removing punctuation and lowercasing
		String name1 = StringPreprocessing.removePunctuation(record1.getName()).toLowerCase();
        String name2 = StringPreprocessing.removePunctuation(record2.getName()).toLowerCase();

        Double similarity = sim.calculate(name1, name2);

		Double postProcessedSimilarity = this.getPostProcessedSim(similarity);

		this.writeLog(record1, record2, similarity, postProcessedSimilarity, name1, name2);

        return postProcessedSimilarity;
    }


   public static void main(String[] args) {
        String s1 = "Shell";
        String s2 = "Royal Dutch Shell";

        RogueTokenSimilarity sim = new RogueTokenSimilarity();

        System.out.println(sim.calculate(s1, s2));
   }



    
}
