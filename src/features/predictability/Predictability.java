/**
 * See Test_Predictability for an example of implementation
 * 
 * Abstract class implemented by all predictability modules
 */
package features.predictability;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import events.EventList;
import extractors.data.Answer;
import extractors.lexical.TokenExtender;
import features.pause.KSE;
import keystroke.KeyStroke;
import keytouch.KeyTouch;
import mwe.TokenExtended;
import ngrammodel.Bigram;
import ngrammodel.BigramModel;
import ngrammodel.Fourgram;
import ngrammodel.FourgramModel;
import ngrammodel.Trigram;
import ngrammodel.TrigramModel;

public abstract class Predictability {
	//user pause map (TODO: Make name part of constructor)
	static final String userPauseMapFile = "Testing_3CategoryDurationMetrics.map"; 
	protected static final HashMap<Integer,HashMap<String,Double>> allUsersPauseMap = loadPauseMapFromFile(userPauseMapFile);
	
	//variables for Ngram Model to utilize
	protected static final String bigramFilePrefix = "LM-BigramModel-Files/";
	protected BigramModel keyStrokeBigramModel;
	protected BigramModel wordBigramModel;
	
	protected static final String trigramFilePrefix = "LM-TrigramModel-Files/";
	protected TrigramModel keyStrokeTrigramModel;
	protected TrigramModel wordTrigramModel;
	
	protected static final String fourgramFilePrefix = "LM-FourgramModel-Files/";
	protected FourgramModel keyStrokeFourgramModel;
	protected FourgramModel wordFourgramModel;
	
	/**
	 * Each predictability module takes two parameters, as outlined below
	 * 
	 * @param modelName		The collection from which the model was generated, e.g 'DEBUG'
	 * @param gramType		Can be either 'keystroke','word', or 'pos'
	 * 
	 */
	public Predictability(String modelName, String gramType) {
		switch (gramType) {
			case "keystroke":
				this.keyStrokeBigramModel = BigramModel.readFromFile(bigramFilePrefix+modelName+"KeyStrokeModel.data");
				this.keyStrokeTrigramModel = TrigramModel.readFromFile(trigramFilePrefix+modelName+"KeyStrokeModel.data");
				this.keyStrokeFourgramModel = FourgramModel.readFromFile(fourgramFilePrefix+modelName+"KeyStrokeModel.data");
				break;
			case "word":
				this.wordBigramModel = BigramModel.readFromFile(bigramFilePrefix+modelName+"WordModel.data");
				this.wordTrigramModel = TrigramModel.readFromFile(trigramFilePrefix+modelName+"WordModel.data");
				this.wordFourgramModel = FourgramModel.readFromFile(fourgramFilePrefix+modelName+"WordModel.data");
				break;
			case "pos":
				this.wordBigramModel = BigramModel.readFromFile(bigramFilePrefix+modelName+"WordModel.data");
				break;
			case "hybrid":
				this.keyStrokeBigramModel = BigramModel.readFromFile(bigramFilePrefix+modelName+"KeyStrokeModel.data");
//				this.keyStrokeTrigramModel = TrigramModel.readFromFile(trigramFilePrefix+modelName+"KeyStrokeModel.data");
//				this.keyStrokeFourgramModel = FourgramModel.readFromFile(fourgramFilePrefix+modelName+"KeyStrokeModel.data");
				this.wordBigramModel = BigramModel.readFromFile(bigramFilePrefix+modelName+"WordModel.data");
//				this.wordTrigramModel = TrigramModel.readFromFile(trigramFilePrefix+modelName+"WordModel.data");
//				this.wordFourgramModel = FourgramModel.readFromFile(fourgramFilePrefix+modelName+"WordModel.data");
				break;
			default:
				throw new IllegalArgumentException("Invalid gram type: "+gramType);
		}
	}
	
