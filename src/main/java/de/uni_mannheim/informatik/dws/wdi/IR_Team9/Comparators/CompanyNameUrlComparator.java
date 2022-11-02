package de.uni_mannheim.informatik.dws.wdi.IR_Team9.Comparators;

import de.uni_mannheim.informatik.dws.wdi.IR_Team9.model.Company;
import de.uni_mannheim.informatik.dws.winter.matching.rules.comparators.Comparator;
import de.uni_mannheim.informatik.dws.winter.model.Correspondence;
import de.uni_mannheim.informatik.dws.winter.model.Matchable;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;
import de.uni_mannheim.informatik.dws.winter.similarity.string.LevenshteinSimilarity;
import de.uni_mannheim.informatik.dws.wdi.IR_Team9.Preprocessing.StringPreprocessing;

public class CompanyNameUrlComparator implements Comparator<Company,Attribute>{

    //define Similarity measure
    private LevenshteinSimilarity sim = new LevenshteinSimilarity();
    private double nameWeight;
    private double urlWeight;
    private boolean normalizeName;
    private boolean normalizeURL;

    public CompanyNameUrlComparator(double nameWeight, double urlWeight, boolean normalizeName, boolean normalizeURL){
        this.nameWeight = nameWeight;
        this.urlWeight = urlWeight;
        this.normalizeName = normalizeName;
        this.normalizeURL = normalizeURL;
    }

    @Override
    public double compare(Company record1, Company record2, Correspondence<Attribute, Matchable> schemaCorrespondence) {
        String name1 = normalizeName ? StringPreprocessing.tokenBasicNormalization(record1.getName(), "", false) : record1.getName();
        String name2 = normalizeName ? StringPreprocessing.tokenBasicNormalization(record2.getName(), "", false) : record2.getName();

        String url1 = normalizeURL ? StringPreprocessing.normalizeURL(record1.getUrl()) : record1.getUrl();
        String url2 = normalizeURL ? StringPreprocessing.normalizeURL(record2.getUrl()) : record2.getUrl();

        Double simName = sim.calculate(name1, name2);

        if (url1 == null || url2 == null){
            return simName;
        }

        

        Double simURL = sim.calculate(url1, url2);

        return nameWeight * simName + urlWeight * simURL;
    }
    
}
