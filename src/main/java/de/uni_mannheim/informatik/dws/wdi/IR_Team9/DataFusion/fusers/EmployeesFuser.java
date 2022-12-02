
package de.uni_mannheim.informatik.dws.wdi.IR_Team9.DataFusion.fusers;

import de.uni_mannheim.informatik.dws.wdi.IR_Team9.model.Company;
import de.uni_mannheim.informatik.dws.winter.datafusion.AttributeValueFuser;
import de.uni_mannheim.informatik.dws.winter.datafusion.conflictresolution.numeric.Average;
import de.uni_mannheim.informatik.dws.winter.model.Correspondence;
import de.uni_mannheim.informatik.dws.winter.model.FusedValue;
import de.uni_mannheim.informatik.dws.winter.model.Matchable;
import de.uni_mannheim.informatik.dws.winter.model.RecordGroup;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;
import de.uni_mannheim.informatik.dws.winter.processing.Processable;

public class EmployeesFuser extends
		AttributeValueFuser<Double,Company, Attribute> {

	public EmployeesFuser() {
		super(new Average<Company, Attribute>());
	}

	@Override
	public void fuse(RecordGroup<Company, Attribute> group, Company fusedRecord, Processable<Correspondence<Attribute, Matchable>> schemaCorrespondences, Attribute schemaElement) {

		// get the fused value
		FusedValue<Double,Company, Attribute> fused = getFusedValue(group, schemaCorrespondences, schemaElement);

		// set the value for the fused record
		fusedRecord.setCurrEmpEst(fused.getValue());

		// add provenance info
		fusedRecord.setAttributeProvenance(Company.CURRENT_EMPLOYEES, fused.getOriginalIds());
	}

	@Override
	public boolean hasValue(Company record, Correspondence<Attribute, Matchable> correspondence) {
		return record.hasValue(Company.CURRENT_EMPLOYEES);
	}

	@Override
	public Double getValue(Company record, Correspondence<Attribute, Matchable> correspondence) {
		return record.getCurrEmpEst();
	}


}
