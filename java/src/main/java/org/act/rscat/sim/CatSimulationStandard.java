package org.act.rscat.sim;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.act.rscat.cat.CatConfig;
import org.act.rscat.cat.CatEngine;
import org.act.rscat.cat.CatEngineStandard;
import org.act.rscat.cat.CatInput;
import org.act.rscat.cat.CatInputStandard;
import org.act.rscat.cat.ExposureControlData;
import org.act.rscat.cat.ExposureControlFunctions;
import org.act.rscat.cat.ExposureControlType;
import org.act.rscat.cat.ExposureItemUsage;
import org.act.rscat.cat.ItemScores;
import org.act.rscat.cat.PassageOrItemEligibilityOverall;
import org.act.rscat.cat.ThetaRange;
import org.act.rscat.sol.InfeasibleTestConfigException;
import org.act.rscat.testdef.Item;
import org.act.rscat.testdef.TestConfig;
import org.act.rscat.util.ProbDistribution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class defines the standard CAT simulation for an individual examinee.
 * @see AbstractCatSimulation
 */
public class CatSimulationStandard extends AbstractCatSimulation {

    /**
     * A Logger instance for the CatSimulationStandard.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(CatSimulationStandard.class);

    /**
     * Constructs a new {@link CatSimulationStandard}
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
    public CatSimulationStandard(String simName, int examineeNum, ProbDistribution thetaDistribution,
            TestConfig testConfig, CatConfig catConfig, Boolean isGenSimResult) {
        super(simName, examineeNum, thetaDistribution, testConfig, catConfig, isGenSimResult);
    }

    @Override
    public List<SimOutput> runSim() throws IOException, InfeasibleTestConfigException {

        // Generate examinees' true theta values
        double[] trueThetas = genTrueThetas();

        // Create CAT engine
        CatEngine catEngine = CatEngineStandard.INSTANCE;
        List<SimOutput> simOutputs = new ArrayList<>();
        Map<ThetaRange, Map<String, ExposureItemUsage>> exposureItemUsageRangeMap = initializeItemUsage();

        // Run simulation for each examinee
        for (int examineeIndex = 0; examineeIndex < getExamineeNum(); examineeIndex++) {
            LOGGER.info("Simulation starts for examinee #{}", examineeIndex + 1);
            double trueTheta = trueThetas[examineeIndex];
            String studentId = examineeIndex + "";
            PassageOrItemEligibilityOverall eligibility = ExposureControlFunctions
                    .buildPassageOrItemEligibilityOverall(getCatConfig(), getTestConfig(), exposureItemUsageRangeMap);

            // Create initial cat input for the examinee
            CatInput catInput = new CatInputStandard.Builder().catConfig(getCatConfig()).testConfig(getTestConfig())
                    .completedCount(0).adaptiveStage(0).studentId(studentId).itemsAdmin(new ArrayList<>())
                    .itemScores(new ItemScores(new int[] {}, new double[] {}))
                    .administeredPassageIndexSequence(new ArrayList<>()).exposureControlData(new ExposureControlData())
                    .passageOrItemEligibilityOverall(eligibility).itemsToAdminister(new ArrayList<>())
                    .shadowTest(new ArrayList<>()).previousTheta(0).previousThetaSe(1).build();
            AbstractCatSimulationTask studentSimTask = new CatSimulationTaskStandard(studentId, trueTheta, catEngine,
                    catInput, eligibility);
            SimOutput simOutput = studentSimTask.runSimTask(isGenSimResult());
            int itemIdColIndex = getTestConfig().getItemPoolTable().columnIndex(Item.ColumnName.ITEM_ID.getColName());
            List<String> itemIds = getTestConfig().getItemPoolTable().columns().get(itemIdColIndex);
            if (getCatConfig().exposureControlConfig().getType().equals(ExposureControlType.ITEM)) {
                // Update item usage after each simulated examinee
                ExposureControlFunctions.updateItemUsage(exposureItemUsageRangeMap,
                        simOutput.getPassageOrItemEligibilityAtThetaRangeList()
                                .get(simOutput.getPassageOrItemEligibilityAtThetaRangeList().size() - 2),
                        simOutput.getItemsAdministered(), itemIds);

                if (examineeIndex == getExamineeNum() - 1) {
                    Map<ThetaRange, Map<String, Double>> itemExposureRates = SimulationFunctions
                            .calItemExposureRates(exposureItemUsageRangeMap, getExamineeNum());
                    simOutput.setItemExposureRates(itemExposureRates);
                }
            }
            if (isGenSimResult()) {
                simOutputs.add(simOutput);
            }
            LOGGER.info("Simulation ends for examinee #{}", examineeIndex + 1);

        }
        return simOutputs;
    }

    private Map<ThetaRange, Map<String, ExposureItemUsage>> initializeItemUsage() {

        // Initialize exposureItemUsageMap
        Map<ThetaRange, Map<String, ExposureItemUsage>> exposureItemUsageRangeMap = new HashMap<>();
        ThetaRange thetaRange = new ThetaRange(ExposureControlFunctions.EC_THETA_MIN,
                ExposureControlFunctions.EC_THETA_MAX);
        int itemIdColIndex = getTestConfig().getItemPoolTable().columnIndex(Item.ColumnName.ITEM_ID.getColName());
        List<String> itemIds = getTestConfig().getItemPoolTable().columns().get(itemIdColIndex);
        Map<String, ExposureItemUsage> exposureItemUsageMap = new HashMap<>();
        for (String itemId : itemIds) {
            exposureItemUsageMap.put(itemId, new ExposureItemUsage(itemId, thetaRange, 0, 0));
        }
        exposureItemUsageRangeMap.put(thetaRange, exposureItemUsageMap);
        return exposureItemUsageRangeMap;
    }
}
