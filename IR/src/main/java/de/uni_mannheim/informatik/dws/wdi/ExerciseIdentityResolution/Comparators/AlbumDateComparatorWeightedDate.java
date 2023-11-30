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


import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.model.Album;
import de.uni_mannheim.informatik.dws.winter.matching.rules.comparators.Comparator;
import de.uni_mannheim.informatik.dws.winter.matching.rules.comparators.ComparatorLogger;
import de.uni_mannheim.informatik.dws.winter.model.Correspondence;
import de.uni_mannheim.informatik.dws.winter.model.Matchable;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;
import de.uni_mannheim.informatik.dws.winter.similarity.date.WeightedDateSimilarity;

/**
 * {@link Comparator} for {@link Album}s based on the {@link Album#getReleaseDate()} ()}
 * value. With a maximal difference of 10 years.
 * 
 * @author Oliver Lehmberg (oli@dwslab.de)
 * 
 */
public class AlbumDateComparatorWeightedDate implements Comparator<Album, Attribute> {

	private static final long serialVersionUID = 1L;
	private WeightedDateSimilarity sim = new WeightedDateSimilarity(0.1, 0.3, 0.6);
	
	private ComparatorLogger comparisonLog;

	@Override
	public double compare(
			Album record1,
			Album record2,
			Correspondence<Attribute, Matchable> schemaCorrespondences) {
    	
		if (record1.getReleaseDate() == null | record2.getReleaseDate() == null) {
			return 0.0;
		}
		
		sim.setYearRange(1);
    	double similarity = sim.calculate(record1.getReleaseDate().atStartOfDay(), record2.getReleaseDate().atStartOfDay());

		if(this.comparisonLog != null){
			this.comparisonLog.setComparatorName(getClass().getName());

			this.comparisonLog.setRecord1Value(record1.getReleaseDate().toString());
			this.comparisonLog.setRecord2Value(record2.getReleaseDate().toString());

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
