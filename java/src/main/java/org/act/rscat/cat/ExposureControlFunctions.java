package org.act.rscat.cat;

import static org.act.rscat.cat.CatHelper.getPreviousShadowBoolean;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.act.rscat.mip.SolverInputSingleItem;
import org.act.rscat.mip.SolverInputSinglePassage;
import org.act.rscat.testdef.Item;
import org.act.rscat.testdef.TestConfig;
import org.apache.commons.math3.distribution.BinomialDistribution;
import org.apache.commons.math3.distribution.UniformRealDistribution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A help class that includes utility functions for exposure control.
 */
public final class ExposureControlFunctions {

    /**
     * The maximum ability value for the exposure control. Only item exposure control at overall ability level
     * in one range is supported, the {@link ExposureControlFunctions#EC_THETA_MIN} and {@link ExposureControlFunctions#EC_THETA_MAX}
     * define the range.
     */
    public static final double EC_THETA_MAX = 8.0;

    /**
     * The minimum ability value for the exposure control. Only item exposure control at overall ability level
     * in one range is supported, the {@link ExposureControlFunctions#EC_THETA_MIN} and {@link ExposureControlFunctions#EC_THETA_MAX}
     * define the range.
     */
    public static final double EC_THETA_MIN = -8.0;
    private static final Logger LOGGER = LoggerFactory.getLogger(ExposureControlFunctions.class);
    private static final double FADING_FACTOR = 0.999;
    private ExposureControlFunctions() {
    }

    /**
     * Higher level function that is called prior to stage 1 of the adaptive test
     * for each student; this function calls the functions that calculate the
     * eligibility probabilities and create the eligibility indicators; the output
     * from this function is passed to the CAT input object and remains the same for
     * every stage of the test (for a given student)
     *
     * @param exposureControlType the exposure control type
     * @param exposureControlData a custom java object with the four aggregated
     *                            count arrays (n, alpha, phi, & rho), the exposure
     *                            type (passage or item), the number of theta
     *                            intervals, the midpoints between the theta
     *                            intervals, the exposure goal rate (rmax), and the
     *                            fading factor.
     * @param entityCount         number of either items or passages
     *
     * @return eligibilityProbabilities: A K x I matrix (i.e., two-dimensional
     *         array) of eligibility probabilities, where K is the number of theta
     *         intervals and I is the number of passages or items
     */
    public static PassageOrItemEligibilityOverall prepExposureControlDataForCat(ExposureControlType exposureControlType,
            ExposureControlData exposureControlData, int entityCount) {

        // calculate eligibility probabilities
        double[][] eligibilityProbabilities = calcEligibilityProbabilities(exposureControlData, entityCount);

        // get eligibility indicators
        boolean[][] eligibilityIndicators = getEligibility(eligibilityProbabilities);

        // get theta points
        double[] thetaPoints = exposureControlData.getThetaPoints();

        // return object to be used as input for CAT engine
        return new PassageOrItemEligibilityOverall(
                thetaPoints, eligibilityIndicators, exposureControlType);
    }

    /**
     * This function calculates eligibility probabilities for passages or items
     * (depending on whether exposure control is at the passage or item level) from
     * Equation 15 in van der Linden & Veldkamp (2007). Two types of aggregated
     * count arrays are required as input data: alpha and epsilon. Alpha is the
     * number of passages/items administered at theta interval k; epsilon is the
     * number of passages/items eligible at theta interval k. The alpha and epsilon
     * arrays have K x I unique counts, where K is the number of theta intervals and
     * I is the number of passages or items in the pool. This function returns a
     * two-dimensional array with one eligibility probability for each passage or
     * item and at each theta interval. If alpha is equal to zero for a given
     * passage or item at a given theta interval, then the eligibility probability
     * is set to 1.0. All probabilities are bound between 0.0 and 1.0. This function
     * is used in conjunction with the getEligibility function to determine whether
     * passages or items are eligible for inclusion on the next shadow test at each
     * stage of the adaptive test.
     *
     * @param exposureControlData a custom java object with the four aggregated
     *                            count arrays (n, alpha, phi, & rho), the exposure
     *                            type (passage or item), the number of theta
     *                            intervals, the midpoints between the theta
     *                            intervals, the exposure goal rate (rmax), and the
     *                            fading factor.
     * @param entityCount         number of either items OR passages
     *
     * @return eligibilityProbabilities: A K x I matrix (i.e., two-dimensional
     *         array) of eligibility probabilities, where K is the number of theta
     *         intervals and I is the number of passages or items
     */

