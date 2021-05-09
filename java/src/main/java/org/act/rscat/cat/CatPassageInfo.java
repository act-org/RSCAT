package org.act.rscat.cat;

/**
 * This class defines the passage information values used in CAT.
 */
public class CatPassageInfo {
    private String id;
    private double value;
    private boolean isPassage;

    /**
     * Constructs a new {@link CatPassageInfo}.
     *
     * @param id the passage identifier
     * @param value the average passage information
     * @param isPassage if the id is for a real passage
     */
    public CatPassageInfo(String id, double value, boolean isPassage) {
        this.id = id;
        this.value = value;
        this.isPassage = isPassage;
    }

    /**
     * Returns the passage identifier.
     *
     * @return the passage identifier
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the average passage information.
     *
     * @return the average passage information
     */
    public double getValue() {
        return value;
    }

    /**
     * Returns the isPassage boolean indicator.
     *
     * @return the isPassage
     */
    public boolean getIsPassage() {
        return isPassage;
    }
}
