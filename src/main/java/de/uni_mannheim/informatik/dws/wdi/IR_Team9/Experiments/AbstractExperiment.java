package de.uni_mannheim.informatik.dws.wdi.IR_Team9.Experiments;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.security.KeyException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.apache.jena.sparql.sse.builders.BuildException;
import org.slf4j.Logger;

import au.com.bytecode.opencsv.CSVReader;
import de.uni_mannheim.informatik.dws.wdi.IR_Team9.Comparators.MATCHING_RULES;
import de.uni_mannheim.informatik.dws.wdi.IR_Team9.model.Company;
import de.uni_mannheim.informatik.dws.wdi.IR_Team9.model.CompanyCSVCorrespondenceFormatter;
import de.uni_mannheim.informatik.dws.wdi.IR_Team9.model.CompanyXMLReader;
import de.uni_mannheim.informatik.dws.wdi.IR_Team9.utils.Constants;
import de.uni_mannheim.informatik.dws.winter.datafusion.CorrespondenceSet;
import de.uni_mannheim.informatik.dws.winter.matching.MatchingEngine;
import de.uni_mannheim.informatik.dws.winter.matching.MatchingEvaluator;
import de.uni_mannheim.informatik.dws.winter.matching.algorithms.MaximumBipartiteMatchingAlgorithm;
import de.uni_mannheim.informatik.dws.winter.matching.blockers.AbstractBlocker;
import de.uni_mannheim.informatik.dws.winter.matching.blockers.Blocker;
import de.uni_mannheim.informatik.dws.winter.matching.rules.LinearCombinationMatchingRule;
import de.uni_mannheim.informatik.dws.winter.matching.rules.MatchingRule;
import de.uni_mannheim.informatik.dws.winter.matching.rules.WekaMatchingRule;
import de.uni_mannheim.informatik.dws.winter.model.Correspondence;
import de.uni_mannheim.informatik.dws.winter.model.FusibleDataSet;
import de.uni_mannheim.informatik.dws.winter.model.FusibleHashedDataSet;
import de.uni_mannheim.informatik.dws.winter.model.HashedDataSet;
import de.uni_mannheim.informatik.dws.winter.model.MatchingGoldStandard;
import de.uni_mannheim.informatik.dws.winter.model.Performance;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;
import de.uni_mannheim.informatik.dws.winter.model.io.CSVCorrespondenceFormatter;
import de.uni_mannheim.informatik.dws.winter.processing.Processable;
import de.uni_mannheim.informatik.dws.winter.utils.WinterLogManager;

public abstract class AbstractExperiment {

    static final Logger logger = WinterLogManager.activateLogger("default");
    static final CompanyXMLReader cr = new CompanyXMLReader();
    static final CompanyCSVCorrespondenceFormatter companyCorrWriter = new CompanyCSVCorrespondenceFormatter();
    static final CSVCorrespondenceFormatter basicCorrWriter = new CSVCorrespondenceFormatter();
    static final MatchingEvaluator<Company, Attribute> evaluator = new MatchingEvaluator<Company, Attribute>();
    static final MatchingEngine<Company,Attribute> engine = new MatchingEngine<>();

    
    static FusibleHashedDataSet<Company, Attribute> ds1cached = null;
    static String ds1Namecached = null;

    static FusibleHashedDataSet<Company, Attribute> ds2cached = null;
    static String ds2Namecached = null;

    
    boolean addedMatchingRule = false;
    boolean addedBlocker = false;

    MatchingRule<Company, Attribute> rule;
    Blocker<Company, Attribute, Company, Attribute> blocker;

    Integer EXPERIMENT_ID; //to be set in the subclasses
    Integer MATCHING_RULE_ID; //to be set in the subclasses
    Integer BLOCKER_ID; //to be set in the subclasses

    double[] threshsToEvaluateForLinearMatchingRules;
    Set<Double> threshs; 

