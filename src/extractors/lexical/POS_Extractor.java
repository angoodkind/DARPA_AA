package extractors.lexical;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTagger;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.util.InvalidFormatException;

/**
 * @author agoodkind
 * analyzes text for parts of speech (POS), using OpenNLP API
 * creates a list of Strings, with each token's POS
 * returns above list to LexFeatureExtractor
 */
public class POS_Extractor extends Tokenize {
	
	private POSTagger tagger;
	private String[] posTags;
	public final static String[] allPosTags = {"CC","CD","DT","EX","FW",
		"IN","JJ","JJR","JJS","LS","MD","NN","NNS","NNP","NNPS","PDT",
		"POS","PRP","PRP$","RB","RBR","RBS","RP","SYM","TO","UH","VB",
		"VBD","VBG","VBN","VBP","VBZ","WDT","WP","WP$","WRB"};
	
	public POS_Extractor() {
		InputStream isTagger = null;
		try {
			isTagger = new FileInputStream("en-pos-maxent.bin");
			POSModel pModel = new POSModel(isTagger);
			tagger = new POSTaggerME(pModel);
		} catch (IOException e) {e.printStackTrace();}
		finally {
			if (isTagger != null) {
				try {
					isTagger.close();
				} catch (IOException e) {e.printStackTrace();}
			}
		}
	}
	
	public POS_Extractor(String[] tokens) {
		this();
		this.posTags = tagger.tag(tokens);
	}
	
	public String[] createPOSTags(String[] tokenStr) throws InvalidFormatException, IOException {
		String[] pos = tagger.tag(tokenStr);
		return pos;
	}
	
	public double getLexDensity(String[] posTokens) {
		int lexicalWordCount = 0;
		Set<String> lexicalTags = new HashSet<String>(Arrays.asList(new String[] {"CD","JJ","JJR","JJS",
				"NN","NNS","NNP","NNPS","RB","RBR","RBS","VB","VBD","VBG","VBN","VBP","VBZ"}));
		
		for (String token : posTokens) {
			if (lexicalTags.contains(token)) 
				lexicalWordCount++;}
		
		double lexDensity = ((double)lexicalWordCount)/posTokens.length;
		return lexDensity;
	}
	
	// uses all possible noun tags to calculate total number of nouns
	public int getNounCount(String[] posTokens) {
		int nounCount = 0;
		Set<String> nounTags = new HashSet<String>(Arrays.asList(new String[] {"NN","NNS","NNP","NNPS","VBG"}));
		
		for (String token : posTokens) {
			if (nounTags.contains(token))
				nounCount++;}
		
		return nounCount;
	}
	
	// uses all possible verb tags to calculate total number of verbs
	public int getVerbCount(String[] posTokens) {
		int verbCount = 0;
		Set<String> verbTags = new HashSet<String>(Arrays.asList(new String[] {"VB","VBD","VBN","VBP","VBZ"}));
		
		for (String token : posTokens) {
			if (verbTags.contains(token))
				verbCount++;}
		
		return verbCount;
	}
	
	// uses all possible modifier tags to calculate total number of modifiers
	public int getModifierCount(String[] posTokens) {
		int modifierCount = 0;
		Set<String> modifierTags = new HashSet<String>(Arrays.asList(new String[] {"CD","DT","JJ","JJR","JJS","RB","RBR","RBS","WDT","WRB"}));
		
		for (String token : posTokens) {
			if (modifierTags.contains(token))
				modifierCount++;}
		
		return modifierCount;
	}
	
	// uses modal tag to calculate total number of modals
	public int getModalCount(String[] posTokens) {
		int modalCount = 0;
		Set<String> modalTags = new HashSet<String>(Arrays.asList(new String[] {"MD"}));
		
		for (String token : posTokens) {
			if (modalTags.contains(token))
				modalCount++;
    }
		
		return modalCount;
	}
	
	/* checks which POSs are being requested
	 * populates POS_Tags
	 * returns elements of POS_Tags based on which POS is entered
	 */
	public List<String> getPOS_Tags(String POS) {
		
		List<String> POS_Tags = new ArrayList<String>();
		
		switch(POS.toLowerCase()) {
		case "modal":
			POS_Tags = Arrays.asList("MD");
			break;
		case "noun":
			POS_Tags = Arrays.asList("NN","NNS","NNP","NNPS","VBG");
			break;
		case "verb":
			POS_Tags = Arrays.asList("VB","VBD","VBN","VBP","VBZ");
			break;
		case "modifier":
			POS_Tags = Arrays.asList("CD","DT","JJ","JJR","JJS","RB","RBR","RBS","WDT","WRB");
			break;
		case "punct":
			POS_Tags = Arrays.asList(".");
			break;
		}
		return POS_Tags;
	}
	
	public HashMap<String,Integer> getPosTagMap() {
		HashMap<String,Integer> tagMap = new HashMap<String,Integer>();
		for (String posTag : posTags) {
			if (tagMap.containsKey(posTag))
				tagMap.put(posTag, tagMap.get(posTag)+1);
			else
				tagMap.put(posTag, 1);
		}
		return tagMap;
	}
	
	public void setPosTags(String[] tokens) {
		this.posTags = tagger.tag(tokens);
	}
	
	public String[] getPosTags() {
		return posTags;
	}
	
	public final static HashSet<String> contentPOS = new HashSet<String>(Arrays.asList("CD","FW","JJ","JJR","JJS","MD","NN",
		"NNS","NNP","NNPS","RB","RBR","RBS","UH","VB","VBD","VBG","VBN","VBP","VBZ","WRB"));
	
	public final static HashSet<String> functionPOS = new HashSet<String>(Arrays.asList("CC","DT","EX","IN","LS","PDT","POS",
		"PRP","PRP$","RP","SYM","TO","WDT","WP","WP$"));
	
	public static boolean isContentPOS(String tag) {
		if (contentPOS.contains(tag))
			return true;
		return false;
	}
	
	public static boolean isFunctionPOS(String tag) {
		if (functionPOS.contains(tag))
			return true;
		return false;
	}
}
