package de.uni_mannheim.informatik.dws.wdi.IR_Team9.Comparators;

import de.uni_mannheim.informatik.dws.wdi.IR_Team9.Preprocessing.StringPreprocessing;
import de.uni_mannheim.informatik.dws.wdi.IR_Team9.model.Company;
import de.uni_mannheim.informatik.dws.winter.matching.rules.comparators.Comparator;
import de.uni_mannheim.informatik.dws.winter.model.Correspondence;
import de.uni_mannheim.informatik.dws.winter.model.Matchable;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;
import de.uni_mannheim.informatik.dws.winter.similarity.string.JaccardOnNGramsSimilarity;


/*
 *For additional information on Similarity Measures in Winter refer to: https://github.com/olehmberg/winter/wiki/SimilarityMeasures 
 */
public class CompanyNameComparatorJaccardNgram implements Comparator<Company,Attribute>{

    private JaccardOnNGramsSimilarity sim;

    public CompanyNameComparatorJaccardNgram(int n){
            //define Similarity measure
            this.sim = new JaccardOnNGramsSimilarity(n);
    }
    

    @Override
    public double compare(Company record1, Company record2, Correspondence<Attribute, Matchable> schemaCorrespondence) {
        // Preprocessing
        String name1 = StringPreprocessing.tokenBasicNormalization(record1.getName(), "", false);
        String name2 = StringPreprocessing.tokenBasicNormalization(record2.getName(), "", false);

        return sim.calculate(name1,name2);
    }
    
}
