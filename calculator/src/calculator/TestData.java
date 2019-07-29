package calculator;

import java.io.*;
import java.util.Random;
import java.util.Scanner;

/**
 * This class is to generate random 100
 * test number.
 */
public class TestData {
  public static void main(String[] args){
    CalculatorController calculatorController = new CalculatorController();
    Random rand = new Random();

    try {
      FileWriter fw = new FileWriter("./data.txt");
      FileWriter fwresult = new FileWriter("./result.txt");
      int i = 0;
      while (i++ < 100) {
        double x = rand.nextDouble()*1000;
        String result = Double.toString(x);
        fw.write(result + '\n');

      }
      fw.close();

      try {
        File file = new File("./data.txt");
        FileReader fr = new FileReader(file);
        BufferedReader reader = new BufferedReader(fr);
        String input = reader.readLine();
        while (input != null) {
          double x = Double.valueOf(input);
          double result = calculatorController.calculate(x);
          String sresult = Double.toString(result);
          fwresult.write(sresult + '\n');
          input = reader.readLine();
        }
        reader.close();
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
