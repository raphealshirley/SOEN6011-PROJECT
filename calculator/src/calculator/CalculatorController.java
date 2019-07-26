package calculator;

/**
 * This is the calculator controller.
 * <li>Entrance of the app</li>
 * <li>Manipulates view and model</li>
 *
 * @date ${date}
 * @author Xueying Li
 */
public class CalculatorController {
  private CalculatorModel calculatorModel;
  private CalculatorView calculatorView;

  private static final double EXP_LIMIT_H = 709.782712893384;
  private static final double EXP_LIMIT_L = -745.1332191019411;
  private static final double LN2 = 0.6931471805599453;
  private static final double LN2_H = 0.6931471803691238;
  private static final double  LN2_L = 1.9082149292705877e-10;
  private static final double  INV_LN2 = 1.4426950408889634;

  private static final double EXPM1_Q1 = -3.33333333333331316428e-02;
  private static final double  EXPM1_Q2 =  1.58730158725481460165e-03;
  private static final double  EXPM1_Q3 = -7.93650757867487942473e-05;
  private static final double  EXPM1_Q4 =  4.00821782732936239552e-06;
  private static final double  EXPM1_Q5 = -2.01099218183624371326e-07;

  private static final double TWO_28 = 0x10000000;
  private static final double  TWO_54 = 0x40000000000000L;

  private static final double P1 = 0.16666666666666602;
  private static final double  P2 = -2.7777777777015593e-3;
  private static final double  P3 = 6.613756321437934e-5;
  private static final double  P4 = -1.6533902205465252e-6;
  private static final double  P5 = 4.1381367970572385e-8;

  public CalculatorController() {
    calculatorModel = new CalculatorModel();
    calculatorView = new CalculatorView();
  }

  /**
  * Calculate sinhx and set model state.
  * @param x input
  * @return
  */
  public double calculate(double x) {
    double result = this.sinh(x);
    this.calculatorModel.setOutput(result);
    this.calculatorModel.setInput(x);
    return result;
  }

  /**
   * Method to calculate sinh regarding the mathematical definition: (exp(x)-exp(-x))/2.
   * 1. Replace x by |x| (sinh(-x) = -sinh(x)).
   * 2. 0 <= x <= 22, sinh := (E + E/(E+1))/2, E=expm1(x)
   * 3. 22 <= x <= log(Double.MAX_VALUE), sinh(x) := exp(x)/2
   * 4. log(Double.MAX_VALUE) <= x <= overflowthreshold:  sinh(x) := exp(x/2)/2 * exp(x/2)
   * 5. overflowthreshold < x :  sinh(x) := +inf (overflow)
   * @param x user input
   * @return sinhx
   */
  public double sinh(double x) {
    double t;
    double w;
    double h;

    // handle special cases
    if (x != x) {
      return x;
    }
    if (x == Double.POSITIVE_INFINITY) {
      return Double.POSITIVE_INFINITY;
    }
    if (x == Double.NEGATIVE_INFINITY) {
      return Double.NEGATIVE_INFINITY;
    }

    if (x < 0) {
      h = - 0.5;
    } else {
      h = 0.5;
    }

    long bits;
    long hbits;
    long lbits;

    bits = Double.doubleToLongBits(x);
    hbits = getHighDWord(bits) & 0x7fffffffL;  // ignore sign
    lbits = getLowDWord(bits);

    // |x| in [0, 22], return sign(x) * 0.5 * (E+E/(E+1))
    if (hbits < 0x40360000L) {
      if (hbits < 0x3e300000L) {
        return x;
      }

      t = expm1(abs(x));

      if (hbits < 0x3ff00000L) {
        return h * (2.0 * t - t * t / (t + 1.0));
      }

      return h * (t + t / (t + 1.0));
    }

    // |x| in [22, log(Double.MAX_VALUE)], return 0.5 * exp(|x|)
    if (hbits < 0x40862e42L) {
      return h * exp(abs(x));
    }

    // |x| in [log(Double.MAX_VALUE), overflowthreshold]
    if ((hbits < 0x408633ceL) || ((hbits == 0x408633ceL) && (lbits <= 0x8fb9f87dL))) {
      w = exp(0.5 * abs(x));
      t = h * w;

      return t * w;
    }
    // |x| > overflowthershold
    return h * Double.POSITIVE_INFINITY;
  }

