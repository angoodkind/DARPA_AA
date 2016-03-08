package ngrammodel;

import java.io.*;
import java.util.*;

/**
 * Used with n-gram distribution files from the Corpus
 * of Contemporary American English (BYU)
 * Reads in a tab-delimited n-gram file, and creates an
 * n-gram model file
 * @author Adam Goodkind
 *
 */
public class GenerateModelFromCOCA {

	public static void main(String[] args) {
//		generateBigramModelFile();
//		generateTrigramModelFile();
		generateFourgramModelFile();
	}
	
	public static void generateBigramModelFile() {
		
		try {
			HashMap<String,Integer> unigramCounts = new HashMap<String,Integer>();
			HashMap<Bigram,Integer> bigramCounts = new HashMap<Bigram,Integer>();
			HashSet<String> vocabulary = new HashSet<String>();
			BufferedReader bigramReadBuffer = new BufferedReader(new FileReader("COCA_Bigrams.txt"));
			String strRead;
			
			while ((strRead=bigramReadBuffer.readLine())!=null){
				
				String[] bigramArray = strRead.split("\t");
				int count = Integer.parseInt(bigramArray[0]);
				String gram1 = bigramArray[1];
				String gram2 = bigramArray[2];
				Bigram bigram = new Bigram(gram1,gram2);
				
				//increment unigram counts
				if (unigramCounts.containsKey(gram1))
					unigramCounts.put(gram1, unigramCounts.get(gram1)+count);
				else
					unigramCounts.put(gram1, count);
				
				//increment bigram counts
				if (bigramCounts.containsKey(bigram))
					bigramCounts.put(bigram, bigramCounts.get(bigram)+count);
				else
					bigramCounts.put(bigram, count);
				
				//increment vocabulary set
				vocabulary.add(gram1);
				vocabulary.add(gram2);
			}
			
				//create BigramModel
				BigramModel bigramModel = new BigramModel(unigramCounts,bigramCounts,vocabulary.size());
				System.out.println(bigramModel.getUnigramCount("a"));
				
				//write Bigram Model to file
				bigramModel.exportToFile("COCA_BigramWordModel.data");
				

			bigramReadBuffer.close();
		} catch (IOException | ModelGeneratorException e) {e.printStackTrace();}
	}

	public static void generateTrigramModelFile() {
		
		try {
			HashMap<Bigram,Integer> bigramCounts = new HashMap<Bigram,Integer>();
			HashMap<Trigram,Integer> trigramCounts = new HashMap<Trigram,Integer>();
			HashSet<String> vocabulary = new HashSet<String>();
			BufferedReader trigramReadBuffer = new BufferedReader(new FileReader("COCA_Trigrams.txt"));
			String strRead;
			
			while ((strRead=trigramReadBuffer.readLine())!=null){
				
				String[] bigramArray = strRead.split("\t");
				int count = Integer.parseInt(bigramArray[0]);
				String gram1 = bigramArray[1];
				String gram2 = bigramArray[2];
				String gram3 = bigramArray[3];
				Bigram bigram = new Bigram(gram1,gram2);
				Trigram trigram = new Trigram(gram1,gram2,gram3);
				
				//increment bigram counts
				if (bigramCounts.containsKey(bigram))
					bigramCounts.put(bigram, bigramCounts.get(bigram)+count);
				else
					bigramCounts.put(bigram, count);
				
				//increment trigram counts
				if (trigramCounts.containsKey(trigram))
					trigramCounts.put(trigram, trigramCounts.get(trigram)+count);
				else
					trigramCounts.put(trigram, count);
				
				//increment vocabulary set
				vocabulary.add(gram1);
				vocabulary.add(gram2);
				vocabulary.add(gram3);
			}
			
				//create TrigramModel
				TrigramModel trigramModel = new TrigramModel(bigramCounts,trigramCounts,vocabulary.size());
				
				//write Bigram Model to file
				trigramModel.exportToFile("COCA_TrigramWordModel.data");
				
				
			trigramReadBuffer.close();
		} catch (IOException | ModelGeneratorException e) {e.printStackTrace();}
	}
	
	public static void generateFourgramModelFile() {
		
		try {
			HashMap<Bigram,Integer> bigramCounts = new HashMap<Bigram,Integer>();
			HashMap<Trigram,Integer> trigramCounts = new HashMap<Trigram,Integer>();
			HashMap<Fourgram,Integer> fourgramCounts = new HashMap<Fourgram,Integer>();
			HashSet<String> vocabulary = new HashSet<String>();
			BufferedReader fourgramReadBuffer = new BufferedReader(new FileReader("COCA_Fourgrams.txt"));
			String strRead;
			
			while ((strRead=fourgramReadBuffer.readLine())!=null){
				
				String[] fourgramArray = strRead.split("\t");
				int count = Integer.parseInt(fourgramArray[0]);
				String gram1 = fourgramArray[1];
				String gram2 = fourgramArray[2];
				String gram3 = fourgramArray[3];
				String gram4 = fourgramArray[4];
				Bigram bigram = new Bigram(gram1,gram2);
				Trigram trigram = new Trigram(gram1,gram2,gram3);
				Fourgram fourgram = new Fourgram(gram1,gram2,gram3,gram4);
				
				//increment bigram counts
				if (bigramCounts.containsKey(bigram))
					bigramCounts.put(bigram, bigramCounts.get(bigram)+count);
				else
					bigramCounts.put(bigram, count);
				
				//increment trigram counts
				if (trigramCounts.containsKey(trigram))
					trigramCounts.put(trigram, trigramCounts.get(trigram)+count);
				else
					trigramCounts.put(trigram, count);
				
				//increment fourgram counts
				if (fourgramCounts.containsKey(fourgram))
					fourgramCounts.put(fourgram, fourgramCounts.get(fourgram)+count);
				else
					fourgramCounts.put(fourgram, count);
				
				//increment vocabulary set
				vocabulary.add(gram1);
				vocabulary.add(gram2);
				vocabulary.add(gram3);
				vocabulary.add(gram4);
			}
			
				//create FourgramModel
				FourgramModel fourgramModel = new FourgramModel(bigramCounts,trigramCounts,fourgramCounts,vocabulary.size());
				
				//write Bigram Model to file
				fourgramModel.exportToFile("COCA_FourgramWordModel.data");
				
				
			fourgramReadBuffer.close();
		} catch (IOException | ModelGeneratorException e) {e.printStackTrace();}
	}


}
