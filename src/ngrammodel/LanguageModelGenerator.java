package ngrammodel;

public class LanguageModelGenerator {

	/**
	 * args[0] - 	unigram token file name, e.g. "DEBUGWordTokens.data". 
	 * 				Bigram and Trigram model files will use the same unique
	 * 				name, and be placed in their respective folders.
	 * @param args
	 */
	public static void main(String[] args) {
		
		try {
			if (args.length < 1) {
		  		System.err.println("Bad args format:\n" +
						"/**" +
						 "* Format of args\n"+
						 "* [0] - unigram token file name"+
						 "**/");
				return;
			}
			String unigramPrefix = "LM-Unigram-Token-Files/";
			String bigramPrefix = "LM-BigramModel-Files/";
			String trigramPrefix = "LM-TrigramModel-Files/";
			String fourgramPrefix = "LM-FourgramModel-Files/";
			
			BigramModel bigramModel = new BigramModel();
			bigramModel.setUnigramsFromFile(unigramPrefix+args[0]);
			bigramModel.generate();
			bigramModel.exportToFile(bigramPrefix+args[0]);
			
			TrigramModel trigramModel = new TrigramModel();
			trigramModel.setUnigramsFromFile(unigramPrefix+args[0]);
			trigramModel.generate();
			trigramModel.exportToFile(trigramPrefix+args[0]);
			
			FourgramModel fourgramModel = new FourgramModel();
			fourgramModel.setUnigramsFromFile(unigramPrefix+args[0]);
			fourgramModel.generate();
			fourgramModel.exportToFile(fourgramPrefix+args[0]);
			
		} catch (Exception|ModelGeneratorException e) {e.printStackTrace();}
	}
	
	

}
