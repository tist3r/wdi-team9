package de.uni_mannheim.informatik.dws.wdi.IR_Team9.Blocking;

import de.uni_mannheim.informatik.dws.wdi.IR_Team9.Preprocessing.StringPreprocessing;
import de.uni_mannheim.informatik.dws.wdi.IR_Team9.model.Company;
import de.uni_mannheim.informatik.dws.winter.matching.blockers.generators.RecordBlockingKeyGenerator;
import de.uni_mannheim.informatik.dws.winter.model.Correspondence;
import de.uni_mannheim.informatik.dws.winter.model.Matchable;
import de.uni_mannheim.informatik.dws.winter.model.Pair;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;
import de.uni_mannheim.informatik.dws.winter.processing.DataIterator;
import de.uni_mannheim.informatik.dws.winter.processing.Processable;

public class CompanyBlockingKeyByNameGenerator extends RecordBlockingKeyGenerator<Company, Attribute>{
    
    private static final long serialVersionUID = 1L;
	/*
     * Blocking based on first letter of company name.     * 
     */
	@Override
	public void generateBlockingKeys(Company record, Processable<Correspondence<Attribute, Matchable>> correspondences, DataIterator<Pair<String, Company>> resultCollector) {
		try{
			String preprocessedName = StringPreprocessing.tokenBasicNormalization(record.getName(), "", false);
			resultCollector.next(new Pair<>(preprocessedName.substring(0, 1), record));
		}catch(NullPointerException e){
			System.out.println("[Missing Name for ]"+record.getId());
		}
	}
}