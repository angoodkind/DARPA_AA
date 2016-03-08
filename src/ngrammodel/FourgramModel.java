package ngrammodel;

import java.io.*;
import java.util.*;

import keystroke.KeyStroke; 

public class FourgramModel implements Serializable {
	
    private static final long serialVersionUID = 6529685098267757691L;
	private ArrayList<String> unigrams;
	private HashMap<String,Integer> unigramCounts;
	private HashMap<Bigram,Integer> bigramCounts;
	private HashMap<Trigram,Integer> trigramCounts;
	private HashMap<Trigram,Double> trigramProbabilities;
	private HashMap<Fourgram,Integer> fourgramCounts;
	private HashMap<Fourgram,Double> fourgramProbabilities;
	private int vocabularySize;
	private boolean generated = false;

	public FourgramModel() {}
	
	/**
	 * Create a FourgramModel from an ArrayList of unigram tokens
	 * @param unigrams	ArrayList of unigram tokens
	 */
	public FourgramModel(ArrayList<String> unigrams) {
		this.unigrams = unigrams;
		this.generate();
	}
	
	public FourgramModel(HashMap<Bigram,Integer> bigramCounts, HashMap<Trigram,Integer> trigramCounts, 
			HashMap<Fourgram,Integer> fourgramCounts, int vocabularySize) 
			throws ModelGeneratorException {
		this.unigrams = new ArrayList<String>(Arrays.asList("EMPTY")); //no need to create unigrams
		this.unigramCounts = new HashMap<String,Integer>();
		this.bigramCounts = bigramCounts;
		this.trigramCounts = trigramCounts;
		this.fourgramCounts = fourgramCounts;
		this.vocabularySize = vocabularySize;
		setFourgramProbabilities();
		generated = true;
	}
	
	/**
	 * Create a Fourgram Model from another Fourgram model
	 * @param trigramModel
	 */
	public FourgramModel(FourgramModel fourgramModel) {
		this.unigrams = fourgramModel.getUnigrams();
		this.unigramCounts = fourgramModel.getUnigramCountsMap();
		this.bigramCounts = fourgramModel.getBigramCountsMap();
		this.trigramCounts = fourgramModel.getTrigramCountsMap();
		this.fourgramCounts = fourgramModel.getFourgramCountsMap();
		this.fourgramProbabilities = fourgramModel.getFourgramProbabilitiesMap();
		this.vocabularySize = fourgramModel.getVocabularySize();
		this.generated = fourgramModel.isGenerated();
	}
	
	/**
	 * Imports a unigram list from a file
	 * @param unigramFileName	File containing unigram list
	 */
	public void setUnigramsFromFile(String unigramFileName) {
		try {
			File file = new File(unigramFileName);
			FileInputStream fileIStream = new FileInputStream(file);
			ObjectInputStream objectIStream = new ObjectInputStream(fileIStream);
			this.unigrams = (ArrayList<String>) objectIStream.readObject();
			objectIStream.close();
		} catch (IOException|ClassNotFoundException e) {e.printStackTrace();}
	}
	
	/**
	 * After unigram list has been added, generate a model
	 */
	public void generate() {
		try {
			this.setUnigramCounts();
			this.setVocabularySize();
			this.setBigramTrigramFourgramCounts();
			this.setFourgramProbabilities();
			generated = true;
		} catch (ModelGeneratorException e) {e.printStackTrace();}
	}
	
