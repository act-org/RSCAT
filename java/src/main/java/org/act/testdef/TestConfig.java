package org.act.testdef;

import org.act.util.ContentTable;

/**
 * This class defines the required and optional fields for a test configuration.
 * <p>
 * The builder pattern is implemented to construct a {@code TestConfig} object.
 * Optimal fields are set with default values, if not specified by users.
 */
public class TestConfig {
    // Required fields for a test configuration.
    /**
     * The test configuration id.
     */
    private String testConfigId;

    /**
     * The number of items in the test.
     */
    private int testLength;

    /**
     * The boolean array indicating if columns in the item pool are numeric.
     */
    private boolean[] itemNumericColumn;

    /**
     * The item pool ContentTable object.
     */
    private ContentTable itemPoolTable;

    // Optional fields for a test configuration.
    /**
     * The lower bound of passage number.
     */
    private int numPassageLB;

    /**
     * The upper bound of passage number.
     */
    private int numPassageUB;

    /**
     * The lower bound of number of items per passage.
     */
    private int numItemPerPassageLB;

    /**
     * The upper bound of number of items per passage.
     */
    private int numItemPerPassageUB;

    /**
     * The item identifier column index.
     */
    private int itemIdColumnIndex;

    /**
     * The passage identifier column index in the item pool.
     */
    private int passageIdColumnIndexItemPool;

    /**
     * The passage identifier column index in the passage pool.
     */
    private int passageIdColumnIndexPassagePool;

    /**
     * The boolean array indicating if columns in the passage pool are numeric.
     */
    private boolean[] passageNumericColumn;

    /**
     * The passage ContentTable object.
     */
    private ContentTable passageTable;

    /**
     * The constraint ContentTable object.
     */
    private ContentTable constraintTable;

    /**
     * The priority value of the test length constraint.
     */
    private int lengthPriority;

    /**
     * The priority value of the item eligibility constraint.
     */
    private int eligibilityPriority;

    /**
     * The boolean indicator specifying whether the energy item constraint is
     * enabled.
     */
    private boolean enableEnemyItemConstraint;

    /**
     * Constructs a new {@link TestConfig} from an instance of {@link Builder}.
     *
     * @param testConfigBuilder the instance of {@code TestConfigBuilder}
     */
    private TestConfig(Builder testConfigBuilder) {
        testConfigId = testConfigBuilder.testConfigId;
        testLength = testConfigBuilder.testLength;
        numPassageLB = testConfigBuilder.numPassageLB;
        numPassageUB = testConfigBuilder.numPassageUB;
        numItemPerPassageLB = testConfigBuilder.numItemPerPassageLB;
        numItemPerPassageUB = testConfigBuilder.numItemPerPassageUB;
        itemPoolTable = testConfigBuilder.itemPoolTable;
        passageTable = testConfigBuilder.passageTable;
        constraintTable = testConfigBuilder.constraintTable;
        itemIdColumnIndex = testConfigBuilder.itemIdColumnIndex;
        passageIdColumnIndexItemPool = testConfigBuilder.passageIdColumnIndexItemPool;
        passageIdColumnIndexPassagePool = testConfigBuilder.passageIdColumnIndexPassagePool;
        itemNumericColumn = testConfigBuilder.itemNumericColumn;
        passageNumericColumn = testConfigBuilder.passageNumericColumn;
        lengthPriority = testConfigBuilder.lengthPriority;
        eligibilityPriority = testConfigBuilder.eligibilityPriority;
        enableEnemyItemConstraint = testConfigBuilder.enableEnemyItemConstraint;
    }

    /**
     * Returns the integer number test length (required number of items).
     *
     * @return the test length
     */
    public int getTestLength() {
        return testLength;
    }

    /**
     * Returns the item pool {@link ContentTable}.
     *
     * @return the item pool content table
     */
    public ContentTable getItemPoolTable() {
        return itemPoolTable;
    }

    /**
     * Returns the item identifier column index in the item pool content table.
     *
     * @return the item identifier column index in the item pool content table
     */
    public int getItemIdColumnIndex() {
        return itemIdColumnIndex;
    }

    /**
     * Returns the passage identifier column index in the item pool content
     * table.
     *
     * @return the passage identifier column index in the item pool content
     *         table
     */
    public int getPassageIdColumnIndexItemPool() {
        return passageIdColumnIndexItemPool;
    }

    /**
     * Returns the boolean array indicators of numeric item pool columns.
     *
     * @return the boolean array indicators of numeric item pool columns
     */
    public boolean[] getItemNumericColumn() {
        return itemNumericColumn;
    }

