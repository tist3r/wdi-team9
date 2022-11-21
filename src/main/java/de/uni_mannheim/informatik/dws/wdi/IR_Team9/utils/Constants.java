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
import org.slf4j.Logger;

import de.uni_mannheim.informatik.dws.winter.utils.WinterLogManager;

public class Constants {
    private static final Logger logger = WinterLogManager.activateLogger("default");

    public static final String RECORD_PATH = "/Companies/Company";

    private static String kaggleBasePath = "data/input/test/";
    private static String blockerBasePath = "data/output/blockers/";
    private static Integer maxKaggleId = getMaxKaggleID();

    private static final String dbpediaPath = "data/input/dbpedia.xml";
    private static final String forbesPath = "data/input/Forbes_results.xml";
    private static final String dataworldPath = "data/input/dataworld_ts.xml";
    private static final String kagglePath = "data/input/companies_shorted_results.xml";
    private static final String kaggleInputPath = "data/input/kaggle.xml";


    private static final String gsFolder = "data/goldstandard/";
    private static final String redKaggleFolder = "data/output/reducedKaggleSet/";
    private static final String aggregatedKaggleFolder = "data/output/aggregatedKaggle/";


    /**
     * Collection that can be used to check valid DS names or to loop over them.
     */
    public static final HashSet<String> validDsNames = new HashSet<>(Arrays.asList(new String[] {"dbpedia", "dw", "forbes", "kaggle"}));


     /**
     * Returns the input folder path of the specified dataset.
     * Possible keys are dbpedia, dw, forbes, and kaggle.
     * KAGGLE_ORIGINAL can be specified but will be problematic during processing.
     * @dsName dataset name
     */
    public static String getDatasetPath(String dsName) throws KeyException{
        switch (dsName){
            case "dbpedia": return dbpediaPath;
            case "dw": return dataworldPath;
            case "forbes": return forbesPath;
            //case "kaggle_a_1": return getKaggleAggregatedRedXMLPath(1);
            //case "kaggle_a_2": return getKaggleAggregatedRedXMLPath(2);
            //case "kaggle_a_3": return getKaggleAggregatedRedXMLPath(3);
            //case "kaggle_a_4": return getKaggleAggregatedRedXMLPath(4);
            case "kaggle": return kaggleInputPath;
            case "KAGGLE_ORIGINAL": return kagglePath;
            default: throw new KeyException("Invalid Key. Use either dbpedia, dw, forbes, or kaggle.");
        }
    }

    /**
     * Get training data for dataset combination.
     * @source1 Name of the source
     * @source2 Name of the source
     */
    public static String getTrainData(String source1, String source2) throws KeyException{
        if(validDsNames.contains(source1) && validDsNames.contains(source2)){
            return String.format("%s%s_%s_%s.csv", gsFolder, source1, source2, "train");
        }

        throw new KeyException(String.format("Invalid dataset key %s, %s or wrong order of sources", source1, source2));
    }



     /**
     * Get test data for dataset combination.
     * @source1 Name of the source
     * @source2 Name of the source
     */
    public static String getTestData(String source1, String source2) throws KeyException{
        if(validDsNames.contains(source1) && validDsNames.contains(source2)){
            return String.format("%s%s_%s_%s.csv", gsFolder, source1, source2, "test");
        }

        throw new KeyException(String.format("Invalid dataset key %s, %s or wrong order of sources", source1, source2));
    }


    /**
     * Returns the path to Experiment log file.
     * @return
     */
    public static String getExperimentLogPath(){
        return "data/output/experiments/experiment_log_new.csv";
    }


    /**
     * Returns the path for the correspondence file with names included.
     */
    public static String getExperimentBasicCorrPath(String ds1, String ds2, String experiment_id){
        return String.format("%s%s_%s_corr.csv", getExperimentRootPath(experiment_id), ds1, ds2);
    }

    /**
     * Returns the path for the correspondence file with names included. For top 1
     */
    public static String getExperimentBasicCorrPathTop1(String ds1, String ds2, String experiment_id){
        return String.format("%s%s_%s_corr_top1.csv", getExperimentRootPath(experiment_id), ds1, ds2);
    }



    /**
     * Returns the path for the correspondence file with names included.
     * @param ds1
     * @param ds2
     * @param experiment_id
     * @return
     */
    public static String getExperimentCompanyCorrPath(String ds1, String ds2, String experiment_id){
        return String.format("%s%s_%s_corr_w_names.csv", getExperimentRootPath(experiment_id), ds1, ds2);
    }

