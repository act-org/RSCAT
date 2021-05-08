package org.act.rscat.util;

import org.apache.commons.lang3.ArrayUtils;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * A set of named primitive arrays, each consisting of the same number of
 * elements. Supports int[], double[], boolean[], and String[] only.
 */
public class PrimitiveArraySet {

    private final Map<String, Object> arrays;
    private final int length;

    /**
     * Constructs a new {@link PrimitiveArraySet}.
     */
    public PrimitiveArraySet() {
        this(new HashMap<>(), 0);
    }

    /**
     * Constructs a new {@link PrimitiveArraySet}.
     *
     * @param arrays a mapping from array identifiers to the arrays
     * @param length the length of each of the arrays
     */
    private PrimitiveArraySet(Map<String, Object> arrays, int length) {
        this.arrays = arrays;
        this.length = length;
    }

    /**
     * Converts {@code String} lists in selected columns to primitive arrays of type of type
     * int[], double[], boolean[], or String[].
     *
     * @param table       the table of string contents
     * @param columnTypes a mapping of column names to primitive array types to
     *                    extract
     * @return the {@link PrimitiveArraySet}
     */
    public static PrimitiveArraySet fromContentTable(ContentTable table, Map<String, Class<?>> columnTypes) {
        ContentTable.ColumnOriented colTable = table.orientByColumns();
        Map<String, Object> arrays = new HashMap<>();
        for (Map.Entry<String, Class<?>> entry : columnTypes.entrySet()) {
            String colName = entry.getKey();
            Class<?> colType = entry.getValue();
            Object array = convert(colTable.columnValues(colTable.columnIndex(colName)), colType);
            arrays.put(colName, array);
        }
        return new PrimitiveArraySet(arrays, table.rowCount());
    }

    /**
     * Returns a copy of this {@link PrimitiveArraySet} with an additional array.
     *
     * @param arrayId the identifier of the new array
     * @param array   the new array
     * @return the new {@link PrimitiveArraySet}
     */
    public PrimitiveArraySet withIntArray(String arrayId, int[] array) {
        return withArray(arrayId, array, array.length);
    }

    /**
     * Returns the array for a given identifier.
     *
     * @param arrayIdentifier the array identifier
     * @return the array
     */
    public int[] getIntArray(String arrayIdentifier) {
        return getArray(arrayIdentifier, int[].class);
    }

    /**
     * Returns a copy array for a given identifier.
     *
     * @param arrayIdentifier the array identifier
     * @return the array
     */
    public int[] getIntArrayCopy(String arrayIdentifier) {
        int[] intArray = getArray(arrayIdentifier, int[].class);
        if (intArray != null) {
            int[] intArrayCopy = new int[intArray.length];
            System.arraycopy(intArray, 0, intArrayCopy, 0, intArray.length);
            return intArrayCopy;

        }
        return intArray;
    }

    /**
     * Returns a copy of this {@link PrimitiveArraySet} with an additional array.
     *
     * @param arrayId the identifier of the new array
     * @param array   the new array
     * @return the new {@link PrimitiveArraySet}
     */
    public PrimitiveArraySet withDoubleArray(String arrayId, double[] array) {
        return withArray(arrayId, array, array.length);
    }

    /**
     * Returns the array for a given identifier.
     *
     * @param arrayIdentifier the array identifier
     * @return the array
     */
    public double[] getDoubleArray(String arrayIdentifier) {
        return getArray(arrayIdentifier, double[].class);
    }

    /**
     * Returns a copy array for a given identifier.
     *
     * @param arrayIdentifier the array identifier
     * @return the array
     */
    public double[] getDoubleArrayCopy(String arrayIdentifier) {
        double[] doubleArray = getArray(arrayIdentifier, double[].class);
        if (doubleArray != null) {
            double[] doubleArrayCopy = new double[doubleArray.length];
            System.arraycopy(doubleArray, 0, doubleArrayCopy, 0, doubleArray.length);
            return doubleArrayCopy;

        }
        return doubleArray;
    }

    /**
     * Returns a copy of this {@link PrimitiveArraySet} with an additional array.
     *
     * @param arrayId the identifier of the new array
     * @param array   the new array
     * @return the new {@link PrimitiveArraySet}
     */
    public PrimitiveArraySet withBooleanArray(String arrayId, boolean[] array) {
        return withArray(arrayId, array, array.length);
    }

    /**
     * Returns the array for a given identifier.
     *
     * @param arrayIdentifier the array identifier
     * @return the array
     */
    public boolean[] getBooleanArray(String arrayIdentifier) {
        return getArray(arrayIdentifier, boolean[].class);
    }

