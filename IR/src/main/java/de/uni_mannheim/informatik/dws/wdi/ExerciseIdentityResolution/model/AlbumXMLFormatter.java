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
		return doc.createElement("albums");
	}

	@Override
	public Element createElementFromRecord(Album record, Document doc) {
		Element album = doc.createElement("album");

		album.appendChild(createTextElement("id", record.getIdentifier(), doc));

		album.appendChild(createTextElement("title",
				record.getTitle(),
				doc));
		album.appendChild(createTextElement("duration",
				String.valueOf(record.getDuration()),
				doc));
		album.appendChild(createTextElement("totalTracks",
				record.getTotalTracks().toString(),
				doc));
		album.appendChild(createTextElement("releaseDate", record
				.getReleaseDate().toString(), doc));
		
		album.appendChild(createArtistElement(record, doc));

		return album;
	}
	
	protected Element createArtistElement(Album record, Document doc) {
		Element actorRoot = artistFormatter.createRootElement(doc);

		for (Artist a : record.getArtists()) {
			actorRoot.appendChild(artistFormatter
					.createElementFromRecord(a, doc));
		}

		return actorRoot;
	}
	
	protected Element createTrackElement(Album record, Document doc) {
		Element trackRoot = trackFormatter.createRootElement(doc);

		for (Track a : record.getTracks()) {
			trackRoot.appendChild(trackFormatter
					.createElementFromRecord(a, doc));
		}

		return trackRoot;
	}
	
	protected Element createTextElementWithProvenance(String name,
			String value, String provenance, Document doc) {
		Element elem = createTextElement(name, value, doc);
		elem.setAttribute("provenance", provenance);
		return elem;
	}

}