    String ds1Name;
    String ds2Name;
    boolean loadedDatasets = false;
    LocalDateTime start;
    LocalDateTime end;
    FusibleHashedDataSet<Company, Attribute> ds1;
    FusibleHashedDataSet<Company, Attribute> ds2;
    int noCorrespondences;
    int noCorrespondencesTop1;
    MatchingGoldStandard gsTrain = new MatchingGoldStandard();
    MatchingGoldStandard gsTest = new MatchingGoldStandard();
    Performance perfTrain;
    Performance perfTest;
    Performance perfTestTop1;
    double reductionRatio;
    int blockedPairs;
    double matchingThresh;
    double matchingThresh_;
    

    LocalDateTime timeStarted;
    int maxNumComparisons;

    boolean isDS1cached = false;
    boolean isDS2cached = false;
    

    public AbstractExperiment(String ds1Name, String ds2Name, int experimentID, double matchingThresh) throws Exception{
        this.ds1Name = ds1Name;
        this.ds2Name = ds2Name;
        this.EXPERIMENT_ID = experimentID;
        this.matchingThresh = matchingThresh;
        this.timeStarted = LocalDateTime.now();
    }


    void orderAndValidateAllThreshs(){
        /*
         * make sure that threshs are in order, and that there are no thresh smaller than the evaluation thresh passed.
         */
        this.matchingThresh_ = this.matchingThresh;

        if(this.rule instanceof LinearCombinationMatchingRule){

            this.threshs = new TreeSet<>(Arrays.asList(ArrayUtils.toObject(this.threshsToEvaluateForLinearMatchingRules)));
            this.threshs.add(this.matchingThresh);

            Double minThresh = this.threshs.stream().min(Double::compareTo).orElse(this.matchingThresh);

            this.matchingThresh = minThresh;
            this.setMatchingRule(MATCHING_RULES.getRuleByID(MATCHING_RULE_ID, this.matchingThresh, 0.5, ds1, ds2, gsTrain), MATCHING_RULE_ID);

            logger.info("min thresh is " + minThresh.toString());
            logger.info(String.format("Final Matching threshold of matching rule is %f", this.rule.getFinalThreshold()));
        }else{
            logger.info("non linear combination matching rule, only considering main threshold");
            this.threshs = new TreeSet<Double>(Arrays.asList(new Double[]{Double.valueOf(this.matchingThresh)}));
        }
    }


    /*
     * Experiment Setup
     */
    void loadData(String ds1Name, String ds2Name) throws Exception{
        //ds1
        //check cache
        if(!isCached(1, ds1Name)){
            //clear cache
            clearCache(1);

            // loading data
            logger.info("*\tLoading datasets\t*");
            this.ds1 = new FusibleHashedDataSet<>();
            cr.loadFromXML(new File(Constants.getDatasetPath(ds1Name)), Constants.RECORD_PATH, ds1);
        }else{
            this.useCachedDS(1);
            logger.info("Using cleared cache for ds1 ...");
        }


        //ds2
        if(!isCached(2, ds2Name)){
            //clear cache
            clearCache(2);

            // loading data
            logger.info("*\tLoading datasets\t*");
            this.ds2 = new FusibleHashedDataSet<>();
            cr.loadFromXML(new File(Constants.getDatasetPath(ds2Name)), Constants.RECORD_PATH, ds2);
        }else{
            this.useCachedDS(2);
            logger.info("Using cleared cache for ds1 ...");
        }


        this.loadedDatasets = true;
        this.maxNumComparisons = this.ds1.size() * this.ds2.size();
        logger.info(String.format("Max number of comparisons would be %d", this.maxNumComparisons));
    }


    static void clearCache(int position){
        if(position == 1 ){ ds1cached = null; ds1Namecached = null;}
        else{ds2cached = null; ds2Namecached = null;}
    }

    void useCachedDS(int position){
        if(position == 1){
            this.ds1 = ds1cached;
            clearCache(1);
        }else{
            this.ds2 = ds2cached;
            clearCache(2);
        }
    }


    void initializeExperiment(){
        this.loadTrainTestData();
        this.setupBlocker();
        this.setupMatchingRule();
    };

    /**
     * This method should be overwritten in the subclass.
     * After Matching rule has been set, super method has to be called and addedMAtchingRule Flag should be put to true.
     */
    void setupMatchingRule(){
        logger.info("*\tadding matching rule\t*");
        //matchingRule.activateDebugReport(..., 1000);

        this.addedMatchingRule = true;
    }

