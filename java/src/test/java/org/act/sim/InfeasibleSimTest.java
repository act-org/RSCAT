package org.act.sim;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.act.cat.CatConfig;
import org.act.cat.CatConfigStandard;
import org.act.cat.ExposureControlConfig;
import org.act.cat.ExposureControlType;
import org.act.cat.ScoringMethodConfig;
import org.act.cat.ScoringMethodConfigEap;
import org.act.cat.ThetaRange;
import org.act.mip.SolverConfig;
import org.act.sol.InfeasibleTestConfigException;
import org.act.testdef.TestConfig;
import org.act.util.ContentTable;
import org.act.util.CsvUtils;
import org.act.util.ProbDistribution;
import org.act.util.UniDimNormalDistribution;
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
        ScoringMethodConfig scoringMethodConfig = new ScoringMethodConfigEap(6, -2, 2,
                new UniDimNormalDistribution(0, 1));
        ExposureControlConfig exposureControlConfig = 
        		new ExposureControlConfig(ExposureControlType.NONE, null, 0);
        int lValue = 0;
        

        // Creates an instance of CatConfig
        CatConfig catConfig = new CatConfigStandard(solverConfig, initialTheta, scalingConstant,
                scoringMethodConfig, exposureControlConfig, lValue);

        // Simulation configuration
        int numExaminees = 1;
        ProbDistribution thetaDistribution = new UniDimNormalDistribution(0, 1);
        boolean isGenSimOutput = true;

        // Test configuraiton
        TestConfig testConfig = new TestConfig.Builder(testLength, itemNumericColumn10Items, itemPool10Items)
                .testConfigId("Test1").enableEnemyItemConstraint(false).constraintTable(constraintTable).build();

        AbstractCatSimulation catSim = new CatSimulationStandard("Sim1", numExaminees, thetaDistribution, testConfig,
                catConfig, isGenSimOutput);
        List<SimOutput> simOutputs = catSim.runSim();

        // Check number of simulation outputs
        assertEquals(numExaminees, simOutputs.size());

        // Check test specifications in shadow test
        for (int i = 0; i < numExaminees; i++) {
            for (int n = 0; n < testLength; n++) {

                // Check test length
                assertEquals(testLength, simOutputs.get(i).getShadowTestList().get(n).size());

                // Check the first constraint
                assertTrue(simOutputs.get(i).getShadowTestList().get(n).contains("1007513"));

                Set<String> allOrNoneSet = new HashSet<String>();
                for (String itemId : simOutputs.get(i).getShadowTestList().get(n)) {
                    if (allList.contains(itemId)) {
                        allOrNoneSet.add(itemId);
                    }
                }

                // Check all items in {"1007513" "1011601" "1094733"} are
                // selected (the second constraint)
                assertEquals(allList.size(), allOrNoneSet.size());
            }
        }

    }

}
