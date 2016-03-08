package features.lexical;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import events.EventList;
import extractors.data.Answer;
import extractors.data.DataNode;
import extractors.data.ExtractionModule;
import extractors.data.Feature;
import extractors.lexical.POS_Extractor;
import keystroke.KeyStroke;

/**
 * @author dbrizan
 *	Measures occurrences of function words
 *	extends POS_Tagger, which compiles POS tags
 */
public class FunctionWordMetrics extends POS_Extractor implements ExtractionModule {

	private static POS_Extractor pos = new POS_Extractor();
	// Name of the file to read
	protected final static String fileName = "function_words_keys.txt";
	// The name of all non-function words:
	protected final static String CONTENT = "CONTENT";
	// Places to store reporting data
	protected static LinkedList<String> metricKeys;  // The function words to keep track of
	protected static LinkedList<String> classKeys;  // The non-content classes
	protected static HashMap<String, Double> allMetrics;

	LinkedList<Feature> output;


	public FunctionWordMetrics() {
		metricKeys = new LinkedList<String>();  
					// It seems to be silly to create this when we can get it
					// from the allMetrics.keys(), but this is in case something has
					// a count of zero.
		classKeys = new LinkedList<String>(); // Classes to keep track of (if not individual functionWords)
		allMetrics = new HashMap <String, Double>();

		// First: decide on which items we're performing metrics on
		readMetricKeys();
		addClosedClasses();

		// Reset the counts, etc.
		resetCounts();
	}


	public boolean resetCounts() {
		// Add counts and rates for all words and (closed) classes:
		for (String key : metricKeys) {
			allMetrics.put(key.toLowerCase() + "_COUNT", new Double(0));
			allMetrics.put(key.toLowerCase() + "_RATE", new Double(0));
		}
		for (String key : classKeys) {
			allMetrics.put(key + "_COUNT", new Double(0));
			allMetrics.put(key + "_RATE", new Double(0));
		}
		// Add one more for content words:
		allMetrics.put("CONTENT_COUNT", new Double(0));
		allMetrics.put("CONTENT_RATE", new Double(0));

		output = new LinkedList<Feature>();

		return true;
	}


	/*
	 * readMetricKeys
	 * Reads a file of function words named "function_word_keys.txt" from the root directory
	 * ... which must be in line-separated-value format.
	 * @return true if (partially) successful; false if not.
	 */
	public boolean readMetricKeys () {
		BufferedReader reader;
		try {
                        reader = new BufferedReader(new FileReader(fileName));
                        String line = null;
                        while ((line = reader.readLine()) != null)
                                if (! metricKeys.add(line.toLowerCase())) {
                                        reader.close();
                                        throw new Exception("Failed to add word " + line);
                                }
                        reader.close();
                } catch (Exception e) {
                        // e.printStackTrace();
			return loadPennebakerKeys();
                        // return false;
                }
                return true;
	}


	/*
	 * loadPennebakerKeys
	 * This is the list of the top-20 function words according to Pennebaker's
	 * The psychological function of function words, which is available here:
	 * http://homepage.psy.utexas.edu/HomePage/Faculty/Pennebaker/Reprints/index.htm
	 * @return true if wholly successful; false otherwise.
	 */
	public boolean loadPennebakerKeys () {
		try {
			// Works better if they're all lowercase. Long story.
			metricKeys.add("i");
			metricKeys.add("the");
			metricKeys.add("and");
			metricKeys.add("to");
			metricKeys.add("a");
			metricKeys.add("of");
			metricKeys.add("that");
			metricKeys.add("in");
			metricKeys.add("it");
			metricKeys.add("my");
			metricKeys.add("is");
			metricKeys.add("you");
			metricKeys.add("was");
			metricKeys.add("for");
			metricKeys.add("have");
			metricKeys.add("with");
			metricKeys.add("he");
			metricKeys.add("me");
			metricKeys.add("on");
			metricKeys.add("but");
			return true;
		} catch (Exception e) {
			return false;
		}
	}


