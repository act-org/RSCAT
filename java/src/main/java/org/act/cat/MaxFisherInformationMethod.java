package org.act.cat;

import org.apache.commons.math3.linear.RealMatrix;

/**
 * This class defines the item selection method by maximizing Fisher
 * information.
 *
 * @see ItemSelectionMethod
 */
public class MaxFisherInformationMethod implements ItemSelectionMethod {
    private RealMatrix itemPar;
    private double thetaEst;

    /**
     * Constructs a new {@link MaxFisherInformationMethod}.
     *
     * @param itemPar  an I X P matrix containing item parameters, where I is the
     *                 number of items and P is the number of parameters
     * @param thetaEst value of ability estimate
     */
    public MaxFisherInformationMethod(RealMatrix itemPar, double thetaEst) {
        this.itemPar = itemPar;
        this.thetaEst = thetaEst;
    }

    /**
     * Returns the item information values associated with the items and theta
     * estimate
     *
     * @return the array of item information values.
     */
    @Override
    public double[] getSelectionCriteria() {
        int parNum = itemPar.getRowDimension();
        double[] fisherInformation = new double[parNum];
        for (int i = 0; i < parNum; i++) {
            double a = itemPar.getEntry(i, 0);
            double b = itemPar.getEntry(i, 1);
            double c = itemPar.getEntry(i, 2);
            double d = itemPar.getEntry(i, 3);
            double p = CatFunctions.getProb3PL(a, b, c, d, thetaEst);
            double q = 1 - p;
            fisherInformation[i] = d * d * a * a * (q / p) * ((p - c) / (1 - c)) * ((p - c) / (1 - c));
        }
        return fisherInformation;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SUPPORTED_METHODS getMethodType() {
        return SUPPORTED_METHODS.MAX_FISHER_INFO;
    }
}
