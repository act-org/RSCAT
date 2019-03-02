package org.act.cat;

import java.util.List;

import org.act.testdef.TestConfig;
import org.act.util.PrimitiveArraySet;

/**
 * An effectively immutable container for input data required by the {@link StandardCatEngine}.
 */
public interface CatInput {

    /**
     * Returns the CAT exposure control data.
     *
     * @return a {@link ExposureControlData} containing the CAT exposure control data
     */
    ExposureControlData getExposureControlData();

    /**
     * Returns the passage or item eligibility data.
     *
     * @return a {@link PassageOrItemEligibilityOverall} containing the passage or item eligibility data
     */
    PassageOrItemEligibilityOverall getPassageOrItemEligibility();

    /**
     * Returns the configuration for test specifications.
     *
     * @return a {@link TestConfig} representing test specifications
     */
    TestConfig getTestConfig();

    /**
     * Returns the CAT algorithm configuration.
     *
     * @return a {@link CatConfig} representing the CAT configuration
     */
    CatConfig getCatConfig();

    /**
     * Returns the number of completed test items.
     *
     * @return the number of completed test items
     */
    int getCompletedCount();

    /**
     * Returns the index of the adaptive stage where the CAT input data will be applied. For the first stage, its
     * index is 0.
     *
     * @return the stage index
     */
    int getAdaptiveStage();

    /**
     * Returns the examinee's identifier associated with this CAT input.
     *
     * @return the examinee's identifier
     */
    String getExamineeId();

    /**
     * Returns the list of item identifiers that have already been administered. Item identifers are ordered
     * in their administration sequence.
     *
     * @return a list of item identifiers that have already been administered
     */
    List<String> getItemsAdmin();

    /**
     * Returns the array of item scores for items administered.
     *
     * @return an array of item scores
     */
    ItemScores getItemScores();

    /**
     * Returns the item pool data set associated with the CAT input, which includes item id, passage id, item IRT parameters,
     * and scaling D constants.
     *
     * @return a {@link PrimitiveArraySet} representing the item pool data set
     */
    PrimitiveArraySet getItemPoolDataSet();

    /**
     * Returns the passage indices in the sequence complying with the order of item administrations.
     *
     * @return a list of passage indices
     */
    List<Integer> getAdministeredPassageIndexSequence();

    /**
     * Returns the passage or item eligibility data from exposure rate control.
     *
     * @return a {@link PassageOrItemEligibilityOverall} containing the eligibility data
     */
    PassageOrItemEligibilityOverall getPassageOrItemEligibilityOverall();

    /**
     * Returns identifiers of items to be administered, ordered in the expected administration sequence.
     *
     * @return a list of item identifiers to be administered.
     */
    List<String> getItemsToAdminister();

    /**
     * Returns the shadow test. Items are not ordered in the shadow test.
     *
     * @return a list of item identifiers constituting the shadow test.
     */
    List<String> getShadowTest();

    /**
     * Returns the item identifiers included in the item pool.
     *
     * @return a list of item identifiers included in the item pool
     */
    List<String> getItemIds();

    /**
     * Returns the item parameter samples generated from prior distributions.
     *
     * @return a 3-dimension array containing item parameter samples,
     *         defined as [item row index][parameter type: 0 for A, 1 for B, 2 for C][sample index].
     */
    double[][][] getParaSamples();

    /**
     * The theta estimate computed from the previous adaptive stage. For the first stage,
     * the initial theta is returned.
     *
     * @return the theta estimate from the previous adaptive stage
     */
    double getPreviousTheta();

    /**
     * The standard error associated with the previous theta estimate.
     * For the first stage, the initial standard error is returned.
     *
     * @return the standard error associated with the previous theta estimate
     */
    double getPreviousThetaSe();

}
