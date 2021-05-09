package org.act.rscat.cat;

/**
 * This class defines the attributes used for item-level exposure.
 * <p>
 * Alpha is the number of passages/items administered at the theta range;
 * epsilon is the number of passages/items eligible at the theta range.
 */
public class ExposureItemUsage {
    private String itemId;
    private ThetaRange thetaRange;
    private double alpha;
    private double epsilon;

    /**
     * Constructs a new {@code ExposureItemUsage}
     *
     * @param itemId     the item identifier
     * @param thetaRange the theta range for the exposure control
     * @param alpha      the alpha value
     * @param epsilon    the epsilon value
     */
    public ExposureItemUsage(String itemId, ThetaRange thetaRange, double alpha, double epsilon) {
        this.itemId = itemId;
        this.thetaRange = thetaRange;
        this.alpha = alpha;
        this.epsilon = epsilon;
    }

    /**
     * Returns the item identifier.
     *
     * @return the item identifier
     */
    public String getItemId() {
        return itemId;
    }

    /**
     * Returns the theta range for the exposure control.
     *
     * @return the theta range instance
     */
    public ThetaRange getThetaRange() {
        return thetaRange;
    }

    /**
     * Returns the alpha value.
     *
     * @return the alpha value
     */
    public double getAlpha() {
        return alpha;
    }

    /**
     * Returns the epsilon value.
     *
     * @return the epsilon value
     */
    public double getEpsilon() {
        return epsilon;
    }

    /**
     * Increases the alpha value by 1.
     */
    public void increaseAlpha() {
        alpha += 1;
    }

    /**
     * Increases the epsilon value by 1.
     */
    public void increaseEpsilon() {
        epsilon += 1;
    }
}
