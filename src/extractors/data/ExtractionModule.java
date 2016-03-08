package extractors.data;

import java.util.Collection;

/**
 * This interface allows for the modularization of feature extraction.
 * <p><p>
 * All feature extraction modules must implement this interface as the
 * FeatureExtrator class requires this for dynamic module loading and iteration.
 * <p><p>
 * Extraction Modules should live in their own packages. Modules produced
 * at NYIT are contained in the edu.nyit.modules package.
 * <p><p>
 * Modules are dynamically loaded from modules.conf during template creation
 * and/or testing.
 *  
 * @author Patrick Koch
 * @see FeatureExtractor
 */
public interface ExtractionModule {
	
	/**
	 * Extracts feature information from a DataNode.
	 * 
	 * The return String should contain comma separated feature values.
	 * <p>It should not contain any newline or carriage returns.
	 * 
	 * @param data DataNode from data selection phase. 
	 * @return Collection of Feature objects.
	 * 
	 * @see Feature
	 */
	public Collection<Feature> extract (DataNode data) ;
	
	/**
	 * Returns the name of the module.
	 * 
	 * @return String corresponding to the name of the module.
	 */
	public String getName();
	
}
