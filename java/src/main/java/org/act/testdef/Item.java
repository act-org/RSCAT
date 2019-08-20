package org.act.testdef;

import java.util.ArrayList;
import java.util.List;

/**
 * This class defines metadata of an item for CAT.
 */
public class Item {

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
     * Item identifier.
     */
    private String id;

    /**
     * Item row index in csv file.
     */
    private int rowIndex;

    /**
     * Item numeric attribute name list.
     */
    private List<String> numericAttrsNames = new ArrayList<>();

    /**
     * Item categorical attribute name list.
     */
    private List<String> categAttrsNames = new ArrayList<>();

    /**
     * Item numeric attribute list.
     */
    private List<Double> numericAttrs = new ArrayList<>();

    /**
     * Item categorical attribute list.
     */
    private List<String> categAttrs = new ArrayList<>();

    /**
     * Construct a new {@link Item}.
     *
     * @param id                 item identifier
     * @param rowData            row data in the csv table
     * @param columnNames        a list of column names
     * @param itemNumericColumns an array of numeric attribute marks
     * @param rowIndex           row index of the item
     */
    public Item(String id, List<String> rowData, List<String> columnNames, boolean[] itemNumericColumns, int rowIndex) {
        int count = 0;
        this.id = id;
        this.rowIndex = rowIndex;
        for (boolean mark : itemNumericColumns) {
            if (mark) {
                numericAttrsNames.add(columnNames.get(count));
                numericAttrs.add(Double.parseDouble(rowData.get(count)));
            } else {
                categAttrsNames.add(columnNames.get(count));
                categAttrs.add(rowData.get(count));
            }
            count++;
        }
    }

    /**
     * Returns the identifier of the item.
     *
     * @return the item identifier
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the item row index.
     *
     * @return the item row index
     */
    public int getRowIndex() {
        return rowIndex;
    }

    /**
     * Returns the item numeric attribute names.
     *
     * @return the item numeric attribute names.
     */
    public List<String> getNumericAttrsNames() {
        return numericAttrsNames;
    }

    /**
     * Returns the item categorical attribute names.
     *
     * @return the item categorical attribute names.
     */
    public List<String> getCategAttrsNames() {
        return categAttrsNames;
    }

    /**
     * Returns the item numeric attributes.
     *
     * @return the item numeric attributes.
     */
    public List<Double> getNumericAttrs() {
        return numericAttrs;
    }

    /**
     * Returns the item categorical attributes.
     *
     * @return the item categorical attributes.
     */
    public List<String> getCategAttrs() {
        return categAttrs;
    }

}
