package org.act.rscat.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This is a general purpose tabular data container where all rows have the same
 * set of columns.
 */
public interface ContentTable {

    /**
     * Defines if a content table is created by rows or by columns.
     */
    enum Orientation {

        /**
         * By rows.
         */
        BY_ROW,

        /**
         * By columns.
         */
        BY_COLUMN
    }

    /**
     * Returns a new {@link ContentTable} for a set of row data.
     *
     * @param columnNames the column names
     * @param rows the row data
     * @return the instance of {@code ContentTable}
     */
    static RowOriented rowOriented(List<String> columnNames, List<List<String>> rows) {
        return new RowOriented(columnNames, rows);
    }

    /**
     * Returns a new empty {@link ContentTable}.
     *
     * @return the empty table
     */
    static RowOriented rowOriented() {
        List<List<String>> rows = new ArrayList<>();
        return new RowOriented(new ArrayList<>(), rows);
    }

    /**
     * Returns a new {@link ContentTable} for a set of column data.
     *
     * @param columnNames the column names
     * @param columns the column data
     * @return the table
     */
    static ColumnOriented columnOriented(List<String> columnNames, List<List<String>> columns) {
        return new ColumnOriented(columnNames, columns);
    }

    /**
     * Returns a new empty {@link ContentTable} for a set of column data.
     *
     * @return the empty table
     */
    static ColumnOriented columnOriented() {
        List<List<String>> columns = new ArrayList<>();
        return new ColumnOriented(new ArrayList<>(), columns);
    }

    /**
     * Returns a {@link RowOriented} view or representation of this table.
     *
     * @return a RowOriented table
     */
    RowOriented orientByRows();

    /**
     * Returns a column oriented view or representation of this table.
     *
     * @return a {ColumnOriented} table
     */
    ColumnOriented orientByColumns();

    /**
     * Returns the column names.
     *
     * @return the column names
     */
    List<String> columnNames();

    /**
     * Returns the column index for a given column name.
     *
     * @param columnName the column name
     * @return the column index
     */
    int columnIndex(String columnName);

    /**
     * Returns the row count.
     *
     * @return the row count
     */
    int rowCount();

    /**
     * A {@link ContentTable} that stores its data in row-major order and
     * provides access to row data.
     */

    /**
     * Returns the list of rows in the content table.
     *
     * @return the list of rows
     */
    List<List<String>> rows();

    /**
     * Returns the list of columns in the content table.
     *
     * @return the list of columns
     */
    List<List<String>> columns();

    /**
     * Returns the table orientation type.
     *
     * @return the table orientation type
     */
    Orientation orientedBy();

    /**
     * A {@link ContentTable} that stores its data in row-major order and
     * provides access to column data.
     */
    class RowOriented implements ContentTable {

        /**
         * List of column names.
         */
        private final List<String> columnNames;

        /**
         * List of data rows. Each row is a string list.
         */
        private final List<List<String>> rows;

        /**
         * Constructs a new {@link RowOriented}.
         *
         * @param columnNames the colum names of the table
         * @param rows table data by rows in list of list
         */
        public RowOriented(List<String> columnNames, List<List<String>> rows) {
            this.columnNames = Collections.unmodifiableList(columnNames);
            this.rows = rows.stream().map(Collections::unmodifiableList).collect(Collectors.toList());
        }

        /**
         * Returns the list of column names.
         *
         * @return the list of column names.
         */
        public List<String> getColumnNames() {
            return columnNames;
        }

        @Override
        public RowOriented orientByRows() {
            return this; // Nothing to do
        }

        @Override
        public ColumnOriented orientByColumns() {
            List<List<String>> columns = transpose(rows);
            return columnOriented(columnNames, columns);
        }

        @Override
        public List<String> columnNames() {
            return columnNames;
        }

        @Override
        public int columnIndex(String columnName) {
            return columnNames.indexOf(columnName);
        }

        @Override
        public int rowCount() {
            return rows.size();
        }

        /**
         * Sorts the rows based on the values in a given column.
         *
         * @param sortingColumn the column to apply natural ordering sort to
         */
        public void sort(String sortingColumn) {
            final int index = columnNames.indexOf(sortingColumn);
            Collections.sort(rows, (row1, row2) -> row1.get(index).compareTo(row2.get(index)));
        }

        @Override
        public List<List<String>> rows() {
            return rows;
        }

        @Override
        public List<List<String>> columns() {
            return orientByColumns().columns();
        }

        @Override
        public Orientation orientedBy() {
            return Orientation.BY_ROW;
        }

        /**
         * Returns the row values.
         *
         * @param index the row index
         * @return the values
         */
        public List<String> rowValues(int index) {
            return rows.get(index);
        }
    }

    /**
     * A {@link ContentTable} that stores its data in column-major order and
     * provides access to column data.
     */
    class ColumnOriented implements ContentTable {

        /**
         * List of column names.
         */
        private final List<String> columnNames;

        /**
         * List of data columns. Each column is a list of String.
         */
        private final List<List<String>> columns;

        /**
         * Constructs a new {@link ColumnOriented}.
         *
         * @param columnNames the column name list
         * @param columns the column data
         */
        private ColumnOriented(List<String> columnNames, List<List<String>> columns) {
            this.columnNames = Collections.unmodifiableList(columnNames);
            this.columns = columns.stream().map(Collections::unmodifiableList).collect(Collectors.toList());
        }

        @Override
        public RowOriented orientByRows() {
            List<List<String>> rows = transpose(columns);
            return rowOriented(columnNames, rows);
        }

        @Override
        public ColumnOriented orientByColumns() {
            return this; // Nothing to do
        }

        @Override
        public List<String> columnNames() {
            return columnNames;
        }

        @Override
        public int columnIndex(String columnName) {
            return columnNames.indexOf(columnName);
        }

        @Override
        public int rowCount() {
            return columns.isEmpty() ? 0 : columns.get(0).size();
        }

        @Override
        public List<List<String>> rows() {
            return orientByRows().rows();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public List<List<String>> columns() {
            return columns;
        }

        @Override
        public Orientation orientedBy() {
            return Orientation.BY_COLUMN;
        }

        /**
         * Returns the column values.
         *
         * @param index the column index
         * @return the values
         */
        public List<String> columnValues(int index) {
            return columns.get(index);
        }

        /**
         * Returns the column values.
         *
         * @param columnName the column name
         * @return the values
         */
        public List<String> columnValues(String columnName) {
            int column = columnNames.indexOf(columnName);
            if (column == -1) {
                throw new IllegalArgumentException("No column available with name " + columnName);
            }
            return columnValues(column);
        }
    }

    /**
     * Transposes the values in the 2d set of data.
     *
     * @param values the values
     * @param <T> the value type
     * @return the transposed list of lists
     */
    static <T> List<List<T>> transpose(List<List<T>> values) {
        int innerLength = values.size();
        int outerLength = values.isEmpty() ? 0 : values.get(0).size();
        List<List<T>> reoriented = new ArrayList<>(outerLength);
        for (int i = 0; i < outerLength; i++) {
            List<T> list = new ArrayList<>(innerLength);
            for (int j = 0; j < innerLength; j++) {
                list.add(values.get(j).get(i));
            }
            reoriented.add(list);
        }
        return reoriented;
    }

}