    /**
     * This method should be overwritten in the subclass.
     * After Matching rule has been set, super method has to be called and addedBlocker Flag should be put to true.
     */
    void setupBlocker(){
        logger.info("*\tadding blocker\t*");

        if(blocker instanceof AbstractBlocker){
            AbstractBlocker<Company,Attribute,Attribute> b = (AbstractBlocker<Company,Attribute,Attribute>) blocker;
            b.collectBlockSizeData(Constants.getExperimentBlockSizePath(this.toString(), this.ds1Name, this.ds2Name, this.BLOCKER_ID), 6000);


            //root path will be determined by mr id, blocker id and thresh e.g. 1_7_85
        }else{
            logger.info("can't collect block size data ...");
        }

        this.addedBlocker = true;
    }


    void evaluateForAllThreshs(Processable<Correspondence<Company, Attribute>> correspondences) throws IOException{
        String experimentID;

        for(Double thresh : this.threshs){
            this.matchingThresh = thresh;
            correspondences = correspondences.where(c -> c.getSimilarityScore() >= this.matchingThresh);
            this.noCorrespondences = correspondences.size();

            experimentID = AbstractExperiment.getID(this.ds1Name, this.ds2Name, this.MATCHING_RULE_ID, this.BLOCKER_ID, this.matchingThresh);

            //write correspondences
            basicCorrWriter.writeCSV(new File(Constants.getExperimentBasicCorrPath(this.ds1Name, this.ds2Name, experimentID)), correspondences);
            companyCorrWriter.writeCSV(new File(Constants.getExperimentCompanyCorrPath(this.ds1Name, this.ds2Name, experimentID)), correspondences);

            this.evaluateMatching(correspondences, "All", experimentID);

        
            //get top 1 global correspondences
            MaximumBipartiteMatchingAlgorithm<Company,Attribute> maxWeight = new MaximumBipartiteMatchingAlgorithm<>(correspondences);
            maxWeight.run();
            Processable<Correspondence<Company, Attribute>> correspondencesTop1 = maxWeight.getResult();
    
            this.noCorrespondencesTop1 = correspondencesTop1.size();
    
            companyCorrWriter.writeCSV(new File(Constants.getExperimentCompanyCorrPathTop1(ds1Name, ds2Name, experimentID)), correspondencesTop1);
            basicCorrWriter.writeCSV(new File(Constants.getExperimentBasicCorrPath(ds1Name, ds2Name, experimentID)), correspondencesTop1);

    
            this.evaluateMatching(correspondencesTop1, "Top1", experimentID);

            ExperimentWriter.appendToExperimentCSV(this);
        }

        this.matchingThresh = this.matchingThresh_;

    }


    /*
     * Run Identity Resolution
     */
    Processable<Correspondence<Company, Attribute>> runIdentityResolution() throws IOException{
        this.start = LocalDateTime.now();
        logger.info("*\tRunning identity resolution\t*");

        Processable<Correspondence<Company, Attribute>> correspondences = engine.runIdentityResolution(this.ds1, this.ds2, null, this.rule, this.blocker);
        this.end = LocalDateTime.now();


        if(this.rule instanceof WekaMatchingRule){
            WekaMatchingRule<Company, Attribute> r = (WekaMatchingRule<Company, Attribute>) rule;
            logger.info(String.format("Matching rule is:\n%s", r.getModelDescription()));
        }

        correspondences = correspondences.distinct(); //due to otherwise duplicate correspondences with sorted neighborhood

        logger.info("Starting evaluation of experiment");
        this.evaluateBlocker();

        if(this.threshs != null){
            this.evaluateForAllThreshs(correspondences);
        }

        correspondences = correspondences.where(c -> c.getSimilarityScore() > this.matchingThresh_);

        
        // this.noCorrespondences = correspondences.size();

        // basicCorrWriter.writeCSV(new File(Constants.getExperimentBasicCorrPath(ds1Name, ds2Name, this.toString())), correspondences);
        // companyCorrWriter.writeCSV(new File(Constants.getExperimentCompanyCorrPath(ds1Name, ds2Name, this.toString())), correspondences);

        // this.evaluateMatching(correspondences, "All");

        
        // //get top 1 global correspondences
        // MaximumBipartiteMatchingAlgorithm<Company,Attribute> maxWeight = new MaximumBipartiteMatchingAlgorithm<>(correspondences);
        // maxWeight.run();
        // Processable<Correspondence<Company, Attribute>> top1correspondences = maxWeight.getResult();

        // this.noCorrespondencesTop1 = top1correspondences.size();


        // basicCorrWriter.writeCSV(new File(Constants.getExperimentBasicCorrPathTop1(ds1Name, ds2Name, this.toString())), correspondences);
        // companyCorrWriter.writeCSV(new File(Constants.getExperimentCompanyCorrPathTop1(ds1Name, ds2Name, this.toString())), correspondences);

        // this.evaluateMatching(top1correspondences, "Top1");

        return correspondences;
    }


