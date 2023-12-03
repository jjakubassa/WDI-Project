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

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.uni_mannheim.informatik.dws.winter.model.io.XMLFormatter;

/**
 * {@link XMLFormatter} for {@link Album}s.
 * 
 * @author Oliver Lehmberg (oli@dwslab.de)
 * 
 */
public class AlbumXMLFormatter extends XMLFormatter<Album> {
	
	ArtistXMLFormatter artistFormatter = new ArtistXMLFormatter();
	TrackXMLFormatter trackFormatter = new TrackXMLFormatter();

	@Override
	public Element createRootElement(Document doc) {
		return doc.createElement("Albums");
	}

	@Override
	public Element createElementFromRecord(Album record, Document doc) {
		Element album = doc.createElement("Album");

		album.appendChild(createTextElement("id", record.getIdentifier(), doc));

		album.appendChild(createTextElementWithProvenance("title",
				record.getTitle(),
				record.getMergedAttributeProvenance(Album.TITLE),
				doc));

		if (record.getDuration() == null) {
			album.appendChild(createTextElementWithProvenance(
				"duration", 
				"null",
				record.getMergedAttributeProvenance(Album.DURATION), doc));
		} else {
			album.appendChild(createTextElementWithProvenance(
				"duration",
				record.getDuration().toString(),
				record.getMergedAttributeProvenance(Album.DURATION), doc));
		}

		if (record.getTotalTracks() == null) {
			album.appendChild(createTextElementWithProvenance(
				"totalTracks", 
				"null",
				record.getMergedAttributeProvenance(Album.TOTALTRACKS), doc));
		} else {
			album.appendChild(createTextElementWithProvenance(
				"totalTracks",
				record.getTotalTracks().toString(),
				record.getMergedAttributeProvenance(Album.TOTALTRACKS), doc));
		}

		if (record.getReleaseDate() == null) {
			album.appendChild(createTextElementWithProvenance(
				"releaseDate", 
				"null",
				record.getMergedAttributeProvenance(Album.RELEASEDATE), doc));
		} else {
			album.appendChild(createTextElementWithProvenance(
				"releaseDate",
				record.getReleaseDate().toString(),
				record.getMergedAttributeProvenance(Album.RELEASEDATE), doc));
		}
		
		if (record.getPrice() == null) {
			album.appendChild(createTextElementWithProvenance(
				"price", 
				"null",
				record.getMergedAttributeProvenance(Album.PRICE), doc));
		} else {
			album.appendChild(createTextElementWithProvenance(
				"price",
				roundToDecimals(record.getPrice(), 2).toString(),
				record.getMergedAttributeProvenance(Album.PRICE), doc));
		}

		album.appendChild(createArtistElement(record, doc));
		album.appendChild(createGenresElement(record, doc));
	

		return album;
	}
	
	
	
	protected Element createArtistElement(Album record, Document doc) {
		Element artistRoot = artistFormatter.createRootElement(doc);

		for (Artist a : record.getArtists()) {
			artistRoot.appendChild(artistFormatter
					.createElementFromRecord(a, doc));
		}

		return artistRoot;
	}
	
	protected Element createGenresElement(Album record, Document doc) {
		Element genresRoot = doc.createElement("genres");
		
		genresRoot.setAttribute("provenance",
				record.getMergedAttributeProvenance(Album.GENRE));

		for (String a : record.getGenres()) {
			if (!a.isEmpty()) {
				genresRoot.appendChild(createTextElement("genre", a, doc));
			}
			
		}

		return genresRoot;
	}
	
	protected Element createTrackElement(Album record, Document doc) {
		Element trackRoot = trackFormatter.createRootElement(doc);

		for (Track a : record.getTracks()) {
			trackRoot.appendChild(trackFormatter
					.createElementFromRecord(a, doc));
		}

		return trackRoot;
	}

	// NEWLY ADDED
	protected Element createTextElementWithProvenance(String name,
			String value, String provenance, Document doc) {
		Element elem = createTextElement(name, value, doc);
		elem.setAttribute("provenance", provenance);
		return elem;
	}

	private static Double roundToDecimals(Double value, Integer decimalPlaces) {
        Double factor = Math.pow(10, decimalPlaces);
        return Math.round(value * factor) / factor;
    }
}
