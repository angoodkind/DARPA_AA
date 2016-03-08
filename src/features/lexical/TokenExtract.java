/**
 * 
 */
package features.lexical;

import java.util.ArrayList;
import java.util.Collection;

import extractors.data.Answer;
import extractors.data.DataNode;
import extractors.data.ExtractionModule;
import extractors.data.Feature;
import extractors.lexical.Tokenize;
import features.pause.KSE;

/**
 * @author agoodkind
 *
 */
public class TokenExtract extends Tokenize implements ExtractionModule {

	/* (non-Javadoc)
	 * @see extractors.nyit.ExtractionModule#extract(extractors.nyit.DataNode)
	 */
	@Override
	public Collection<Feature> extract(DataNode data) {
		
		for (Answer a : data) {
			
			// create KSE array
			KSE[] answerKseArray = KSE.parseSessionToKSE(a.getKeyStrokes()).toArray(new KSE[0]);
			
			// instantiate and fill an array of only keyPresses (as opposed to keyReleases)
			ArrayList<KSE> keypressKseArray = new ArrayList<KSE>();
			for (KSE kse : answerKseArray) 
				if (kse.isKeyPress())
					keypressKseArray.add(kse);
			
			for (KSE kse : keypressKseArray) {
				System.out.println(kse.getM_pauseMs());
			}
			
//			try {
//				String charStream = a.getCharStream();
//				
//				String[] tokens = runTokenizer(a.getCharStream());
			
			
//			} catch (IOException e) {e.printStackTrace();
			
//			try {
//				System.out.println(Arrays.toString(runTokenizer(s)));
//			} catch (InvalidFormatException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
		}
		
		
		
		return null;
	}

	/* (non-Javadoc)
	 * @see extractors.nyit.ExtractionModule#getName()
	 */
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}
	
	

}
