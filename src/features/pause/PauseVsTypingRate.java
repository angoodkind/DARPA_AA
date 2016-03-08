package features.pause;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import events.EventList;
import extractors.data.Answer;
import extractors.data.DataNode;
import extractors.data.ExtractionModule;
import extractors.data.Feature;
import extractors.lexical.Tokenize;
import keystroke.KeyStroke;
import output.util.AncillaryDataInterface;

public class PauseVsTypingRate extends Tokenize implements ExtractionModule {
	private static final String fileName = "pauseDistribVtypingRate.data";
	AncillaryDataInterface adi;
	private static PrintWriter out;
	PauseBursts pb;
	static HashMap<Long,Integer> slowPauses = new HashMap<Long,Integer>();
	static HashMap<Long,Integer> fastPauses = new HashMap<Long,Integer>();
	static final int userCount = 486;
	static int currentUser = 0;
	
	public PauseVsTypingRate() throws IOException {
		out = new PrintWriter(new BufferedWriter(new FileWriter(fileName, true)));
		try {
			//Always instantiate in default module constructor.
			adi = new AncillaryDataInterface();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	@Override
	public Collection<Feature> extract(DataNode data) {
		for (Answer a : data) {
			currentUser++;
			try {
				EventList<KeyStroke> ksList = a.getKeyStrokeList();
				double rate = getIntraWordTypingRate(ksList);
				ArrayList<KSE> kseList = new ArrayList<KSE>(KSE.parseSessionToKSE(a.getKeyStrokes()));
				
				for (KSE kse : kseList) {
					if (kse.isKeyPress()) {
						if (rate < 168.9) {
							if (slowPauses.containsKey(kse.getM_pauseMs())) {
								slowPauses.put(kse.getM_pauseMs(), slowPauses.get(kse.getM_pauseMs())+1);
							}
							else {
								slowPauses.put(kse.getM_pauseMs(), 1);
							}
						} else {
							if (fastPauses.containsKey(kse.getM_pauseMs())) {
								fastPauses.put(kse.getM_pauseMs(), fastPauses.get(kse.getM_pauseMs())+1);
							}
							else {
								fastPauses.put(kse.getM_pauseMs(), 1);
							}
						}
					}
				}
					
				String[] tokens = runTokenizer(a.getCharStream());
				ArrayList<Integer> startPositions = getStartIndex(a.getCharStream());
				pb = new PauseBursts();
				List<Integer> pauses = pb.generatePauseDownList(a.getKeyStrokes(),2000);
				ArrayList<Integer> wordsBtwPauses = getWordsBtwPause(startPositions,pauses);
				int nonzeroCount = 0;
				int zeroCount = 0;
				double nonzeroTotal = 0.0;
				double zeroTotal = 0.0;
				for (int words : wordsBtwPauses) {
					zeroCount++;
					if (words > 0)
						nonzeroCount++;
					nonzeroTotal += words;
					zeroTotal += words;
				}
				double nonzeroMean = nonzeroTotal/nonzeroCount;
				double zeroMean = zeroTotal/zeroCount;
				
				Object subj_lang = null;
				String language = null;
				Object keyboarding = null;
				try {
					subj_lang = adi.getData(data.getUserID(), AncillaryDataInterface.UD_FIRST_LANGUAGE);
					keyboarding = adi.getData(data.getUserID(), AncillaryDataInterface.UD_HOURS_TYPING_PER_DAY);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (subj_lang.equals("English"))
					language = "English";
				else
					language = "Non-English";
				
				//get mean pause length
				double meanPause = 0;
				for (Integer pause : pauses)
					meanPause += pause;
				meanPause /= pauses.size();
				
				out.println(data.getUserID()+","+language+","+keyboarding+","+a.getCogLoad()+","+rate+","+zeroMean+","+nonzeroMean);
				
			} catch (Exception e) {e.printStackTrace();}
			finally {out.flush();}
		}
//		System.out.println(slowPauses);
//		System.out.println(fastPauses);
//		if (currentUser == userCount) {
//			out.println(slowPauses+","+fastPauses);
//			out.flush();
//		}
		return null;
	}

	public double getIntraWordTypingRate(EventList<KeyStroke> keyStrokeList) {
		double keyStrokeCount = 0;
		for (KeyStroke k : keyStrokeList)
			if (k.isKeyPress() && k.isVisible() && !k.isSpace())
				keyStrokeCount++;
		long startTime = keyStrokeList.get(0).getWhen();
		long endTime = keyStrokeList.get(keyStrokeList.size()-1).getWhen();
		double sessionDuration = (endTime-startTime)/60000.0;
		
		return keyStrokeCount/sessionDuration;
	}
	
	/* counts number of words between pauses
	 * uses start indices of words, checks whether they fall between pauses
	 */
	private ArrayList<Integer> getWordsBtwPause(ArrayList<Integer> starts, List<Integer> pauses) {
		ArrayList<Integer> allWordCounts = new ArrayList<Integer>(); // to hold word count
		
		for (int i = 0; i < pauses.size()-1; i++) {
			int wordCount = 0;
			int startIdx = pauses.get(i);
			int endIdx = pauses.get(i+1);
			for (int wordStartIdx : starts) {
				
				if ((wordStartIdx >= startIdx) && (wordStartIdx < endIdx)) {
//					System.out.println("Start: "+startIdx+" End: "+endIdx);
//					System.out.println("Word Start: "+wordStartIdx);
					wordCount++;
				}
			}
			allWordCounts.add(wordCount);
//			System.out.println("Word Count: "+wordCount);
		}
		return allWordCounts;
	}
	
	
	
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

}
