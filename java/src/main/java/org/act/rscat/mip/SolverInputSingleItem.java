package org.act.rscat.mip;

/**
 * An immutable encapsulation of data required by solver.
 */
public final class SolverInputSingleItem {

    /**
     * Item identifier.
     */
    private final String itemIdentifier;

    /**
     * Item information value.
     */
    private final double information;

    /**
     * Item administration mark, true if an item is administered in previous
     * stages, otherwise false.
     */
    private final boolean administered;
    /**
     * Item eligibility mark, true if an item is eligible, otherwise false.
     */
    private final boolean eligible;

    /**
     * Item hard eligibility mark (cannot be relaxed for feasible solutions),
     * true if an item is eligible, otherwise false.
     */
    private final boolean eligibleHard;

    /**
     * Item selection mark, true if item was selected in previous shadow test.
     */
    private final boolean selected;

    /**
     * Constructs a new {@link SolverInputSingleItem}.
     *
     * @param itemIdentifier item identifier
     * @param information item information value
     * @param administered item administration mark, true if an item is
     *            administered in previous stages, otherwise false
     * @param eligible item eligibility mark, true if an item is eligible,
     *            otherwise false
     * @param eligibleHard item hard eligibility mark (cannot be relaxed for
     *            feasible solutions), true if an item is eligible, otherwise
     *            false.
     * @param selected boolean flag indicating whether the item is selected or
     *            not
     */
    public SolverInputSingleItem(String itemIdentifier, double information, boolean administered, boolean eligible,
            boolean eligibleHard, boolean selected) {
        this.itemIdentifier = itemIdentifier;
        this.information = information;
        this.administered = administered;
        this.eligible = eligible;
        this.eligibleHard = eligibleHard;
        this.selected = selected;
    }

    /**
     * Returns item identifier.
     *
     * @return item identifier
     */
    public String getItemIdentifier() {
        return itemIdentifier;
    }

    /**
     * Returns item information.
     *
     * @return item information
     */
    public double getInformation() {
        return information;
    }

    /**
     * Returns item administration mark.
     *
     * @return item administration mark
     */
    public boolean isAdministered() {
        return administered;
    }

    /**
     * Returns item eligibility mark.
     *
     * @return item eligibility mark
     */
    public boolean isEligible() {
        return eligible;
    }

    /**
     * Returns item hard eligibility mark.
     *
     * @return item hard eligibility mark
     */
    public boolean isEligibleHard() {
        return eligibleHard;
    }

    /**
     * Returns item selection mark.
     *
     * @return item selection mark
     */
    public boolean isSelected() {
        return selected;
    }
}
