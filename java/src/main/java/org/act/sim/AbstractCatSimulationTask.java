package org.act.sim;

import java.io.IOException;

import org.act.cat.CatEngine;
import org.act.cat.CatInput;
import org.act.sol.InfeasibleTestConfigException;

/**
 * This class defines the CAT simulation for each examinee.
 */
public abstract class AbstractCatSimulationTask {

    // Student information
    private final String studentId;
    private final double trueTheta;

    // CAT related
    private final CatEngine catEngine;
    private CatInput catInput;

    public AbstractCatSimulationTask(String studentId, double trueTheta, CatEngine engine, CatInput catInput) {
        this.studentId = studentId;
        this.trueTheta = trueTheta;
        this.catEngine = engine;
        this.catInput = catInput;
    }

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
