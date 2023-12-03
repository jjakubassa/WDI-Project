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
package de.uni_mannheim.informatik.dws.wdi.ExerciseDataFusion.model;


import java.util.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.stream.Collectors;
import java.time.LocalDateTime;
import java.time.temporal.ChronoField;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.apache.commons.lang3.StringUtils;

import de.uni_mannheim.informatik.dws.winter.model.FusibleFactory;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;
import de.uni_mannheim.informatik.dws.winter.model.io.XMLMatchableReader;
import de.uni_mannheim.informatik.dws.winter.model.DataSet;
import de.uni_mannheim.informatik.dws.winter.model.RecordGroup;
/**
 * 
 * @author Oliver Lehmberg (oli@dwslab.de)
 * 
 */
public class AlbumXMLReader extends XMLMatchableReader<Album, Attribute> implements
FusibleFactory<Album, Attribute> {
	
	@Override
	protected void initialiseDataset(DataSet<Album, Attribute> dataset) {
	super.initialiseDataset(dataset);

		// the schema is defined in the Movie class and not interpreted from the file, so we have to set the attributes manually
		
		dataset.addAttribute(Album.TITLE);
		dataset.addAttribute(Album.RELEASEDATE);
		dataset.addAttribute(Album.PRICE);
		dataset.addAttribute(Album.ARTISTS);
		dataset.addAttribute(Album.TRACKS);
		dataset.addAttribute(Album.LABELS);
		dataset.addAttribute(Album.TOTALTRACKS);
		dataset.addAttribute(Album.GENRE);
		dataset.addAttribute(Album.COUNTRY);
		dataset.addAttribute(Album.LANGUAGE);
		dataset.addAttribute(Album.DURATION);

	}
		
	@Override
	public Album createModelFromElement(Node node, String provenanceInfo) {
		NamedNodeMap id_attribute = node.getAttributes();
		Node id_node = id_attribute.getNamedItem("id");

		String id;
		// deal with fused.xml
		if (id_node == null) {
			id = getValueFromChildElement(node, "id");
		}
		else {
			id = id_attribute.getNamedItem("id").getNodeValue();
		}
		
		// create the object with id and provenance information
		Album Album = new Album(id, provenanceInfo);

		// fill the attributes
		String title = getValueFromChildElement(node, "Title");
		Album.setTitle(stripQuotes(title)); 
		// if (title == null) {
		// 	System.out.println("Title: " + Album.getTitle());
		// }

		// Album.setReleaseDate(getValueFromChildElement(node, "ReleaseDate"));
		
		String country = getValueFromChildElement(node, "Country");
		
		if (country != null) {
			Album.setCountry(stripQuotes(country));
		}
		
		String language = getValueFromChildElement(node, "Language");
		if (language != null) {
			Album.setLanguage(stripQuotes(language));
		}
		
		String artists = getValueFromChildElement(node, "Artists");
		if (artists != null){
			String replace = artists.replace("[","");
			String replace1 = replace.replace("]","");
			// List<String> strList = new ArrayList<String>(Arrays.asList(replace1.split(",")));
			List<String> strList = new ArrayList<String>(Arrays.asList(replace1.split("'")));
			List<Artist> artistList = strList.stream().map(a -> new Artist(stripQuotes(a))).collect(Collectors.toList());
			Album.setArtists(artistList);

			List<Artist> ArtistList = new ArrayList<Artist>();
			for (String s : artists.split("[\n,&]| and ")){
				String s_normalized = stripQuotes(s.replace("\n","").trim());
				Artist a = new Artist(s_normalized);
				a.setName(s_normalized);
				// System.out.println(a.getName());
				ArtistList.add(a);
			}
			
			Album.setArtists(ArtistList);
		}
		else {
			Album.setArtists(null);
		}
		// print artist
		// if (Album.getArtists() == null) {
		// 	System.out.println("Artists: " + Album.getArtists());
		// }


		String tracks = getValueFromChildElement(node, "Tracks");
		if (tracks != null){

			String replace = tracks.replace("[","");
			String replace1 = replace.replace("]","");
			// List<String> strList = new ArrayList<String>(Arrays.asList(replace1.split(",")));
			List<String> strList = new ArrayList<String>(Arrays.asList(replace1.split("'")));
			List<Track> trackList = strList.stream().map(a -> new Track(stripQuotes(a))).collect(Collectors.toList());
			Album.setTracks(trackList);
			
			List<Track> TrackList = new ArrayList<Track>();
			for (String s : tracks.split("\n")){
				String s_normalized = stripQuotes(s.replace("\n","").trim());
				Track t = new Track(s_normalized);
				t.setName(s_normalized);
				// System.out.println(t.getName());
				TrackList.add(t);
			}
			Album.setTracks(TrackList);
		}
		else {
			Album.setTracks(null);
		}

		String labels = getValueFromChildElement(node, "Labels");
		if (labels != null) {
		    String replace = labels.replace("[", "");
		    String replace1 = replace.replace("]", "");

		    String[] labelArray = Arrays.stream(replace.split("\\s*['\"\t\n]+\\s*"))
		            .filter(s -> !s.isEmpty())
		            .toArray(String[]::new);
		    
		    Album.setLabels(labelArray);
		} else {
		    Album.setLabels(new String[]{""});
		}
		
		String genres = getValueFromChildElement(node, "Genres");
		if (genres != null) {
		    String replace = genres.replace("[", "");
		    String replace1 = replace.replace("]", "");

		    String[] genreArray = Arrays.stream(replace.split("\\s*['\"\t\n]+\\s*"))
		            .filter(s -> !s.isEmpty())
		            .toArray(String[]::new);
		    
		    Album.setGenres(genreArray);
		} else {
		    Album.setGenres(new String[]{""});
		}

        
      // Deal with missing values
		String TotalTracksStr  = getValueFromChildElement(node, "TotalTracks");
		if (TotalTracksStr != null) {
			int TotalTracks = Integer.parseInt(getValueFromChildElement(node, "TotalTracks"));
			Album.setTotalTracks(TotalTracks);
		} 
		else {
			Album.setTotalTracks(null);
		}
		
        String DurationStr  = getValueFromChildElement(node, "Duration");
        if (DurationStr != null) {
        	int Duration = Integer.parseInt(getValueFromChildElement(node, "Duration"));
        	Album.setDuration(Duration);
        }
		else {
			Album.setDuration(null);
		}
		
        String PriceStr  = getValueFromChildElement(node, "Price");
        if (PriceStr != null) {
        	double Price = Double.parseDouble(getValueFromChildElement(node, "Price"));
        	Album.setPrice(Price);
        }
		else {
			Album.setPrice(null);
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
			else {
				Album.setReleaseDate(null);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return Album;
		}

		@Override
		public Album createInstanceForFusion(RecordGroup<Album, Attribute> cluster) {
		
		List<String> ids = new LinkedList<>();
		
		for (Album a : cluster.getRecords()) {
			ids.add(a.getIdentifier());
		}
		
		Collections.sort(ids);
		
		String mergedId = StringUtils.join(ids, '+');
		
		return new Album(mergedId, "fused");
	}
	
	private String stripQuotes(String title) {
		if (title == null) {
			return null;
		}
		else {
			return title.replaceFirst("\"$", "").replaceFirst("^\"", "");
		}
	}
}
