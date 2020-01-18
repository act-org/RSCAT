package org.act.sim;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.act.cat.ItemSelectionMethod.SUPPORTED_METHODS.MAX_FISHER_INFO;
import java.io.IOException;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.act.cat.CatConfig;
import org.act.cat.CatConfigStandard;
import org.act.cat.ExposureControlConfig;
import org.act.cat.ExposureControlType;
import org.act.cat.AbstractScoringMethodConfig;
import org.act.cat.ScoringMethodConfigEap;
import org.act.cat.ThetaRange;
import org.act.mip.SolverConfig;
import org.act.sol.InfeasibleTestConfigException;
import org.act.testdef.Item;
import org.act.testdef.TestConfig;
import org.act.util.ContentTable;
import org.act.util.CsvUtils;
import org.act.util.PrimitiveArrays;
import org.act.util.ProbDistribution;
import org.act.util.UniDimNormalDistribution;
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
    boolean[] itemNumericColumn720Items;
    boolean[] passageNumericColumn30Passages;
    List<String> itemIds;
    List<String> content3;
    List<String> passageIds;
    List<String> wordCount;
    List<String> passageIdsPassage;
    List<String> wordCountPassage;
    List<String> includesFiguresPassage;
    List<String> contentTypePassage;

    @Before
    public void setup() throws IOException, InfeasibleTestConfigException {
        itemPool720Items = CsvUtils
                .read(URLClassLoader.getSystemResourceAsStream("org/act/data/SampleCATPool/itemPool720Items.csv"));
        passagePool30Passages = CsvUtils
                .read(URLClassLoader.getSystemResourceAsStream("org/act/data/SampleCATPool/passagePool30Passages.csv"));
        itemNumericColumn720Items = new boolean[] {false, false, false, false, true, false, false, true, true, true,
                true, true, true, true, false, true, true, false, false, false, false, true, false, true, false, false,
                false};
        passageNumericColumn30Passages = new boolean[] {false, true, true, false, false};
        itemIds = itemPool720Items.columns().get(itemPool720Items.columnIndex("Item ID"));
        content3 = itemPool720Items.columns().get(itemPool720Items.columnIndex("Content 3"));
        passageIds = itemPool720Items.columns().get(itemPool720Items.columnIndex("Passage ID"));
        wordCount = itemPool720Items.columns().get(itemPool720Items.columnIndex("Word Count"));
        passageIdsPassage = passagePool30Passages.columns().get(passagePool30Passages.columnIndex("Passage ID"));
        wordCountPassage = passagePool30Passages.columns().get(passagePool30Passages.columnIndex("Word Count"));
        includesFiguresPassage = passagePool30Passages.columns().get(passagePool30Passages.columnIndex("Includes Figures"));
        contentTypePassage = passagePool30Passages.columns().get(passagePool30Passages.columnIndex("Content Type"));
    }

    /**
     * Runs simulation test with 3 examinees without exposure control.
     *
     * @throws IOException if there is an IO error.
     * @throws InfeasibleTestConfigException if the test configuration is infeasible.
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
                int[] shadowTestItemIndices = PrimitiveArrays.select(itemIds.toArray(new String[0]), shadowTest);
            	// Check test length
                assertEquals(testLength, shadowTest.length);
            }
        }
    }
}