	/**
	 * Calculates predictability of ngram, as expressed in an array
	 * of Strings
	 * 
	 * @param ngram		String[] of individual grams
	 * @param pause		pause before last gram, or whichever pause is to
	 * 					be predictabilized
	 * 
	 * @return			predictabilized pause
	 */
	protected double getWordNgramPredictability(String[] ngram, long pause, String pauseType, int userID) {
		double probability = getWordNgramProbability(ngram);
		double predictability = getZScore(pause,probability,pauseType,userID);
		return predictability;
	}
	
	/**
	 * Calculates predictability of ngram, as expressed in an array
	 * of TimingTokens
	 * 
	 * @param ngram		TimingToken[] of individual grams
	 * @return			predictabilized pause
	 */
	protected double getWordNgramPredictability(TimingToken[] ngram, String pauseType, int userID) {
		String[] ngramStr = new String[ngram.length];
		for (int i = 0; i < ngram.length; i++)
			ngramStr[i] = ngram[i].token;
		
		double probability = getWordNgramProbability(ngramStr);
		double pause = (double) ngram[ngram.length-1].pause;
		double predictability = getZScore(pause,probability,pauseType,userID);
		return predictability;
	}
	
	/**
	 * Calculates predictability of ngram, as expressed in an array
	 * of KSEs
	 * 
	 * @param ngram		KSE[] of individual grams
	 * 
	 * @return			predictabilized pause
	 */
	protected double getKeystrokeNgramPredictability(KSE[] ngram, String pauseType, int userID) {
		double probability = getKeystrokeNgramProbability(ngram);
		double pause = (double) ngram[ngram.length-1].getM_pauseMs();
		double predictability = getZScore(pause,probability,pauseType,userID);
		
		return predictability;
	}
	
	/**
	 * Calculates predictability of ngram, as expressed in an array
	 * of KSEs
	 * 
	 * Uses hold time, rather than preceding pause
	 * 
	 * @param ngram		KeyTouch[] of individual grams
	 * 
	 * @return			predictabilized pause
	 */
	protected double getKeystrokeNgramPredictability(KeyTouch[] ngram, int userID) {
		String pauseType;
		if (KeyStroke.vkCodetoString(ngram[0].getKeyCode()).equals("Spacebar"))
			pauseType = "preWord";
		else
			pauseType = "intraWord";
		
		double probability = getKeystrokeNgramProbability(ngram);
		double pause = 0.0; //duration of entire ngram
		for (KeyTouch k : ngram)
			pause += k.getHoldTime();
		double predictability = getZScore(pause,probability,pauseType,userID);
		
		return predictability;
	}
	
