package org.act.cat;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.act.testdef.Item;
import org.act.testdef.TestConfig;
import org.act.util.ContentTable;
import org.act.util.PrimitiveArraySet;

/**
 * An implementation of {@link CatInput} for the standard CAT engine.
 */
public class CatInputStandard implements CatInput {

    private final CatConfig catConfig;
    private final TestConfig testConfig;
    private int completedCount;
    private int adaptiveStage;
    private final String examineeId;
    private List<String> itemsAdmin;
    private ItemScores itemScores;
    /**
     * The item pool data table
     */
    private final PrimitiveArraySet itemPoolDataSet;
    private final List<Integer> administeredPassageIndexSequence;
    private final ExposureControlData exposureControlData;
    private final PassageOrItemEligibilityOverall passageOrItemEligibilityOverall;
    private final List<String> itemsToAdminister;
    private final List<String> shadowTest;
    private final double[][][] paraSamples;
    private final double previousTheta;
    private final double previousThetaSe;

    /**
     * Defines fields for internal standard cat input data object.
     *
     * @param catConfig object with test configuration settings
     * @param testConfig
     * @param completedCount number of items administered
     * @param adaptiveStage the adaptive stage index
     * @param studentId the student identification string
     * @param itemsAdmin an array of item ids for items administered
     * @param itemScores an array of item scores for items administered
     * @param exposureControlData the aggregated exposure control counts for
     *            item or passage usage
     * @param passageOrItemEligibilityOverall eligibility indicators for each
     *            theta range for a particular student
     * @param enemyItemsEnabled flag indicating whether enemy items are examined
     */

    private CatInputStandard(CatConfig catConfig, TestConfig testConfig, int completedCount, int adaptiveStage,
            String studentId, List<String> itemsAdmin, ItemScores itemScores, List<Integer> passageRowIndexSequence,
            ExposureControlData exposureControlData, PassageOrItemEligibilityOverall passageOrItemEligibilityOverall,
            List<String> itemsToAdminister, List<String> shadowTest, double[][][] paraSamples, double previousTheta,
            double previousThetaSe) {
        this.catConfig = catConfig;
        this.testConfig = testConfig;
        this.completedCount = completedCount;
        this.adaptiveStage = adaptiveStage;
        this.examineeId = studentId;
        this.itemsAdmin = itemsAdmin;
        this.itemScores = itemScores;
        this.itemPoolDataSet = convertItemPoolDataSet();
        this.administeredPassageIndexSequence = passageRowIndexSequence;
        this.exposureControlData = exposureControlData;
        this.passageOrItemEligibilityOverall = passageOrItemEligibilityOverall;
        this.itemsToAdminister = itemsToAdminister;
        this.shadowTest = shadowTest;
        this.paraSamples = paraSamples;
        this.previousTheta = previousTheta;
        this.previousThetaSe = previousThetaSe;
    }

    @Override
    public CatConfig getCatConfig() {
        return catConfig;
    }

    @Override
    public TestConfig getTestConfig() {
        return testConfig;
    }

    @Override
    public int getCompletedCount() {
        return completedCount;
    }

    @Override
    public int getAdaptiveStage() {
        return adaptiveStage;
    }

    @Override
    public String getExamineeId() {
        return examineeId;
    }

    @Override
    public List<String> getItemsAdmin() {
        return itemsAdmin;
    }

    @Override
    public ItemScores getItemScores() {
        return itemScores;
    }

    @Override
    public ExposureControlData getExposureControlData() {
        return exposureControlData;
    }

    @Override
    public PassageOrItemEligibilityOverall getPassageOrItemEligibility() {
        return passageOrItemEligibilityOverall;
    }

    @Override
    public List<Integer> getAdministeredPassageIndexSequence() {
        return administeredPassageIndexSequence;
    }

    @Override
    public PassageOrItemEligibilityOverall getPassageOrItemEligibilityOverall() {
        return passageOrItemEligibilityOverall;
    }

    @Override
    public List<String> getItemsToAdminister() {
        return itemsToAdminister;
    }

    @Override
    public List<String> getShadowTest() {
        return shadowTest;
    }

    @Override
    public List<String> getItemIds() {
        return Arrays.asList(itemPoolDataSet.getStringArrayCopy(Item.ColumnName.ITEM_ID.getColName()));
    }

    @Override
    public double getPreviousTheta() {
        return previousTheta;
    }

    @Override
    public double getPreviousThetaSe() {
        return previousThetaSe;
    }

    public CatInputStandard withAdministeredPassageIndexSequence(List<Integer> passageRowIndexSequence) {
        return new CatInputStandard(catConfig, testConfig, completedCount, adaptiveStage, examineeId, itemsAdmin,
                itemScores, passageRowIndexSequence, exposureControlData, passageOrItemEligibilityOverall,
                itemsToAdminister, shadowTest, paraSamples, previousTheta, previousThetaSe);
    }

