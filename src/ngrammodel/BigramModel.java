package ngrammodel;

import java.io.*;
import java.util.*;

import keystroke.KeyStroke;

public class BigramModel implements Serializable {

    private static final long serialVersionUID = 6529685098267757691L;
	private ArrayList<String> unigrams;
	private HashMap<String,Integer> unigramCounts;
	private HashMap<Bigram,Integer> bigramCounts;
	private HashMap<Bigram,Double> bigramProbabilities;
	private int vocabularySize;
	private boolean generated = false;

	public BigramModel() {}
	
	/**
	 * Create a BigramModel from an ArrayList of unigram tokens
	 * @param unigrams	ArrayList of unigram tokens
	 */
	public BigramModel(ArrayList<String> unigrams) {
		this.unigrams = unigrams;
		this.generate();
	}
	
	public BigramModel(HashMap<String,Integer> unigramCounts, HashMap<Bigram,Integer> bigramCounts, int vocabularySize) 
						throws ModelGeneratorException {
		this.unigrams = new ArrayList<String>(Arrays.asList("EMPTY")); //no need to create unigrams
		this.unigramCounts = unigramCounts;
		this.bigramCounts = bigramCounts;
		this.vocabularySize = vocabularySize;
		setBigramProbabilities();
		generated = true;
	}
	
