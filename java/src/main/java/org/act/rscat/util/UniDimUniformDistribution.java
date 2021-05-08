package org.act.rscat.util;

import java.util.Random;

/**
 * This class is an implementation of {@link ProbDistribution} for the
 * uni-dimensional uniform distribution.
 */
public class UniDimUniformDistribution implements ProbDistribution {

    /**
     * Minimum value.
     */
    private double min;

    /**
     * Maximum value
     */
    private double max;
    private Random random = new Random();

    /**
     * Constructs a new {@link UniDimUniformDistribution}.
     *
     * @param min the lower bound of the range
     * @param max the upper bound of the range
     */
    public UniDimUniformDistribution(double min, double max) {
        this.min = min;
        this.max = max;
    }

    @Override
    public int getDim() {
        return 1;
    }

    @Override
    public TYPE getType() {
        return TYPE.UNIFORM;
    }

    @Override
    public double density(double... x) {
        if (x[0] >= min && x[0] <= max) {
            return 1.0 / (max - min);
        } else {
            return 0;
        }
    }

    @Override
    public double[] sample(int n, double minVal, double maxVal) {
        double[] sample = new double[n];
        for (int i = 0; i < n; i++) {
            sample[i] = random.nextDouble() * (maxVal - minVal) + minVal;
        }
        return sample;
    }

    @Override
    public double[] sample(int n) {
        return sample(n, min, max);
    }

    @Override
    public double mean() {
        return (min + max)/2.0;
    }

    @Override
    public double sd() {
        return Math.pow(max - min, 2) / 12.0;
    }

}
