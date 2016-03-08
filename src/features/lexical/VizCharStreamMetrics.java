/**
 * 
 */
package features.lexical;

import java.util.Collection;

import extractors.data.Answer;
import extractors.data.DataNode;
import extractors.data.ExtractionModule;
import extractors.data.Feature;
import extractors.lexical.VisualCharStream;

/**
 * @author agoodkind
 *
 */
public class VizCharStreamMetrics extends VisualCharStream implements ExtractionModule {

	/* 
	 * @see extractors.nyit.ExtractionModule#extract(extractors.nyit.DataNode)
	 */
	@Override
	public Collection<Feature> extract(DataNode data) {
		
		for (Answer a : data) {
			
			VisualCharStream vcs = new VisualCharStream(a.getKeyStrokes());
			System.out.println(vcs.toString());
			
		}
		
		return null;
	}

	/* 
	 */
	@Override
	public String getName() {
		return null;
	}
	
	

}