	/**
	 * Create a Bigram Model from another Bigram model
	 * @param bigramModel
	 */
	public BigramModel(BigramModel bigramModel) {
		this.unigrams = bigramModel.getUnigrams();
		this.unigramCounts = bigramModel.getUnigramCountsMap();
		this.bigramCounts = bigramModel.getBigramCountsMap();
		this.bigramProbabilities = bigramModel.getBigramProbabilitiesMap();
		this.vocabularySize = bigramModel.getVocabularySize();
		this.generated = bigramModel.isGenerated();
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
			this.setBigramCounts();
			this.setBigramProbabilities();
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
	
	public double getUnigramProbability(String unigram) {
		int unigramCount = 1;
		if (unigramCounts.containsKey(unigram))
			unigramCount = unigramCounts.get(unigram);
		double inverseVocab = 1.0/this.getVocabularySize();
		double smoothed = ((double)unigramCount+inverseVocab)/
						  ((double)unigramCounts.keySet().size());
		return smoothed;
	}
	
	/**
	 * From the unigram list, create counts of unique bigrams
	 * @throws ModelGeneratorException
	 */
	public void setBigramCounts() throws ModelGeneratorException {
		if (unigrams==null || unigramCounts==null) 
			throw new ModelGeneratorException("Unigrams or unigramCounts not set");
		
		this.bigramCounts = new HashMap<Bigram,Integer>();
		
		for (int i = 0; i < unigrams.size()-1; i++) {
			//skip {"STOP","START"} bigram
			if (unigrams.get(i).equals("STOP"))
				i++;
			String gram1 = unigrams.get(i);
			String gram2 = unigrams.get(i+1);
			Bigram bigram = new Bigram(gram1,gram2);
			if (bigramCounts.containsKey(bigram))
				bigramCounts.put(bigram, bigramCounts.get(bigram)+1);
			else
				bigramCounts.put(bigram, 1);
		}
	}
	
	/**
	 * sets probabilities of existing bigrams
	 * @throws ModelGeneratorException
	 */
	public void setBigramProbabilities() throws ModelGeneratorException {
		if (unigrams==null || unigramCounts==null || bigramCounts==null) 
			throw new ModelGeneratorException("Unigrams, unigramCounts or bigramCounts not set");
		
		
		bigramProbabilities = new HashMap<Bigram,Double>();
		
		for (Bigram bigram : this.bigramCounts.keySet()) {
			int gram1Count = this.getUnigramCount(bigram.getGram1());
			int bigramCount = this.getBigramCount(bigram);
			double bigramProbability = (bigramCount*1.0)/gram1Count;
			bigramProbabilities.put(bigram, bigramProbability);
		}
	}
	
	/**
	 * Returns a smoothed probability of a bigram, using
	 * the inverse vocabulary size to smooth
	 * @return 	smoothed probability 
	 */
	public double getBigramProbability(String gram1, String gram2) {
		int gram1Count = 0;
		int bigramCount = 0;
		Bigram bigram = new Bigram(gram1,gram2);
//		System.out.println(bigram.gram1+" "+bigram.gram2);
		if (this.getUnigramCountsMap().containsKey(gram1))
			gram1Count = this.getUnigramCount(bigram.getGram1());
		if (this.getBigramCountsMap().containsKey(bigram))
			bigramCount = this.getBigramCount(bigram);
		
		double inverseVocab = 1.0/this.getVocabularySize();
		double smoothed = ((double)bigramCount+inverseVocab)/
						  ((double)gram1Count+(bigramCounts.keySet().size()*inverseVocab));
		return smoothed;
	}

  /**
   * Returns a smoothed probability of a bigram, using
   * the inverse vocabulary size to smooth
   * @return 	smoothed probability
   */
  public double getLogBigramProbability(String gram1, String gram2) {
    int gram1Count = 0;
    int bigramCount = 0;
    Bigram bigram = new Bigram(gram1,gram2);
    if (this.getUnigramCountsMap().containsKey(gram1))
      gram1Count = this.getUnigramCount(bigram.getGram1());
    if (this.getBigramCountsMap().containsKey(bigram))
      bigramCount = this.getBigramCount(bigram);

    double inverseVocab = 1.0/this.getVocabularySize();
    double smoothed = Math.log((double)bigramCount+inverseVocab) - Math.log(
        ((double)gram1Count+(bigramCounts.keySet().size()*inverseVocab)));
    return smoothed;
  }
	
	public ArrayList<String> getUnigrams() {
		return unigrams;
	}

	public void setUnigrams(ArrayList<String> unigrams) {
		this.unigrams = unigrams;
	}
	
	public int getUnigramTotalCount() {
		int grams = 0;
		for (String unigram : unigramCounts.keySet())
			grams += unigramCounts.get(unigram);
		
		return grams;
	}
	
	public int getUnigramCount(String unigram) {
		if (unigramCounts.get(unigram) != null)
			return unigramCounts.get(unigram);
		else
			return 1;
	}

	public HashMap<String, Integer> getUnigramCountsMap() {
		return unigramCounts;
	}

	public void setUnigramCounts(HashMap<String, Integer> unigramCounts) {
		this.unigramCounts = unigramCounts;
	}
	
	public int getBigramCount(Bigram bigram) {
		return this.bigramCounts.get(bigram);
	}

	public HashMap<Bigram, Integer> getBigramCountsMap() {
		return bigramCounts;
	}

	public void setBigramCounts(HashMap<Bigram, Integer> bigramCounts) {
		this.bigramCounts = bigramCounts;
	}
	
	public double getBigramProbability(KeyStroke ks1, KeyStroke ks2) {
		String gram1 = KeyStroke.vkCodetoString(ks1.getKeyCode());
		String gram2 = KeyStroke.vkCodetoString(ks2.getKeyCode());
		return this.getBigramProbability(gram1,gram2);
	}
	
	public double getBigramProbability(Bigram bigram) {
		return this.getBigramProbability(bigram.getGram1(), bigram.getGram2());
	}

  public double getLogBigramProbability(Bigram bigram) {
    return this.getLogBigramProbability(bigram.getGram1(), bigram.getGram2());
  }


  public HashMap<Bigram, Double> getBigramProbabilitiesMap() {
		return bigramProbabilities;
	}

	public void setBigramProbabilities(HashMap<Bigram, Double> bigramProbabilities) {
		this.bigramProbabilities = bigramProbabilities;
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
	
	/**
	 * Writes a generated model to an external file
	 * @param fileName	File name where Model is to be written
	 * @throws ModelGeneratorException
	 */
	public void exportToFile(String fileName) throws ModelGeneratorException {
		if (!this.isGenerated()) 
			throw new ModelGeneratorException("Bigram Model not generated");
		
		try {
			File file = new File(fileName);
			FileOutputStream fileOStream = new FileOutputStream(file);
			ObjectOutputStream objectOStream = new ObjectOutputStream(fileOStream);
			objectOStream.writeObject(this);
			objectOStream.close();
		} catch (IOException e) {e.printStackTrace();}
	}
	
	/**
	 * Read a bigram model from an external file
	 * @param fileName	File containing bigram Model
	 * @return	Bigram model contained in file
	 */
	public static BigramModel readFromFile(String fileName) {
		BigramModel model = null;
		try {
			File file = new File(fileName);
			FileInputStream fileIStream = new FileInputStream(file);
			ObjectInputStream objectIStream = new ObjectInputStream(fileIStream);
			model = (BigramModel) objectIStream.readObject();
			objectIStream.close();
		} catch(IOException|ClassNotFoundException e) {e.printStackTrace();}
		return model;
	}
	
}