  /**
   * Method to calculate E(r) = expm1(r) = exp(r)-1.
   * 1. Argument reduction:
   * Given x, find r and integer k such that
   * x = k * ln(2) + r,  |r| <= 0.5 * ln(2)
   * 2. Approximating expm1(r) according to a special rational function:
   * expm1(r) := r + r^2/2 + {r^3 * [3 - (R1 + R1*r/2)]/[6 - r*(3 - R1*r/2)]}/2
   * where, R1(z) ~ 1.0 + Q1*z + Q2*z**2 + Q3*z**3 + Q4*z**4 + Q5*z**5
   * where, Q1 = -1.6666666666666567384E-2,
   * Q2 = 3.9682539681370365873E-4,
   * Q3 = -9.9206344733435987357E-6,
   * Q4 =  2.5051361420808517002E-7,
   * Q5  =  -6.2843505682382617102E-9;
   * (where z=r*r, and Q1 to Q5 are called EXPM1_Qx in the source)
   * To compensate the error in the argument reduction,
   * we use expm1(r+c) = expm1(r) + c + expm1(r)*c
   * @param x exmp(x) - 1
   * @return expm1(x)
   */
  public static double expm1(double x) {
    boolean negative = (x < 0);
    double y;
    double hi;
    double lo;
    double c;
    double t;

    c = 0.0;
    y = abs(x);


    int k;
    long bits;
    long hbits;
    long lbits;

    bits = Double.doubleToLongBits(y);
    hbits = getHighDWord(bits);
    lbits = getLowDWord(bits);


    // handle special cases and large arguments
    if (hbits >= 0x4043687aL) {
      if (hbits >= 0x40862e42L) {
        if (hbits >= 0x7ff00000L) {
          if (((hbits & 0x000fffffL) | (lbits & 0xffffffffL)) != 0) {
            return x;                        // exp(NaN) = NaN
          } else {
            return negative ? -1.0 : x;      // exp({+-inf}) = {+inf, -1}
          }
        }

        if (x > EXP_LIMIT_H) {
          return Double.POSITIVE_INFINITY;     // overflow
        }
      }

      if (negative)  {
        return -1.0;
      }
    }

    // argument reduction
    if (hbits > 0x3fd62e42L) {
      if (hbits < 0x3ff0a2b2L) {
        if (negative) {
          hi = x + LN2_H;
          lo = -LN2_L;
          k = -1;
        } else {
          hi = x - LN2_H;
          lo = LN2_L;
          k  = 1;
        }
      } else {
        k = (int) (INV_LN2 * x + (negative ? - 0.5 : 0.5));
        t = k;
        hi = x - t * LN2_H;
        lo = t * LN2_L;
      }

      x = hi - lo;
      c = (hi - x) - lo;
    } else if (hbits < 0x3c900000L) {
      return x;
    } else {
      k = 0;
    }


    double hxs;
    double hfx;
    double r1;

    hfx = 0.5 * x;
    hxs = x * hfx;
    r1 = 1.0 + hxs * (EXPM1_Q1 + hxs * (EXPM1_Q2 + hxs * (EXPM1_Q3 + hxs
        * (EXPM1_Q4 + hxs *  EXPM1_Q5))));
    t = 3.0 - r1 * hfx;

    double e;
    e = hxs * ((r1 - t) / (6.0 - x * t));

    if (k == 0) {
      return x - (x * e - hxs);    // c == 0
    } else {
      e = x * (e - c) - c;
      e -= hxs;

      if (k == -1) {
        return 0.5 * (x - e) - 0.5;
      }

      if (k == 1) {
        if (x < - 0.25) {
          return -2.0 * (e - (x + 0.5));
        } else {
          return 1.0 + 2.0 * (x - e);
        }
      }

      if (k <= -2 || k > 56) {
        y = 1.0 - (e - x);

        bits = Double.doubleToLongBits(y);
        hbits = getHighDWord(bits);
        lbits = getLowDWord(bits);

        hbits += (k << 20);     // add k to y's exponent

        y = buildDouble(lbits, hbits);

        return y - 1.0;
      }

      t = 1.0;
      if (k < 20) {
        bits = Double.doubleToLongBits(t);
        hbits = 0x3ff00000L - (0x00200000L >> k);
        lbits = getLowDWord(bits);

        t = buildDouble(lbits, hbits);      // t = 1 - 2^(-k)
        y = t - (e - x);

        bits = Double.doubleToLongBits(y);
        hbits = getHighDWord(bits);
        lbits = getLowDWord(bits);

        hbits += (k << 20);     // add k to y's exponent

        y = buildDouble(lbits, hbits);
      } else {
        bits = Double.doubleToLongBits(t);
        hbits = (0x000003ffL - k) << 20;
        lbits = getLowDWord(bits);

        t = buildDouble(lbits, hbits);      // t = 2^(-k)
        y = x - (e + t);
        y += 1.0;

        bits = Double.doubleToLongBits(y);
        hbits = getHighDWord(bits);
        lbits = getLowDWord(bits);
        hbits += (k << 20);     // add k to y's exponent
        y = buildDouble(lbits, hbits);
      }
    }

    return y;
  }

