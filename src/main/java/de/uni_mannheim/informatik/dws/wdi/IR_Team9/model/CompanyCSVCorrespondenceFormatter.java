package de.uni_mannheim.informatik.dws.wdi.IR_Team9.model;

import de.uni_mannheim.informatik.dws.winter.model.Correspondence;
import de.uni_mannheim.informatik.dws.winter.model.Matchable;
import de.uni_mannheim.informatik.dws.winter.model.io.CSVCorrespondenceFormatter;

public class CompanyCSVCorrespondenceFormatter extends CSVCorrespondenceFormatter{

    @Override
    public <TypeA extends Matchable, TypeB extends Matchable> String[] format(Correspondence<TypeA, TypeB> record) {
        Company company1 = (Company) record.getFirstRecord();
        Company company2 = (Company) record.getSecondRecord();

        String url1 = company1.getUrl() == null ? "" : company1.getUrl();
        String url2 = company2.getUrl() == null ? "" : company2.getUrl();
        String y1 = company1.getYearFounded() == null ? "" : company1.getYearFounded().toString();
        String y2 = company2.getYearFounded() == null ? "" : company2.getYearFounded().toString();

		return new String[] { record.getFirstRecord().getIdentifier(), record.getSecondRecord().getIdentifier(),
				Double.toString(record.getSimilarityScore()), company1.getName(), company2.getName(), url1, url2, y1, y2};
	}


    
}
