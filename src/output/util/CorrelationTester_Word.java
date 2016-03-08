package output.util;

import java.io.*;
import java.util.*;

import events.EventList;
import extractors.data.Answer;
import extractors.data.DataNode;
import extractors.data.ExtractionModule;
import extractors.data.Feature;
import extractors.lexical.TokenExtender;
import features.pause.KSE;
import features.predictability.Predictability;
import keystroke.KeyStroke;
import mwe.TokenExtended;
import ngrammodel.BigramModel;

/**
 * Output is compatible with Python script available
 * at TODO: INSERT LINK
 * 
 * Must set ngramLength to between 1 and 4 
 * 
 * This module does not extract Features
 * @author Adam Goodkind
 *
 */
public class CorrelationTester_Word extends Predictability implements ExtractionModule {

	private static final int ngramLength = 2; //<===SET ME!!! (1-4)
	
	private static final String modelName = "SESSION1";
	private static final String gramType = "word";
	private static final String outFileName = gramType+Integer.toString(ngramLength)+"gramAbsZscoreIntraPauseMean-training.csv";
	private static final String userPauseMapFile = "TrainingPauseMetrics.map"; 
	private static final HashMap<Integer,HashMap<String,Double>> allUsersPauseMap = loadPauseMapFromFile(userPauseMapFile);
	private static TokenExtender extender = new TokenExtender();
	private static PrintWriter writer;
	
	public CorrelationTester_Word() throws IOException {
		super(modelName, gramType);
		writer = new PrintWriter(new BufferedWriter(new FileWriter(outFileName, true)));
	}

	@Override
	public Collection<Feature> extract(DataNode data) {
		for (Answer a : data) {
			writer.println("S"+data.getUserID()+"_A"+a.getAnswerID());
			
			HashMap<String,Double> userPauseMap = allUsersPauseMap.get(data.getUserID());
			
			ArrayList<KSE> kseList = new ArrayList<KSE>(KSE.parseSessionToKSE(a.getKeyStrokes()));
			ArrayList<KSE> visibleKSEs = KSE.parseToVisibleTextKSEs(kseList);
			
			EventList<KeyStroke> keyStrokeList = a.getKeyStrokeList();
			String visibleText = keyStrokeList.toVisibleTextString();
			
			ArrayList<TokenExtended> tokens = extender.generateExtendedTokens(visibleText);

			for (int i = ngramLength; i <= tokens.size(); i++) {
				String[] ngram = new String[ngramLength];
				for (int j=0; j < ngramLength; j++) {
					ngram[j] = tokens.get(i-ngramLength+j).token;
				}
				
				KSE lastGramBegin = visibleKSEs.get(tokens.get(i-1).getTokenSpan().begin);
				double ngramRawPause = (double) lastGramBegin.getM_pauseMs()/1000.0;
				double ngramProbability = getNgramProbability(ngram);
//				writer.println(ngramRawPause+","+ngramProbability);
				
				//linear pause adjust
				double meanLinearPause = userPauseMap.get("preWordPausesLinearMean");
				double simpleAdjustedPause = ngramRawPause - meanLinearPause;
//				writer.println(simpleAdjustedPause+","+ngramProbability);
				
				//log log pause adjust
				double logMeanLogPause = Math.log(userPauseMap.get("preWordPausesLogMean"));
				double simpleLogAdjustedPause = Math.log(ngramRawPause) - logMeanLogPause;
//				if (ngramRawPause != 0)
//					writer.println(simpleLogAdjustedPause+","+ngramProbability);
				
				//z-score
				double meanLogPause = userPauseMap.get("intraWordPausesLogMean");
				double stdDevLogPause = userPauseMap.get("intraWordPausesLogStdDev");
				double zScore = (Math.log(ngramRawPause)-meanLogPause)/stdDevLogPause;
				double absZScore = Math.abs((Math.log(ngramRawPause)-meanLogPause))/stdDevLogPause;
				
				if (absZScore != Double.NaN)
//					writer.println(zScore+","+ngramProbability);
					writer.println(absZScore+","+ngramProbability);
				
				
			}
		}
		writer.flush();
		return null;
	}
	
	/**
	 * Calculates smoothed probability of ngram between 
	 * length 1 (unigram) to length 4 (fourgram)
	 * 
	 * If ngramLength is >1 or <4, returns Double.NaN
	 * 
	 * @param ngram		A String[] of the ngram
	 * @return			The ngram's smoothed probability
	 */
	private double getNgramProbability(String[] ngram) {
		double probability = Double.NaN;
		
		if (ngram.length != ngramLength) {
			System.out.println("ngram.length != ngramLength");
			return probability;
		}
			
		switch (ngram.length) {
			case 1:
				probability = wordBigramModel.getUnigramCount(ngram[0])/
							 (wordBigramModel.getUnigramTotalCount()*1.0);
				break;
			case 2:
				probability = wordBigramModel.getBigramProbability(ngram[0],ngram[1]);
				break;
			case 3:
				probability = wordTrigramModel.getTrigramProbability(ngram[0],ngram[1],ngram[2]);
				break;
			case 4:
				probability = wordFourgramModel.getFourgramProbability(ngram[0],ngram[1],ngram[2],ngram[3]);
				break;
			default:
				System.out.println("Invalid Gram Length: "+ngram.length);
				break;
		}
		return probability;	
	}
	
	private static HashMap<Integer,HashMap<String,Double>> loadPauseMapFromFile(String filename) {
		HashMap<Integer,HashMap<String,Double>> pauseMap = null;
		try {
			File file = new File(filename);
			FileInputStream fileIStream = new FileInputStream(file);
			ObjectInputStream objectIStream = new ObjectInputStream(fileIStream);
			pauseMap = (HashMap<Integer,HashMap<String,Double>>) objectIStream.readObject();
			objectIStream.close();
		} catch(IOException|ClassNotFoundException e) {e.printStackTrace();}
		return pauseMap;
	}

	@Override
	public String getName() {
		return null;
	}

}
