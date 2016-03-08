 package mwe;

import java.io.*;
import java.sql.SQLException;
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
import output.util.AncillaryDataInterface;

/**
 * TODO
 * @author Adam Goodkind
 * For research purposes
 */
public class MweTimingDemog implements ExtractionModule {

	protected Set<String> searchSpace = new TreeSet<String>();
	protected HashMap<String,ArrayList<Double>> pauseRatioMap = new HashMap<String,ArrayList<Double>>();
	private ArrayList<KSE> keyStrokeExtendedList;
	private ArrayList<KSE> visibleKSEs;
	private ArrayList<TokenExtended> allTokensExtended;
	private ArrayList<ArrayList<TokenExtended>> mweTokens;
	private HashMap<String,ArrayList<Long>> tokenLocationPauseMap;
	private final String[] mweLocationStrings = new String[] {"StartOfMwe","MiddleOfMwe","EndOfMwe","OutsideOfMwe"};
	
	//for research purposes. comment out if doing feature extraction//
	private static final String fileName = "mweTimingDemog.data";
	private static PrintWriter out;
	AncillaryDataInterface adi;
	
	public MweTimingDemog() {
		searchSpace.clear();
		pauseRatioMap.clear();
		generateSearchSpace();
		keyStrokeExtendedList = new ArrayList<KSE>();
		visibleKSEs = new ArrayList<KSE>();
		mweTokens = new ArrayList<ArrayList<TokenExtended>>();
		allTokensExtended = new ArrayList<TokenExtended>();
		tokenLocationPauseMap = new HashMap<String,ArrayList<Long>>();
		//research//
		try {
			out = new PrintWriter(new BufferedWriter(new FileWriter(fileName, true)));
			adi = new AncillaryDataInterface();
		} catch (IOException | ClassNotFoundException | InstantiationException | IllegalAccessException | SQLException e) {e.printStackTrace();}
	}
	
	public void clearLists() {
		keyStrokeExtendedList.clear();
		visibleKSEs.clear();
		mweTokens.clear();
		allTokensExtended.clear();
		tokenLocationPauseMap.clear();
	}
	
	public void buildMap() {
		for (String location : mweLocationStrings) {
			ArrayList<Long> temp = new ArrayList<Long>();
			tokenLocationPauseMap.put(location,temp);
		}
	}
	
	public void generateSearchSpace() {
		int i = 0;
		for (String s : Arrays.copyOfRange(mweLocationStrings,i,mweLocationStrings.length)) {
			for (String t : Arrays.copyOfRange(mweLocationStrings,i,mweLocationStrings.length)) 
				if (s != t) 
					searchSpace.add(s+"/"+t);
			i++;
		}
	}
	
	public void initializePauseRatioMap() {
		for (String s : searchSpace) {
			ArrayList<Double> temp = new ArrayList<Double>();
			pauseRatioMap.put(s, temp);
		}
	}
	
