package features.revision;

import java.io.*;
import java.util.*;

import events.EventList;
import extractors.data.Answer;
import extractors.data.DataNode;
import extractors.data.ExtractionModule;
import extractors.data.Feature;
import extractors.data.TestVectorShutdownModule;
import extractors.lexical.VisualCharStream;
import keystroke.KeyStroke;

/**
 * A utility class to visualize the revision process
 * 
 * The class does not print any features to output, but rather 
 * produces html files that allow for printing strikethroughs
 * 
 * Call through TestVector, not Template, creation
 * 
 * Use parameters for 1 Answer per slice, i.e.:
 * 		- Slice Size is very large
 * 		- Allow Partials
 * 
 * @author Adam Goodkind
 *
 */
public class ReVisualize implements ExtractionModule, TestVectorShutdownModule {
	
	private static final String htmlFile = "reviz.html";
	private static PrintWriter out;

	public ReVisualize() {
		try {
			out = new PrintWriter(new BufferedWriter(new FileWriter(htmlFile, true)));
			out.write("<html>");
			out.write("\n");
		} catch (IOException e) {e.printStackTrace();}
	}
	@Override
	public Collection<Feature> extract(DataNode data) {
		for (Answer a : data ) {
			EventList<KeyStroke> allKS = a.getKeyStrokeList();
			EventList<KeyStroke> ksList = new EventList<KeyStroke>();
			for (KeyStroke k : allKS) {
				if (k.isKeyPress())
					ksList.add(k);
			}
			ArrayList<String> strList = new ArrayList<String>();
			strList.add("UserID: "+data.getUserID()+" ");
			strList.add("QuestionID: "+a.getQuestionID()+"<br>");
			int backspaceCount = 0;
			
			for (int i = 0; i < ksList.size(); i++) {
//				System.out.println(ksList.get(i)+" "+strList.size()+" "+backspaceCount);
				if (!ksList.get(i).isBackspace()) {
					if (backspaceCount > 0 && strList.size() > 0 ) { //from backspace to alpha
						if (strList.size() - backspaceCount > 0)
							strList.add(strList.size()-backspaceCount,"<strike>");
						else //if backspacing past end of list
							strList.add(0,"<strike>");
						strList.add("</strike>");
						backspaceCount = 0; //reset
					}
					strList.add(VisualCharStream.ppVkCode(ksList.get(i).getKeyCode()));
				} else { //is backspace
					backspaceCount++;
				}
			}
			for (String s : strList)
				out.write(s);
			out.write("\n<br><br>");
		}
		
		return null;
	}

	@Override
	public void shutdown() {
		out.write("</html>");
		out.flush();
	}
	
	@Override
	public String getName() { return "ReVisualize";	}
	

}