	/**
	 * Based on array (ngram) length, calculated ngram's probability
	 * from appropriate model
	 * 
	 * Called by getKeystrokeNgramPredictability()
	 * 
	 * @param ngram		KSE[] of individual grams
	 * @return			ngram probability
	 */
	protected double getKeystrokeNgramProbability(KSE[] ngram) {
		double probability = Double.NaN;
			
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
	
	/**
	 * Based on array (ngram) length, calculated ngram's probability
	 * from appropriate model. Strings should match KeyStroke.vkCodeToString
	 * 
	 * Called by getKeystrokeNgramPredictability()
	 * 
	 * @param ngram		String[] of individual grams
	 * @return			ngram probability
	 */
	protected double getKeystrokeNgramProbability(String[] ngram) {
		double probability = Double.NaN;
			
		switch (ngram.length) {
			case 1:
				probability = keyStrokeBigramModel.getUnigramCount(ngram[0])/
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
	
	/**
	 * Based on String (ngram) length, calculated ngram's probability
	 * from appropriate model
	 * 
	 * Called by getKeystrokeNgramPredictability()
	 * 
	 * @param ngram		String of ngram
	 * @return			ngram probability
	 */
	protected double getKeystrokeNgramProbability(String ngram) {
		double probability = Double.NaN;
			
		switch (ngram.length()) {
			case 1:
				probability = keyStrokeBigramModel.getUnigramCount(ngram.toUpperCase())/
							 (keyStrokeBigramModel.getUnigramTotalCount()*1.0);
				break;
			case 2:
				probability = keyStrokeBigramModel.getBigramProbability(new Bigram(ngram));
				break;
			case 3:
				probability = keyStrokeTrigramModel.getTrigramProbability(new Trigram(ngram));
				break;
			case 4:
				probability = keyStrokeFourgramModel.getFourgramProbability(new Fourgram(ngram));
				break;
			default:
				System.out.println("Invalid Gram Length: "+ngram.length());
				break;
		}
		return probability;
	}
	
	/**
	 * Based on array (ngram) length, calculated ngram's probability
	 * from appropriate model
	 * 
	 * Called by getKeystrokeNgramPredictability()
	 * 
	 * @param ngram		KeyTouch[] of individual grams
	 * @return			ngram probability
	 */
	protected double getKeystrokeNgramProbability(KeyTouch[] ngram) {
		double probability = Double.NaN;
		
		String[] ngramStr = new String[ngram.length];
		for (int i=0; i < ngram.length; i++) {
			ngramStr[i] = KeyStroke.vkCodetoString(ngram[i].getKeyCode());
		}
			
		switch (ngram.length) {
			case 1:
				probability = keyStrokeBigramModel.getUnigramCount(KeyStroke.vkCodetoString(ngram[0].getKeyCode()))/
							 (keyStrokeBigramModel.getUnigramTotalCount()*1.0);
				break;
			case 2:
				probability = keyStrokeBigramModel.getBigramProbability(ngramStr[0],ngramStr[1]);
				break;
			case 3:
				probability = keyStrokeTrigramModel.getTrigramProbability(ngramStr[0],ngramStr[1],ngramStr[2]);
				break;
			case 4:
				probability = keyStrokeFourgramModel.getFourgramProbability(ngramStr[0],ngramStr[1],ngramStr[2],ngramStr[3]);
				break;
			default:
				System.out.println("Invalid Gram Length: "+ngram.length);
				break;
		}
		return probability;
	}
	
	/**
	 * One method of predictabilizing pause time
	 * 
	 * @param pause			Length of pause to be predictabilized
	 * @param probability	Probability of ngram used to predictabilie pause
	 * 
	 * @return				predictabilized pause
	 */
	protected double getZScore(double pause, double probability, String pauseType, int userID) {
		double mean = allUsersPauseMap.get(userID).get(pauseType+"LogMean");
		double stdDev = allUsersPauseMap.get(userID).get(pauseType+"LogStdDev");
		double zScore = (Math.log(pause)-mean)/stdDev;
//		System.out.printf("%.4f %.4f %.4f %n",mean,stdDev,zScore);
		return zScore;
	}
	
//	/**
//	 * One method of predictabilizing pause time
//	 * 
//	 * @param pause			Length of pause to be predictabilized
//	 * @param probability	Probability of ngram used to predictabilie pause
//	 * 
//	 * @return				predictabilized pause
//	 */
//	protected double pauseDividedByLogProbability(double pause, double probability) {
//		return pause/(-1.0*Math.log(probability));
//	}
	
	/**
	 * Based on array (ngram) length, calculated ngram's probability
	 * from appropriate model
	 * 
	 * called by getWordNgramPredictability()
	 * 
	 * @param ngram		String[] of individual grams
	 * 
	 * @return			ngram probability
	 */
	protected double getWordNgramProbability(String[] ngram) {
		double probability = Double.NaN;
			
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
	
	
	
	protected static class TimingToken {
		protected String token;
		protected long pause;
		protected String POS;
		
		protected TimingToken(String token, long pause, String POS) {
			this.token = token;
			this.pause = pause;
			this.POS = POS;
		}
		
		public String toString() {
			return (token+" "+POS+" "+pause);
		}
	}
	
	protected static ArrayList<TimingToken> createTimingTokenList(Answer answer) {
		ArrayList<TimingToken> tokenList = new ArrayList<TimingToken>();
		
		ArrayList<KSE> kseList = new ArrayList<KSE>(KSE.parseSessionToKSE(answer.getKeyStrokes()));
		ArrayList<KSE> visibleKSEs = KSE.parseToVisibleTextKSEs(kseList);
		
		EventList<KeyStroke> keyStrokeList = answer.getKeyStrokeList();
		String visibleText = keyStrokeList.toVisibleTextString();
		
		TokenExtender extender = new TokenExtender();
		ArrayList<TokenExtended> tokens = extender.generateExtendedTokens(visibleText);
		
		for (TokenExtended tokenE : tokens) {
			String word = tokenE.token;
			KSE wordBegin = visibleKSEs.get(tokenE.getTokenSpan().begin);
			long pause = wordBegin.getM_pauseMs();
			String POS = tokenE.getPartOfSpeech();
			TimingToken tt = new TimingToken(word,pause,POS);
			tokenList.add(tt);
		}
		
		return tokenList;
	}
	
	public static double RootMSE(Collection<Double> values) {
		double mse = 0;
		double mean = mean(values);
		for (double value : values)
			mse += Math.pow(value-mean,2);
		mse /= values.size();
		return Math.sqrt(mse);
	}
	
	public static double StdDev(Collection<Double> values) {
		return Math.sqrt(variance(values));
	}
	
    public static double variance(Collection<Double> values) {
        double avg = mean(values);
        double sum = 0.0;
        for (double value : values) {
            sum += (value - avg) * (value - avg);
        }
        return sum / (values.size() - 1);
    }
    
    public static double mean(Collection<Double> values) {
        double sum = 0.0;
        for (double value : values) {
            sum += value;
        }
        return sum / values.size();
    }
    
    protected double meanPause(ArrayList<KSE> kseList) {
        double sum = 0.0;
        for (KSE kse : kseList) {
        		sum += kse.getM_pauseMs();
        }
        return sum / kseList.size();
    }
    
    /**
     * Generates a list of KSEs, and then filters out only KeyPresses
     * 
     * @param keyStrokeString	Answer.getKeyStrokes()
     * @return
     */
    public static ArrayList<KSE> generateKSEKeyPresses(String keyStrokeString) {
	    ArrayList<KSE> kseList = new ArrayList<KSE>(KSE.parseSessionToKSE(keyStrokeString));
		ArrayList<KSE> keyPresses = new ArrayList<KSE>();
		for (KSE kse : kseList)
			if (kse.isKeyPress()) 
				keyPresses.add(kse);
		return keyPresses;
    }
    
    /**
     * @param keyStrokeString	Answer.getKeyStrokes()
     * @return
     */
    protected ArrayList<KSE> generateVisibleKSEs(String keyStrokeString) {
    		ArrayList<KSE> kseList = new ArrayList<KSE>(KSE.parseSessionToKSE(keyStrokeString));
		ArrayList<KSE> visibleKSEs = KSE.parseToVisibleTextKSEs(kseList);
		return visibleKSEs;
    }
    
    /**
     * use for word predictability
     * @param keyStrokeList	Answer,getKeyStrokeList()
     * @return a list of word tokens extended
     */
    protected ArrayList<TokenExtended> generateVisibleExtendedTokens(EventList<KeyStroke> keyStrokeList) {
		String visibleText = keyStrokeList.toVisibleTextString();
		TokenExtender extender = new TokenExtender();
		ArrayList<TokenExtended> tokens = extender.generateExtendedTokens(visibleText);
		return tokens;
    }
    /**
     * Helper function to convert small or large doubles that
     * use scientific notation into readable Strings
     * @param probability	a double
     * @return
     */
    public static String probabilityToString(double probability) {
    	DecimalFormat df = new DecimalFormat("#.#");
    	df.setMaximumFractionDigits(8);
    	String formattedString = df.format(probability);
    	return formattedString;
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
    
}
