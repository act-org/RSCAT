package org.act.rscat.sim;

import java.util.List;
import java.util.Map;

import org.act.rscat.cat.ItemScores;
import org.act.rscat.cat.PassageOrItemEligibilityAtThetaRange;
import org.act.rscat.cat.ThetaEst;
import org.act.rscat.cat.ThetaRange;

/**
 * This class defines the CAT simulation output for an individual examinee.
 */
public class SimOutput {
    private final String examineeId;
    private final double trueTheta;
    private final List<String> itemsAdministered;
    private final List<ItemScores> itemScoresList;
    private final double initTheta;
    private final ThetaEst finalTheta;
    private final List<PassageOrItemEligibilityAtThetaRange> passageOrItemEligibilityAtThetaRangeList;
    private final List<List<String>> shadowTestList;
    private final List<ThetaEst> thetaEstList;
    private final List<Double> catEngineTimeList;
    private final List<Integer> adaptiveStageList;
    private final List<List<Integer>> passageRowIndexSequences;
    private Map<ThetaRange, Map<String, Double>> itemExposureRates;

    private SimOutput(String examineeId, double trueTheta, List<String> itemsAdministered,
            List<ItemScores> itemScoresList, double initTheta, ThetaEst finalTheta,
            List<PassageOrItemEligibilityAtThetaRange> passageOrItemEligibilityAtThetaRangeList,
            List<List<String>> shadowTestList, List<ThetaEst> thetaEstList, List<Double> catEngineTimeList,
            List<Integer> adaptiveStageList, List<List<Integer>> passageRowIndexSequences,
            Map<ThetaRange, Map<String, Double>> itemExposureRates) {
        this.examineeId = examineeId;
        this.trueTheta = trueTheta;
        this.itemsAdministered = itemsAdministered;
        this.itemScoresList = itemScoresList;
        this.initTheta = initTheta;
        this.finalTheta = finalTheta;
        this.passageOrItemEligibilityAtThetaRangeList = passageOrItemEligibilityAtThetaRangeList;
        this.shadowTestList = shadowTestList;
        this.thetaEstList = thetaEstList;
        this.catEngineTimeList = catEngineTimeList;
        this.adaptiveStageList = adaptiveStageList;
        this.passageRowIndexSequences = passageRowIndexSequences;
        this.itemExposureRates = itemExposureRates;
    }

    /**
     * Returns the identifier of the examinee.
     *
     * @return the identifier of the examinee
     */
    public String getexamineeId() {
        return examineeId;
    }

    /**
     * Returns the true theta value of the examinee.
     *
     * @return the true theta value of the exaimnee
     */
    public double getTrueTheta() {
        return trueTheta;
    }

    /**
     * Returns the identifiers of administered items.
     *
     * @return the identifiers of administered items
     */
    public List<String> getItemsAdministered() {
        return itemsAdministered;
    }

    /**
     * Returns the list of scores of the administered items.
     *
     * @return the list of scores
     */
    public List<ItemScores> getItemScoresList() {
        return itemScoresList;
    }

    /**
     * Returns the initial theta before the first theta estimate update.
     *
     * @return the initial theta
     */
    public double getInitTheta() {
        return initTheta;
    }

    /**
     * Returns the final theta estimate.
     *
     * @return the final theta estimate
     */
    public ThetaEst getFinalTheta() {
        return finalTheta;
    }

    /**
     * Returns the list of {@link PassageOrItemEligibilityAtThetaRange}.
     *
     * @return the list of {@code PassageOrItemEligibilityAtThetaRange}
     */
    public List<PassageOrItemEligibilityAtThetaRange> getPassageOrItemEligibilityAtThetaRangeList() {
        return passageOrItemEligibilityAtThetaRangeList;
    }

    /**
     * Returns the list of shadow tests for all adaptive stages. Each sub-list
     * represents the item identifiers in the shadow test.
     *
     * @return the list of shadow tests
     */
    public List<List<String>> getShadowTestList() {
        return shadowTestList;
    }

    /**
     * Returns the list of theta estimates for all adaptive stages.
     *
     * @return the list of theta estimates
     * @see ThetaEst
     */
    public List<ThetaEst> getThetaEstList() {
        return thetaEstList;
    }

    /**
     * Returns the list of CAT engine time for all adaptive stages, in seconds.
     *
     * @return the list of CAT engine time
     */
    public List<Double> getCatEngineTimeList() {
        return catEngineTimeList;
    }

    /**
     * Returns the list of adaptive stage indices.
     *
     * @return the list of adaptive stage indices
     */
    public List<Integer> getAdaptiveStageList() {
        return adaptiveStageList;
    }

    /**
     * Returns the list of passage id sequence for all adaptive stages.
     *
     * @return the list of passage id sequence
     */
    public List<List<Integer>> getPassageRowIndexSequences() {
        return passageRowIndexSequences;
    }

