
package de.uni_mannheim.informatik.dws.wdi.IR_Team9.DataFusion.fusers;


import de.uni_mannheim.informatik.dws.wdi.IR_Team9.model.Company;
import de.uni_mannheim.informatik.dws.winter.datafusion.AttributeValueFuser;
import de.uni_mannheim.informatik.dws.winter.datafusion.conflictresolution.numeric.Median;
import de.uni_mannheim.informatik.dws.winter.model.Correspondence;
import de.uni_mannheim.informatik.dws.winter.model.FusedValue;
import de.uni_mannheim.informatik.dws.winter.model.Matchable;
import de.uni_mannheim.informatik.dws.winter.model.RecordGroup;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;
import de.uni_mannheim.informatik.dws.winter.processing.Processable;

public class YearFoundedFuser extends
		AttributeValueFuser<Double,Company, Attribute> {

	public YearFoundedFuser() {
		super(new Median<Company, Attribute>());
	}

	@Override
	public void fuse(RecordGroup<Company, Attribute> group, Company fusedRecord, Processable<Correspondence<Attribute, Matchable>> schemaCorrespondences, Attribute schemaElement) {

		// get the fused value
		FusedValue<Double,Company, Attribute> fused = getFusedValue(group, schemaCorrespondences, schemaElement);

		// System.out.println("Trying to evaluate year founded");
		// for(Company c : group.getRecords()){
		// 	System.out.println(c.getYearFounded() != null ? c.getYearFounded() : "null");
		// }

		if(fused.getValue() != null){
			// set the value for the fused record
			fusedRecord.setYearFounded(fused.getValue());
		}


		// add provenance info
		fusedRecord.setAttributeProvenance(Company.YEAR_FOUNDED, fused.getOriginalIds());
	}

	@Override
	public boolean hasValue(Company record, Correspondence<Attribute, Matchable> correspondence) {
		return record.hasValue(Company.YEAR_FOUNDED);
	}

	@Override
	public Double getValue(Company record, Correspondence<Attribute, Matchable> correspondence) {
		return record.getYearFounded();
	}


}
