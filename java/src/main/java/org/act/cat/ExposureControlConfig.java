package org.act.cat;

import java.util.List;

/**
 * This class defines the parameters used to configure the exposure control.
 */
public class ExposureControlConfig {

    /**
     * The exposure control type defined in {@link ExposureControlType}, including
     * item level, passage level, or no exposure control.
     */
    private final ExposureControlType type;

    /**
     * The list of {@link ThetaRange} for exposure rate control.
     */
    private final List<ThetaRange> thetaRanges;

    /**
     * The goal of exposure rate control.
     */
    private final double rMax;

    /**
     * Constructs a new {@link ExposureControlConfig}.
     *
     * @param type        the exposure control type
     * @param thetaRanges the list of {@link ThetaRange} for exposure rate control
     * @param rMax        the goal of exposure rate control
     */
    public ExposureControlConfig(ExposureControlType type, List<ThetaRange> thetaRanges, double rMax) {
        this.type = type;
        this.thetaRanges = thetaRanges;
        this.rMax = rMax;
    }

    /**
     * Returns the exposure control type.
     *
     * @return the exposure control type.
     */
    public ExposureControlType getType() {
        return type;
    }

    /**
     * Returns the theta ranges for the exposure control. For the exposure control
     * at overall level, only one theta range is used.
     *
     * @return the list of {@link ThetaRange}
     */
    public List<ThetaRange> getThetaRanges() {
        return thetaRanges;
    }

    /**
     * Returns the exposure control goal rate.
     *
     * @return the exposure control goal rate
     */
    public double getrMax() {
        return rMax;
    }
}
