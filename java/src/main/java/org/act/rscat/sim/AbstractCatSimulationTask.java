package org.act.rscat.sim;

import java.io.IOException;

import org.act.rscat.cat.CatEngine;
import org.act.rscat.cat.CatInput;
import org.act.rscat.sol.InfeasibleTestConfigException;

/**
 * This class defines the CAT simulation for an individual examinee.
 */
public abstract class AbstractCatSimulationTask {

    // Student information
    private final String studentId;
    private final double trueTheta;

    // CAT related
    private final CatEngine catEngine;
    private CatInput catInput;

    /**
     * Creates a new {@link AbstractCatSimulationTask}.
     *
     * @param studentId the identifier of the simulated examinee
     * @param trueTheta the true ability value of the simulated examinee
     * @param engine the CAT engine used for the CAT simulation
     * @param catInput the initial CAT input
     */
    public AbstractCatSimulationTask(String studentId, double trueTheta, CatEngine engine, CatInput catInput) {
        this.studentId = studentId;
        this.trueTheta = trueTheta;
        this.catEngine = engine;
        this.catInput = catInput;
    }

    /**
     * Runs the simulation task for the examinne.
     *
     * @param generateOutput a boolean indicator that specifies if the simulation results are generated or not
     * @return the simulation output
     * @throws IOException if there is an IO exception
     * @throws InfeasibleTestConfigException if the test configuraiton is infeasible
     */
    public abstract SimOutput runSimTask(boolean generateOutput) throws IOException, InfeasibleTestConfigException;

    protected String getStudentId() {
        return studentId;
    }

    protected double getTrueTheta() {
        return trueTheta;
    }

    protected CatEngine getEngine() {
        return catEngine;
    }

    protected CatInput getCatInput() {
        return catInput;
    }

    protected void setCatInput(CatInput catInput) {
        this.catInput = catInput;
    }

}
