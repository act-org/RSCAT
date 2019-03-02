package org.act.cat;

import org.act.util.ProbDistribution;

/**
 * This class defines a scoring method configuration. Parameters in the
 * configuration are applied to the scoring algorithm.
 */
public abstract class ScoringMethodConfig {

    private final ProbDistribution priorDistribution;

    /**
     * Constructs an empty {@link ScoringMethodConfig}.
     */
    public ScoringMethodConfig() {
        this.priorDistribution = null;
    }

    /**
     * Constructs a new {@link ScoringMethodConfig}.
     *
     * @param priorDistribution the prior distribution used in the scoring
     *            method.
     * @see ProbDistribution
     */
    public ScoringMethodConfig(ProbDistribution priorDistribution) {
        this.priorDistribution = priorDistribution;
    }

    /**
     * Returns the type of the scoring method related to the configuration.
     *
     * @return the type of the scoring method
     */
    public abstract ScoringMethod.SUPPORTED_METHODS scoringMethod();

    /**
     * Returns the prior distribution used in the scoring method.
     *
     * @return the prior distribution
     * @see ProbDistribution
     */
    public ProbDistribution getPriorDistribution() {
        return priorDistribution;
    }

}
