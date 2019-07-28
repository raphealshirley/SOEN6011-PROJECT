package calculator;

import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Random;

/**
 * This class is to generate random 100
 * test number.
 */
public class TestData {
  public static void main(String[] args){
    CalculatorController calculatorController = new CalculatorController();
    Random rand = new Random();

    try {
      FileWriter fw = new FileWriter("../data.txt");
      FileWriter fwresult = new FileWriter("../result.txt");
      int i = 0;
      while (i++ < 100) {
        double x = rand.nextDouble()*1000;
        String result = Double.toString(x);
        fw.write(result+'\n');
      }

      try {
        FileReader fr = new FileReader("../data.txt");
        int j = 0;
        while (j++ < 100) {
          double input = fr.read();
          double result = calculatorController.calculate(input);
          String sresult = Double.toString(result);
          fwresult.write(sresult);
        }
        fr.close();
      } catch (Exception e) {
        System.out.println(e);
      }

      fw.close();
      fwresult.close();
    } catch (Exception e) {
      System.out.println(e);
    }
    


  }
}
