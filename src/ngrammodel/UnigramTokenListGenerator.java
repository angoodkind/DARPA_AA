package ngrammodel;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

import events.EventList;
import extractors.data.Answer;
import extractors.data.DataNode;
import extractors.data.ExtractionModule;
import extractors.data.Feature;
import extractors.lexical.Tokenize;
import keystroke.KeyStroke;
import ngrammodel.BigramModel;;

public class UnigramTokenListGenerator implements ExtractionModule {
	
	private static final int TOTAL_USERS = 351; 		//session1 = 838 users, debug = 12; session1 <> session2 = 351
	private static int userCount = 0;
	private static ArrayList<String> unigramKeyStrokeTokens = new ArrayList<String>();
	private static ArrayList<String> unigramWordTokens = new ArrayList<String>();
	
//	private static Date dateBegin = new Date();
//	private static SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd[H-m-s]Z");
//	private static StringBuilder now = new StringBuilder(dateformat.format(dateBegin));
	
	private static String uniDirectory = "LM-Unigram-Token-Files/";
	private static String unigramKeyStrokeFileName = "SESSION1KeyStrokeTokens.data";
	private static File unigramKeyStrokeTokenFile = new File(uniDirectory+unigramKeyStrokeFileName);
	private static String unigramWordFileName = "SESSION1WordTokens.data";
	private static File unigramWordTokenFile = new File(uniDirectory+unigramWordFileName);
	
	private static Tokenize tokenize = new Tokenize();
	
	public UnigramTokenListGenerator() {}
	
	@Override
	public Collection<Feature> extract(DataNode data) {

		for (Answer a : data) {
			addToKeyStrokeUnigramTokens(a.getKeyStrokeList());
			addToWordUnigramTokens(a.getCharStream());
		}
		
		userCount++;
		
		if (userCount == TOTAL_USERS) {
			updateKeyStrokeUnigramTokenFile();
			updateWordUnigramTokenFile();
		}
		return null;
	}
	
	public void addToKeyStrokeUnigramTokens(EventList<KeyStroke> keystrokes) {
		ArrayList<String> tokens = new ArrayList<String>();
		tokens.add(Bigram.START);
		for (KeyStroke ks : keystrokes)
			if (ks.isKeyPress())
				tokens.add(KeyStroke.vkCodetoString(ks.getKeyCode()));
		tokens.add(Bigram.STOP);
		unigramKeyStrokeTokens.addAll(tokens);
	}
	
	public void addToWordUnigramTokens(String charStream) {
		ArrayList<String> tokens = new ArrayList<String>();
		try {
			tokens.add(Bigram.START);
			tokens.addAll(Arrays.asList(tokenize.runTokenizer(charStream)));
			tokens.add(Bigram.STOP);
		} catch (IOException e ) {e.printStackTrace();}
		unigramWordTokens.addAll(tokens);
	}
	
	public void updateKeyStrokeUnigramTokenFile() {
		ArrayList<String> unigrams = new ArrayList<String>();
		if (unigramKeyStrokeTokenFile.exists())
			addKeyStrokeUnigramsFromFile(unigrams);
		unigrams.addAll(unigramKeyStrokeTokens);
		writeNewKeyStrokeUnigramTokenFile(unigrams);
	}
	
	public void addKeyStrokeUnigramsFromFile(ArrayList<String> unigramList) {
		ArrayList<String> tempList = new ArrayList<String>();
		try {
			FileInputStream fileInputStream = new FileInputStream(unigramKeyStrokeTokenFile);
			ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
			tempList = (ArrayList<String>) objectInputStream.readObject();
			objectInputStream.close();
		} catch (IOException|ClassNotFoundException e) {e.printStackTrace();}
		unigramList.addAll(tempList);
	}
	
	public void writeNewKeyStrokeUnigramTokenFile(ArrayList<String> unigrams) {
		try {
			FileOutputStream fos = new FileOutputStream(unigramKeyStrokeTokenFile);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(unigrams);
			oos.close();
		} catch (IOException e) {e.printStackTrace();}
	}
	
	public void updateWordUnigramTokenFile() {
		ArrayList<String> unigrams = new ArrayList<String>();
		if (unigramWordTokenFile.exists())
			addWordUnigramsFromFile(unigrams);
		unigrams.addAll(unigramWordTokens);
		writeNewWordUnigramTokenFile(unigrams);
	}
	
	public void addWordUnigramsFromFile(ArrayList<String> unigramList) {
		ArrayList<String> tempList = new ArrayList<String>();
		try {
			FileInputStream fileInputStream = new FileInputStream(unigramWordTokenFile);
			ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
			tempList = (ArrayList<String>) objectInputStream.readObject();
			objectInputStream.close();
		} catch (IOException|ClassNotFoundException e) {e.printStackTrace();}
		unigramList.addAll(tempList);
	}
	
	public void writeNewWordUnigramTokenFile(ArrayList<String> unigrams) {
		try {
			FileOutputStream fos = new FileOutputStream(unigramWordTokenFile);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(unigrams);
			oos.close();
		} catch (IOException e) {e.printStackTrace();}
	}
	
	/**
	 * Prepends unigram folder name
	 * @param filename	File name, no need to include folder name
	 * @return	An ArrayList of unigram tokens
	 */
	public static ArrayList<String> unigramTokenFileToList(String filename) {
		ArrayList<String> tempList = new ArrayList<String>();
		File file = new File(uniDirectory+filename);
		try {
			FileInputStream fileInputStream = new FileInputStream(file);
			ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
			tempList = (ArrayList<String>) objectInputStream.readObject();
			objectInputStream.close();
		} catch (IOException|ClassNotFoundException e) {e.printStackTrace();}
		return tempList;
	}
	
	@Override
	public String getName() {
		return null;
	}


}