  /**
   * Calculate absolute x.
   *
   * @param a input
   * @return absolute value of x
   */
  public static double abs(double a) {
    return (a <= 0.0D) ? 0.0D - a : a;
  }

  /**
   * Returns the lower two words of a long. This is intended to be.
   * used like this:
   * <code>getLowDWord(Double.doubleToLongBits(x))</code>.
   * */
  private static long getLowDWord(long x) {
    return x & 0x00000000ffffffffL;
  }

  /**
   * Take <em>e</em><sup>a</sup>.  The opposite of <code>log()</code>.
   * If the argument is NaN, the result is NaN; if the argument is positive infinity,
   * the result is positive infinity; and if the argument is negative
   * infinity, the result is positive zero.
   *
   * @param x the number to raise to the power
   * @return the number raised to the power of <em>e</em>
   */
  public static double exp(double x) {
    if (x != x) {
      return x;
    }
    if (x > EXP_LIMIT_H) {
      return Double.POSITIVE_INFINITY;
    }
    if (x < EXP_LIMIT_L) {
      return 0;
    }

    // Argument reduction.
    double hi;
    double lo;
    int k;
    double t = abs(x);
    if (t > 0.5 * LN2) {
      if (t < 1.5 * LN2) {
        hi = t - LN2_H;
        lo = LN2_L;
        k = 1;
      } else {
        k = (int) (INV_LN2 * t + 0.5);
        hi = t - k * LN2_H;
        lo = k * LN2_L;
      }
      if (x < 0) {
        hi = -hi;
        lo = -lo;
        k = -k;
      }
      x = hi - lo;
    } else if (t < 1 / TWO_28) {
      return 1;
    } else {
      lo = hi = k = 0;
    }

    // Now x is in primary range.
    t = x * x;
    double c = x - t * (P1 + t * (P2 + t * (P3 + t * (P4 + t * P5))));
    if (k == 0) {
      return 1 - (x * c / (c - 2) - x);
    }

    double y = 1 - (lo - x * c / (2 - c) - hi);
    return scale(y, k);
  }

  /**
   * Returns the higher two words of a long.
   * <code>getHighDWord(Double.doubleToLongBits(x))</code>.
   */
  private static long getHighDWord(long x) {
    return (x & 0xffffffff00000000L) >> 32;
  }

  private static double buildDouble(long lowDWord, long highDWord) {
    return Double.longBitsToDouble(((highDWord & 0xffffffffL) << 32) | (lowDWord & 0xffffffffL));
  }

  /**
   * Helper method for scaling a double by a power of 2.
   *
   * @param x the double
   * @param n the scale; |n| < 2048
   * @return x * 2**n
   */
  private static double scale(double x, int n) {
    if (abs(n) >= 2048) {
      throw new InternalError("Assertion failure");
    }
    if (x == 0 || x == Double.NEGATIVE_INFINITY || ! (x < Double.POSITIVE_INFINITY) || n == 0) {
      return x;
    }
    long bits = Double.doubleToLongBits(x);
    int exp = (int) (bits >> 52) & 0x7ff;
    if (exp == 0) {
      x *= TWO_54;
      exp = ((int) (Double.doubleToLongBits(x) >> 52) & 0x7ff) - 54;
    }
    exp += n;
    if (exp > 0x7fe) {
      return Double.POSITIVE_INFINITY * x;
    }
    if (exp > 0) {
      return Double.longBitsToDouble((bits & 0x800fffffffffffffL) | ((long) exp << 52));
    }
    if (exp <= -54) {
      return 0 * x; // Underflow.
    }
    exp += 54; // Subnormal result.
    x = Double.longBitsToDouble((bits & 0x800fffffffffffffL) | ((long) exp << 52));
    return x * (1 / TWO_54);
  }


  public static void main(String[] args) {
    CalculatorController calculatorController = new CalculatorController();
    calculatorController.calculatorView.run();
  }
}
