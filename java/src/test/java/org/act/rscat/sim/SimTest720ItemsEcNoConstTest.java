package org.act.rscat.sim;

import static org.act.rscat.cat.ItemSelectionMethod.SUPPORTED_METHODS.MAX_FISHER_INFO;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.act.rscat.cat.AbstractScoringMethodConfig;
import org.act.rscat.cat.CatConfig;
import org.act.rscat.cat.CatConfigStandard;
import org.act.rscat.cat.ExposureControlConfig;
import org.act.rscat.cat.ExposureControlType;
import org.act.rscat.cat.ScoringMethodConfigEap;
import org.act.rscat.cat.ThetaRange;
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
 * This class includes a unit test for CAT simulation, with item exposure control.
 * <p>
 * The CAT configuration is based on an item pool of 720 items, 30 passages, no constraints.
 * The test verifies if all test specifications are satisfied during the simulation.
 */
public class SimTest720ItemsEcNoConstTest {
    private ContentTable.RowOriented itemPool720Items;
    private ContentTable.RowOriented passagePool30Passages;
    private boolean[] itemNumericColumn720Items;
    private boolean[] passageNumericColumn30Passages;

    /**
     * Loads csv files for testing.
     * 
     * @throws IOException if there is an IO failure
     */
    @Before
    public void setup() throws IOException {
        try (InputStream itemPoolInput = URLClassLoader
                .getSystemResourceAsStream("org/act/rscat/data/SampleCATPool/itemPool720Items.csv");
             InputStream passagePoolInput = URLClassLoader
                 .getSystemResourceAsStream("org/act/rscat/data/SampleCATPool/passagePool30Passages.csv")) {
            itemPool720Items = CsvUtils.read(itemPoolInput);
            passagePool30Passages = CsvUtils.read(passagePoolInput);
        }
        itemNumericColumn720Items = new boolean[] {false, false, false, false, true, false, false, true, true, true,
                true, true, true, true, false, true, true, false, false, false, false, true, false, true, false, false,
                false};
        passageNumericColumn30Passages = new boolean[] {false, true, true, false, false};
    }

    /**
     * Runs simulation test with 3 examinees without exposure control.
     *
     * @throws IOException if there is an IO failure
     * @throws InfeasibleTestConfigException if the test configuration is infeasible
     */
    @Test
    public void simTest720Items() throws IOException, InfeasibleTestConfigException {

        // Cat configuration parameters
        int testLength = 20;
        int numPassageLB = 3;
        int numPassageUB = 5;
        int numItemPerPassageLB = 1; 
        int numItemPerPassageUB = 10;
        SolverConfig solverConfig = new SolverConfig(1E-3, 1E-3, 1E-3, false);
        double initialTheta = 0;
        double scalingConstant = 1.0;
        AbstractScoringMethodConfig scoringMethodConfig = new ScoringMethodConfigEap(6, -2, 2,
                new UniDimNormalDistribution(0, 1));
        List<ThetaRange> thetaRanges = new ArrayList<>(1);
        thetaRanges.add(new ThetaRange(-8, 8));
        ExposureControlConfig exposureControlConfig = 
        		new ExposureControlConfig(ExposureControlType.ITEM, thetaRanges, 0.3);
        int lValue = 0;

        // Creates an instance of CatConfig
        CatConfig catConfig = new CatConfigStandard(solverConfig, initialTheta, scalingConstant,
                scoringMethodConfig, exposureControlConfig, MAX_FISHER_INFO, lValue);

        // Simulation configuration
        int numExaminees = 100;
        ProbDistribution thetaDistribution = new UniDimNormalDistribution(0, 1);
        boolean isGenSimOutput = true;

        // Test configuraiton
        TestConfig testConfig = new TestConfig.Builder(testLength, itemNumericColumn720Items, itemPool720Items)
                .testConfigId("Test1").enableEnemyItemConstraint(false)
                .passageTable(passagePool30Passages).passageNumericColumn(passageNumericColumn30Passages)
                .numPassageLB(numPassageLB).numPassageUB(numPassageUB).numItemPerPassageLB(numItemPerPassageLB)
                .numItemPerPassageUB(numItemPerPassageUB).build();

        AbstractCatSimulation catSim = new CatSimulationStandard("Sim1", numExaminees, thetaDistribution, testConfig,
                catConfig, isGenSimOutput);
        List<SimOutput> simOutputs = catSim.runSim();
        
        Map<ThetaRange, Map<String, Double>> itemExposureThetaRates = simOutputs.get(numExaminees-1).getItemExposureRates();
        Map<String, Double> itemExposureRates = itemExposureThetaRates.get(new ThetaRange(-8, 8));
        double maxRate = Collections.max(itemExposureRates.values());
        double meanRate = 0.0;
        int count = 0;
        for (double value : itemExposureRates.values()) {
        	if (value > 0) {
        		meanRate += value;
        		count ++;
        	}
        }
        meanRate /= count;
        
        System.out.println("Max exposure rate: " + maxRate);
        System.out.println("Mean exposure rate: " + meanRate);
        
        // Check number of simulation outputs
        assertEquals(numExaminees, simOutputs.size());

        // Check test specifications in shadow test
        for (int i = 0; i < numExaminees; i++) {
            for (int n = 0; n < testLength; n++) {
            	String[] shadowTest = simOutputs.get(i).getShadowTestList().get(n).toArray(new String[0]);
            	
            	// Check test length
                assertEquals(testLength, shadowTest.length);
            }
        }
    }
}
