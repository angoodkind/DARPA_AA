package extractors.data;

/**
 * This interface allows for a process to run once all TestVector queries 
 * are complete
 * <p><p>
 * This is necessary, since TestVector querying divides up a user into 
 * multiple scans, and does not give indication as to when a user's scanning
 * is complete
 * 
 * @see FeatureExtractor
 * @see ExtractionModule
 * 
 * @author Adam Goodkind
 *
 */

public interface TestVectorShutdownModule {
	
	/**
	 * Called at the completion of TestVector creation, when all extraction is complete,
	 * for every scan of every user. This is done in order to allow for a final operation
	 * in TestVector creation, similar to after all Answers are scanned in Template creation.
	 */
	public void shutdown();
	

}
