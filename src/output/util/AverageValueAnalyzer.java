package output.util;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.TreeSet;

/**
 * Reads a template directory that is created by the feature extractor and 
 * provides availability of features in a .csv format.
 * 
 * @author Patrick
 *
 */
public class AverageValueAnalyzer implements Runnable {

	private File dir;
	
	public static void main (String[] args) {
		new AverageValueAnalyzer("Templates2012-10-18[17-26-5]-0400").run();	
	}
	/**
	 * Creates a new availability analyzer that runs in it's own thread.
	 * 
	 * @param directory
	 */
	public AverageValueAnalyzer(String directory) {
		dir = new File(directory);
		//Thread t = new Thread(this);
        //t.start();
	}
	
	@Override
	public void run() {
		System.out.println("Computing Feature Averages...");
		Scanner scanner = null;
		String lineString;
		String[] splitLine;
		String[] splitValues;
		LinkedList<HashMap<String, Double>> userList = new LinkedList<HashMap<String, Double>>();   
		for (File child : dir.listFiles()) {
			System.out.println(child.getName());
			HashMap<String, Double> availabilityMap = new HashMap<String, Double>(3000);
			//Ignore self and parent.
			if (".".equals(child.getName()) || "..".equals(child.getName()))
				continue;
		    String[] nameSplit = child.getPath().substring(child.getPath().lastIndexOf(File.separatorChar, child.getPath().length() - 1) + 1).split("\\.");
		    String fileExtension = nameSplit[nameSplit.length - 1];
		    if ("txt".equals(fileExtension)) {

		    	try {
					scanner = new Scanner(child);
				} catch (FileNotFoundException e) {
					System.err.println("I/O Error.");
					break;
				}
		    	availabilityMap.put("Subj_id", Double.parseDouble(nameSplit[0].substring(12)));
		    	while (scanner.hasNextLine()) {
		    		lineString = scanner.nextLine();
		    		if (!lineString.equals("")) {
			    		splitLine = lineString.split(":");
			    		splitValues = splitLine[1].trim().split(",");
			    		//if (availabilityMap.keySet().contains(splitLine[0]))
			    		//	availabilityMap.put(splitLine[0], (availabilityMap.get(splitLine[0]) + average(splitValues)) / 2);
			    		if (splitValues[0].equals(""))
			    			continue;
			    		availabilityMap.put(splitLine[0], average(splitValues));
		    		}
		    	}
		    	userList.add(availabilityMap);
		    }
		}
		PrintWriter pw = null;
		try {
			pw = new PrintWriter(dir.getPath() + File.separatorChar + "Averages.csv");
		
			pw.print("Subj_id" + ",");
			TreeSet<String> keySet = new TreeSet<String>();
			for (HashMap<String, Double> map : userList) {
				keySet.addAll(map.keySet());
			}
			keySet.remove("Subj_id");
			for (String s : keySet)
				pw.print(s + ",");
			pw.println();
			for (HashMap<String, Double> map : userList) {
				pw.print(map.get("Subj_id") + ",");
				for (String s : keySet)
					if (map.keySet().contains(s))
						pw.print(map.get(s) + ",");
					else
						pw.print(" ,");
				pw.println();
			
			}
			pw.close();
			//doCalculations(dir.getPath()  + File.separatorChar + "Averages.csv");
		} 
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	/*private void doCalculations(String csvFile) throws FileNotFoundException {
		Scanner scanner = new Scanner (new File(csvFile));
		String[] columns;
		ArrayList<String[]> rows = new ArrayList<String[]>();
		while (scanner.hasNextLine()) {
			columns = scanner.nextLine().trim().split(",");
			rows.add(columns);
		}
		scanner.close();
		double[] total = new double[rows.get(0).length - 1];
		double[] average = new double[rows.get(0).length - 1];
		double[] sd = new double[rows.get(0).length - 1];
		double[] population = new double[rows.get(0).length - 1];
		for (int column = 1; column < rows.get(0).length; column++ )  {
			for (int row = 1; row < rows.size(); row++) {
				double cellValue = Float.parseFloat(rows.get(row)[column]);
				total[column-1] += cellValue;
				population[column-1] = cellValue;
			}
			average[column-1] = total[column-1] / (rows.size() - 1);
			
			for (int i = 0; i < population.length; i++) {
				population[i] = population[i] - average[column-1];
				sd[column - 1] += population[i] * population[i];
				population[i] = 0;
			}
			
			sd[column - 1] = Math.sqrt((sd[column - 1] / (rows.size() - 1)));
		}
		PrintWriter pw = new PrintWriter(new FileOutputStream(csvFile, true));
		pw.println();
		printCalculations(pw, "Total Features" , total);
		printCalculations(pw, "Average Per Subject" , average);
		printCalculations(pw, "Standard Deviation" , sd);
		pw.close();	
	}
	
	private void printCalculations (PrintWriter pw, String name, double[] dArray) {
		pw.print(name);
		for (Double d : dArray)
			pw.print("," + d);
		pw.println();
	}*/
	
	private static double average(String[] strArray) {
		double total = 0;
		for (String s : strArray) {
			if (s.isEmpty())
				continue;
			total += Double.parseDouble(s.trim());
		}
		total = total / strArray.length;
		return total;
	}
}
