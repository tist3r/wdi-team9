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

	private boolean basicNormalization = false;
	private boolean removeFrequentTokens = false;


	public CompanyBlockingKeyByNameGenerator() {
	}


	public CompanyBlockingKeyByNameGenerator(Boolean basicNormalization, Boolean removeFrequentTokens) {
		this.basicNormalization = basicNormalization;
		this.removeFrequentTokens = removeFrequentTokens;
	}
	

	/*
     * Blocking based on first letter of company name.     * 
     */
	@Override
	public void generateBlockingKeys(Company record, Processable<Correspondence<Attribute, Matchable>> correspondences, DataIterator<Pair<String, Company>> resultCollector) {
		try{
			String preprocessedName = record.getName();
			if(this.removeFrequentTokens){
				preprocessedName = StringPreprocessing.removeFrequentToken(preprocessedName, true);
			}
			if(this.basicNormalization){
				preprocessedName = StringPreprocessing.tokenBasicNormalization(preprocessedName, "", false);
			}

			if(preprocessedName.length()>0){
				resultCollector.next(new Pair<>(preprocessedName.substring(0, 1), record));
			}else{
				resultCollector.next(new Pair<>(record.getName().toLowerCase().substring(0, 1), record));
			}

			

		}catch(NullPointerException e){
			System.out.println(String.format("[ERROR ] Missing Name for %s - skipping...",record.getId()));
		}
	}
}