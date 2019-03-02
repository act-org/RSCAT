package org.act.sim;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.act.cat.CatFunctions;
import org.act.cat.ExposureItemUsage;
import org.act.cat.ItemScores;
import org.act.cat.ThetaRange;
import org.act.util.ContentTable;
import org.apache.commons.math3.distribution.BinomialDistribution;
import org.apache.commons.math3.linear.RealMatrix;

/**
 * Utility class providing numeric simulation functions.
 */
public final class SimulationFunctions {

    /**
     * Internal java object for simulation ID values and associated true theta
     * values.
     *
     */
    public static class ThetaTrue {

        private String[] thetaId;
        private double[] thetaVec;

        public ThetaTrue(String[] thetaId, double[] thetaVec) {
            this.thetaId = thetaId;
            this.thetaVec = thetaVec;
        }

        public String[] getThetaId() {
            return thetaId;
        }

        public double[] getThetaVec() {
            return thetaVec;
        }
    }

    /**
	 * Simulates item scores for I items, where I is the number of
     * items that have been administered. In a fully adaptive test, an item
     * score will be simulated for the last item administered. The function
     * currently simulates item scores for the 3PL model by calculating the
     * probability of a correct response for each item given the true theta
     * value and the item parameters for the item. A score of 0 or 1 is then
     * simulated by sampling from a binomial distribution with n=1 and p equal
     * to the probability of a correct response for the item. A score of 0
     * indicates an incorrect response and 1 indicates a correct response.
     *
     * @param itemPar An I x P matrix of item parameters, where I is the number
     *            of items and P is the number of parameters in the model (Note:
     *            for the 3PL model, P=3)
     * @param thetaTrue A double value indicating the true ability level of the
     *            person
     * @return A double array of item responses of length I, where I is the
     *         number of items.
     */

    public static ItemScores simItemScores(RealMatrix itemPar, double thetaTrue) {
        int nItems = itemPar.getRowDimension();
        int[] itemScoreInt = new int[nItems];
        double[] respProb = new double[nItems];
        for (int i = 0; i < nItems; i++) {
            double a = itemPar.getEntry(i, 0);
            double b = itemPar.getEntry(i, 1);
            double c = itemPar.getEntry(i, 2);
            double D = itemPar.getEntry(i, 3);
            double p = CatFunctions.getProb3PL(a, b, c, D, thetaTrue);
            // double p = c + (1 - c) / (1 + Math.exp(-a * (thetaTrue - b)));
            BinomialDistribution distBinomial = new BinomialDistribution(1, p);
            int sampleBinomial = distBinomial.sample();
            itemScoreInt[i] = sampleBinomial;
            respProb[i] = p;
        }
        ItemScores itemScores = new ItemScores(itemScoreInt, respProb);
        return (itemScores);
    }

    public static Map<String, Integer> getItemIdToPassageIndexMap(ContentTable itemPool, ContentTable passagePool) {

        Map<String, Integer> passageIdToIndexMap = new HashMap<>();
        if (passagePool != null) {
            for (int i = 0; i < passagePool.rows().size(); i++) {
                passageIdToIndexMap.put(passagePool.rows().get(i).get(0), i);
            }
        }

        Map<String, Integer> itemIdToPassageIndexMap = new HashMap<>();
        if (itemPool != null) {
            for (int i = 0; i < itemPool.rows().size(); i++) {
                String passageId = itemPool.rows().get(i).get(1);
                if (!"NONE".equalsIgnoreCase(passageId)) {
                    Integer index = passageIdToIndexMap.get(passageId);
                    if (index != null) {
                        String itemId = itemPool.rows().get(i).get(0);
                        itemIdToPassageIndexMap.put(itemId, index);
                    }
                }
            }
        }

        return itemIdToPassageIndexMap;
    }

    public static List<Integer> getPassageIndexOrderForAdministeredItems(Map<String, Integer> itemIdToPassageIndexMap,
            List<String> administeredItems) {
        List<Integer> passageIndexOrder = new ArrayList<>();
        for (String item : administeredItems) {
            Integer index = itemIdToPassageIndexMap.get(item);
            if (index != null) {
                if (passageIndexOrder.size() == 0) {
                    passageIndexOrder.add(index);
                } else if (!passageIndexOrder.get(passageIndexOrder.size() - 1).equals(index)) {
                    passageIndexOrder.add(index);
                }
            }
        }

        return passageIndexOrder;
    }
    
    public static Map<ThetaRange, Map<String, Double>> calItemExposureRates(Map<ThetaRange, Map<String, ExposureItemUsage>> exposureItemUsageRangeMap,
    		int numExaminee) {
    	Map<ThetaRange, Map<String, Double>> exposreRates = new HashMap<>();
    	Iterator<ThetaRange> thetaRangeIt = exposureItemUsageRangeMap.keySet().iterator();
    	
    	// Calculate the exposure rates
    	thetaRangeIt = exposureItemUsageRangeMap.keySet().iterator();
    	while(thetaRangeIt.hasNext()) {
    		ThetaRange thetaRange = thetaRangeIt.next();
    		if (!exposreRates.keySet().contains(thetaRange)) {
    			exposreRates.put(thetaRange, new HashMap<String, Double>());
    		}
    		Map<String, ExposureItemUsage> exposureItemUsage = exposureItemUsageRangeMap.get(thetaRange);
    		for (Entry<String, ExposureItemUsage> entry : exposureItemUsage.entrySet()) {
    			exposreRates.get(thetaRange).put(entry.getKey(), entry.getValue().getAlpha()/numExaminee);
    		}
    	}	
    	return(exposreRates);
    }

    ///////////////////////////////////////////////////////////////
    // private

    private SimulationFunctions() {
    }
}
