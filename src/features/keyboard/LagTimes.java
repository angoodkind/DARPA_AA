/**
 * 
 */
package features.keyboard;

import java.util.Collection;

import extractors.data.Answer;
import extractors.data.DataNode;
import extractors.data.ExtractionModule;
import extractors.data.Feature;
import keystroke.KeyStroke;

/**
 * @author agoodkind
 * Extract all lag times between keypresses
 *
 */
public class LagTimes implements ExtractionModule{

	

	@Override
	public Collection<Feature> extract(DataNode data) {
		
		for (Answer a : data) {
			for (KeyStroke k : a.getKeyStrokeList()) {
				if (k.isKeyPress())
					System.out.println(k.getKeyChar()+"\\"+k.getWhen());
			}
		}

		return null;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

}