    /**
     * Returns the constraint {@link ContentTable} object.
     *
     * @return the constraint <code>ContentTable</code> object
     */
    public ContentTable getConstraintTable() {
        return constraintTable;
    }

    /**
     * Returns the test length constraint priority.
     *
     * @return the test length constraint priority
     */
    public int getLengthPriority() {
        return lengthPriority;
    }

    /**
     * Returns the test eligibility constraint priority.
     *
     * @return the test eligibility constraint priority
     */
    public int getEligibilityPriority() {
        return eligibilityPriority;
    }

    /**
     * Returns the passage {@link ContentTable} object.
     *
     * @return the passage <code>ContentTable</code> object
     */
    public ContentTable getPassageTable() {
        return passageTable;
    }

    /**
     * Returns the passage identifier column index in the passage table.
     *
     * @return the passage identifier column index
     */
    public int getPassageIdColumnIndexPassagePool() {
        return passageIdColumnIndexPassagePool;
    }

    /**
     * Returns the numeric passage column boolean marks.
     *
     * @return the numeric passage column boolean marks
     */
    public boolean[] getPassageNumericColumn() {
        return passageNumericColumn;
    }

    /**
     * Returns the lower bound of passage number.
     *
     * @return the lower bound of passage number
     */
    public int getNumPassageLB() {
        return numPassageLB;
    }

    /**
     * Returns the upper bound of passage number.
     *
     * @return the upper bound of passage number
     */
    public int getNumPassageUB() {
        return numPassageUB;
    }

    /**
     * Returns the boolean indicator of whether enemy item constraints are
     * enabled.
     *
     * @return the boolean indicator of whether enemy item constraints are
     *         enabled.
     */
    public boolean isEnableEnemyItemConstraint() {
        return enableEnemyItemConstraint;
    }

    /**
     * Returns the lower bound of number of items per passage.
     *
     * @return the lower bound of number of items per passage.
     */
    public int getNumItemPerPassageLB() {
        return numItemPerPassageLB;
    }

    /**
     * Returns the upper bound of number of items per passage.
     *
     * @return the upper bound of number of items per passage
     */
    public int getNumItemPerPassageUB() {
        return numItemPerPassageUB;
    }

    /**
     * Returns the test configuration identifier.
     *
     * @return the test configuration identifier
     */
    public String getTestConfigId() {
        return testConfigId;
    }

    /**
     * {@code TestConfigBuilder} is used to build an instance of
     * {@link TestConfig}.
     */
    public static class Builder {
        /*
         * See definition in TestConfig.
         */
        // CHECKSTYLE: stop JavadocVariable
        // Required fields
        private final int testLength;
        private final boolean[] itemNumericColumn;
        private final ContentTable itemPoolTable;

        // Optional fields
        private String testConfigId;
        private int numPassageLB = 0;
        private int numPassageUB = 100;
        private int numItemPerPassageLB = 0;
        private int numItemPerPassageUB = 100;
        private int itemIdColumnIndex = 0;
        private int passageIdColumnIndexItemPool = 1;
        private int passageIdColumnIndexPassagePool = 0;
        private boolean[] passageNumericColumn;
        private ContentTable passageTable = ContentTable.rowOriented();
        private ContentTable constraintTable = ContentTable.rowOriented();
        private int lengthPriority = 10;
        private int eligibilityPriority = 0;
        private boolean enableEnemyItemConstraint = true;

        // CHECKSTYLE: resume JavadocVariable

        /**
         * Constructs a new {@link Builder}.
         *
         * @param testLength the test length
         * @param itemNumericColumn the boolean array indicators of numeric
         *            columns in the item pool
         * @param itemPoolTable the item pool {@link ContentTable}
         */
        public Builder(int testLength, boolean[] itemNumericColumn, ContentTable itemPoolTable) {
            this.testLength = testLength;
            this.itemNumericColumn = itemNumericColumn;
            this.itemPoolTable = itemPoolTable;
        }

        // CHECKSTYLE: stop HiddenField

        /**
         * Sets the lower bound of the number of passages.
         *
         * @param numPassageLB the lower bound of the number of passages
         * @return this {@code TestConfig.Builder}
         */
        public Builder numPassageLB(int numPassageLB) {
            this.numPassageLB = numPassageLB;
            return this;
        }

        /**
         * Sets the upper bound of the number of passages.
         *
         * @param numPassageUB the upper bound of the number of bassages
         * @return this {@code TestConfig.Builder}
         */
        public Builder numPassageUB(int numPassageUB) {
            this.numPassageUB = numPassageUB;
            return this;
        }

