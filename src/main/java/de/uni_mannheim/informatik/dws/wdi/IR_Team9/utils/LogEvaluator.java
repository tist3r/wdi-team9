package de.uni_mannheim.informatik.dws.wdi.IR_Team9.utils;

import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

public class LogEvaluator {

    public static void removeDuplicates() throws Exception{
        String filename = "data/output/experiments/reevaluated_log.csv";
        Set<String[]> entries = new HashSet<>();
        String[] firstLine;

        String[] line;

        try(CSVReader r = new CSVReader(Files.newBufferedReader(Paths.get(filename), StandardCharsets.UTF_8))){
            firstLine = r.readNext();

            while((line = r.readNext())!=null){
                if(!entries.contains(line)){
                    entries.add(line);
                }
            }

        }

        try(CSVWriter w = new CSVWriter(Files.newBufferedWriter(Paths.get(filename),StandardCharsets.UTF_8))){
            w.writeNext(firstLine);
            w.writeAll(entries.stream().toList());
        }
    }

    public static void reformatCSV() throws IOException{
        String filename = "data/output/experiments/27_10_85_dbpedia_kaggle_f/dbpedia_kaggle_f_corr_w_names.csv";
        String filename_out = "data/output/experiments/27_10_85_dbpedia_kaggle_f/dbpedia_kaggle_f_corr_w_names_new.csv";
        Set<String[]> entries = new HashSet<>();
        String[] firstLine;

        String[] line;

        try(CSVReader r = new CSVReader(Files.newBufferedReader(Paths.get(filename), StandardCharsets.ISO_8859_1),';')){
            firstLine = r.readNext();

            while((line = r.readNext())!=null){
                entries.add(line);
            }

        }

        try(CSVWriter w = new CSVWriter(Files.newBufferedWriter(Paths.get(filename_out),StandardCharsets.UTF_8))){
            w.writeNext(firstLine);
            w.writeAll(entries.stream().toList());
        }
    }


    public static void main(String[] args) throws Exception {
        //removeDuplicates();
        reformatCSV();
    }
    
}
