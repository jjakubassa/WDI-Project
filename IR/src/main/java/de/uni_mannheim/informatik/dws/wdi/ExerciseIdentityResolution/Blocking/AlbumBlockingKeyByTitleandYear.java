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

package de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.Blocking;

import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.model.Album;
import de.uni_mannheim.informatik.dws.winter.matching.blockers.generators.BlockingKeyGenerator;
import de.uni_mannheim.informatik.dws.winter.matching.blockers.generators.RecordBlockingKeyGenerator;
import de.uni_mannheim.informatik.dws.winter.model.Correspondence;
import de.uni_mannheim.informatik.dws.winter.model.Matchable;
import de.uni_mannheim.informatik.dws.winter.model.Pair;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;
import de.uni_mannheim.informatik.dws.winter.processing.DataIterator;
import de.uni_mannheim.informatik.dws.winter.processing.Processable;

/**
 * 
 * @author Oliver Lehmberg (oli@dwslab.de)
 * 
 */
public class AlbumBlockingKeyByTitleandYear extends
		RecordBlockingKeyGenerator<Album, Attribute> {

	private static final long serialVersionUID = 1L;


	/* (non-Javadoc)
	 * @see de.uni_mannheim.informatik.wdi.matching.blocking.generators.BlockingKeyGenerator#generateBlockingKeys(de.uni_mannheim.informatik.wdi.model.Matchable, de.uni_mannheim.informatik.wdi.model.Result, de.uni_mannheim.informatik.wdi.processing.DatasetIterator)
	 */
	@Override
	public void generateBlockingKeys(Album record, Processable<Correspondence<Attribute, Matchable>> correspondences,
			DataIterator<Pair<String, Album>> resultCollector) {
		

	    String[] titleTokens = record.getTitle().replaceAll("[^a-zA-Z0-9\\s]", "").split(" ");
	    String normalizedTitle = "";
	    for (int i = 0; i < Math.min(3, titleTokens.length); i++) {
	        normalizedTitle += titleTokens[i].substring(0, Math.min(2, titleTokens[i].length())).toUpperCase();
	    }

	    int releaseYear = record.getReleaseDate().getYear();
	    
//	    String normalizedArtist = record.getArtist().replaceAll("[^a-zA-Z0-9\\s]", "").substring(0, 2).toUpperCase();
//	    String blockingKeyValue = normalizedTitle + normalizedArtist + releaseYear;	    
	    
	    String blockingKeyValue = normalizedTitle + releaseYear;
	    
		resultCollector.next(new Pair<>(blockingKeyValue, record));
	}

}
