package org.act.cat;

import org.act.mip.SolverConfig;

/**
 * An implementation of {@link CatConfig} for the standard CAT.
 */
public class CatConfigStandard implements CatConfig {
    private final SolverConfig solverConfig;
    private final double initTheta;
    private final double scalingConstant;
    private final ScoringMethodConfig scoringMethodConfig;
    private final ExposureControlConfig exposureControlConfig;
    private final int lValue;

    /**
     * Constructs a new {@link CatConfigStandard}.
     *
     * @param solverConfig          the configuration parameters for solver
     * @param initTheta             initial value for theta (i.e., starting theta
     *                              value for the first adapative stage)
     * @param scalingConstant       the D scaling constant for rescaling IRT
     *                              response function
     * @param scoringMethodConfig   the scoring method configuration
     * @param exposureControlConfig the exposure control configuration data
     * @param lValue                the number of random item administrations at the
     *                              beginning of test
     * @see SolverConfig
     * @see ScoringMethodConfig
     * @see ExposureControlType
     */
    public CatConfigStandard(SolverConfig solverConfig, double initTheta, double scalingConstant,
            ScoringMethodConfig scoringMethodConfig, ExposureControlConfig exposureControlConfig, int lValue) {
        this.solverConfig = solverConfig;
        this.initTheta = initTheta;
        this.scalingConstant = scalingConstant;
        this.scoringMethodConfig = scoringMethodConfig;
        this.exposureControlConfig = exposureControlConfig;
        this.lValue = lValue;
    }

    @Override
    public double initTheta() {
        return initTheta;
    }

    @Override
    public double scalingConstant() {
        return scalingConstant;
    }

    @Override
    public ScoringMethodConfig scoringMethodConfig() {
        return scoringMethodConfig;
    }

    @Override
    public int lValue() {
        return lValue;
    }

    @Override
    public SolverConfig solverConfig() {
        return solverConfig;
    }

    @Override
    public ExposureControlConfig exposureControlConfig() {
        return exposureControlConfig;
    }
}
