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
package de.uni_mannheim.informatik.dws.wdi.ExerciseDataFusion.evaluation;

import java.util.HashSet;
import java.util.Set;
import java.util.List;

import de.uni_mannheim.informatik.dws.wdi.ExerciseDataFusion.model.Album;
import de.uni_mannheim.informatik.dws.wdi.ExerciseDataFusion.model.Artist;
import de.uni_mannheim.informatik.dws.wdi.ExerciseDataFusion.model.Movie;
import de.uni_mannheim.informatik.dws.wdi.ExerciseDataFusion.model.Track;
import de.uni_mannheim.informatik.dws.winter.datafusion.EvaluationRule;
import de.uni_mannheim.informatik.dws.winter.model.Correspondence;
import de.uni_mannheim.informatik.dws.winter.model.Matchable;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;
import de.uni_mannheim.informatik.dws.winter.similarity.SimilarityMeasure;
import de.uni_mannheim.informatik.dws.winter.similarity.string.TokenizingJaccardSimilarity;

/**
 * {@link EvaluationRule} for the titles of {@link Album}s. The rule simply
 * compares the titles of two {@link Album}s and returns true, in case their
 * similarity based on {@link TokenizingJaccardSimilarity} is 1.0.
 * 
 * @author Oliver Lehmberg (oli@dwslab.de)
 * 
 */
public class TrackTitlesEvaluationRule extends EvaluationRule<Album, Attribute> {

	SimilarityMeasure<String> sim = new TokenizingJaccardSimilarity();

//	@Override
//	public boolean isEqual(Album record1, Album record2, Attribute schemaElement) {
//		Set<String> tracks1 = new HashSet<>();
//
//		for (Track t : record1.getTracks()) {
//			// remove quotation marks (both single and double) and (Explicit) or [Explicit] (case-insensitive)
//			tracks1.add(t.getName().replaceAll("[\"']|(?i)\\(Explicit\\)|\\[Explicit\\]", "").trim());		   
//		}
//
//		Set<String> tracks2 = new HashSet<>();
//		
//		for (Track t : record2.getTracks()) {
//			tracks2.add(t.getName().replaceAll("[\"']|(?i)\\(Explicit\\)|\\[Explicit\\]", "").trim());		   
//		}
//
//		return tracks1.containsAll(tracks2) && tracks2.containsAll(tracks1);
//	}
	
	@Override
	public boolean isEqual(Album record1, Album record2, Attribute schemaElement) {
        Set<String> tracks1 = cleanAndCreateSet(record1.getTracks());
        Set<String> tracks2 = cleanAndCreateSet(record2.getTracks());

        return tracks1.containsAll(tracks2) && tracks2.containsAll(tracks1);
    }

    private Set<String> cleanAndCreateSet(List<Track> tracks) {
        Set<String> cleanedTracks = new HashSet<>();

        for (Track t : tracks) {
            String cleanedName = cleanTrackName(t.getName());
            cleanedTracks.add(cleanedName);
        }

        return cleanedTracks;
    }

    private String cleanTrackName(String trackName) {
        // Remove quotation marks (both single and double) and anything in parentheses
        trackName = trackName.replaceAll("[\"']", "");
        trackName = trackName.replaceAll("\\([^)]*\\)", "").trim();

        return trackName;
    }
    

	/* (non-Javadoc)
	 * @see de.uni_mannheim.informatik.wdi.datafusion.EvaluationRule#isEqual(java.lang.Object, java.lang.Object, de.uni_mannheim.informatik.wdi.model.Correspondence)
	 */
	@Override
	public boolean isEqual(Album record1, Album record2,
			Correspondence<Attribute, Matchable> schemaCorrespondence) {
		return isEqual(record1, record2, (Attribute)null);
	}
	
}
