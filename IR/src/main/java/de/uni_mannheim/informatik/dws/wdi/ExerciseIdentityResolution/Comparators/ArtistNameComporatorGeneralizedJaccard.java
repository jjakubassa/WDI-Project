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
package de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.Comparators;

import de.uni_mannheim.informatik.dws.winter.matching.rules.comparators.Comparator;
import de.uni_mannheim.informatik.dws.winter.matching.rules.comparators.ComparatorLogger;
import de.uni_mannheim.informatik.dws.winter.model.Correspondence;
import de.uni_mannheim.informatik.dws.winter.model.Matchable;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.model.Album;
import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.model.Artist;
import de.uni_mannheim.informatik.dws.winter.similarity.string.LevenshteinSimilarity;
import de.uni_mannheim.informatik.dws.winter.similarity.list.GeneralisedJaccard;
//import de.uni_mannheim.informatik.dws.winter.similarity.list.GeneralisedMaximumOfContainment;


/**
 * {@link Comparator} for {@link Artist}s
 */

public class ArtistNameComporatorGeneralizedJaccard implements Comparator<Album, Attribute> {

	private static final long serialVersionUID = 1L;
	
	private ComparatorLogger comparisonLog;
	
	LevenshteinSimilarity levenshteinSimilarity = new LevenshteinSimilarity();
	private GeneralisedJaccard jaccardSimilarity = new GeneralisedJaccard(levenshteinSimilarity, 0.5);
	
	@Override
	public double compare(
			Album record1,
			Album record2,
			Correspondence<Attribute, Matchable> schemaCorrespondences) {
		
		List<Artist> l1_init = record1.getArtists();
		List l1 = new ArrayList();

		for (Artist a : l1_init) {
//			System.out.println(a.getName());
//			System.out.println(record1.getIdentifier());
			String name = a.getName();
			if (name == null) {
				name = "";
			}
            l1.add(name.toLowerCase());
        }
		
		List<Artist> l2_init = record2.getArtists();
		List l2 = new ArrayList();
		
		for (Artist a : l2_init) {
			String name = a.getName();
			if (name == null) {
				name = "";
			}
            l2.add(name.toLowerCase());
        }
		
		// calculate similarity
		double similarity = jaccardSimilarity.calculate(l1, l2);
		

		// postprocessing
//		double postSimilarity = 1;
//		if (similarity <= 0.3) {
//			postSimilarity = 0;
//		}

//		postSimilarity *= similarity;
		
		String l1_str = l1_init.stream()
			      .map(n -> String.valueOf(n.getName()))
			      .collect(Collectors.joining("-", "{", "}"));
		
		String l2_str = l2_init.stream()
			      .map(n -> String.valueOf(n.getName()))
			      .collect(Collectors.joining("-", "{", "}"));
		
		if(this.comparisonLog != null){
			this.comparisonLog.setComparatorName(getClass().getName());
		
			this.comparisonLog.setRecord1Value(l1_str);
			this.comparisonLog.setRecord2Value(l2_str);
    	
			this.comparisonLog.setSimilarity(Double.toString(similarity));
			this.comparisonLog.setPostprocessedSimilarity(Double.toString(similarity));
		}
		return similarity;
	}

	@Override
	public ComparatorLogger getComparisonLog() {
		return this.comparisonLog;
	}

	@Override
	public void setComparisonLog(ComparatorLogger comparatorLog) {
		this.comparisonLog = comparatorLog;
	}
	

}