    public static double[][] calcEligibilityProbabilities(ExposureControlData exposureControlData, int entityCount) {

        /*
         * -the first dimension for nArray, alphaArray, phiArray, & rhoArray should have
         * length K, where K is the number of theta intervals at which exposure is
         * controlled -the second dimension for alphaArray & rhoArray should have length
         * I, where I is the number of passages/items in the pool
         */

        // get aggregated count arrays
        double[][] alphaArray = exposureControlData.getAlphaArray();
        double[][] epsilonArray = exposureControlData.getEpsilonArray();

        // get correct lengths for dimension 1 and dimension 2
        int numThetaIntervals = exposureControlData.getNumThetaIntervals();

        // initialize eligibility probabilities array
        double[][] eligibilityProbabilities = new double[numThetaIntervals][entityCount];

        // check to make sure arrays are the correct length
        if (alphaArray.length != numThetaIntervals || epsilonArray.length != numThetaIntervals) {
            String message = "one of the aggregated count arrays is not of length K on first dimension";
            LOGGER.error(message);
            throw new IllegalStateException(message);
        }

        if (entityCount == 0) {
            String message = "exposureType has not been specified or item/passage pool is of length 0";
            LOGGER.error(message);
            throw new IllegalStateException(message);
        }

        if (alphaArray[0].length != entityCount || epsilonArray[0].length != entityCount) {
            String message = "alpha or epsilon aggregated count array is not of length I on second dimension";
            LOGGER.error(message);
            throw new IllegalStateException(message);
        }

        double rMax = exposureControlData.getRMax();
        /*
         * -loop through count arrays to calculate eligibility probabilities -k =
         * 0,1,...(K-1) where K is the number of theta intervals at which exposure is
         * controlled -i = 0,1,...(I-1) where I is the number of passages/items in the
         * pool
         */
        for (int k = 0; k < alphaArray.length; k++) {
            for (int i = 0; i < alphaArray[k].length; i++) {
                // check to make sure counts in denominator of Equation 15 are
                // greater than zero
                if (alphaArray[k][i] > 0) {
                    eligibilityProbabilities[k][i] = (rMax * epsilonArray[k][i]) / alphaArray[k][i];
                    // if counts in denominator are not greater than zero, set
                    // the probability to 1.0
                } else {
                    eligibilityProbabilities[k][i] = 1.0d;
                }

                // make sure probabilities aren't greater than 1.0
                if (eligibilityProbabilities[k][i] > 1.0d) {
                    eligibilityProbabilities[k][i] = 1.0d;
                }

                // make sure probabilities aren't less than 0.0
                if (eligibilityProbabilities[k][i] < 0.0d) {
                    eligibilityProbabilities[k][i] = 0.0d;
                }
            }
        }

        return eligibilityProbabilities;
    }

