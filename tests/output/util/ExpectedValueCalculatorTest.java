package output.util;

import static junit.framework.Assert.assertEquals;

import org.junit.Test;

import output.util.ExpectedValueCalculator;

/**
 * Test class for ExpectedValueCalculator
 */
public class ExpectedValueCalculatorTest {

  @Test
  public void testProcessDataUpdatesMean() {
    ExpectedValueCalculator calculator = new ExpectedValueCalculator();
    calculator.setAttrNames(new String[]{"featureA", "featureB"});

    calculator.processData(new String[]{"5,6,7,8,9", "10,10,10"});

    assertEquals(7., calculator.getMean("featureA"), 0.0001);
    assertEquals(10., calculator.getMean("featureB"), 0.0001);
  }

  @Test
  public void testProcessDataUpdatesStdDev() {
    ExpectedValueCalculator calculator = new ExpectedValueCalculator();
    calculator.setAttrNames(new String[]{"featureA", "featureB"});

    calculator.processData(new String[]{"5,6,7,8,9", "10,10,10"});

    assertEquals(1.58114, calculator.getStdev("featureA"), 0.0001);
    assertEquals(0., calculator.getStdev("featureB"), 0.0001);
  }
}
