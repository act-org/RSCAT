package org.act.cat;

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
     * Stores the aggregated count of examinees who visited theta range k and
     * took item/passage i.
     */
    private double[][] alphaArray;

    /**
     * Stores the aggregated count of examinees who visited theta range k when
     * item/passage i was eligible.
     */
    private double[][] epsilonArray;

    /**
     * NumThetaIntervals is the number of theta intervals at which exposure is
     * controlled.
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
     * @param alphaArray the alphaArray
     * @param epsilonArray the epsilonArray
     * @param numThetaIntervals the numThetaIntervals
     * @param rMax the rMax value
     * @param fadingFactor the fadingFactor value
     */
    public ExposureControlData(double[][] alphaArray, double[][] epsilonArray, int numThetaIntervals,
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
     * Sets the 2d alphaArray.
     *
     * @param alphaArray the 2d alphaArray
     */
    public void setAlphaArray(double[][] alphaArray) {
        this.alphaArray = alphaArray;
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
     * Sets the epsilonArray.
     *
     * @param epsilonArray the epsilonArray
     */
    public void setEpsilonArray(double[][] epsilonArray) {
        this.epsilonArray = epsilonArray;
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
     * Sets the numThetaIntervals value. NumThetaIntervals is the number of
     * theta intervals at which exposure is controlled.
     *
     * @param numThetaIntervals the numThetaIntervals
     */
    public void setNumThetaIntervals(int numThetaIntervals) {
        this.numThetaIntervals = numThetaIntervals;
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
     * Sets the array of theta points.
     *
     * @param thetaPoints the array of theta points
     */
    public void setThetaPoints(double[] thetaPoints) {
        this.thetaPoints = thetaPoints;
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
     * Sets the exposure control goal rate (maximum exposure rate).
     *
     * @param rMax the exposure control goal rate (maximum exposure rate)
     */
    public void setrMax(double rMax) {
        this.rMax = rMax;
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

        public Builder() {
        }

        public Builder alphaArray(double[][] alphaArray) {
            this.alphaArray = alphaArray;
            return this;
        }

        public Builder epsilonArray(double[][] epsilonArray) {
            this.epsilonArray = epsilonArray;
            return this;
        }

        public Builder numThetaIntervals(int numThetaIntervals) {
            this.numThetaIntervals = numThetaIntervals;
            return this;
        }

        public Builder thetaPoints(double[] thetaPoints) {
            this.thetaPoints = thetaPoints;
            return this;
        }

        public Builder rMax(double rMax) {
            this.rMax = rMax;
            return this;
        }

        public Builder fadingFactor(double fadingFactor) {
            this.fadingFactor = fadingFactor;
            return this;
        }

        public ExposureControlData build() {
        	return new ExposureControlData(alphaArray, epsilonArray, numThetaIntervals,
                    thetaPoints, rMax, fadingFactor);
        }
    }
}
