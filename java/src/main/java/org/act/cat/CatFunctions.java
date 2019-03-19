package org.act.cat;

import static org.act.util.PrimitiveArrays.select;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.act.util.ContentTable;
import org.act.util.PrimitiveArraySet;
import org.act.util.PrimitiveArrays;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class providing numeric functions.
 */
public final class CatFunctions {

    /**
     * A Logger instance for the CAT engine.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(CatFunctions.class);

    /**
     * Header for item fisher information.
     */
    private static final String HEADER_FISHER_INFORMATION = CatEngine.MapIndiceHeader.FISHER_INFORMATION.name();

    /**
     * Header for item identifiers.
     */
    private static final String HEADER_ITEM_IDENTIFIERS = CatEngine.MapIndiceHeader.ITEM_IDENTIFIERS.name();

    /**
     * Header for item administration status.
     */
    private static final String HEADER_ITEMS_ADMINISTERED = CatEngine.MapIndiceHeader.ITEMS_ADMINISTERED.name();

    /**
     * Header for passage identifiers.
     */
    private static final String HEADER_PASSAGE_IDENTIFIERS = CatEngine.MapIndiceHeader.PASSAGE_IDENTIFIERS.name();


    /**
     * Header for item order in passage
     */
    private static final String HEADER_PASSAGE_ITEM_ORDER = CatEngine.MapIndiceHeader.PASSAGE_ITEM_ORDER.name();

    /**
     * String "none"
     */
    private static final String STRING_NONE_LOWER_CASE = "none";

    /**
     * String "id"
     */
    private static final String STRING_ID = "id";

    /**
     * String "value"
     */
    private static final String STRING_VALUE = "value";

    /**
     * String "passage"
     */
    private static final String STRING_PASSAGE = "passage";

    /**
     * Private constructor for a utility class.
     */
    private CatFunctions() {
    }

    /**
     * Calculates the average item information value for each
     * passage in the shadow test. Items that are not affiliated with a passage
     * retain the same information values. Items not affiliated with a passage
     * are marked with the passage identifier "none"
     *
     * @param selectedItemIndices An integer array containing the item pool row
     *            indices associated with shadow test items.
     * @param itemPoolDataSet An I x C table with item indices mapped to item
     *            level data, where I is equal to the number of items in the
     *            pool, and C is the number of columns. There are three required
     *            columns: itemIdentifiers: a string array with the item
     *            identifiers (i.e., the item names) passageIdentifiers: a
     *            string array with the passage identifiers fisherInformation: a
     *            double array with the Fisher Information values, which depend
     *            on the current theta estimate
     * @return passageInfoList: a list of PassageInfo objects, with each object
     *         containing the following two fields: id: the unique passage ID
     *         value: average item information value for the passage passage:
     *         logical; true if the id is for a passage; false if the id is for
     *         an item The size of the list is equal to the number of unique
     *         passage identifiers plus the number of items not associated with
     *         a passage.
     */

    public static List<CatPassageInfo> calcPassageInfo(int[] selectedItemIndices, PrimitiveArraySet itemPoolDataSet) {

        // subset rows of item-level data table that correspond with items in
        // shadow test
        PrimitiveArraySet shadowTestDataSet = itemPoolDataSet.subSample(selectedItemIndices);

        // sort rows by passage id
        PrimitiveArraySet sortedItemsByPassage = shadowTestDataSet.groupSort(HEADER_PASSAGE_IDENTIFIERS);

        // get arrays from table
        String[] passageIdentifiers = sortedItemsByPassage.getStringArrayCopy(HEADER_PASSAGE_IDENTIFIERS);
        String[] itemIdentifiers = sortedItemsByPassage.getStringArray(HEADER_ITEM_IDENTIFIERS);
        double[] itemInformationValues = sortedItemsByPassage.getDoubleArrayCopy(HEADER_FISHER_INFORMATION);

        // create list to store passage info results
        List<CatPassageInfo> passageInfoList = new ArrayList<>();

        // calculate average info for each passage and store result in
        // passageInfoList
        String oldPassageId = "INITIALIZE";
        boolean oldIsPassage = true;
        double totalInfoValue = 0;
        int counter = 1;
        double averageInfoValue = 0;
        CatPassageInfo passageInfo = null;
        for (int i = 0; i < passageIdentifiers.length; i++) {
            boolean newIsPassage = true;
            String newPassageId = passageIdentifiers[i];
            double itemInfoValue = itemInformationValues[i];

            // check if item is not associated with a passage
            if (newPassageId.equalsIgnoreCase(STRING_NONE_LOWER_CASE)) {
                newPassageId = itemIdentifiers[i];
                newIsPassage = false;
            }
            if (i == 0) {
                oldPassageId = newPassageId;
                oldIsPassage = newIsPassage;
                totalInfoValue = totalInfoValue + itemInfoValue;
            } else if (i > 0) {

                // check if current item is in the same passage as previous item
                if (newPassageId.equals(oldPassageId)) {

                    // sum item info values within passage
                    totalInfoValue = totalInfoValue + itemInfoValue;

                    // count number of items within passage
                    counter++;
                } else {

                    // calculate average item info for passage
                    averageInfoValue = totalInfoValue / counter;
                    passageInfo = new CatPassageInfo(oldPassageId, averageInfoValue, oldIsPassage);

                    // store passage info in list
                    passageInfoList.add(passageInfo);

                    // reset values
                    oldPassageId = newPassageId;
                    oldIsPassage = newIsPassage;
                    totalInfoValue = itemInfoValue;
                    counter = 1;
                }
            }

            // if last passage identifier, then calculate and store passage info
            if (i == passageIdentifiers.length - 1) {
                averageInfoValue = totalInfoValue / counter;
                passageInfo = new CatPassageInfo(oldPassageId, averageInfoValue, oldIsPassage);
                passageInfoList.add(passageInfo);
            }
        }
        return passageInfoList;
    }