    @Override
    public double[][][] getParaSamples() {
        return paraSamples;
    }

    @Override
    public PrimitiveArraySet getItemPoolDataSet() {
        return itemPoolDataSet;
    }

    /**
     * Converts the item pool {@link ContentTable.RowOriented} to
     * {@link PrimitiveArraySet}.
     *
     * @return the item pool {@code PrimitiveArraySet} data
     */
    private PrimitiveArraySet convertItemPoolDataSet() {
        Map<String, Class<?>> columnTypes = new HashMap<>();
        columnTypes.put(Item.ColumnName.ITEM_ID.getColName(), String[].class);
        columnTypes.put(Item.ColumnName.ITEM_PASSAGE_ID.getColName(), String[].class);
        columnTypes.put(Item.ColumnName.A_PARAM.getColName(), double[].class);
        columnTypes.put(Item.ColumnName.A_PARAM_SE.getColName(), double[].class);
        columnTypes.put(Item.ColumnName.B_PARAM.getColName(), double[].class);
        columnTypes.put(Item.ColumnName.B_PARAM_SE.getColName(), double[].class);
        columnTypes.put(Item.ColumnName.C_PARAM.getColName(), double[].class);
        columnTypes.put(Item.ColumnName.C_PARAM_SE.getColName(), double[].class);
        columnTypes.put(Item.ColumnName.D_CONSTANT.getColName(), double[].class);
        return PrimitiveArraySet.fromContentTable(testConfig.getItemPoolTable(), columnTypes);
    }

    public static class Builder {

        private CatConfig catConfig;
        private TestConfig testConfig;
        private int completedCount;
        private int adaptiveStage;
        private String studentId;
        private List<String> itemsAdmin;
        private ItemScores itemScores;
        private List<Integer> administeredPassageIndexSequence;
        private ExposureControlData exposureControlData;
        private PassageOrItemEligibilityOverall passageOrItemEligibilityOverall;
        private List<String> itemsToAdminister;
        private List<String> shadowTest;
        private double[][][] paraSamples;
        private double previousTheta;
        private double previousThetaSe;

        public Builder catConfig(CatConfig catConfig) {
            this.catConfig = catConfig;
            return this;
        }

        public Builder testConfig(TestConfig testConfig) {
            this.testConfig = testConfig;
            return this;
        }

        public Builder completedCount(int completedCount) {
            this.completedCount = completedCount;
            return this;
        }

        public Builder adaptiveStage(int adaptiveStage) {
            this.adaptiveStage = adaptiveStage;
            return this;
        }

        public Builder studentId(String studentId) {
            this.studentId = studentId;
            return this;
        }

        public Builder itemsAdmin(List<String> itemsAdmin) {
            this.itemsAdmin = itemsAdmin;
            return this;
        }

        public Builder itemScores(ItemScores itemScores) {
            this.itemScores = itemScores;
            return this;
        }

        public Builder administeredPassageIndexSequence(List<Integer> administeredPassageIndexSequence) {
            this.administeredPassageIndexSequence = administeredPassageIndexSequence;
            return this;
        }

        public Builder exposureControlData(ExposureControlData exposureControlData) {
            this.exposureControlData = exposureControlData;
            return this;
        }

        public Builder passageOrItemEligibilityOverall(
                PassageOrItemEligibilityOverall passageOrItemEligibilityOverall) {
            this.passageOrItemEligibilityOverall = passageOrItemEligibilityOverall;
            return this;
        }

        public Builder itemsToAdminister(List<String> itemsToAdminister) {
            this.itemsToAdminister = itemsToAdminister;
            return this;
        }

        public Builder shadowTest(List<String> shadowTest) {
            this.shadowTest = shadowTest;
            return this;
        }

        public Builder paraSamples(double[][][] paraSamples) {
            this.paraSamples = paraSamples;
            return this;
        }

        public Builder previousTheta(double previousTheta) {
            this.previousTheta = previousTheta;
            return this;
        }

        public Builder previousThetaSe(double previousThetaSe) {
            this.previousThetaSe = previousThetaSe;
            return this;
        }

        public CatInputStandard build() {
            CatInputStandard catInputStandard = new CatInputStandard(catConfig, testConfig, completedCount,
                    adaptiveStage, studentId, itemsAdmin, itemScores, administeredPassageIndexSequence,
                    exposureControlData, passageOrItemEligibilityOverall, itemsToAdminister, shadowTest, paraSamples,
                    previousTheta, previousThetaSe);

            return catInputStandard;
        }

    }

}
