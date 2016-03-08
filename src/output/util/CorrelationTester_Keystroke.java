package output.util;

import java.io.*;
import java.util.*;

import extractors.data.Answer;
import extractors.data.DataNode;
import extractors.data.ExtractionModule;
import extractors.data.Feature;
import extractors.lexical.TokenExtender;
import features.pause.KSE;
import features.predictability.Predictability;
import keystroke.KeyStroke;
import keytouch.KeyTouch;

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
public class CorrelationTester_Keystroke extends Predictability implements ExtractionModule {

	private static final int ngramLength = 2; //<===SET ME!!! (1-4)
	
	private static final String modelName = "SESSION1";
	private static final String gramType = "keystroke";
	private static final String outFileName = gramType+Integer.toString(ngramLength)+"gramHold-training.csv";
//	private static final String userPauseMapFile = "Training_3CategoryPauseMetrics.map"; 
//	private static final HashMap<Integer,HashMap<String,Double>> allUsersPauseMap = loadPauseMapFromFile(userPauseMapFile);
	private static PrintWriter writer;
	
	public CorrelationTester_Keystroke() throws IOException {
		super(modelName, gramType);
		writer = new PrintWriter(new BufferedWriter(new FileWriter(outFileName, true)));
	}

	@Override
	public Collection<Feature> extract(DataNode data) {
		for (Answer a : data) {
			
			writer.println("S"+data.getUserID()+"_A"+a.getAnswerID());
//			HashMap<String,Double> userPauseMap = allUsersPauseMap.get(data.getUserID());
			
			LinkedList<KeyTouch> keyTouchList = KeyTouch.parseSessionToKeyTouches(a.getKeyStrokes());
			
			for (int i = ngramLength; i < keyTouchList.size(); i++) {
				KeyTouch[] ngram = new KeyTouch[ngramLength];
				
				//populate ngram[]
				for (int j=0; j < ngramLength; j++) {
					ngram[j] = keyTouchList.get(i-ngramLength+j);
				}
        // i = 2 through N
        // [i-2, i-1]
				
				double ngramHold = (double) ngram[ngramLength-1].getHoldTime();
				double ngramProbability = getKeystrokeNgramProbability(ngram);
				writer.println(ngramHold+","+ngramProbability);
			}
			
			//only key presses
//			ArrayList<KSE> kseList = generateKSEKeyPresses(a.getKeyStrokes());
//			
//			boolean punctuationEncountered = false;
//			for (int i = ngramLength; i < kseList.size()-1; i++) {
//				KSE[] ngram = new KSE[ngramLength];
//				
//				//populate KSE[] with ngram KSEs
//				for (int j=0; j < ngramLength; j++) {
//					ngram[j] = kseList.get(i-ngramLength+j);
//				}
//				
//				double ngramPause = (double) ngram[ngramLength-1].getM_pauseMs();
//				double ngramProbability = getNgramProbability(ngram);
				
				//simple
//				writer.println(ngramPause+","+ngramProbability);
				
				//minus allPause mean
//				double allPauseLinMean = userPauseMap.get("allKeystrokesLinearMean");
//				writer.println((ngramPause-allPauseLinMean)+","+ngramProbability);
				
				//minus categorized pause
//				double preWordPauseLinMean = userPauseMap.get("preWordLinearMean");
//				double intraWordPauseLinMean = userPauseMap.get("intraWordLinearMean");
//				double clauseInitialPauseLinMean = userPauseMap.get("clauseInitialLinearMean");
//				double spacePauseLinMean = userPauseMap.get("spaceLinearMean");
//				double wordFinalPauseLinMean = userPauseMap.get("wordFinalLinearMean");
				
//				if (kseList.get(i-1).isSpace())
//					writer.println(ngramPause+","+/**(ngramPause-preWordPauseLinMean)+","+**/ngramProbability);
//				else 
//					writer.println(ngramPause+","+/**(ngramPause-intraWordPauseLinMean)+","+**/ngramProbability);
				
				/**--------------------------**/
				//log pause
//				double preWordPauseLogMean = userPauseMap.get("preWordLogMean");
//				double intraWordPauseLogMean = userPauseMap.get("intraWordLogMean");
//				if (kseList.get(i-1).isSpace())
//					writer.println((Math.log(ngramPause)-preWordPauseLogMean)+","+Math.log(ngramProbability));
//				else 
//					writer.println((Math.log(ngramPause)-intraWordPauseLogMean)+","+Math.log(ngramProbability));

//			}
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
		private double getNgramProbability(KSE[] ngram) {
			double probability = Double.NaN;
			
			if (ngram.length != ngramLength) {
				System.out.println("ngram.length != ngramLength");
				return probability;
			}
				
			switch (ngram.length) {
				case 1:
					probability = keyStrokeBigramModel.getUnigramCount(KeyStroke.vkCodetoString(ngram[0].getKeyCode()))/
								 (keyStrokeBigramModel.getUnigramTotalCount()*1.0);
					break;
				case 2:
					probability = keyStrokeBigramModel.getBigramProbability(ngram[0],ngram[1]);
					break;
				case 3:
					probability = keyStrokeTrigramModel.getTrigramProbability(ngram[0],ngram[1],ngram[2]);
					break;
				case 4:
					probability = keyStrokeFourgramModel.getFourgramProbability(ngram[0],ngram[1],ngram[2],ngram[3]);
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
