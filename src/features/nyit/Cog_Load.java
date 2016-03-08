package features.nyit;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;

import extractors.data.Answer;
import extractors.data.DataNode;
import extractors.data.ExtractionModule;
import extractors.data.Feature;
import keystroke.KeyStroke;

public class Cog_Load implements ExtractionModule {

	LinkedList<KeyStroke> buffer = new LinkedList<KeyStroke>();

	private HashMap<Integer, LinkedList<Long>> featureMap = new HashMap<Integer, LinkedList<Long>>(60);

	public Collection<Feature> extract(DataNode data) {
		createSearchSpace();

		LinkedList<KeyStroke> buffer = new LinkedList<KeyStroke>();
		for (Answer a : data) {
			//long interval = 0;
			int j=0;
			//int count = 0;
			buffer.clear();
			for (KeyStroke k : a.getKeyStrokeList()) {

				if (buffer.size() == 4) {
					//System.out.println("buffer > 3");
					if(isInterval(buffer)) {
						//System.out.println("Yes Interval");
						//interval += buffer.get(2).getWhen() - buffer.get(1).getWhen();
						//count++;
						//if(count>0) {
						j++;
						System.out.println(/*(long)interval/3*/buffer.get(2).getKeyChar() + " "+ buffer.get(1).getKeyChar());
						featureMap.get(j).add(/*(long)interval/3*/buffer.get(2).getWhen() - buffer.get(1).getWhen());
						//interval = 0;
						buffer.poll();
						//}
					}
					buffer.poll();
				}
				if (k.isAlphaNumeric()||k.getKeyChar()==KeyStroke.CHAR_UNDEFINED||(Character.toString(k.getKeyChar()).matches("\\p{Punct}"))||k.isSpace()||k.isBackspace())
					buffer.add(k);
			}
		}
		LinkedList<Feature> output = new LinkedList<Feature>();
		//(i=71 for TH), (i=22 for ER), (i=24 for IN)
		for (int i = 1; i < 27; i++) {
			output.add(new Feature( "AVG_" + "TH_" + i,featureMap.get(i)));
		}
		return output;
	}

	private void createSearchSpace() {
		featureMap.clear();
		for (int i = 1; i < 27; i++)
			featureMap.put(i, new LinkedList<Long>());
	}


	/**
	 * Determines if the buffer contains a key Interval.
	 * 
	 * @return Hashtable to be used during extraction.
	 */
	private boolean isInterval(LinkedList<KeyStroke> buffer) {
		//System.out.println("In Interval");
		// if the keys follow the pattern press, release, press, release.
		if (buffer.get(0).isKeyPress() && buffer.get(1).isKeyRelease()
				&& buffer.get(2).isKeyPress() && buffer.get(3).isKeyRelease()) {				 
			if (buffer.get(0).getKeyCode() == buffer.get(1).getKeyCode() 
					&& buffer.get(2).getKeyCode() == buffer.get(3).getKeyCode()) {
				// if the keys follow pattern T, T, H, H(TH_84,72)(IN-73,78)(ER-69,82)
				if (buffer.get(0).getKeyCode() == 84 && buffer.get(1).getKeyCode() == 84 
						&& buffer.get(2).getKeyCode() == 72 && buffer.get(3).getKeyCode() == 72) {
					return true;
				}
			}					
		}
		return false;
	}

	public String getName() {
		return "Key Interval";
	}
}