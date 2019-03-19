package org.act.mip;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.apache.commons.lang3.ArrayUtils;

/**
 * Defines results from the MIP solver related to shadow test MIP.
 */
public class SolverOutput {

    /**
     * Status of the MIP solver after trying to solve the shadow test MIP. The code
     * should be the same as the definition in the Mosel code.
     */
    public enum SOLVER_STATS {

        /**
         * Solution is optimal.
         */
        OPTIMAL(2),

        /**
         * The solving procedure in interrupted.
         */
        UNFINISHED(4),

        /**
         * The model is infeasible.
         */
        INFEASIBLE(6),

        /**
         * The solution is unbounded.
         */
        UNBOUNDED(8),

        /**
         * Other status.
         */
        OTHER(10);

        private static Map<Integer, SOLVER_STATS> lookup = new HashMap<>();

        static {
            for (SOLVER_STATS s : values()) {
                lookup.put(s.getCode(), s);
            }
        }

        /**
         * Code for the solver status.
         */
        private int code;

        /**
         * Constructs a {@link SOLVER_STATS}.
         *
         * @param code the integer code for a solver status
         */
        SOLVER_STATS(int code) {
            this.code = code;
        }

        /**
         * Returns the code of associated with a solver status.
         *
         * @return the code of solver status
         */
        public int getCode() {
            return code;
        }

        /**
         * Returns the solver status according to the status code
         *
         * @param code the solver status code from Mosel
         * @return the solver status in {@link SOLVER_STATS}
         * @throws NoSuchElementException if the solver status code is not defined
         */
        public static SOLVER_STATS get(int code) throws NoSuchElementException {
            if (!lookup.containsKey(code)) {
                throw new NoSuchElementException("The solver code " + code + " doesn't exist!");
            }
            return lookup.get(code);
        }

    }

    /**
     * Selected item identifiers.
     */
    private final List<String> selectedItemIdentifiers;

    /**
     * Selected item row indices.
     */
    private final List<Integer> selectedItemRowIndices;

    /**
     * Selected passage identifiers.
     */
    private final List<String> selectedPassageIdentifiers;

    /**
     * Selected passage row indices.
     */
    private final List<Integer> selectedPassageRowIndices;

    /**
     * Selected passage row indices sequcne in the test.
     */
    private final List<Integer> passageRowIndexSequence;

    /**
     * The objective function value.
     */
    private final Double objective;

    /**
     * The solver status.
     */
    private final SOLVER_STATS solverStatus;

    /**
     * Constructs a new {@link SolverOutput}.
     *
     * @param solverOutputBuilder the instance of solverOutputBuilder
     */
    private SolverOutput(SolverOutputBuilder solverOutputBuilder) {
        this.selectedItemIdentifiers = Collections.unmodifiableList(solverOutputBuilder.selectedItemIdentifiers);
        this.selectedItemRowIndices = Collections.unmodifiableList(solverOutputBuilder.selectedItemRowIndices);
        this.selectedPassageIdentifiers = Collections.unmodifiableList(solverOutputBuilder.selectedPassageIdentifiers);
        this.selectedPassageRowIndices = Collections.unmodifiableList(solverOutputBuilder.selectedPassageRowIndices);
        if (solverOutputBuilder.passageRowIndexSequence != null) {
            this.passageRowIndexSequence = Collections.unmodifiableList(solverOutputBuilder.passageRowIndexSequence);
        } else {
            this.passageRowIndexSequence = null;
        }

        this.objective = solverOutputBuilder.objective;
        this.solverStatus = solverOutputBuilder.solverStatus;
    }

    /**
     * Returns the list of selected item identifiers.
     *
     * @return the list of selected item identifiers
     */
    public List<String> getSelectedItemIdentifiers() {
        return selectedItemIdentifiers;
    }

    /**
     * Returns the list of selected item row indices.
     *
     * @return the list of selected item row indices
     */
    public List<Integer> getSelectedItemRowIndices() {
        return selectedItemRowIndices;
    }

    /**
     * Returns the list of selected passage identifiers.
     *
     * @return the list of selected passage identifiers
     */
    public List<String> getSelectedPassageIdentifiers() {
        return selectedPassageIdentifiers;
    }

    /**
     * Returns the list of selected passage row indices.
     *
     * @return the list of selected passage row indices
     */
    public List<Integer> getSelectedPassageRowIndices() {
        return selectedPassageRowIndices;
    }

    /**
     * Returns the passage sequence in shadow test.
     *
     * @return the passage sequence in shadow test
     */
    public List<Integer> getPassageRowIndexSequence() {
        return passageRowIndexSequence;
    }

