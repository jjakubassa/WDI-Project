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
package de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.model;

import org.slf4j.Logger;


import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.Locale;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;
import de.uni_mannheim.informatik.dws.winter.model.io.XMLMatchableReader;
import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.model.XMLMatchableReaderID;
/**
 * A {@link XMLMatchableReader} for {@link Actor}s.
 * 
 * @author Oliver Lehmberg (oli@dwslab.de)
 * 
 */
public class AlbumXMLReader extends XMLMatchableReader<Album, Attribute> {

	@Override
	public Album createModelFromElement(Node node, String provenanceInfo) {
		NamedNodeMap id_attribute = node.getAttributes();
		String id = id_attribute.getNamedItem("id").getNodeValue();
		
		// create the object with id and provenance information
		Album Album = new Album(id, provenanceInfo);

		// fill the attributes
		Album.setTitle(getValueFromChildElement(node, "Title")); 
		
		// Album.setReleaseDate(getValueFromChildElement(node, "ReleaseDate"));
		
		
		Album.setCountry(getValueFromChildElement(node, "Country"));
		Album.setLanguage(getValueFromChildElement(node, "Language"));
		Album.setGenre(getValueFromChildElement(node, "Genre"));
		// TODO artists, tracks, labels
		String artists = getValueFromChildElement(node, "Artists");
		if (artists != null){
			String replace = artists.replace("[","");
			System.out.println(replace);
			String replace1 = replace.replace("]","");
			System.out.println(replace1);
			List<String> artistList = new ArrayList<String>(Arrays.asList(replace1.split(",")));
			Album.setArtists(artistList);
		}

		// Album.setTracks(getValueFromChildElement(node, "Tracks"));
		// Album.setLabels(getValueFromChildElement(node, "Labels"));

        
        // Deal with missing values
		String TotalTracksStr  = getValueFromChildElement(node, "TotalTracks");
        if (TotalTracksStr != null) {
        	int TotalTracks = Integer.parseInt(getValueFromChildElement(node, "TotalTracks"));
        	Album.setTotalTracks(TotalTracks);
        } 

        
        String DurationStr  = getValueFromChildElement(node, "Duration");
        if (DurationStr != null) {
        	int Duration = Integer.parseInt(getValueFromChildElement(node, "Duration"));
        	Album.setDuration(Duration);
        }
		
        String PriceStr  = getValueFromChildElement(node, "Price");
        if (PriceStr != null) {
        	double Price = Double.parseDouble(getValueFromChildElement(node, "Price"));
        	Album.setPrice(Price);
        }
        
		
		
		// convert the date string into a DateTime object
		try {
			String date = getValueFromChildElement(node, "ReleaseDate");
			if (date == "") {
				date = null;
			}
			
			if (date != null) {
				if (date.length() == 4) {
					date = date.concat("-02-30");
				}
				if (date.length() == 7) {
					date = date.concat("-01"); //TODO how to deal with this?
				}
				DateTimeFormatter formatter = new DateTimeFormatterBuilder()
				        .appendPattern("yyyy-MM-dd")
				        .toFormatter(Locale.ENGLISH);
				LocalDate dt = LocalDate.parse(date, formatter);
				Album.setReleaseDate(dt);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return Album;
	}

}
