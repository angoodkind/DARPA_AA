package output.util;

import java.sql.SQLException;

import extractors.data.DataSelector;
import extractors.data.DbConfig;
import extractors.data.FeatureExtractor;

/**
 * Creates a directory of TestVectors, but without
 * running the AppWindow script
 * - Called from VectorToInstancesConverter
 * 
 * @author Adam Goodkind
 *
 */
public class TestVectorCreator {
	
	private static final boolean DEBUG = true; // to print some error in output
	private static DataSelector ds = null; // for querying database
	private String collectionName = "collection1";
	private String outputDirectory;
	private boolean allowPartials = true;
	private int sliceSize = 99;
	private boolean doWrapping = false;
	private String unit = "minutes";
	private boolean incremental = false;
	private int partialPercent = 0;
	private String queryString;

	public TestVectorCreator(String outputDir, String queryString) {
		this.outputDirectory = outputDir;
		this.queryString = queryString;
	}
	public void query() {
		DbConfig.setCollectionName(collectionName);
		FeatureExtractor extractor;
		// "Initialize + query database
		try {
			ds = new DataSelector();
			System.out.println("Connected to Database.");
		} catch (ClassNotFoundException | InstantiationException
				| IllegalAccessException | SQLException e) {
			System.out.println("ERROR: " + e.toString());
		}
		
//		try {
//			ds.maintenanceQuery("ALTER TABLE `collection2`.`userdata` DROP COLUMN `LastName` , DROP COLUMN `FirstName`");
//		} catch (SQLException e1) {
//			e1.printStackTrace();
//		}
		
		System.out.println("Fetching Data...");
		ds.setQuery(queryString);
		
		try {
			ds.query();
		} catch (SQLException e) {
			System.out.println("ERROR : " + e.toString());
		}
		
		System.out.println("Data Loaded Successfully.");
		
		////end query database
		
		extractor = new FeatureExtractor(ds.getData());
		extractor.setQuery(ds.getQuery());
		
		////CREATE TEST VECTORS
		extractor.setCustomOutputDirectory(outputDirectory);
		extractor.setSuppressSystemOut(false);
		if (!ds.getData().isEmpty()) {
			try {
				extractor.loadModulesExternal();
				extractor.createTestVectors(unit, sliceSize,
						doWrapping, allowPartials, partialPercent,
						incremental, 0, 0);
			} catch (Exception e) {
				if (DEBUG)
					e.printStackTrace();
				System.out.println("ERROR: " + e.toString());
			}
		}
		extractor.flushModules();

		System.out.println("Vector Creation Complete.");
		//END CREATE TEST VECTORS
			
	}
	

}
