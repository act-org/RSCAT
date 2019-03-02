package org.act.cat;

/**
 * Defines ability estimate and associated standar error.
 */
public class ThetaEst {

    /**
     * Ability estimate of an examinee.
     */
    private double theta;

    /**
     * Standard error associated with the theta estimate
     */
    private double se;

    /**
     * Constructs a new {@link ThetaEst}.
     *
     * @param theta the ability estimate
     * @param se the standard error associated with the theta estimate
     */
    public ThetaEst(double theta, double se) {
        this.theta = theta;
        this.se = se;
    }

    /**
     * Returns the theta estimate.
     *
     * @return the theta estimate
     */
    public double getTheta() {
        return theta;
    }

    /**
     * Returns the theta estimate standard error.
     *
     * @return the theta estimate standard error
     */
    public double getSe() {
        return se;
    }

}
