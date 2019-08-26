package org.act.cat;

/**
 * This class defines exposure control eligibility indicators.
 *
 */
public class PassageOrItemEligibilityOverall {

    /**
     * An instance of {@link PassageOrItemEligibilityOverall} for disabling exposure control.
     */
    public static final PassageOrItemEligibilityOverall PASSAGE_OR_ITEM_ELIGIBILITY_OVERALL_NONE = new PassageOrItemEligibilityOverall(
            null, null, ExposureControlType.NONE);

    /**
     * Boundaries that determine the theta ranges at which exposure is controlled.
     */
    private double[] thetaPoints;

    /**
     * Eligibility indicators for all theta ranges; first index refers to theta
     * range; second index refers to passage/item.
     */
    private boolean[][] eligibilityIndicators;

    /**
     * ExposureType indicates whether exposure control is at the passage level, at
     * the item level, or turned off.
     */
    private ExposureControlType exposureType;

    /**
     * Constructs a new {@link PassageOrItemEligibilityOverall}
     *
     * @param thetaPoints           the boundaries that determine the theta ranges
     *                              at which exposure is controlled
     * @param eligibilityIndicators the eligibility indicators for all theta ranges;
     *                              first index refers to theta range; second index
     *                              refers to passage/item.
     * @param exposureType          the exposure type indicating whether exposure
     *                              control is at the passage level, item level, or
     *                              turned off.
     */
    public PassageOrItemEligibilityOverall(double[] thetaPoints, boolean[][] eligibilityIndicators,
            ExposureControlType exposureType) {
        this.thetaPoints = thetaPoints;
        this.eligibilityIndicators = eligibilityIndicators;
        this.exposureType = exposureType;
    }

    /**
     * Returns boundaries that determine the theta ranges at which exposure is
     * controlled.
     *
     * @return the theta boundaries
     */
    public double[] getThetaPoints() {
        return thetaPoints;
    }

    /**
     * Sets boundaries that determine the theta ranges at which exposure is
     * controlled.
     *
     * @param thetaPoints the theta boundaries
     */
    public void setThetaPoints(double[] thetaPoints) {
        this.thetaPoints = thetaPoints;
    }

    /**
     * Returns eligibility indicators for all theta ranges; first index refers to
     * theta range; second index refers to passage/item.
     *
     * @return the eligibility indicators
     */
    public boolean[][] getEligibilityIndicators() {
        return eligibilityIndicators;
    }

    /**
     * Sets eligibility indicators for all theta ranges; first index refers to theta
     * range; second index refers to passage/item.
     *
     * @param eligibilityIndicators the eligibility indicators
     */
    public void setEligibilityIndicators(boolean[][] eligibilityIndicators) {
        this.eligibilityIndicators = eligibilityIndicators;
    }

    /**
     * Returns the {@link ExposureType} indicating whether exposure control is at
     * the passage level, at the item level, or turned off.
     *
     * @return the {@code ExposureType} result
     */
    public ExposureControlType getExposureType() {
        return exposureType;
    }

    /**
     * Sets the {@link ExposureType} indicating whether exposure control is at the
     * passage level, at the item level, or turned off.
     *
     * @param exposureType the ExposureType
     */
    public void setExposureType(ExposureControlType exposureType) {
        this.exposureType = exposureType;
    }
}
