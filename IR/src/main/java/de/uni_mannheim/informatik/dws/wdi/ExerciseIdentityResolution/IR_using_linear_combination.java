package de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution;

import java.io.File;

import org.slf4j.Logger;

import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.Blocking.AlbumBlockingKeyByTitleGenerator;
import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.Comparators.MovieDateComparator2Years;
import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.Comparators.MovieTitleComparatorJaccard;
import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.Comparators.AlbumTitleComparatorLevenshtein;
import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.model.Album;
import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.model.AlbumXMLReader;
// import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.model.Movie;
// import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.model.MovieXMLReader;
import de.uni_mannheim.informatik.dws.winter.matching.MatchingEngine;
import de.uni_mannheim.informatik.dws.winter.matching.MatchingEvaluator;
import de.uni_mannheim.informatik.dws.winter.matching.algorithms.MaximumBipartiteMatchingAlgorithm;
import de.uni_mannheim.informatik.dws.winter.matching.blockers.Blocker;
import de.uni_mannheim.informatik.dws.winter.matching.blockers.NoBlocker;
import de.uni_mannheim.informatik.dws.winter.matching.blockers.SortedNeighbourhoodBlocker;
import de.uni_mannheim.informatik.dws.winter.matching.blockers.StandardRecordBlocker;
import de.uni_mannheim.informatik.dws.winter.matching.rules.LinearCombinationMatchingRule;
import de.uni_mannheim.informatik.dws.winter.model.Correspondence;
import de.uni_mannheim.informatik.dws.winter.model.HashedDataSet;
import de.uni_mannheim.informatik.dws.winter.model.MatchingGoldStandard;
import de.uni_mannheim.informatik.dws.winter.model.Performance;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;
import de.uni_mannheim.informatik.dws.winter.model.io.CSVCorrespondenceFormatter;
import de.uni_mannheim.informatik.dws.winter.processing.Processable;
import de.uni_mannheim.informatik.dws.winter.utils.WinterLogManager;

public class IR_using_linear_combination 
{
	/*
	 * Logging Options:
	 * 		default: 	level INFO	- console
	 * 		trace:		level TRACE     - console
	 * 		infoFile:	level INFO	- console/file
	 * 		traceFile:	level TRACE	- console/file
	 *  
	 * To set the log level to trace and write the log to winter.log and console, 
	 * activate the "traceFile" logger as follows:
	 *     private static final Logger logger = WinterLogManager.activateLogger("traceFile");
	 *
	 */

	private static final Logger logger = WinterLogManager.activateLogger("default");
	
    public static void main( String[] args ) throws Exception
    {
		// loading data
		logger.info("*\tLoading datasets\t*");
		HashedDataSet<Album, Attribute> dataWDC = new HashedDataSet<>();
		new AlbumXMLReader().loadFromXML(new File("data/input/WDC_mapped_output.xml"), "/root/Albums/Album", dataWDC);
		
		HashedDataSet<Album, Attribute> dataSpotify = new HashedDataSet<>();
		new AlbumXMLReader().loadFromXML(new File("data/input/spotify mapped.xml"), "/root/Albums/Album", dataSpotify);

		HashedDataSet<Album, Attribute> dataMB = new HashedDataSet<>();
		new AlbumXMLReader().loadFromXML(new File("data/input/output_target schema_MB.xml"), "/root/Albums/Album", dataMB);
		
		// load the gold standard (test set)
		logger.info("*\tLoading gold standard\t*");
		MatchingGoldStandard gsTest = new MatchingGoldStandard();
		gsTest.loadFromCSVFile(new File(
				"data/goldstandard/gs_mb_spotify_test.csv"));

		// create a matching rule
		LinearCombinationMatchingRule<Album, Attribute> matchingRule = new LinearCombinationMatchingRule<>(
				0.7);
		matchingRule.activateDebugReport("data/output/debugResultsMatchingRule.csv", 1000, gsTest);
		
		// add comparators
//		matchingRule.addComparator(new MovieDateComparator2Years(), 0.5);
		matchingRule.addComparator(new AlbumTitleComparatorLevenshtein(), 0.5);
		

		// create a blocker (blocking strategy)
//		StandardRecordBlocker<Album, Attribute> blocker = new StandardRecordBlocker<Album, Attribute>(new AlbumBlockingKeyByTitleGenerator());
		NoBlocker<Album, Attribute> blocker = new NoBlocker<>();
//		SortedNeighbourhoodBlocker<Movie, Attribute, Attribute> blocker = new SortedNeighbourhoodBlocker<>(new MovieBlockingKeyByTitleGenerator(), 1);
		blocker.setMeasureBlockSizes(true);
		
		//Write debug results to file:
		blocker.collectBlockSizeData("data/output/debugResultsBlocking.csv", 100);
		
		// Initialize Matching Engine
		MatchingEngine<Album, Attribute> engine = new MatchingEngine<>();

		// Execute the matching
		logger.info("*\tRunning identity resolution\t*");
		Processable<Correspondence<Album, Attribute>> correspondences = engine.runIdentityResolution(
				dataMB, dataSpotify, null, matchingRule,
				blocker);

		// Create a top-1 global matching
		  correspondences = engine.getTopKInstanceCorrespondences(correspondences, 1, 0.0);

////		 Alternative: Create a maximum-weight, bipartite matching
////		 MaximumBipartiteMatchingAlgorithm<Movie,Attribute> maxWeight = new MaximumBipartiteMatchingAlgorithm<>(correspondences);
////		 maxWeight.run();
////		 correspondences = maxWeight.getResult();

//		 write the correspondences to the output file
		new CSVCorrespondenceFormatter().writeCSV(new File("data/output/wd_spotify_correspondences.csv"), correspondences);		
		
//		logger.info("*\tEvaluating result\t*");
//		// evaluate your result
//		MatchingEvaluator<Movie, Attribute> evaluator = new MatchingEvaluator<Movie, Attribute>();
//		Performance perfTest = evaluator.evaluateMatching(correspondences,
//				gsTest);
//
//		// print the evaluation result
//		logger.info("Academy Awards <-> Actors");
//		logger.info(String.format(
//				"Precision: %.4f",perfTest.getPrecision()));
//		logger.info(String.format(
//				"Recall: %.4f",	perfTest.getRecall()));
//		logger.info(String.format(
//				"F1: %.4f",perfTest.getF1()));
    }
}
