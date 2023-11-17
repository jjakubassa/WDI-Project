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
import de.uni_mannheim.informatik.dws.winter.similarity.numeric.DeviationSimilarity;
import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.model.Album;

/**
 * {@link Comparator} for {@link Movie}s based on the
 * {@link Movie#getDirector()} values, and their
 * {@link TokenizingJaccardSimilarity} similarity, with a lower casing
 * beforehand.
 * 
 * @author Robert Meusel (robert@dwslab.de)
 * @author Oliver Lehmberg (oli@dwslab.de)
 * 
 */
public class TotalTracksComparatorDeviationSimilarity implements Comparator<Album, Attribute> {

	private static final long serialVersionUID = 1L;
	private DeviationSimilarity sim = new DeviationSimilarity();
	
	private ComparatorLogger comparisonLog;

	@Override
	public double compare(
			Album record1,
			Album record2,
			Correspondence<Attribute, Matchable> schemaCorrespondences) {
		
		Double s1;
		Integer totalTracks1 = record1.getTotalTracks();
		if (totalTracks1 != null) {
			s1 = (double) totalTracks1;
		} else {
	    	return 0;
		}
		
		Double s2;
		Integer totalTracks2 = record2.getTotalTracks();
		if (totalTracks2 != null) {
			s2 = (double) totalTracks2;
		} else {
	    	return 0;
		}
		
//		s1 = record1.getTotalTracks();
//		s2 = record2.getTotalTracks();
		
		// calculate similarity
		double similarity = sim.calculate(s1, s2);
		
		if(this.comparisonLog != null){
			this.comparisonLog.setComparatorName(getClass().getName());
			this.comparisonLog.setRecord1Value(Double.toString(s1));
			this.comparisonLog.setRecord2Value(Double.toString(s2));
			
			this.comparisonLog.setSimilarity(Double.toString(similarity));
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
