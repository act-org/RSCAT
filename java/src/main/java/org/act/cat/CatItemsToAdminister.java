package org.act.cat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class defines the data structure to store a list of items to administer
 * (i.e., the remaining items on the shadow test sorted by information value),
 * as well as an integer value indicating the number of items to administer in
 * the current adaptive stage.
 */

public class CatItemsToAdminister {
    private static final String NULL_LIST_ITEMS_ALREADY_ADMINISTERED = "listItemsAlreadyAdministered cannot be null";
    private static final String NULL_LIST_ITEMS_TO_ADMINISTERED = "listItemsToAdminister cannot be null";
    private List<String> listItemsToAdminister = new ArrayList<String>();
    private List<String> listItemsAlreadyAdministered = new ArrayList<String>();
    private int numItemsToAdminister = 1;

    /**
     * Constructs a new {@link CatItemsToAdminister}.
     *
     * @param listItemsToAdminister        the list of remaining item identifiers
     * @param listItemsAlreadyAdministered the list of item identifiers that have
     *                                     already been administered
     * @param numItemsToAdminister         the number of item to administer per
     *                                     stage
     */
    public CatItemsToAdminister(List<String> listItemsToAdminister, List<String> listItemsAlreadyAdministered,
            int numItemsToAdminister) {
        if (listItemsToAdminister == null) {
            throw new NullPointerException(NULL_LIST_ITEMS_TO_ADMINISTERED);
        }
        if (listItemsAlreadyAdministered == null) {
            throw new NullPointerException(NULL_LIST_ITEMS_ALREADY_ADMINISTERED);
        }
        this.listItemsToAdminister = listItemsToAdminister;
        this.listItemsAlreadyAdministered = listItemsAlreadyAdministered;
        this.numItemsToAdminister = numItemsToAdminister;
    }

    /**
     * Constructs a new {@link CatItemsToAdminister}.
     *
     * @param listItemsToAdminister        the array of {@code String} characters of remaining item identifiers
     * @param listItemsAlreadyAdministered the array of {@code String} characters of item identifiers that have
     *                                     already been administered
     * @param numItemsToAdminister         the number of item to administer per
     *                                     stage
     */
    public CatItemsToAdminister(String[] listItemsToAdminister, String[] listItemsAlreadyAdministered,
            int numItemsToAdminister) {
        if (listItemsToAdminister == null) {
            throw new NullPointerException(NULL_LIST_ITEMS_TO_ADMINISTERED);
        }
        if (listItemsAlreadyAdministered == null) {
            throw new NullPointerException(NULL_LIST_ITEMS_ALREADY_ADMINISTERED);
        }
        this.listItemsToAdminister = Arrays.asList(listItemsToAdminister);
        this.listItemsAlreadyAdministered = Arrays.asList(listItemsAlreadyAdministered);
        this.numItemsToAdminister = numItemsToAdminister;
    }

    /**
     * Returns the list of remaining item identifiers.
     *
     * @return the list of remaining item identifiers
     */
    public List<String> getListItemsToAdminister() {
        return listItemsToAdminister;
    }

    /**
     * Returns the list of item identifiers that have already been administered.
     *
     * @return the list of item identifiers that have already been administered
     */
    public List<String> getListItemsAlreadyAdministered() {
        return listItemsAlreadyAdministered;
    }

    /**
     * Returns the number of item to administer per stage.
     *
     * @return the number of item to administer per stage
     */
    public int getNumItemsToAdminister() {
        return numItemsToAdminister;
    }

    /**
     * Returns the item identifiers to be administered for the stage.
     *
     * @return the item identifiers to be administered for the stage
     */
    public List<String> getItemsToAdmin() {
        return listItemsToAdminister.subList(0, numItemsToAdminister);
    }
}
