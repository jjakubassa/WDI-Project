package de.uni_mannheim.informatik.dws.wdi.ExerciseDataFusion;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.Locale;

import de.uni_mannheim.informatik.dws.wdi.ExerciseDataFusion.evaluation.AlbumTitleEvaluationRule;
import de.uni_mannheim.informatik.dws.wdi.ExerciseDataFusion.evaluation.DirectorEvaluationRule;
import de.uni_mannheim.informatik.dws.wdi.ExerciseDataFusion.evaluation.DurationEvaluationRule;
import de.uni_mannheim.informatik.dws.wdi.ExerciseDataFusion.evaluation.ReleaseDateEvaluationRule;
import de.uni_mannheim.informatik.dws.wdi.ExerciseDataFusion.evaluation.TotalTracksEvaluationRule;
import de.uni_mannheim.informatik.dws.wdi.ExerciseDataFusion.fusers.DateFuserFavourSource;
import de.uni_mannheim.informatik.dws.wdi.ExerciseDataFusion.fusers.DateFuserVoting;
import de.uni_mannheim.informatik.dws.wdi.ExerciseDataFusion.fusers.DurationFuserAverage;
import de.uni_mannheim.informatik.dws.wdi.ExerciseDataFusion.fusers.TitleFuserLongestString;
import de.uni_mannheim.informatik.dws.wdi.ExerciseDataFusion.fusers.TotalTracksFuserVoting;
import de.uni_mannheim.informatik.dws.wdi.ExerciseDataFusion.model.FusibleAlbumFactory;
import de.uni_mannheim.informatik.dws.wdi.ExerciseDataFusion.model.Album;
import de.uni_mannheim.informatik.dws.wdi.ExerciseDataFusion.model.AlbumXMLFormatter;
import de.uni_mannheim.informatik.dws.wdi.ExerciseDataFusion.model.AlbumXMLReader;
import de.uni_mannheim.informatik.dws.winter.datafusion.CorrespondenceSet;
import de.uni_mannheim.informatik.dws.winter.datafusion.DataFusionEngine;
import de.uni_mannheim.informatik.dws.winter.datafusion.DataFusionEvaluator;
import de.uni_mannheim.informatik.dws.winter.datafusion.DataFusionStrategy;
import de.uni_mannheim.informatik.dws.winter.model.DataSet;
import de.uni_mannheim.informatik.dws.winter.model.FusibleDataSet;
import de.uni_mannheim.informatik.dws.winter.model.FusibleHashedDataSet;
import de.uni_mannheim.informatik.dws.winter.model.RecordGroupFactory;
import de.uni_mannheim.informatik.dws.winter.model.defaultmodel.Attribute;
import de.uni_mannheim.informatik.dws.winter.utils.WinterLogManager;
import org.slf4j.Logger;

