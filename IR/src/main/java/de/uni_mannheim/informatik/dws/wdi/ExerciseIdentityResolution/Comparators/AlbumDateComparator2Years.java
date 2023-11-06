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
import de.uni_mannheim.informatik.dws.winter.similarity.date.YearSimilarity;
import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.model.Album;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * {@link Comparator} for {@link Movie}s based on the {@link Movie#getDate()}
 * value, with a maximal difference of 2 years.
 * 
 * @author Oliver Lehmberg (oli@dwslab.de)
 * 
 */
public class AlbumDateComparator2Years implements Comparator<Album, Attribute> {

	private static final long serialVersionUID = 1L;
	private YearSimilarity sim = new YearSimilarity(2);
	
	private ComparatorLogger comparisonLog;

	@Override
	public double compare(
			Album record1,
			Album record2,
			Correspondence<Attribute, Matchable> schemaCorrespondences) {
    	
        LocalDateTime releaseDate1 = record1.getReleaseDate().atTime(LocalTime.MIDNIGHT);
        LocalDateTime releaseDate2 = record2.getReleaseDate().atTime(LocalTime.MIDNIGHT);
        
	    
    	double similarity = sim.calculate(releaseDate1, releaseDate2);
    	
		if(this.comparisonLog != null){
			this.comparisonLog.setComparatorName(getClass().getName());
		
			String releaseYear1 = Integer.toString(record1.getReleaseDate().getYear());
            String releaseYear2 = Integer.toString(record2.getReleaseDate().getYear());

            this.comparisonLog.setRecord1Value(releaseYear1);
            this.comparisonLog.setRecord2Value(releaseYear2);
    	
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
