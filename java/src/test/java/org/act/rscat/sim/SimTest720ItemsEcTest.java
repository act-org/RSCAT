package org.act.rscat.sim;

import static org.act.rscat.cat.ItemSelectionMethod.SUPPORTED_METHODS.MAX_FISHER_INFO;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import org.act.rscat.util.PrimitiveArrays;
import org.act.rscat.util.ProbDistribution;
import org.act.rscat.util.UniDimNormalDistribution;
import org.junit.Before;
import org.junit.Test;

/**
 * This class includes a unit test for CAT simulation, with item exposure control.
 * <p>
 * The CAT configuration is based on an item pool of 720 items, 30 passages, 11 constraints.
 * The test verifies if all test specifications are satisfied during the simulation.
 */
public class SimTest720ItemsEcTest {

    private ContentTable.RowOriented itemPool720Items;
    private ContentTable.RowOriented passagePool30Passages;
    private ContentTable.RowOriented constraintTable;
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

    /**
     * Loads csv files for testing.
     * 
     * @throws IOException if there are IO errors
     */
    @Before
    public void setup() throws IOException {
        try (InputStream itemPoolInput = URLClassLoader
                .getSystemResourceAsStream("org/act/rscat/data/SampleCATPool/itemPool720Items.csv");
             InputStream passagePoolInput = URLClassLoader
                 .getSystemResourceAsStream("org/act/rscat/data/SampleCATPool/passagePool30Passages.csv");
             InputStream constraintInput = URLClassLoader
                 .getSystemResourceAsStream("org/act/rscat/data/SampleConstraint/constraintSet2EC.csv")) {
            itemPool720Items = CsvUtils.read(itemPoolInput);
            passagePool30Passages = CsvUtils.read(passagePoolInput);
            constraintTable = CsvUtils.read(constraintInput);
        }
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
     * @throws IOException if there is an IO error
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
        SolverConfig solverConfig = new SolverConfig(1E-2, 1E-2, 1E-6, false);
        double initialTheta = 0;
        double scalingConstant = 1.0;
        AbstractScoringMethodConfig scoringMethodConfig = new ScoringMethodConfigEap(6, -2, 2,
                new UniDimNormalDistribution(0, 1));
        List<ThetaRange> thetaRanges = new ArrayList<>(1);
        thetaRanges.add(new ThetaRange(-8, 8));
        ExposureControlConfig exposureControlConfig = 
        		new ExposureControlConfig(ExposureControlType.ITEM, thetaRanges, 0.5);
        int lValue = 0;

        // Creates an instance of CatConfig
        CatConfig catConfig = new CatConfigStandard(solverConfig, initialTheta, scalingConstant,
                scoringMethodConfig, exposureControlConfig, MAX_FISHER_INFO, lValue);

        // Simulation configuration
        int numExaminees = 10;
        ProbDistribution thetaDistribution = new UniDimNormalDistribution(0, 1);
        boolean isGenSimOutput = true;

        // Test configuraiton
        TestConfig testConfig = new TestConfig.Builder(testLength, itemNumericColumn720Items, itemPool720Items)
                .testConfigId("Test1").enableEnemyItemConstraint(false).constraintTable(constraintTable)
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
                
                // Check the first constraint
                int countGeometry = PrimitiveArrays.count(PrimitiveArrays.subSamples(content3, shadowTestItemIndices), "Geometry");
                if (countGeometry < 8 || countGeometry > 10) {
                	fail("The second constraint is not met!");
                }
                
                // Check the second constraint
                int countPassageId = PrimitiveArrays.count(PrimitiveArrays.subSamples(passageIds, shadowTestItemIndices), new String[] {"1", "2", "27"});
                if (countPassageId < 9 || countGeometry > 11) {
                	fail("The third constraint is not met!");
                }
                
                // Check the third constraint
                List<String> wordCountStr = PrimitiveArrays.subSamples(wordCount, shadowTestItemIndices);
                for (String str : wordCountStr) {
                	int wordCount = Integer.parseInt(str);
                	if (wordCount <= 15) {
                		fail("The fourth constraint is not met!");
                	}
                }
                
                // Check the fourth constraint
                List<String> content3List = PrimitiveArrays.subSamples(content3, shadowTestItemIndices);
                for (int j = 0; j < wordCountStr.size(); j ++) {
                	if (Integer.parseInt(wordCountStr.get(j)) >= 30 && content3List.get(j).equals("Algebra")) {
                		fail("The fifth constraint is not met!");
                	}
                }
                
                // Check the fifth constraint
                int countPassage1Item = PrimitiveArrays.count(PrimitiveArrays.subSamples(passageIds, shadowTestItemIndices), "1");
                if (countPassage1Item > 9) {
                	fail("The sixth constraint is not met!");
                }
                
                // Check the sixth constraint
                int countPassage2Item = PrimitiveArrays.count(PrimitiveArrays.subSamples(passageIds, shadowTestItemIndices), "2");
                if (countPassage2Item > 9) {
                	fail("The seventh constraint is not met!");
                }
                
                // Check the seventh constraint
                int countPassage3Item = PrimitiveArrays.count(PrimitiveArrays.subSamples(passageIds, shadowTestItemIndices), "3");
                if (countPassage3Item < 7 || countPassage3Item > 9) {
                	fail("The eighth constraint is not met!");
                }
                
                // Check the eighth constraint
                List<String> selPassageIdList = PrimitiveArrays.subSamples(passageIds, shadowTestItemIndices);
                if (selPassageIdList.containsAll(Arrays.asList("1", "5", "10", "20", "25"))) {
                	fail("The ninth constraint is not met!");
                }
                
                // Check the ninth constraint
                Set<String> selPassageIdSet = new HashSet<>(selPassageIdList);
                int[] selPassageIndices = new int[selPassageIdSet.size()];
                double avgPassageWordCount = 0;
                count = 0;
                for (String passageId : selPassageIdSet) {
                	int passageIndex = passageIdsPassage.indexOf(passageId);
                	selPassageIndices[count] = passageIndex;
                	avgPassageWordCount += Integer.parseInt(wordCountPassage.get(passageIndex));
                	count ++;
                }
                avgPassageWordCount /= selPassageIdSet.size();
                if (avgPassageWordCount < 200 || avgPassageWordCount > 350) {
                	fail("The tenth constraint is not met!");
                }
                
                // Check the tenth constraint
                int countPassageWithFigs = PrimitiveArrays.count(PrimitiveArrays.subSamples(includesFiguresPassage, selPassageIndices), "Y");
                if (countPassageWithFigs < 3 || countPassageWithFigs > 4) {
                	fail("The eleventh constraint is not met!");
                }
                
                // Check the eleventh constraint
                int countPassageWithFic = PrimitiveArrays.count(PrimitiveArrays.subSamples(contentTypePassage, selPassageIndices), "FIC");
                if (countPassageWithFic < 2 || countPassageWithFigs > 5) {
                	fail("The twelfth constraint is not met!");
                }
            }
        }
    }
}
