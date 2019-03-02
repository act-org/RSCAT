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

    private List<String> listItemsToAdminister = new ArrayList<String>();
    private List<String> listItemsAlreadyAdministered = new ArrayList<String>();
    private int numItemsToAdminister = 1;

    /**
     * Constructs a new {@link CatItemsToAdminister}.
     *
     * @param listItemsToAdminister the list of item identifiers to be administered
     * @param listItemsAlreadyAdministered the list of item identifiers that have already been administerd
     * @param numItemsToAdminister the number of item to administer per stage
     */
    public CatItemsToAdminister(List<String> listItemsToAdminister, List<String> listItemsAlreadyAdministered,
            int numItemsToAdminister) {
        if (listItemsToAdminister == null)
            throw new NullPointerException("listItemsToAdminister cannot be null");
        if (listItemsAlreadyAdministered == null)
            throw new NullPointerException("listItemsAlreadyAdministered cannot be null");
        this.listItemsToAdminister = listItemsToAdminister;
        this.listItemsAlreadyAdministered = listItemsAlreadyAdministered;
        this.numItemsToAdminister = numItemsToAdminister;
    }

    public CatItemsToAdminister(String[] listItemsToAdminister, String[] listItemsAlreadyAdministered,
            int numItemsToAdminister) {
        if (listItemsToAdminister == null)
            throw new NullPointerException("listItemsToAdminister cannot be null");
        if (listItemsAlreadyAdministered == null)
            throw new NullPointerException("listItemsAlreadyAdministered cannot be null");
        this.listItemsToAdminister = Arrays.asList(listItemsToAdminister);
        this.listItemsAlreadyAdministered = Arrays.asList(listItemsAlreadyAdministered);
        this.numItemsToAdminister = numItemsToAdminister;
    }

    public List<String> getListItemsToAdminister() {

        return listItemsToAdminister;
    }

    public List<String> getListItemsAlreadyAdministered() {
        return listItemsAlreadyAdministered;
    }

    public int getNumItemsToAdminister() {
        return numItemsToAdminister;
    }

    public List<String> getItemsToAdmin() {
        return listItemsToAdminister.subList(0, numItemsToAdminister);
    }
}