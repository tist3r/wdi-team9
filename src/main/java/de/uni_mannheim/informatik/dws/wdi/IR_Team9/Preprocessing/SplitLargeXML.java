package de.uni_mannheim.informatik.dws.wdi.IR_Team9.Preprocessing;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;

public class SplitLargeXML {

    private static final String FILENAME = "data/input/companies_shorted_results.xml"; //companies_shorted_results.xml

    public static void main(String[] args) throws Exception{

        File file1 = new File("data/input/kaggle_1.xml");
        File file2 = new File("data/input/kaggle_2.xml");
        File file3 = new File("data/input/kaggle_3.xml");

        if (!file1.exists()) {
            file1.createNewFile();
         }

         if (!file2.exists()) {
            file2.createNewFile();
         }

         if (!file3.exists()) {
            file3.createNewFile();
         }


        try (BufferedReader br = new BufferedReader(new FileReader(FILENAME, StandardCharsets.UTF_8))){

            try(BufferedWriter bw1 = new BufferedWriter(new FileWriter(file1, StandardCharsets.UTF_8))){
                try(BufferedWriter bw2 = new BufferedWriter(new FileWriter(file2, StandardCharsets.UTF_8))){
                    try(BufferedWriter bw3 = new BufferedWriter(new FileWriter(file3, StandardCharsets.UTF_8))){
                        String line;
                        Integer row = 0;
            
                        String header = br.readLine(); //first line
                        String openingTag = br.readLine(); //secondLine
                        String closingTag = "</Companies>";
                        boolean closedCompanyTag1 = false;
                        boolean closedCompanyTag2 = false;

                        bw1.write(header);
                        bw1.newLine();
                        bw1.write(openingTag);
                        bw1.newLine();

                        bw2.write(header);
                        bw2.newLine();
                        bw2.write(openingTag);
                        bw2.newLine();

                        
                        while((line = br.readLine()) != null){
                            if(row%1000000 == 0){
                                System.out.println(row);
                            }

                            if(row < 33000000 || !closedCompanyTag1){
                                bw1.write(line);
                                bw1.newLine();

                                if(row >= 33000000){
                                    closedCompanyTag1 = line.contains("</Company>");
                                }
                            }
                            else if(row < 66000000 || !closedCompanyTag2){
                                    bw2.write(line);
                                    bw2.newLine();
        
                                    if(row >= 33000000){
                                        closedCompanyTag2 = line.contains("</Company>");
                                    }
                            }
                            else{
                                bw3.write(line);
                                bw3.newLine();      
                            }

                            row++;
                        }

                        bw1.write(closingTag); 
                        bw2.write(closingTag);
                        }}}
       

        }

        
    }

}