    /**
     * This function produces a set of K arrays of logical values that indicate
     * whether an item is eligible for inclusion in the shadow test (one array for
     * each theta range). A value of false indicates that the item is ineligible and
     * a value of true indicates that the item is eligible. The value for each item
     * is sampled from a binomial distribution with n=1 and p equal to the item
     * eligibility probability at a particular theta level.
     *
     * @param eligibilityProbabilities a K x I two dimensional double array with the
     *                                 eligibility probability for each passage
     *                                 item, i, at each theta interval, k.
     *
     * @return boolean[][] eligibility indicators: a two dimensional boolean array
     *         with dimensions K x I, where K is the number of theta intervals and I
     *         is the number of passage/items. Each element of the array contains a
     *         value of false or true, with false indicating that the corresponding
     *         passage/item at theta interval k is ineligible and true indicating
     *         that the passage/item is eligible for inclusion on the next shadow
     *         test
     */
    public static boolean[][] getEligibility(double[][] eligibilityProbabilities) {

        // getting the number of theta intervals and number of items
        int numThetaIntervals = eligibilityProbabilities.length;
        int numItems = eligibilityProbabilities[0].length;

        // creating eligibility indicators two dimensional array; first index is
        // for theta interval, second index is for item/passage
        boolean[][] eligibilityIndicators = new boolean[numThetaIntervals][numItems];

        /*
         * cycle through all theta intervals k and all passages/items i, conduct one
         * bernoulli trial for each i at each k; the result of each experiment is
         * converted to a boolean value and saved in the eligibilityIndicators array
         */
        for (int k = 0; k < numThetaIntervals; k++) {
            for (int i = 0; i < numItems; i++) {
                double p = eligibilityProbabilities[k][i];
                BinomialDistribution binomDist = new BinomialDistribution(1, p);
                int experiment = binomDist.sample();
                boolean experimentBoolean = false;
                if (experiment == 1) {
                    experimentBoolean = true;
                }
                eligibilityIndicators[k][i] = experimentBoolean;
            }
        }

        return eligibilityIndicators;
    }

    /**
     * This function finds the index of the theta interval in which the current
     * theta estimate is located. The index is then used to pass the corresponding
     * array of eligibility indicators for the solver
     *
     * @param passageOrItemEligibilityOverall the eligibility indicators for all
     *                                        theta intervals
     * @param currentThetaEstimate            the current theta estimate and
     *                                        associated standard error
     *
     * @return passageOrItemEligibilityAtThetaRange: the eligibility indicators
     *         associated with the theta interval at which the current theta
     *         estimate is currently in
     */

    public static PassageOrItemEligibilityAtThetaRange findThetaInterval(
            PassageOrItemEligibilityOverall passageOrItemEligibilityOverall, ThetaEst currentThetaEstimate) {

        // get theta points
        double[] thetaPoints = passageOrItemEligibilityOverall.getThetaPoints();

        // get current theta estimate
        double currentTheta = currentThetaEstimate.getTheta();

        // boolean value indicating whether the theta interval has been found
        boolean foundTheta = false;

        // theta interval index
        int thetaInd = 0;

        // get index of theta interval that the current theta estimate is in
        if (thetaPoints.length > 0) {
            for (int k = 0; k < (thetaPoints.length + 1) && !foundTheta; k++) {
                if (k == 0) {
                    if (currentTheta < thetaPoints[k]) {
                        thetaInd = k;
                        foundTheta = true;
                    }
                } else if (k > 0 && k < thetaPoints.length) {
                    if (currentTheta >= thetaPoints[k - 1] && currentTheta < thetaPoints[k]) {
                        thetaInd = k;
                        foundTheta = true;
                    }
                } else if (k == thetaPoints.length) {
                    if (currentTheta >= thetaPoints[k - 1]) {
                        thetaInd = k;
                        foundTheta = true;
                    }
                } else {
                    String message = "theta interval index k is out of range";
                    LOGGER.error(message);
                    throw new IllegalStateException(message);
                }
            }
        } else {
            thetaInd = 0;
        }

        // get eligibility vector associated with current theta interval.
        boolean[] eligibilityAtCurrentTheta = passageOrItemEligibilityOverall.getEligibilityIndicators()[thetaInd];

        return new PassageOrItemEligibilityAtThetaRange(thetaInd, eligibilityAtCurrentTheta,
                passageOrItemEligibilityOverall.getExposureType());
    }

