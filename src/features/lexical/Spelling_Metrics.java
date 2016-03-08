

package features.lexical;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import extractors.data.Answer;
import extractors.data.DataNode;
import extractors.data.ExtractionModule;
import extractors.data.Feature;
import extractors.lexical.CreateSpellCheckMap;
import extractors.lexical.POS_Extractor;
import features.pause.KSE;
import features.pause.PauseBursts;

public class Spelling_Metrics extends POS_Extractor implements ExtractionModule {
	
	private final long INVALID_PAUSE_TIME = -1;
	LinkedList<Feature> output;
	Collection<Long> pauseBeforeMisspelling;
	Collection<Long> pauseBeforeCorrectSpelling;
	Collection<Long> pauseAfterMisspelling;
	Collection<Long> pauseAfterCorrectSpelling;

	public Spelling_Metrics() {
		output = new LinkedList<Feature>();
		pauseBeforeMisspelling = new LinkedList<Long>();
		pauseBeforeCorrectSpelling = new LinkedList<Long>();
		pauseAfterMisspelling = new LinkedList<Long>();
		pauseAfterCorrectSpelling = new LinkedList<Long>();
	}
	
	public void clearLists() {
		output.clear();
		pauseBeforeMisspelling.clear();
		pauseBeforeCorrectSpelling.clear();
		pauseAfterMisspelling.clear();
		pauseAfterCorrectSpelling.clear();
	}
	
	@Override
	public Collection<Feature> extract(DataNode data) {
		
		clearLists();
		
		// new instance of Pause class, for use in extracting pause list
		PauseBursts pb = new PauseBursts();
		int pause_threshold = 250;
		
		for (Answer a : data) {
		      try {
		    	  // get List of word Tokens
		    	  String[] tokens = runTokenizer(a.getCharStream());
		    	  // String[] tokens = runTokenizer(a.getFinalText());
		    	  // get starting indices (of charStream) for all tokens
		    	  ArrayList<Integer> startPositions = getStartIndex(a.getCharStream());
		    	  
//		    	  for (int pos : startPositions)
//		    		  System.out.println(pos);
		    	  
		    	  // Get a list of correctly-spelled & misspelled words
		    	  CreateSpellCheckMap spellCheck = new CreateSpellCheckMap();
		    	  HashMap<String, Boolean> spellMap = spellCheck.createUserMap(tokens);
		    	  
		    	  // get list of Pauses from charStream
		    	  pb.generatePauseDownList(a.getKeyStrokes(),pause_threshold);
		    	  List<Integer> pauseList = pb.getPauseDownList();
		    	  KSE[] kses = KSE.parseSessionToKSE(a.getKeyStrokes()).toArray(new KSE[0]);
		    	  
		    	  // Process the tokens:
		    	  for (int i = 0; i < tokens.length; i++) {
		    		  long pause = 0;
		    		  if (! ignoreSpelling(tokens[i]) && spellMap.get(tokens[i])) {
		    			  // Correct spelling (to be added only if there's a valid value for "pause"):
		    			  pause = getPauseBefore(i, pauseList, kses, startPositions);
		    			  if (pause != INVALID_PAUSE_TIME) {
		    				  pauseBeforeCorrectSpelling.add(pause);
		    			  }
		    			  
		    			  pause = getPauseAfter(i, pauseList, kses, startPositions);
		    			  if (pause != INVALID_PAUSE_TIME) {
		    				  pauseAfterCorrectSpelling.add(pause);
		    			  }

		    		  }
		    		  else if (! ignoreSpelling(tokens[i])) {
		    			  // Incorrect spelling:
		    			  pause = getPauseBefore(i, pauseList, kses, startPositions);
		    			  if (pause != INVALID_PAUSE_TIME) {
		    				  pauseBeforeMisspelling.add(pause);
		    			  }
		    			  
		    			  pause = getPauseAfter(i, pauseList, kses, startPositions);
		    			  if (pause != INVALID_PAUSE_TIME) {
		    				  pauseAfterMisspelling.add(pause);
		    			  }

		    		  }
		    	  }

		      } catch (IOException e) {
		    	  e.printStackTrace();
		      }
		}
		
		output.add(new Feature("PauseBeforeMisspelling",pauseBeforeMisspelling));
		output.add(new Feature("PauseBeforeCorrectSpelling",pauseBeforeCorrectSpelling));
		output.add(new Feature("PauseAfterMisspelling",pauseAfterMisspelling));
		output.add(new Feature("PauseAfterCorrectSpelling",pauseAfterCorrectSpelling));
		
		/*
		for (Feature f : output)
			System.out.println(f.toTemplate());
		*/
		
		return output;
	}
	
	
	// Extracts pauses before the pos-th token (assuming an entry exists)
	private long getPauseBefore (int pos, List<Integer> pauseArray, KSE[] kses, ArrayList<Integer> startPositions) {
		for (int i = 0; i < pauseArray.size(); i++) {
			if (pauseArray.get(i) == (startPositions.get(pos) - 1)) {
				return kses[startPositions.get(pos) - 1].getM_pauseMs();
			}
		}
		
		return INVALID_PAUSE_TIME;
	}
	
	
	// Extracts pauses after the pos-th token (assuming an entry exists)
	private long getPauseAfter (int pos, List<Integer> pauseArray, KSE[] kses, ArrayList<Integer> startPositions) {
		for (int i = 0; i < pauseArray.size(); i++) {
			if (pauseArray.get(i) == (startPositions.get(pos) + 1)) {
				return kses[startPositions.get(pos) + 1].getM_pauseMs();
			}
		}
		
		return INVALID_PAUSE_TIME;
	}
	
	// Returns "true" if the string should be ignored for spelling purposes
	// ... i.e., if a number or punctuation.
	private boolean ignoreSpelling (String candidate) {
		return isNumber(candidate) || isPunctuation(candidate);
	}
	
	// Returns "true" if the entire parameter (candidate) is a punctuation character
	// TODO: Change this to regex.
	public boolean isPunctuation (String candidate) {
		return (candidate.equals(".") ||
				candidate.equals(",") ||
				candidate.equals(";") ||
				candidate.equals("\"") ||
				candidate.equals("'") ||
				candidate.equals(":"));
	}
	
	// Returns "true" if the entire parameter is a number
	public boolean isNumber (String candidate) {
		// If it's a number, it should be parse-able:
		try {
			Double.parseDouble(candidate);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}
	

	// I am a ...
	public String getName() {
		return "Spelling_Pause";
	}

}
