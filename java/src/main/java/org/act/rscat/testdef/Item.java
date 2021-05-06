package org.act.rscat.testdef;

import java.util.List;

/**
 * This class defines metadata of an item for CAT.
 */
public class Item extends AbstractTestEntity {

    /**
     * Defines column names of the item pool csv file.
     */
    public enum ColumnName {

        /**
         * Item identifiers.
         */
        ITEM_ID("Item ID"),

        /**
         * Passage identifiers.
         */
        ITEM_PASSAGE_ID("Passage ID"),

        /**
         * A parameter for 3PL IRT model.
         */
        A_PARAM("A-Param"),

        /**
         * B parameter for 3PL IRT model.
         */
        B_PARAM("B-Param"),

        /**
         * C parameter for 3PL IRT model.
         */
        C_PARAM("C-Param"),

        /**
         * Standard error of A parameter for 3PL IRT model.
         */
        A_PARAM_SE("A-Param-SE"),

        /**
         * Standard error of B parameter for 3PL IRT model.
         */
        B_PARAM_SE("B-Param-SE"),

        /**
         * Standard error of C parameter for 3PL IRT model.
         */
        C_PARAM_SE("C-Param-SE"),

        /**
         * Scaling constant.
         */
        D_CONSTANT("D-Constant");
        private String colName;

        ColumnName(String colName) {
            this.colName = colName;
        }

        /**
         * Returns the column name.
         *
         * @return the column name
         */
        public String getColName() {
            return colName;
        }
    }

    /**
     * Construct a new {@link Item}.
     *
     * @param id               an item identifier
     * @param rowData          a {@code List} collection of {@code String} as the
     *                         row data of the item in the csv
     * @param columnNames      a {@code List} collection of {@code String} as column
     *                         names of the item attributes
     * @param isNumericColumns an array of {@code boolean} indicators representing
     *                         if item attribute columns are numeric or not. True,
     *                         if a column is numeric; false, otherwise
     * @param rowIndex         a row index
     */
    public Item(String id, List<String> rowData, List<String> columnNames, boolean[] isNumericColumns, int rowIndex) {
        super(id, rowData, columnNames, isNumericColumns, rowIndex);
    }
}