    /**
     * Returns the array of the selected item identifiers.
     *
     * @return the array of selected item identifiers
     */
    public String[] getSelectedItemIdentifiersArray() {
        return selectedItemIdentifiers.toArray(new String[selectedItemIdentifiers.size()]);
    }

    /**
     * Returns the array of the selected item row indices.
     *
     * @return the array of selected item row indices
     */
    public int[] getSelectedItemRowIndicesArray() {
        return ArrayUtils.toPrimitive(selectedItemRowIndices.toArray(new Integer[selectedItemIdentifiers.size()]));
    }

    /**
     * Returns the array of selected passage identifiers.
     *
     * @return the array of selected item identifiers
     */
    public String[] getSelectedPassageIdentifiersArray() {
        return selectedPassageIdentifiers.toArray(new String[selectedPassageIdentifiers.size()]);
    }

    /**
     * Returns the array of selected passage row indices.
     *
     * @return the array of selected passage row indices
     */
    public int[] getSelectedPassageRowIndicesArray() {
        return ArrayUtils.toPrimitive(selectedPassageRowIndices.toArray(new Integer[selectedPassageRowIndices.size()]));
    }

    /**
     * Returns the shadow test optimal objective.
     *
     * @return the shadow test optimal objective
     */
    public Double getObjective() {
        return objective;
    }

    /**
     * Returns the solver status.
     *
     * @return the solver status defined in {@link SOLVER_STATS}
     */
    public SOLVER_STATS getSolverStatus() {
        return solverStatus;
    }

    /**
     * <code>SolverOutputBuilder</code> is used to build instances of
     * {@link SolverOutput}.
     *
     */
    public static class SolverOutputBuilder {
        private List<String> selectedItemIdentifiers = new ArrayList<String>();
        private List<Integer> selectedItemRowIndices = new ArrayList<Integer>();
        private List<String> selectedPassageIdentifiers = new ArrayList<String>();
        private List<Integer> selectedPassageRowIndices = new ArrayList<Integer>();
        private List<Integer> passageRowIndexSequence = new ArrayList<Integer>();
        private Double objective = new Double(0.0);
        private SOLVER_STATS solverStatus = SOLVER_STATS.OTHER;

        /**
         * Sets the selected item identifiers.
         *
         * @param newSelectedItemIdentifiers the selected item identifiers
         * @return a SolverOutputBuilder
         */
        public SolverOutputBuilder selectedItemIdentifiers(List<String> newSelectedItemIdentifiers) {
            this.selectedItemIdentifiers = newSelectedItemIdentifiers;
            return this;
        }

        /**
         * Sets the selected item row indices.
         *
         * @param newSelectedItemRowIndices the list of selected item row indices
         * @return a SolverOutputBuilder
         */
        public SolverOutputBuilder selectedItemRowIndices(List<Integer> newSelectedItemRowIndices) {
            this.selectedItemRowIndices = newSelectedItemRowIndices;
            return this;
        }

        /**
         * Sets the selected passage identifiers.
         *
         * @param newSelectedPassageIdentifiers the selected passage identifiers
         * @return a SolverOutputBuilder
         */
        public SolverOutputBuilder selectedPassageIdentifiers(List<String> newSelectedPassageIdentifiers) {
            this.selectedPassageIdentifiers = newSelectedPassageIdentifiers;
            return this;
        }

        /**
         * Sets the selected passage row indices.
         *
         * @param newSelectedPassageRowIndices the selected passage row indices
         * @return a SolverOutputBuilder
         */
        public SolverOutputBuilder selectedPassageRowIndices(List<Integer> newSelectedPassageRowIndices) {
            this.selectedPassageRowIndices = newSelectedPassageRowIndices;
            return this;
        }

        /**
         * Sets the passage row index sequence.
         *
         * @param newPassageRowIndexSequence the passage row index sequence
         * @return a SolverOutputBuilder
         */
        public SolverOutputBuilder passageRowIndexSequence(List<Integer> newPassageRowIndexSequence) {
            this.passageRowIndexSequence = newPassageRowIndexSequence;
            return this;
        }

        /**
         * Sets the optimal MIP objective.
         *
         * @param newObjective the optimal MIP objective
         * @return a SolverOutputBuilder
         */
        public SolverOutputBuilder objective(double newObjective) {
            this.objective = newObjective;
            return this;
        }

        /**
         * Sets solver status.
         *
         * @param code the solver status code
         * @return this builder
         */
        public SolverOutputBuilder solverStatus(int code) {
            this.solverStatus = SOLVER_STATS.get(code);
            return this;
        }

        /**
         * Builds an instance of {@link SolverOutput}.
         *
         * @return an instance of <code>SolverOutput</code>
         */
        public SolverOutput build() {
            return new SolverOutput(this);
        }
    }

}