    public Processable<Correspondence<Company, Attribute>> runExperiment() throws Exception{
        //make sure everything is run in the correct order
        this.orderAndValidateAllThreshs();

        if(this.loadedDatasets && addedBlocker && addedMatchingRule){
            logger.info("Starting identity resolution for " + this.toString());
            Processable<Correspondence<Company, Attribute>> correspondences = this.runIdentityResolution();

            if(this.rule instanceof WekaMatchingRule){
                WekaMatchingRule<Company, Attribute> wekaRule = (WekaMatchingRule<Company, Attribute>) this.rule;
                try{
                    //if weka Matching rule write model to file
                    wekaRule.exportModel(new File(Constants.getMLModelPath(this.toString())));
                }catch(Exception e){
                    logger.warn("Could not write ML model to file for experiment " + this.toString());
                }
            }


            this.cacheDatasets();

            return correspondences;

        }else{
            throw new BuildException("Blocker or Matching Rule not initialized.");
        }
    }

    /**
     * caches the datasets from the current experiment.
     */
    private void cacheDatasets() {
        ds1cached = ds1;
        ds1 = null;
        ds1Namecached = ds1Name;
        
        ds2cached = ds2;
        ds2 = null;
        ds2Namecached = ds2Name;
    }

    /**
     * checks cache.
     */

     private static boolean isCached(int position, String dsName){
        switch(position){
            case 1: return ds1Namecached.matches(dsName);
            case 2: return ds2Namecached.matches(dsName);
            default: return false;
        }
     }


    void loadTrainTestData(){
        try{
            this.gsTrain.loadFromCSVFile(new File(Constants.getTrainData(this.ds1Name, this.ds2Name)));
            //Load Test data
            this.gsTest.loadFromCSVFile(new File(Constants.getTestData(this.ds1Name, this.ds2Name)));
        }catch(KeyException e){
            System.out.println("[WARNING ] could not load train and test data because dataset name is wrong ..." + e.getMessage());
        }catch(IOException e){
            System.out.println("[WARNING ] could not read train test data ..." + e.getMessage());
        }
    }


    void evaluateMatching(Processable<Correspondence<Company, Attribute>> correspondences, String flag){
        this.evaluateMatching(correspondences, flag, this.toString());
    }


    void evaluateMatching(Processable<Correspondence<Company, Attribute>> correspondences, String flag, String experimentID){
        
        try{
            //Evaluate Train data
            this.perfTrain = evaluator.evaluateMatching(correspondences, this.gsTrain);
            evaluator.writeEvaluation(new File(Constants.getExperimentEvaluationFilePath(experimentID, "train", "")), correspondences, this.gsTrain);
    
            //Evaluate Test data
            if(flag.matches("All")){
                this.perfTest = evaluator.evaluateMatching(correspondences, this.gsTest);
                evaluator.writeEvaluation(new File(Constants.getExperimentEvaluationFilePath(experimentID, "test", flag)), correspondences, this.gsTest);
            }else{
                this.perfTestTop1 = evaluator.evaluateMatching(correspondences, this.gsTest);
                evaluator.writeEvaluation(new File(Constants.getExperimentEvaluationFilePath(experimentID, "test", flag)), correspondences, this.gsTest);
            }

            
        }catch(IOException e){
            System.out.println("[WARNING ] could not write evaluation file ...");
            e.printStackTrace();
        }


        try{
            CorrespondenceSet<Company, Attribute> cSet = new CorrespondenceSet<Company, Attribute>();

            cSet.loadCorrespondences(new File(Constants.getExperimentBasicCorrPath(ds1Name, ds2Name, experimentID)),this.ds1, this.ds2);

            logger.info("Writing group size dist to " + Constants.getGroupSizeDistPath(experimentID, flag));
            cSet.writeGroupSizeDistribution(new File(Constants.getGroupSizeDistPath(experimentID, flag)));

        }catch(IOException e){
            logger.warn("Could not evaluate correspondences ...");
            e.printStackTrace();
        }
        
    }

