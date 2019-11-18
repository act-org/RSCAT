package org.act.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.act.cat.AbstractScoringMethodConfig;
import org.act.cat.CatConfig;
import org.act.cat.CatConfigStandard;
import org.act.cat.ExposureControlConfig;
import org.act.cat.ExposureControlFunctions;
import org.act.cat.ExposureControlType;
import org.act.cat.ThetaRange;
import org.act.cat.ItemSelectionMethod;
import org.act.mip.SolverConfig;
import org.act.sim.AbstractCatSimulation;
import org.act.sim.CatSimulationStandard;
import org.act.sim.SimOutput;
import org.act.sol.InfeasibleTestConfigException;
import org.act.testdef.TestConfig;

/**
 * Defines helper methods to run simulation from R.
 */
public class RHelper {

    private RHelper() {
    }

    /**
     * Runs a CAT simulation from R through rJava.
     *
     * @param scoringMethodConfig  the scoring method configuration
     * @param initTheta            the initial theta value of examinees
     * @param scalingConstant      a scaling constant, either 1.0 or 1.7
     * @param exposureControlType  a String that specifies the exposure control type
     * @param rMax                 the exposure control goal rate
     * @param lValue               an integer that specifies to randomize the
     *                             administration of first L items
     * @param absGap               the absolute gap parameter for the MIP solver
     * @param relGap               the relative gap parameter for the MIP solver
     * @param intTol               the integer tolerance parameter for the MIP
     *                             solver
     * @param saveInput            a boolean indicator to specify if input data to
     *                             MIP solver will be saved locally in a file
     * @param testConfigID         a string as the test configuration identifier
     * @param testLength           an integer value that specifies the test length
     * @param itempoolPath         a string that specifies the path of the item pool
     *                             file
     * @param passagepoolPath      a string that specifies the path of the passage
     *                             pool file
     * @param constraintPath       a string that specifies the path of the
     *                             constraint file
     * @param itemNumericColumn    a boolean array that specifies which columns in
     *                             the item pool are numeric
     * @param passageNumericColumn a boolean array that specifies which columns in
     *                             the passage pool are numeric
     * @param enableEnemyItem      a boolean indicator that specifies if enables
     *                             defining enemy item constraints through the
     *                             preclude column
     * @param numPassageLB         the lower bound of number of passages in the test
     * @param numPassageUB         the upper bound of number of passages in the test
     * @param numItemPerPassageLB  the lower bound of number of items per passages
     *                             in the test
     * @param numItemPerPassageUB  the upper bound of number of items per passages
     *                             in the test
     * @param simID                a string that specifies the simulation identifier
     * @param numExaminees         the number of simulated examinees
     * @param trueThetaDistType    a string that specifies the type of distribution
     *                             of true ability theta
     * @param trueThetaDistParams  an array that specifies the parameters of the
     *                             true theta distribution
     * @return the list of {@link SimOutput} as the simulation result
     * @throws IOException                   if there is an IO error
     * @throws InfeasibleTestConfigException if the test configuration is infeasible
     */
    public static List<SimOutput> runSim(
            AbstractScoringMethodConfig scoringMethodConfig, double initTheta, double scalingConstant,
            String exposureControlType, double rMax, int lValue, double absGap, double relGap, double intTol,
            boolean saveInput, String testConfigID, int testLength, String itempoolPath, String passagepoolPath,
            String constraintPath, boolean[] itemNumericColumn, boolean[] passageNumericColumn, boolean enableEnemyItem,
            int numPassageLB, int numPassageUB, int numItemPerPassageLB, int numItemPerPassageUB, String simID,
            int numExaminees, String trueThetaDistType, double[] trueThetaDistParams)
            throws IOException, InfeasibleTestConfigException {
        SolverConfig solverConfig = new SolverConfig(absGap, relGap, intTol, saveInput);

        // Initialize CAT configuration
        ExposureControlType ecType;
        if (exposureControlType.equals("Item")) {
            ecType = ExposureControlType.ITEM;
        } else if (exposureControlType.equals("None")) {
            ecType = ExposureControlType.NONE;
        } else {
            throw new IllegalArgumentException("Invalid exposure control type!");
        }
        List<ThetaRange> thetaRanges = Arrays
                .asList(new ThetaRange(ExposureControlFunctions.EC_THETA_MIN, ExposureControlFunctions.EC_THETA_MAX));
        ExposureControlConfig exposureConfig = new ExposureControlConfig(ecType, thetaRanges, rMax);
        
        // TODO read the item selection type from UI
        CatConfig catConfig = new CatConfigStandard(solverConfig, initTheta, scalingConstant, scoringMethodConfig,
                exposureConfig, ItemSelectionMethod.SUPPORTED_METHODS.MAX_FISHER_INFO, lValue);

        // Initialize test configuration
        ContentTable.RowOriented itemPoolTable = CsvUtils.read(new FileInputStream(new File(itempoolPath)));

        TestConfig.Builder testConfigBuilder = new TestConfig.Builder(testLength, itemNumericColumn, itemPoolTable);
        testConfigBuilder.testConfigId(testConfigID).enableEnemyItemConstraint(enableEnemyItem);

        // Add constraint table if provided
        if (constraintPath.length() > 0) {
            ContentTable.RowOriented constraintTable = CsvUtils.read(new FileInputStream(new File(constraintPath)));
            testConfigBuilder.constraintTable(constraintTable);
        }

        // Add passage table if provided
        if (passagepoolPath.length() > 0) {
            ContentTable.RowOriented passagepoolTable = CsvUtils.read(new FileInputStream(new File(passagepoolPath)));
            testConfigBuilder.passageTable(passagepoolTable).passageNumericColumn(passageNumericColumn)
                    .numPassageLB(numPassageLB).numPassageUB(numPassageUB).numItemPerPassageLB(numItemPerPassageLB)
                    .numItemPerPassageUB(numItemPerPassageUB);
        }

        TestConfig testConfig = testConfigBuilder.build();

        AbstractCatSimulation catSim = new CatSimulationStandard(simID, numExaminees,
                ProbDistributionFactory.getProbDistribution(trueThetaDistType, trueThetaDistParams), testConfig,
                catConfig, true);
        List<SimOutput> simOutputs = catSim.runSim();
        return simOutputs;
    }
}