    /**
     *
     * This method is used within the pre-MIP processing logic
     * (getEligiblePassageItems) for the relative item ordering functionality
     * within echo; namely, this method ensures that any items in the current
     * passage with lower order numbers than the most recently administered item
     * should be made ineligible for inclusion on the next shadow test; note
     * that any items on the previous shadow test must be made eligible for the
     * next shadow test (regardless of the item order number)
     *
     * @param itemsAdministeredString a string array with item ids of previously
     *            administered items
     * @param itemPoolTable a ContentTable.RowOriented object with the item
     *            data; the columns used in this method are column 0 (item
     *            ids), column 1 (passage ids) and column 2 (passage item order
     *            values)
     * @return ineligibleIndicesItemOrder: an integer list of ineligible item
     *         indices
     */
    public static List<Integer> getIneligibleIndicesItemOrder(String[] itemsAdministeredString,
            ContentTable itemPoolTable) {

        // create list to store ineligible item indices
        List<Integer> ineligibleIndicesItemOrder = new ArrayList<>();

        // if at least one item has been administered, do the following logic
        if (itemsAdministeredString.length > 0) {

            // get item ids, passage ids, and passage item orders from
            // itemPoolTable
            String[] itemIds = new String[itemPoolTable.rowCount()];
            String[] passageIds = new String[itemPoolTable.rowCount()];
            String[] passageItemOrders = new String[itemPoolTable.rowCount()];
            Integer[] passageItemOrdersInt = new Integer[itemPoolTable.rowCount()];
            for (int i = 0; i < itemPoolTable.rowCount(); i++) {
                itemIds[i] = itemPoolTable.rows().get(i).get(0);
                passageIds[i] = itemPoolTable.rows().get(i).get(1);
                passageItemOrders[i] = itemPoolTable.rows().get(i).get(2);
                passageItemOrdersInt[i] = NumberUtils.toInt(passageItemOrders[i]);
            }

            // get item identifier and row index for last item administered
            String lastItemAdministered = itemsAdministeredString[itemsAdministeredString.length - 1];
            int indexLastItemAdministered = select(itemIds, lastItemAdministered)[0];

            // get passage item order number of last item administered
            String passageItemOrderLastItem = passageItemOrders[indexLastItemAdministered];

            // if a passage item order is specified, then perform the following
            // logic
            if (!passageItemOrderLastItem.equalsIgnoreCase(STRING_NONE_LOWER_CASE)) {

                // get passage id associated with last item administered
                String passageIdLastItemAdministered = passageIds[indexLastItemAdministered];

                // get indices of item ids associated with current passage
                int[] indicesItemsInCurrentPassage = select(passageIds, passageIdLastItemAdministered);

                // remove indices of already administered items
                int[] indicesAdministeredItems = select(itemIds, itemsAdministeredString);
                List<Integer> indicesItemsInCurrentPassageNotYetAdministered = new ArrayList<>();
                for (int i = 0; i < indicesItemsInCurrentPassage.length; i++) {
                    boolean itemAlreadyAdministered = false;
                    for (int j = 0; j < indicesAdministeredItems.length && !itemAlreadyAdministered; j++) {
                        if (indicesItemsInCurrentPassage[i] == indicesAdministeredItems[j]) {
                            itemAlreadyAdministered = true;
                        }
                    }
                    if (!itemAlreadyAdministered) {
                        indicesItemsInCurrentPassageNotYetAdministered.add(indicesItemsInCurrentPassage[i]);
                    }
                }

                // loop through item orders and save index of any ineligible
                // item (i.e., if item has order number less than order number
                // of current item
                for (int i = 0; i < indicesItemsInCurrentPassageNotYetAdministered.size(); i++) {
                    int index = indicesItemsInCurrentPassageNotYetAdministered.get(i);
                    if (passageItemOrdersInt[index] < passageItemOrdersInt[indexLastItemAdministered]) {
                        ineligibleIndicesItemOrder.add(index);
                    }
                }
            }
        }
        return ineligibleIndicesItemOrder;
    }

    /**
     * Determines whether each item in the item pool is eligible
     * or ineligible for inclusion in the next shadow test depending on the
     * passages that have been completed in the test.
     *
     *
     * @param itemsAdministeredString a string array with item ids of previously
     *            administered items
     * @param mapIndices An I x C table with item indices mapped to item level
     *            data, where I is equal to the number of items in the pool and
     *            C is equal to the number of columns. The three required
     *            columns are:
     *            <p>
     *            itemIndices: an integer array of the index values that are
     *            mapped to item level information
     *            <p>
     *            itemIdentifiers: a string array of the item identifiers (i.e.,
     *            the item names)
     *            <p>
     *            passageIdentifiers: a string array of the passage identifiers
     * @param itemPoolTable a ContentTable.RowOriented object with the item
     *            data; the columns used in this function are column zero (item
     *            ids) and column 2 (passage item order values)
     * @return itemEligibilityIndicatorsPassageManagement: a boolean array of
     *         item eligibility indicators with length equal to the number of
     *         items in the item pool; a value of true indicates that the
     *         associated item is eligible for inclusion in the next shadow
     *         test; a value of false indicates that the associated item is
     *         ineligible for inclusion in the next shadow test; this boolean
     *         array is used as input to the MIP solver and serves as a hard
     *         constraint in the model (i.e., the constraint will not be relaxed
     *         if there is no shadow test solution).
     */
    public static boolean[] getEligiblePassageItems(String[] itemsAdministeredString, PrimitiveArraySet mapIndices,
            ContentTable itemPoolTable) {

        // get primitive arrays from item pool data set
        String[] itemIdentifiersPool = mapIndices.getStringArray(HEADER_ITEM_IDENTIFIERS);
        String[] passageIdentifiersPool = mapIndices.getStringArray(HEADER_PASSAGE_IDENTIFIERS);

        // array for storing eligibility results (set all values to true)
        boolean[] eligiblePassageItemsBoolean = new boolean[passageIdentifiersPool.length];
        for (int i = 0; i < eligiblePassageItemsBoolean.length; i++) {
            eligiblePassageItemsBoolean[i] = true;
        }

        // create list for storing eligible item indices
        List<Integer> ineligibleItemIndicesList = new ArrayList<>();

        // the following logic is only necessary if at least one item has been
        // administered
        if (itemsAdministeredString.length > 0) {
            for (int i = 0; i < (itemsAdministeredString.length - 1); i++) {

                // get item identifier and row index for previous item
                int previousItemIndex = select(itemIdentifiersPool, itemsAdministeredString[i])[0];

                // get item identifier and row index for next item
                int nextItemIndex = select(itemIdentifiersPool, itemsAdministeredString[i + 1])[0];

                // get passage ids associated with previous item and next item
                String previousPassageId = passageIdentifiersPool[previousItemIndex];
                String nextPassageId = passageIdentifiersPool[nextItemIndex];

                // check to see if previous item is from passage
                boolean previousItemFromPassage = false;
                if (!passageIdentifiersPool[previousItemIndex].equalsIgnoreCase(STRING_NONE_LOWER_CASE)) {
                    previousItemFromPassage = true;
                }

                /*
                 * IF (a) next passage id is different than previous passage id
                 * AND (b) previous item is from a passage (i.e., not a discrete
                 * item with passage identifier "none") THEN make all items not
                 * yet administered that are associated with the previous
                 * passage ineligible for inclusion in the next shadow test
                 */
                if (!(previousPassageId.equalsIgnoreCase(nextPassageId)) && previousItemFromPassage) {

                    // get item indices associated with previous passage id
                    int[] previousPassageIndices = select(passageIdentifiersPool, previousPassageId);

                    // only change eligibility to false for items that have not
                    // already been administered
                    /*
                     * check each item associated with previous passage against
                     * the previously administered items to determine whether
                     * item has already been administered
                     */
                    for (int j = 0; j < previousPassageIndices.length; j++) {
                        boolean itemAlreadyAdministered = false;

                        // loop through previously administered items for each
                        // check
                        for (int k = 0; k < itemsAdministeredString.length; k++) {
                            int administeredItemIndex = select(itemIdentifiersPool, itemsAdministeredString[k])[0];
                            if (previousPassageIndices[j] == administeredItemIndex) {
                                itemAlreadyAdministered = true;
                            }
                        }

                        // if item has not yet been administered then save index
                        // to ineligibleItemIndicesList
                        if (!itemAlreadyAdministered) {
                            ineligibleItemIndicesList.add(previousPassageIndices[j]);
                        }
                    }
                }
            }

            // call function to get indices of ineligible items from item
            // passage ordering logic
            List<Integer> ineligibleIndicesItemOrder = getIneligibleIndicesItemOrder(itemsAdministeredString,
                    itemPoolTable);

            // add item order ineligible indices to passage management
            // ineligible indices
            ineligibleItemIndicesList.addAll(ineligibleIndicesItemOrder);

            // update eligiblePassageItemsBoolean array to false value for every
            // ineligible item
            for (int j = 0; j < ineligibleItemIndicesList.size(); j++) {
                int index = ineligibleItemIndicesList.get(j).intValue();
                eligiblePassageItemsBoolean[index] = false;
            }
        }

        return eligiblePassageItemsBoolean;
    }

