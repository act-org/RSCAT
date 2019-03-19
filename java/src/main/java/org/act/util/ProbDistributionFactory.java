package org.act.util;

/**
 * Factory class to create instances of classes that implement {@link ProbDistribution}.
 */
public class ProbDistributionFactory {

    private ProbDistributionFactory() {
    }

    /**
     * Creates a {@link ProbDistribution}.
     * @param name the name of the probability distribution
     * @param params the parameters of the probability distribution
     * @return a <code>ProbDistribution</code>, either <code>UniDimNormalDistribution</code>
     *          or <code>UniDimUniformDistribution</code>.
     */
    public static ProbDistribution getProbDistribution(String name, double[] params) {
        if (name.equals("Normal")) {
            return new UniDimNormalDistribution(params[0], params[1]);
        } else if (name.equals("Uniform")) {
            return new UniDimUniformDistribution(params[0], params[1]);
        }
        return null;

    }
}
