package de.uni_mannheim.informatik.dws.wdi.IR_Team9.DataFusion.fusers;

import de.uni_mannheim.informatik.dws.wdi.IR_Team9.model.Company;
import de.uni_mannheim.informatik.dws.winter.datafusion.AttributeValueFuser;
import de.uni_mannheim.informatik.dws.winter.datafusion.conflictresolution.string.ShortestString;
import de.uni_mannheim.informatik.dws.winter.model.FusedValue;
import de.uni_mannheim.informatik.dws.winter.model.RecordGroup;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;
import de.uni_mannheim.informatik.dws.winter.processing.Processable;
import de.uni_mannheim.informatik.dws.winter.model.Correspondence;
import de.uni_mannheim.informatik.dws.winter.model.Matchable;


public class CountryFuser extends
AttributeValueFuser<String, Company, Attribute> {
	public CountryFuser() {
		super(new ShortestString<Company, Attribute>());
		}

		@Override
		public void fuse(RecordGroup<Company, Attribute> group, Company fusedRecord, Processable<Correspondence<Attribute, Matchable>> schemaCorrespondences, Attribute schemaElement) {

			// get the fused value
			FusedValue<String, Company, Attribute> fused = getFusedValue(group, schemaCorrespondences, schemaElement);

			// set the value for the fused record
			fusedRecord.setCountry(fused.getValue());
			fusedRecord.setAttributeProvenance(Company.COUNTRY,fused.getOriginalIds());

		}

		@Override
		public boolean hasValue(Company record, Correspondence<Attribute, Matchable> correspondence) {
				return record.hasValue(Company.COUNTRY);
		}

		@Override
		public String getValue(Company record, Correspondence<Attribute, Matchable> correspondence) {
			return record.getCountry();

		}
	
}


