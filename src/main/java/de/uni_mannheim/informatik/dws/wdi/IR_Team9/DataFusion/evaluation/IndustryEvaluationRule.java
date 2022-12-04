package de.uni_mannheim.informatik.dws.wdi.IR_Team9.DataFusion.evaluation;

import java.util.List;

import de.uni_mannheim.informatik.dws.wdi.IR_Team9.model.Company;
import de.uni_mannheim.informatik.dws.winter.datafusion.EvaluationRule;
import de.uni_mannheim.informatik.dws.winter.model.Correspondence;
import de.uni_mannheim.informatik.dws.winter.model.Matchable;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;
import de.uni_mannheim.informatik.dws.winter.similarity.SimilarityMeasure;
import de.uni_mannheim.informatik.dws.winter.similarity.string.LevenshteinSimilarity;

public class IndustryEvaluationRule extends EvaluationRule <Company, Attribute> {

	SimilarityMeasure<String> sim = new LevenshteinSimilarity();

	@Override
	public boolean isEqual(Company record1, Company record2, Attribute schemaElement) {
		List<String> industries1 = record1.getIndustries();
		List<String> industries2 = record2.getIndustries();

		for(String i : industries1){
			for(String j:industries2){
				double similarity = sim.calculate(i,j);
				if(similarity >= 0.9){
					return true;
				}
		}}

		return false;
	}

	@Override
	public boolean isEqual(Company record1, Company record2,
			Correspondence<Attribute, Matchable> schemaCorrespondence) {
		return isEqual(record1, record2, (Attribute)null);
	}
	
}
