package de.uni_mannheim.informatik.dws.wdi.IR_Team9.utils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyException;
import java.util.NoSuchElementException;
import java.util.stream.Stream;
import java.util.Arrays;
import java.util.HashSet;

import org.apache.jena.tdb.base.file.FileException;

public class Constants {

    public static final String RECORD_PATH = "/Companies/Company";

    private static String kaggleBasePath = "data/input/test/";
    private static Integer maxKaggleId = getMaxKaggleID();

    private static final String dbpediaPath = "data/input/dbpedia.xml";
    private static final String forbesPath = "data/input/Forbes_results.xml";
    private static final String dataworldPath = "data/input/dataworld_ts.xml";
    private static final String kagglePath = "data/input/companies_shorted_results.xml";


    private static final String gsFolder = "data/goldstandard/";
    private static final String redKaggleFolder = "data/output/reducedKaggleSet/";
    private static final String aggregatedKaggleFolder = "data/output/aggregatedKaggle/";


    private static final HashSet<String> validDsNames = new HashSet<>(Arrays.asList(new String[] {"dbpedia", "dw", "forbes", "kaggle_a_1", "kaggle_a_2", "kaggle_a_3", "kaggle_a_4"}));


    public static String getKagglePath(Integer id) throws FileException{
        if(id > maxKaggleId){
            throw new FileException(String.format("File with ID %d does not exist", id));
        }
        return kaggleBasePath + "kaggle_" +Integer.toString(id) + ".xml";
    }

    public static Integer getMaxKaggleID(){
        //thanks to: https://www.baeldung.com/java-list-directory-files
        
        Integer maxID = Stream.of(new File(kaggleBasePath).listFiles())
            .filter(file -> !file.isDirectory())
            .map(File::getName)
            .map(filename -> filename.replaceAll("[^0-9]", ""))
            .map(d -> Integer.parseInt(d))
            .max(Integer::compare).orElseThrow(NoSuchElementException::new);

        //System.out.println(maxID);
        return maxID;
    }

    /**
     * Returns the input folder path of the specified dataset. Not applicable for kaggle dataset.
     */
    public static String getDatasetPath(String dsName) throws KeyException{
        switch (dsName){
            case "dbpedia": return dbpediaPath;
            case "dw": return dataworldPath;
            case "forbes": return forbesPath;
            case "kaggle_a_1": return getKaggleAggregatedRedXMLPath(1);
            case "kaggle_a_2": return getKaggleAggregatedRedXMLPath(2);
            case "kaggle_a_3": return getKaggleAggregatedRedXMLPath(3);
            case "kaggle_a_4": return getKaggleAggregatedRedXMLPath(4);
            default: throw new KeyException("Invalid Key. Use either dbpedia, dw, or forbes. For Kaggle Paths use the overloaded function");
        }
    }

    /*
     * Return dataset path of any dataset. Specify @param id to get the kaggle partitioned file with this id.
     */
    public static String getDatasetPath(String dsName, Integer id) throws KeyException{
        if(dsName.matches("kaggle")){
            return getKagglePath(id);
        }

        return getDatasetPath(dsName);
    }

    /**
     * Get training data for dataset combination.
     */
    public static String getTrainData(String source1, String source2) throws KeyException{
        if(validDsNames.contains(source1) && validDsNames.contains(source2)){
            return String.format("%s%s_%s_%s.csv", gsFolder, source1, source2, "train");
        }

        throw new KeyException(String.format("Invalid dataset key %s, %s or wrong order of sources", source1, source2));
    }

     /**
     * Get test data for dataset combination.
     */
    public static String getTestData(String source1, String source2) throws KeyException{
        if(validDsNames.contains(source1) && validDsNames.contains(source2)){
            return String.format("%s%s_%s_%s.csv", gsFolder, source1, source2, "test");
        }

        throw new KeyException(String.format("Invalid dataset key %s, %s or wrong order of sources", source1, source2));
    }

    public static String getExperimentLogPath(){
        return "data/output/experiments/experiment_log.csv";
    }

    public static String getExperimentBasicCorrPath(String ds1, String ds2, String experiment_id){
        return String.format("%s%s_%s_corr.csv", getExperimentRootPath(experiment_id), ds1, ds2);
    }

    public static String getExperimentCompanyCorrPath(String ds1, String ds2, String experiment_id){
        return String.format("%s%s_%s_corr_w_names.csv", getExperimentRootPath(experiment_id), ds1, ds2);
    }

    /**
     * 
     * @param experiment_id
     * @param train_test Specifiy either "test" or "train"
     * @return
     */
    public static String getExperimentEvaluationFilePath(String experiment_id, String train_test){
        return String.format("%seval_%s.csv", getExperimentRootPath(experiment_id),train_test);
    }

    public static String getExperimentRootPath(String experiment_id){
        String path = String.format("data/output/experiments/%s/", experiment_id);
        
        if(!Files.exists(Paths.get(path))){
            //make experiment folder if not exists
            try{
                Files.createDirectories(Paths.get(path));
            }catch(Exception e){
                e.printStackTrace();
            }
            
        }

        return path;
    }


    public static String getExperimentBlockSizePath(String experiment_id, String ds1, String ds2){
        return String.format("%s%s_%s_blockSizeInfo.csv", getExperimentRootPath(experiment_id),ds1,ds2);
    }


    public static String getKaggleReducedXMLPath(int id){
        return String.format("%skaggle_%d_red.xml", getKaggleReducedXMLRootPath(), id);
    }

    public static String getKaggleReducedXMLRootPath(){
        return redKaggleFolder;
    }

    public static int getNumKaggleReducedPartitions(){
        Integer maxID = Stream.of(new File(getKaggleReducedXMLRootPath()).listFiles())
            .filter(file -> !file.isDirectory())
            .map(File::getName)
            .filter(filename -> filename.matches("[0-9]{2}\\.csv"))
            .map(filename -> filename.replaceAll("\\.csv", ""))
            .map(d -> Integer.parseInt(d))
            .max(Integer::compare).orElseThrow(NoSuchElementException::new);

        //System.out.println(maxID);
        return maxID;
    }

    public static String getKaggleAggregatedRedXMLPath(int id){
        return String.format("%skaggle_a_%d.xml", aggregatedKaggleFolder, id);
    }

    /**
     * @author Thomas
     * Returns path to sorted top k correspondences for the experiment ID
     * @param experimentID
     * @return
     */
    public static String getSortedCorrespondencesPath(String experimentID, boolean withNames){
        if(withNames){
            return String.format("data/output/sortedCorrespondences/%s_w_names.csv", experimentID);
        }
        return String.format("data/output/sortedCorrespondences/%s.csv", experimentID);
    }

    /**
     * Returns the dataset name of the final kaggle input partitions.
     * @return
     */
    public static String[] getAggregateKagglePartitionedDSNames(){
        return new String[]{"kaggle_a_1","kaggle_a_2","kaggle_a_3","kaggle_a_4"};
    }

    /**
     * Returns the dataset name of the final kaggle input partitions by the partition ID
     * @return
     */
    public static String getAggregateKagglePartitionedDSNamesByID(int i){
        if(i > 0 && i <= 4){
            return getAggregateKagglePartitionedDSNames()[i-1];
        }

        throw new IndexOutOfBoundsException();    
    }

    /**
     * Returns the path of the Kaggle error log
     * @return
     */
    public static String getKaggleErrorLogPath(){
        return "logs/kaggleErrorLog.txt";
    }

    public static void main(String[] args) throws Exception{
        System.out.println(getNumKaggleReducedPartitions());
    }
}
