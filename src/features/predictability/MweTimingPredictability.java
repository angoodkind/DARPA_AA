package features.predictability;

import java.io.*;
import java.util.*;

import edu.mit.jmwe.data.IMWE;
import edu.mit.jmwe.data.IToken;
import events.EventList;
import extractors.data.Answer;
import extractors.data.DataNode;
import extractors.data.ExtractionModule;
import extractors.data.Feature;
import extractors.lexical.TokenExtender.TokenSpan;
import features.pause.KSE;
import keystroke.KeyStroke;
import mwe.MweExtractor;
import mwe.TokenExtended;

public class MweTimingPredictability extends Predictability implements ExtractionModule {

	private static final String modelName = "SESSION1";
	private static final String gramType = "word";
	protected Set<String> searchSpace = new TreeSet<String>();
	protected HashMap<String,ArrayList<Double>> pauseRatioMap = new HashMap<String,ArrayList<Double>>();
	private ArrayList<KSE> keyStrokeExtendedList;
	private ArrayList<KSE> visibleKSEs;
	private ArrayList<TokenExtended> allTokensExtended;
	private ArrayList<ArrayList<TokenExtended>> mweTokens;
	private HashMap<String,ArrayList<Double>> pauseMapBigram;
	private HashMap<String,ArrayList<Double>> pauseMapTrigram;
	private final String[] mweLocationStrings = new String[] {"StartOfMwe","MiddleOfMwe","EndOfMwe","OutsideOfMwe"};
	
	public MweTimingPredictability() {
		super(modelName,gramType);
		searchSpace.clear();
		pauseRatioMap.clear();
		generateSearchSpace();
		keyStrokeExtendedList = new ArrayList<KSE>();
		visibleKSEs = new ArrayList<KSE>();
		mweTokens = new ArrayList<ArrayList<TokenExtended>>();
		allTokensExtended = new ArrayList<TokenExtended>();
		pauseMapBigram = new HashMap<String,ArrayList<Double>>();
		pauseMapTrigram = new HashMap<String,ArrayList<Double>>();
	}
	
	public void clearLists() {
		keyStrokeExtendedList.clear();
		visibleKSEs.clear();
		mweTokens.clear();
		allTokensExtended.clear();
		pauseMapBigram.clear();
		pauseMapTrigram.clear();
	}
	
	public void buildMaps() {
		for (String location : mweLocationStrings) {
			ArrayList<Double> temp1 = new ArrayList<Double>();
			ArrayList<Double> temp2 = new ArrayList<Double>();
			pauseMapBigram.put(location,temp1);
			pauseMapTrigram.put(location,temp2);
		}
	}
	
	public void generateSearchSpace() {
		int i = 0;
		for (String s : Arrays.copyOfRange(mweLocationStrings,i,mweLocationStrings.length)) {
			for (String t : Arrays.copyOfRange(mweLocationStrings,i,mweLocationStrings.length)) 
				if (s != t) {
					searchSpace.add(s+"/"+t+"_Bi");
					searchSpace.add(s+"/"+t+"_Tri");
				}
			i++;
		}
	}
	
	public void initializeFeatureMap() {
		for (String s : searchSpace) {
			ArrayList<Double> temp = new ArrayList<Double>();
			pauseRatioMap.put(s, temp);
		}
	}
	
	@Override
	public Collection<Feature> extract(DataNode data) {
		
		initializeFeatureMap();
		
		for (Answer a : data) {
			generateMweTimingData(a,data.getUserID());
		}
		
		//create output feature list
		LinkedList<Feature> output = new LinkedList<Feature>();
		
//		for (String s : searchSpace) {
//			output.add(new Feature(s+"_Predict",pauseRatioMap.get(s)));
//		}
		for (String s : pauseMapBigram.keySet())
			output.add(new Feature(s+"_BigramPredict",pauseMapBigram.get(s)));

		for (String s : pauseMapTrigram.keySet())
			output.add(new Feature(s+"_TrigramPredict",pauseMapTrigram.get(s)));
		
//		for (String s : pauseRatioMap.keySet())
//			System.out.println(s+" "+pauseRatioMap.get(s));
		
//		for (Feature f : output) System.out.println(f.toTemplate());
		return output;
	}
	
