package org.act.rscat.testdef;

import java.util.ArrayList;
import java.util.List;

/**
 * This class defines metadata of a test entity for CAT.
 */
public abstract class AbstractTestEntity {

    // Identifier.
    private String id;

    //Row index in a csv file.
    private int rowIndex;

    // Numeric attribute name list.
    private List<String> numericAttrsNames = new ArrayList<>();

    // Categorical attribute name list.
    private List<String> categAttrsNames = new ArrayList<>();

    // Numeric attribute list.
    private List<Double> numericAttrs = new ArrayList<>();

    // Categorical attribute list.
    private List<String> categAttrs = new ArrayList<>();

    /**
     * Construct a new {@link AbstractTestEntity}.
     *
     * @param id               an identifier
     * @param rowData          a {@code List} collection of {@code String} row data
     *                         in the csv
     * @param columnNames      a {@code List} collection of {@code String} as column
     *                         names
     * @param isNumericColumns an array of {@code boolean} indicators representing
     *                         if columns are numeric columns. True, if a column is
     *                         numeric; false, otherwise
     * @param rowIndex         a row index
     */
    protected AbstractTestEntity(String id, List<String> rowData, List<String> columnNames, boolean[] isNumericColumns, int rowIndex) {
        int count = 0;
        this.id = id;
        this.rowIndex = rowIndex;
        for (boolean mark : isNumericColumns) {
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
     * Returns the identifier.
     *
     * @return the identifier
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the row index.
     *
     * @return the row index
     */
    public int getRowIndex() {
        return rowIndex;
    }

    /**
     * Returns the numeric attribute names.
     *
     * @return the numeric attribute names.
     */
    public List<String> getNumericAttrsNames() {
        return numericAttrsNames;
    }

    /**
     * Returns the categorical attribute names.
     *
     * @return the categorical attribute names.
     */
    public List<String> getCategAttrsNames() {
        return categAttrsNames;
    }

    /**
     * Returns the numeric attributes.
     *
     * @return the numeric attributes.
     */
    public List<Double> getNumericAttrs() {
        return numericAttrs;
    }

    /**
     * Returns the categorical attributes.
     *
     * @return the categorical attributes.
     */
    public List<String> getCategAttrs() {
        return categAttrs;
    }

}
