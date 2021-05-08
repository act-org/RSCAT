package org.act.rscat.sol;

/**
 * This class defines the checked exception due to an infeasible test configuration.
 */
public class InfeasibleTestConfigException extends Exception {
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a {@link InfeasibleTestConfigException} without a message.
     */
    public InfeasibleTestConfigException() {}

    /**
     * Constructs a {@link InfeasibleTestConfigException} with a message.
     * @param message the error message
     */
    public InfeasibleTestConfigException(String message) {
        super(message);
    }
}
