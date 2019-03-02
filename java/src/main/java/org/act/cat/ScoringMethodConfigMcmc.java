package org.act.cat;

import org.act.cat.ScoringMethod.SUPPORTED_METHODS;
import org.act.util.ProbDistribution;

public class ScoringMethodConfigMcmc extends ScoringMethodConfig {
    private final int burnInLength;
    private final int postBurnInSampleSize;
    private final int postBurnInLagSize;

    /**
     * Constructs a new {@link ScoringMethodConfigMcmc}
     *
     * @param burnInLength the MCMC burn-in length
     * @param postBurnInSampleSize the number of samples saved for post burn-in
     *            iterations
     * @param postBurnInLagSize the interval (number of iterations) between two
     *            saved post burn-in samples
     * @param priorName the name of the prior distribution
     * @param priorPars the parameters of the prior distribution
     */
    public ScoringMethodConfigMcmc(int burnInLength, int postBurnInSampleSize, int postBurnInLagSize,
            ProbDistribution priorDistribution) {
        super(priorDistribution);
        this.burnInLength = burnInLength;
        this.postBurnInSampleSize = postBurnInSampleSize;
        this.postBurnInLagSize = postBurnInLagSize;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SUPPORTED_METHODS scoringMethod() {
        return ScoringMethod.SUPPORTED_METHODS.MCMC;
    }

    /**
     * Returns the MCMC burn-in length.
     *
     * @return the MCMC burn-in length
     */
    public int getBurnInLength() {
        return burnInLength;
    }

    /**
     * Returns the number of samples saved for post burn-in iterations.
     *
     * @return the number of samples saved for post burn-in iterations
     */
    public int getPostBurnInSampleSize() {
        return postBurnInSampleSize;
    }

    /**
     * Returns the interval (number of iterations) between two saved post
     * burn-in samples
     *
     * @return the interval between two saved post burn-in samples
     */
    public int getPostBurnInLagSize() {
        return postBurnInLagSize;
    }

}
