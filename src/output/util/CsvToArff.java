package output.util;

import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVLoader;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
 
public class CsvToArff {
  /**
   * takes 2 arguments:
   * - CSV input file
   * - ARFF output file
   * 
   * From /bin
   * java -cp .:lib/weka.jar output.util/CsvToArff mwe_pred_debug.csv mwe_pred_debug.arff
   */
  public static void main(String[] args) throws Exception {
    if (args.length != 2) {
      System.out.println("\nUsage: CSV2Arff <input.csv> <output.arff>\n");
      System.exit(1);
    }
 
    // load CSV
    CSVLoader loader = new CSVLoader();
//    loader.setNominalAttributes("2,5,8,10"); //with time
    loader.setNominalAttributes("first,last"); //without time
    loader.setSource(new File(args[0]));
    Instances data = loader.getDataSet();
 
    // save ARFF
    ArffSaver saver = new ArffSaver();
    saver.setInstances(data);
    saver.setFile(new File(args[1]));
//    saver.setDestination(new File(args[1]));
    saver.writeBatch();
  }
}