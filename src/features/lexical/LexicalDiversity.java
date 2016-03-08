package features.lexical;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;

import extractors.data.DataNode;
import extractors.data.ExtractionModule;
import extractors.data.Feature;
import extractors.lexical.Tokenize;

/**
 * @author agoodkind
 *	measures different forms of lexical diversity
 *	- Standard Type-Token Ratio
 *	- MATTR (Moving-Average Type Token Ratio
 */
public class LexicalDiversity extends Tokenize implements ExtractionModule {

	/* 
	 * @see extractors.nyit.ExtractionModule#extract(extractors.nyit.DataNode)
	 */
	@Override
	public Collection<Feature> extract(DataNode data) {
//		System.out.println("Creating instance of "+this.getClass().toString());
		Collection<String> finalAnswers = data.getFinalTextList();
		Collection<Double> typeTokenRatios = new LinkedList<Double>();
		Collection<Double> mattrLargeWindowList = new LinkedList<Double>();
		Collection<Double> mattrSmallWindowList = new LinkedList<Double>();
		Collection<Double> mattrWindowRatioList = new LinkedList<Double>();
		LinkedList<Feature> output = new LinkedList<Feature>();
		
		// Parameters for MATTR calculation (suggested by Michael Covington)
		int minimumLength = 40;
		int largeWindow = 35;
		int smallWindow = 10;

		for (String f : finalAnswers) {
			try {
				
				typeTokenRatios.add(calculateTTR(f));
				double largeWindowMATTR = Double.NaN;
				double smallWindowMATTR = Double.NaN;
				double mattrWindowRatio = Double.NaN;
				
				if (f.length() > minimumLength) {
					largeWindowMATTR = calculateMATTR(f,largeWindow);
					smallWindowMATTR = calculateMATTR(f,smallWindow);
					mattrWindowRatio = smallWindowMATTR/largeWindowMATTR;
				}
				
//				System.out.println(data.getUserID()+","
//									+calculateTTR(f));
				
				mattrLargeWindowList.add(largeWindowMATTR);
				mattrSmallWindowList.add(smallWindowMATTR);
				mattrWindowRatioList.add(mattrWindowRatio);
				
			} catch (IOException e) {e.printStackTrace();}
			
		}
		
		output.add(new Feature("Type-Token_Ratio",typeTokenRatios));
		output.add(new Feature("Large_MATTR",mattrLargeWindowList));
		output.add(new Feature("Small_MATTR",mattrSmallWindowList));
		output.add(new Feature("MATTR_Ratio",mattrWindowRatioList));
		
//		for (Feature f : output) System.out.println(f.toTemplate());
			
		return output;
	}

	
	@Override
	public String getName() {
		return "Lexical Diversity";
	}
}
