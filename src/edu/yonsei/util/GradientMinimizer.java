package edu.yonsei.util;

/**
 * @author Dan Klein
 */
public interface GradientMinimizer {
  double[] minimize(DifferentiableFunction function, double[] initial, double tolerance);
}