    /**
     * Prepares item data for the solver.
     *
     * @param itemIds an array of item identifiers
     * @param fisherInformation an array of item fisher information for items in the {@code itemIds}
     * @param itemsAdministeredBoolean a boolean array that indicates whether items have been administered or not
     * @param eligibilityIndicatorsItemSoft a boolean array that indicates the soft eligibility of items, from the exposure rate control
     * @param eligiblePassageItemsHard a boolean array that indicates the hard eligibility of items
     * @param previousShadowTest an array of item identifiers selected in the previous shadow test
     * @return the {@code List} of {@link SolverInputSingleItem}
     */
    public static List<SolverInputSingleItem> prepItemDataForSolver(String[] itemIds, double[] fisherInformation,
            boolean[] itemsAdministeredBoolean, boolean[] eligibilityIndicatorsItemSoft,
            boolean[] eligiblePassageItemsHard, String[] previousShadowTest) {

        // convert previous shadow test string array to previous shadow test
        // boolean array
        boolean[] previousShadowTestBoolean = getPreviousShadowBoolean(itemIds, previousShadowTest);

        // put data in form that solver can use (item level)
        List<SolverInputSingleItem> solverInputSingleItemList = new ArrayList<>();
        for (int index = 0; index < itemIds.length; index++) {
            String itemId = itemIds[index];
            double information = fisherInformation[index];
            boolean administered = itemsAdministeredBoolean[index];
            boolean eligible = eligibilityIndicatorsItemSoft[index];
            boolean eligibleHard = eligiblePassageItemsHard[index];
            boolean selected = previousShadowTestBoolean[index]; // selected in
                                                                 // previous
                                                                 // shadow test
            SolverInputSingleItem singleItemInput = new SolverInputSingleItem(itemId, information, administered,
                    eligible, eligibleHard, selected);
            // update list
            solverInputSingleItemList.add(singleItemInput);
        }

        return solverInputSingleItemList;
    }

    /**
     * Prepares passage eligibility data for the solver.
     *
     * @param passageIds an array of passage identifiers
     * @param eligibilityIndicatorsPassageSoft a boolean array that indicates the soft eligibility of passages
     * @return the {@code List} of {@link SolverInputSinglePassage}
     */
    public static List<SolverInputSinglePassage> prepPassageDataForSolver(String[] passageIds,
            boolean[] eligibilityIndicatorsPassageSoft) {

        // put data in form that solver can use (passage level)
        List<SolverInputSinglePassage> solverInputSinglePassageList = new ArrayList<>();
        for (int index = 0; index < passageIds.length; index++) {
            String passageId = passageIds[index];
            boolean eligible = eligibilityIndicatorsPassageSoft[index];
            SolverInputSinglePassage singlePassageInput = new SolverInputSinglePassage(passageId, eligible);
            // update list
            solverInputSinglePassageList.add(singlePassageInput);
        }
        return solverInputSinglePassageList;
    }

    /**
     * Applies a random value to item information for
     * the first L items
     *
     * @param l        number of items for which to apply randomization method
     * @param s        current item number (expect this to start at 1; i.e. be
     *                 1-based)
     * @param itemInfo item information values at current theta estimate
     * @return itemInfoWithLRandom a double array of info values updated with random
     *         components
     */
    public static double[] applyLRandomToInfo(int l, int s, double[] itemInfo) {

        // initialize return object
        double[] itemInfoWithLRandom = new double[itemInfo.length];

        // calculate the weight based on the stage number s and the value of L
        if (s < l) {
            double w = s / (l + 1.0);

            // find min and max info
            double minInfo = 1000.0d;
            double maxInfo = -1000.0d;
            for (int i = 0; i < itemInfo.length; i++) {
                if (itemInfo[i] < minInfo) {
                    minInfo = itemInfo[i];
                }
                if (itemInfo[i] > maxInfo) {
                    maxInfo = itemInfo[i];
                }
            }

            // construct uniform distribution
            UniformRealDistribution uniformRealDistribution = new UniformRealDistribution(minInfo, maxInfo);

            // get new info values by adding random weighted value to each item
            // information value
            for (int i = 0; i < itemInfo.length; i++) {
                double randomValue = uniformRealDistribution.sample();
                itemInfoWithLRandom[i] = w * itemInfo[i] + (1.0 - w) * randomValue;
            }
        } else {
            itemInfoWithLRandom = itemInfo;
        }
        return itemInfoWithLRandom;
    }

