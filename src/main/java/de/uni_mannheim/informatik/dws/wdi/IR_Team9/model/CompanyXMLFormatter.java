package de.uni_mannheim.informatik.dws.wdi.IR_Team9.model;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.uni_mannheim.informatik.dws.winter.model.io.XMLFormatter;

public class CompanyXMLFormatter extends XMLFormatter<Company>{

	
	@Override
	public Element createRootElement(Document doc) {
		return doc.createElement("companies");
	}

	@Override
	public Element createElementFromRecord(Company record, Document doc) {
		Element company = doc.createElement("company");

		company.appendChild(createTextElement("id", record.getIdentifier(), doc));

		company.appendChild(createTextElementWithProvenance("name",
				record.getName(),
				record.getMergedAttributeProvenance(Company.NAME), doc));
		company.appendChild(createTextElementWithProvenance("country",
				record.getCountry(),
				record.getMergedAttributeProvenance(Company.COUNTRY), doc));
		company.appendChild(createTextElement("industry",
				record.getIndustries(),
				record.getMergedAttributeProvenance(Company.INDUSTRY), doc));
		company.appendChild(createTextElementWithProvenance("sales_amount",
				record.getSalesAmount(),
				record.getMergedAttributeProvenance(Company.SALES_AMOUNT), doc));
		
		return company;
	}
	
	protected Element createTextElementWithProvenance(String name,
			String value, String provenance, Document doc) {
		Element elem = createTextElement(name, value, doc);
		elem.setAttribute("provenance", provenance);
		return elem;
	}
	
	protected Element createTextElement(Company comp, Document doc) {
		Element company = doc.createElement("Industries");
		company.setAttribute("provenance",
				comp.getMergedAttributeProvenance(Company.INDUSTRY));

		for (String a : comp.getIndustries()) {
			company.appendChild(createTextElement("Industry", a, doc));
		}

		return company;
	}

}
