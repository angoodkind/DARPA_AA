/**
 * CreateSpellCheckMap.java
 * 
 */
package extractors.lexical;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import opennlp.tools.util.InvalidFormatException;


/**
 * @author David Guy Brizan
 * Uses dictionary from Jazzy spell checker to determine correctly spelled and misspelled words.
 */
public class CreateSpellCheckMap extends Tokenize {
	
	// Internal class DictionaryTreeNode will keep data for this class... implementation follows:
	protected class DictionaryTreeNode {
		public static final char TERMINAL = '.';
		protected char letter;
		protected int  count;
		protected List<DictionaryTreeNode> subsequent;
		
		// By default, a node in this trie only contains the TERMINAL symbol, indicating the start / end of a spelling sequence
		public DictionaryTreeNode () {
			this.letter = TERMINAL;
			count = 0;
			// subsequent = new LinkedList<DictionaryTreeNode>();
			subsequent = new ArrayList<DictionaryTreeNode>();
		}
		
		// Non-default node in the trie:
		public DictionaryTreeNode (char letter) {
			this.letter = letter;
			count = 0;
			// subsequent = new LinkedList<DictionaryTreeNode>();
			subsequent = new ArrayList<DictionaryTreeNode>();
		}
		
		// Accessor
		public int getCount () {
			return count;
		}
		
		// Accessor
		public char getLetter () {
			return letter;
		}
		
		// Returns a single node one level lower than "this"; useful for recursion
		protected DictionaryTreeNode getSingleNode (char seeking) {
			for (DictionaryTreeNode node : subsequent) {
				if (node.getLetter() == seeking) 
					return node;
			}

			return null;
		}
		
		// Recursively gets a node in the trie until 
		protected DictionaryTreeNode getNode (String prefix) {
			if (prefix.length() == 0)
				return this;
			
			char seeking = prefix.charAt(0);
			DictionaryTreeNode node = getSingleNode(seeking);
			if (seeking == TERMINAL)
				return node;
			else if (node != null)
				return node.getNode(prefix.substring(1));
			return null;
		}
		
		// Returns "true" if there exists a path in the trie with the spelling (parameter)
		// ending in the TERMINAL -- in other words, a whole word.
		public boolean getWord (String spelling) {
			DictionaryTreeNode node = getNode(spelling);
			if (node != null) {
				if (node.getSingleNode(TERMINAL) != null)
					return true;
				else
					return false;
			}
			return false;
		}
		
		// Adds a path to the trie defined by word (parameter)
		public boolean addWord (String word) {
			if (word.length() == 0) {
				++count;
				return subsequent.add(new DictionaryTreeNode()); // Adds the TERMINAL by default
			}
			
			DictionaryTreeNode subsequentNode = getSingleNode(word.charAt(0));
			
			if (subsequentNode == null) {
				subsequentNode = word.length() == 0 ? new DictionaryTreeNode() : new DictionaryTreeNode(word.charAt(0));
				subsequent.add(subsequentNode);
				if (word.length() == 0)
					return true;
			}
			++count;
			return subsequentNode.addWord(word.substring(1));
		}
		
	}  // end DictionaryTreeNode
	
	
	protected DictionaryTreeNode trie;  // Implementation above
	
	
	// Constructor
	public CreateSpellCheckMap () {	
		trie = new DictionaryTreeNode();
		loadDictionary("sae-sorted.dic");
	}
	
	/**
	 * Changes the argument to lower case and adds it to the dictionary
	 * @param word a string to be added
	 * @return true if word added correctly; false otherwise.
	 */
	protected boolean addWord (String word) {
		try {
			return trie.addWord(word.toLowerCase());
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Reads the argument as the name of the file to be loaded.
	 * File must have one (dictionary) word per line.
	 * @param filename name of file to load.
	 * @return true if loaded successfully; false otherwise.
	 */
	public boolean loadDictionary (String filename) {
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(filename));
			String         line = null;
			while ((line = reader.readLine()) != null) 
				if (! addWord(line)) {
					reader.close();
					throw new Exception("Failed to add word " + line);
				}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	/**
	 * Returns the number of words (including whole words) which begin with the argument prefix, case insensitive.
	 * @param prefix string
	 * @return a non-negative integer.
	 */
	public int getPossibleWords (String prefix) {
		try {
			return trie.getNode(prefix.toLowerCase()).getCount();
		} catch (Exception e) {
			return 0;
		}
	}
	
	/**
	 * Returns true if there is a (whole) word with the argument spelling.
	 * @param word string
	 * @return true if there is a word with the argument spelling; false otherwise.
	 */
	public boolean getWord (String word) {
		return trie.getWord(word.toLowerCase());
	}


	public HashMap<String, Boolean> createUserMap(String[] tokens) {
		HashMap<String,Boolean> termMap = new HashMap<String,Boolean>();
		for (String token : Arrays.asList(tokens)) {
			termMap.put(token, getWord(token));
		}
		return termMap;
	}

	private HashMap<String,Boolean> createUserMap(String str) throws InvalidFormatException, IOException {
		return (createUserMap(runTokenizer(str)));
	}

}

