package org.act.rscat.testdef;

/**
 * Defines the passage real time data.
 * <p>
 * Public fields are required to transfer data between Java and Mosel.
 */
@SuppressWarnings("java:S1104")
public class PassageRealTimeData {
    /**
     * Passage identifier.
     */
    public String id;

    /**
     * Passage row index in csv table.
     */
    public int rowIndex;

    /**
     * Passage eligibility mark for the exposure control.
     */
    public boolean isEligible;

    /**
     * Constructs a new {@link PassageRealTimeData}.
     *
     * @param id the passage identifier
     * @param rowIndex the passage row index
     * @param isEligible the passage eligibility mark for the exposure control
     */
    public PassageRealTimeData(String id, int rowIndex, boolean isEligible) {
        this.id = id;
        this.rowIndex = rowIndex;
        this.isEligible = isEligible;
    }
}