    /**
     * Returns a copy array for a given identifier.
     *
     * @param arrayIdentifier the array identifier
     * @return the array
     */
    public boolean[] getBooleanArrayCopy(String arrayIdentifier) {
        boolean[] booleanArray = getArray(arrayIdentifier, boolean[].class);
        if (booleanArray != null) {
            boolean[] booleanArrayCopy = new boolean[booleanArray.length];
            System.arraycopy(booleanArray, 0, booleanArrayCopy, 0, booleanArray.length);
            return booleanArrayCopy;

        }
        return booleanArray;
    }

    /**
     * Returns a copy of this {@link PrimitiveArraySet} with an additional array.
     *
     * @param arrayId the identifier of the new array
     * @param array   the new array
     * @return the new {@link PrimitiveArraySet}
     */
    public PrimitiveArraySet withStringArray(String arrayId, String[] array) {
        return withArray(arrayId, array, array.length);
    }

    /**
     * Returns the array for a given identifier.
     *
     * @param arrayIdentifier the array identifier
     * @return the array
     */
    public String[] getStringArray(String arrayIdentifier) {
        return getArray(arrayIdentifier, String[].class);
    }

    /**
     * Returns a copy of the array for a given identifier.
     *
     * @param arrayIdentifier the array identifier
     * @return the array
     */
    public String[] getStringArrayCopy(String arrayIdentifier) {
        String[] stringArray = getArray(arrayIdentifier, String[].class);
        if (stringArray != null) {
            String[] stringArrayCopy = new String[stringArray.length];
            System.arraycopy(stringArray, 0, stringArrayCopy, 0, stringArray.length);
            return stringArrayCopy;
        }
        return stringArray;
    }

    /**
     * Returns a copy of this {@link PrimitiveArraySet} without the specified array.
     *
     * @param arrayIdentifier the identifier of the array to exclude
     * @return the new {@link PrimitiveArraySet}
     */
    public PrimitiveArraySet without(String arrayIdentifier) {
        Map<String, Object> newArrays = new HashMap<>(arrays);
        newArrays.remove(arrayIdentifier);
        int newLength = newArrays.isEmpty() ? 0 : length;
        return new PrimitiveArraySet(newArrays, newLength);
    }

    /**
     * Returns a copy of a subset of this {@link PrimitiveArraySet}.
     *
     * @param startIndex the index to begin the subset with (inclusive)
     * @param endIndex   the index to end the subset with (exclusive)
     * @return the new {@link PrimitiveArraySet}
     */
    public PrimitiveArraySet subset(int startIndex, int endIndex) {
        if (endIndex < startIndex) {
            throw new IllegalArgumentException("invalid subset");
        }
        Map<String, Object> newArrays = new HashMap<>();
        arrays.entrySet()
                .forEach(array -> newArrays.put(array.getKey(), subset(array.getValue(), startIndex, endIndex)));
        int newLength = endIndex - startIndex;
        return new PrimitiveArraySet(newArrays, newLength);
    }

    /**
     * Returns a sub-sampling of this {@link PrimitiveArraySet}.
     *
     * @param indices the array indices to include in the sampling
     * @return the new {@link PrimitiveArraySet}
     */
    public PrimitiveArraySet subSample(int... indices) {
        Map<String, Object> newArrays = new HashMap<>();
        arrays.entrySet().forEach(array -> newArrays.put(array.getKey(), subSample(array.getValue(), indices)));
        int newLength = indices.length;
        return new PrimitiveArraySet(newArrays, newLength);
    }

    /**
     * Returns a set of the the array identifiers for arrays contained by this
     * {@link PrimitiveArraySet}.
     *
     * @return a set of the array identifiers
     */
    public Set<String> arrayIds() {
        return arrays.keySet();
    }

    /**
     * Returns the length of the arrays that are part of this {@link PrimitiveArraySet}.
     *
     * @return the length of the arrays (all arrays in the set have the same length)
     */
    public int length() {
        return length;
    }

    /**
     * Sorts the arrays as a group based on the natural sorting order of the
     * specified array.
     *
     * @param arrayId the identifier of the array that should define the sorting
     *                order
     * @return the sorted {@link PrimitiveArraySet}
     */
    public PrimitiveArraySet groupSort(String arrayId) {
        Object arrayObject = arrays.get(arrayId);
        Comparator<Integer> comparator = indexComparator(arrayObject);
        List<Integer> indexes = IntStream.range(0, length).boxed().collect(Collectors.toList());
        Collections.sort(indexes, comparator);
        return groupSort(indexes);
    }

    /**
     * Gets the set of primitive arrays reversed in order.
     *
     * @return the reversed {@link PrimitiveArraySet}
     */
    public PrimitiveArraySet reverse() {
        List<Integer> indexes = IntStream.iterate(length - 1, value -> value - 1).limit(length).boxed()
                .collect(Collectors.toList());
        return groupSort(indexes);
    }

