package edu.yonsei.util;

/**
 */
public interface DifferentiableFunction extends Function {
  double[] derivativeAt(double[] x);
}
