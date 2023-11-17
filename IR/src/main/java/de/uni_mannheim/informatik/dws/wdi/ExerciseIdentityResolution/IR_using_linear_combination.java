package de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution;

import java.io.File;

import org.slf4j.Logger;

import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.Blocking.AlbumBlockingKeyByTitleGenerator;
// import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.Comparators.AlbumTitleComparatorLevenshtein;
import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.Comparators.AlbumTitleComparatorLevenshteinLowerCase;
import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.Comparators.AlbumTotalTracksComparatorAbsoluteDifferenceSimilarity;
import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.Comparators.AlbumTotalTracksComparatorDeviationSimilarity;
import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.Comparators.AlbumDateComparator10Years;
import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.Comparators.AlbumDurationComparatorAbsoluteDifferenceSimilarity;
import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.Comparators.AlbumTitleComparatorJaccard;
import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.model.Album;
import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.model.AlbumXMLReader;
import de.uni_mannheim.informatik.dws.winter.matching.MatchingEngine;
import de.uni_mannheim.informatik.dws.winter.matching.blockers.StandardRecordBlocker;
import de.uni_mannheim.informatik.dws.winter.matching.rules.LinearCombinationMatchingRule;
import de.uni_mannheim.informatik.dws.winter.model.Correspondence;
import de.uni_mannheim.informatik.dws.winter.model.HashedDataSet;
import de.uni_mannheim.informatik.dws.winter.model.MatchingGoldStandard;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;
import de.uni_mannheim.informatik.dws.winter.model.io.CSVCorrespondenceFormatter;
import de.uni_mannheim.informatik.dws.winter.model.Performance;
import de.uni_mannheim.informatik.dws.winter.processing.Processable;
import de.uni_mannheim.informatik.dws.winter.utils.WinterLogManager;
import de.uni_mannheim.informatik.dws.winter.matching.MatchingEvaluator;
import de.uni_mannheim.informatik.dws.winter.matching.algorithms.MaximumBipartiteMatchingAlgorithm;
import de.uni_mannheim.informatik.dws.winter.matching.blockers.Blocker;
import de.uni_mannheim.informatik.dws.winter.matching.blockers.NoBlocker;
import de.uni_mannheim.informatik.dws.winter.matching.blockers.SortedNeighbourhoodBlocker;

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
		new AlbumXMLReader().loadFromXML(new File("data/input/WDC.xml"), "/root/Albums/Album", dataWDC);
		// new AlbumXMLReader().loadFromXML(new File("data/input/WDC_min.xml"), "/root/Albums/Album", dataWDC);
		
		HashedDataSet<Album, Attribute> dataSpotify = new HashedDataSet<>();
		new AlbumXMLReader().loadFromXML(new File("data/input/SPY.xml"), "/root/Albums/Album", dataSpotify);
		// new AlbumXMLReader().loadFromXML(new File("data/input/SPY_min.xml"), "/root/Albums/Album", dataSpotify);
		
//		HashedDataSet<Album, Attribute> dataMB = new HashedDataSet<>();
////		new AlbumXMLReader().loadFromXML(new File("data/input/MB_min.xml"), "/root/Albums/Album", dataMB);
//		new AlbumXMLReader().loadFromXML(new File("data/input/MB.xml"), "/root/Albums/Album", dataMB);
		
		
//		Performance perfTest_MB_SPY = identityResolution(dataMB, dataSpotify, "MB_SPY", "gs_mb_spy");
//		Performance perfTest_WDC_MB = identityResolution(dataWDC, dataMB, "WDC_MB", "gs_wdc_mb");
		Performance perfTest_WDC_SPY = identityResolution(dataWDC, dataSpotify, "WDC_SPY", "gs_wdc_spy");

		// print the evaluation result