    ///////////////////////////////////////////////////////////
    // private

    /**
     * Sort the arrays based on the given index mappings.
     *
     * @param indexes a list of integers with the value of each index corresponding
     *                to the destination index
     * @return the new {@link PrimitiveArraySet}
     */
    private PrimitiveArraySet groupSort(List<Integer> indexes) {
        Map<String, Object> sortedArrays = new HashMap<>(arrays.size());
        arrays.entrySet().forEach(array -> sortedArrays.put(array.getKey(), sort(array.getValue(), indexes)));
        return new PrimitiveArraySet(sortedArrays, length);
    }

    /**
     * Returns the array for a given identifier.
     *
     * @param arrayIdentifier the array identifier
     * @param arrayType       the type token
     * @param                 <T> the array type
     * @return the array
     */
    private <T> T getArray(String arrayIdentifier, Class<T> arrayType) {
        return arrayType.cast(arrays.get(arrayIdentifier));
    }

    /**
     * Returns a copy of this {@link PrimitiveArraySet} with an additional array.
     *
     * @param arrayId the identifier of the new array
     * @param array   the new array
     * @param aLength the length of the array
     * @return the new {@link PrimitiveArraySet}
     */
    private PrimitiveArraySet withArray(String arrayId, Object array, int aLength) {
        if (!this.arrays.isEmpty() && this.length != aLength) {
            throw new IllegalArgumentException(
                    "Cannot add array of length " + aLength + " to set of length " + this.length);
        }
        Map<String, Object> newArrays = new HashMap<>(this.arrays);
        newArrays.put(arrayId, array);
        return new PrimitiveArraySet(newArrays, aLength);
    }

    /**
     * Convert a list of string values to a primitive array object.
     *
     * @param values    the string values
     * @param arrayType the supported array type
     * @return the primitive array object
     */
    private static Object convert(List<String> values, Class<?> arrayType) {
        return ArrayTypes.support(arrayType).convert(values);
    }

    /**
     * Returns the {@link Comparator} for indices.
     *
     * @param array the primitive array object
     * @return the index indexComparator
     */
    private static Comparator<Integer> indexComparator(Object array) {
        return ArrayTypes.support(array.getClass()).indexComparator(array);
    }

    /**
     * Returns a sorted version of an array according to an index mapping.
     *
     * @param array   the primitive array object
     * @param indices the index mapping
     * @return the index indexComparator
     */
    private static Object sort(Object array, List<Integer> indices) {
        return ArrayTypes.support(array.getClass()).sort(array, indices);
    }

    /**
     * Returns of subset of an array.
     *
     * @param array      the primitive array object
     * @param startIndex the index to begin the subset with (inclusive)
     * @param endIndex   the index to end the subset with (exclusive)
     * @return the index indexComparator
     */
    private static Object subset(Object array, int startIndex, int endIndex) {
        return ArrayTypes.support(array.getClass()).subset(array, startIndex, endIndex);
    }

    /**
     * Returns a sub-sampling of an array.
     *
     * @param indices the array indices to include in the sampling
     * @return the new {@link PrimitiveArraySet}
     */
    private static Object subSample(Object array, int... indices) {
        return ArrayTypes.support(array.getClass()).subSample(array, indices);
    }

