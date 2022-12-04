
package de.uni_mannheim.informatik.dws.wdi.IR_Team9.DataFusion.fusers;


import de.uni_mannheim.informatik.dws.wdi.IR_Team9.model.Company;
import de.uni_mannheim.informatik.dws.winter.datafusion.AttributeValueFuser;
import de.uni_mannheim.informatik.dws.winter.datafusion.conflictresolution.meta.FavourSources;
import de.uni_mannheim.informatik.dws.winter.model.Correspondence;
import de.uni_mannheim.informatik.dws.winter.model.FusedValue;
import de.uni_mannheim.informatik.dws.winter.model.Matchable;
import de.uni_mannheim.informatik.dws.winter.model.RecordGroup;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;
import de.uni_mannheim.informatik.dws.winter.processing.Processable;


public class SalesCurrencyFuser extends AttributeValueFuser<String, Company, Attribute> {

	public SalesCurrencyFuser() {
		super(new FavourSources<String, Company, Attribute>());
	}

	@Override
	public boolean hasValue(Company record, Correspondence<Attribute, Matchable> correspondence) {
		return record.hasValue(Company.SALES_CURRENCY);
	}

	@Override
	public String getValue(Company record, Correspondence<Attribute, Matchable> correspondence) {
		return record.getSalesCurrency();
	}

	@Override
	public void fuse(RecordGroup<Company, Attribute> group, Company fusedRecord, Processable<Correspondence<Attribute, Matchable>> schemaCorrespondences, Attribute schemaElement) {
		FusedValue<String, Company, Attribute> fused = getFusedValue(group, schemaCorrespondences, schemaElement);
		fusedRecord.setSalesCurrency(fused.getValue());
		fusedRecord.setAttributeProvenance(Company.SALES_CURRENCY, fused.getOriginalIds());
	}


}