	public void generateMweTimingData(Answer a, int userID) {
		clearLists();
		buildMaps();
		EventList<KeyStroke> keyStrokeList = a.getKeyStrokeList();
		String visibleText = keyStrokeList.toVisibleTextString();  
		keyStrokeExtendedList = new ArrayList<KSE>(KSE.parseSessionToKSE(a.getKeyStrokes()));
		visibleKSEs = KSE.parseToVisibleTextKSEs(keyStrokeExtendedList); 
		MweExtractor mweExtractor = new MweExtractor(); 
		mweExtractor.parseTextToExtendedTokens(visibleText);
		mweTokens = mweExtractor.getMweTokens(); 
		allTokensExtended = mweExtractor.getallTokensExtended();
		generateTokenLocationPauseMap(userID); 
		addPausesToFeatureMap();
	}	

	
	public void generateTokenLocationPauseMap(int userID) {
		HashSet<Integer> mweIndices = new HashSet<Integer>();

		for (ArrayList<TokenExtended> mweToken : mweTokens) {
			for (TokenExtended wordToken : mweToken) {
				String location = tokenLocation(wordToken.tokenSpan,mweToken);
				Long pause = visibleKSEs.get(wordToken.tokenSpan.begin).getM_pauseMs();
				Long endPause = null;
				if (wordToken.tokenSpan.end < visibleKSEs.size()-1) 
					endPause = visibleKSEs.get(wordToken.tokenSpan.end).getWhen() - visibleKSEs.get(wordToken.tokenSpan.end-1).getWhen();
				
				int index = wordToken.tokenSpan.index;
				double bigramPredictability = Double.NaN;
				double trigramPredictability = Double.NaN;
				if (wordToken.tokenSpan.index == 1) {
					String gram1 = allTokensExtended.get(index-1).token;
					String gram2 = allTokensExtended.get(index).token;
					String[] bigram = {gram1,gram2};
					bigramPredictability = getWordNgramPredictability(bigram,pause,"preWord",userID);
				}
				if (wordToken.tokenSpan.index > 1) {
					String gram1 = allTokensExtended.get(index-2).token;
					String gram2 = allTokensExtended.get(index-1).token;
					String gram3 = allTokensExtended.get(index).token;
					String[] bigram = {gram2,gram3};
					String[] trigram = {gram1,gram2,gram3};
					bigramPredictability = getWordNgramPredictability(bigram,pause,"preWord",userID);
					trigramPredictability = getWordNgramPredictability(trigram,pause,"preWord",userID);
				}
				
				switch (location) {
					case "StartOfMwe":
//						tokenLocationPauseMap.get(location).add(pause);
						mweIndices.add(wordToken.tokenSpan.index);
						if (!Double.isNaN(bigramPredictability))
							pauseMapBigram.get(location).add(bigramPredictability);
						if (!Double.isNaN(trigramPredictability))
							pauseMapTrigram.get(location).add(trigramPredictability);
						break;
					case "EndOfMwe":
//						pauseMapBigram.get("MiddleOfMwe").add(pause);
						if (!Double.isNaN(bigramPredictability))
							pauseMapBigram.get("MiddleOfMwe").add(bigramPredictability);
						if (!Double.isNaN(trigramPredictability))
							pauseMapTrigram.get("MiddleOfMwe").add(trigramPredictability);
						if (endPause != null) {
//							pauseMapBigram.get(location).add(endPause);
							if (!Double.isNaN(bigramPredictability))
								pauseMapBigram.get(location).add(bigramPredictability);
							if (!Double.isNaN(trigramPredictability))
								pauseMapTrigram.get(location).add(trigramPredictability);
						}
						mweIndices.add(wordToken.tokenSpan.index);
						break;
					case "MiddleOfMwe":
//						pauseMapBigram.get(location).add(pause);
						if (bigramPredictability != Double.NaN)
							pauseMapBigram.get(location).add(bigramPredictability);
						if (trigramPredictability != Double.NaN)
							pauseMapTrigram.get(location).add(trigramPredictability);
						mweIndices.add(wordToken.tokenSpan.index);
						break;
				}
			}
		}

		for (int i = 1; i < allTokensExtended.size(); i++) {
			if (!mweIndices.contains(allTokensExtended.get(i).tokenSpan.index)
					&& !mweIndices.contains(allTokensExtended.get(i-1).tokenSpan.index)) {
				Long pause = visibleKSEs.get(allTokensExtended.get(i).tokenSpan.begin).getM_pauseMs();
				int index = allTokensExtended.get(i).tokenSpan.index;
				double bigramPredictability = Double.NaN;
				double trigramPredictability = Double.NaN;
				if (allTokensExtended.get(i).tokenSpan.index == 1) {
					String gram1 = allTokensExtended.get(index-1).token;
					String gram2 = allTokensExtended.get(index).token;
					String[] bigram = {gram1,gram2};
					bigramPredictability = getWordNgramPredictability(bigram,pause,"preWord",userID);
				}
				if (allTokensExtended.get(i).tokenSpan.index > 2) {
					String gram1 = allTokensExtended.get(index-2).token;
					String gram2 = allTokensExtended.get(index-1).token;
					String gram3 = allTokensExtended.get(index).token;
					String[] bigram = {gram2,gram3};
					String[] trigram = {gram1,gram2,gram3};
					bigramPredictability = getWordNgramPredictability(bigram,pause,"preWord",userID);
					trigramPredictability = getWordNgramPredictability(trigram,pause,"preWord",userID);
				}
//				pauseMapBigram.get("OutsideOfMwe").add(pause);
				if (!Double.isNaN(bigramPredictability))
					pauseMapBigram.get("OutsideOfMwe").add(bigramPredictability);
				if (!Double.isNaN(trigramPredictability))
					pauseMapTrigram.get("OutsideOfMwe").add(trigramPredictability);
			}		
		}			
	}

	
	public String tokenLocation(TokenSpan tokenSpan, ArrayList<TokenExtended> mweToken) {
		String location = null;
			
		if (mweToken.get(0).tokenSpan.toString().equals(tokenSpan.toString()))
			location = "StartOfMwe";
		else if (mweToken.get(mweToken.size()-1).tokenSpan.toString().equals(tokenSpan.toString()))
			location = "EndOfMwe";
		else 
			location =  "MiddleOfMwe";

		return location;
	}
	
