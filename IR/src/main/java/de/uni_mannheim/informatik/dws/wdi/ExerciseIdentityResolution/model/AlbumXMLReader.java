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


import java.util.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.stream.Collectors;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;
import de.uni_mannheim.informatik.dws.winter.model.io.XMLMatchableReader;
/**
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
		String title = getValueFromChildElement(node, "Title");
		Album.setTitle(stripQuotes(title)); 
		
		// Album.setReleaseDate(getValueFromChildElement(node, "ReleaseDate"));
		
		String country = getValueFromChildElement(node, "Country");
		Album.setCountry(stripQuotes(country));
		
		String language = getValueFromChildElement(node, "Language");
		Album.setLanguage(stripQuotes(language));
		
		String artists = getValueFromChildElement(node, "Artists");
		if (artists != null){
			String replace = artists.replace("[","");
			String replace1 = replace.replace("]","");
			// List<String> strList = new ArrayList<String>(Arrays.asList(replace1.split(",")));
			List<String> strList = new ArrayList<String>(Arrays.asList(replace1.split("'")));
			List<Artist> artistList = strList.stream().map(a -> new Artist(stripQuotes(a))).collect(Collectors.toList());
			Album.setArtists(artistList);

			List<Artist> ArtistList = new ArrayList<Artist>();
			for (String s : artists.split("\n")){
				String s_normalized = stripQuotes(s.replace("\n","").trim());
				Artist a = new Artist(s_normalized);
				a.setName(s_normalized);
				// System.out.println(a.getName());
				ArtistList.add(a);
			}
			
			Album.setArtists(ArtistList);
		}

		String tracks = getValueFromChildElement(node, "Tracks");
		if (tracks != null){

			List<Track> trackList = new ArrayList<Track>();
			for (String s : tracks.split("\n")){
				String s_normalized = stripQuotes(s.replace("\n","").trim());
				Track t = new Track(s_normalized);
				t.setName(s_normalized);
				// System.out.println(t.getName());
				trackList.add(t);
			}
			Album.setTracks(trackList);
		}

		String labels = getValueFromChildElement(node, "Labels");
		if (labels != null){
			String replace = labels.replace("[","");
			String replace1 = replace.replace("]","");
			List<String> labelList = new ArrayList<String>(Arrays.asList(replace1.split(",")));
			labelList = labelList.stream().map(l -> stripQuotes(l)).collect(Collectors.toList());
			Album.setLabels(labelList);
		}

		String genres = getValueFromChildElement(node, "Genres");
		if (genres != null){
			String replace = genres.replace("[","");
			String replace1 = replace.replace("]","");
			List<String> genreList = new ArrayList<String>(Arrays.asList(replace1.split(",")));
			genreList = genreList.stream().map(g -> stripQuotes(g)).collect(Collectors.toList());
			Album.setGenres(genreList);
		}

        
        // Deal with missing values
		String TotalTracksStr  = getValueFromChildElement(node, "TotalTracks");
        if (TotalTracksStr != null) {
        	Double TotalTracks = (double) Integer.parseInt(getValueFromChildElement(node, "TotalTracks"));
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

	private String stripQuotes(String title) {
		if (title == null) {
			return null;
		}
		else {
			title = title.replaceFirst("[\"']+$", "").replaceFirst("^[\"']+", ""); 
			// System.out.println(title);
			return title;
		}
	}

}
