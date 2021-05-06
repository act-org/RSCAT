package org.act.rscat.sim;

import static org.act.rscat.cat.ItemSelectionMethod.SUPPORTED_METHODS.MAX_FISHER_INFO;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.act.rscat.cat.AbstractScoringMethodConfig;
import org.act.rscat.cat.CatConfig;
import org.act.rscat.cat.CatConfigStandard;
import org.act.rscat.cat.ExposureControlConfig;
import org.act.rscat.cat.ExposureControlType;
import org.act.rscat.cat.ScoringMethodConfigEap;
import org.act.rscat.mip.SolverConfig;
import org.act.rscat.sim.AbstractCatSimulation;
import org.act.rscat.sim.CatSimulationStandard;
import org.act.rscat.sim.SimOutput;
import org.act.rscat.sol.InfeasibleTestConfigException;
import org.act.rscat.testdef.TestConfig;
import org.act.rscat.util.ContentTable;
import org.act.rscat.util.CsvUtils;
import org.act.rscat.util.ProbDistribution;
import org.act.rscat.util.UniDimNormalDistribution;
import org.junit.Before;
import org.junit.Test;

/**
 * This class includes a unit test for CAT simulation with a simple configuration.
 * <p>
 * The CAT configuration is based on a discrete item pool of 10 items and 2 constraints.
 * The test length is set to 11 so that the test configuration is infeasible. The infeasibility should
 * be caught.
 */
public class InfeasibleSimTest {

    private ContentTable.RowOriented itemPool10Items;
    private ContentTable.RowOriented constraintTable;
    boolean[] itemNumericColumn10Items;
    List<String> allList;

    @Before
    public void setup() throws IOException {
        itemPool10Items = CsvUtils
                .read(URLClassLoader.getSystemResourceAsStream("org/act/data/SampleCATPool/itemPool10Items.csv"));
        constraintTable = CsvUtils
                .read(URLClassLoader.getSystemResourceAsStream("org/act/data/SampleConstraint/constraintSet1.csv"));
        itemNumericColumn10Items = new boolean[] { false, false, false, false, true, false, false, true, true, true,
                true, true, true, true, false, true, true, false, false, false, false, true, false, true, false, false,
                false };
        allList = Arrays.asList(new String[] { "1007513", "1011601", "1094733" });
    }

    /**
     * Tests an infeasible configuration in a simulation.
     * <p>
     * The test length is set to 11, which exceeds the number of items. Thus the configuration is infeasible.
     * @throws IOException if there is an IO error.
     * @throws InfeasibleTestConfigException if the CAT configuration is infeasible.
     */
    @Test(expected = InfeasibleTestConfigException.class)
    public void infeasibleTest() throws IOException, InfeasibleTestConfigException {

        // Cat configuration parameters
        int testLength = 11;
        SolverConfig solverConfig = new SolverConfig(1E-2, 1E-2, 1E-6, false);
        double initialTheta = 0;
        double scalingConstant = 1.0;
        AbstractScoringMethodConfig scoringMethodConfig = new ScoringMethodConfigEap(6, -2, 2,
                new UniDimNormalDistribution(0, 1));
        ExposureControlConfig exposureControlConfig = 
        		new ExposureControlConfig(ExposureControlType.NONE, null, 0);
        int lValue = 0;
        

        // Creates an instance of CatConfig
        CatConfig catConfig = new CatConfigStandard(solverConfig, initialTheta, scalingConstant,
                scoringMethodConfig, exposureControlConfig, MAX_FISHER_INFO, lValue);

        // Simulation configuration
        int numExaminees = 1;
        ProbDistribution thetaDistribution = new UniDimNormalDistribution(0, 1);
        boolean isGenSimOutput = true;

        // Test configuraiton
        TestConfig testConfig = new TestConfig.Builder(testLength, itemNumericColumn10Items, itemPool10Items)
                .testConfigId("Test1").enableEnemyItemConstraint(false).constraintTable(constraintTable).build();

        AbstractCatSimulation catSim = new CatSimulationStandard("Sim1", numExaminees, thetaDistribution, testConfig,
                catConfig, isGenSimOutput);
        catSim.runSim();
    }

}
