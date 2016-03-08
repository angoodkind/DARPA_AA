package output.util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

import events.EventList;
import extractors.data.Answer;
import extractors.data.DataNode;
import extractors.data.ExtractionModule;
import extractors.data.Feature;
import extractors.lexical.TokenExtender;
import features.pause.KSE;
import keystroke.KeyStroke;
import mwe.TokenExtended;

public class MeasureWordTiming implements ExtractionModule {
	
	private static TokenExtender extender = new TokenExtender();
	private static PrintWriter writer;
	private static String outFileName = "word-timingTr.data";
	
	public MeasureWordTiming() throws IOException {
		writer = new PrintWriter(new BufferedWriter(new FileWriter(outFileName, true)));
	}

	@Override
	public Collection<Feature> extract(DataNode data) {
		for (Answer a : data) {
			ArrayList<KSE> kseList = new ArrayList<KSE>(KSE.parseSessionToKSE(a.getKeyStrokes()));
			ArrayList<KSE> visibleKSEs = KSE.parseToVisibleTextKSEs(kseList);
			
			EventList<KeyStroke> keyStrokeList = a.getKeyStrokeList();
			String visibleText = keyStrokeList.toVisibleTextString();
			ArrayList<TokenExtended> tokens = extender.generateExtendedTokens(visibleText);
			
			for (TokenExtended t : tokens) {
				if (t.tokenSpan.begin > 2 && t.tokenSpan.end < visibleText.length()-1 && t.size() == 4) {
					for (int i=-1;i <5;i++) {
						writer.println(i+","+
								KeyStroke.vkCodetoString(visibleKSEs.get(t.tokenSpan.begin+i).getKeyCode())+
								","+visibleKSEs.get(t.tokenSpan.begin+i).getM_pauseMs());
					}
					
				}
			}
			
		}
		return null;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

}
