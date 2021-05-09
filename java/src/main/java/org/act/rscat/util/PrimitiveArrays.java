package org.act.rscat.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Utility methods for working with primitive arrays.
 */
public final class PrimitiveArrays {

    private PrimitiveArrays() {
    }

    /**
     * Converts a {@link List} of Strings into a primitive int array.
     *
     * @param strings strings providing int values
     * @return the int array
     */
    public static int[] intArray(List<String> strings) {
        return strings.stream().mapToInt(Integer::valueOf).toArray();
    }

    /**
     * Converts a {@link List} of Strings into a primitive double array.
     *
     * @param strings strings providing double values
     * @return the double array
     */
    public static double[] doubleArray(List<String> strings) {
        return strings.stream().mapToDouble(Double::valueOf).toArray();
    }

    /**
     * Converts a {@link List} of Strings into a primitive boolean array.
     *
     * @param strings strings providing boolean values
     * @return the boolean array
     */
    public static boolean[] booleanArray(List<String> strings) {
        boolean[] values = new boolean[strings.size()];
        Iterator<String> stringIterator = strings.iterator();
        for (int i = 0; i < values.length; i++) {
            values[i] = Boolean.valueOf(stringIterator.next());
        }
        return values;
    }

    /**
     * Converts a primitive int array into a {@link List} of Strings.
     *
     * @param array the primitive array of int values
     * @return the String list
     */
    public static List<String> stringList(int[] array) {
        return Arrays.stream(array).mapToObj(String::valueOf).collect(Collectors.toList());
    }

    /**
     * Converts a primitive double array into a {@link List} of Strings.
     *
     * @param array the primitive array of double values
     * @return the String list
     */
    public static List<String> stringList(double[] array) {
        return Arrays.stream(array).mapToObj(String::valueOf).collect(Collectors.toList());
    }

    /**
     * Converts a primitive boolean array into a {@link List} of Strings.
     *
     * @param array the primitive array of boolean values
     * @return the String list
     */
    public static List<String> stringList(boolean[] array) {
        List<String> strings = new ArrayList<>(array.length);
        for (boolean value : array) {
            strings.add(String.valueOf(value));
        }
        return strings;
    }

    /**
     * Gets an array of indices of elements from an array that match a value (or any
     * of multiple values).
     *
     * @param array  the array
     * @param values the value (or values) to match
     * @return an array of indices of matching values
     */
    public static int[] select(int[] array, int... values) {
        List<Integer> results = new ArrayList<>();
        for (int i = 0; i < values.length; i++) {
            for (int j = 0; j < array.length; j++) {
                if (values[i] == array[j]) {
                    results.add(j);
                }
            }
        }
        return getIntArray(results);
    }

    /**
     * Gets an array of indices of elements from an array that match a value (or any
     * of multiple values).
     *
     * @param array  the array
     * @param values the value (or values) to match
     * @return an array of indices of matching values
     */
    public static int[] select(double[] array, double... values) {
        List<Integer> results = new ArrayList<>();
        for (int i = 0; i < values.length; i++) {
            for (int j = 0; j < array.length; j++) {
                if (values[i] == array[j]) {
                    results.add(j);
                }
            }
        }
        return getIntArray(results);
    }

    /**
     * Gets an array of indices of elements from an array that match a value (or any
     * of multiple values).
     *
     * @param array  the array
     * @param values the value (or values) to match
     * @return an array of indices of matching values
     */
    public static int[] select(boolean[] array, boolean... values) {
        List<Integer> results = new ArrayList<>();
        for (int i = 0; i < values.length; i++) {
            for (int j = 0; j < array.length; j++) {
                if (values[i] == array[j]) {
                    results.add(j);
                }
            }
        }
        return getIntArray(results);
    }

    /**
     * Gets an array of indices of elements from an array that match a value (or any
     * of multiple values).
     *
     * @param array  the array
     * @param values the value (or values) to match
     * @return an array of indices of matching values
     */
    public static int[] select(String[] array, String... values) {
        List<Integer> results = new ArrayList<>();
        for (int i = 0; i < values.length; i++) {
            for (int j = 0; j < array.length; j++) {
                if (values[i].equals(array[j])) {
                    results.add(j);
                }
            }
        }
        return getIntArray(results);
    }

    /**
     * Counts the number of values in the array that match a given value.
     *
     * @param array a primitive array
     * @param value the value to match
     * @return the number of match occurrences
     */
    public static int count(boolean[] array, boolean value) {
        int count = 0;
        for (boolean aValue : array) {
            if (value == aValue) {
                count++;
            }
        }
        return count;
    }

    /**
     * Counts the number of values in the array that match a given value.
     *
     * @param aList a primitive list
     * @param value the value to match
     * @return the number of match occurrences
     */
    public static int count(List<Boolean> aList, boolean value) {
        int count = 0;
        for (boolean aValue : aList) {
            if (value == aValue) {
                count++;
            }
        }
        return count;
    }

    /**
     * Counts the number of values in the array that match a given value.
     *
     * @param array  a String array
     * @param values the String values to match
     * @return the number of match occurrences
     */
    public static int count(String[] array, String... values) {
        int count = 0;
        for (String aValue : array) {
            for (String matchValue : values) {
                if (aValue.equals(matchValue)) {
                    count++;
                    break;
                }
            }
        }
        return count;
    }

    /**
     * Counts the number of values in the array that match a given value.
     *
     * @param aList a String list
     * @param values the String value to match
     * @return the number of match occurrences
     */
    public static int count(List<String> aList, String... values) {
        return count(aList.toArray(new String[0]), values);
    }

    /**
     * Returns subset samples of an array.
     *
     * @param array   the original array to be subset
     * @param indices the array indices to include in the sampling
     * @return the sampled array
     */
    public static String[] subSamples(String[] array, int... indices) {
        String[] subSamples = new String[indices.length];
        int count = 0;
        for (int index : indices) {
            subSamples[count] = array[index];
            count++;
        }
        return subSamples;
    }

    /**
     * Returns subset samples of a list.
     *
     * @param aList   the original list to be subset
     * @param indices the array indices to include in the sampling
     * @return the sampled array
     */
    public static List<String> subSamples(List<String> aList, int... indices) {
        List<String> subList = new ArrayList<>(indices.length);
        for (int index : indices) {
            subList.add(aList.get(index));
        }
        return subList;
    }

    private static int[] getIntArray(List<Integer> list) {
        int[] intArray = new int[list.size()];
        for (int i = 0; i < list.size(); i++) {
            intArray[i] = list.get(i);
        }
        return intArray;
    }

}