    /**
     * Determines which ineligible items/passages were relaxed to create a feasible
     * shadow test solution
     *
     * @param exposureType                     whether exposure control is at the
     *                                         item or passage level
     * @param eligibilityIndicatorsItemSoft    eligibility indicators for items
     * @param selectedItemRowIndicesArray      item row indices for selected shadow
     *                                         test items
     * @param itemIds                          item identifiers for items in item
     *                                         pool
     * @param eligibilityIndicatorsPassageSoft eligibility indicators for passages
     * @param selectedPassageRowIndicesArray   passage row indices for selected
     *                                         shadow test passages
     * @param passageIdsFromPassageTable       passage identifiers for passages from
     *                                         passage pool table
     * @return relaxedEligibilityIds ids of items/passages that were relaxed
     */
    public static List<String> determineRelaxedEligibilityIds(ExposureControlType exposureType,
            boolean[] eligibilityIndicatorsItemSoft, int[] selectedItemRowIndicesArray, String[] itemIds,
            boolean[] eligibilityIndicatorsPassageSoft, int[] selectedPassageRowIndicesArray,
            String[] passageIdsFromPassageTable) {

        List<String> relaxedEligibilityIds = new ArrayList<>();

        // if item was ineligible but included on shadow test, then add string
        // identifier to returned list
        if (ExposureControlType.ITEM.equals(exposureType)) {
            for (int i = 0; i < selectedItemRowIndicesArray.length; i++) {
                int rowIdx = selectedItemRowIndicesArray[i];
                if (eligibilityIndicatorsItemSoft[rowIdx]) {
                    relaxedEligibilityIds.add(itemIds[rowIdx]);
                }
            }
            // if passage was ineligible but included on shadow test, then add
            // string identifier to returned list
        } else if (ExposureControlType.PASSAGE.equals(exposureType)) {
            for (int j = 0; j < selectedPassageRowIndicesArray.length; j++) {
                int rowIdx = selectedPassageRowIndicesArray[j];
                if (eligibilityIndicatorsPassageSoft[rowIdx]) {
                    relaxedEligibilityIds.add(passageIdsFromPassageTable[rowIdx]);
                }
            }
        }
        return relaxedEligibilityIds;
    }

    /**
     * Creates the {@link PassageOrItemEligibilityOverall} for item or passage
     * exposure rate control.
     *
     * @param catConfig the CAT configuration
     * @param testConfig the test configuration
     * @param exposureItemUsageRangeMap the exposureItemUsageRangeMap
     * @return the instance of PassageOrItemEligibilityOverall
     * @see CatConfig
     * @see TestConfig
     * @see ExposureItemUsage
     */
    public static PassageOrItemEligibilityOverall buildPassageOrItemEligibilityOverall(CatConfig catConfig,
            TestConfig testConfig, Map<ThetaRange, Map<String, ExposureItemUsage>> exposureItemUsageRangeMap) {
        if (catConfig.exposureControlConfig().getType().equals(ExposureControlType.ITEM)) {
            int itemIdColIndex = testConfig.getItemPoolTable().columnIndex(Item.ColumnName.ITEM_ID.getColName());
            List<String> itemIds = testConfig.getItemPoolTable().columns().get(itemIdColIndex);
            ExposureControlData exposureControlData = buildExposureControlDataItem(exposureItemUsageRangeMap, itemIds,
                    catConfig.exposureControlConfig().getThetaRanges(), catConfig.exposureControlConfig().getrMax(),
                    FADING_FACTOR);

            return ExposureControlFunctions
                    .prepExposureControlDataForCat(catConfig.exposureControlConfig().getType(), exposureControlData,
                            testConfig.getItemPoolTable().rowCount());

        } else {
            return PassageOrItemEligibilityOverall.PASSAGE_OR_ITEM_ELIGIBILITY_OVERALL_NONE;
        }

    }

