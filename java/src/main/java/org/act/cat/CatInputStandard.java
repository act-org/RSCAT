package org.act.cat;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.act.testdef.Item;
import org.act.testdef.TestConfig;
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
    private final double previousTheta;
    private final double previousThetaSe;
    private CatInputStandard(CatConfig catConfig, TestConfig testConfig, int completedCount, int adaptiveStage,
            String studentId, List<String> itemsAdmin, ItemScores itemScores, List<Integer> passageRowIndexSequence,
            ExposureControlData exposureControlData, PassageOrItemEligibilityOverall passageOrItemEligibilityOverall,
            List<String> itemsToAdminister, List<String> shadowTest, double previousTheta, double previousThetaSe) {
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
        return getPassageOrItemEligibility();
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

    /**
     * Builder class for {@link CatInputStandard}.
     */
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
        private double previousTheta;
        private double previousThetaSe;

        /**
         * Adds CAT configuration settings.
         *
         * @param aCatConfig an instance of {@link CatConfig} as the CAT configuration
         *                   settings
         * @return the {@code CatConfig} builder
         */
        public Builder catConfig(CatConfig aCatConfig) {
            this.catConfig = aCatConfig;
            return this;
        }

        /**
         * Adds test configuration settings.
         *
         * @param aTestConfig an instance of {@link TestConfig} as the test
         *                    configuration settings
         * @return the builder for {@link CatInputStandard}
         */
        public Builder testConfig(TestConfig aTestConfig) {
            this.testConfig = aTestConfig;
            return this;
        }

        /**
         * Adds the number of items administered.
         *
         * @param aCompletedCount the number of items administered
         * @return the builder for {@link CatInputStandard}
         */
        public Builder completedCount(int aCompletedCount) {
            this.completedCount = aCompletedCount;
            return this;
        }

        /**
         * Adds the adaptive stage index.
         *
         * @param anAdaptiveStage the adaptive stage index
         * @return the builder for {@link CatInputStandard}
         */
        public Builder adaptiveStage(int anAdaptiveStage) {
            this.adaptiveStage = anAdaptiveStage;
            return this;
        }

        /**
         * Adds the student identification string.
         *
         * @param aStudentId the student identification string
         * @return the builder for {@link CatInputStandard}
         */
        public Builder studentId(String aStudentId) {
            this.studentId = aStudentId;
            return this;
        }

        /**
         * Adds the array of item ids for administered items
         *
         * @param newItemsAdmin the array of item ids for administered items
         * @return the builder for {@link CatInputStandard}
         */
        public Builder itemsAdmin(List<String> newItemsAdmin) {
            this.itemsAdmin = newItemsAdmin;
            return this;
        }

        /**
         * Adds the array of item scores for administered items.
         *
         * @param newItemScores the array of item scores for administered items.
         * @return the builder for {@link CatInputStandard}
         */
        public Builder itemScores(ItemScores newItemScores) {
            this.itemScores = newItemScores;
            return this;
        }

        /**
         * Adds the sequence of administered passages.
         *
         * @param newAdministeredPassageIndexSequence a {@code List} of integers that
         *                                         specifies the sequence of
         *                                         administered passages.
         * @return the builder for {@link CatInputStandard}
         */
        public Builder administeredPassageIndexSequence(List<Integer> newAdministeredPassageIndexSequence) {
            this.administeredPassageIndexSequence = newAdministeredPassageIndexSequence;
            return this;
        }

        /**
         * Adds the exposure control data.
         *
         * @param anExposureControlData an instance of {@link ExposureControlData} that
         *                            includes the exposure control data
         * @return the builder for {@link CatInputStandard}
         */
        public Builder exposureControlData(ExposureControlData anExposureControlData) {
            this.exposureControlData = anExposureControlData;
            return this;
        }

        /**
         * Adds eligibility indicators for a particular student.
         *
         * @param aPassageOrItemEligibilityOverall the eligibility indicators for a
         *                                        particular student
         * @return the builder for {@link CatInputStandard}
         */
        public Builder passageOrItemEligibilityOverall(
                PassageOrItemEligibilityOverall aPassageOrItemEligibilityOverall) {
            this.passageOrItemEligibilityOverall = aPassageOrItemEligibilityOverall;
            return this;
        }

        /**
         * Adds the list of items to be administered.
         *
         * @param newItemsToAdminister the list of {@code String} characters as the
         *                          identifiers of items to be administered
         * @return the builder for {@link CatInputStandard}
         */
        public Builder itemsToAdminister(List<String> newItemsToAdminister) {
            this.itemsToAdminister = newItemsToAdminister;
            return this;
        }

        /**
         * Adds the shadow test.
         *
         * @param aShadowTest the list of {@code String} characters as the identifiers of
         *                   items in the shadow test
         * @return the builder for {@link CatInputStandard}
         */
        public Builder shadowTest(List<String> aShadowTest) {
            this.shadowTest = aShadowTest;
            return this;
        }

        /**
         * Adds the previous theta value.
         *
         * @param aPreviousTheta the previous theta value
         * @return the builder for {@link CatInputStandard}
         */
        public Builder previousTheta(double aPreviousTheta) {
            this.previousTheta = aPreviousTheta;
            return this;
        }

        /**
         * Adds the previous theta se value.
         *
         * @param aPreviousThetaSe the previous theta se value
         * @return the builder for {@link CatInputStandard}
         */
        public Builder previousThetaSe(double aPreviousThetaSe) {
            this.previousThetaSe = aPreviousThetaSe;
            return this;
        }

        /**
         * Builds a {@link CatInputStandard}.
         *
         * @return an instance of {@code CatInputStandard}
         */
        public CatInputStandard build() {
            CatInputStandard catInputStandard = new CatInputStandard(catConfig, testConfig, completedCount,
                    adaptiveStage, studentId, itemsAdmin, itemScores, administeredPassageIndexSequence,
                    exposureControlData, passageOrItemEligibilityOverall, itemsToAdminister, shadowTest, previousTheta,
                    previousThetaSe);

            return catInputStandard;
        }

    }
}
