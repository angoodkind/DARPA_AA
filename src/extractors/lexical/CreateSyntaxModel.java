package extractors.lexical;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;

import extractors.data.Answer;
import extractors.data.DataNode;
import extractors.data.ExtractionModule;
import extractors.data.Feature;
import opennlp.tools.util.InvalidFormatException;
import ngrammodel.Bigram;
import ngrammodel.BigramModel;
import ngrammodel.ModelGeneratorException;

/**
 * Creates a file of a HashMap, containing POS transition likelihoods
 * s
 * @author Adam Goodkind
 *
 */
public class CreateSyntaxModel implements ExtractionModule {

	private static Tokenize tokenize = new Tokenize();
	private static POS_Extractor posExtractor = new POS_Extractor();
	private static HashMap<Bigram,Integer> posBigramMap = new HashMap<Bigram,Integer>();
	private static ArrayList<String> allPosUnigramTags = new ArrayList<String>();
	private static final int TOTAL_USERS = 838; //session1=838
	private static int currentUser = 0;
	private String filename = "S1BigramPCFG.data";
	
	@Override
	public Collection<Feature> extract(DataNode data) {
		currentUser++;
		for (Answer a: data) {
			
			try {
				//get answer string
				String charStream = a.getCharStream();	
				//tokenize string
				String[] tokens = tokenize.runTokenizer(charStream);
				//extract pos tags
				String[] posTokens = posExtractor.createPOSTags(tokens);
				//add tags to static list
				allPosUnigramTags.add("START");
				allPosUnigramTags.addAll(Arrays.asList(posTokens));
				allPosUnigramTags.add("STOP");
			} catch (IOException e) {e.printStackTrace();}
		}
		
		if (currentUser == TOTAL_USERS) {
			//generate bigram model
			BigramModel bigramModel = new BigramModel(allPosUnigramTags);
			//write to file
			try {
				bigramModel.exportToFile(filename);
				File f = new File(filename);
				if (f.exists())
					System.out.println(f.toString()+" created...");
			} catch (ModelGeneratorException e) {e.printStackTrace();}
			
		}
		return null;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

}
