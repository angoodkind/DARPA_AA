package output.util;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Reads a template directory that is created by the feature extractor and 
 * provides availability of features in a .csv format.
 * 
 * @author Patrick
 *
 */
public class AvailabilityAnalyzer implements Runnable {

	private File dir;
	
	/**
	 * Creates a new availability analyzer that runs in it's own thread.
	 * 
	 * @param directory
	 */
	public AvailabilityAnalyzer(String directory) {
		dir = new File(directory);
		//Thread t = new Thread(this);
        //t.start();
	}
	
	/**
	 * Runs Analysis and outputs results to csv.
	 */
	@Override
	public void run() {
		System.out.println("Computing Feature Availability...");
		Scanner scanner = null;
		String lineString;
		String[] splitLine;
		String[] splitValues;
		LinkedList<HashMap<String, Integer>> userList = new LinkedList<HashMap<String, Integer>>();   
		for (File child : dir.listFiles()) {
			HashMap<String, Integer> availabilityMap = new HashMap<String, Integer>();
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
		    	availabilityMap.put("Subj_id", Integer.parseInt(nameSplit[0].substring(12)));
		    	while (scanner.hasNextLine()) {
		    		lineString = scanner.nextLine();
		    		if (!lineString.equals("")) {
			    		splitLine = lineString.split(":");
			    		splitValues = splitLine[1].trim().split(",");
			    		if (!splitValues[0].trim().equals("")) {
			    			//System.out.println(splitValues[0]);
			    			
			    			int value_counts = CountNonZero(splitValues);
			    			if (value_counts > 0) {
			    				//System.out.println("Features: " + splitLine[0]);
					    		if (availabilityMap.keySet().contains(splitLine[0]))
					    			availabilityMap.put(splitLine[0], availabilityMap.get(splitLine[0]) + value_counts);
					    		else
					    			availabilityMap.put(splitLine[0], value_counts);
			    			}
			    		}
		    		}
		    	}
		    	userList.add(availabilityMap);
		    }
		}
		PrintWriter pw = null;
		try {
			pw = new PrintWriter(dir.getPath() + File.separatorChar + "Availability.csv");
		
			pw.print("Subj_id" + ",");
			TreeSet<String> keySet = new TreeSet<String>();
			for (HashMap<String, Integer> map : userList) {
				keySet.addAll(map.keySet());
			}
			keySet.remove("Subj_id");
			for (String s : keySet)
				pw.print(s + ",");
			pw.println();
			for (HashMap<String, Integer> map : userList) {
				pw.print(map.get("Subj_id") + ",");
				for (String s : keySet)
					if (map.keySet().contains(s))
						pw.print(map.get(s) + ",");
					else
						pw.print(0 + ",");
				pw.println();
			
			}
			pw.close();
			doCalculations(dir.getPath()  + File.separatorChar + "Availability.csv");
		} 
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	private int CountNonZero(String[] values) {
		//System.out.println(matcher.group(1));
		String p = "^[0]*\\.?[0]*";				
		int counter = 0;
		for (int v = 0; v < values.length; v++) {
			//System.out.println(values[v]);
			if (!values[v].trim().matches(p)) counter++;
		}
		return counter;
	}
	
	/**
	 * Opens the Availability csv file, performs necessary computations and appends
	 * the results.
	 * 
	 * @param csvFile
	 * @throws FileNotFoundException
	 */
	private void doCalculations(String csvFile) throws FileNotFoundException {
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
		for (int column = 1; column < rows.get(0).length; column++ )  {
			double[] population = new double[rows.size() - 1];
			for (int row = 1; row < rows.size(); row++) {
				double cellValue = Float.parseFloat(rows.get(row)[column]);
				total[column-1] += cellValue;
				population[row-1] = cellValue;
			}
			average[column-1] = total[column-1] / (rows.size() - 1);
			
			for (int i = 0; i < population.length; i++) {
				population[i] = population[i] - average[column-1];
				population[i] = population[i] * population[i];
				sd[column - 1] += population[i];
			}
			
			sd[column - 1] = Math.sqrt((sd[column - 1] / (rows.size() - 2)));
		}
		PrintWriter pw = new PrintWriter(new FileOutputStream(csvFile, true));
		pw.println();
		printCalculations(pw, "Total Features" , total);
		printCalculations(pw, "Average Per Subject" , average);
		printCalculations(pw, "Standard Deviation" , sd);
		pw.close();	
	}
	
	/**
	 * Prints calculated results in csv format. 
	 * 
	 * @param pw
	 * @param name
	 * @param dArray
	 */
	private void printCalculations (PrintWriter pw, String name, double[] dArray) {
		pw.print(name);
		for (Double d : dArray)
			pw.print("," + d);
		pw.println();
	}
	
}
