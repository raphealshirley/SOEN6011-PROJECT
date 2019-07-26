package calculator;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Scanner;

/**
 * This is the view class of Calculator.
 * <p>A text-based user interface(TUI) to interact with users</p>
 * <li>Get user input</li>
 * <li>Print calculated result</li>
 * <li>Showing error messages</li>
 * <li>Guide user to enter valid input</li>
 *
 * @date ${date}
 * @author Xueying Li
 */
public class CalculatorView {
  private static CalculatorController calculatorController = new CalculatorController();

  /**
   * Method to run TUI.
   */
  public void run() {
    System.out.println("Welcome to calculator sinhx");

    first:
    while (true) {
      System.out.println("Please enter a number x: ");
      Scanner input = new Scanner(System.in);
      String number = input.nextLine().trim();
      try {
        float x = Float.valueOf(number.trim()).floatValue();
        double r = calculate(x);
        if (r == Double.POSITIVE_INFINITY || r == Double.NEGATIVE_INFINITY) {
            System.err.println("The result is overflow.");
        } else {
          NumberFormat formatter = new DecimalFormat();
          formatter = new DecimalFormat("0.#####");
          String xstring = formatter.format(x);
          System.out.printf("The result of sinh(%s) is: " + "\n",xstring);
          System.out.println(calculate(x));
        }
        second:
        while (true) {
          try {
            System.out.println("Do you want to continue? y/n");
            String ans = input.nextLine().trim();
            if (ans.equalsIgnoreCase("y")) {
              break second;
            } else if (ans.equalsIgnoreCase("n")) {
              break first;
            } else {
              throw new Exception();
            }
          } catch (Exception e) {
            System.err.println("Ohh, Please Enter y/n!");
          }
        }

      } catch (NumberFormatException nfe) {
        System.err.println("Ohh, Please Enter a Number!");
      }
    }
    System.out.println("Thanks, Bye!");
  }

  /**
   * Controller is notified to return calculate result.
   * @param x input
   * @return
   */
  public double calculate(double x) {
    return calculatorController.calculate(x);
  }

}