    /**
     * Defines the types of primitive arrays supported and encapsulates support
     * code.
     */
    private enum ArrayTypes {
        INT(int[].class, new ArraySupport() {
            @Override
            public Object convert(List<String> stringValues) {
                return PrimitiveArrays.intArray(stringValues);
            }

            @Override
            public Comparator<Integer> indexComparator(Object arrayObject) {
                int[] array = (int[]) arrayObject;
                return (o1, o2) -> Integer.compare(array[o1], array[o2]);
            }

            @Override
            public Object sort(Object arrayObject, List<Integer> indices) {
                int[] array = (int[]) arrayObject;
                return indices.stream().mapToInt(index -> array[index]).toArray();
            }

            @Override
            public Object subset(Object arrayObject, int startIndex, int endIndex) {
                return ArrayUtils.subarray((int[]) arrayObject, startIndex, endIndex);
            }

            @Override
            public Object subSample(Object arrayObject, int... indices) {
                int[] array = (int[]) arrayObject;
                return IntStream.of(indices).map(index -> array[index]).toArray();
            }
        }), DOUBLE(double[].class, new ArraySupport() {
            @Override
            public Object convert(List<String> stringValues) {
                return PrimitiveArrays.doubleArray(stringValues);
            }

            @Override
            public Comparator<Integer> indexComparator(Object arrayObject) {
                double[] array = (double[]) arrayObject;
                return (o1, o2) -> Double.compare(array[o1], array[o2]);
            }

            @Override
            public Object sort(Object arrayObject, List<Integer> indices) {
                double[] array = (double[]) arrayObject;
                return indices.stream().mapToDouble(index -> array[index]).toArray();
            }

            @Override
            public Object subset(Object arrayObject, int startIndex, int endIndex) {
                return ArrayUtils.subarray((double[]) arrayObject, startIndex, endIndex);
            }

            @Override
            public Object subSample(Object arrayObject, int... indices) {
                double[] array = (double[]) arrayObject;
                return IntStream.of(indices).mapToDouble(index -> array[index]).toArray();
            }
        }), BOOLEAN(boolean[].class, new ArraySupport() {
            @Override
            public Object convert(List<String> stringValues) {
                return PrimitiveArrays.booleanArray(stringValues);
            }

            @Override
            public Comparator<Integer> indexComparator(Object arrayObject) {
                boolean[] array = (boolean[]) arrayObject;
                return (o1, o2) -> Boolean.compare(array[o1], array[o2]);
            }

            @Override
            public Object sort(Object arrayObject, List<Integer> indices) {
                Iterator<Integer> indexIterator = indices.iterator();
                boolean[] array = (boolean[]) arrayObject;
                boolean[] copy = new boolean[array.length];
                for (int i = 0; i < copy.length; i++) {
                    copy[i] = array[indexIterator.next()];
                }
                return copy;
            }

            @Override
            public Object subset(Object arrayObject, int startIndex, int endIndex) {
                return ArrayUtils.subarray((boolean[]) arrayObject, startIndex, endIndex);
            }

            @Override
            public Object subSample(Object arrayObject, int... indices) {
                boolean[] array = (boolean[]) arrayObject;
                boolean[] sample = new boolean[indices.length];
                for (int i = 0; i < indices.length; i++) {
                    sample[i] = array[indices[i]];
                }
                return sample;
            }
        }), STRING(String[].class, new ArraySupport() {
            @Override
            public Object convert(List<String> stringValues) {
                return stringValues.toArray(new String[stringValues.size()]);
            }

            @Override
            public Comparator<Integer> indexComparator(Object arrayObject) {
                String[] array = (String[]) arrayObject;
                return (o1, o2) -> array[o1].compareTo(array[o2]);
            }

            @Override
            public Object sort(Object arrayObject, List<Integer> indices) {
                String[] array = (String[]) arrayObject;
                return indices.stream().map(index -> array[index]).toArray(String[]::new);
            }

            @Override
            public Object subset(Object arrayObject, int startIndex, int endIndex) {
                return ArrayUtils.subarray((String[]) arrayObject, startIndex, endIndex);
            }

            @Override
            public Object subSample(Object arrayObject, int... indices) {
                String[] array = (String[]) arrayObject;
                return IntStream.of(indices).mapToObj(index -> array[index]).toArray(String[]::new);
            }
        });
        private Class<?> arrayType;
        private ArraySupport support;
        ArrayTypes(Class<?> arrayType, ArraySupport support) {
            this.arrayType = arrayType;
            this.support = support;
        }
        /**
         * Returns the {@link ArraySupport} for a given array type.
         *
         * @param arrayType the supported array type
         * @return the support object
         */
        static ArraySupport support(Class<?> arrayType) {
            return Stream.of(ArrayTypes.values()).filter(type -> type.arrayType.equals(arrayType)).findAny()
                    .orElseThrow(() -> new IllegalArgumentException("Unsupported type: " + arrayType)).support;
        }
    }

    /**
     * Functions that must be implemented to support an array type.
     */
    private interface ArraySupport {

        /**
         * Convert a list of string values to a primitive array object.
         *
         * @param stringValues the string values
         * @return the primitive array object
         */
        Object convert(List<String> stringValues);

        /**
         * Returns indexComparator for indices of the given array.
         *
         * @return the indexComparator
         */
        Comparator<Integer> indexComparator(Object array);

        /**
         * Sort the given array with the given index mapping.
         *
         * @param array   the array to sort
         * @param indices the index mapping
         * @return the sorted array object
         */
        Object sort(Object array, List<Integer> indices);

        /**
         * Returns a subset of an array.
         *
         * @param array      the primitive array object
         * @param startIndex the index to begin the subset with (inclusive)
         * @param endIndex   the index to end the subset with (exclusive)
         * @return the index indexComparator
         */
        Object subset(Object array, int startIndex, int endIndex);

        /**
         * Returns a sub-sampling of an array.
         *
         * @param indices the array indices to include in the sampling
         * @return the new {@link PrimitiveArraySet}
         */
        Object subSample(Object array, int... indices);

    }

}
