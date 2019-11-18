package org.act.cat;

import org.apache.commons.math3.linear.RealMatrix;
import static org.act.cat.ItemSelectionMethod.SUPPORTED_METHODS;

/**
 * Factory class to generate instance of type {@link Information}.
 */
public class ItemSelectionMethodFactory {
	
	public static ItemSelectionMethod getInstance(ItemSelectionMethod.SUPPORTED_METHODS itemSelectionMethodType,
			RealMatrix itemPar, double thetaEst) {
		switch (itemSelectionMethodType) {
			case MAX_FISHER_INFO: return new MaxFisherInformationMethod(itemPar, thetaEst);
			default: throw new IllegalArgumentException("The item selection is method not supported!");
		}
	}
	
	private ItemSelectionMethodFactory() {
	}
}
