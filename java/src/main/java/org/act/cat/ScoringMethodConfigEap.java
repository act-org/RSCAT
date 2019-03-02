package org.act.cat;

import org.act.util.ProbDistribution;

/**
 * This class describes of {@link ScoringMethodConfig} for the EAP method.
 *
 * @see {@link ScoringMethodEap}
 */
public class ScoringMethodConfigEap extends ScoringMethodConfig {

    /*
     * See the constructor for the parameter definition.
     */
    // CHECKSTYLE: stop JavadocVariable
    private final int numQuad;
    private final double minQuad;
    private final double maxQuad;

    // CHECKSTYLE: resume JavadocVariable

    /**
     * Constructs a new {@link ScoringMethodConfigEap}.
     * <p>
     * For the <i>Normal</i> prior distribution, the first parameter in
     * <code>priorPars</code> is mean, the second parameter is standard error;
     * for the uniform distribution, the two parameters define the interval.
     *
     * @param numQuad the number of quadrature points
     * @param minQuad the minimum quadrature point
     * @param maxQuad the maximum quadrature point
     * @param priorName the name of the prior distribution ("normal" or
     *            "uniform")
     * @param priorPars the parameters for the prior distribution
     */
    public ScoringMethodConfigEap(int numQuad, double minQuad, double maxQuad, ProbDistribution priorDistribution) {
        super(priorDistribution);
        this.numQuad = numQuad;
        this.minQuad = minQuad;
        this.maxQuad = maxQuad;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ScoringMethod.SUPPORTED_METHODS scoringMethod() {
        return ScoringMethod.SUPPORTED_METHODS.EAP;
    }

    /**
     * Returns the number of quadrature points.
     */
    public int getNumQuad() {
        return numQuad;
    }

    /**
     * Returns the minimum quadrature point.
     */
    public double getMinQuad() {
        return minQuad;
    }

    /**
     * Returns the maximum quadrature point.
     */
    public double getMaxQuad() {
        return maxQuad;
    }

}