	public boolean addClosedClasses () {
		// These are the classes: firstly, anything not a function word.
		// This is based on POS_Extractor, itself based on Penn-Treebank, perhaps.
		// To be clear, we want to add JUST the function classes, not nouns, etc.
		// Many of these could end up having null counts if all the words in the class
		// exist in metricKeys.
		classKeys.add("CC");  // Coordinating conjunction
		// classKeys.add("CD");  // Cardinal number (one, two, etc.)
		classKeys.add("DT");  // Determiner (a, the, this, that)
		classKeys.add("EX");  // Existential "there"
		classKeys.add("IN");  // preposition or subordinate conjunction
		classKeys.add("MD");  // Modal
		// classKeys.add("PDT");  // Predeterminer (?)
		classKeys.add("POS");  // Possessive
		classKeys.add("PRP");  // Personal pronoun
		classKeys.add("PRP$");
		classKeys.add("RP");  // Particle
		classKeys.add("SYM");  // Symbol
		classKeys.add("TO");  // "to" -- which should almost definitely be in metricKeys
		classKeys.add("UH");  // Interjection
		classKeys.add("WDT");  // Wh-determiner
		classKeys.add("WP");  // Wh-pronoun
		classKeys.add("WP$");  // Possessive wh-pronoun
		classKeys.add("WRB");  // Wh-adverb
		// Anything else will just be shoved into "CONTENT", but leave that off for now.
		return true;
	}
	
	
	@Override
	public Collection<Feature> extract(DataNode data) {
		double wordCount = 0;

		resetCounts();  // For obvious reasons...?

		for (Answer a : data) {
			try {
				EventList<KeyStroke> k = a.getKeyStrokeList();
				String f = KeyStroke.keyStrokesToFinalText(k);
				String[] wordTokens = runTokenizer(f);
				pos.setPosTags(wordTokens);
				String[] posTokens = pos.getPosTags();

				for (int i = 0; i < wordTokens.length; i++) {
					++wordCount;
					String word = wordTokens[i].toLowerCase();
					String tag  = posTokens[i];

					// If the word is one of the function words we care about, update its count
					// if ((metricKeys.contains(word + "_COUNT"))) {
					if ((metricKeys.contains(word))) {
						if (allMetrics.get(word + "_COUNT") == null) {
							allMetrics.put(word + "_COUNT", new Double(1));
						}
						else {
							double d = (allMetrics.get(word + "_COUNT")).doubleValue();
							allMetrics.put(word + "_COUNT", d+1);
						}
					}
					else if (classKeys.contains(tag)) {
						// Maybe the word's tag indicates it's a function word?
						if (allMetrics.get(tag + "_COUNT") == null) {
							allMetrics.put(tag + "_COUNT", new Double(1));
						}
						else {
							double d = (allMetrics.get(tag + "_COUNT")).doubleValue();
							allMetrics.put(tag + "_COUNT", d+1);
						}
					}
					else {
						// It's just content. Update that count
						if (allMetrics.get(CONTENT + "_COUNT") == null) {
							allMetrics.put(CONTENT + "_COUNT", new Double(1));
						}
						else {
							double d = (allMetrics.get(CONTENT + "_COUNT")).doubleValue();
							allMetrics.put(CONTENT + "_COUNT", d+1);
						}
					}
				}
			} catch (IOException e) {e.printStackTrace();}
			
		}

		// Done with counts; generate rates:
		if (wordCount > 0) {
			double count = 0.0;
			for (String key : metricKeys) {
				count = allMetrics.get(key + "_COUNT").doubleValue();
				allMetrics.put(key + "_RATE", new Double (count/wordCount));
			}
			for (String key : classKeys) {
				count = allMetrics.get(key + "_COUNT").doubleValue();
				allMetrics.put(key + "_RATE", new Double (count/wordCount));
			}
			count = allMetrics.get(CONTENT+ "_COUNT").doubleValue();
			allMetrics.put(CONTENT+ "_RATE", new Double (count/wordCount));
		}

		for (String category : allMetrics.keySet()) {
			output.add(new Feature(category, allMetrics.get(category)));
		}
		
//	    for (Feature f : output) System.out.println(f.toTemplate());
		
		return output;
	}

	
	@Override
	public String getName() {
		return "Function Word Metrics";
	}
	
	public static ArrayList<String> PennebakerWords() {
		ArrayList<String> pennebakerWords = new ArrayList<String>();
		pennebakerWords.add("i");
		pennebakerWords.add("the");
		pennebakerWords.add("and");
		pennebakerWords.add("to");
		pennebakerWords.add("a");
		pennebakerWords.add("of");
		pennebakerWords.add("that");
		pennebakerWords.add("in");
		pennebakerWords.add("it");
		pennebakerWords.add("my");
		pennebakerWords.add("is");
		pennebakerWords.add("you");
		pennebakerWords.add("was");
		pennebakerWords.add("for");
		pennebakerWords.add("have");
		pennebakerWords.add("with");
		pennebakerWords.add("he");
		pennebakerWords.add("me");
		pennebakerWords.add("on");
		pennebakerWords.add("but");
		return pennebakerWords;
	}

}
