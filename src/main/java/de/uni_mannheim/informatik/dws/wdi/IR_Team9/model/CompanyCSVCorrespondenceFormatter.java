package de.uni_mannheim.informatik.dws.wdi.IR_Team9.model;

import de.uni_mannheim.informatik.dws.winter.model.Correspondence;
import de.uni_mannheim.informatik.dws.winter.model.Matchable;
import de.uni_mannheim.informatik.dws.winter.model.io.CSVCorrespondenceFormatter;

public class CompanyCSVCorrespondenceFormatter extends CSVCorrespondenceFormatter{

    @Override
    public <TypeA extends Matchable, TypeB extends Matchable> String[] format(Correspondence<TypeA, TypeB> record) {
        Company company1 = (Company) record.getFirstRecord();
        Company company2 = (Company) record.getSecondRecord();

		return new String[] { record.getFirstRecord().getIdentifier(), record.getSecondRecord().getIdentifier(),
				Double.toString(record.getSimilarityScore()), company1.getName(), company2.getName(),company1.getUrl(), company2.getUrl(), company1.getYearFounded().toString(), company2.getYearFounded().toString()};
	}


    
}
