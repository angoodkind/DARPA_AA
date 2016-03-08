package mwe;

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

/**
 * TODO
 * @author Adam Goodkind
 *
 */
public class MweTiming implements ExtractionModule {

	static final boolean CSV = true;
	static final boolean CSV4ARFF = false;
	static final boolean DEBUG = false;
	static final String csv_file = "mwe_full_tokens.csv";
	protected Set<String> searchSpace = new TreeSet<String>();
	protected HashMap<String,ArrayList<Double>> pauseRatioMap = new HashMap<String,ArrayList<Double>>();
	private ArrayList<KSE> keyStrokeExtendedList;
	private ArrayList<KSE> visibleKSEs;
	private ArrayList<TokenExtended> allTokensExtended;
	private ArrayList<ArrayList<TokenExtended>> mweTokens;
	private HashMap<String,ArrayList<Long>> tokenLocationPauseMap;
	private final String[] mweLocationStrings = new String[] {"StartOfMwe","MiddleOfMwe","EndOfMwe","OutsideOfMwe"};

	public MweTiming() {
		searchSpace.clear();
		pauseRatioMap.clear();
		generateSearchSpace();
		keyStrokeExtendedList = new ArrayList<KSE>();
		visibleKSEs = new ArrayList<KSE>();
		mweTokens = new ArrayList<ArrayList<TokenExtended>>();
		allTokensExtended = new ArrayList<TokenExtended>();
		tokenLocationPauseMap = new HashMap<String,ArrayList<Long>>();
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
		BufferedWriter outf = null;
		try {
			outf = new BufferedWriter(new FileWriter(csv_file, true));

			for (Answer a : data) {
				generateMweTimingData(a,outf);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (outf != null) {
				try {
					outf.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		//create output feature list
		LinkedList<Feature> output = new LinkedList<Feature>();

		//		for (String s : searchSpace) {
		//			output.add(new Feature(s,pauseRatioMap.get(s)));
		//		}
		for (String s : tokenLocationPauseMap.keySet())
			output.add(new Feature(s,tokenLocationPauseMap.get(s)));


		//for (Feature f : output) System.out.println(f.toTemplate());
		return output;
	}

	public void generateMweTimingData(Answer a, BufferedWriter writer) {
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
		generateTokenLocationPauseMap(writer); 
		addPausesToFeatureMap();
	}	


	public void generateTokenLocationPauseMap(BufferedWriter writer) {
		HashSet<Integer> mweIndices = new HashSet<Integer>();

//		for (ArrayList<TokenExtended> mweToken : mweTokens) {
		for (int i = 0; i < mweTokens.size(); i++) {
			ArrayList<TokenExtended> mweToken = mweTokens.get(i);
//			for (TokenExtended wordToken : mweToken) {
			for (int j = 0; j < mweToken.size(); j++) {
				TokenExtended wordToken = mweToken.get(j);
				String location = tokenLocation(wordToken.tokenSpan,mweToken);
				Long pause = visibleKSEs.get(wordToken.tokenSpan.begin).getM_pauseMs();
				Long endPause = null;
				if (wordToken.tokenSpan.end < visibleKSEs.size()-1) 
					endPause = visibleKSEs.get(wordToken.tokenSpan.end).getWhen() - visibleKSEs.get(wordToken.tokenSpan.end-1).getWhen();

				if (CSV) {
					try {
						if (/**location.equals("EndOfMwe") || **/location.equals("MiddleOfMwe")) {
							writer.write(printMweToken(mweToken));
							writer.write('|');
							writer.write(mweToken.get(j-1).token+"_"+wordToken.token);
							writer.write('|');
							writer.write(mweToken.get(j-1).partOfSpeech+"_"+wordToken.partOfSpeech);
							writer.write('|');
							writer.write(location);
							writer.write('|');
							writer.write(Long.toString(pause));
							writer.write('\n');
						}
					} catch (Exception e) {e.printStackTrace();}
				}

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
		
		if (CSV4ARFF) {
			
			for (int k = 2; k < allTokensExtended.size()-1; k++) {
				LinkedList<TokenExtended> trigram = new LinkedList<TokenExtended>();
				trigram.add(allTokensExtended.get(k-1));
				trigram.add(allTokensExtended.get(k));
				trigram.add(allTokensExtended.get(k+1));
				String location = "IN";
				if (! mweIndices.contains(allTokensExtended.get(k).tokenSpan.index))
					location = "OUT";
				try {
					for (TokenExtended t : trigram) {
//						writer.write(Long.toString(visibleKSEs.get(t.tokenSpan.begin).getM_pauseMs()));
//						writer.write(',');
						writer.write(String.format("\"%s\"",t.partOfSpeech));
						writer.write(',');
						writer.write(String.format("%d",t.size()));
						writer.write(',');
						if (DEBUG) {
							writer.write(t.token);
							writer.write(',');
						}
					}
					writer.write(String.format("\"%s\"",location));
					writer.write('\n');
				} catch (Exception e) {e.printStackTrace();}
			}
			
		}
		
		for (int i = 0; i < allTokensExtended.size(); i++) {
			TokenExtended token = allTokensExtended.get(i);
			Long pause = null;
			if (! mweIndices.contains(token.tokenSpan.index)) {
				if (i==0) {
					pause = visibleKSEs.get(token.tokenSpan.begin).getM_pauseMs();
				} else if (! mweIndices.contains(allTokensExtended.get(i-1).tokenSpan.index)) {
					pause = visibleKSEs.get(token.tokenSpan.begin).getM_pauseMs();
				}
				if (pause != null)
					tokenLocationPauseMap.get("OutsideOfMwe").add(pause);
				
				if (CSV && pause != null && i>0) {
					try {
						writer.write("NONMWE");
						writer.write('|');
						writer.write(allTokensExtended.get(i-1).token+"_"+token.token);
						writer.write('|');
						writer.write(allTokensExtended.get(i-1).partOfSpeech+"_"+token.partOfSpeech);
						writer.write('|');
						writer.write("OutsideOfMwe");
						writer.write('|');
						writer.write(Long.toString(pause));
						writer.write('\n');
					} catch (Exception e) {e.printStackTrace();}
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
				}
			}
			i++;
		}
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

	public HashMap<String, ArrayList<Long>> getTokenLocationPauseMap() {
		return tokenLocationPauseMap;
	}

	public void setTokenLocationPauseMap(
			HashMap<String, ArrayList<Long>> tokenLocationPauseMap) {
		this.tokenLocationPauseMap = tokenLocationPauseMap;
	}
	
	public String printMweToken(ArrayList<TokenExtended> mweTokenArray ) {
		StringBuilder sb = new StringBuilder();
		for (TokenExtended token : mweTokenArray)
			sb.append(token.token);
		return sb.toString();
	}


	@Override
	public String getName() {
		return "MWE Timing";
	}

}