        /**
         * Sets the lower bound of the number of items per passage.
         *
         * @param numItemPerPassageLB the lower bound of the number of items per
         *            passage.
         * @return this {@code TestConfig.Builder}
         */
        public Builder numItemPerPassageLB(int numItemPerPassageLB) {
            this.numItemPerPassageLB = numItemPerPassageLB;
            return this;
        }

        /**
         * Sets the upper bound of the number of items per passage.
         *
         * @param numItemPerPassageUB the upper bound of the number of items per
         *            passage
         * @return this {@code TestConfig.Builder}
         */
        public Builder numItemPerPassageUB(int numItemPerPassageUB) {
            this.numItemPerPassageUB = numItemPerPassageUB;
            return this;
        }

        /**
         * Sets the passage table.
         *
         * @param passageTable the passage {@link ContentTable}
         * @return this {@code TestConfig.Builder}
         */
        public Builder passageTable(ContentTable passageTable) {
            this.passageTable = passageTable;
            return this;
        }

        /**
         * Sets the constraint table.
         *
         * @param constraintTable the constraint {@link ContentTable} table
         * @return this {@code TestConfig.Builder}
         */
        public Builder constraintTable(ContentTable constraintTable) {
            this.constraintTable = constraintTable;
            return this;
        }

        /**
         * Sets the index of item ID column in the item table.
         *
         * @param itemIdColumnIndex the index of item ID column in the item
         *            table
         * @return this {@code TestConfig.Builder}
         */
        public Builder itemIdColumnIndex(int itemIdColumnIndex) {
            this.itemIdColumnIndex = itemIdColumnIndex;
            return this;
        }

        /**
         * Sets the index of passage ID column in the item table.
         *
         * @param passageIdColumnIndexItemPool the index of passage ID column in
         *            the item table
         * @return this {@code TestConfig.Builder}
         */
        public Builder passageIdColumnIndexItemPool(int passageIdColumnIndexItemPool) {
            this.passageIdColumnIndexItemPool = passageIdColumnIndexItemPool;
            return this;
        }

        /**
         * Sets the index of passage ID column in the passage table.
         *
         * @param passageIdColumnIndexPassagePool the index of passage ID column
         *            in the passage table
         * @return this {@code TestConfig.Builder}
         */
        public Builder passageIdColumnIndexPassagePool(int passageIdColumnIndexPassagePool) {
            this.passageIdColumnIndexPassagePool = passageIdColumnIndexPassagePool;
            return this;
        }

        /**
         * Sets the boolean array indicating if columns in the passage pool are
         * numeric.
         *
         * @param passageNumericColumn the boolean array indicating if columns
         *            in the passage pool are numeric
         * @return this {@code TestConfig.Builder}
         */
        public Builder passageNumericColumn(boolean[] passageNumericColumn) {
            this.passageNumericColumn = passageNumericColumn;
            return this;
        }

        /**
         * Sets the priority value of the test length constraint.
         *
         * @param lengthPriority the priority value of the test length
         *            constraint
         * @return this {@code TestConfig.Builder}
         */
        public Builder lengthPriority(int lengthPriority) {
            this.lengthPriority = lengthPriority;
            return this;
        }

        /**
         * Sets the priority value of the item eligibility constraint.
         *
         * @param eligibilityPriority the priority value of the item eligibility
         *            constraint
         * @return this {@code TestConfig.Builder}
         */
        public Builder eligibilityPriority(int eligibilityPriority) {
            this.eligibilityPriority = eligibilityPriority;
            return this;
        }

        /**
         * Sets the indicator to enable the enemy item constraints.
         *
         * @param enableEnemyItemConstraint the boolean indicator to enable the
         *            enemy item constraints
         * @return this {@code TestConfig.Builder}
         */
        public Builder enableEnemyItemConstraint(boolean enableEnemyItemConstraint) {
            this.enableEnemyItemConstraint = enableEnemyItemConstraint;
            return this;
        }

        /**
         * Sets the test configuration ID.
         *
         * @param testConfigId the long test configuration ID
         * @return this {@code TestConfig.Builder}
         */
        public Builder testConfigId(String testConfigId) {
            this.testConfigId = testConfigId;
            return this;
        }

        // CHECKSTYLE: resume HiddenField

        /**
         * Returns a {@code TestConfig} from the parameters set by the setter
         * methods.
         *
         * @return this {@code TestConfig.Builder}
         */
        public TestConfig build() {
            return new TestConfig(this);
        }
    }

}
