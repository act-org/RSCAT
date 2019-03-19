package org.act.cat;

/**
 * This class describes the item eligibility data in CAT engine
 */
public class PassageOrItemEligibilityAtThetaRange {

    /**
     * Index of the <i>current</i> theta range used for the exposure control
     */
    private Integer thetaRangeIndex;

    /**
     * Eligibility indicators for the <i>current</i> theta range
     */
    private boolean[] eligibilityIndicators;

    /**
     * {@link ExposureControlType} indicating whether exposure control is at the
     * passage or the item level
     */
    private ExposureControlType exposureControlType;

    /**
     * Constructs a new {@link PassageOrItemEligibilityAtThetaRange}.
     */
    public PassageOrItemEligibilityAtThetaRange() {
    }

    /**
     * Constructs a new {@link PassageOrItemEligibilityAtThetaRange}.
     *
     * @param thetaRangeIndex the index of the <i>current</i> theta range used
     *            for the exposure control
     * @param eligibilityIndicators the eligibility indicators for
     *            <i>current</i> theta range
     * @param exposureControlType the {@link ExposureControlType} indicating
     *            whether exposure control is at the passage or the item level
     */
    public PassageOrItemEligibilityAtThetaRange(Integer thetaRangeIndex, boolean[] eligibilityIndicators,
            ExposureControlType exposureControlType) {
        this.thetaRangeIndex = thetaRangeIndex;
        this.eligibilityIndicators = eligibilityIndicators;
        this.exposureControlType = exposureControlType;
    }

    /**
     * Returns the index of the <i>current</i> theta range used for the exposure
     * control.
     *
     * @return the current theta range index
     */
    public Integer getThetaRangeIndex() {
        return thetaRangeIndex;
    }

    /**
     * Sets the index of the <i>current</i> theta range used for the exposure
     * control.
     *
     * @param thetaRangeIndex the current theta range index
     */
    public void setThetaRangeIndex(Integer thetaRangeIndex) {
        this.thetaRangeIndex = thetaRangeIndex;
    }

    /**
     * Returns the eligibility indicators for the <i>current</i> theta range.
     *
     * @return the eligibility indicators
     */
    public boolean[] getEligibilityIndicators() {
        return eligibilityIndicators;
    }

    /**
     * Sets the eligibility indicators for the <i>current</i> theta range.
     *
     * @param eligibilityIndicators the eligibility indicators
     */
    public void setEligibilityIndicators(boolean[] eligibilityIndicators) {
        this.eligibilityIndicators = eligibilityIndicators;
    }

    /**
     * Returns the exposure control type as an instance of
     * {@link ExposureControlData.ExposureType}.
     *
     * @return the {@link ExposureControlData.ExposureType} instance
     */
    public ExposureControlType getExposureType() {
        return exposureControlType;
    }

    /**
     * Sets the exposure control type instance of
     * {@link ExposureControlData.ExposureType}.
     *
     * @param exposureControlType the instance of
     *            {@link ExposureControlData.ExposureType}
     */
    public void setExposureControlType(ExposureControlType exposureControlType) {
        this.exposureControlType = exposureControlType;
    }
}
