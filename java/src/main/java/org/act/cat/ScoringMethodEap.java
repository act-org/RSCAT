package org.act.cat;

import static org.act.cat.CatFunctions.getProb3PL;

import org.act.util.ProbDistribution;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

/**
 * An implementation of {@link ScoringMethod} using the expected A posteriori
 * (EAP) estimate.
 * <p>
 * This class includes the calculation of the expected a posteriori (EAP)
 * estimate and associated standard error using equations from Bock and Mislevy
 * (1982). The EAP procedure is a commonly used procedure for estimating the
 * examinee's ability estimate (or theta estimate) from (a) the examinee's item
 * scores, (b) the item parameters, and (c) a prior distribution. The default
 * prior is a standard normal distribution (i.e., a normal distribution with a
 * mean of zero and a variance of unity), although users can change the
 * parameters of the normal distribution or specify a uniform distribution
 * instead (so that the prior does not affect the ability estimate).
 */
public class ScoringMethodEap implements ScoringMethod {

    /*
     * See field definition in the constructor.
     */
    // CHECKSTYLE: stop JavadocVariable
    private RealMatrix itemPar;
    private ItemScores itemScores;
    private int nQuad;
    private double minQuad;
    private double maxQuad;
    private ProbDistribution priorDistribution;

    // CHECKSTYLE: resume JavadocVariable

    /**
     * Constructs a new{@link ScoringMethodEap}.
     *
     * @param itemPar an I X P matrix containing item parameters, where I is the
     *            number of items and P is the number of item parameters in the
     *            model (note: for the 3PL model, P=3)
     * @param itemScores an integer array of item scores with length equal to
     *            the number of items administered
     * @param config the configuration of scoring method
     *
     * @see {@link ScoringMethodConfigEap}
     */
    public ScoringMethodEap(RealMatrix itemPar, ItemScores itemScores, ScoringMethodConfigEap config) {
        super();
        this.itemPar = itemPar;
        this.itemScores = itemScores;
        this.nQuad = config.getNumQuad();
        this.minQuad = config.getMinQuad();
        this.maxQuad = config.getMaxQuad();
        this.priorDistribution = config.getPriorDistribution();
    }

    /**
     * {@inheritDoc}
     *
     * Estimates ability using EAP.
     */
    @Override
    public ThetaEst estimateTheta() {

        int parNum = itemPar.getRowDimension();
        RealVector quadPoints = new ArrayRealVector(nQuad);
        RealVector densities = new ArrayRealVector(nQuad);
        RealVector denominatorVec = new ArrayRealVector(nQuad);
        double numeratorTheta = 0;
        double numeratorSD = 0;
        double denominator = 0;
        double sumDensities = 0;
        for (int q = 0; q < nQuad; q++) {
            double qPoint = q * (maxQuad - minQuad) / (nQuad - 1.0) + minQuad;
            quadPoints.addToEntry(q, qPoint);
            densities.addToEntry(q, priorDistribution.density(quadPoints.getEntry(q)));
            sumDensities = sumDensities + densities.getEntry(q);
        }
        densities = densities.mapDivide(sumDensities);
        for (int q = 0; q < nQuad; q++) {
            double likelihood = 1;
            for (int i = 0; i < parNum; i++) {
                double a = itemPar.getEntry(i, 0);
                double b = itemPar.getEntry(i, 1);
                double c = itemPar.getEntry(i, 2);
                double D = itemPar.getEntry(i, 3);
                double p = getProb3PL(a, b, c, D, quadPoints.getEntry(q));
                double pResp = Math.pow((1.0d - p), 1.0d - itemScores.getItemScores()[i]) *
                        Math.pow(p, itemScores.getItemScores()[i]);
                likelihood = likelihood * pResp;
            }
            numeratorTheta = numeratorTheta + quadPoints.getEntry(q) * likelihood * densities.getEntry(q);
            denominator = denominator + likelihood * densities.getEntry(q);
            denominatorVec.addToEntry(q, likelihood * densities.getEntry(q));
        }
        double postMean = numeratorTheta / denominator;
        for (int q = 0; q < nQuad; q++) {
            numeratorSD = numeratorSD + Math.pow(quadPoints.getEntry(q) - postMean, 2.0d) * denominatorVec.getEntry(q);
        }
        double postSd = Math.pow(numeratorSD / denominator, 0.5d);
        ThetaEst thetaEst = new ThetaEst(postMean, postSd);
        return (thetaEst);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ScoringMethod.SUPPORTED_METHODS scoringMethodType() {
        return ScoringMethod.SUPPORTED_METHODS.EAP;
    }

}
