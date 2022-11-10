package de.uni_mannheim.informatik.dws.wdi.IR_Team9.Experiments;

import java.io.File;
import java.io.IOException;
import java.security.KeyException;
import java.time.Duration;
import java.time.LocalDateTime;

import org.apache.commons.lang.time.DurationFormatUtils;
import org.apache.jena.sparql.sse.builders.BuildException;
import org.slf4j.Logger;

import com.fasterxml.jackson.databind.annotation.JsonAppend.Attr;

import de.uni_mannheim.informatik.dws.wdi.IR_Team9.Blocking.CompanyQgramBlocking;
import de.uni_mannheim.informatik.dws.wdi.IR_Team9.model.Company;
import de.uni_mannheim.informatik.dws.wdi.IR_Team9.model.CompanyCSVCorrespondenceFormatter;
import de.uni_mannheim.informatik.dws.wdi.IR_Team9.model.CompanyXMLReader;
import de.uni_mannheim.informatik.dws.wdi.IR_Team9.utils.Constants;
import de.uni_mannheim.informatik.dws.winter.matching.MatchingEngine;
import de.uni_mannheim.informatik.dws.winter.matching.MatchingEvaluator;
import de.uni_mannheim.informatik.dws.winter.matching.blockers.AbstractBlocker;
import de.uni_mannheim.informatik.dws.winter.matching.blockers.Blocker;
import de.uni_mannheim.informatik.dws.winter.matching.blockers.SortedNeighbourhoodBlocker;
import de.uni_mannheim.informatik.dws.winter.matching.blockers.StandardBlocker;
import de.uni_mannheim.informatik.dws.winter.matching.blockers.StandardRecordBlocker;
import de.uni_mannheim.informatik.dws.winter.matching.rules.LinearCombinationMatchingRule;
import de.uni_mannheim.informatik.dws.winter.matching.rules.MatchingRule;
import de.uni_mannheim.informatik.dws.winter.model.Correspondence;
import de.uni_mannheim.informatik.dws.winter.model.HashedDataSet;
import de.uni_mannheim.informatik.dws.winter.model.MatchingGoldStandard;
import de.uni_mannheim.informatik.dws.winter.model.Performance;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;
import de.uni_mannheim.informatik.dws.winter.model.io.CSVCorrespondenceFormatter;
import de.uni_mannheim.informatik.dws.winter.processing.Processable;
import de.uni_mannheim.informatik.dws.winter.utils.WinterLogManager;

public class AbstractExperiment {

    static final Logger logger = WinterLogManager.activateLogger("default");
    static final CompanyXMLReader cr = new CompanyXMLReader();
    static final CompanyCSVCorrespondenceFormatter companyCorrWriter = new CompanyCSVCorrespondenceFormatter();
    static final CSVCorrespondenceFormatter basicCorrWriter = new CSVCorrespondenceFormatter();
    static final MatchingEvaluator<Company, Attribute> evaluator = new MatchingEvaluator<Company, Attribute>();

    
    static boolean addedMatchingRule = false;
    static boolean addedBlocker = false;
    static MatchingRule<Company, Attribute> rule;
    static MatchingEngine<Company,Attribute> engine;
    static Blocker<Company, Attribute, Company, Attribute> blocker;

    static Integer EXPERIMENT_ID; //to be set in the subclasses
    static Integer MATCHING_RULE_ID; //to be set in the subclasses
    static Integer BLOCKER_ID; //to be set in the subclasses
    
    String ds1Name;
    String ds2Name;
    boolean loadedDatasets = false;
    LocalDateTime start;
    LocalDateTime end;
    HashedDataSet<Company, Attribute> ds1;
    HashedDataSet<Company, Attribute> ds2;
    int noCorrespondences;
    MatchingGoldStandard gs = new MatchingGoldStandard();
    Performance perfTrain;
    Performance perfTest;
    double reductionRatio;
    int blockedPairs;
    

    public AbstractExperiment(String ds1Name, String ds2Name) throws Exception{
        this.ds1Name = ds1Name;
        this.ds2Name = ds2Name;
        this.loadData(ds1Name, ds2Name);
    }

