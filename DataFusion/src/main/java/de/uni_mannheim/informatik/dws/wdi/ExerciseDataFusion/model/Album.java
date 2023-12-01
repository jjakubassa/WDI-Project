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

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import de.uni_mannheim.informatik.dws.winter.model.AbstractRecord;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;

/**
 * 
 */
/**
 * A {@link AbstractRecord} representing an album.
 * 
 * @author Oliver Lehmberg (oli@dwslab.de)
 * 
 */
public class Album extends AbstractRecord<Attribute> implements Serializable {

	/*
	 * example entry <movie> <id>academy_awards_2</id> <title>True Grit</title>
	 * <director> <name>Joel Coen and Ethan Coen</name> </director> <actors>
	 * <actor> <name>Jeff Bridges</name> </actor> <actor> <name>Hailee
	 * Steinfeld</name> </actor> </actors> <date>2010-01-01</date> </movie>
	 */

	private static final long serialVersionUID = 1L;

	public Album(String identifier, String provenance) {
		super(identifier, provenance);
		id = identifier;
//		this.provenance = provenance;
		artists = new LinkedList<>();
		tracks = new LinkedList<>();
	}
	
	
	protected String id;
//	protected String provenance;
	private String title;
	private LocalDate releaseDate;
	private Double price;
	private List<Artist> artists;
	private List<Track> tracks;
	private List<String> labels;
	private Integer totalTracks;
	private List<String> genre;
	private String country;
	private String language;
	private int duration;
	
	
	@Override
	public String getIdentifier() {
		return id;
	}

//	@Override
//	public String getProvenance() {
//		return provenance;
//	}

	public LocalDate getReleaseDate() {
		return releaseDate;
	}

	public void setReleaseDate(LocalDate releaseDate) {
		this.releaseDate = releaseDate;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public List<Artist> getArtists() {
		return artists;
	}

	public void setArtists(List<Artist> artists) {
		this.artists = artists;
	}

	public List<Track> getTracks() {
		return tracks;
	}

	public void setTracks(List<Track> tracks) {
		this.tracks = tracks;
	}

	public List<String> getLabels() {
		return labels;
	}

	public void setLabels(List<String> labels) {
		this.labels = labels;
	}

	public Integer getTotalTracks() {
		return totalTracks;
	}

	public void setTotalTracks(Integer totalTracks) {
		this.totalTracks = totalTracks;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}


	public List<String> getGenres() {
		return genre;
	}

	public void setGenres(List<String> genre) {
		this.genre = genre;
	}

// NEWLY ADDED
	

	private Map<Attribute, Collection<String>> provenance = new HashMap<>();
	private Collection<String> recordProvenance;

	public void setRecordProvenance(Collection<String> provenance) {
		recordProvenance = provenance;
	}

	public Collection<String> getRecordProvenance() {
		return recordProvenance;
	}

	public void setAttributeProvenance(Attribute attribute,
			Collection<String> provenance) {
		this.provenance.put(attribute, provenance);
	}

	public Collection<String> getAttributeProvenance(String attribute) {
		return provenance.get(attribute);
	}

	public String getMergedAttributeProvenance(Attribute attribute) {
		Collection<String> prov = provenance.get(attribute);

		if (prov != null) {
			return StringUtils.join(prov, "+");
		} else {
			return "";
		}
	}
	
	public static final Attribute TITLE = new Attribute("Title");
	public static final Attribute RELEASEDATE = new Attribute("Release Date");
	public static final Attribute PRICE = new Attribute("Price");
	public static final Attribute ARTISTS = new Attribute("Artist");
	public static final Attribute TRACKS = new Attribute("Tracks");
	public static final Attribute LABELS = new Attribute("Labels");
	public static final Attribute TOTALTRACKS = new Attribute("Total Tracks");
	public static final Attribute GENRE = new Attribute("Genre");
	public static final Attribute COUNTRY = new Attribute("Country");
	public static final Attribute LANGUAGE = new Attribute("Language");
	public static final Attribute DURATION = new Attribute("Duration");
	
	@Override
	public boolean hasValue(Attribute attribute) {
		if(attribute==TITLE)
			return getTitle() != null && !getTitle().isEmpty(); // ???
		else if(attribute==RELEASEDATE)
			return getReleaseDate() != null;
		else if(attribute==PRICE)
			return getPrice() != null;
		else if(attribute==ARTISTS)
			return getArtists() != null && getArtists().size() > 0; // ???
		else if(attribute==TRACKS)
			return getTracks() != null;
		else if(attribute==LABELS)
			return getLabels() != null;
		else if(attribute==TOTALTRACKS)
			return getTotalTracks() != null;
		else if(attribute==GENRE)
			return getGenres() != null;
		else if(attribute==COUNTRY)
			return getCountry() != null;
		else if(attribute==LANGUAGE)
			return getLanguage() != null;
		else if(attribute==DURATION)
			return getDuration() > 0;
		else
			return false;
	}

	@Override
	public String toString() {
		return String.format("[Album %s: %s / %s / %s]", getIdentifier(), getTitle(),
				getArtists(), getReleaseDate().toString());
	}

	@Override
	public int hashCode() {
		return getIdentifier().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Album){
			return this.getIdentifier().equals(((Album) obj).getIdentifier());
		}else
			return false;
	}
	
	
	
}
