package features.lexical;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;
import java.util.TreeSet;

import events.EventList;
import extractors.data.Answer;
import extractors.data.DataNode;
import extractors.data.ExtractionModule;
import extractors.data.Feature;
import extractors.lexical.POS_Extractor;
import keystroke.KeyStroke;

/**
 * @author agoodkind
 *	Measures occurrences of Parts-of-Speech
 *	extends POS_Tagger, which compiles POS tags
 */
public class POS_Metrics extends POS_Extractor implements ExtractionModule {

	static POS_Extractor pos = new POS_Extractor();
	private Collection<Double> lexDensities;
	private Collection<Integer> nounCounts;
	private Collection<Integer> verbCounts;
	private Collection<Integer> modifierCounts;
	private Collection<Integer> modalCounts;
	private HashMap<String,ArrayList<Integer>> posTagMap;
	LinkedList<Feature> output;
	
	public POS_Metrics() {
		lexDensities = new LinkedList<Double>();
		nounCounts = new LinkedList<Integer>();
		verbCounts = new LinkedList<Integer>();
		modifierCounts = new LinkedList<Integer>();
		modalCounts = new LinkedList<Integer>();
		posTagMap = new HashMap<String,ArrayList<Integer>>();
		output = new LinkedList<Feature>();
		createPosMap();
	}
	
	public void createPosMap() {
		for (String pos : POS_Extractor.allPosTags) {
			ArrayList<Integer> temp = new ArrayList<Integer>();
			posTagMap.put(pos, temp);
		}
	}
	
	public void clearLists() {
		lexDensities.clear();
		nounCounts.clear();
		verbCounts.clear();
		modifierCounts.clear();
		modalCounts.clear();
		output.clear();
	}
	
	@Override
	public Collection<Feature> extract(DataNode data) {
//		System.out.println("Creating instance of "+this.getClass().toString());
		clearLists();
		
		for (Answer a : data) {
//			System.out.printf("Answer %d%n",a.getAnswerID());
			try {
				EventList<KeyStroke> k = a.getKeyStrokeList();
				String f = KeyStroke.keyStrokesToFinalText(k);
				String[] wordTokens = runTokenizer(f);
				pos.setPosTags(wordTokens);
				String[] posTokens = pos.getPosTags();
				lexDensities.add(pos.getLexDensity(posTokens));
				nounCounts.add(pos.getNounCount(posTokens));
				verbCounts.add(pos.getVerbCount(posTokens));
				modifierCounts.add(pos.getModifierCount(posTokens));
				modalCounts.add(pos.getModalCount(posTokens));
				/*---comment out for only category counts-----*/
//				HashMap<String,Integer> answerPosMap = pos.getPosTagMap();
//				addPosCountsToAllAnswersMap(answerPosMap);
				/*--------------------------------------------*/
				
			} catch (IOException e) {e.printStackTrace();}
			
		}
		
		output.add(new Feature("Lexical_Density",lexDensities));
		output.add(new Feature("Noun_Counts",nounCounts));
		output.add(new Feature("Verb_Counts",verbCounts));
		output.add(new Feature("Modifier_Counts",modifierCounts));
		output.add(new Feature("Modal_Counts",modalCounts));
		/*---comment out for only category counts-----*/
//		for (String posTag : posTagMap.keySet())
//			output.add(new Feature(posTag+"_Count",posTagMap.get(posTag)));
		/*-------------------------------------------*/
		
//	    for (Feature f : output) System.out.println(f.toTemplate());
		
		return output;
	}

	public void addPosCountsToAllAnswersMap(HashMap<String,Integer> answerPosMap) {
		for (String pos : answerPosMap.keySet())
			posTagMap.get(pos).add(answerPosMap.get(pos));
//		for (String posTag : POS_Extractor.allPosTags) {
//			//create ArrayList if it does not exist yet
//			ArrayList<Integer> tagList = posTagMap.get(posTag);
//			if (tagList == null) {
//				tagList = new ArrayList<Integer>();
//				posTagMap.put(posTag, tagList);
//			}
//			//get count for selected tag, and add to ArrayList
//			int tagCount;
//			if (answerPosMap.get(posTag) == null)
//				tagCount = 0;
//			else
//				tagCount = answerPosMap.get(posTag);
//			posTagMap.get(posTag).add(tagCount);
//		}

	}
	
	public HashMap<String,ArrayList<Integer>> getAllAnswersPosTagMap() {
		return posTagMap;
	}
	
	@Override
	public String getName() {
		return "POS Metrics";
	}

}
