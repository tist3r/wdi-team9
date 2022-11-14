package de.uni_mannheim.informatik.dws.wdi.IR_Team9.Experiments;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.security.KeyException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.time.DurationFormatUtils;
import org.apache.jena.sparql.sse.builders.BuildException;
import org.slf4j.Logger;

import au.com.bytecode.opencsv.CSVReader;
import de.uni_mannheim.informatik.dws.wdi.IR_Team9.model.Company;
import de.uni_mannheim.informatik.dws.wdi.IR_Team9.model.CompanyCSVCorrespondenceFormatter;
import de.uni_mannheim.informatik.dws.wdi.IR_Team9.model.CompanyXMLReader;
import de.uni_mannheim.informatik.dws.wdi.IR_Team9.utils.Constants;
import de.uni_mannheim.informatik.dws.winter.matching.MatchingEngine;
import de.uni_mannheim.informatik.dws.winter.matching.MatchingEvaluator;
import de.uni_mannheim.informatik.dws.winter.matching.blockers.AbstractBlocker;
import de.uni_mannheim.informatik.dws.winter.matching.blockers.Blocker;
import de.uni_mannheim.informatik.dws.winter.matching.rules.MatchingRule;
import de.uni_mannheim.informatik.dws.winter.matching.rules.WekaMatchingRule;
import de.uni_mannheim.informatik.dws.winter.model.Correspondence;
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

    boolean addedMatchingRule = false;
    boolean addedBlocker = false;

    MatchingRule<Company, Attribute> rule;
    Blocker<Company, Attribute, Company, Attribute> blocker;

    Integer EXPERIMENT_ID; //to be set in the subclasses
    Integer MATCHING_RULE_ID; //to be set in the subclasses
    Integer BLOCKER_ID; //to be set in the subclasses
    
    String ds1Name;
    String ds2Name;
    boolean loadedDatasets = false;
    LocalDateTime start;
    LocalDateTime end;
    HashedDataSet<Company, Attribute> ds1;
    HashedDataSet<Company, Attribute> ds2;
    int noCorrespondences;
    MatchingGoldStandard gsTrain = new MatchingGoldStandard();
    MatchingGoldStandard gsTest = new MatchingGoldStandard();
    Performance perfTrain;
    Performance perfTest;
    double reductionRatio;
    int blockedPairs;
    double matchingThresh;
    

    public AbstractExperiment(String ds1Name, String ds2Name, int experimentID, double matchingThresh) throws Exception{
        this.ds1Name = ds1Name;
        this.ds2Name = ds2Name;
        this.EXPERIMENT_ID = experimentID;
        this.matchingThresh = matchingThresh;
    }


    /*
     * Experiment Setup
     */
    void loadData(String ds1Name, String ds2Name) throws Exception{
        // loading data
        logger.info("*\tLoading datasets\t*");
        this.ds1 = new HashedDataSet<>();
        cr.loadFromXML(new File(Constants.getDatasetPath(ds1Name)), Constants.RECORD_PATH, ds1);


        this.ds2 = new HashedDataSet<>();
        cr.loadFromXML(new File(Constants.getDatasetPath(ds2Name)), Constants.RECORD_PATH, ds2);
        this.loadedDatasets = true;
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
            b.collectBlockSizeData(Constants.getExperimentBlockSizePath(this.toString(), this.ds1Name, this.ds2Name), 6000);


            //root path will be determined by mr id, blocker id and thresh e.g. 1_7_85
        }else{
            logger.info("can't collect block size data ...");
        }

        this.addedBlocker = true;
    }


    /*
     * Run Identity Resolution
     */
    Processable<Correspondence<Company, Attribute>> runIdentityResolution() throws IOException{
        this.start = LocalDateTime.now();
        logger.info("*\tRunning identity resolution\t*");

        Processable<Correspondence<Company, Attribute>> correspondences = engine.runIdentityResolution(this.ds1, this.ds2, null, this.rule, this.blocker);
        
        if(this.rule instanceof WekaMatchingRule){
            WekaMatchingRule<Company, Attribute> r = (WekaMatchingRule<Company, Attribute>) rule;
            logger.info(String.format("Matching rule is:\n%s", r.getModelDescription()));
        }
        
        this.noCorrespondences = correspondences.size();

        basicCorrWriter.writeCSV(new File(Constants.getExperimentBasicCorrPath(ds1Name, ds2Name, this.toString())), correspondences);
        companyCorrWriter.writeCSV(new File(Constants.getExperimentCompanyCorrPath(ds1Name, ds2Name, this.toString())), correspondences);

        this.end = LocalDateTime.now();

        return correspondences;
    }


    public Processable<Correspondence<Company, Attribute>> runExperiment() throws Exception{
        if(this.loadedDatasets && addedBlocker && addedMatchingRule){
            logger.info("Starting identity resolution for " + this.toString());
            Processable<Correspondence<Company, Attribute>> correspondences = this.runIdentityResolution();

            logger.info("Starting evaluation of experiment");
            this.evaluateMatching(correspondences);
            this.evaluateBlocker();

            //append to file
            ExperimentWriter.appendToExperimentCSV(this);


            if(this.rule instanceof WekaMatchingRule){
                WekaMatchingRule<Company, Attribute> wekaRule = (WekaMatchingRule<Company, Attribute>) this.rule;
                try{
                    //if weka Matching rule write model to file
                    wekaRule.exportModel(new File(Constants.getMLModelPath(this.toString())));
                }catch(Exception e){
                    logger.warn("Could not write ML model to file for experiment " + this.toString());
                }
            }
            
            

            return correspondences;

        }else{
            throw new BuildException("Blocker or Matching Rule not initialized.");
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


    void evaluateMatching(Processable<Correspondence<Company, Attribute>> correspondences){
        
        try{
            //Evaluate Train data
            this.perfTrain = evaluator.evaluateMatching(correspondences, this.gsTrain);
            evaluator.writeEvaluation(new File(Constants.getExperimentEvaluationFilePath(this.toString(), "train")), correspondences, this.gsTrain);
    
            //Evaluate Test data
            this.perfTest = evaluator.evaluateMatching(correspondences, this.gsTest);
            evaluator.writeEvaluation(new File(Constants.getExperimentEvaluationFilePath(this.toString(), "test")), correspondences, this.gsTest);

        }catch(IOException e){
            System.out.println("[WARNING ] could not write evaluation file ...");
            e.printStackTrace();
        }

    }

    void evaluateBlocker(){
        //reduction ration + blocker info
        this.reductionRatio = this.blocker.getReductionRatio();

        if(blocker instanceof AbstractBlocker){
            AbstractBlocker<Company,Attribute,Attribute> b = (AbstractBlocker<Company,Attribute,Attribute>) this.blocker;
            if(b.getBlockedPairs() != null){
                this.blockedPairs = b.getBlockedPairs().size();
            }else{
                this.blockedPairs = -1;
            }
            
        }else{
            this.blockedPairs = -1;
        }
    }

    abstract void setBlocker(Blocker<Company, Attribute, Company, Attribute> blocker, Integer blockerID);
    abstract void setMatchingRule(MatchingRule<Company, Attribute> rule, Integer ruleID);

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
