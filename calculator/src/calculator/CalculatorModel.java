package calculator;

/**
 *  This is the model class of calculator.
 *  <p>This class is responsible for storing input and output</p>
 *
 *  @date ${date}
 *  @author Xueying Li
 */
public class CalculatorModel {
  public double getOutput() {
    return output;
  }

  public void setOutput(double output) {
    this.output = output;
  }

  public double getInput() {
    return input;
  }

  public void setInput(double input) {
    this.input = input;
  }

  private double output;
  private double input;

}
