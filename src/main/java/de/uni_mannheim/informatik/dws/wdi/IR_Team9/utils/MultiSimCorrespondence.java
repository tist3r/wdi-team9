package de.uni_mannheim.informatik.dws.wdi.IR_Team9.utils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Set;
import java.util.TreeSet;
import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

/**
 * Handeling Correspondences after they have been written to a file was an issue that had to be solved. Especially after processing the Kaggle input, functionality was needed 
 * to postprocess the files to make them managable. This class brings some conveneince for handeling these files and the contained correspondences.
 */
public class MultiSimCorrespondence {
    public String id1;
    public String id2;
    public double sim1 = 0;
    public  double sim2 = 0;
    public  double sim3 = 0;
    public  String name1;
    public  String name2;

    /**
     * @author Thomas
     * Empty constructor that can be used to set each field individually.
     */
    public MultiSimCorrespondence() {
    }

    /**
     * @author Thomas
     * Conveneince Constructor that can be used when reading a file with only one similarity value
     * @param id1
     * @param id2
     * @param sim The similarity
     */
    public MultiSimCorrespondence(String id1, String id2, double sim) {
        this.id1 = id1;
        this.id2 = id2;
        this.sim1 = sim;
    }


    /**
     * @author Thomas
     * Sets the similarity at one of the 3 possible id places.
     * @param simID
     * @param sim
     */
    public void setSimAtID(Integer simID, Double sim){
        switch (simID){
            case 1: this.sim1 = sim; break;
            case 2: this.sim2 = sim; break;
            case 3: this.sim3 = sim; break;
            default: throw new IndexOutOfBoundsException(String.format("Index %d is out of bounds. Use index 1 to 3...", simID));
        }
    }

    @Override
    public String toString() {
        return id1 + id2;
    }

    
    /**
     * 
     * Writes a set of correspondences to a file
     * @param set
     * @param maxLines
     * @param toPath
     * @throws IOException
     * @author Thomas
     */
    private static void writeSetToFile(Set<MultiSimCorrespondence> set, int maxLines, String toPath, boolean withNames) throws IOException{
        try(CSVWriter writer = new CSVWriter(Files.newBufferedWriter(Paths.get(toPath), StandardCharsets.UTF_8))){
            String[] line;

            int numLines = 0;
            
            for(MultiSimCorrespondence cf : set){

                if(!withNames){
                    line = new String[]{
                        cf.id1,
                        cf.id2,
                        Double.toString(cf.sim1)
                    };
                }else{
                    line = new String[]{
                        cf.id1,
                        cf.id2,
                        Double.toString(cf.sim1),
                        cf.name1,
                        cf.name2
                    }; 
                }


                writer.writeNext(line);

                numLines++;

                if(numLines > maxLines){
                    break;
                }
            }
        }
    }


    /**
     * 
     * Reads a correspondence output from an IR tasks and returns and ordered set of these correspondences.
     * @author Thomas
     * @param path
     * @param desc
     * @return
     * @throws IOException
     */
    private static Set<MultiSimCorrespondence> getOrderedCFfromInput(String path, boolean desc, boolean withNames) throws IOException{
        int rev = desc ? -1 : 1;

        Set<MultiSimCorrespondence> sortedSet = new TreeSet<MultiSimCorrespondence>(
            (cf1, cf2) -> Double.compare(cf1.sim1,cf2.sim1)*rev);

            
        try(CSVReader reader = new CSVReader(Files.newBufferedReader(Paths.get(path),StandardCharsets.ISO_8859_1))){
            String[] line;
            MultiSimCorrespondence c;
            while((line = reader.readNext())!=null){
                c = new MultiSimCorrespondence();
                if(line.length >= 3){
                    c.id1 = line[0];
                    c.id2 = line[1];
                    c.sim1 = Double.parseDouble(line[2]); 
                }
                if(line.length == 5 && withNames){
                    c.name1 = line[3];
                    c.name2 = line[4];
                }

                sortedSet.add(c);
            }
        }

        return sortedSet;
    }

    public static void writeTopKCorrespondences(String inPath, String outPath, int k, boolean withNames) throws IOException{
        writeSetToFile(getOrderedCFfromInput(inPath, true, withNames), 10000, outPath, true);
    }

    public static void main(String[] args) throws Exception{
        String inPath = "data/output/experiments/5_9_2_dbpedia_kaggle_a_2/dbpedia_kaggle_a_2_corr_w_names.csv";
        String toPath = "data/output/sortedCorrespondences/5_9_2_dbpedia_kaggle_a_2.csv";
    }
}
