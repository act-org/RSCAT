package org.act.cat;

public class ThetaEstWithSamples extends ThetaEst {

    /**
     * Sample of theta posterior draws;
     */
    private double[] postThetaDrawSamples = null;

    /**
     * Constructs a new {@link ThetaEstWithSamples}.
     *
     * @param theta the ability estimate
     * @param se the standard error associated with the theta estimate
     * @param postThetaDrawSamples the samples of theta posterior draws
     */
    public ThetaEstWithSamples(double theta, double se, double[] postThetaDrawSamples) {
        super(theta, se);
        this.postThetaDrawSamples = postThetaDrawSamples;
    }

    /**
     * Returns the samples of posterior draws associated with the estimate.
     *
     * @return the samples of posterior draws
     */
    public double[] getPostThetaDrawSamples() {
        return postThetaDrawSamples;
    }

}