	/**
	 * From the unigram list, create the counts of all unique
	 * unigrams
	 * @throws ModelGeneratorException
	 */
	public void setUnigramCounts() throws ModelGeneratorException {
		if (unigrams==null) 
			throw new ModelGeneratorException("Unigrams not set");
		
		this.unigramCounts = new HashMap<String,Integer>();
		for (String unigram : unigrams) {
			if (unigramCounts.containsKey(unigram))
				unigramCounts.put(unigram, unigramCounts.get(unigram)+1);
			else {
				unigramCounts.put(unigram, 1);
			}
		}
	}
	
	
	/**
	 * From the unigram list, create counts of unique fourgrams
	 * @throws ModelGeneratorException
	 */
	public void setBigramTrigramFourgramCounts() throws ModelGeneratorException {
		if (unigrams==null || unigramCounts==null) 
			throw new ModelGeneratorException("Unigrams or unigramCounts not set");
		
		this.bigramCounts = new HashMap<Bigram,Integer>();
		this.trigramCounts = new HashMap<Trigram,Integer>();
		this.fourgramCounts = new HashMap<Fourgram,Integer>();
		
		for (int i = 0; i < unigrams.size()-3; i++) {
			String gram1 = unigrams.get(i);
			String gram2 = unigrams.get(i+1);
			String gram3 = unigrams.get(i+2);
			String gram4 = unigrams.get(i+3);
			Bigram bigram = new Bigram(gram1,gram2);
			Trigram trigram = new Trigram(gram1,gram2,gram3);
			Fourgram fourgram = new Fourgram(gram1,gram2,gram3,gram4);
			if (bigramCounts.containsKey(bigram))
				bigramCounts.put(bigram, bigramCounts.get(bigram)+1);
			else
				bigramCounts.put(bigram, 1);
			if (trigramCounts.containsKey(trigram))
				trigramCounts.put(trigram, trigramCounts.get(trigram)+1);
			else
				trigramCounts.put(trigram, 1);
			if (fourgramCounts.containsKey(fourgram))
				fourgramCounts.put(fourgram, fourgramCounts.get(fourgram)+1);
			else
				fourgramCounts.put(fourgram, 1);
		}
	}
	
	/**
	 * sets probabilities of existing fourgrams
	 * @throws ModelGeneratorException
	 */
	public void setFourgramProbabilities() throws ModelGeneratorException {
		if (unigrams==null || unigramCounts==null || bigramCounts==null || trigramCounts==null || fourgramCounts==null) 
			throw new ModelGeneratorException("Unigrams, unigramCounts, bigramCounts, trigramCounts or fourgramCounts not set");
		
		
		fourgramProbabilities = new HashMap<Fourgram,Double>();
		
		for (Fourgram fourgram : this.fourgramCounts.keySet()) {
			int trigramCount = this.getTrigramCount(new Trigram(fourgram.gram1,fourgram.gram2,fourgram.gram3));
			int fourgramCount = this.getFourgramCount(fourgram);
			double fourgramProbability = (fourgramCount*1.0)/trigramCount;
			fourgramProbabilities.put(fourgram, fourgramProbability);
		}
	}
	
	/**
	 * Returns a smoothed probability of a fourgram, using
	 * the inverse vocabulary size to smooth
	 * @return 	smoothed probability 
	 */
	public double getFourgramProbability(String gram1, String gram2, String gram3,String gram4) {
		int trigramCount = 0;
		int fourgramCount = 0;
		Trigram trigram = new Trigram(gram1,gram2,gram3);
		Fourgram fourgram = new Fourgram(gram1,gram2,gram3,gram4);
		if (this.getTrigramCountsMap().containsKey(trigram))
			trigramCount = this.getTrigramCount(trigram);
		if (this.getFourgramCountsMap().containsKey(fourgram))
			fourgramCount = this.getFourgramCount(fourgram);
			
		double inverseVocab = 1.0/this.getVocabularySize();
		double smoothed = ((double)fourgramCount+inverseVocab)/((double)trigramCount+(fourgramCounts.keySet().size()*inverseVocab));
		return smoothed;
	}
	
	public ArrayList<String> getUnigrams() {
		return unigrams;
	}

	public void setUnigrams(ArrayList<String> unigrams) {
		this.unigrams = unigrams;
	}
	
	public int getUnigramCount(String unigram) {
		return unigramCounts.get(unigram);
	}

