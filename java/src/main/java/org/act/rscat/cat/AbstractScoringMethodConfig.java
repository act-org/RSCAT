package org.act.rscat.cat;

import org.act.rscat.util.ProbDistribution;

/**
 * This class defines a scoring method configuration. Parameters in the
 * configuration are applied to the scoring algorithm.
 */
public abstract class AbstractScoringMethodConfig {

    private final ProbDistribution priorDistribution;
    
    protected AbstractScoringMethodConfig() {
        this.priorDistribution = null;
    }

    protected AbstractScoringMethodConfig(ProbDistribution priorDistribution) {
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
