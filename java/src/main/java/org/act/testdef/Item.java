package org.act.testdef;

import java.util.ArrayList;
import java.util.List;

/**
 * Defines metadata of an item for CAT.
 */
public class Item {
	
	public enum ColumnName{
		ITEM_ID("Item ID"), ITEM_PASSAGE_ID("Passage ID"),
		A_PARAM("A-Param"), B_PARAM("B-Param"), C_PARAM("C-Param"),
		A_PARAM_SE("A-Param-SE"), B_PARAM_SE("B-Param-SE"), C_PARAM_SE("C-Param-SE"),
		D_CONSTANT("D-Constant");
		private String colName;
		ColumnName(String colName) {
			this.colName = colName;
		}
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
    private List<String> numericAttrsNames = new ArrayList<String>();

    /**
     * Item categorical attribute name list.
     */
    private List<String> categAttrsNames = new ArrayList<String>();

    /**
     * Item numeric attribute list.
     */
    private List<Double> numericAttrs = new ArrayList<Double>();

    /**
     * Item categorical attribute list.
     */
    private List<String> categAttrs = new ArrayList<String>();

    /**
     * Construct a new {@link Item}.
     *
     * @param id item identifier
     * @param rowData row data in the csv table
     * @param columnNames a list of column names
     * @param itemNumericColumns an array of numeric attribute marks
     * @param rowIndex row index of the item
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
