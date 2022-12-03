package de.uni_mannheim.informatik.dws.wdi.IR_Team9.DataFusion.evaluation;

import de.uni_mannheim.informatik.dws.wdi.IR_Team9.Comparators.LongestCommonSubsequenceSimilarity;
import de.uni_mannheim.informatik.dws.wdi.IR_Team9.Comparators.LongestCommonSubsequenceSimilarity.NormalizationFlag;
import de.uni_mannheim.informatik.dws.wdi.IR_Team9.model.Company;
import de.uni_mannheim.informatik.dws.winter.datafusion.EvaluationRule;
import de.uni_mannheim.informatik.dws.winter.model.Correspondence;
import de.uni_mannheim.informatik.dws.winter.model.Matchable;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;
import de.uni_mannheim.informatik.dws.winter.similarity.SimilarityMeasure;
import de.uni_mannheim.informatik.dws.winter.similarity.string.TokenizingJaccardSimilarity;

public class NameEvaluationRule extends EvaluationRule <Company, Attribute> {

	SimilarityMeasure<String> sim = new LongestCommonSubsequenceSimilarity(NormalizationFlag.MIN);

	@Override
	public boolean isEqual(Company record1, Company record2, Attribute schemaElement) {
		return sim.calculate(record1.getName(), record2.getName()) >= 0.75;
	}

	/* (non-Javadoc)
	 * @see de.uni_mannheim.informatik.wdi.datafusion.EvaluationRule#isEqual(java.lang.Object, java.lang.Object, de.uni_mannheim.informatik.wdi.model.Correspondence)
	 */
	@Override
	public boolean isEqual(Company record1, Company record2,
			Correspondence<Attribute, Matchable> schemaCorrespondence) {
		return isEqual(record1, record2, (Attribute)null);
	}
	
}
