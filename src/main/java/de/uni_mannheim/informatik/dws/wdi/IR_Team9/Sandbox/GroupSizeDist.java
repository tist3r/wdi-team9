package de.uni_mannheim.informatik.dws.wdi.IR_Team9.Sandbox;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import de.uni_mannheim.informatik.dws.wdi.IR_Team9.model.Company;
import de.uni_mannheim.informatik.dws.wdi.IR_Team9.model.CompanyXMLReader;
import de.uni_mannheim.informatik.dws.wdi.IR_Team9.utils.Constants;
import de.uni_mannheim.informatik.dws.winter.datafusion.CorrespondenceSet;
import de.uni_mannheim.informatik.dws.winter.model.FusibleHashedDataSet;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;

public class GroupSizeDist {


    public static void correctCorrespondences(String ds1, String ds2, String eID){

        //fix basic correspondences
        try(CSVReader reader = new CSVReader(Files.newBufferedReader(Paths.get(Constants.getExperimentCompanyCorrPath(ds1, ds2, eID)), StandardCharsets.ISO_8859_1))){
            try(CSVWriter writer = new CSVWriter(Files.newBufferedWriter(Paths.get(Constants.getExperimentBasicCorrPath(ds1, ds2, eID)), StandardCharsets.UTF_8))){
                String[] line;
                String[] newLine;

                while((line = reader.readNext()) != null){
                    newLine = new String[]{line[0], line[1], line[2]};
                    writer.writeNext(newLine);
                }
            }catch(IOException e){
                e.printStackTrace();
            }
        }catch(IOException e){
            e.printStackTrace();
        }

                //fix top1 correspondences
                try(CSVReader reader = new CSVReader(Files.newBufferedReader(Paths.get(Constants.getExperimentCompanyCorrPathTop1(ds1, ds2, eID)),StandardCharsets.ISO_8859_1))){
                    try(CSVWriter writer = new CSVWriter(Files.newBufferedWriter(Paths.get(Constants.getExperimentBasicCorrPathTop1(ds1, ds2, eID)),StandardCharsets.UTF_8))){
                        String[] line;
                        String[] newLine;
        
                        while((line = reader.readNext()) != null){
                            newLine = new String[]{line[0], line[1], line[2]};
                            writer.writeNext(newLine);
                        }
                    }catch(IOException e){
                        e.printStackTrace();
                    }
                }catch(IOException e){
                    e.printStackTrace();
                }

    }


    public static void main(String[] args) throws Exception{
        FusibleHashedDataSet<Company, Attribute> ds1 = new FusibleHashedDataSet<>();
        new CompanyXMLReader().loadFromXML(new File(Constants.getDatasetPath("dbpedia")), Constants.RECORD_PATH, ds1);

        FusibleHashedDataSet<Company, Attribute> ds2 = new FusibleHashedDataSet<>();
        new CompanyXMLReader().loadFromXML(new File(Constants.getDatasetPath("kaggle_f")), Constants.RECORD_PATH, ds2);

        
        try{
            CorrespondenceSet<Company, Attribute> cSet = new CorrespondenceSet<Company, Attribute>();

            cSet.loadCorrespondences(new File(Constants.getExperimentBasicCorrPath("dbpedia", "kaggle_f", "29_10_87_dbpedia_kaggle_f")),ds1, ds2);

            cSet.writeGroupSizeDistribution(new File(Constants.getGroupSizeDistPath("29_10_87_dbpedia_kaggle_f", "All_new")));

        }catch(IOException e){
            e.printStackTrace();
        }
    }
    
}
