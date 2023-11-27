package de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution;

import java.io.File;

import org.slf4j.Logger;

import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.Blocking.AlbumBlockingKeyByTitleGenerator;
// import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.Comparators.AlbumTitleComparatorLevenshtein;
import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.Comparators.AlbumTitleComparatorLevenshteinLowerCase;
import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.Comparators.ArtistNameComporatorGeneralisedMaximumOfContainment;
import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.Comparators.ArtistNameComporatorGeneralizedJaccard;
import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.Comparators.TotalTracksComparatorDeviationSimilarity;
import de.uni_mannheim.informatik.dws.wdi.ExerciseIdentityResolution.Comparators.TrackNameComporatorGeneralisedMaximumOfContainment;
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
import de.uni_mannheim.informatik.dws.winter.matching.rules.WekaMatchingRule;
import de.uni_mannheim.informatik.dws.winter.model.Correspondence;
import de.uni_mannheim.informatik.dws.winter.model.HashedDataSet;
import de.uni_mannheim.informatik.dws.winter.model.MatchingGoldStandard;
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
		
		Performance perfTest_MB_SPY;
		Performance perfTest_WDC_MB;
		Performance perfTest_WDC_SPY;
		Boolean learn_weights = true;

		if (learn_weights == true) {
			perfTest_MB_SPY = identityResolutionLearnedWeights(dataMB, dataSpotify, "MB_SPY", "gs_mb_spy");
			perfTest_WDC_MB = identityResolutionLearnedWeights(dataWDC, dataMB, "WDC_MB", "gs_wdc_mb");
			perfTest_WDC_SPY = identityResolutionLearnedWeights(dataWDC, dataSpotify, "WDC_SPY", "gs_wdc_spy");
		}
		else {
			perfTest_MB_SPY = identityResolution(dataMB, dataSpotify, "MB_SPY", "gs_mb_spy");
			perfTest_WDC_MB = identityResolution(dataWDC, dataMB, "WDC_MB", "gs_wdc_mb");
			perfTest_WDC_SPY = identityResolution(dataWDC, dataSpotify, "WDC_SPY", "gs_wdc_spy");
		}
		
		// print the evaluation result
		logger.info("*\tEvaluating result: MusicBrainz <-> Spotify");
		printEvalPerf(perfTest_MB_SPY);
		
		logger.info("*\tEvaluating result: WebDataCommons <-> MusicBrainz");
		printEvalPerf(perfTest_WDC_MB);

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
		matchingRule.activateDebugReport("data/output/debugResultsMatchingRule" + d1_d2_name + ".csv", 100_000, gsTest);
		
		// add comparators
		// album title comparators
		matchingRule.addComparator(new AlbumTitleComparatorLevenshteinLowerCase(), 0.15); // WDC-MB: 0.15
		matchingRule.addComparator(new AlbumTitleComparatorJaccard(), 0.15); // WDC-MB: 0.15
		
		// artist name comparators
		matchingRule.addComparator(new ArtistNameComporatorGeneralisedMaximumOfContainment(), 0.3); // WDC-MB: 0.3
		matchingRule.addComparator(new ArtistNameComporatorGeneralizedJaccard(), 0.2); // WDC-MB: 0.2
		
		// album total tracks comparators	
		matchingRule.addComparator(new AlbumTotalTracksComparatorAbsoluteDifferenceSimilarity(), 0.1); // WDC-MB: 0.1
		matchingRule.addComparator(new TotalTracksComparatorDeviationSimilarity(), 0.1); // WDC-MB: 0.1
		// matchingRule.addComparator(new AlbumTotalTracksComparatorDeviationSimilarity(), 0.2); // this does not work
		
		// track names comparators
		// matchingRule.addComparator(new TrackNameComporatorGeneralisedMaximumOfContainment(), 0.2);
		
		// album date comparators
		matchingRule.addComparator(new AlbumDateComparator10Years(), 0.1);

		// album duration comparators
		matchingRule.addComparator(new AlbumDurationComparatorAbsoluteDifferenceSimilarity(), 0.15);
		
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
		
		return perfTest;
	}

	private static Performance identityResolutionLearnedWeights(HashedDataSet<Album, Attribute> d1, HashedDataSet<Album, Attribute> d2, String d1_d2_name, String gs_name) throws Exception {
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
		// album title comparators
		matchingRule.addComparator(new AlbumTitleComparatorLevenshteinLowerCase()); 
		matchingRule.addComparator(new AlbumTitleComparatorJaccard()); 
		
		// artist name comparators
		matchingRule.addComparator(new ArtistNameComporatorGeneralisedMaximumOfContainment()); 
		matchingRule.addComparator(new ArtistNameComporatorGeneralizedJaccard()); 
		
		// album total tracks comparators	
		matchingRule.addComparator(new AlbumTotalTracksComparatorAbsoluteDifferenceSimilarity());
		matchingRule.addComparator(new TotalTracksComparatorDeviationSimilarity()); 
		matchingRule.addComparator(new AlbumTotalTracksComparatorDeviationSimilarity()); // this does not work
		
		// track names comparators
		matchingRule.addComparator(new TrackNameComporatorGeneralisedMaximumOfContainment());
		
		// album date comparators
		matchingRule.addComparator(new AlbumDateComparator10Years());

		// album duration comparators
		matchingRule.addComparator(new AlbumDurationComparatorAbsoluteDifferenceSimilarity());

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
		
		return perfTest;	
	}

}