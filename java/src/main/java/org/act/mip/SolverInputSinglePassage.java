package org.act.mip;

/**
 * An immutable encapsulation of data required by solver.
 */
public final class SolverInputSinglePassage {

    /**
     * Passage identifier.
     */
    private final String passageIdentifier;

    /**
     * Passage eligibility mark, true if an passage is eligible, otherwise
     * false.
     */
    private final boolean eligible;

    /**
     * Constructs a new {@link SolverInputSinglePassage}.
     *
     * @param passageIdentifier passage identifier
     *
     * @param eligible passage eligibility mark, true if a passage is eligible,
     *            otherwise false
     */
    public SolverInputSinglePassage(String passageIdentifier, boolean eligible) {
        this.passageIdentifier = passageIdentifier;
        this.eligible = eligible;
    }

    /**
     * Returns passage identifier.
     *
     * @return passage identifier
     */
    public String getPassageIdentifier() {
        return passageIdentifier;
    }

    /**
     * Returns passage eligibility mark.
     *
     * @return passage eligibility mark
     */
    public boolean isEligible() {
        return eligible;
    }

}
