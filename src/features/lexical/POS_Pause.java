package features.lexical;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import extractors.data.Answer;
import extractors.data.DataNode;
import extractors.data.ExtractionModule;
import extractors.data.Feature;
import extractors.lexical.CreateSpellCheckMap;
import extractors.lexical.POS_Extractor;
import extractors.lexical.Tokenize;
import features.pause.KSE;
import features.pause.PauseBursts;

public class POS_Pause extends POS_Extractor implements ExtractionModule {

	private Set<String> searchSpace = new TreeSet<String>();
	private HashMap<String, Long> featureMap = new HashMap<String, Long>();
    private ArrayList<KSE> keyPressArray;
	private String[] wordTokens;
    private String[] posTokens;
    private ArrayList<Integer> startPositions;
    private ArrayList<Integer> endPositions;
	private HashMap<String,Integer> posTagCounts;
	private final Set<String> posLabels = new HashSet<String>(Arrays.asList("MD","NN","NNS","NNP","NNPS",
																	"VBG","VB","VBD","VBN","VBP","VBZ",
																	"CD","DT","JJ","JJR","JJS","RB","RBR","RBS","WDT","WRB"));
	public POS_Pause() {
		searchSpace.clear();
		featureMap.clear();
		generateSearchSpace();
		keyPressArray  = new ArrayList<KSE>();
		wordTokens = new String[0];
		posTokens = new String[0];
		startPositions = new ArrayList<Integer>();
		endPositions = new ArrayList<Integer>();
		posTagCounts  = new HashMap<String,Integer>();
	}
		
	public void generateSearchSpace() {
		for (String s : posLabels) {
			searchSpace.add("before"+s);
			searchSpace.add("after"+s);
		}
	}
	
	@Override
	public Collection<Feature> extract(DataNode data) {
		
		posTagCounts.clear();
		
		for (String s : searchSpace)
			featureMap.put(s, null);
		
		for (Answer a : data) {
			
			try {
		        // create a Collection of KSEs, and then modify to look like character stream
		        Collection<KSE> kseArray = KSE.parseSessionToKSE(a.getKeyStrokes());	        
				keyPressArray.clear();
		        StringBuilder sb = new StringBuilder();
		        for (KSE k : kseArray)
		        	if (k.isKeyPress() && k.isVisible()) {
		        		sb.append(k.getKeyChar());			// add character to StringBuilder
		        		keyPressArray.add(k);				// add KSE to keyPressArray
		        	}
		        
		        wordTokens = runTokenizer(sb.toString());
		        posTokens = createPOSTags(wordTokens);
		        startPositions = getStartIndex(sb.toString());
		        endPositions = getEndIndex(sb.toString());
		        
		        if (wordTokens.length == startPositions.size()) {		// just checking, should always be equal
		        	
		        	// Get a list of correctly-spelled & misspelled words
		        	CreateSpellCheckMap spellCheck = new CreateSpellCheckMap();
		        	HashMap<String, Boolean> spellMap = spellCheck.createUserMap(wordTokens);
		        			        	
		        	for (int i = 1; i < wordTokens.length - 1; i++)			// exclude first & last token because no pause before or after
		        		if (spellMap.get(wordTokens[i])) { 					// if value = TRUE, i.e. a correctly spelled word
		        		
		        			//increment count in posTagCounts map
		        			if (posTagCounts.get(posTokens[i]) != null)
		        				posTagCounts.put(posTokens[i], posTagCounts.get(posTokens[i]) + 1);
		        			else
		        				posTagCounts.put(posTokens[i], 1);
		        			
		        			calculateMeanPauseBeforePos(i);
	        				calculateMeanPauseAfterPos(i);
		        		}
			        } 
		      } catch (IOException e) {
		        e.printStackTrace();
		      }
		}
		
		//create output feature list
		LinkedList<Feature> output = new LinkedList<Feature>();

		//iterate over the featureMap using searchSpace because it is auto-sorted by TreeSet class.
		for (String s : searchSpace) {
			if (featureMap.get(s).equals(null))
				output.add(new Feature(s, 0));
			else
				output.add(new Feature(s, featureMap.get(s)));
		}

//	    for (Feature f : output) System.out.println(f.toTemplate());
		return output;
	}
	
	public void calculateMeanPauseBeforePos(int i) {

		long beforePosPauseMean_Previous = 0;
		if (featureMap.get("before"+posTokens[i]) != null) 
			beforePosPauseMean_Previous = featureMap.get("before"+posTokens[i]);
		long currentTokenPauseBefore = keyPressArray.get(startPositions.get(i)).getM_pauseMs();
		int currentPosCount = posTagCounts.get(posTokens[i]);
		long beforePosPauseMean_New = ((beforePosPauseMean_Previous * (currentPosCount - 1)) 
										+ currentTokenPauseBefore)/currentPosCount;
		featureMap.put("before"+posTokens[i], beforePosPauseMean_New);
	}
	
	public void calculateMeanPauseAfterPos(int i) {
		
		long afterPosPauseMean_Previous = 0;
		if (featureMap.get("after"+posTokens[i]) != null) 
			afterPosPauseMean_Previous = featureMap.get("after"+posTokens[i]);
		long currentTokenPauseAfter = keyPressArray.get(endPositions.get(i)).getM_pauseMs();
		int currentPosCount = posTagCounts.get(posTokens[i]);			
		long afterPosPauseMean_New = ((afterPosPauseMean_Previous * (currentPosCount - 1)) 
										+ currentTokenPauseAfter)/currentPosCount;
		featureMap.put("after"+posTokens[i], afterPosPauseMean_New);
	}
		
	@Override
	public String getName() {
		return "POS_Pause";
	}

}
