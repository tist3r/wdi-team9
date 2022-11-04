package de.uni_mannheim.informatik.dws.wdi.IR_Team9.Preprocessing;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.ClosedDirectoryStreamException;

public class SplitLargeXML {

    private static final String FILENAME = "data/input/companies_shorted_results.xml"; //companies_shorted_results.xml
    private static Integer maxID;


    private static void createNewFile(BufferedReader br, String filename, Integer fileID, Integer lineLimit, String firstLine, String openingTag, String lastTested) throws Exception{
        maxID = fileID;
        File file = new File(filename+"_"+Integer.toString(fileID)+".xml");

        Integer row = 0;
        boolean closedCompanyTag = false;

        //Buffered Writer
        try(BufferedWriter bw = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8))){

            bw.write(firstLine);
            bw.newLine();
            bw.write(openingTag);
            bw.newLine();
            bw.write(lastTested);
            bw.newLine();

            String line;

            while((line = br.readLine()) != null){

                if(row < lineLimit || !closedCompanyTag){
                    bw.write(line);
                    bw.newLine();

                    if(row > lineLimit){
                        closedCompanyTag = line.contains("</Company>");
                    }
                }else{
                    createNewFile(br, filename, fileID+1, lineLimit, firstLine, openingTag, line);
                    break;
                }
                row++;

                //System.out.println(line);
            }

            if(fileID != maxID){
                bw.write("</Companies>");
            }  
        }



    }

    public static void main(String[] args) throws Exception{

        try (BufferedReader br = new BufferedReader(new FileReader(FILENAME, StandardCharsets.UTF_8))){
            String firstLine = br.readLine();
            String secondLine = br.readLine();
            Integer lineLimit = 6000000;

            createNewFile(br, "data/input/test/kaggle", 1, lineLimit, firstLine, secondLine, "");

        }

        // File file1 = new File("data/input/kaggle_1.xml");
        // File file2 = new File("data/input/kaggle_2.xml");
        // File file3 = new File("data/input/kaggle_3.xml");
        // File file4 = new File("data/input/kaggle_4.xml");

        // if (!file1.exists()) {
        //     file1.createNewFile();
        //  }

        //  if (!file2.exists()) {
        //     file2.createNewFile();
        //  }

        //  if (!file3.exists()) {
        //     file3.createNewFile();
        //  }
        //  if (!file4.exists()) {
        //     file4.createNewFile();
        //  }

        // try (BufferedReader br = new BufferedReader(new FileReader(FILENAME, StandardCharsets.UTF_8))){

        //     try(BufferedWriter bw1 = new BufferedWriter(new FileWriter(file1, StandardCharsets.UTF_8))){
        //         try(BufferedWriter bw2 = new BufferedWriter(new FileWriter(file2, StandardCharsets.UTF_8))){
        //             try(BufferedWriter bw3 = new BufferedWriter(new FileWriter(file3, StandardCharsets.UTF_8))){
        //                 try(BufferedWriter bw4 = new BufferedWriter(new FileWriter(file4, StandardCharsets.UTF_8))){
        //                 String line;
        //                 Integer row = 0;
            
        //                 String header = br.readLine(); //first line
        //                 String openingTag = br.readLine(); //secondLine
        //                 String closingTag = "</Companies>";
        //                 boolean closedCompanyTag1 = false;
        //                 boolean closedCompanyTag2 = false;
        //                 boolean closedCompanyTag3 = false;

        //                 bw1.write(header);
        //                 bw1.newLine();
        //                 bw1.write(openingTag);
        //                 bw1.newLine();

        //                 bw2.write(header);
        //                 bw2.newLine();
        //                 bw2.write(openingTag);
        //                 bw2.newLine();

        //                 bw3.write(header);
        //                 bw3.newLine();
        //                 bw3.write(openingTag);
        //                 bw3.newLine();

        //                 bw4.write(header);
        //                 bw4.newLine();
        //                 bw4.write(openingTag);
        //                 bw4.newLine();

                        
        //                 while((line = br.readLine()) != null){
        //                     if(row%1000000 == 0){
        //                         System.out.println(row);
        //                     }

        //                     if(row < 25000000 || !closedCompanyTag1){
        //                         bw1.write(line);
        //                         bw1.newLine();

        //                         if(row >= 25000000){
        //                             closedCompanyTag1 = line.contains("</Company>");
        //                         }
        //                     }
        //                     else if(row < 50000000 || !closedCompanyTag2){
        //                             bw2.write(line);
        //                             bw2.newLine();
        
        //                             if(row >= 50000000){
        //                                 closedCompanyTag2 = line.contains("</Company>");
        //                             }
        //                     }
        //                     else if(row < 75000000 || !closedCompanyTag3){
        //                         bw3.write(line);
        //                         bw3.newLine();
    
        //                         if(row >= 75000000){
        //                             closedCompanyTag3 = line.contains("</Company>");
        //                         }
        //                 }
        //                     else{
        //                         bw4.write(line);
        //                         bw4.newLine();      
        //                     }

        //                     row++;
        //                 }

        //                 bw1.write(closingTag); 
        //                 bw2.write(closingTag);
        //                 }}}}
       

        // }

        
    }

}