    /**
     * This method is used within the prepShadowTest function. It orders items
     * according to the relative order specified in the "Passage Item Order"
     * column of the item.csv file. After the next passage to administer is
     * selected, this function is called to find the next item to administer
     * given the item ordering column in the itemPoolTable
     *
     * @param remainingItemsInCurrentPassage a string array with item ids of
     *            remaining items to administer from current passage
     * @param itemPoolTable a ContentTable.RowOriented object with the item
     *            data; the columns used in this function are column zero (item
     *            ids) and column 2 (passage item order values)
     * @return nextItemsToAdminister: a string array with the next items to
     *         administer; if there is an item order specified, then this will
     *         have the remaining items in the current passage in the correct
     *         order to administer; if there is not an item order specified,
     *         this object will be null
     */

    public static String[] orderPassageItems(String[] remainingItemsInCurrentPassage, ContentTable itemPoolTable) {

        // get item identifiers & passage item orders from itemPoolTable
        String[] itemIds = new String[itemPoolTable.rowCount()];
        String[] passageItemOrders = new String[itemPoolTable.rowCount()];
        int[] passageItemOrdersInt = new int[itemPoolTable.rowCount()];
        for (int i = 0; i < itemPoolTable.rowCount(); i++) {
            itemIds[i] = itemPoolTable.rows().get(i).get(0);
            passageItemOrders[i] = itemPoolTable.rows().get(i).get(2);
            passageItemOrdersInt[i] = NumberUtils.toInt(passageItemOrders[i]);
        }

        // get indices of remaining items to administer in current passage
        int[] indicesRemainingItems = select(itemIds, remainingItemsInCurrentPassage);

        /*
         * if there is at least one item remaining to administer from the
         * current passage, check the order value for the first remaining item
         * to see if order values are used; if they are not used, the first item
         * will have order value "none"
         */
        boolean isItemOrder = false;
        if (indicesRemainingItems.length > 0) {
            if (!passageItemOrders[indicesRemainingItems[0]].equalsIgnoreCase(STRING_NONE_LOWER_CASE)) {
                isItemOrder = true;
            }
        }

        // create primitive array set to map item ids to passage item orders
        PrimitiveArraySet mapIndices = new PrimitiveArraySet().withStringArray(HEADER_PASSAGE_IDENTIFIERS, itemIds)
                .withIntArray(HEADER_PASSAGE_ITEM_ORDER, passageItemOrdersInt);

        // subsample from primitive array set only remaining items for current
        // passage
        PrimitiveArraySet remainingItemsArraySet = mapIndices.subSample(indicesRemainingItems);

        String[] nextItemsToAdminister = null;
        if (isItemOrder) {
            // sort by passage item orders
            PrimitiveArraySet sortedRemainingItemsArraySet = remainingItemsArraySet
                    .groupSort(HEADER_PASSAGE_IDENTIFIERS);
            // get item ids
            nextItemsToAdminister = sortedRemainingItemsArraySet.getStringArrayCopy(HEADER_ITEM_IDENTIFIERS);
        }
        return nextItemsToAdminister;
    }

