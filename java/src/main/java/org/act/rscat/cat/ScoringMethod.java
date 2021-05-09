package org.act.rscat.cat;

/**
 * This interface describes scoring methods to estimate an exaiminee's ability
 * (theta value) in CAT.
 */
public interface ScoringMethod {
    /**
     * Supported scoring methods.
     */
    enum SUPPORTED_METHODS {
        EAP
    }

    /**
     * Estimates the ability (theta value) of an examinee.
     *
     * @return the ability estimate in the format of {@link ThetaEst}
     */
    ThetaEst estimateTheta();

    /**
     * Returns the enum type of the scoring method.
     *
     * @return the enum type of the scoring method
     */
    SUPPORTED_METHODS scoringMethodType();
}
