package word_variation;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;

import extractors.data.Answer;
import extractors.data.DataNode;
import extractors.data.ExtractionModule;
import extractors.data.Feature;
import extractors.lexical.TokenExtender;
import keytouch.KeyTouch;
import mwe.TokenExtended;

public class WordVariationCSV implements ExtractionModule {

	private static TokenExtender extender = new TokenExtender();
	static final String csv_file = "someone_training.csv";
	static final String SEP = ",";
	static boolean HEADERS = false;

	static final ArrayList<Integer> touchTypists = new ArrayList<Integer>(Arrays.asList(4,10,14,15,21,25,27,31,33,34,36,39,40,46,53,55,60,62,66,69,74,76,77,80,90,93,96,98,100,101,105,107,108,119,121,131,133,134,135,137,139,140,142,143,151,153,157,167,172,173,174,178,179,180,182,185,189,190,199,203,206,207,209,211,213,215,219,226,229,233,236,237,240,242,244,248,260,268,269,270,273,282,284,285,289,290,293,297,305,308,309,310,314,315,319,320,326,332,334,335,337,342,345,348,351,352,355,356,357,358,360,362,365,368,374,375,376,377,381,383,385,389,400,401,402,405,406,411,415,417,419,422,423,425,426,430,431,432,434,439,442,447,449,450,451,454,455,458,462,465,467,468,470,471,476,478,479,481,482,486,493,497,498,501,505,511,513,514,520,524,525,529,532,536,538,540,549,551,552,553,559,560,562,563,566,572,573,576,578,580,581,582,588,590,595,597,603,604,606,611,612,613,616,630,631,632,642,643,645,646,656,663,665,674,680,683,691,692,693,694,695,701,705,709,716,717,720,722,726,729,730,735,736,737,740,741,744,747,754,761,763,764,766,768,771,776,778,784,792,793,795,800,810,814,817,818,822,823,824,830,835,837,839,840,843,844,847,852,854,864,867,868,872,876,879,885,888,893,899,902,907,916,917,918,919,923,927,928,929,931,932,933,934,936,939,941,943,945,948,950,954,955,956,961,963,964,965,966,969,974,981,987,989,995,996,997,998,999,1001,1002,1003,1006,1008));
	static final ArrayList<String> complexWords = new ArrayList<String>(Arrays.asList("a","am","an","as","at","be","by","if","of","oh","so","to","up","age","aid","all","and","ape","apt","are","bed","bit","but","can","dip","eat","eel","eye","for","fur","gin","hay","hen","her","hog","hot","how","its","led","leg","lip","nor","not","now","off","one","peg","rum","sin","too","two","was","who","ache","ally","also","arch","back","bang","barn","beam","beef","been","beer","beet","bell","belt","bite","boar","boat","bolt","book","both","bowl","bump","came","care","cast","coat","code","coil","come","cool","cowl","crab","dear","diet","drab","duel","dumb","earn","fang","fast","fine","flee","form","from","full","gasp","goof","gray","hail","half","hall","hard","head","heir","help","hero","hose","idle","into","iron","keel","keen","kill","kink","kiss","knee","know","lady","lake","lamp","lean","leap","liar","live","loop","loot","luck","lure","make","many","milk","mine","mood","most","much","need","nine","only","page","part","path","pear","pest","pity","poet","pore","prop","raid","rash","ripe","risk","rule","rung","sale","save","sick","side","snow","soon","sore","suds","tank","team","than","thaw","then","thus","turn","ugly","used","vein","very","view","wail","walk","wash","weed","well","welt","were","what","when","wild","wise","wrap","wren","about","after","array","audit","being","belly","birch","bland","bloom","board","brake","brawl","broom","chief","child","cider","claim","clang","court","crook","curse","devil","dozen","drain","drill","earth","eerie","faint","fever","first","flash","frail","fudge","gavel","guilt","harsh","hasty","hobby","horse","house","known","least","light","match","meant","might","moral","mouse","mouth","mural","nerve","nurse","place","robin","rough","round","scale","scent","shawl","sheer","shell","shiny","shout","siege","since","skunk","slime","slope","slush","smart","sound","spray","stare","state","steak","store","stump","style","swarm","swell","table","tally","their","there","these","think","those","throw","trace","trunk","upset","value","venom","waltz","waste","which","while","whole","wrath","yacht","aspect","banner","battle","beauty","belief","bother","bright","bucket","canary","carrot","cattle","cavern","cellar","cement","chance","cheese","clover","cookie","cotton","course","dagger","deface","dinner","effort","entree","faster","finish","gender","ginger","hamlet","humble","jacket","jingle","kernel","letter","lotion","madman","mallet","manure","motive","murder","nation","normal","ornate","oxygen","parcel","phrase","pigeon","pliers","poison","public","punish","python","racket","rattle","rocket","rubber","safety","savant","soccer","social","spirit","squeak","stifle","stride","stupid","sucker","suffix","summer","sunset","thwart","tomato","tunnel","unjust","weight","bandage","because","beehive","biology","chuckle","citizen","comfort","commode","concept","concert","dreamer","embrace","eternal","evening","express","extreme","fantasy","fashion","glitter","grammar","hostage","however","literal","nowhere","oatmeal","oblique","paradox","placard","quickly","racquet","reality","sampler","scooter","selling","sulphur","tweezer","unknown","vinegar","walking","wedding","welfare","alphabet","although","approach","capacity","chlorine","creature","cucumber","derelict","distinct","educator","herdsman","industry","material","mischief","optimism","portrait","prisoner","sapphire","stranger","sunshine","surprise","transfer","unbelief","attendant","authentic","beginning","causality","comforter","composure","criterion","dictation","diversity","elopement","encounter","expulsion","following","foreigner","greenness","limelight","lubricant","magnitude","obedience","obsession","pineapple","prevalent","reckoning","reluctant","sanctuary","submarine","surrender","tangerine","telescope","therefore","traveller","voluntary","appearance","centennial","collection","convention","distortion","enthusiasm","generation","gymnastics","importance","impossible","lieutenant","literature","mediocrity","permission","preference","procession","projectile","responsive","suggestion","tablespoon","upholstery","affirmation","arrangement","cauliflower","destruction","disobedient","grasshopper","immortality","incongruity","indifferent","observation","requirement","supposition","theoretical","uncertainty","unhappiness","enlightenment","fortification","investigation"));

