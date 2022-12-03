package de.uni_mannheim.informatik.dws.wdi.IR_Team9.DataFusion.evaluation;

import de.uni_mannheim.informatik.dws.wdi.IR_Team9.model.Company;
import de.uni_mannheim.informatik.dws.winter.datafusion.EvaluationRule;
import de.uni_mannheim.informatik.dws.winter.model.Correspondence;
import de.uni_mannheim.informatik.dws.winter.model.Matchable;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;
import de.uni_mannheim.informatik.dws.winter.similarity.SimilarityMeasure;
import de.uni_mannheim.informatik.dws.winter.similarity.string.TokenizingJaccardSimilarity;

public class SalesAmountEvaluationRule extends EvaluationRule <Company, Attribute> {
	
	@Override
	public boolean isEqual(Company record1, Company record2, Attribute schemaElement) {

		if(record1.getSalesAmount() != null && record2.getSalesAmount() != null){
			double lowerBound = record1.getSalesAmount()*0.95;
			double upperBound = record1.getSalesAmount()*1.05;


			return lowerBound <= record2.getSalesAmount() && record2.getSalesAmount() < upperBound;

		}

		return false;
	}

	@Override
	public boolean isEqual(Company record1, Company record2,
			Correspondence<Attribute, Matchable> schemaCorrespondence) {
		return false;
	}

}
