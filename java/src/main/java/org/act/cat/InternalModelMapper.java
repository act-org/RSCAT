package org.act.cat;

import java.util.List;
import java.util.Set;

import org.act.testdef.Item;
import org.act.util.ContentTable;
import org.apache.commons.lang3.math.NumberUtils;

import com.google.common.collect.Sets;

public class InternalModelMapper {
    private static final String ITEM_ID_COLUMN_NAME = Item.ColumnName.ITEM_ID.getColName();
    private static final String PASSAGE_ID_COLUMN_NAME = Item.ColumnName.ITEM_PASSAGE_ID.getColName();
    private static final String ITEM_IDS_COLUMN_NAMES = "item_ids";
    private static final String PASSAGE_ORDER_COLUMN_NAME = "passage_item_order";
    private static final String PRECLUDES_COLUMN_NAME = "Precludes";
    private static Set<String> NON_NUMERIC_ITEM_COLUMNS = Sets.newHashSet(ITEM_ID_COLUMN_NAME, PASSAGE_ID_COLUMN_NAME,
            PASSAGE_ORDER_COLUMN_NAME, PRECLUDES_COLUMN_NAME);

    private static Set<String> NON_NUMERIC_PASSAGE_COLUMNS = Sets.newHashSet(PASSAGE_ID_COLUMN_NAME,
            ITEM_IDS_COLUMN_NAMES);

    public static boolean[] getItemNumericColumn(ContentTable.RowOriented itemPoolTable) {
        return getNumericColumns(itemPoolTable, NON_NUMERIC_ITEM_COLUMNS, "item");
    }

    public static boolean[] getPassageNumericColumn(ContentTable.RowOriented passagePoolTable) {
        return getNumericColumns(passagePoolTable, NON_NUMERIC_PASSAGE_COLUMNS, "passage");
    }

    private static boolean[] getNumericColumns(ContentTable.RowOriented table, Set<String> nonNumericColumns,
            String poolType) {
        boolean[] numericColumns = null;
        if (tableHasData(table, poolType)) {
            List<String> columnNames = table.getColumnNames();
            List<List<String>> rows = table.rows();

            numericColumns = new boolean[columnNames.size()];
            List<String> row = rows.get(0);
            if (row.size() == columnNames.size()) {
                int curCol = 0;
                for (String columnName : columnNames) {
                    if (nonNumericColumns.contains((columnName))) {
                        numericColumns[curCol] = false;
                    } else {
                        String value = row.get(curCol);
                        numericColumns[curCol] = NumberUtils.isNumber(row.get(curCol));
                    }
                    curCol++;
                }
            }
        }
        return numericColumns;
    }

    private static boolean tableHasData(ContentTable.RowOriented table, String poolType) {
        boolean hasData = false;
        if (table == null) {
            throw new IllegalStateException("Received empty " + poolType + " table.");
        }
        List<String> columnNames = table.getColumnNames();
        List<List<String>> rows = table.rows();
        if (columnNames == null) {
            throw new IllegalStateException("Received " + poolType + " table with no column headers.");
        }
        if (rows == null) {
            throw new IllegalStateException("Received " + poolType + " table with no data rows.");
        }
        if (rows.size() > 0 && columnNames.size() > 0) {
            if (columnNames.size() == rows.get(0).size()) {
                hasData = true;
            } else {
                throw new IllegalStateException(
                        "Received " + poolType + " table with mismatched number of columns and column headers.");
            }
        }
        return hasData;
    }

}
