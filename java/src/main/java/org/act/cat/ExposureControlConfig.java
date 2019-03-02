package org.act.cat;

import java.util.List;

public class ExposureControlConfig {
	
    /**
     * The exposure control type defined in {@link ExposureControlType},
     * including item level, passage level, or no exposure control.
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
     * @param type the exposure control type
     * @param thetaRanges the list of {@link ThetaRange} for exposure rate control
     * @param rMax the goal of exposure rate control
     */
	public ExposureControlConfig(ExposureControlType type, List<ThetaRange> thetaRanges, double rMax) {
		this.type = type;
		this.thetaRanges = thetaRanges;
		this.rMax = rMax;
	}

	public ExposureControlType getType() {
		return type;
	}

	public List<ThetaRange> getThetaRanges() {
		return thetaRanges;
	}

	public double getrMax() {
		return rMax;
	}
}