	public void addPausesToFeatureMap() {
		int i = 0;
		for (String s : Arrays.copyOfRange(mweLocationStrings,i,mweLocationStrings.length)) {
			for (String t : Arrays.copyOfRange(mweLocationStrings,i,mweLocationStrings.length)) {
				if (s != t) {
					double b1Mean = mean(pauseMapBigram.get(s));
					double b2Mean = mean(pauseMapBigram.get(t));
					if (b1Mean != -1.0 && b2Mean != -1.0)
						pauseRatioMap.get(s+"/"+t+"_Bi").add(b1Mean/b2Mean);
					else
						pauseRatioMap.get(s+"/"+t+"_Bi").add(null);
					double t1Mean = mean(pauseMapTrigram.get(s));
					double t2Mean = mean(pauseMapTrigram.get(t));
					if (t1Mean != -1.0 && t2Mean != -1.0)
						pauseRatioMap.get(s+"/"+t+"_Tri").add(t1Mean/t2Mean);
					else
						pauseRatioMap.get(s+"/"+t+"_Tri").add(null);
				}
			}
			i++;
		}
	}
	
	
	public HashMap<String, ArrayList<Double>> getPauseMapBigram() {
		return pauseMapBigram;
	}

	public void setPauseMapBigram(HashMap<String, ArrayList<Double>> pauseMapBigram) {
		this.pauseMapBigram = pauseMapBigram;
	}

	public HashMap<String, ArrayList<Double>> getPauseMapTrigram() {
		return pauseMapTrigram;
	}

	public void setPauseMapTrigram(HashMap<String, ArrayList<Double>> pauseMapTrigram) {
		this.pauseMapTrigram = pauseMapTrigram;
	}

	@Override
	public String getName() {
		return "MWE Timing Predictability";
	}

}
