package org.act.rscat.testdef;

import java.util.List;

/**
 * Defines metadata of a passage for CAT.
 */
public class Passage extends AbstractTestEntity {

    /**
     * Construct a new {@link Item}.
     *
     * @param id               an passage identifier
     * @param rowData          a {@code List} collection of {@code String} as the
     *                         row data of the passage in the csv
     * @param columnNames      a {@code List} collection of {@code String} as column
     *                         names of the passage attributes
     * @param isNumericColumns an array of {@code boolean} indicators representing
     *                         if passage attribute columns are numeric or not.
     *                         True, if a column is numeric; false, otherwise
     * @param rowIndex         a row index
     */
    public Passage(String id, List<String> rowData, List<String> columnNames, boolean[] isNumericColumns,
            int rowIndex) {
        super(id, rowData, columnNames, isNumericColumns, rowIndex);
    }
}
