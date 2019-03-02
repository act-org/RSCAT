package org.act.testdef;

import java.util.ArrayList;
import java.util.List;

/**
 * Defines metadata of a passage for CAT.
 */
public class Passage {
    /**
     * Passage identifier.
     */
    public String id;

    /**
     * Passage row index in csv file.
     */
    private int rowIndex;

    /**
     * Passage numeric attribute name list.
     */
    private List<String> numericAttrsNames = new ArrayList<String>();

    /**
     * Passage categorical attribute name list.
     */
    private List<String> categAttrsNames = new ArrayList<String>();

    /**
     * Passage numeric attribute list.
     */
    private List<Double> numericAttrs = new ArrayList<Double>();

    /**
     * Passage categorical attribute list.
     */
    private List<String> categAttrs = new ArrayList<String>();

    /**
     * Constructs a new {@link passage}.
     *
     * @param id the passage identifier
     * @param rowData the row data in the csv table
     * @param columnNames the list of column names
     * @param passageNumericColumns the array of numeric attribute marks
     * @param rowIndex row the index of the passage
     */
    public Passage(String id, List<String> rowData, List<String> columnNames, boolean[] passageNumericColumns,
            int rowIndex) {
        int count = 0;
        this.id = id;
        this.rowIndex = rowIndex;
        for (boolean mark : passageNumericColumns) {
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
     * Returns the passage identifier.
     *
     * @return the passage identifier
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the passage row index.
     *
     * @return the passage row index.
     */
    public int getRowIndex() {
        return rowIndex;
    }

    /**
     * Returns the passage numeric attribute name list.
     *
     * @return the passage numeric attribute name list
     */
    public List<String> getNumericAttrsNames() {
        return numericAttrsNames;
    }

    /**
     * Returns the passage categorical attribute name list.
     *
     * @return the passage categorical attribute name list
     */
    public List<String> getCategAttrsNames() {
        return categAttrsNames;
    }

    /**
     * Returns the passage numeric attribute list.
     *
     * @return the passage numeric attribute list
     */
    public List<Double> getNumericAttrs() {
        return numericAttrs;
    }

    /**
     * Returns the passage categorical attribute list.
     *
     * @return the passage categorical attribute list
     */
    public List<String> getCategAttrs() {
        return categAttrs;
    }

}
