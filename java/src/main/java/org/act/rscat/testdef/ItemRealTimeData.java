package org.act.rscat.testdef;

/**
 * Defines the real-time data in the CAT process for an item.
 * <p>
 * Public fields are required to transfer data between Java and Mosel.
 */
@SuppressWarnings("java:S1104")
public class ItemRealTimeData {
    /**
     * Item id.
     */
    public String id;

    /**
     * Item row index in csv table.
     */
    public int rowIndex;

    /**
     * Item information value at current student ability.
     */
    public double info;

    /**
     * Item eligibility mark for the exposure control.
     */
    public boolean isEligible;

    /**
     * Item hard eligibility mark.
     */
    public boolean isEligibleHard;

    /**
     * Item selected mark.
     */
    public boolean isAdmined;

    /**
     * Constructs a new {@link ItemRealTimeData}.
     *
     * @param id the item identifier
     * @param rowIndex the item row index
     * @param info the item information value
     * @param isEligible the item eligibility mark
     * @param isEligibleHard the item hard eligibility mark
     * @param isAdmined the item administration mark
     */
    public ItemRealTimeData(String id, int rowIndex, double info, boolean isEligible, boolean isEligibleHard,
            boolean isAdmined) {
        this.id = id;
        this.rowIndex = rowIndex;
        this.info = info;
        this.isEligible = isEligible;
        this.isEligibleHard = isEligibleHard;
        this.isAdmined = isAdmined;
    }
}
