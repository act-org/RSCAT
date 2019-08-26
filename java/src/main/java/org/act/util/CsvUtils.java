package org.act.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import org.act.sol.TestAssembly;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility methods for dealing with csv files.
 */
public class CsvUtils {
    /**
     * Logger for solver performance metrics.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(CsvUtils.class);
    private static final char DEFAULT_SEPARATOR = ',';
    private CsvUtils() {
    }

    /**
     * Reads CSV values from an {@link InputStream}.
     *
     * @param stream the content stream
     * @return a {@link ContentTable} populated with the comma seperated values
     *         from each line in the content stream
     * @throws IOException if there is a failure reading the values from the
     *             source
     */
    public static ContentTable.RowOriented read(InputStream stream) throws IOException {
        List<List<String>> contents = new ArrayList<>();
        try (InputStreamReader streamReader = new InputStreamReader(stream);
                BufferedReader reader = new BufferedReader(streamReader)) {
            reader.lines().forEach(line -> contents.add(parse(line)));
        }
        List<String> columnNames = contents.remove(0);
        return ContentTable.rowOriented(columnNames, contents);
    }

    /**
     * Parse a line from a CSV file.
     *
     * @param line the line
     * @return a list of comma separated values from the line
     */
    public static List<String> parse(String line) {
        // String#split fails to insert empty string for trailing commas, split
        // manually
        List<String> values = new ArrayList<>();
        String delimiter = String.valueOf(DEFAULT_SEPARATOR);
        int start = 0;
        int end = 0;
        while (start < line.length() || end != -1) {
            end = line.indexOf(delimiter, start);
            int endIndex = end != -1 ? end : line.length();
            values.add(start < line.length() ? line.substring(start, endIndex) : "");
            start = endIndex + delimiter.length();
        }
        return values;
    }

    /**
     * Write CSV values to an {@link OutputStream}.
     *
     * @param data a {@link ContentTable} populated with values to write out
     * @param stream the destination
     * @throws IOException if there is a failure writing the values to the
     *             destination
     */
    public static void write(ContentTable.RowOriented data, OutputStream stream) throws IOException {
        try (OutputStreamWriter writer = new OutputStreamWriter(stream)) {
            writer.write(format(data.columnNames()) + System.lineSeparator());
            for (List<String> row : data.rows()) {
                writer.write(format(row) + System.lineSeparator());
            }
        } catch (IOException e) {
        	LOGGER.error("",e);
        }
    }

    /**
     * Format a list of String values into a single comma separated string.
     *
     * @param values the values
     * @return the formatted CSV string
     */
    public static String format(Iterable<String> values) {
        StringBuilder sb = new StringBuilder();
        values.forEach(value -> sb.append(sb.length() > 0 ? DEFAULT_SEPARATOR + value : value));
        return sb.toString();
    }

}
