package org.act.cat;

import org.apache.commons.math3.linear.RealMatrix;

/**
 * Factory class to generate instance of type {@link Information}.
 */
public class ItemSelectionMethodFactory {

    private ItemSelectionMethodFactory() {
    }

    /**
     * Returns an instance of {@link ItemSelectionMethod}.
     *
     * @param itemSelectionMethodType the item selection method type
     * @param itemPar the item parameter {@link RealMatrix}
     * @param thetaEst the ability estimate
     * @param thetaSe the ability estimate standard error
     * @return the instance of {@code ItemSelectionMethod}
     */
    public static ItemSelectionMethod getInstance(ItemSelectionMethod.SUPPORTED_METHODS itemSelectionMethodType,
            RealMatrix itemPar, double thetaEst, double thetaSe) {
        switch (itemSelectionMethodType) {
        case MAX_FISHER_INFO:
            return new MaxFisherInformationMethod(itemPar, thetaEst);
        case EBI:
            return new EBIMethod(itemPar, thetaEst, thetaSe);
        default:
            throw new IllegalArgumentException("The item selection is method not supported!");
        }
    }

}
