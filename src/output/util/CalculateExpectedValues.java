package output.util;

import java.io.IOException;

/**
 * Write expected value information based on extracted features.
 */
public class CalculateExpectedValues {

  public static void main(String[] args) {
    if (args.length < 3) {
      System.out.println(
          "\tUsage: java CalculateExpectedValues  -[f|d] path output_file\n\n -f -> load a single file, " +
              "-d load a full directory");
      return;
    }

    ExpectedValueCalculator calculator = new ExpectedValueCalculator();
    try {
      if (args[0].equals("-f")) {
        calculator.loadFile(args[1]);
      } else {
        calculator.loadAllFilesInDirectory(args[1]);
      }
      calculator.writeExpectedValues(args[2]);

    } catch (IOException | TestVectorException e) {
      e.printStackTrace();
    }
  }
}
