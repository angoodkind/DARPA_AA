package appwindow;


import java.sql.SQLException;

import extractors.data.DataSelector;
import extractors.data.DbConfig;
import extractors.data.FeatureExtractor;
import features.nyit.PR_PR_BURST;
import features.nyit.PR_P_BURST;
import features.nyit.P_P_BURST;

/*
 * Script to automaticaly create datasets with different parameters.
 * Results should be (this was checked) the same as from AppWindow.java. 
 * User can set up collection name, length of a pause, length of cuts for testing data
 * 
 * @author Zdenka Sitova
 */

public class AppWindowScript {
	private static final boolean DEBUG = true; // to print some error in output
	private static DataSelector ds = null; // for querying database
	//check settings of P_P_BURST.java files, modules.conf, PipelineModuleList, this script is not setting up everything! (currently only length of a pause in milliseconds and collection name)
	//For PR_P/PR_PR/P_PR/P_P, you have to change not only modules.conf, but also change line 56 ("P_P_BURST.setPauseTimeInMs")

	public static void main(String[] args) {
		int pauseLength = 0; // sets pause length
		String collectionName = "";
		int[] secondsIntervalArray = { 30, 60, 90, 120, 150, 180, 210 };
		int[] pauseLengthArray = {1000, 1500, 2000}; 
		int[] collectionNumberArray = {1, 2};
		
		String currentOutputDirectory = "";
		String beginningOutputDirectory = "";
		boolean allowPartials = true;
		boolean doWrapping = false;
		String unit = "seconds";
		boolean incremental = false;
		int partialPercent = 0;
		
		
		boolean operationMode = DEBUG;
		for (int k = 0; k < collectionNumberArray.length; k++) {
			collectionName = "collection" + Integer.toString(collectionNumberArray[k]);
			DbConfig.setCollectionName(collectionName);
			beginningOutputDirectory = "C:\\Spring 2014\\02_10_2014_automatic\\Collection" + Integer.toString(collectionNumberArray[k] )+ "\\PR_PR_cut_between_P_P\\"; 
			for (int j = 0; j < pauseLengthArray.length; j++) {
				FeatureExtractor extractor;
				pauseLength = pauseLengthArray[j];
				PR_PR_BURST.setPauseTimeInMs(pauseLength);
				currentOutputDirectory = beginningOutputDirectory + "\\P=" + Integer.toString(pauseLength) + "\\template"; 
				
				
				// "Initialize + query database
				try {
					ds = new DataSelector();
					System.out.println("Connected to Database.");
				} catch (ClassNotFoundException | InstantiationException
						| IllegalAccessException | SQLException e) {
					System.out.println("ERROR: " + e.toString());
				}
	
				try {
					ds.maintenanceQuery("ALTER TABLE `collection2`.`userdata` DROP COLUMN `LastName` , DROP COLUMN `FirstName`");
				} catch (SQLException e1) {
					// be silent
				}
	
				System.out.println("Fetching Data...");
				ds.setQuery("SELECT * FROM training;");
				try {
					ds.query();
				} catch (SQLException e) {
					System.out.println("ERROR : " + e.toString());
				}
	
				System.out.println("Data Loaded Successfully.");
	
				////end query database
				
				extractor = new FeatureExtractor(ds.getData());
				extractor.setQuery(ds.getQuery());
				extractor.setCustomOutputDirectory(currentOutputDirectory);
				extractor.setSuppressSystemOut(false);
				try {
					extractor.loadModules();
					if (!ds.isEmpty())
						extractor.createTemplates();
					else if (ds.isEmpty())
						throw new Exception("No Data Loaded.");
				} catch (Exception e) {
					if (operationMode == DEBUG)
						e.printStackTrace();
					System.out.println("ERROR : " + e.toString());
				}
				extractor.flushModules();
	
				System.out.println("Template Creation Complete.");
				ds = null;
				extractor = null;
				System.gc(); //call garbage collector
				/// END CREATE TEMPLATES
				 
				
				///"Initialize + query database for testing
				try {
					ds = new DataSelector();
					System.out.println("Connected to Database.");
				} catch (ClassNotFoundException | InstantiationException
						| IllegalAccessException | SQLException e) {
					System.out.println("ERROR: " + e.toString());
				}
	
				try {
					ds.maintenanceQuery("ALTER TABLE `collection2`.`userdata` DROP COLUMN `LastName` , DROP COLUMN `FirstName`");
				} catch (SQLException e1) {
					// be silent
				}
	
				System.out.println("Fetching Data...");
				ds.setQuery("SELECT * FROM testing;");
				try {
					ds.query();
				} catch (SQLException e) {
					System.out.println("ERROR : " + e.toString());
				}
	
				System.out.println("Data Loaded Successfully.");
				////end query database
				extractor = new FeatureExtractor(ds.getData());
				extractor.setQuery(ds.getQuery());
				
				///TESTING:
				for (int i = 0; i < secondsIntervalArray.length; i++) {
					int currentSliceSize = secondsIntervalArray[i];
					currentOutputDirectory = beginningOutputDirectory + "\\P=" + Integer.toString(pauseLength) + "\\" +
						  Integer.toString(currentSliceSize) + "sec"; 
					
					////CREATE TEST VECTORS
					extractor.setCustomOutputDirectory(currentOutputDirectory);
					extractor.setSuppressSystemOut(false);
					if (!ds.getData().isEmpty()) {
						try {
							extractor.loadModules();
							extractor.createTestVectors(unit, currentSliceSize,
									doWrapping, allowPartials, partialPercent,
									incremental, 0, 0);
						} catch (Exception e) {
							if (operationMode == DEBUG)
								e.printStackTrace();
							System.out.println("ERROR: " + e.toString());
						}
					}
					extractor.flushModules();
	
					System.out.println("Vector Creation Complete.");
					//END CREATE TEST VECTORS
	
				}
				ds = null;
				extractor = null;
				System.gc();
				
			}
		}
	}

}
