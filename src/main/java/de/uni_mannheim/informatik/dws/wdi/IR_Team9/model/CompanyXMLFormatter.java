package de.uni_mannheim.informatik.dws.wdi.IR_Team9.model;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.uni_mannheim.informatik.dws.winter.model.io.XMLFormatter;

public class CompanyXMLFormatter extends XMLFormatter<Company> {

	@Override
	public Element createRootElement(Document doc) {
		return doc.createElement("Companies");
	}

	@Override
	public Element createElementFromRecord(Company record, Document doc) {
		Element company = doc.createElement("Company");

		company.appendChild(createTextElement("Id", record.getIdentifier(), doc));

		company.appendChild(createTextElementWithProvenance("Name", record.getName(),
				record.getMergedAttributeProvenance(Company.NAME), doc));
		company.appendChild(createTextElementWithProvenance("Country", record.getCountry(),
				record.getMergedAttributeProvenance(Company.COUNTRY), doc));
		company.appendChild(createIndustryElement(record, doc));
		company.appendChild(createTextElementSalesAmount("sales_amount", record.getSalesAmount(),
				record.getMergedAttributeProvenance(Company.SALES_AMOUNT), doc));
		company.appendChild(createTextElementWithProvenance("sales_currency", record.getSalesCurrency(),
				record.getMergedAttributeProvenance(Company.SALES_CURRENCY), doc));
		company.appendChild(createTextElementEmployee("current_employees", record.getCurrEmpEst(),
				record.getMergedAttributeProvenance(Company.CURRENT_EMPLOYEES), doc));
		company.appendChild(createTextElementEmployee("year_founded", record.getYearFounded(),
				record.getMergedAttributeProvenance(Company.YEAR_FOUNDED), doc));

		return company;
	}

	protected Element createTextElementWithProvenance(String name, String value, String provenance, Document doc) {
		Element elem = createTextElement(name, value, doc);
		elem.setAttribute("provenance", provenance);
		return elem;
	}

	protected Element createIndustryElement(Company record, Document doc) {
		Element industryRoot = doc.createElement("Industries");
		industryRoot.setAttribute("provenance", record.getMergedAttributeProvenance(Company.INDUSTRY));

		for (String i : record.getIndustries()) {
			industryRoot.appendChild(createTextElement("Industry", i, doc));
		}

		return industryRoot;
	}

	protected Element createTextElementSalesAmount(String name, Integer integer, String provenance, Document doc) {
		Element elem = createTextElement(name, String.valueOf(integer), doc);
		elem.setAttribute("provenance", provenance);
		return elem;
	}

	protected Element createTextElementEmployee(String name, Double doubl, String provenance, Document doc) {
		Element elem = createTextElement(name, String.valueOf(doubl), doc);
		elem.setAttribute("provenance", provenance);
		return elem;
	}

}
