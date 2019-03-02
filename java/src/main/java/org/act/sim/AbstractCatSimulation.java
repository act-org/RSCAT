package org.act.sim;

import java.io.IOException;
import java.util.List;

import org.act.cat.CatConfig;
import org.act.sol.InfeasibleTestConfigException;
import org.act.testdef.TestConfig;
import org.act.util.ProbDistribution;

/**
 * This class defines a CAT simulation.
 */
public abstract class AbstractCatSimulation {

    private String simName;
    private int examineeNum;
    private ProbDistribution thetaDistribution;
    private TestConfig testConfig;
    private CatConfig catConfig;
    private boolean isGenSimResult;

    public AbstractCatSimulation(String simName, int examineeNum, ProbDistribution thetaDistribution,
            TestConfig testConfig, CatConfig catConfig, boolean isGenSimResult) {
        this.simName = simName;
        this.examineeNum = examineeNum;
        this.thetaDistribution = thetaDistribution;
        this.testConfig = testConfig;
        this.catConfig = catConfig;
        this.isGenSimResult = isGenSimResult;
    }

    /**
     * Runs the CAT simulation, which consists of multiple CAT simulation tasks.
     * Each simulation task is for one examinee.
     *
     * @return the list of results of all examinees
     * @throws IOException if there is an IO error.
     * @throws InfeasibleTestConfigException if the test configuration is infeasible.
     * @see SimOutput
     */
    public abstract List<SimOutput> runSim() throws IOException, InfeasibleTestConfigException;

    /**
     * Generates true theta values for all examinees based on the distribution.
     *
     * @param min the minimum true theta value
     * @param max the maximum true theta value
     * @return the true theta values double array
     */
    protected double[] genTrueThetas(double min, double max) {
        return thetaDistribution.sample(examineeNum, min, max);
    }

    /**
     * Generates true theta values for all examinees based on the distribution.
     *
     * @return the true theta values double array
     */
    protected double[] genTrueThetas() {
        return thetaDistribution.sample(examineeNum);
    }

    /**
     * Returns the simulation name.
     *
     * @return the simulation name.
     */
    public String getSimName() {
        return simName;
    }

    public int getExamineeNum() {
        return examineeNum;
    }

    public ProbDistribution getThetaDistribution() {
        return thetaDistribution;
    }

    public CatConfig getCatConfig() {
        return catConfig;
    }

    public TestConfig getTestConfig() {
        return testConfig;
    }

    public boolean isGenSimResult() {
        return isGenSimResult;
    }

}
