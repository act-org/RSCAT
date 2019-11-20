package org.act.cat;

import org.apache.commons.math3.linear.RealMatrix;

/**
 * This class defines the item selection method using the efficiency balanced information (EBI) criterion .
 * 
 * @see ItemSelectionMethod
 */
public class EBIMethod implements ItemSelectionMethod {
	private RealMatrix itemPar;
	private double thetaEst;
	private double thetaSe;

	/**
	 * Constructs a new {@link EBIMethod}.
	 * 
	 * @param itemPar an I X P matrix containing item parameters, where I is
     *            the number of items and P is the number of parameters
	 * @param thetaEst the value of ability estimate
	 * @param thetaSe the value of ability estimate standard error
	 */
	public EBIMethod(RealMatrix itemPar, double thetaEst, double thetaSe) {
		this.itemPar = itemPar;
		this.thetaEst = thetaEst;
		this.thetaSe = thetaSe;
	}
	
	/**
	 * Returns the EBI values of items.
	 * 
	 * @return the array of item EBI values.
	 */
	@Override
	public double[] getSelectionCriteria() {
        int parNum = itemPar.getRowDimension();
        double[] ebiValues = new double[parNum];
        for (int i = 0; i < parNum; i++) {
            double a = itemPar.getEntry(i, 0);
            double b = itemPar.getEntry(i, 1);
            double c = itemPar.getEntry(i, 2);
            double d = itemPar.getEntry(i, 3);
            double maxInfo = calMaxInfo(a, b, c, d);
            ebiValues[i] = calEBI(maxInfo, a, b, c, d);
        }
        return ebiValues;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SUPPORTED_METHODS getMethodType() {
		return SUPPORTED_METHODS.MAX_FISHER_INFO;
	}
	
	private double calMaxInfo(double a, double b, double c, double d) {
		double thetaMax = b + (1 / a) * Math.log((1 + Math.pow(1 + 8 * c, 0.5)) / 2.0);
		return CatFunctions.calInfo(thetaMax, a, b, c, d);
	}
	
	private double calEBI(double maxInfo, double a, double b, double c, double d) {
		
		int stepNum = 100;
		double interval = 4 * thetaSe / stepNum;
		double ebi = 0;
		double theta = thetaEst - 2 * thetaSe;
		for (int i = 0 ; i < stepNum; i ++) {
			ebi += CatFunctions.calInfo(theta + (i + 0.5) * interval, a, b, c, d) * interval;
		}
		return ebi * (1 + 1 / calMaxInfo( a, b, c, d));
	}

}
