/*
 * Copyright (c) 2017 Data and Web Science Group, University of Mannheim, Germany (http://dws.informatik.uni-mannheim.de/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 */
package de.uni_mannheim.informatik.dws.wdi.IR_Team9.utils;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import de.uni_mannheim.informatik.dws.winter.model.DataSet;
import de.uni_mannheim.informatik.dws.winter.model.Matchable;
import de.uni_mannheim.informatik.dws.winter.model.MatchingGoldStandard;
import de.uni_mannheim.informatik.dws.winter.model.Pair;
import de.uni_mannheim.informatik.dws.winter.utils.WinterLogManager;
//import de.uni_mannheim.informatik.dws.winter.utils.LogUtil;

/**
 * Class representing a gold standard data.
 * 
 * @author Oliver Lehmberg (oli@dwslab.de)
 * 
 */
public class LabeledMatchingGoldStandard extends MatchingGoldStandard {

	private static final Logger logger = WinterLogManager.getLogger();


	/**
	 * Loads a gold standard from a CSV file
	 * 
	 * @param file
	 * @throws IOException
	 */
	@Override
	public void loadFromCSVFile(File file) throws IOException {
		CSVReader reader = new CSVReader(new FileReader(file));

		readAllLines(reader);

		reader.close();

		printGSReport();
	}

	/**
	 * Read all lines. Add positive and negative examples.
	 *
	 * @param reader
	 * @throws IOException
	 */
	private void readAllLines(CSVReader reader) throws IOException {
		String[] values = null;

		while ((values = reader.readNext()) != null) {

			if (values.length == 4) {

				boolean isPositive = Integer.parseInt(values[2]) == 1;

				Pair<String, String> example = new Pair<String, String>(
						values[0], values[1]);

				if (isPositive) {
					addPositiveExample(example);
				} else {
					addNegativeExample(example);
				}

			} else {
				logger.error(String.format("Skipping malformed line: %s",
						StringUtils.join(values,",")));
			}
		}
	}
}