	public HashMap<String, Integer> getUnigramCountsMap() {
		return unigramCounts;
	}

	public void setUnigramCountsMap(HashMap<String, Integer> unigramCounts) {
		this.unigramCounts = unigramCounts;
	}
	
	public int getBigramCount(Bigram bigram) {
		return this.bigramCounts.get(bigram);
	}

	public HashMap<Bigram, Integer> getBigramCountsMap() {
		return bigramCounts;
	}

	public void setBigramCountsMap(HashMap<Bigram, Integer> bigramCounts) {
		this.bigramCounts = bigramCounts;
	}
	
	public int getTrigramCount(Trigram trigram) {
		return this.trigramCounts.get(trigram);
	}

	public HashMap<Trigram, Integer> getTrigramCountsMap() {
		return trigramCounts;
	}

	public void setTrigramCountsMap(HashMap<Trigram, Integer> trigramCounts) {
		this.trigramCounts = trigramCounts;
	}
	
	public double getFourgramProbability(KeyStroke ks1, KeyStroke ks2, KeyStroke ks3, KeyStroke ks4) {
		String gram1 = KeyStroke.vkCodetoString(ks1.getKeyCode());
		String gram2 = KeyStroke.vkCodetoString(ks2.getKeyCode());
		String gram3 = KeyStroke.vkCodetoString(ks3.getKeyCode());
		String gram4 = KeyStroke.vkCodetoString(ks4.getKeyCode());
		return this.getFourgramProbability(gram1,gram2,gram3,gram4);
	}
	
	public double getFourgramProbability(Fourgram fourgram) {
		return this.getFourgramProbability(fourgram.getGram1(), fourgram.getGram2(), fourgram.getGram3(), fourgram.getGram4());
	}
	
	public HashMap<Fourgram, Double> getFourgramProbabilitiesMap() {
		return fourgramProbabilities;
	}

	public void setTrigramProbabilitiesMap(HashMap<Trigram, Double> trigramProbabilities) {
		this.trigramProbabilities = trigramProbabilities;
	}
	
	public void setVocabularySize() throws ModelGeneratorException {
		if (unigrams==null) 
			throw new ModelGeneratorException("Unigrams not set");
		
		Set<String> vocab = new HashSet<String>(unigrams);
		vocabularySize = vocab.size();
	}
	
	public int getVocabularySize() {
		return vocabularySize;
	}
	
	public boolean isGenerated() {
		return generated;
	}
	
	public HashMap<Fourgram,Integer> getFourgramCountsMap() {
		return fourgramCounts;
	}
	
	public int getFourgramCount(Fourgram fourgram) {
		return fourgramCounts.get(fourgram);
	}
	
	/**
	 * Writes a generated model to an external file
	 * @param fileName	File name where Model is to be written
	 * @throws ModelGeneratorException
	 */
	public void exportToFile(String fileName) throws ModelGeneratorException {
		if (!this.isGenerated()) 
			throw new ModelGeneratorException("Fourgram Model not generated");
		
		try {
			File file = new File(fileName);
			FileOutputStream fileOStream = new FileOutputStream(file);
			ObjectOutputStream objectOStream = new ObjectOutputStream(fileOStream);
			objectOStream.writeObject(this);
			objectOStream.close();
		} catch (IOException e) {e.printStackTrace();}
	}
	
	/**
	 * Read a fourgram model from an external file
	 * @param fileName	File containing fourgram Model
	 * @return	Fourgram model contained in file
	 */
	public static FourgramModel readFromFile(String fileName) {
		FourgramModel model = null;
		try {
			File file = new File(fileName);
			FileInputStream fileIStream = new FileInputStream(file);
			ObjectInputStream objectIStream = new ObjectInputStream(fileIStream);
			model = (FourgramModel) objectIStream.readObject();
			objectIStream.close();
		} catch(IOException|ClassNotFoundException e) {e.printStackTrace();}
		return model;
	}
	

}
