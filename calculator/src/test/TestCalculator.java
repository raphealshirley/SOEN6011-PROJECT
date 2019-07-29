package test;

import calculator.CalculatorController;
import calculator.CalculatorModel;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileReader;

import static org.junit.Assert.assertEquals;

public class TestCalculator {
  private CalculatorController calculatorController;
  private CalculatorModel calculatorModel;

  /**
   * TQR2(set up execute environment).
   */
  @Before
  public void setup() {
    calculatorController = new CalculatorController();
    calculatorModel = new CalculatorModel();
  }

  /**
   * TFR1(Execute Application).
   * TFR2(Input a number).
   */
  @Test
  public void testCalculateInput() {
    calculatorController.calculate(0);
    double input = calculatorModel.getInput();
    assertEquals(0,input,0);
  }

  /**
   * TFR3(Obtained an output).
   */
  @Test
  public void testCalculateOutput() {
    double result = calculatorController.calculate(0);
    assertEquals(0,result,0);
  }

  /**
   * TFR4(Input is validated).
   */
  @Test
  public void testCalculateInputFormat() {
    double result = calculatorController.calculate(900);
    assertEquals(Double.POSITIVE_INFINITY,result,0);
  }

  /**
   * TQR1(Accuracy), TQR2(Testability).
   */
  @Test
  public void testAccuracy() {
    double result = calculatorController.calculate(1);
    assertEquals(1.1752011936438,result,0.11752);
  }


  /**
   * TQR3(System Reliability).
   */
  @Test
  public void testReliability() {
    try {
      FileReader fr = new FileReader("./data.txt");
      FileReader frr = new FileReader("./result.txt");
      BufferedReader reader = new BufferedReader(fr);
      String input = reader.readLine();
      BufferedReader reader2 = new BufferedReader(frr);
      String input2 = reader2.readLine();
      while (input != null && input2 !=null) {
        double x = Double.valueOf(input);
        double result = calculatorController.calculate(x);
        double exresult = Double.valueOf(input2);
        assertEquals(exresult, result, 0.1);
        input = reader.readLine();
        input2 = reader2.readLine();
      }
      fr.close();
      frr.close();
    } catch (Exception e) {
      System.out.println(e);
    }
  }


}
