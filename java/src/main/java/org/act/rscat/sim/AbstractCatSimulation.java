package org.act.rscat.sim;

import java.io.IOException;
import java.util.List;

import org.act.rscat.cat.CatConfig;
import org.act.rscat.sol.InfeasibleTestConfigException;
import org.act.rscat.testdef.TestConfig;
import org.act.rscat.util.ProbDistribution;

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

    /**
     * Constructs a new {@link AbstractCatSimulation}.
     *
     * @param simName the simulation name
     * @param examineeNum the number of simulated examinees
     * @param thetaDistribution the distribution of true ability of the simulated examinees
     * @param testConfig the test configuration
     * @param catConfig the CAT configuration
     * @param isGenSimResult a boolean value that specifies if simulation results are to be generated or not
     * @see ProbDistribution
     * @see TestConfig
     * @see CatConfig
     */
    protected AbstractCatSimulation(String simName, int examineeNum, ProbDistribution thetaDistribution,
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

    /**
     * Returns the number of simulated examinees.
     *
     * @return the number of simulated examinees
     */
    public int getExamineeNum() {
        return examineeNum;
    }

    /**
     * Returns the true theta distribution.
     *
     * @return the true theta distribution
     */
    public ProbDistribution getThetaDistribution() {
        return thetaDistribution;
    }

    /**
     * Returns the CAT configuration.
     *
     * @return the CAT configuration
     */
    public CatConfig getCatConfig() {
        return catConfig;
    }

    /**
     * Returns the test configuration.
     *
     * @return the test configuration
     */
    public TestConfig getTestConfig() {
        return testConfig;
    }

    /**
     * Returns the boolean indicator to generate simulation results.
     *
     * @return the boolean indicator
     */
    public boolean isGenSimResult() {
        return isGenSimResult;
    }

}