public class DataFusion_Main 
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

	private static final Logger logger = WinterLogManager.activateLogger("trace");
	
	public static void main( String[] args ) throws Exception
    {
		// Load the Data into FusibleDataSet
		logger.info("*\tLoading datasets\t*");
		FusibleDataSet<Album, Attribute> ds1 = new FusibleHashedDataSet<>();
		new AlbumXMLReader().loadFromXML(new File("data/input/WDC.xml"), "/Albums/Album", ds1);
		ds1.printDataSetDensityReport();

		FusibleDataSet<Album, Attribute> ds2 = new FusibleHashedDataSet<>();
		new AlbumXMLReader().loadFromXML(new File("data/input/MB.xml"), "/Albums/Album", ds2);
		ds2.printDataSetDensityReport();

		FusibleDataSet<Album, Attribute> ds3 = new FusibleHashedDataSet<>();
		new AlbumXMLReader().loadFromXML(new File("data/input/SPY.xml"), "/Albums/Album", ds3);
		ds3.printDataSetDensityReport();

		// Maintain Provenance
		// Scores (e.g. from rating)
		ds1.setScore(1.0);
		ds2.setScore(2.0);
		ds3.setScore(3.0);

		// Date (e.g. last update)
		DateTimeFormatter formatter = new DateTimeFormatterBuilder()
	 	        .appendPattern("yyyy-MM-dd")
		        .parseDefaulting(ChronoField.CLOCK_HOUR_OF_DAY, 0)
		        .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
		        .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
		        .toFormatter(Locale.ENGLISH);
		
		ds1.setDate(LocalDateTime.parse("2012-01-01", formatter));
		ds2.setDate(LocalDateTime.parse("2010-01-01", formatter));
		ds3.setDate(LocalDateTime.parse("2008-01-01", formatter));

		// load correspondences
		// TODO: find correct data of our data
		logger.info("*\tLoading correspondences\t*");
		CorrespondenceSet<Album, Attribute> correspondences = new CorrespondenceSet<>();
		correspondences.loadCorrespondences(new File("data/correspondences/WDC_MB_correspondences.csv"),ds1, ds2);
		correspondences.loadCorrespondences(new File("data/correspondences/WDC_SPY_correspondences.csv"),ds1, ds3);
		correspondences.loadCorrespondences(new File("data/correspondences/MB_SPY_correspondences.csv"),ds2, ds3);

		// write group size distribution
		correspondences.printGroupSizeDistribution();

		// // load the gold standard
		logger.info("*\tEvaluating results\t*");
		DataSet<Album, Attribute> gs = new FusibleHashedDataSet<>();
		new AlbumXMLReader().loadFromXML(new File("data/goldstandard/gold.xml"), "Albums/Album", gs);

		for(Album m : gs.get()) {
			logger.info(String.format("gs: %s", m.getIdentifier()));
		}

		// define the fusion strategy
		DataFusionStrategy<Album, Attribute> strategy = new DataFusionStrategy<>(new AlbumXMLReader());
		// write debug results to file
		strategy.activateDebugReport("data/output/debugResultsDatafusion.csv", -1, gs);
		
		// add attribute fusers
		strategy.addAttributeFuser(Album.TITLE, new TitleFuserLongestString(),new AlbumTitleEvaluationRule());
		strategy.addAttributeFuser(Album.DURATION, new DurationFuserAverage(),new DurationEvaluationRule());
		strategy.addAttributeFuser(Album.TOTALTRACKS, new TotalTracksFuserVoting(),new TotalTracksEvaluationRule());
		strategy.addAttributeFuser(Album.RELEASEDATE, new DateFuserVoting(),new ReleaseDateEvaluationRule());

		// strategy.addAttributeFuser(Album.DIRECTOR,new DirectorFuserLongestString(), new DirectorEvaluationRule());
		// strategy.addAttributeFuser(Album.DATE, new DateFuserFavourSource(),new DateEvaluationRule());
		// strategy.addAttributeFuser(Album.ACTORS,new ActorsFuserUnion(),new ActorsEvaluationRule());
		
		// create the fusion engine
		DataFusionEngine<Album, Attribute> engine = new DataFusionEngine<>(strategy);

		// print consistency report
		engine.printClusterConsistencyReport(correspondences, null);
		
		// print record groups sorted by consistency
		engine.writeRecordGroupsByConsistency(new File("data/output/recordGroupConsistencies.csv"), correspondences, null);

		// run the fusion
		logger.info("*\tRunning data fusion\t*");
		FusibleDataSet<Album, Attribute> fusedDataSet = engine.run(correspondences, null);

		// write the result
		new AlbumXMLFormatter().writeXML(new File("data/output/fused.xml"), fusedDataSet);

		// evaluate
		DataFusionEvaluator<Album, Attribute> evaluator = new DataFusionEvaluator<>(strategy, new RecordGroupFactory<Album, Attribute>());
		
		double accuracy = evaluator.evaluate(fusedDataSet, gs, null);

		logger.info(String.format("*\tAccuracy: %.2f", accuracy));
    }
}
