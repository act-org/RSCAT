package org.act.rscat.mip;

/**
 * Defines configuration parameters of the MIP solver.
 */
public class SolverConfig {
    /**
     * Used bit array length.
     */
    public static final int USED_BIT_LENGTH = 31;

    /**
     * Absolute gap target to terminate the MIP solving.
     */
    private double absGap;

    /**
     * Relative gap target to terminate the MIP solving.
     */
    private double relGap;

    /**
     * Integer tolerance for the MIP solving.
     */
    private double intTol;

    /**
     * Switch to save MIP input in a separate file.
     */
    private boolean saveInput;

    /**
     * Constructs a new {@link SolverParam}.
     *
     * @param absGap the absolute gap
     * @param relGap the relative gap
     * @param intTol the integer tolerance
     * @param saveInput the save input after model run switch
     */
    public SolverConfig(double absGap, double relGap, double intTol, boolean saveInput) {
        this.absGap = absGap;
        this.relGap = relGap;
        this.intTol = intTol;
        this.saveInput = saveInput;
    }

    /**
     * Returns the solver absolute gap.
     *
     * @return the solver absolute gap
     */
    public double getAbsGap() {
        return absGap;
    }

    /**
     * Sets solver absolute gap.
     *
     * @param absGap the solver absolute gap
     */
    public void setAbsGap(double absGap) {
        this.absGap = absGap;
    }

    /**
     * Returns the solver relative gap.
     *
     * @return the solver relative gap.
     */
    public double getRelGap() {
        return relGap;
    }

    /**
     * Sets solver relative gap.
     *
     * @param relGap the solver relative gap.
     */
    public void setRelGap(double relGap) {
        this.relGap = relGap;
    }

    /**
     * Returns the solver integer tolerance.
     *
     * @return the solver integer tolerance.
     */
    public double getIntTol() {
        return intTol;
    }

    /**
     * Sets the solver integer tolerance.
     *
     * @param intTol the solver integer tolerance
     */
    public void setIntTol(double intTol) {
        this.intTol = intTol;
    }

    /**
     * Gets isSaveInput status.
     *
     * @return isSaveInput boolean status
     */
    public boolean isSaveInput() {
        return saveInput;
    }

}
