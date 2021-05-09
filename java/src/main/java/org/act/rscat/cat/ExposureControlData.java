package org.act.rscat.cat;

/**
 * Defines attributes for exposure control.
 * <p>
 * Includes storing passage/item aggregated counts, and other values for
 * exposure control. The two arrays <code>alphaArray</code> and
 * <code>epsilonArray</code> store the aggregated count data The first dimension
 * for <code>alphaArray</code> and <code>epsilonArray</code> should have length
 * K, where K is the number of theta intervals at which exposure is controlled.
 * The second dimension for <code>alphaArray</code> and
 * <code>epsilonArray</code> should have length I, where I is the number of
 * passages/items in the pool.
 */
public class ExposureControlData {

    /**
     * The aggregated count of examinees who visited theta range k and took
     * item/passage i.
     */
    private double[][] alphaArray;

    /**
     * The aggregated count of examinees who visited theta range k when item/passage
     * i was eligible.
     */
    private double[][] epsilonArray;

    /**
     * The number of theta intervals at which exposure is controlled.
     */
    private int numThetaIntervals;

    /**
     * Array of theta points.
     */
    private double[] thetaPoints;

    /**
     * Goal rate (maximum exposure rate).
     */
    private double rMax;

    /**
     * Fading factor.
     */
    private double fadingFactor;

    /**
     * Constructs an empty {@link ExposureControlData} when exposure control is
     * disabled.
     */
    public ExposureControlData() {
    }

    /**
     * Constructs a new {@link ExposureControlData}
     *
     * @param alphaArray        the aggregated count of examinees who visited theta
     *                          range k and took item/passage i
     * @param epsilonArray      the aggregated count of examinees who visited theta
     *                          range k when item/passage i was eligible
     * @param numThetaIntervals the number of theta intervals at which exposure is
     *                          controlled.
     * @param thetaPoints       the theta points used to define theta ranges for
     *                          exaposure control
     * @param rMax              the exposure control goal rate
     * @param fadingFactor      the fadingFactor value
     */
    private ExposureControlData(double[][] alphaArray, double[][] epsilonArray, int numThetaIntervals,
            double[] thetaPoints, double rMax, double fadingFactor) {
        this.alphaArray = alphaArray;
        this.epsilonArray = epsilonArray;
        this.numThetaIntervals = numThetaIntervals;
        this.thetaPoints = thetaPoints;
        this.rMax = rMax;
        this.fadingFactor = fadingFactor;
    }

    /**
     * Returns the 2d alphaArray.
     *
     * @return the 2d alphaArray
     */
    public double[][] getAlphaArray() {
        return alphaArray;
    }

    /**
     * Returns the epsilonArray.
     *
     * @return the epsilonArray
     */
    public double[][] getEpsilonArray() {
        return epsilonArray;
    }

    /**
     * Returns the numThetaIntervals value.
     *
     * @return the numThetaIntervals value
     */
    public int getNumThetaIntervals() {
        return numThetaIntervals;
    }

    /**
     * Returns the array of theta points.
     *
     * @return the array of theta points
     */
    public double[] getThetaPoints() {
        return thetaPoints;
    }

    /**
     * Returns the exposure control goal rate (maximum exposure rate).
     *
     * @return the exposure control goal rate (maximum exposure rate).
     */
    public double getRMax() {
        return rMax;
    }

    /**
     * Returns the fading factor.
     *
     * @return the fading factor
     */
    public double getFadingFactor() {
        return fadingFactor;
    }

    /**
     * Sets the fading factor.
     *
     * @param fadingFactor the fading factor
     */
    public void setFadingFactor(double fadingFactor) {
        this.fadingFactor = fadingFactor;
    }

    /**
     * A builder class.
     *
     */
    public static class Builder {
        private double[][] alphaArray;
        private double[][] epsilonArray;
        private int numThetaIntervals;
        private double[] thetaPoints;
        private double rMax;
        private double fadingFactor;

        /**
         * Constructs an empty Builder.
         */
        public Builder() {
            // Create a default empty Builder
        }

        /**
         * Sets the aggregated count of examinees who visited theta range k and took
         * item/passage i
         *
         * @param newAlphaArray the alphaArray
         * @return this builder
         */
        public Builder alphaArray(double[][] newAlphaArray) {
            this.alphaArray = newAlphaArray;
            return this;
        }

        /**
         * Sets the aggregated count of examinees who visited theta range k when
         * item/passage i was eligible
         *
         * @param newEpsilonArray the epsilonArray
         * @return this builder
         */
        public Builder epsilonArray(double[][] newEpsilonArray) {
            this.epsilonArray = newEpsilonArray;
            return this;
        }

        /**
         * Sets the number of theta intervals at which exposure is controlled
         *
         * @param newNumThetaIntervals the numThetaIntervals
         * @return this builder
         */
        public Builder numThetaIntervals(int newNumThetaIntervals) {
            this.numThetaIntervals = newNumThetaIntervals;
            return this;
        }

        /**
         * Sets the theta points used to define theta ranges for exposure control.
         *
         * @param newThetaPoints the thetaPoints
         * @return this builder
         */
        public Builder thetaPoints(double[] newThetaPoints) {
            this.thetaPoints = newThetaPoints;
            return this;
        }

        /**
         * Sets the exposure control goal rate.
         *
         * @param newRMax the rMax
         * @return this builder
         */
        public Builder rMax(double newRMax) {
            this.rMax = newRMax;
            return this;
        }

        /**
         * Sets the fading factor.
         *
         * @param newFadingFactor the fadingFactor
         * @return this builder
         */
        public Builder fadingFactor(double newFadingFactor) {
            this.fadingFactor = newFadingFactor;
            return this;
        }

        /**
         * Builds the exposure control data.
         *
         * @return the instance of {@link ExposureControlData}
         */
        public ExposureControlData build() {
            return new ExposureControlData(alphaArray, epsilonArray, numThetaIntervals, thetaPoints, rMax,
                    fadingFactor);
        }
    }
}
