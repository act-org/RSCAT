package org.act.cat;

/**
 * This interface describes item selection methods in CAT.
 */
public interface ItemSelectionMethod {

    /**
     * Supported scoring methods.
     * <ul>
     * <li>MAX_FISHER_INFO: maximizing fisher information.</li>
     * <li>EBI: efficiency balanced information .</li>
     * </ul>
     */
    enum SUPPORTED_METHODS {
        MAX_FISHER_INFO, EBI
    }

    /**
     * Returns the item selection criteria of all items.
     *
     * @return the item selection criteria.
     */
    double[] getSelectionCriteria();

    /**
     * Returns the item selection method type.
     *
     * @return the item selection method type.
     * @see SUPPORTED_METHODS
     */
    SUPPORTED_METHODS getMethodType();
}
