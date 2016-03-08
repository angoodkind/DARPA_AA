package output.util;

import java.io.IOException;

/**
 * A utility class to convert test vectors
 */
public class ConvertTestVectors {
  public static void main(String[] args) {
    if (args.length < 4) {
      System.out.println(
          "\tUsage: java ConvertTestVectors nominal_indices -[f|d] path arff_file\n\n -f -> load a single file, " +
              "-d load a full directory");
      return;
    }

    VectorToWekaConverter converter = new VectorToWekaConverter();
    converter.setNominalIndices(args[0]);
    try {

      if (args[1].equals("-f")) {
        converter.loadFile(args[2]);
      } else {
//        converter.loadAllFilesInDirectory(args[2]);
    	  converter.vectorDirToArff(args[2], args[3]);
      }
      

    } catch (IOException | TestVectorException e) {
      e.printStackTrace();
    }

  }
}
