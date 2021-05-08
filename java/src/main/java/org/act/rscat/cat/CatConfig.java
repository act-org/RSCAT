package org.act.rscat.cat;

import org.act.rscat.mip.SolverConfig;

/**
 * This interface describes the configuration of a Computerized Adaptive Test.
 */
public interface CatConfig {

    /**
     * Returns the solver configuration to solve the shadow test assembly.
     *
     * @return the solver configuration instance of {@link SolverConfig}
     */
    SolverConfig solverConfig();

    /**
     * Returns the initial theta estimate value used in CAT. This is the common
     * value for all simulated examinees.
     *
     * @return the initial theta value
     */
    double initTheta();

    /**
     * Returns the scaling D constant value, which can be 1.6 or 1.7 depending on
     * the item pool.
     *
     * @return the scaling D constant value
     */
    double scalingConstant();

    /**
     * Returns the scoring method configuration for CAT.
     *
     * @return the instance of {@link ScoringMethodConfig}
     */
    AbstractScoringMethodConfig scoringMethodConfig();

    /**
     * Returns the number <code>L</code> of items that are randomly administered at
     * the beginning of the test. The randomized item administration still comply
     * with test blueprint (including all test specification constraints).
     *
     * @return the number of items that are randomly adminstered at the beginning of
     *         the test
     */
    int lValue();

    /**
     * Returns the exposure control configuration defined in
     * {@link ExposureControlConfig}.
     *
     * @return the exposure control configuration
     */
    ExposureControlConfig exposureControlConfig();

    /**
     * Returns the type of item selection method.
     *
     * @return the type of item selection method.
     * @see ItemSelectionMethod.SUPPORTED_METHODS
     */
    ItemSelectionMethod.SUPPORTED_METHODS itemSelectionMethod();
}
