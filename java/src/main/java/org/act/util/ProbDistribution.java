package org.act.util;

/**
 * This interface describes probability distributions that can be used in the
 * CAT simulation
 */
public interface ProbDistribution {

    /**
     * Describes the supported distribution types. "Normal" represents the
     * normal distribution; "Uniform" represents the uniform distribution.
     */
    enum TYPE {
        Normal, Uniform
    };

    /**
     * Returns the number of dimensions of the distribution.
     *
     * @return the number of dimensions of the distribution
     */
    int getDim();

    /**
     * Returns the type of distribution.
     *
     * @return the type of distribution.
     * @see TYPE
     */
    TYPE getType();

    /**
     * Returns the probability density at the point x
     *
     * @param x the point where its probability density is to be calculated
     * @return the probability density at x
     */
    double density(double... x);

    /**
     * Generates <code>n</code> samples between <code>min</code> and
     * <code>max</code> based on the distribution.
     *
     * @param n the sample size
     * @param min the minimum value of samples
     * @param max the maximum value of samples
     * @return the array containing samples
     */
    double[] sample(int n, double min, double max);

    /**
     * Generates <code>n</code> samples based on the distribution.
     *
     * @param n the sample size
     * @param min the minimum value of samples
     * @param max the maximum value of samples
     * @return the array containing samples
     */
    double[] sample(int n);

    /**
     * Returns the mean of the distribution.
     *
     * @return the mean of the distribution
     */
    double mean();

    /**
     * Returns the standard error of the distribution.
     *
     * @return the standard error of the distribution
     */
    double sd();
}