    /**
     * Prepares the next item to be delivered and included in
     * {@link CatItemsToAdminister#listItemsToAdminister}.
     * <p>
     * The next item is determined by sorting the remaining items with descending Fisher
     * Information values while conforming to the passage management logics. The method
     * performs a post-processing step (i.e., after the mixed-integer
     * solver program has returned a shadow test) to ensure that items
     * associated with the same passage are grouped together in the shadow test.
     * Namely, the logic checks whether the previous item administered was from
     * a passage and whether there are any remaining items from the passage. If
     * there are any remaining items, then the item with the highest information
     * value is administered next. If the previous item administered was not
     * from a passage or the previous item administered was from a passage and
     * there are no remaining items from the passage, then the passage or
     * discrete item with the highest information value is administered next.
     * Note that passage information values are determined by taking the average
     * information value of the associated items in the shadow test (using the
     * calcPassageInfo function).
     * <p>
     * The method also ensures that passage order constraints are correctly applied when
     * determining the next item(s) to administer. The new logic has been fully
     * integrated with the existing passage management logic that ensures items
     * are administered together, and that the item with the highest Fisher
     * information value at the current ability estimate is administered next
     * given the other test constraints.
     *
     * @param itemsAdminArray a string array with item ids of previously
     *            administered items
     * @param selectedItemIndices An integer array with length equal to the test
     *            length. This array contains indices that are mapped to the
     *            selected items in the shadow test.
     * @param mapIndices An I x C table with item indices mapped to item level
     *            data, where I is equal to the number of items in the pool and
     *            C is equal to the number of columns. The five required columns
     *            are: itemIndices: an integer array of the index values that
     *            are mapped to item level information itemIdentifiers: a string
     *            array of the item identifiers (i.e., the item names)
     *            passageIdentifiers: a string array of the passage identifiers
     *            fisherInformation: a double array of the Fisher Information
     *            values, which depend on the current theta estimate
     *            itemsAdministered: a boolean array of the items administered.
     *            A value of false indicates that the item has not been
     *            administered, and a value of true indicates that the item has
     *            been administered.
     * @param itemPoolTable a ContentTable.RowOriented object with the item data
     * @param passagePoolTable a ContentTable.RowOriented object with the
     *            passage data
     * @param passageRowIndexSequence an Integer list with the row indices of
     *            the passage order constraints (output from the solver)
     * @return itemsToAdminister: an object that contains two fields:
     *         listItemsToAdminister: a string array with the item IDs of the
     *         remaining items to administer in the shadow test, sorted by
     *         Fisher Information value in descending order (only the first item in the array
     *         is guaranteed with the correct order)
     *         numItemsToAdminister: an integer value indicating the number of
     *         items to administer in the current adaptive stage.
     */
    private static CatItemsToAdminister prepShadowTestNextItem(String[] itemsAdminArray,
            int[] selectedItemIndices, PrimitiveArraySet mapIndices, ContentTable itemPoolTable,
            ContentTable passagePoolTable, List<Integer> passageRowIndexSequence) {

        long start = System.currentTimeMillis();

        // subset rows of item-level data table that correspond with items in
        // shadow test
        PrimitiveArraySet shadowMapIndices = mapIndices.subSample(selectedItemIndices);

        // select items administered array from table
        boolean[] itemsAdministered = shadowMapIndices.getBooleanArrayCopy(HEADER_ITEMS_ADMINISTERED);
        int[] falseIndices = select(itemsAdministered, false);
        int[] trueIndices = select(itemsAdministered, true);

        // subset rows of item-level data table that correspond with items that
        // have NOT been administered
        PrimitiveArraySet notAdminShadowMapIndices = shadowMapIndices.subSample(falseIndices);

        // subset rows of item-level data table that correspond with items that
        // have already been administered
        PrimitiveArraySet alreadyAdminShadowMapIndices = shadowMapIndices.subSample(trueIndices);

        // sort items NOT administered by Fisher Information values
        PrimitiveArraySet sortedItems = notAdminShadowMapIndices.groupSort(HEADER_FISHER_INFORMATION);

        // PASSAGE MANAGEMENT CODE BELOW

        String nextItemToAdminister = null;
        // check if there are passages
        if (passagePoolTable != null && passagePoolTable.rows().size() > 0) {
            // specify decision variable
            boolean nextItemSelected = false;
            // check if there are remaining items associated with passages in
            // the current shadow test
            String[] remainingShadowTestPassageIds = sortedItems.getStringArrayCopy(HEADER_PASSAGE_IDENTIFIERS);
            boolean anyRemainingPassageItems = false;
            for (int i = 0; i < remainingShadowTestPassageIds.length; i++) {
                if (!remainingShadowTestPassageIds[i].equalsIgnoreCase(STRING_NONE_LOWER_CASE)) {
                    anyRemainingPassageItems = true;
                }
            }

            // if there are any remaining passage items in shadow test, perform
            // the following logic
            if (anyRemainingPassageItems) {

                // if at least one item has been administered, perform the
                // following logic
                if (itemsAdminArray != null && itemsAdminArray.length > 0) {

                    // get item pool row index of last item administered
                    String[] shadowTestItemIds = shadowMapIndices.getStringArrayCopy(HEADER_ITEM_IDENTIFIERS);
                    String[] shadowTestPassageIds = shadowMapIndices.getStringArrayCopy(HEADER_PASSAGE_IDENTIFIERS);
                    int rowIndexLastItemAdministered = select(shadowTestItemIds,
                            itemsAdminArray[itemsAdminArray.length - 1])[0];

                    // get passage id associated with last item administered
                    String passageIdLastItemAdministered = shadowTestPassageIds[rowIndexLastItemAdministered];
                    boolean lastItemFromPassage = false;

                    // check to see if last item administered was associated
                    // with a passage
                    if (!passageIdLastItemAdministered.equalsIgnoreCase(STRING_NONE_LOWER_CASE)) {
                        lastItemFromPassage = true;
                    }

                    // if last item administered is associated with a passage,
                    // perform the following logic
                    if (lastItemFromPassage) {

                        // get indices for remaining items associated with
                        // current passage
                        int[] remainingItemIndicesForCurrentPassage = select(remainingShadowTestPassageIds,
                                passageIdLastItemAdministered);

                        // if there are any remaining items associated with the
                        // current passage, then select the most informative
                        // remaining item (Outcome #1)
                        if (remainingItemIndicesForCurrentPassage.length > 0) {

                            // subset rows of item-level data table that
                            // correspond with remaining items associated with
                            // the current passage
                            PrimitiveArraySet remainingItemsInCurrentPassageDataSet = sortedItems
                                    .subSample(remainingItemIndicesForCurrentPassage);

                            // select item identifiers from table for items
                            // associated with current passage
                            String[] remainingItemsInCurrentPassageArray = remainingItemsInCurrentPassageDataSet
                                    .getStringArrayCopy(HEADER_ITEM_IDENTIFIERS);

                            // sort by item order specified in item.csv file
                            // (note that if there isn't an order specified, the
                            // order will not be changed
                            String[] sortedItemsInCurrentPassageArray = orderPassageItems(
                                    remainingItemsInCurrentPassageArray, itemPoolTable);
                            // TODO
                            // if item order is specified, then select next item
                            // based on order
                            if (sortedItemsInCurrentPassageArray != null) {
                                nextItemToAdminister = sortedItemsInCurrentPassageArray[0];
                            } else {

                                // if item order is not specified, select final
                                // element in array (items are already sorted in
                                // ascending order by fisher information
                                nextItemToAdminister = remainingItemsInCurrentPassageArray[remainingItemsInCurrentPassageArray.length -
                                        1];
                            }

                            // change decision variable to indicate that next
                            // item has been selected
                            nextItemSelected = true;
                        }
                    }
                }

                // if the next item to administer has not been selected yet
                if (!nextItemSelected) {

                    // get passage ids from passage table
                    String[] passageIdsFromPassageTable = new String[passagePoolTable.rowCount()];
                    for (int i = 0; i < passagePoolTable.rowCount(); i++) {
                        passageIdsFromPassageTable[i] = passagePoolTable.rows().get(i).get(0);
                    }

                    // calculate passage information values
                    List<CatPassageInfo> passageInfoList = calcPassageInfo(selectedItemIndices, mapIndices);
                    String[] idFromCalcPassageInfo = new String[passageInfoList.size()];
                    double[] valueFromCalcPassageInfo = new double[passageInfoList.size()];
                    boolean[] passageFromCalcPassageInfo = new boolean[passageInfoList.size()];

                    // create primitive array set from list
                    for (int i = 0; i < passageInfoList.size(); i++) {
                        idFromCalcPassageInfo[i] = passageInfoList.get(i).getId();
                        valueFromCalcPassageInfo[i] = passageInfoList.get(i).getValue();
                        passageFromCalcPassageInfo[i] = passageInfoList.get(i).getIsPassage();
                    }
                    PrimitiveArraySet passageInfoDataSet = new PrimitiveArraySet()
                            .withStringArray(STRING_ID, idFromCalcPassageInfo)
                            .withDoubleArray(STRING_VALUE, valueFromCalcPassageInfo)
                            .withBooleanArray(STRING_PASSAGE, passageFromCalcPassageInfo);

                    // if passage order constraints are used, then perform the
                    // following logic
                    if (passageRowIndexSequence != null && passageRowIndexSequence.size() > 0) {

                        // get passage ids in correct order from order
                        // constraint passage table indices
                        String[] passageOrderConstraintIds = new String[passageRowIndexSequence.size()];
                        for (int i = 0; i < passageRowIndexSequence.size(); i++) {
                            passageOrderConstraintIds[i] = passageIdsFromPassageTable[passageRowIndexSequence.get(i)];
                        }

                        // initialize variable for storing next passage to
                        // administer
                        String nextPassageToAdminister = null;

                        // check to see if an item associated with a passage has
                        // been administered yet, and if so, store the passage
                        // id of the most recent passage administered
                        String[] shadowTestItemIds = shadowMapIndices.getStringArrayCopy(HEADER_ITEM_IDENTIFIERS);
                        String[] shadowTestPassageIds = shadowMapIndices.getStringArrayCopy(HEADER_PASSAGE_IDENTIFIERS);
                        boolean itemAssociatedWithPassageHasBeenAdministered = false;
                        Integer previousPassageAdministeredIndex = null;
                        for (int i = 0; i < itemsAdminArray.length; i++) {
                            int rowIndexForPassageIdCheck = select(shadowTestItemIds, itemsAdminArray[i])[0];
                            String passageIdForPassageIdCheck = shadowTestPassageIds[rowIndexForPassageIdCheck];

                            // if there is a passage id associated with the
                            // administered item, then use the passage id to get
                            // the corresponding index of the passage constraint
                            // array
                            // NOTE: if there is a passage id associated with a
                            // more recently administered item, then overwrite
                            // the previously stored index
                            if (!passageIdForPassageIdCheck.equalsIgnoreCase(STRING_NONE_LOWER_CASE)) {
                                previousPassageAdministeredIndex = select(passageOrderConstraintIds,
                                        passageIdForPassageIdCheck)[0];
                                itemAssociatedWithPassageHasBeenAdministered = true;
                            }
                        }

                        /*
                         * if an item associated with a passage has been
                         * administered (i.e., at least the first passage has
                         * been administered), then use the previously obtained
                         * index to find the next passage to administer
                         */
                        if (itemAssociatedWithPassageHasBeenAdministered) {

                            // there should always be a passage id left to
                            // administer at this point in the logic, but check
                            // anyway just to make sure
                            if (passageOrderConstraintIds.length > (previousPassageAdministeredIndex + 1)) {
                                nextPassageToAdminister = passageOrderConstraintIds[previousPassageAdministeredIndex +
                                        1];
                            }
                        }

                        /*
                         * if no items have been administered or all items
                         * administered have been discrete items(i.e., the first
                         * passage has not yet been delivered), then the next
                         * passage to administer is the first passage in the
                         * constraint list
                         */
                        if (itemsAdminArray.length == 0 || !itemAssociatedWithPassageHasBeenAdministered) {
                            nextPassageToAdminister = passageOrderConstraintIds[0];
                        }

                        // if nextPassageToAdminister is not null, then find
                        // indices of all discrete items, as well as index of
                        // nextPassageToAdminister from calcPassageInfo results
                        if (nextPassageToAdminister != null) {
                            List<Integer> passageConstraintDataSetIndices = new ArrayList<>();
                            for (int i = 0; i < idFromCalcPassageInfo.length; i++) {

                                // if the row has a discrete item or the row has
                                // an id matching the passage id of the next
                                // passage to administer, then get the index of
                                // the row
                                if (!passageFromCalcPassageInfo[i] ||
                                        idFromCalcPassageInfo[i].equals(nextPassageToAdminister)) {
                                    passageConstraintDataSetIndices.add(i);
                                }
                            }

                            // convert list of row indices to array of row
                            // indices
                            int[] passageConstraintDataSetIndicesArray = new int[passageConstraintDataSetIndices
                                    .size()];
                            for (int i = 0; i < passageConstraintDataSetIndices.size(); i++) {
                                passageConstraintDataSetIndicesArray[i] = passageConstraintDataSetIndices.get(i);
                            }

                            // subset rows of item-level data table that
                            // correspond with discrete items and next passage
                            // to administer
                            PrimitiveArraySet passageInfoDataSetGivenPassageOrderConstraints = passageInfoDataSet
                                    .subSample(passageConstraintDataSetIndicesArray);

                            // sort items NOT administered by Fisher Information
                            // values
                            PrimitiveArraySet passageOrderConstraintDataSetSorted = passageInfoDataSetGivenPassageOrderConstraints
                                    .groupSort(STRING_VALUE);

                            // subsample final row of data set
                            PrimitiveArraySet passageOrderConstraintMostInformative = passageOrderConstraintDataSetSorted
                                    .subSample(passageConstraintDataSetIndices.size() - 1);

                            // get id and passage indicator from primitive array
                            // set
                            String passageOrderConstraintMostInformativePassageId = passageOrderConstraintMostInformative
                                    .getStringArray(STRING_ID)[0];
                            boolean passageOrderConstraintMostInformativePassageIndicator = passageOrderConstraintMostInformative
                                    .getBooleanArray(STRING_PASSAGE)[0];

                            // if most informative id is associated with a
                            // passage, then perform the following logic
                            boolean noRemainingItemsInPassage = true;
                            boolean noNewDiscrete = true;
                            String[] remainingShadowTestPassageIdsNextPassage = new String[0];
                            int[] remainingItemIndicesNextPassage = new int[0];

                            // loop through all passage and discrete items until
                            // either a passage with remaining items is found or
                            // a discrete item is found
                            int counter = 1;
                            while (noRemainingItemsInPassage && noNewDiscrete && counter <= passageInfoList.size()) {

                                // subSample each row of data until a passage is
                                // found with remaining items or a discrete item
                                // is found
                                passageOrderConstraintMostInformative = passageOrderConstraintDataSetSorted
                                        .subSample(passageConstraintDataSetIndices.size() - counter);

                                // get id and passage indicator from primitive
                                // array set
                                passageOrderConstraintMostInformativePassageId = passageOrderConstraintMostInformative
                                        .getStringArray(STRING_ID)[0];
                                passageOrderConstraintMostInformativePassageIndicator = passageOrderConstraintMostInformative
                                        .getBooleanArray(STRING_PASSAGE)[0];

                                // get indices for remaining items associated
                                // with current passage
                                remainingShadowTestPassageIdsNextPassage = sortedItems
                                        .getStringArrayCopy(HEADER_PASSAGE_IDENTIFIERS);
                                remainingItemIndicesNextPassage = select(remainingShadowTestPassageIdsNextPassage,
                                        passageOrderConstraintMostInformativePassageId);

                                // check if there are any remaining items
                                // associated with next passage to administer
                                if (remainingItemIndicesNextPassage.length > 0) {

                                    // subset rows of item-level data table that
                                    // correspond with items associated with the
                                    // current passage
                                    PrimitiveArraySet remainingItemsInCurrentPassageSet = sortedItems
                                            .subSample(remainingItemIndicesNextPassage);

                                    // select item identifiers from table for
                                    // items associated with remaining items in
                                    // most informative passage
                                    String[] remainingItemsInMostInformativePassageArray = remainingItemsInCurrentPassageSet
                                            .getStringArrayCopy(HEADER_ITEM_IDENTIFIERS);

                                    // sort by item order specified in item.csv
                                    // file (note that if there isn't an order
                                    // specified, the order will not be changed
                                    String[] sortedItemsInCurrentPassageArray = orderPassageItems(
                                            remainingItemsInMostInformativePassageArray, itemPoolTable); // TODO

                                    // if item order is specified, then select
                                    // next item based on order
                                    if (sortedItemsInCurrentPassageArray != null) {
                                        nextItemToAdminister = sortedItemsInCurrentPassageArray[0];
                                    } else {

                                        // if item order is not specified,
                                        // select final element in array (items
                                        // are already sorted in ascending order
                                        // by fisher information
                                        nextItemToAdminister = remainingItemsInMostInformativePassageArray[remainingItemsInMostInformativePassageArray.length -
                                                1];
                                    }
                                    noRemainingItemsInPassage = false;
                                }

                                // if the item is discrete, check if it has been
                                // administered yet
                                if (!passageOrderConstraintMostInformativePassageIndicator) {
                                    int[] indexAlreadyAdministeredItem = PrimitiveArrays.select(itemsAdminArray,
                                            passageOrderConstraintMostInformativePassageId);
                                    if (indexAlreadyAdministeredItem.length == 0) {

                                        // if most informative id is not
                                        // associated with a passage, then the
                                        // "id" from the calcPassageInfo table
                                        // is actually the item id
                                        nextItemToAdminister = passageOrderConstraintMostInformativePassageId;
                                        noNewDiscrete = false;
                                    }
                                }
                                counter++;
                            }
                        }
                    } else {

                        // if no passage order constraints are used, then do the
                        // following logic sort ids by average Fisher Information value
                        PrimitiveArraySet passageInfoSorted = passageInfoDataSet.groupSort(STRING_VALUE);

                        // subSample final row of data set
                        PrimitiveArraySet passageInfoMostInformative = passageInfoSorted
                                .subSample(passageInfoList.size() - 1);

                        // get id and passage indicator from primitive array set
                        String mostInformativePassageId = passageInfoMostInformative.getStringArray(STRING_ID)[0];
                        boolean mostInformativePassageIndicator = passageInfoMostInformative
                                .getBooleanArray(STRING_PASSAGE)[0];

                        // if most informative id is associated with a passage,
                        // then perform the following logic
                        boolean noRemainingItemsInPassage = true;
                        boolean noNewDiscrete = true;
                        String[] remainingShadowTestPassageIdsNextPassage = new String[0];
                        int[] remainingItemIndicesNextPassage = new int[0];

                        // loop through all passage and discrete items until
                        // either a passage with remaining items is found or a
                        // discrete item is found
                        int counter = 1;
                        while (noRemainingItemsInPassage && noNewDiscrete && counter <= passageInfoList.size()) {

                            // subSample each row of data until a passage is
                            // found with remaining items or a discrete item is
                            // found
                            passageInfoMostInformative = passageInfoSorted.subSample(passageInfoList.size() - counter);

                            // get id and passage indicator from primitive array
                            // set
                            mostInformativePassageId = passageInfoMostInformative.getStringArray(STRING_ID)[0];
                            mostInformativePassageIndicator = passageInfoMostInformative.getBooleanArray(STRING_PASSAGE)[0];

                            // get indices for remaining items associated with
                            // current passage
                            remainingShadowTestPassageIdsNextPassage = sortedItems
                                    .getStringArrayCopy(HEADER_PASSAGE_IDENTIFIERS);
                            remainingItemIndicesNextPassage = select(remainingShadowTestPassageIdsNextPassage,
                                    mostInformativePassageId);

                            // check if there are any remaining items associated
                            // with most informative passage
                            if (remainingItemIndicesNextPassage.length > 0) {

                                // subset rows of item-level data table that
                                // correspond with items associated with the
                                // current passage
                                PrimitiveArraySet remainingItemsInCurrentPassageSet = sortedItems
                                        .subSample(remainingItemIndicesNextPassage);

                                // select item identifiers from table for items
                                // associated with remaining items in most
                                // informative passage
                                String[] remainingItemsInMostInformativePassageArray = remainingItemsInCurrentPassageSet
                                        .getStringArrayCopy(HEADER_ITEM_IDENTIFIERS);

                                // sort by item order specified in item.csv file
                                // (note that if there isn't an order specified,
                                // the order will not be changed
                                String[] sortedItemsInCurrentPassageArray = orderPassageItems(
                                        remainingItemsInMostInformativePassageArray, itemPoolTable); // TODO

                                // if item order is specified, then select next
                                // item based on order
                                if (sortedItemsInCurrentPassageArray != null) {
                                    nextItemToAdminister = sortedItemsInCurrentPassageArray[0];
                                } else {

                                    // if item order is not specified, select
                                    // final element in array (items are already
                                    // sorted in ascending order by fisher
                                    // information
                                    nextItemToAdminister = remainingItemsInMostInformativePassageArray[remainingItemsInMostInformativePassageArray.length -
                                            1];
                                }
                                noRemainingItemsInPassage = false;
                            }

                            // if the item is discrete, check if it has been
                            // administered yet
                            if (!mostInformativePassageIndicator) {
                                int[] indexAlreadyAdministeredItem = PrimitiveArrays.select(itemsAdminArray,
                                        mostInformativePassageId);
                                if (indexAlreadyAdministeredItem.length == 0) {

                                    // if most informative id is not associated
                                    // with a passage, then the "id" from the
                                    // calcPassageInfo table is actually the
                                    // item id
                                    nextItemToAdminister = mostInformativePassageId;
                                    noNewDiscrete = false;
                                }
                            }
                            counter++;
                        }
                    }
                }
            }

            // END OF NEW PASSAGE MANAGEMENT LOGIC; if next item to administer
            // has not been selected yet, then original logic for discrete item
            // pools only will apply
        }

        // select item identifiers from table for items NOT administered
        String[] listItemsToAdminister = sortedItems.getStringArrayCopy(HEADER_ITEM_IDENTIFIERS);

        // select item identifiers from table for items already administered
        String[] listItemsAlreadyAdministered = alreadyAdminShadowMapIndices
                .getStringArrayCopy(HEADER_ITEM_IDENTIFIERS);

        // reverse order item ids (i.e., sort by Fisher Information in
        // descending order) for items NOT administered
        List<String> listItemsToAdministerTemp = Arrays.asList(listItemsToAdminister);
        Collections.reverse(listItemsToAdministerTemp);
        listItemsToAdminister = (String[]) listItemsToAdministerTemp.toArray();

        // if next item to administer was selected from new passage management
        // logic, then remove it from the list of items to administer and put it
        // at the beginning of the list
        if (nextItemToAdminister != null) {
            Integer indexForNextItem = null;
            for (int i = 0; i < listItemsToAdminister.length; i++) {
                if (listItemsToAdminister[i].equals(nextItemToAdminister)) {
                    indexForNextItem = i;
                }
            }
            String[] newItemsToAdminister = new String[listItemsToAdminister.length];

            // next best item to administer
            newItemsToAdminister[0] = listItemsToAdminister[indexForNextItem];
            // remaining items to administer in shadow test
            int counter = 1;
            for (int j = 0; j < listItemsToAdminister.length; j++) {
                if (j != indexForNextItem) {
                    newItemsToAdminister[counter] = listItemsToAdminister[j];
                    counter++;
                }
            }

            // put new items to administer in original list
            listItemsToAdminister = newItemsToAdminister;
        }

        // store list in data object with number of items to administer
        CatItemsToAdminister itemsToAdminister = new CatItemsToAdminister(listItemsToAdminister,
                listItemsAlreadyAdministered, 1);

        long end = System.currentTimeMillis();
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("prepShadowTestNextItem time:" + (end - start));
        }

