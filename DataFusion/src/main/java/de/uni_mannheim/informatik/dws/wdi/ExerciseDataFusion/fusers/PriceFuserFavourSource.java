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
package de.uni_mannheim.informatik.dws.wdi.ExerciseDataFusion.fusers;

import de.uni_mannheim.informatik.dws.wdi.ExerciseDataFusion.model.Album;
import de.uni_mannheim.informatik.dws.winter.datafusion.AttributeValueFuser;
import de.uni_mannheim.informatik.dws.winter.datafusion.conflictresolution.meta.FavourSources;
import de.uni_mannheim.informatik.dws.winter.model.Correspondence;
import de.uni_mannheim.informatik.dws.winter.model.FusedValue;
import de.uni_mannheim.informatik.dws.winter.model.Matchable;
import de.uni_mannheim.informatik.dws.winter.model.RecordGroup;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;
import de.uni_mannheim.informatik.dws.winter.processing.Processable;

/**
 * {@link AttributeValueFuser} for the actors of {@link Album}s. 
 * 
 * @author Oliver Lehmberg (oli@dwslab.de)
 * 
 */
public class PriceFuserFavourSource extends AttributeValueFuser<Double, Album, Attribute> {
	
	public PriceFuserFavourSource() {
		super(new FavourSources<Double, Album, Attribute>());
	}
	
	@Override
	public boolean hasValue(Album record, Correspondence<Attribute, Matchable> correspondence) {
		return record.hasValue(Album.PRICE);
	}
	
	@Override
	public Double getValue(Album record, Correspondence<Attribute, Matchable> correspondence) {
		return record.getPrice();
	}

	@Override
	public void fuse(RecordGroup<Album, Attribute> group, Album fusedRecord, Processable<Correspondence<Attribute, Matchable>> schemaCorrespondences, Attribute schemaElement) {
		FusedValue<Double,Album, Attribute> fused = getFusedValue(group, schemaCorrespondences, schemaElement);
		fusedRecord.setPrice(fused.getValue());
		fusedRecord.setAttributeProvenance(Album.PRICE, fused.getOriginalIds());
	}

}