	@Override
	public Collection<Feature> extract(DataNode data) {
		
		initializePauseRatioMap();
		
		for (Answer a : data) {
			//research//
			EventList<KeyStroke> ksList = a.getKeyStrokeList();
			double rate = getIntraWordTypingRate(ksList);
			Object subj_lang = null;
			String language = null;
			try {
				subj_lang = adi.getData(data.getUserID(), AncillaryDataInterface.UD_FIRST_LANGUAGE);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (subj_lang.equals("English"))
				language = "English";
			else
				language = "Non-English";
			out.print("S"+data.getUserID()+"_A"+a.getAnswerID()+","+language+","+rate+","+"CL"+a.getCogLoad()+",");
			//end research//
			generateMweTimingData(a);
			for (String s : tokenLocationPauseMap.keySet()) {
				out.print(getMean(tokenLocationPauseMap.get(s))+",");
			}
			out.println();
		}
		
		//create output feature list
		LinkedList<Feature> output = new LinkedList<Feature>();
		
		for (String s : searchSpace) {
			output.add(new Feature(s,pauseRatioMap.get(s)));
		}
//		for (String s : tokenLocationPauseMap.keySet())
//			output.add(new Feature(s,tokenLocationPauseMap.get(s)));

		
//		for (Feature f : output) System.out.println(f.toTemplate());
		out.flush();
		return output;
	}
	
	public void generateMweTimingData(Answer a) {
		clearLists();
		buildMap();
		EventList<KeyStroke> keyStrokeList = a.getKeyStrokeList();
		String visibleText = keyStrokeList.toVisibleTextString();  
		keyStrokeExtendedList = new ArrayList<KSE>(KSE.parseSessionToKSE(a.getKeyStrokes()));
		visibleKSEs = KSE.parseToVisibleTextKSEs(keyStrokeExtendedList); 
		MweExtractor mweExtractor = new MweExtractor(); 
		mweExtractor.parseTextToExtendedTokens(visibleText);
		mweTokens = mweExtractor.getMweTokens(); 
		allTokensExtended = mweExtractor.getallTokensExtended();
		generateTokenLocationPauseMap(); 
		addPausesToFeatureMap();
	}	

	
	public void generateTokenLocationPauseMap() {
		HashSet<Integer> mweIndices = new HashSet<Integer>();

		for (ArrayList<TokenExtended> mweToken : mweTokens) {
			for (TokenExtended wordToken : mweToken) {
				String location = tokenLocation(wordToken.tokenSpan,mweToken);
				Long pause = visibleKSEs.get(wordToken.tokenSpan.begin).getM_pauseMs();
				Long endPause = null;
				if (wordToken.tokenSpan.end < visibleKSEs.size()-2) 
					endPause = visibleKSEs.get(wordToken.tokenSpan.end+1).getWhen() - visibleKSEs.get(wordToken.tokenSpan.end).getWhen();
				
				
				
				switch (location) {
					case "StartOfMwe":
						tokenLocationPauseMap.get(location).add(pause);
						mweIndices.add(wordToken.tokenSpan.index);
						break;
					case "EndOfMwe":
						tokenLocationPauseMap.get("MiddleOfMwe").add(pause);
						if (endPause != null)
							tokenLocationPauseMap.get(location).add(endPause);
						mweIndices.add(wordToken.tokenSpan.index);
						break;
					case "MiddleOfMwe":
						tokenLocationPauseMap.get(location).add(pause);
						mweIndices.add(wordToken.tokenSpan.index);
						break;
				}
			}
		}

		for (int i = 0; i < allTokensExtended.size(); i++) {
			if (!mweIndices.contains(allTokensExtended.get(i).tokenSpan.index)) {
				if (i==0) {
					Long pause = visibleKSEs.get(allTokensExtended.get(i).tokenSpan.begin).getM_pauseMs();
					tokenLocationPauseMap.get("OutsideOfMwe").add(pause);
				} else if (!mweIndices.contains(allTokensExtended.get(i-1).tokenSpan.index)) {
					Long pause = visibleKSEs.get(allTokensExtended.get(i).tokenSpan.begin).getM_pauseMs();
					tokenLocationPauseMap.get("OutsideOfMwe").add(pause);
				}
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
					double sMean = getMean(tokenLocationPauseMap.get(s));
					double tMean = getMean(tokenLocationPauseMap.get(t));
					if (sMean != -1.0 && tMean != -1.0)
						pauseRatioMap.get(s+"/"+t).add(sMean/tMean);
					else {
						pauseRatioMap.get(s+"/"+t).add(null);
					}
//					out.print(sMean/tMean+",");
				}
			}
			i++;
		}
//		out.println();
	}
	
	public double getMean(ArrayList<Long> values) {
		double sum = 0;
		for (Long l : values)
			if (l == null) {
				return -1.0;
			}
			else {
				sum += Double.valueOf(l);
			}
		return sum/values.size();
	}
	/**
	 * Removes outliers above a threshold
	 */
	private double getInlierMean(ArrayList<Long> values, double threshold) {
		double sum = 0.0;
		int size = 0;
		for (Long l : values) {
			if (l == null)
				return -1.0;
			else {
				if (Double.valueOf(l) > threshold) {
					sum += Double.valueOf(l);
					size++;
				}
			}	
		}
		return sum/size;
	}
	
	public HashMap<String, ArrayList<Long>> getTokenLocationPauseMap() {
		return tokenLocationPauseMap;
	}

	public void setTokenLocationPauseMap(
			HashMap<String, ArrayList<Long>> tokenLocationPauseMap) {
		this.tokenLocationPauseMap = tokenLocationPauseMap;
	}
	
	public double getIntraWordTypingRate(EventList<KeyStroke> keyStrokeList) {
		double keyStrokeCount = 0;
		for (KeyStroke k : keyStrokeList)
			if (k.isKeyPress() && k.isVisible() && !k.isSpace())
				keyStrokeCount++;
		long startTime = keyStrokeList.get(0).getWhen();
		long endTime = keyStrokeList.get(keyStrokeList.size()-1).getWhen();
		double sessionDuration = (endTime-startTime)/60000.0;
		
		return keyStrokeCount/sessionDuration;
	}


	@Override
	public String getName() {
		return "MWE Timing";
	}

}