    /**
     * Updates the item usage in exposure data.
     *
     * @param exposureItemUsageRangeMap the exposure control data to be updated
     * @param passageOrItemEligibilityAtThetaRange the instance of {@link PassageOrItemEligibilityAtThetaRange}
     * @param itemsAdminstered a {@code List} of item identifiers that have been administered
     * @param itemIds a {@code List} of item identifiers in the item pool
     */
    public static void updateItemUsage(Map<ThetaRange, Map<String, ExposureItemUsage>> exposureItemUsageRangeMap,
            PassageOrItemEligibilityAtThetaRange passageOrItemEligibilityAtThetaRange, List<String> itemsAdminstered,
            List<String> itemIds) {

        // Fix the range as [EC_THETA_MIN, EC_THETA_MAX] to disable exposure control
        // conditional on theta ranges
        ThetaRange thetaRange = new ThetaRange(EC_THETA_MIN, EC_THETA_MAX);
        boolean[] eligibilityIndicators = passageOrItemEligibilityAtThetaRange.getEligibilityIndicators();
        for (int i = 0; i < eligibilityIndicators.length; i++) {

            // Update epsilon
            if (eligibilityIndicators[i]) {
                exposureItemUsageRangeMap.get(thetaRange).get(itemIds.get(i)).increaseEpsilon();
            }
        }
        // Update alpha
        for (int j = 0; j < itemsAdminstered.size(); j++) {
            String entityIdAlpha = itemsAdminstered.get(j);
            exposureItemUsageRangeMap.get(thetaRange).get(entityIdAlpha).increaseAlpha();
        }
    }

    /**
     * Creates {@link ExposureControlData} for the exposure control.
     *
     * @param exposureItemUsageRangeMap the item usage data
     * @param itemIds a {@code List} of item identifiers in the item pool
     * @param thetaRanges a {@code List} of {@link ThetaRange} for the exposure control. For the exposure control at
     *                      the overall range, only include the overall theta range in the list.
     * @param rMax the exposure control goal rate
     * @param fadingFactor the fading factor
     * @return an instance of {@link ExposureControlData}
     */
    public static ExposureControlData buildExposureControlDataItem(
            Map<ThetaRange, Map<String, ExposureItemUsage>> exposureItemUsageRangeMap, List<String> itemIds,
            List<ThetaRange> thetaRanges, double rMax, double fadingFactor) {

        ThetaRange key = new ThetaRange(ExposureControlFunctions.EC_THETA_MIN, ExposureControlFunctions.EC_THETA_MAX);
        double[][] alphaArray = new double[thetaRanges.size()][itemIds.size()];
        double[][] epsilonArray = new double[thetaRanges.size()][itemIds.size()];

        for (int i = 0; i < thetaRanges.size(); i++) {
            Map<String, ExposureItemUsage> itemUsageMap = exposureItemUsageRangeMap.get(key);
            for (int j = 0; j < itemIds.size(); j++) {
                if (itemUsageMap != null && itemUsageMap.get(itemIds.get(j)) != null) {
                    alphaArray[i][j] = itemUsageMap.get(itemIds.get(j)).getAlpha();
                    epsilonArray[i][j] = itemUsageMap.get(itemIds.get(j)).getEpsilon();
                }
            }
        }

        return new ExposureControlData.Builder().thetaPoints(getThetaPoints(thetaRanges)).alphaArray(alphaArray)
                .epsilonArray(epsilonArray).numThetaIntervals(thetaRanges.size()).rMax(rMax).fadingFactor(fadingFactor)
                .build();
    }
    private static double[] getThetaPoints(List<ThetaRange> thetaRanges) {
        double[] thetaPoints = new double[thetaRanges.size() - 1];
        for (int i = 0; i < thetaRanges.size() - 1; i++) {
            thetaPoints[i] = thetaRanges.get(i).getMaxThetaExclusive();
        }
        return thetaPoints;
    }
}
