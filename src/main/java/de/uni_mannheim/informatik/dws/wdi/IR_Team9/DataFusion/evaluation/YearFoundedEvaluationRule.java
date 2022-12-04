package de.uni_mannheim.informatik.dws.wdi.IR_Team9.DataFusion.evaluation;

import de.uni_mannheim.informatik.dws.wdi.IR_Team9.model.Company;
import de.uni_mannheim.informatik.dws.winter.datafusion.EvaluationRule;
import de.uni_mannheim.informatik.dws.winter.model.Correspondence;
import de.uni_mannheim.informatik.dws.winter.model.Matchable;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;

public class YearFoundedEvaluationRule extends EvaluationRule <Company, Attribute> {

	@Override
	public boolean isEqual(Company record1, Company record2, Attribute schemaElement) {
		
		if(record1.getYearFounded() != null && record2.getYearFounded() != null){
			double lowerBound = record1.getYearFounded()*0.95;
			double upperBound = record1.getYearFounded()*1.05;
	
	
			return lowerBound <= record2.getYearFounded() && record2.getYearFounded() < upperBound;
		}

		return false;

	}


	@Override
	public boolean isEqual(Company record1, Company record2,
			Correspondence<Attribute, Matchable> schemaCorrespondence) {
		return isEqual(record1, record2, (Attribute)null);
	}
	
}
