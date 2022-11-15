package de.uni_mannheim.informatik.dws.wdi.IR_Team9.Sandbox;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ArrayList;
import java.util.HashMap;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import de.uni_mannheim.informatik.dws.wdi.IR_Team9.Preprocessing.StringPreprocessing;
import de.uni_mannheim.informatik.dws.wdi.IR_Team9.utils.Constants;

public class FrequentTokenAnalysis {
    
    public static List<Entry<String, Integer>> getFrequentTokensFromCorrCSV(String path) throws FileNotFoundException{
        HashMap<String, Integer> occurences = new HashMap<>();

        String[] nameTokens;
        String name1, name2;
        try(CSVReader reader = new CSVReader(new FileReader(path))){
            //5,6
            for(String[] line : reader.readAll()){
                name1 = StringPreprocessing.removePunctuation(line[5].toLowerCase(), "");
                name2 = StringPreprocessing.removePunctuation(line[6].toLowerCase(), "");

                nameTokens = name1.split("\\s+");

                for(String token : nameTokens){
                    if (occurences.containsKey(token)){
                        occurences.put(token, occurences.get(token) + 1);
                    }else{
                        occurences.put(token, 1);
                    }
                }

                nameTokens = name2.split("\\s+");

                for(String token : nameTokens){
                    if (occurences.containsKey(token)){
                        occurences.put(token, occurences.get(token) + 1);
                    }else{
                        occurences.put(token, 1);
                    }
                }
            }

            List<Entry<String, Integer>> sortedEntries = occurences.entrySet()
            .stream()
            .sorted(Map.Entry.comparingByValue())
            .toList();

            return sortedEntries;


            
        }catch(IOException e){
            e.printStackTrace();
        }

        return null;
    }

    public static List<Entry<String, Integer>> getFrequentTokensFromXML(String path) throws FileNotFoundException{
        HashMap<String, Integer> occurences = new HashMap<>();

        String[] nameTokens;
        String name1, name2;
        try(BufferedReader reader = Files.newBufferedReader(Paths.get(path), StandardCharsets.UTF_8)){
            System.out.println("Starting to read file");
            //5,6
            String line;
            String name;

            while((line = reader.readLine())!=null){
                if(line.contains("<Name")){
                    line = line.trim();

                    try{
                        name = line.substring(line.indexOf(">"), line.indexOf("<", line.indexOf(">")));
                        nameTokens = name.split("\\s+");
    
                        for(String token : nameTokens){
                            if (occurences.containsKey(token)){
                                occurences.put(token, occurences.get(token) + 1);
                            }else{
                                occurences.put(token, 1);
                            }
                        }
                    }catch(Exception e){
                        System.out.println(String.format("Problem with line %s", line));
                    }
                    

                }
            }

            List<Entry<String, Integer>> sortedEntries = occurences.entrySet()
            .stream()
            .sorted(Map.Entry.comparingByValue())
            .toList();

            return sortedEntries;
     
        }catch(IOException e){
            e.printStackTrace();
        }

        return null;
    }

    public static void writeOccurences(List<Entry<String, Integer>> occurences, String toPath) throws IOException{
        try(CSVWriter writer = new CSVWriter(new FileWriter(toPath))){
            for(Entry<String, Integer> e : occurences){
                String[] values = {e.getKey(), Integer.toString(e.getValue())};
                writer.writeNext(values);
            }
        }
    }

    public static void main(String[] args) throws Exception {
        //String inPath = "data/output/combinedFiles/dbpedia_dw.csv";
        String inPath = "data/output/combinedFiles/dbpedia__kaggle_t.csv";
        String outPath = "data/output/combinedFiles/frequent_tokens2.csv";

        // List<Entry<String, Integer>> occurences = getFrequentTokensFromCorrCSV(inPath);
        // writeOccurences(occurences, outPath);

        List<Entry<String, Integer>> occurences = getFrequentTokensFromXML(Constants.getDatasetPath("KAGGLE_ORIGINAL"));
        writeOccurences(occurences, outPath);
    }


}
