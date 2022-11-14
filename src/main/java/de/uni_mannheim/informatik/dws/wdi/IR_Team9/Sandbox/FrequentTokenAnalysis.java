package de.uni_mannheim.informatik.dws.wdi.IR_Team9.Sandbox;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ArrayList;
import java.util.HashMap;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import de.uni_mannheim.informatik.dws.wdi.IR_Team9.Preprocessing.StringPreprocessing;

public class FrequentTokenAnalysis {
    
    public static List<Entry<String, Integer>> getFrequentTokens(String path) throws FileNotFoundException{
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
        String outPath = "data/output/combinedFiles/frequent_tokens1.csv";

        List<Entry<String, Integer>> occurences = getFrequentTokens(inPath);
        writeOccurences(occurences, outPath);
    }


}