    /**
     * Returns the item exposure control rate data defined in a {@code Map}.
     *
     * @return the item exposure control rate data
     */
    public Map<ThetaRange, Map<String, Double>> getItemExposureRates() {
        return itemExposureRates;
    }

    /**
     * Sets the item exposure rate data.
     *
     * @param itemExposureRates the item exposure rate data
     */
    public void setItemExposureRates(Map<ThetaRange, Map<String, Double>> itemExposureRates) {
        this.itemExposureRates = itemExposureRates;
    }

    /**
     * The builder class for {@link SimOutput}.
     */
    public static class Builder {

        // Required fields
        private final String examineeId;
        private final double trueTheta;

        // Optional fields
        private List<String> itemsAdministered;
        private List<ItemScores> itemScoresList;
        private double initTheta;
        private ThetaEst finalTheta;
        private List<PassageOrItemEligibilityAtThetaRange> passageOrItemEligibilityAtThetaRangeList;
        private List<List<String>> shadowTestList;
        private List<ThetaEst> thetaEstList;
        private List<Double> catEngineTimeList;
        private List<Integer> adaptiveStageList;
        private List<List<Integer>> passageRowIndexSequences;
        private Map<ThetaRange, Map<String, Double>> itemExposureRates;

        /**
         * Constructs a new {@link Builder} with required fields.
         *
         * @param examineeId the identifier of the examinee
         * @param trueTheta the true ability of the examinee
         */
        public Builder(String examineeId, double trueTheta) {
            this.examineeId = examineeId;
            this.trueTheta = trueTheta;
        }

        /**
         * Adds the itemsAdministered
         *
         * @param newItemsAdministered the itemsAdministered
         * @return this builder
         */
        public Builder itemsAdministered(List<String> newItemsAdministered) {
            this.itemsAdministered = newItemsAdministered;
            return this;
        }

        /**
         * Adds the itemScoresList.
         *
         * @param newItemScoresList the itemScoresList
         * @return this builder
         */
        public Builder itemScoresList(List<ItemScores> newItemScoresList) {
            this.itemScoresList = newItemScoresList;
            return this;
        }

        /**
         * Adds the initTheta.
         *
         * @param newInitTheta the initTheta
         * @return this builder
         */
        public Builder initTheta(double newInitTheta) {
            this.initTheta = newInitTheta;
            return this;
        }

        /**
         * Adds the finalTheta
         *
         * @param newFinalTheta the finalTheta
         * @return this builder
         */
        public Builder finalTheta(ThetaEst newFinalTheta) {
            this.finalTheta = newFinalTheta;
            return this;
        }

        /**
         * Adds the passageOrItemEligibilityAtThetaRangeList
         *
         * @param newPassageOrItemEligibilityAtThetaRangeList the passageOrItemEligibilityAtThetaRangeList
         * @return this builder
         */
        public Builder itemEligibilityList(
                List<PassageOrItemEligibilityAtThetaRange> newPassageOrItemEligibilityAtThetaRangeList) {
            this.passageOrItemEligibilityAtThetaRangeList = newPassageOrItemEligibilityAtThetaRangeList;
            return this;
        }

        /**
         * Adds the shadowTestList
         *
         * @param newShadowTestList the shadowTestList
         * @return this builder
         */
        public Builder shadowTestList(List<List<String>> newShadowTestList) {
            this.shadowTestList = newShadowTestList;
            return this;
        }

        /**
         * Adds the thetaEstList
         *
         * @param newThetaEstList the thetaEstList
         * @return this builder
         */
        public Builder thetaEstList(List<ThetaEst> newThetaEstList) {
            this.thetaEstList = newThetaEstList;
            return this;
        }

        /**
         * Adds the catEngineTimeList
         *
         * @param newCatEngineTimeList the catEngineTimeList
         * @return this builder
         */
        public Builder catEngineTimeList(List<Double> newCatEngineTimeList) {
            this.catEngineTimeList = newCatEngineTimeList;
            return this;
        }

        /**
         * Adds the adaptiveStageList
         *
         * @param newAdaptiveStageList the adaptiveStageList
         * @return this builder
         */
        public Builder adaptiveStageList(List<Integer> newAdaptiveStageList) {
            this.adaptiveStageList = newAdaptiveStageList;
            return this;
        }

        /**
         * Adds the passageRowIndexSequences
         *
         * @param newPassageRowIndexSequences the passageRowIndexSequences
         * @return this builder
         */
        public Builder passageRowIndexSequences(List<List<Integer>> newPassageRowIndexSequences) {
            this.passageRowIndexSequences = newPassageRowIndexSequences;
            return this;
        }

        /**
         * Builds a new {@link SimOutput}.
         *
         * @return an instance of {@code SimOutput}
         */
        public SimOutput build() {
            return new SimOutput(examineeId, trueTheta, itemsAdministered, itemScoresList, initTheta, finalTheta,
                    passageOrItemEligibilityAtThetaRangeList, shadowTestList, thetaEstList, catEngineTimeList,
                    adaptiveStageList, passageRowIndexSequences, itemExposureRates);

        }

    }
}