	@Override
	public Collection<Feature> extract(DataNode data) {
		if (touchTypists.contains(data.getUserID())) {
			BufferedWriter outf = null;
			try {
				outf = new BufferedWriter(new FileWriter(csv_file, true));

				//			ArrayList<Double> userPauses = new ArrayList<Double>();

				for (Answer a : data) {
					int userID = data.getUserID();
					int aID = a.getAnswerID();

					ArrayList<Double> answerPauses = new ArrayList<Double>();

					LinkedList<KeyTouch> ktList = KeyTouch.parseSessionToKeyTouches(a.getKeyStrokes());
					ArrayList<TokenExtended> tokens = extender.generateExtendedTokens(ktList);
					
					String targetToken = "someone";
					
					// create headers
					if (!HEADERS) {
						String[] headers = {"UserID",
											"Token",
											"PreWord_Pause",
											"SentIdx",
											"SentLen",
											"WordIdx"};
						
						for (String colName : headers) {
							outf.write(colName);
							outf.write(SEP);
						}
						for (int i=0; i<targetToken.length(); i++) {
							outf.write(String.format("%d_pause", i));
							outf.write(SEP);
							outf.write(String.format("%d_hold", i));
							if (i != targetToken.length()-1)
								outf.write(SEP);
						}
						outf.write("\n");
						HEADERS = true;
					}
					
					for (TokenExtended t : tokens) {
//						if (complexWords.contains(t.token)) { 
						if (t.token.toLowerCase().equals(targetToken) && 
								t.keyTouchList.size()==targetToken.length()) {
							double precedingPause = t.keyTouchList.get(0).getPrecedingPause();
							
							outf.write(Integer.toString(userID));
							outf.write(SEP);
							outf.write(t.token);
							outf.write(SEP);
							outf.write(Double.toString(precedingPause));
							outf.write(SEP);
							outf.write(Integer.toString(t.indexInfo.sentenceIndex));
							outf.write(SEP);
							outf.write(Integer.toString(t.indexInfo.sentenceLength));
							outf.write(SEP);
							outf.write(Integer.toString(t.indexInfo.wordIndex));
							outf.write(SEP);
							
							for (int i=0; i <t.keyTouchList.size(); i++) {
								double pause = t.keyTouchList.get(i).getPrecedingPause();
								double hold = t.keyTouchList.get(i).getHoldTime();
								outf.write(Double.toString(pause));
								outf.write(SEP);
								outf.write(Double.toString(hold));
								if (i != t.keyTouchList.size()-1)
									outf.write(SEP);
							}
							
							outf.write("\n");
						}
					}
					//				double answerMeanPause = mean(answerPauses);
					//				double answerSdPause = stdDev(answerPauses);
					//				
					//				outf.write(Integer.toString(userID));
					//				outf.write(SEP);
					//				outf.write(Integer.toString(aID));
					//				outf.write(SEP);
					//				outf.write(Double.toString(answerMeanPause));
					//				outf.write(SEP);
					//				outf.write(Double.toString(answerSdPause));
					//				outf.write("\n");

				}

				//			double userMeanPause = mean(userPauses);
				//			double userSdPause = stdDev(userPauses);
				//			
				//			outf.write(Integer.toString(data.getUserID()));
				//			outf.write(SEP);
				//			outf.write(Double.toString(userMeanPause));
				//			outf.write(SEP);
				//			outf.write(Double.toString(userSdPause));
				//			outf.write("\n");

			}  	 catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (outf != null) {
					try {
						outf.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return null;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "Word Variation";
	}

	private static double mean(ArrayList<Double> values) {
		double sum = 0.0;
		for (Double d : values) {
			if (d instanceof Double)
				sum += d;
		}
		return sum/values.size();
	}

	private static double stdDev(ArrayList<Double> values) {
		double mean = mean(values);
		double sum = 0.0;
		int count = 0;
		for (Double d : values) {
			if (d instanceof Double && d > 0) {
				sum += ((d-mean) * (d-mean));
				count++;
			}
		}
		return Math.sqrt(sum/count);
	}
}