//		logger.info("*\tEvaluating result: MusicBrainz <-> Spotify");
//		printEvalPerf(perfTest_MB_SPY);
//		
//		logger.info("*\tEvaluating result: WebDataCommons <-> MusicBrainz");
//		printEvalPerf(perfTest_WDC_MB);
		
		logger.info("*\tEvaluating result: WebDataCommons <-> Spotify");
		printEvalPerf(perfTest_WDC_SPY);
    }


	private static void printEvalPerf(Performance perfTest) {
		logger.info(String.format(
				"Precision: %.4f",perfTest.getPrecision()));
		logger.info(String.format(
				"Recall: %.4f",	perfTest.getRecall()));
		logger.info(String.format(
				"F1: %.4f",perfTest.getF1()));
	}
    
    
	private static Performance identityResolution(HashedDataSet<Album, Attribute> d1, HashedDataSet<Album, Attribute> d2, String d1_d2_name, String gs_name) throws Exception {
		// load the gold standard (test set)
		logger.info("*\tLoading gold standard\t* " + d1_d2_name);
		
		MatchingGoldStandard gsTest = new MatchingGoldStandard();
		gsTest.loadFromCSVFile(new File("data/goldstandard/" + gs_name  + ".csv"));

		// create a matching rule
		LinearCombinationMatchingRule<Album, Attribute> matchingRule = new LinearCombinationMatchingRule<>(
				0.7);
		matchingRule.activateDebugReport("data/output/debugResultsMatchingRule" + d1_d2_name + ".csv", 10000, gsTest);
		
		// add comparators
//		matchingRuleMBSpy.addComparator(new MovieDateComparator2Years(), 0.5);
		matchingRule.addComparator(new AlbumTitleComparatorLevenshteinLowerCase(), 1.0);
		matchingRule.addComparator(new AlbumTotalTracksComparatorDeviationSimilarity(), 0.5);
		matchingRule.addComparator(new AlbumTotalTracksComparatorAbsoluteDifferenceSimilarity(), 0.5);
		matchingRule.addComparator(new AlbumDurationComparatorAbsoluteDifferenceSimilarity(), 0.5);
//		matchingRule.addComparator(new AlbumDateComparator10Years(), 1);
//		matchingRule.addComparator(new AlbumTitleComparatorJaccard(), 0.5);
		
		// create a blocker (blocking strategy)
		StandardRecordBlocker<Album, Attribute> blocker = new StandardRecordBlocker<Album, Attribute>(new AlbumBlockingKeyByTitleGenerator());
//		NoBlocker<Album, Attribute> blocker = new NoBlocker<>();
//		SortedNeighbourhoodBlocker<Movie, Attribute, Attribute> blocker = new SortedNeighbourhoodBlocker<>(new MovieBlockingKeyByTitleGenerator(), 1);
		blocker.setMeasureBlockSizes(true);
		
		//Write debug results to file:
		blocker.collectBlockSizeData("data/output/debugResultsBlocking" + d1_d2_name +  ".csv", 100);
		
		// Initialize Matching Engine
		MatchingEngine<Album, Attribute> engine = new MatchingEngine<>();

		// Execute the matching
		logger.info("*\tRunning identity resolution\t* " + d1_d2_name);
		Processable<Correspondence<Album, Attribute>> correspondences = engine.runIdentityResolution(
				d1, d2, null, matchingRule,
				blocker);
		
		// Create a top-1 global matching
		  correspondences = engine.getTopKInstanceCorrespondences(correspondences, 1, 0.7);

////		 Alternative: Create a maximum-weight, bipartite matching
////		 MaximumBipartiteMatchingAlgorithm<Movie,Attribute> maxWeight = new MaximumBipartiteMatchingAlgorithm<>(correspondences);
////		 maxWeight.run();
////		 correspondences = maxWeight.getResult();

//		 write the correspondences to the output file
		new CSVCorrespondenceFormatter().writeCSV(new File("data/output/" + d1_d2_name + "_correspondences.csv"), correspondences);		
		
		// evaluate your result
		MatchingEvaluator<Album, Attribute> evaluator = new MatchingEvaluator<Album, Attribute>();
		Performance perfTest = evaluator.evaluateMatching(correspondences,
				gsTest);

		return perfTest;
	}
}
