package de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.Comparators;

import java.util.HashSet;
import java.util.Set;

import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.model.Album;
import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.model.Track;
import de.uni_mannheim.informatik.dws.winter.matching.rules.comparators.Comparator;
import de.uni_mannheim.informatik.dws.winter.matching.rules.comparators.ComparatorLogger;
import de.uni_mannheim.informatik.dws.winter.model.Correspondence;
import de.uni_mannheim.informatik.dws.winter.model.Matchable;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;
import de.uni_mannheim.informatik.dws.winter.utils.query.Q;

public class AlbumTrackComparator implements Comparator<Album, Attribute> {

	private static final long serialVersionUID = 1L;
	
	private ComparatorLogger comparisonLog;

	@Override
	public double compare(
			Album record1,
			Album record2,
			Correspondence<Attribute, Matchable> schemaCorrespondences) {
		
		Set<String> tracks1 = new HashSet<>();
		Set<String> tracks2 = new HashSet<>();
		
		for(Track t : record1.getTracks()) {
			tracks1.add(t.getName());
		}
		for(Track t : record2.getTracks()) {
			tracks2.add(t.getName());
		}
		
		double similarity = Q.intersection(tracks1, tracks2).size() / (double)Math.max(tracks1.size(), tracks2.size());
		
		if(this.comparisonLog != null){
			this.comparisonLog.setComparatorName(getClass().getName());
		
			this.comparisonLog.setRecord1Value(tracks1.toString());
			this.comparisonLog.setRecord2Value(tracks2.toString());
    	
			this.comparisonLog.setSimilarity(Double.toString(similarity));
		}
		
		return similarity;
	}
	
	@Override
	public ComparatorLogger getComparisonLog() {
		return this.comparisonLog;
	}

	@Override
	public void setComparisonLog(ComparatorLogger comparatorLog) {
		this.comparisonLog = comparatorLog;
	}

}
