package de.uni_mannheim.informatik.dws.wdi.IR_Team9.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import com.github.andrewoma.dexx.collection.List;

import de.uni_mannheim.informatik.dws.wdi.IR_Team9.model.Company;
import de.uni_mannheim.informatik.dws.winter.matching.MatchingEvaluator;
import de.uni_mannheim.informatik.dws.winter.model.Correspondence;
import de.uni_mannheim.informatik.dws.winter.model.HashedDataSet;
import de.uni_mannheim.informatik.dws.winter.model.MatchingGoldStandard;
import de.uni_mannheim.informatik.dws.winter.model.Pair;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;

public class T9MatchingEvaluator extends MatchingEvaluator<Company, Attribute> {

    @Override
    public void writeEvaluation(File f, Collection<Correspondence<Company, Attribute>> correspondences,
			MatchingGoldStandard goldStandard) throws IOException {
		BufferedWriter w = new BufferedWriter(new FileWriter(f));

		// keep a list of all unmatched positives for later output
		List<Pair<String, String>> positives = new ArrayList<Pair<String, String>>(goldStandard.getPositiveExamples());

		for (Correspondence<Company, Attribute> correspondence : correspondences) {
			if (goldStandard.containsPositive(correspondence.getFirstRecord(), correspondence.getSecondRecord())) {
				w.write(String.format("%s,%s,%s,%b\n", 
					correspondence.getFirstRecord().getIdentifier(),
					correspondence.getSecondRecord().getIdentifier(),
					Double.toString(correspondence.getSimilarityScore()),
                    correspondence.getFirstRecord().getName(),
                    correspondence.getSecondRecord().getName(),
					true));

				// remove pair from positives
				Iterator<Pair<String, String>> it = positives.iterator();
				while (it.hasNext()) {
					Pair<String, String> p = it.next();
					String id1 = correspondence.getFirstRecord().getIdentifier();
					String id2 = correspondence.getSecondRecord().getIdentifier();

					if (p.getFirst().equals(id1) && p.getSecond().equals(id2)
							|| p.getFirst().equals(id2) && p.getSecond().equals(id1)) {
						it.remove();
					}
				}
			} else if (goldStandard.isComplete() || goldStandard.containsNegative(correspondence.getFirstRecord(),
					correspondence.getSecondRecord())) {
				w.write(String.format("%s,%s,%s,%b\n", 
					correspondence.getFirstRecord().getIdentifier(),
					correspondence.getSecondRecord().getIdentifier(),
					Double.toString(correspondence.getSimilarityScore()),
                    correspondence.getFirstRecord().getName(),
                    correspondence.getSecondRecord().getName(),
					false));

			}
		}

		// print all missing positive examples
		for (Pair<String, String> p : positives) {
			w.write(String.format("%s,%s,0.0,missing\n", 
					p.getFirst(),
					p.getSecond()));
		}

		w.close();
	}
    
}
