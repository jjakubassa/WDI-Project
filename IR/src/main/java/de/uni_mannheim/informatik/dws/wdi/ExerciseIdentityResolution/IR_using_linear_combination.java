package de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;

import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.Blocking.AlbumBlockingKeyByTitleGenerator;
import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.Blocking.AlbumBlockingKeyByTitleandYear;
// import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.Comparators.AlbumTitleComparatorLevenshtein;
import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.Comparators.AlbumTitleComparatorLevenshteinLowerCase;
import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.Comparators.AlbumTitleComparatorMaximumOfTokenContainnment;
import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.Comparators.ArtistNameComporatorGeneralisedMaximumOfContainment;
import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.Comparators.ArtistNameComporatorGeneralizedJaccard;
import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.Comparators.TotalTracksComparatorDeviationSimilarity;
import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.Comparators.TrackNameComporatorGeneralisedMaximumOfContainment;
import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.Comparators.AlbumTotalTracksComparatorAbsoluteDifferenceSimilarity;
import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.Comparators.AlbumDateComparator10Years;
import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.Comparators.AlbumDateComparatorWeightedDate;
import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.Comparators.AlbumDurationComparatorAbsoluteDifferenceSimilarity;
import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.Comparators.AlbumTitleComparatorJaccard;
import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.model.Album;
import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.model.AlbumXMLReader;
import de.uni_mannheim.informatik.dws.winter.matching.MatchingEngine;
import de.uni_mannheim.informatik.dws.winter.matching.blockers.StandardRecordBlocker;
import de.uni_mannheim.informatik.dws.winter.matching.rules.LinearCombinationMatchingRule;
import de.uni_mannheim.informatik.dws.winter.matching.rules.WekaMatchingRule;
import de.uni_mannheim.informatik.dws.winter.model.Correspondence;
import de.uni_mannheim.informatik.dws.winter.model.HashedDataSet;
import de.uni_mannheim.informatik.dws.winter.model.MatchingGoldStandard;
import de.uni_mannheim.informatik.dws.winter.model.Pair;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;
import de.uni_mannheim.informatik.dws.winter.model.io.CSVCorrespondenceFormatter;
import de.uni_mannheim.informatik.dws.winter.model.Performance;
import de.uni_mannheim.informatik.dws.winter.processing.Processable;
import de.uni_mannheim.informatik.dws.winter.utils.WinterLogManager;
import weka.core.pmml.jaxbbindings.False;
import weka.core.pmml.jaxbbindings.True;
import de.uni_mannheim.informatik.dws.winter.matching.MatchingEvaluator;
import de.uni_mannheim.informatik.dws.winter.matching.algorithms.MaximumBipartiteMatchingAlgorithm;
import de.uni_mannheim.informatik.dws.winter.matching.algorithms.RuleLearner;
import de.uni_mannheim.informatik.dws.winter.matching.blockers.Blocker;
import de.uni_mannheim.informatik.dws.winter.matching.blockers.NoBlocker;
import de.uni_mannheim.informatik.dws.winter.matching.blockers.SortedNeighbourhoodBlocker;
import de.uni_mannheim.informatik.dws.winter.matching.blockers.AbstractBlocker;

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
		// new AlbumXMLReader().loadFromXML(new File("data/input/spotify_min.xml"), "/root/Albums/Album", dataSpotify);
		
		HashedDataSet<Album, Attribute> dataMB = new HashedDataSet<>();
		new AlbumXMLReader().loadFromXML(new File("data/input/MB.xml"), "/root/Albums/Album", dataMB);
		// new AlbumXMLReader().loadFromXML(new File("data/input/MB_min.xml"), "/root/Albums/Album", dataMB);
		
		Pair<Performance, String> result;
		Performance perfTest_MB_SPY;
		Performance perfTest_WDC_MB;
		Performance perfTest_WDC_SPY;
		String number_correspondences_MB_SPY;
		String number_correspondences_WDC_MB;
		String number_correspondences_WDC_SPY;
		Boolean learn_weights = false;
		long startTime;
		long endTime;
		long elapsedTime_MB_SPY;
		long elapsedTime_WDC_MB;
		long elapsedTime_WDC_SPY;

		// Define a matching rule
		Map<String, Boolean> comparatorMap1 = new HashMap<>();
        comparatorMap1.put("AlbumTitleComparatorLevenshteinLowerCase", true);
		comparatorMap1.put("AlbumTitleComparatorJaccard", true);
		comparatorMap1.put("AlbumTitleComparatorMaximumOfTokenContainnment", true);
		comparatorMap1.put("ArtistNameComporatorGeneralisedMaximumOfContainment", true);
		comparatorMap1.put("ArtistNameComporatorGeneralizedJaccard", true);
		comparatorMap1.put("AlbumTotalTracksComparatorAbsoluteDifferenceSimilarity", true);
		comparatorMap1.put("TotalTracksComparatorDeviationSimilarity", true);
		comparatorMap1.put("AlbumTotalTracksComparatorDeviationSimilarity", true);
		comparatorMap1.put("TrackNameComporatorGeneralisedMaximumOfContainment", true);
		comparatorMap1.put("AlbumDateComparator10Years", true);
		comparatorMap1.put("AlbumDateComparatorWeightedDate", true);
		comparatorMap1.put("AlbumDurationComparatorAbsoluteDifferenceSimilarity", true);

		Map<String, Boolean> comparatorMap2 = new HashMap<>();
        comparatorMap2.put("AlbumTitleComparatorLevenshteinLowerCase", true);
		comparatorMap2.put("AlbumTitleComparatorJaccard", true);
		comparatorMap2.put("AlbumTitleComparatorMaximumOfTokenContainnment", true);
		comparatorMap2.put("ArtistNameComporatorGeneralisedMaximumOfContainment", false);
		comparatorMap2.put("ArtistNameComporatorGeneralizedJaccard", false);
		comparatorMap2.put("AlbumTotalTracksComparatorAbsoluteDifferenceSimilarity", false);
		comparatorMap2.put("TotalTracksComparatorDeviationSimilarity", false);
		comparatorMap2.put("AlbumTotalTracksComparatorDeviationSimilarity", false);
		comparatorMap2.put("TrackNameComporatorGeneralisedMaximumOfContainment", false);
		comparatorMap2.put("AlbumDateComparator10Years", false);
		comparatorMap2.put("AlbumDateComparatorWeightedDate", false);
		comparatorMap2.put("AlbumDurationComparatorAbsoluteDifferenceSimilarity", false);
		
		// Create a Map to store matching rules
		Map<String, Map<String, Boolean>> matchingRules = new HashMap<>();
		matchingRules.put("mr1", comparatorMap1);
		// matchingRules.put("mr2", comparatorMap2);

		// create a map to store blockers
		Map<String, AbstractBlocker> blockers = new HashMap<>();
		blockers.put("StandardRecordBlocker AlbumBlockingKeyByTitleGenerator", new StandardRecordBlocker<Album, Attribute>(new AlbumBlockingKeyByTitleGenerator()));
		blockers.put("SortedNeighbourhoodBlocker AlbumBlockingKeyByTitleGenerator", new SortedNeighbourhoodBlocker<Album, Attribute,  Attribute>(new AlbumBlockingKeyByTitleGenerator(), 1));
		blockers.put("StandardRecordBlocker AlbumBlockingKeyByTitleandYear", new StandardRecordBlocker<Album, Attribute>(new AlbumBlockingKeyByTitleandYear()));
		blockers.put("SortedNeighbourhoodBlocker AlbumBlockingKeyByTitleandYear", new SortedNeighbourhoodBlocker<Album, Attribute,  Attribute>(new AlbumBlockingKeyByTitleandYear(), 1));

		// Loop over matching rules
		for (Map.Entry<String, Map<String, Boolean>> entry : matchingRules.entrySet()) {
			String name_mr = entry.getKey();
			Map<String, Boolean> comparatorMap = entry.getValue();

			logger.info("Matching rule: " + name_mr);
			
			for (Map.Entry<String, AbstractBlocker> entry2 : blockers.entrySet()) {
				String name_blocker = entry2.getKey();
				AbstractBlocker blocker = entry2.getValue();
				
				logger.info("Blocker: " + name_blocker);
				if (learn_weights == true) {
					// MB_SPY
					startTime = System.currentTimeMillis();
					result = identityResolutionLearnedWeights(dataMB, dataSpotify, "MB_SPY", "gs_mb_spy", comparatorMap, blocker);
					perfTest_MB_SPY = result.getFirst();
					number_correspondences_MB_SPY = result.getSecond();
					endTime = System.currentTimeMillis();
					elapsedTime_MB_SPY = endTime - startTime;
					logger.info("*\tEvaluating result: MusicBrainz <-> Spotify");
					printEvalPerf(perfTest_MB_SPY);
					logger.info("Number of correspondences: " + number_correspondences_MB_SPY);
					
					// WDC_MB
					startTime = System.currentTimeMillis();
					result = identityResolutionLearnedWeights(dataWDC, dataMB, "WDC_MB", "gs_wdc_mb", comparatorMap, blocker);
					perfTest_WDC_MB = result.getFirst();
					number_correspondences_WDC_MB = result.getSecond();
					endTime = System.currentTimeMillis();
					elapsedTime_WDC_MB = endTime - startTime; 
					logger.info("*\tEvaluating result: WebDataCommons <-> MusicBrainz");
					printEvalPerf(perfTest_WDC_MB);
					logger.info("Number of correspondences: " + number_correspondences_WDC_MB);
					
					// WDC_SPY
					startTime = System.currentTimeMillis();
					result = identityResolutionLearnedWeights(dataWDC, dataSpotify, "WDC_SPY", "gs_wdc_spy", comparatorMap, blocker);
					perfTest_WDC_SPY = result.getFirst();
					number_correspondences_WDC_SPY = result.getSecond();
					endTime = System.currentTimeMillis();
					elapsedTime_WDC_SPY = endTime - startTime;
					logger.info("*\tEvaluating result: WebDataCommons <-> Spotify");
					printEvalPerf(perfTest_WDC_SPY);
					logger.info("Number of correspondences: " + number_correspondences_WDC_SPY);		
				}
				else {
					// MB_SPY
					startTime = System.currentTimeMillis();
					result = identityResolution(dataMB, dataSpotify, "MB_SPY", "gs_mb_spy", comparatorMap);
					perfTest_MB_SPY = result.getFirst();
					number_correspondences_MB_SPY = result.getSecond();
					endTime = System.currentTimeMillis();
					elapsedTime_MB_SPY = endTime - startTime;
					logger.info("*\tEvaluating result: MusicBrainz <-> Spotify");
					printEvalPerf(perfTest_MB_SPY);
					logger.info("Number of correspondences: " + number_correspondences_MB_SPY);
					
					// WDC_MB
					startTime = System.currentTimeMillis();
					result = identityResolution(dataWDC, dataMB, "WDC_MB", "gs_wdc_mb", comparatorMap);
					perfTest_WDC_MB = result.getFirst();
					number_correspondences_WDC_MB = result.getSecond();
					endTime = System.currentTimeMillis();
					elapsedTime_WDC_MB = endTime - startTime; 
					logger.info("*\tEvaluating result: WebDataCommons <-> MusicBrainz");
					printEvalPerf(perfTest_WDC_MB);
					logger.info("Number of correspondences: " + number_correspondences_WDC_MB);
					
					// WDC_SPY
					startTime = System.currentTimeMillis();
					result = identityResolution(dataWDC, dataSpotify, "WDC_SPY", "gs_wdc_spy", comparatorMap);
					perfTest_WDC_SPY = result.getFirst();
					number_correspondences_WDC_SPY = result.getSecond();
					endTime = System.currentTimeMillis();
					elapsedTime_WDC_SPY = endTime - startTime;
					logger.info("*\tEvaluating result: WebDataCommons <-> Spotify");
					printEvalPerf(perfTest_WDC_SPY);
					logger.info("Number of correspondences: " + number_correspondences_WDC_SPY);
				}
				
				// print summary in csv style
				logger.info("Compact summary of evaluation results:");
				logger.info("MatchingRule, Blocker, Dataset, Precision, Recall, F1, # Corr, Time [ms], weights_learned");
				logger.info(name_mr + ", " + name_blocker + ", MB_SPY, " + perfTest_MB_SPY.getPrecision() + ", " + perfTest_MB_SPY.getRecall() + ", " + perfTest_MB_SPY.getF1() + ", " + number_correspondences_MB_SPY + ", " + elapsedTime_MB_SPY + ", " + learn_weights);
				logger.info(name_mr + ", " + name_blocker + ", WDC_MB, " + perfTest_WDC_MB.getPrecision() + ", " + perfTest_WDC_MB.getRecall() + ", " + perfTest_WDC_MB.getF1() + ", " + number_correspondences_WDC_MB + ", " + elapsedTime_WDC_MB + ", " + learn_weights);
				logger.info(name_mr + ", " + name_blocker + ", WDC_SPY, " + perfTest_WDC_SPY.getPrecision() + ", " + perfTest_WDC_SPY.getRecall() + ", " + perfTest_WDC_SPY.getF1() + ", " + number_correspondences_WDC_SPY + ", " + elapsedTime_WDC_SPY + ", " + learn_weights);
			}
		}
    }

	private static void printEvalPerf(Performance perfTest) {
		logger.info(String.format(
				"Precision: %.4f",perfTest.getPrecision()));
		logger.info(String.format(
				"Recall: %.4f",	perfTest.getRecall()));
		logger.info(String.format(
				"F1: %.4f",perfTest.getF1()));
	}
    
    
	private static Pair<Performance, String> identityResolution(
		HashedDataSet<Album, Attribute> d1, 
		HashedDataSet<Album, Attribute> d2, 
		String d1_d2_name, 
		String gs_name, 
		Map<String, Boolean> comparatorMap
		// AbstractBlocker blocker
		) throws Exception {

		// load the gold standard (test set)
		logger.info("*\tLoading gold standard\t* " + d1_d2_name);
		
		MatchingGoldStandard gsTest = new MatchingGoldStandard();
		gsTest.loadFromCSVFile(new File("data/goldstandard/" + gs_name  + ".csv"));

		// create a matching rule
		LinearCombinationMatchingRule<Album, Attribute> matchingRule = new LinearCombinationMatchingRule<>(
				0.7);
		matchingRule.activateDebugReport("data/output/debugResultsMatchingRule" + d1_d2_name + ".csv", 100_000, gsTest);
		
		// add comparators
		logger.info("*\tAdding comparators\t*");

		
		if (d1_d2_name.equals("WDC_MB")) {
			
			// album title comparators	
			if (comparatorMap.get("AlbumTitleComparatorLevenshteinLowerCase")) {
				matchingRule.addComparator(new AlbumTitleComparatorLevenshteinLowerCase(), 0.1); 
				logger.info("Attribute: AlbumTitle, Comparator: AlbumTitleComparatorLevenshteinLowerCase");
			}
			if (comparatorMap.get("AlbumTitleComparatorJaccard")) {
				matchingRule.addComparator(new AlbumTitleComparatorJaccard(), 0.1); 
				logger.info("Attribute: AlbumTitle, Comparator: AlbumTitleComparatorJaccard");
			}
			if (comparatorMap.get("AlbumTitleComparatorMaximumOfTokenContainnment")) {
				matchingRule.addComparator(new AlbumTitleComparatorMaximumOfTokenContainnment(), 0.2); 
				logger.info("Attribute: AlbumTitle, Comparator: AlbumTitleComparatorMaximumOfTokenContainnment");
			}

			// artist name comparators
			if (comparatorMap.get("ArtistNameComporatorGeneralisedMaximumOfContainment")) {
				matchingRule.addComparator(new ArtistNameComporatorGeneralisedMaximumOfContainment(), 0.2); 
				logger.info("Attribute: ArtistName, Comparator: ArtistNameComporatorGeneralisedMaximumOfContainment");
			}
			if (comparatorMap.get("ArtistNameComporatorGeneralizedJaccard")) {
				matchingRule.addComparator(new ArtistNameComporatorGeneralizedJaccard(), 0.2); 
				logger.info("Attribute: ArtistName, Comparator: ArtistNameComporatorGeneralizedJaccard");
			}

			// album total tracks comparators	
			if (comparatorMap.get("AlbumTotalTracksComparatorAbsoluteDifferenceSimilarity")) {
				matchingRule.addComparator(new AlbumTotalTracksComparatorAbsoluteDifferenceSimilarity(), 0.15); 
				logger.info("Attribute: AlbumTotalTracks, Comparator: AlbumTotalTracksComparatorAbsoluteDifferenceSimilarity");
			}

			if (comparatorMap.get("TotalTracksComparatorDeviationSimilarity")) {
				matchingRule.addComparator(new TotalTracksComparatorDeviationSimilarity(), 0.05); 
				logger.info("Attribute: AlbumTotalTracks, Comparator: TotalTracksComparatorDeviationSimilarity");
			}
			
		}
		
		else if (d1_d2_name.equals("MB_SPY")) {
		
			// album title comparators	
			if (comparatorMap.get("AlbumTitleComparatorLevenshteinLowerCase")) {
				matchingRule.addComparator(new AlbumTitleComparatorLevenshteinLowerCase(), 0.15); 
				logger.info("Attribute: AlbumTitle, Comparator: AlbumTitleComparatorLevenshteinLowerCase");
			}
			if (comparatorMap.get("AlbumTitleComparatorJaccard")) {
				matchingRule.addComparator(new AlbumTitleComparatorJaccard(), 0.05); 
				logger.info("Attribute: AlbumTitle, Comparator: AlbumTitleComparatorJaccard");
			}
			if (comparatorMap.get("AlbumTitleComparatorMaximumOfTokenContainnment")) {
				matchingRule.addComparator(new AlbumTitleComparatorMaximumOfTokenContainnment(), 0.1); 
				logger.info("Attribute: AlbumTitle, Comparator: AlbumTitleComparatorMaximumOfTokenContainnment");
			}

			// artist name comparators
			if (comparatorMap.get("ArtistNameComporatorGeneralisedMaximumOfContainment")) {
				matchingRule.addComparator(new ArtistNameComporatorGeneralisedMaximumOfContainment(), 0.2); 
				logger.info("Attribute: ArtistName, Comparator: ArtistNameComporatorGeneralisedMaximumOfContainment");
			}
			if (comparatorMap.get("ArtistNameComporatorGeneralizedJaccard")) {
				matchingRule.addComparator(new ArtistNameComporatorGeneralizedJaccard(), 0.2); 
				logger.info("Attribute: ArtistName, Comparator: ArtistNameComporatorGeneralizedJaccard");
			}

			// album total tracks comparators	
			if (comparatorMap.get("AlbumTotalTracksComparatorAbsoluteDifferenceSimilarity")) {
				matchingRule.addComparator(new AlbumTotalTracksComparatorAbsoluteDifferenceSimilarity(), 0.15); 
				logger.info("Attribute: AlbumTotalTracks, Comparator: AlbumTotalTracksComparatorAbsoluteDifferenceSimilarity");
			}

			if (comparatorMap.get("TotalTracksComparatorDeviationSimilarity")) {
				matchingRule.addComparator(new TotalTracksComparatorDeviationSimilarity(), 0.05); 
				logger.info("Attribute: AlbumTotalTracks, Comparator: TotalTracksComparatorDeviationSimilarity");
			}
			
			// album date comparators
			if (comparatorMap.get("AlbumDateComparator10Years")) {
				matchingRule.addComparator(new AlbumDateComparator10Years(), 0.03); 
				logger.info("Attribute: AlbumDate, Comparator: AlbumDateComparator10Years");
			}
			
			if (comparatorMap.get("AlbumDateComparatorWeightedDate")) {
				matchingRule.addComparator(new AlbumDateComparatorWeightedDate(), 0.07); 
				logger.info("Attribute: AlbumDate, Comparator: AlbumDateComparatorWeightedDate");

			}
			
		}
		
		
		else if (d1_d2_name.equals("WDC_SPY")) {
			
			// album title comparators	
			if (comparatorMap.get("AlbumTitleComparatorLevenshteinLowerCase")) {
				matchingRule.addComparator(new AlbumTitleComparatorLevenshteinLowerCase(), 0.15); 
				logger.info("Attribute: AlbumTitle, Comparator: AlbumTitleComparatorLevenshteinLowerCase");
			}
			if (comparatorMap.get("AlbumTitleComparatorJaccard")) {
				matchingRule.addComparator(new AlbumTitleComparatorJaccard(), 0.05); 
				logger.info("Attribute: AlbumTitle, Comparator: AlbumTitleComparatorJaccard");
			}
			if (comparatorMap.get("AlbumTitleComparatorMaximumOfTokenContainnment")) {
				matchingRule.addComparator(new AlbumTitleComparatorMaximumOfTokenContainnment(), 0.1); 
				logger.info("Attribute: AlbumTitle, Comparator: AlbumTitleComparatorMaximumOfTokenContainnment");
			}

			// artist name comparators
			if (comparatorMap.get("ArtistNameComporatorGeneralisedMaximumOfContainment")) {
				matchingRule.addComparator(new ArtistNameComporatorGeneralisedMaximumOfContainment(), 0.2); 
				logger.info("Attribute: ArtistName, Comparator: ArtistNameComporatorGeneralisedMaximumOfContainment");
			}
			if (comparatorMap.get("ArtistNameComporatorGeneralizedJaccard")) {
				matchingRule.addComparator(new ArtistNameComporatorGeneralizedJaccard(), 0.1); 
				logger.info("Attribute: ArtistName, Comparator: ArtistNameComporatorGeneralizedJaccard");
			}

			// album total tracks comparators	
			if (comparatorMap.get("AlbumTotalTracksComparatorAbsoluteDifferenceSimilarity")) {
				matchingRule.addComparator(new AlbumTotalTracksComparatorAbsoluteDifferenceSimilarity(), 0.15); 
				logger.info("Attribute: AlbumTotalTracks, Comparator: AlbumTotalTracksComparatorAbsoluteDifferenceSimilarity");
			}

			if (comparatorMap.get("TotalTracksComparatorDeviationSimilarity")) {
				matchingRule.addComparator(new TotalTracksComparatorDeviationSimilarity(), 0.05); 
				logger.info("Attribute: AlbumTotalTracks, Comparator: TotalTracksComparatorDeviationSimilarity");
			}			
			
			// album duration comparators
			if (comparatorMap.get("AlbumDurationComparatorAbsoluteDifferenceSimilarity")) {
				matchingRule.addComparator(new AlbumDurationComparatorAbsoluteDifferenceSimilarity(), 0.2); 
				logger.info("Attribute: AlbumDuration, Comparator: AlbumDurationComparatorAbsoluteDifferenceSimilarity");
			}
			
		}
		
		// create a blocker (blocking strategy)
		StandardRecordBlocker<Album, Attribute> blocker = new StandardRecordBlocker<Album, Attribute>(new AlbumBlockingKeyByTitleGenerator());
		// NoBlocker<Album, Attribute> blocker = new NoBlocker<>();
		// SortedNeighbourhoodBlocker<Movie, Attribute, Attribute> blocker = new SortedNeighbourhoodBlocker<>(new MovieBlockingKeyByTitleGenerator(), 1);
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

		//// Alternative: Create a maximum-weight, bipartite matching
		//  MaximumBipartiteMatchingAlgorithm<Movie,Attribute> maxWeight = new MaximumBipartiteMatchingAlgorithm<>(correspondences);
		//  maxWeight.run();
		//  correspondences = maxWeight.getResult();

		//  write the correspondences to the output file
		new CSVCorrespondenceFormatter().writeCSV(new File("data/output/" + d1_d2_name + "_correspondences.csv"), correspondences);		
		
		// evaluate your result
		MatchingEvaluator<Album, Attribute> evaluator = new MatchingEvaluator<Album, Attribute>();
		Performance perfTest = evaluator.evaluateMatching(correspondences,
				gsTest);
		
		String numberCorrespondence = String.format("%d", correspondences.size());
		logger.info("Found correspondences: " + numberCorrespondence);
		
		return new Pair<Performance, String>(perfTest, numberCorrespondence);	
	}
	private static Pair<Performance, String> identityResolutionLearnedWeights(HashedDataSet<Album, Attribute> d1, HashedDataSet<Album, Attribute> d2, String d1_d2_name, String gs_name, Map<String, Boolean> comparatorMap, AbstractBlocker blocker2) throws Exception {
		// load the gold standard (test set)
		logger.info("*\tLoading gold standard\t* " + d1_d2_name);
		
		MatchingGoldStandard gsTraining = new MatchingGoldStandard();
		gsTraining.loadFromCSVFile(new File("data/goldstandard/" + gs_name  + "_train.csv"));

		// create a matching rule
		String options[] = new String[] { "-S" }; // save model? or what is this for?
		String modelType = "SimpleLogistic"; // use a logistic regression
		WekaMatchingRule<Album, Attribute> matchingRule = new WekaMatchingRule<>(0.7, modelType, options);
		matchingRule.activateDebugReport("data/output/debugResultsMatchingRuleLearnedWeights" + d1_d2_name + ".csv", 100_000, gsTraining);

		// add comparators
		logger.info("*\tAdding comparators\t*");

		// album title comparators
		if (comparatorMap.get("AlbumTitleComparatorLevenshteinLowerCase")) {
			matchingRule.addComparator(new AlbumTitleComparatorLevenshteinLowerCase()); 
			logger.info("Model: " + modelType + " Attribute: AlbumTitle, Comparator: AlbumTitleComparatorLevenshteinLowerCase");
		}
		if (comparatorMap.get("AlbumTitleComparatorJaccard")) {
			matchingRule.addComparator(new AlbumTitleComparatorJaccard()); 
			logger.info("Model: " + modelType + " Attribute: AlbumTitle, Comparator: AlbumTitleComparatorJaccard");
		}
		if (comparatorMap.get("AlbumTitleComparatorMaximumOfTokenContainnment")) {
			matchingRule.addComparator(new AlbumTitleComparatorMaximumOfTokenContainnment()); 
			logger.info("Model: " + modelType + " Attribute: AlbumTitle, Comparator: AlbumTitleComparatorMaximumOfTokenContainnment");
		}

		// artist name comparators
		if (comparatorMap.get("ArtistNameComporatorGeneralisedMaximumOfContainment")) {
			matchingRule.addComparator(new ArtistNameComporatorGeneralisedMaximumOfContainment()); 
			logger.info("Model: " + modelType + " Attribute: ArtistName, Comparator: ArtistNameComporatorGeneralisedMaximumOfContainment");
		}
		if (comparatorMap.get("ArtistNameComporatorGeneralizedJaccard")) {
			matchingRule.addComparator(new ArtistNameComporatorGeneralizedJaccard()); 
			logger.info("Model: " + modelType + " Attribute: ArtistName, Comparator: ArtistNameComporatorGeneralizedJaccard");
		}

		// album total tracks comparators	
		if (comparatorMap.get("AlbumTotalTracksComparatorAbsoluteDifferenceSimilarity")) {
			matchingRule.addComparator(new AlbumTotalTracksComparatorAbsoluteDifferenceSimilarity()); 
			logger.info("Model: " + modelType + " Attribute: AlbumTotalTracks, Comparator: AlbumTotalTracksComparatorAbsoluteDifferenceSimilarity");
		}

		if (comparatorMap.get("TotalTracksComparatorDeviationSimilarity")) {
			matchingRule.addComparator(new TotalTracksComparatorDeviationSimilarity()); 
			logger.info("Model: " + modelType + " Attribute: AlbumTotalTracks, Comparator: TotalTracksComparatorDeviationSimilarity");
		}
		
		// track names comparators
		if (comparatorMap.get("TrackNameComporatorGeneralisedMaximumOfContainment")) {
			matchingRule.addComparator(new TrackNameComporatorGeneralisedMaximumOfContainment()); 
			logger.info("Model: " + modelType + " Attribute: TrackName, Comparator: TrackNameComporatorGeneralisedMaximumOfContainment");
		}

		// album date comparators
		if (comparatorMap.get("AlbumDateComparator10Years")) {
			matchingRule.addComparator(new AlbumDateComparator10Years()); 
			logger.info("Model: " + modelType + " Attribute: AlbumDate, Comparator: AlbumDateComparator10Years");
		}

		// album duration comparators
		if (comparatorMap.get("AlbumDurationComparatorAbsoluteDifferenceSimilarity")) {
			matchingRule.addComparator(new AlbumDurationComparatorAbsoluteDifferenceSimilarity()); 
			logger.info("Model: " + modelType + " Attribute: AlbumDuration, Comparator: AlbumDurationComparatorAbsoluteDifferenceSimilarity");
		}

		// train the matching rule's model
		logger.info("*\tLearning matching rule\t*");
		RuleLearner<Album, Attribute> learner = new RuleLearner<>();
		learner.learnMatchingRule(d1, d2, null, matchingRule, gsTraining);
		logger.info(String.format("Matching rule is:\n%s", matchingRule.getModelDescription()));

		// create a blocker (blocking strategy)
		StandardRecordBlocker<Album, Attribute> blocker = new StandardRecordBlocker<Album, Attribute>(new AlbumBlockingKeyByTitleGenerator());
		// NoBlocker<Album, Attribute> blocker = new NoBlocker<>();
		// SortedNeighbourhoodBlocker<Movie, Attribute, Attribute> blocker = new SortedNeighbourhoodBlocker<>(new MovieBlockingKeyByTitleGenerator(), 1);
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

		//// Alternative: Create a maximum-weight, bipartite matching
		//  MaximumBipartiteMatchingAlgorithm<Movie,Attribute> maxWeight = new MaximumBipartiteMatchingAlgorithm<>(correspondences);
		//  maxWeight.run();
		//  correspondences = maxWeight.getResult();

		//  write the correspondences to the output file
		new CSVCorrespondenceFormatter().writeCSV(new File("data/output/" + d1_d2_name + "_correspondences.csv"), correspondences);		
		
		// load the gold standard (test set)
		logger.info("*\tLoading gold standard\t*");
		MatchingGoldStandard gsTest = new MatchingGoldStandard();
		gsTest.loadFromCSVFile(new File(
				"data/goldstandard/" + gs_name  + "_test.csv"));

		// evaluate your result
		MatchingEvaluator<Album, Attribute> evaluator = new MatchingEvaluator<Album, Attribute>();
		Performance perfTest = evaluator.evaluateMatching(correspondences,
				gsTest);

		String numberCorrespondence = String.format("%d", correspondences.size());
		logger.info("Found correspondences: " + numberCorrespondence);
		
		return new Pair<Performance, String>(perfTest, numberCorrespondence);	
	}
}