package org.act.mip;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Defines attributes of a constraint in the shadow test.
 */
public class Constraint {

    private static final String STR_NULL = "Null";
    private static final String DATA_SET_DELIMITER = "#";
    private static final String DATA_ELEMENT_DELIMITER = "\\|";
    private static final String BOUND_FILTER = "Bounds";
    private static final String SET_FILTER = "Set";
    private static final double BIG_NUMBER = 1e6;

    /**
     * Constraint row index.
     */
    public int rowIndex;

    /**
     * Constraint identifier.
     */
    public String id;

    /**
     * The constraint short description.
     */
    public String description;

    /**
     * Constraint type.
     */
    public String type;

    /**
     * Constraint level, item or passage.
     */
    public String level;

    /**
     * The attribute that to be calculated and applied with LB and UB.
     */
    public String calAttr;

    /**
     * The lower bound to be applied to CalAttr, "null" for unbounded.
     */
    public double calLB;

    /**
     * The upper bound to be applied to CalAttr, "null" for unbounded.
     */
    public double calUB;

    /**
     * A {@link Map} collection with filter attributes as keys and filter bounds as values.
     */
    public Map<String, List<Double>> filterBoundsData;

    /**
     * A {@link Map} collection with filter attributes as keys and filter sets as values.
     */
    public Map<String, Set<String>> filterSetData;

    /**
     * Constraint activity value.
     */
    public double activity;

    /**
     * Defines a constraint column (attribute) enum types.
     */
    public enum ColumnName {

        /**
         * Constraint identifier.
         */
        id("Id"),

        /**
         * Constraint short description.
         */
        description("Description"),

        /**
         * Constraint type.
         */
        type("Type"),

        /**
         * Constraint level, item or passage.
         */
        level("Level"),

        /**
         * The attribute that to be calculated and applied with LB and UB.
         */
        calAttr("CalAttr"),

        /**
         * The lower bound to be applied to CalAttr, "null" for unbounded.
         */
        calLB("CalLB"),

        /**
         * The upper bound to be applied to CalAttr, "null" for unbounded.
         */
        calUB("CalUB"),

        /**
         * Attributes to filter qualified items/passages for the constraint,
         * multiple attributes are separated by |
         */
        filterAttr("FilterAttr"),

        /**
         * Logics to filter qualified items/passages for the constraint,
         * for each attribute in FilterAttr. Multiple attributes are separated by |
         */
        filterLogic("FilterLogic"),

        /**
         * Filter data to be applied to FilterLogic. Multiple data sets are separated by #.
         * Elements in a data set are separated by |
         */
        filterData("FilterData"),

        /**
         * Constraint application status
         */
        isLoaded("IsLoaded");
        ;

        /**
         * The String name of a column (attribute).
         */
        private String name;

        /**
         * Constructs a ColumnName enum type.
         *
         * @param name the column String name
         */
        ColumnName(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    /**
     * Constructs a new {@link Constraint}.
     *
     * @param columnNameList the String list of constraint attributes' names
     * @param data the String array storing values of constraint attributes
     * @param rowIndex the row index of the constraint
     */
    public Constraint(List<String> columnNameList, String[] data, int rowIndex) {

        // Parse constraint parameters
        this.rowIndex = rowIndex;
        id = data[columnNameList.indexOf(ColumnName.id.name)];
        description = data[columnNameList.indexOf(ColumnName.description.name)];
        type = data[columnNameList.indexOf(ColumnName.type.name)];
        level = data[columnNameList.indexOf(ColumnName.level.name)];
        calAttr = data[columnNameList.indexOf(ColumnName.calAttr.name)];

        // Parse bounds of calAttr
        if (!data[columnNameList.indexOf(ColumnName.calLB.name)].equals(STR_NULL)) {
            calLB = Double.parseDouble(data[columnNameList.indexOf(ColumnName.calLB.name)]);
        } else {
            calLB = -BIG_NUMBER;
        }
        if (!data[columnNameList.indexOf(ColumnName.calUB.name)].equals(STR_NULL)) {
            calUB = Double.parseDouble(data[columnNameList.indexOf(ColumnName.calUB.name)]);
        } else {
            calUB = BIG_NUMBER;
        }

        // Parse filter data
        List<String> filterAttr = Arrays
                .asList(data[columnNameList.indexOf(ColumnName.filterAttr.name)].split(DATA_ELEMENT_DELIMITER));
        List<String> filterLogic = Arrays
                .asList(data[columnNameList.indexOf(ColumnName.filterLogic.name)].split(DATA_ELEMENT_DELIMITER));
        List<String> filterDataSet = Arrays
                .asList(data[columnNameList.indexOf(ColumnName.filterData.name)].split(DATA_SET_DELIMITER));
        Iterator<String> attrIt = filterAttr.iterator();
        Iterator<String> logicIt = filterLogic.iterator();
        Iterator<String> dataSetIt = filterDataSet.iterator();
        filterBoundsData = new HashMap<String, List<Double>>();
        filterSetData = new HashMap<String, Set<String>>();
        while (attrIt.hasNext() && logicIt.hasNext() && dataSetIt.hasNext()) {
            String attrStr = attrIt.next();
            String logicStr = logicIt.next();
            String dataSetStr = dataSetIt.next();
            if (logicStr.equalsIgnoreCase(BOUND_FILTER)) {
                List<String> dataStr = Arrays.asList(dataSetStr.split(DATA_ELEMENT_DELIMITER));
                List<Double> dataDouble = new ArrayList<Double>();
                for (String str : dataStr) {
                    dataDouble.add(Double.parseDouble(str));
                }
                filterBoundsData.put(attrStr, dataDouble);
            } else if (logicStr.equals(SET_FILTER)) {
                Set<String> dataSet = new HashSet<String>(
                        Arrays.asList(dataSetStr.split(DATA_ELEMENT_DELIMITER)));
                filterSetData.put(attrStr, dataSet);
            }
        }
    }
}
