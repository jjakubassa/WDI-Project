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
 * {@link Comparator} for {@link Album}s based on the
 * {@link Album#getTotalTracks()} values, and their
 * {@link DeviationSimilarity} similarity.
 * 
 * @author Oliver Lehmberg (oli@dwslab.de)
 * @author Robert Meusel (robert@dwslab.de)
 * 
 */
public class AlbumTotalTracksComparatorDeviationSimilarity implements Comparator<Album, Attribute> {

	private static final long serialVersionUID = 1L;
//	TokenizingJaccardSimilarity sim = new TokenizingJaccardSimilarity();
	DeviationSimilarity sim = new DeviationSimilarity();
	
	private ComparatorLogger comparisonLog;
	
	@Override
	public double compare(
			Album record1,
			Album record2,
			Correspondence<Attribute, Matchable> schemaCorrespondences) {
		
		Double n1 = (double) record1.getTracks().size();
		Double n2 = (double) record2.getTracks().size();
		
//		Double n1 = record1.getTotalTracks();
//		Double n2 = record2.getTotalTracks();

		// calculate similarity
		double similarity = sim.calculate(n1, n2);

		// postprocessing
		double postSimilarity = 1;
		if (similarity <= 0.3) {
			postSimilarity = 0;
		}

		postSimilarity *= similarity;
		
		if(this.comparisonLog != null){
			this.comparisonLog.setComparatorName(getClass().getName());
			this.comparisonLog.setRecord1Value(Double.toString(n1));
			this.comparisonLog.setRecord1Value(Double.toString(n2));

			// only needed if null values can occur as in getTotalTracks()
//			if (n1 != null) {
//				this.comparisonLog.setRecord1Value(Double.toString(n1));
//			} else {
//				this.comparisonLog.setRecord1Value("NA"); 
//			}
//
//			if (n2 != null) {
//				this.comparisonLog.setRecord1Value(Double.toString(n2));
//			} else {
//				this.comparisonLog.setRecord1Value("NA"); 
//			}
			
    	
			this.comparisonLog.setSimilarity(Double.toString(similarity));
			this.comparisonLog.setPostprocessedSimilarity(Double.toString(postSimilarity));
		}
		return postSimilarity;
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