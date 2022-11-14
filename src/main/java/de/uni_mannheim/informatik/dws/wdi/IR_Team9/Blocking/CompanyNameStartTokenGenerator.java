package de.uni_mannheim.informatik.dws.wdi.IR_Team9.Blocking;

import java.util.StringTokenizer;

import de.uni_mannheim.informatik.dws.wdi.IR_Team9.Preprocessing.StringPreprocessing;
import de.uni_mannheim.informatik.dws.wdi.IR_Team9.model.Company;
import de.uni_mannheim.informatik.dws.winter.matching.blockers.generators.RecordBlockingKeyGenerator;
import de.uni_mannheim.informatik.dws.winter.model.Correspondence;
import de.uni_mannheim.informatik.dws.winter.model.Matchable;
import de.uni_mannheim.informatik.dws.winter.model.Pair;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;
import de.uni_mannheim.informatik.dws.winter.processing.DataIterator;
import de.uni_mannheim.informatik.dws.winter.processing.Processable;

public class CompanyNameStartTokenGenerator extends RecordBlockingKeyGenerator<Company, Attribute>{

    private int n;
    private boolean rmFrequentTokens;

    public CompanyNameStartTokenGenerator(){
        n = 1;
    }

    public CompanyNameStartTokenGenerator(int n){
        this.n = n;
    }

    public CompanyNameStartTokenGenerator(int n, boolean rmFrequentTokens){
        this.n = n;
        this.rmFrequentTokens = rmFrequentTokens;
    }

    private static final long serialVersionUID = 1L;
	/*
     * Blocking based on first letter of company name.     * 
     */
	@Override
	public void generateBlockingKeys(Company record, Processable<Correspondence<Attribute, Matchable>> correspondences, DataIterator<Pair<String, Company>> resultCollector) {
		String ppName = StringPreprocessing.removePunctuation(record.getName().toLowerCase(), "");

        if(rmFrequentTokens){
            ppName = StringPreprocessing.removeFrequentTokens(ppName);
        }

        StringTokenizer tokenizer = new StringTokenizer(ppName);
        String token;

        while(tokenizer.hasMoreTokens()){
            token = tokenizer.nextToken();

            if(token.length() > n){
                resultCollector.next(new Pair<>(token.substring(0,n), record));
            }else{
                resultCollector.next(new Pair<>(token.substring(0,token.length()), record));
            }

            
        }
		
	}
    
}