        return itemsToAdminister;
    }

    /**
     * Prepares a shadow test to be sent to either the test
     * delivery system or the simulation engine.
     * <p>
     * Namely, the function removes
     * items from the shadow test that have already been administered and sorts
     * the remaining items by Fisher Information values in descending order and based on the
     * passage management logics.
     *
     * @param itemsAdminArray a string array with item ids of previously
     *            administered items
     * @param selectedItemIndices An integer array with length equal to the test
     *            length. This array contains indices that are mapped to the
     *            selected items in the shadow test.
     * @param mapIndices An I x C table with item indices mapped to item level
     *            data, where I is equal to the number of items in the pool and
     *            C is equal to the number of columns. The five required columns
     *            are: itemIndices: an integer array of the index values that
     *            are mapped to item level information itemIdentifiers: a string
     *            array of the item identifiers (i.e., the item names)
     *            passageIdentifiers: a string array of the passage identifiers
     *            fisherInformation: a double array of the Fisher Information
     *            values, which depend on the current theta estimate
     *            itemsAdministered: a boolean array of the items administered.
     *            A value of false indicates that the item has not been
     *            administered, and a value of true indicates that the item has
     *            been administered.
     * @param itemPoolTable a ContentTable.RowOriented object with the item data
     * @param passagePoolTable a ContentTable.RowOriented object with the
     *            passage data
     * @param passageRowIndexSequence an Integer list with the row indices of
     *            the passage order constraints (output from the solver)
     * @return itemsToAdminister: an object that contains two fields:
     *         listItemsToAdminister: a string array with the item IDs of the
     *         remaining items to administer in the shadow test, sorted by
     *         Fisher Information value in descending order
     *         numItemsToAdminister: an integer value indicating the number of
     *         items to administer in the current adaptive stage.
     */
    public static CatItemsToAdminister prepShadowTest(String[] itemsAdminArray, int[] selectedItemIndices,
            PrimitiveArraySet mapIndices, ContentTable itemPoolTable, ContentTable passagePoolTable,
            List<Integer> passageRowIndexSequence) {

        long start = System.currentTimeMillis();

        Map<String, Integer> itemIdToIndexMap = genMap(mapIndices.getStringArray(HEADER_ITEM_IDENTIFIERS));
        List<String> tempItemsAdmin = new ArrayList<>(Arrays.asList(itemsAdminArray));
        CatItemsToAdminister itemsToAdminister = prepShadowTestNextItem(itemsAdminArray, selectedItemIndices,
                mapIndices, itemPoolTable, passagePoolTable, passageRowIndexSequence);

        // iterate to get items with correct orders in itemsToAdminister
        while (itemsToAdminister.getListItemsToAdminister().size() > 0) {
            String nextItem = itemsToAdminister.getListItemsToAdminister().get(0);
            tempItemsAdmin.add(nextItem);
            mapIndices.getBooleanArray(HEADER_ITEMS_ADMINISTERED)[itemIdToIndexMap.get(nextItem)] = true;
            itemsToAdminister = prepShadowTestNextItem(tempItemsAdmin.toArray(new String[] {}), selectedItemIndices,
                    mapIndices, itemPoolTable, passagePoolTable, passageRowIndexSequence);
        }

        String[] itemsToAdmin = tempItemsAdmin.subList(itemsAdminArray.length, tempItemsAdmin.size())
                .toArray(new String[] {});

        long end = System.currentTimeMillis();

        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("prepShadowTest time:" + (end - start));
        }

        CatItemsToAdminister catItemsToAdminister = new CatItemsToAdminister(itemsToAdmin, itemsAdminArray, 1);

        return catItemsToAdminister;

    }

    /**
     * Converts a string array of item identifiers to a {@link Map} with identifiers as keys
     * and indices as values.
     *
     * @param itemIdentifiers a string array of item identifiers to be converted
     * @return a converted <code>Map</code>
     */
    private static Map<String, Integer> genMap(String[] itemIdentifiers) {
        Map<String, Integer> map = new HashMap<>();
        for (int i = 0; i < itemIdentifiers.length; i++) {
            map.put(itemIdentifiers[i], i);
        }
        return map;
    }

    /**
     * Calculates item information for I items, given the current value of
     * thetaEst.
     *
     * @param itemPar : an I X P matrix containing item parameters, where I is
     *            the number of items and P is the number of parameters
     * @param thetaEst : scalar value of ability estimate
     * @return fisherInformation : a double array of length I containing item
     *         information values given thetaEst
     */
    public static double[] calcInfo(RealMatrix itemPar, double thetaEst) {
        int parNum = itemPar.getRowDimension();
        double[] fisherInformation = new double[parNum];
        for (int i = 0; i < parNum; i++) {
            double a = itemPar.getEntry(i, 0);
            double b = itemPar.getEntry(i, 1);
            double c = itemPar.getEntry(i, 2);
            double D = itemPar.getEntry(i, 3);
            double p = getProb3PL(a, b, c, D, thetaEst);
            // double p = c + (1 - c) / (1 + Math.exp(-a * (thetaEst - b)));
            double q = 1 - p;
            fisherInformation[i] = D * D * a * a * (q / p) * ((p - c) / (1 - c)) * ((p - c) / (1 - c));
        }
        return fisherInformation;
    }

    /**
     * Calculates item information for a single item.
     *
     * @param theta the ability value
     * @param a the value of item parameter A
     * @param b the value of item parameter B
     * @param c the value of item parameter C
     * @param D the value of parameter D
     * @return item information
     */
    public static double calcInfo(double theta, double a, double b, double c, double D) {

        double p = getProb3PL(a, b, c, D, theta);
        double q = 1 - p;
        double fisherInformation = D * D * a * a * (q / p) * ((p - c) / (1 - c)) * ((p - c) / (1 - c));
        return fisherInformation;
    }

    /**
     * Response function for 3PL model with D scaling constant (i.e.,
     * probability of a correct response conditional on theta)
     *
     * @param a the value of item parameter A
     * @param b the value of item parameter B
     * @param c the value of item parameter C
     * @param D the value of parameter D
     * @param theta the ability value
     * @return the probability of a correct response conditional on theta
     */
    public static double getProb3PL(double a, double b, double c, double D, double theta) {
        double p = c + (1.0d - c) / (1.0d + Math.exp(-D * a * (theta - b)));
        return p;
    }

    /**
     * This function calculates the posterior expected Fisher information.
     *
     * @param thetaDraws theta samples
     * @param aDraws item parameter A samples
     * @param bDraws item parameter B samples
     * @param cDraws item parameter C samples
     * @param D item parameter D
     * @return posterior expected Fisher information
     */
    public static double calPostExpInfo(double[] thetaDraws, double[] aDraws, double[] bDraws, double[] cDraws,
            double D) {
        int sampleLength = thetaDraws.length;
        double postExpInfo = 0;

        for (int s = 0; s < sampleLength; s++) {
            postExpInfo += calcInfo(thetaDraws[s], aDraws[s], bDraws[s], cDraws[s], D);
        }
        postExpInfo /= sampleLength;
        return postExpInfo;
    }

    /**
     * This function calculates the posterior expected Fisher information of a
     * batch of items.
     *
     * @param thetaDraws theta samples
     * @param itemParaSamples item parameter samples of a batch of items
     * @param D item parameter D
     * @return posterior expected Fisher information of the batch of items
     */
    public static double[] calPostExpInfo(double[] thetaDraws, double[][][] itemParaSamples, double[] D) {
        double[] postExpInfo = new double[itemParaSamples.length];
        for (int i = 0; i < itemParaSamples.length; i++) {
            postExpInfo[i] = calPostExpInfo(thetaDraws, itemParaSamples[i][0], itemParaSamples[i][1],
                    itemParaSamples[i][2], D[i]);
        }

        return postExpInfo;
    }

}
