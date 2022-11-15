package de.uni_mannheim.informatik.dws.wdi.IR_Team9.Comparators;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import de.uni_mannheim.informatik.dws.winter.similarity.SimilarityMeasure;

public class RogueTokenSimilarity extends SimilarityMeasure<String>{
     /* In Anlehnung an - ROUGE: A Package for Automatic Evaluation of Summaries Chin-Yew Lin */


     @Override
     public double calculate(java.lang.String first, java.lang.String second) {
         //tokenize
         String[] t1 = first.split("\\s");
         String[] t2 = second.split("\\s");


        Set<String> s1 = new HashSet<>(Arrays.asList(t1));
        Set<String> s2 = new HashSet<>(Arrays.asList(t2));

        //get intersection
        Set<String> intersectSet = new HashSet<>(s1);
        intersectSet.retainAll(s2);

        double p = (intersectSet.size()*1d)/s1.size();
        double r = (intersectSet.size()*1d)/s2.size();

        return 0.5 * p + 0.5 * r;
     }


     

}