    /*
     * Experiment Setup
     */
    void loadData(String ds1Name, String ds2Name) throws Exception{
        // loading data
        logger.info("*\tLoading datasets\t*");
        ds1 = new HashedDataSet<>();
        cr.loadFromXML(new File(Constants.getDatasetPath(ds1Name)), Constants.RECORD_PATH, ds1);


        ds2 = new HashedDataSet<>();
        cr.loadFromXML(new File(Constants.getDatasetPath(ds2Name)), Constants.RECORD_PATH, ds2);
        this.loadedDatasets = true;
    }

    /**
     * This method should be overwritten in the subclass.
     * After Matching rule has been set, super method has to be called and addedMAtchingRule Flag should be put to true.
     */
    static void setupMatchingRule(){
        logger.info("*\tadding matching rule\t*");
        //matchingRule.activateDebugReport(..., 1000);

        addedMatchingRule = true;
    }

    /**
     * This method should be overwritten in the subclass.
     * After Matching rule has been set, super method has to be called and addedBlocker Flag should be put to true.
     */
    static void setupBlocker(){
        logger.info("*\tadding blocker\t*");

        if(blocker instanceof AbstractBlocker){
            AbstractBlocker<Company,Attribute,Attribute> b = (AbstractBlocker) blocker;
            b.collectBlockSizeData(Constants.getExperimentBlockSizePath(EXPERIMENT_ID), 2000);
        }else{
            logger.info("can't collect block size data ...");
        }

        addedBlocker = true;
    }


    /*
     * Run Identity Resolution
     */
    Processable<Correspondence<Company, Attribute>> runIdentityResolution() throws IOException{
        this.start = LocalDateTime.now();
        logger.info("*\tRunning identity resolution\t*");

        Processable<Correspondence<Company, Attribute>> correspondences = engine.runIdentityResolution(ds1, ds2, null, rule, blocker);
        this.noCorrespondences = correspondences.size();

        basicCorrWriter.writeCSV(new File(Constants.getExperimentBasicCorrPath(ds1Name, ds2Name, EXPERIMENT_ID)), correspondences);
        companyCorrWriter.writeCSV(new File(Constants.getExperimentCompanyCorrPath(ds1Name, ds2Name, EXPERIMENT_ID)), correspondences);

        this.end = LocalDateTime.now();

        return correspondences;
    }


    Processable<Correspondence<Company, Attribute>> runExperiment() throws Exception{
        if(this.loadedDatasets && addedBlocker && addedMatchingRule){
            Processable<Correspondence<Company, Attribute>> correspondences = this.runIdentityResolution();
            this.evaluateMatching(correspondences);
            this.evaluateBlocker();

            return correspondences;

        }else{
            throw new BuildException("Blocker or Matching Rule not initialized.");
        }
    }


    void evaluateMatching(Processable<Correspondence<Company, Attribute>> correspondences){
        //Load Train data
        try{
            this.gs.loadFromCSVFile(new File(Constants.getTrainData(this.ds1Name, this.ds2Name)));
            this.perfTrain = evaluator.evaluateMatching(correspondences, this.gs);
            evaluator.writeEvaluation(new File(Constants.getExperimentEvaluationFilePath(EXPERIMENT_ID, "train")), correspondences, this.gs);
    
            //Load Test data
            gs.loadFromCSVFile(new File(Constants.getTestData(this.ds1Name, this.ds2Name)));
            this.perfTest = evaluator.evaluateMatching(correspondences, this.gs);
            evaluator.writeEvaluation(new File(Constants.getExperimentEvaluationFilePath(EXPERIMENT_ID, "train")), correspondences, this.gs);

        }catch(IOException e){
            System.out.println("[WARNING ] could not write evaluation file ...");
            e.printStackTrace();
        }catch(KeyException e){
            System.out.println("[WARNING ] could not load train and test data because dataset name is wrong ...");
            e.printStackTrace();
        }

    }

    void evaluateBlocker(){
        //reduction ration + blocker info
        this.reductionRatio = blocker.getReductionRatio();

        if(blocker instanceof AbstractBlocker){
            AbstractBlocker<Company,Attribute,Attribute> b = (AbstractBlocker) blocker;
            this.blockedPairs = b.getBlockedPairs().size();
        }else{
            this.blockedPairs = -1;
        }
    }

    int getExperimentID(){
        return EXPERIMENT_ID;
    }

    int getMatchingRuleID(){
        return MATCHING_RULE_ID;
    }

    int getBlcokerID(){
        return BLOCKER_ID;
    }

    String getDurationString(){
        return DurationFormatUtils.formatDurationHMS(Duration.between(start, end).toMillis());
    }
}