        /**
     * Returns the path for the correspondence file with names included.
     * @param ds1
     * @param ds2
     * @param experiment_id
     * @return
     */
    public static String getExperimentCompanyCorrPathTop1(String ds1, String ds2, String experiment_id){
        return String.format("%s%s_%s_corr_w_names_top1.csv", getExperimentRootPath(experiment_id), ds1, ds2);
    }


    public static String getGroupSizeDistPath(String experiment_id, String flag){
        return String.format("%sgroup_size_dist_%s.csv", getExperimentRootPath(experiment_id), flag);
    }

    /**
     * Returns the path of the matching rule evaluation file path for the experiment
     * @param experiment_id
     * @param train_test Specifiy either "test" or "train"
     * @return
     */
    public static String getExperimentEvaluationFilePath(String experiment_id, String train_test){
        if(!(train_test.matches("train") || train_test.matches("test"))){
            //if key not correctly specified change it to "test"
            logger.warn(String.format("Train / Test flag for creating the path was wrongly specified (%s). Using -test- instead", train_test));
            train_test = "test";
        }

        return String.format("%seval_%s.csv", getExperimentRootPath(experiment_id),train_test);
    }

    public static String getExperimentEvaluationFilePath(String experiment_id, String train_test, String flag){
        if(!(train_test.matches("train") || train_test.matches("test"))){
            //if key not correctly specified change it to "test"
            logger.warn(String.format("Train / Test flag for creating the path was wrongly specified (%s). Using -test- instead", train_test));
            train_test = "test";
        }

        return String.format("%seval_%s%s.csv", getExperimentRootPath(experiment_id),train_test, flag);
    }


    /**
     * Returns the root path for an experiment ID.
     * @param experiment_id
     * @return
     */
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


    /**
     * Returns the path for the block size debug file for an experiment
     * @param experiment_id
     * @param ds1
     * @param ds2
     * @return
     */
    public static String getExperimentBlockSizePath(String experiment_id, String ds1, String ds2, int blockerID){
        return String.format("%s%d%s_%s_blockSizeInfo.csv",blockerBasePath,blockerID,ds1,ds2);
        //return String.format("%s%s_%s_blockSizeInfo.csv", getExperimentRootPath(experiment_id),ds1,ds2);
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
    @Deprecated
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


    /**
     * Returns path to file with multiple aggregate similarities. Used during the IR to create the gold standard
     * @param ds1Name
     * @param ds2Name
     * @param blockerIndication
     * @return
     */
    public static String getCombinedMultiSimFilePath(String ds1Name, String ds2Name, String blockerIndication){
        return String.format("data/output/combinedFiles/%s_%s_%s.csv", ds1Name,ds2Name,blockerIndication);
    }


    /**
     * Returns the export path for ML model/matching rule description
     * @param experimentID
     * @return
     */
    public static String getMLModelPath(String experimentID){
        return String.format("%smodel", getExperimentRootPath(experimentID));
    }

    /*Deprecated or outdated methods*/


    /**
     * @deprecated Method that was used during the processing of large kaggle file.
     */
    @Deprecated
    public static String getKagglePath(Integer id) throws FileException{
        if(id > maxKaggleId){
            throw new FileException(String.format("File with ID %d does not exist", id));
        }
        return kaggleBasePath + "kaggle_" +Integer.toString(id) + ".xml";
    }

    
    /**
     * @deprecated Method that was used during the processing of large kaggle file.
     */
    @Deprecated
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
     * @deprecated use getDatasetPath(String dsName) instead.
     * Return dataset path of any dataset. Specify @param id to get the kaggle partitioned file with this id.
     */
    @Deprecated
    public static String getDatasetPath(String dsName, Integer id) throws KeyException{
        if(dsName.matches("kaggle")){
            return getKagglePath(id);
        }

        return getDatasetPath(dsName);
    }

    /**
     * @deprecated was used for processing large XML
     * Gets the path to a reduced kaggle XML
     * @param id
     * @return
     */
    @Deprecated
    public static String getKaggleReducedXMLPath(int id){
        return String.format("%skaggle_%d_red.xml", getKaggleReducedXMLRootPath(), id);
    }

    @Deprecated
    public static String getKaggleReducedXMLRootPath(){
        return redKaggleFolder;
    }

    public static void main(String[] args) throws Exception{
        System.out.println(getExperimentEvaluationFilePath("abc", "nicht test"));
    }
}