    void evaluateBlocker(){
        //reduction ration + blocker info
        this.reductionRatio = this.blocker.getReductionRatio();
        this.blockedPairs = Double.valueOf(Integer.valueOf(this.maxNumComparisons).doubleValue() * (1. - this.reductionRatio)).intValue();
        logger.info(String.format("%d", this.blockedPairs));
    }

    abstract void setBlocker(Blocker<Company, Attribute, Company, Attribute> blocker, Integer blockerID);
    abstract void setMatchingRule(MatchingRule<Company, Attribute> rule, Integer ruleID);

    @Deprecated
    int getExperimentID(){
        return EXPERIMENT_ID;
    }

    int getMatchingRuleID(){
        return MATCHING_RULE_ID;
    }

    int getBlockerID(){
        return BLOCKER_ID;
    }

    String getDurationString(){
        return DurationFormatUtils.formatDurationHMS(Duration.between(start, end).toMillis());
    }

    public String toString(){
        String thresh = Double.toString(this.matchingThresh).split("\\.")[1];
        if(thresh.length()>=2){
            thresh = Double.toString(this.matchingThresh).split("\\.")[1].substring(0,2);
        }else{
            thresh = Double.toString(this.matchingThresh).split("\\.")[1].substring(0,1);
        }

        return String.format("%d_%d_%s_%s_%s",this.MATCHING_RULE_ID, this.BLOCKER_ID, thresh, this.ds1Name, this.ds2Name);
    }

    


    /*EXPERIMENT MANAGEMENT */

    /**
     * Given a set of experiment ids, determines if the experiment to test was already conducted
     * @param ds1Name
     * @param ds2Name
     * @param ruleID
     * @param blockerID
     * @param thresh
     * @param conductedExp
     * @return
     */
    static boolean hasAlreadyRun(String ds1Name, String ds2Name, int ruleID, int blockerID, double thresh, Set<String> conductedExp){
        String id = getID(ds1Name, ds2Name, ruleID, blockerID, thresh);

        return conductedExp.contains(id);
    }

    /**
     * Makes ID from experiment input parameters
     * @param ds1Name
     * @param ds2Name
     * @param ruleID
     * @param blockerID
     * @param matchingThresh
     * @return
     */
    public static String getID(String ds1Name, String ds2Name, int ruleID, int blockerID, double matchingThresh){
        String thresh = Double.toString(matchingThresh).split("\\.")[1];
        if(thresh.length()>=2){
            thresh = Double.toString(matchingThresh).split("\\.")[1].substring(0,2);
        }else{
            thresh = Double.toString(matchingThresh).split("\\.")[1].substring(0,1);
        }

        return String.format("%d_%d_%s_%s_%s",ruleID, blockerID, thresh, ds1Name, ds2Name);
    }


    /**
     * Reads experiment log IDs to a set.
     * @return the set of IDs
     */
    public static Set<String> getConductedExperiments(){

        Set<String> exp = new HashSet<String>();

        try(CSVReader reader = new CSVReader(new FileReader(Constants.getExperimentLogPath()))){
            List<String[]> lines = reader.readAll();

            for(String[] line : lines){
                exp.add(line[0]);
            }
        }catch(IOException e){
            e.printStackTrace();
        }

        return exp;
    }
}
