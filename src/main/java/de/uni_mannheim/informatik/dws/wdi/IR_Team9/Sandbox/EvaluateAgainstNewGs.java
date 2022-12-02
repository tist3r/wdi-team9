package de.uni_mannheim.informatik.dws.wdi.IR_Team9.Sandbox;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import au.com.bytecode.opencsv.CSVReader;
import de.uni_mannheim.informatik.dws.wdi.IR_Team9.Experiments.AbstractExperiment;
import de.uni_mannheim.informatik.dws.wdi.IR_Team9.Experiments.Experiment;
import de.uni_mannheim.informatik.dws.wdi.IR_Team9.model.Company;
import de.uni_mannheim.informatik.dws.wdi.IR_Team9.utils.Constants;
import de.uni_mannheim.informatik.dws.winter.model.Correspondence;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;
import de.uni_mannheim.informatik.dws.winter.processing.Processable;
import de.uni_mannheim.informatik.dws.winter.processing.ProcessableCollection;
import edu.stanford.nlp.util.EditDistance;

public class EvaluateAgainstNewGs {

    /**
     * Reevaluate the correspondences for a certain ds name
     * @param dsName
     * @throws IOException
     */
    public static void reevaluateCorrespondences(String ds1Name, String ds2Name) throws IOException{
        Path dir = Paths.get(Constants.EXPERIMENT_PATH);

        try(DirectoryStream<Path> ds = Files.newDirectoryStream(dir)){
            ds.forEach(x -> reevaluateIfDirectory(x, ds1Name, ds2Name));           
        }
    }

    /**
     * if directory initiates the reevaluation
     * @param p
     */
    private static void reevaluateIfDirectory(Path p, String ds1Name, String ds2Name){
        if(Files.isDirectory(p) && p.getFileName().toString().contains(ds1Name) && p.getFileName().toString().contains(ds2Name)){
            try{parseExperimentAndReevaluate(p);}
            catch(Exception e){e.printStackTrace();}
        }else{
            System.out.println("skipping "+ p.getFileName().toString());
        }
    }

    /**
     * Parses experiment info from experiment folder path and reevaluates against current gold standard
     */
    private static void parseExperimentAndReevaluate(Path p) throws Exception{
        String eID = p.getFileName().toString();
        Pattern pat = Pattern.compile("([0-9]{1,2})_([0-9]{1,2})_([0-9]{1,2})_([a-z]*)_([a-z_]*)");
        Matcher m = pat.matcher(eID);
        if(m.matches()){
            String ds1Name = m.group(4);
            String ds2Name = m.group(5);
            int mrID = Integer.parseInt(m.group(1));
            int blockerID = Integer.parseInt(m.group(2));
            String thresh = m.group(3);

            if(thresh.matches("[2-3]{1}")|| mrID == 3 || mrID == 4){
                return;
            }

            System.out.println(String.format("%d%d%s%s%s",mrID,blockerID,thresh,ds1Name,ds2Name ));
            AbstractExperiment.reevaluateMatching(ds1Name, ds2Name, eID, blockerID, mrID, thresh);
        }
    }


    /**
     * Loads correspondences to processable without company information except identifier
     * @param p
     * @return
     * @throws Exception
     */
    public static Processable<Correspondence<Company, Attribute>> loadCorrespondences(Path p) throws Exception{
        Processable<Correspondence<Company, Attribute>> c = new ProcessableCollection<>();

        Company c1, c2;
        String[] line;
        String id1, id2;
        double sim;
        Correspondence<Company,Attribute> corr;
        try(CSVReader reader = new CSVReader(Files.newBufferedReader(p))){
            while((line = reader.readNext())!=null){
                id1 = line[0];
                id2 = line[1];
                sim = Double.parseDouble(line[2]);

                c1 = new Company(id1, "corr");
                c2 = new Company(id2, "corr");
                corr = new Correspondence<>();
                corr.setFirstRecord(c1);
                corr.setSecondRecord(c2);
                corr.setsimilarityScore(sim);
                c.add(corr);
            }

        }

        return c;
    }
    

    public static void main(String[] args) throws Exception{
        /*
         * 1.loop through all experiments
         * 2. check where dataset combination matches, skip forbes
         * 3. evaluate correspondences against new gold standard
         * 4. new file or experimentlogwriter
         */

        //reevaluateCorrespondences("dw");
        // Experiment e = new Experiment("forbes", "kaggle_f");
        //Processable<Correspondence<Company, Attribute>> corr = loadCorrespondences(Paths.get(Constants.getExperimentBasicCorrPath("forbes", "kaggle_f", "15_10_87_forbes_kaggle_f")));
        // e.evaluateMatching(corr, "test", "15_10_87_forbes_kaggle_f");
        // AbstractExperiment.reevaluateMatching("forbes", "kaggle_f", "15_10_87_forbes_kaggle_f");
        //parseExperiment(Paths.get("data/output/experiments/15_10_87_forbes_kaggle_f"));

        reevaluateCorrespondences("forbes", "kaggle_f");


       
    }
}
