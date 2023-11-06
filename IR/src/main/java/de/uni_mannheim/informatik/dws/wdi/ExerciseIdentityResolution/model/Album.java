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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import de.uni_mannheim.informatik.dws.winter.model.AbstractRecord;
import de.uni_mannheim.informatik.dws.winter.model.Matchable;

/**
 * A {@link AbstractRecord} representing a movie.
 * 
 * @author Oliver Lehmberg (oli@dwslab.de)
 * 
 */
public class Album implements Matchable {

	/*
	 * example entry <movie> <id>academy_awards_2</id> <title>True Grit</title>
	 * <director> <name>Joel Coen and Ethan Coen</name> </director> <actors>
	 * <actor> <name>Jeff Bridges</name> </actor> <actor> <name>Hailee
	 * Steinfeld</name> </actor> </actors> <date>2010-01-01</date> </movie>
	 */

	protected String id;
	protected String provenance;
	private String title;
	private LocalDate releaseDate;
	private Double price;
	private List<Artist> artists;
	private List<Track> tracks;
	private List<String> labels;
	private Integer totalTracks;
	private String genre;
	private String country;
	private String language;
	private Double duration;
	

	public Album(String identifier, String provenance) {
		id = identifier;
		this.provenance = provenance;
		artists = new LinkedList<>();
		tracks = new LinkedList<>();
	}
	
	@Override
	public String getIdentifier() {
		return id;
	}

	@Override
	public String getProvenance() {
		return provenance;
	}

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

	public Double getDuration() {
		return duration;
	}

	public void setDuration(Double duration) {
		this.duration = duration;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}


	public String getGenre() {
		return genre;
	}

	public void setGenre(String genre) {
		this.genre = genre;
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
