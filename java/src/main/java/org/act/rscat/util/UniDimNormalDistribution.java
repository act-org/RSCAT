package org.act.rscat.util;

import java.util.Arrays;

import org.apache.commons.math3.distribution.NormalDistribution;

/**
 * This class is an implementation of {@link ProbDistribution} for the
 * uni-dimensional normal distribution.
 */
public class UniDimNormalDistribution implements ProbDistribution {

    /**
     * Mean of the normal distribution.
     */
    private double mean;

    /**
     * Standard deviation of the normal distribution.
     */
    private double sd;

    /**
     * The probability distribution.
     */

    private NormalDistribution normDist;

    /**
     * Constructs a new {@link UniDimNormalDistribution}.
     *
     * @param mean the mean
     * @param sd the standard deviation
     */
    public UniDimNormalDistribution(double mean, double sd) {
        this.mean = mean;
        this.sd = sd;
        normDist = new NormalDistribution(mean, sd);
    }

    @Override
    public int getDim() {
        return 1;
    }

    @Override
    public TYPE getType() {
        return ProbDistribution.TYPE.NORMAL;
    }

    @Override
    public double density(double... x) {
        return normDist.density(x[0]);
    }

    @Override
    public double[] sample(int n, double min, double max) {
        double[] samples = new double[n];
        if (min > max) {
            throw new IllegalArgumentException("Min should not be greater than Max.");
        } else if (min == max) {
            Arrays.fill(samples, min);
            return samples;
        } else {
            for (int i = 0; i < n; i++) {
                double value = normDist.sample();
                while (value > max || value < min) {
                    value = normDist.sample();
                }
                samples[i] = value;
            }
            return samples;
        }
    }

    @Override
    public double[] sample(int n) {
        return normDist.sample(n);

    }

    @Override
    public double mean() {
        return mean;
    }

    @Override
    public double sd() {
        return sd;
    }

}
