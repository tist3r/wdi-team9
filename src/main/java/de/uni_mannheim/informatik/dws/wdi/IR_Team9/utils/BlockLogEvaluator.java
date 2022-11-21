package de.uni_mannheim.informatik.dws.wdi.IR_Team9.utils;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import au.com.bytecode.opencsv.CSVReader;

public class BlockLogEvaluator {

    /**
     * Reads blocker log to determine number of blocked pairs
     * @return the set of IDs
     */
    public static Integer getNumBlockedPairs(String experimentID, String ds1Name, String ds2Name){

        Set<String> exp = new HashSet<String>();

        try(CSVReader reader = new CSVReader(new FileReader(Constants.getExperimentBlockSizePath(experimentID, ds1Name, ds2Name, 1)))){
            int numBlocks;
            int blockSize;
            int blockedPairs = 0;
            String blocks;

            reader.readNext(); //skip header line

            String[] line;
            while((line = reader.readNext()) != null){
                //System.out.println(line); break;
                if(line.length ==2){
                    blocks = line[0];
                    blockSize = Integer.parseInt(line[1]);

                    numBlocks = blocks.split(",").length;

                    blockedPairs += numBlocks*blockSize;
                }
                
            }

            return blockedPairs;


        }catch(IOException e){
            e.printStackTrace();
            return -1;

        }
    }


    public static void main(String[] args) {
        System.out.println(getNumBlockedPairs("7_2_85_dbpedia_forbes", "dbpedia", "forbes"));
    }
    
}
