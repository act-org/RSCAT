package org.act.sim;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.act.cat.CatConfig;
import org.act.cat.CatEngine;
import org.act.cat.CatEngineStandard;
import org.act.cat.CatInput;
import org.act.cat.CatInputStandard;
import org.act.cat.ExposureControlData;
import org.act.cat.ExposureControlFunctions;
import org.act.cat.ExposureControlType;
import org.act.cat.ExposureItemUsage;
import org.act.cat.ItemScores;
import org.act.cat.PassageOrItemEligibilityOverall;
import org.act.cat.ThetaRange;
import org.act.sol.InfeasibleTestConfigException;
import org.act.testdef.Item;
import org.act.testdef.TestConfig;
import org.act.util.ProbDistribution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CatSimulationStandard extends AbstractCatSimulation {
	
    /**
     * A Logger instance for the CatSimulationStandard.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(CatSimulationStandard.class);
	public CatSimulationStandard(String simName, int examineeNum, ProbDistribution thetaDistribution,
			TestConfig testConfig, CatConfig catConfig, Boolean isGenSimResult) {
		super(simName, examineeNum, thetaDistribution, testConfig, catConfig, isGenSimResult);
	}

	@Override
	public List<SimOutput> runSim() throws IOException, InfeasibleTestConfigException {

		// Generate examinees' true theta values
		double[] trueThetas = genTrueThetas();
		
		// Create CAT engine
		CatEngine catEngine = new CatEngineStandard();
		List<SimOutput> simOutputs = new ArrayList<SimOutput>();
		Map<ThetaRange, Map<String, ExposureItemUsage>> exposureItemUsageRangeMap = initializeItemUsage();

		// Run simulation for each examinee
		for (int examineeIndex = 0; examineeIndex < getExamineeNum(); examineeIndex++) {
			LOGGER.info("Simulation starts for examinee #" + (examineeIndex + 1));
			double trueTheta = trueThetas[examineeIndex];
			String studentId = examineeIndex + "";
			PassageOrItemEligibilityOverall eligibility = ExposureControlFunctions
					.buildPassageOrItemEligibilityOverall(getCatConfig(), getTestConfig(), exposureItemUsageRangeMap);

			// Create initial cat input for the examinee
			CatInput catInput = new CatInputStandard.Builder().catConfig(getCatConfig()).testConfig(getTestConfig())
					.completedCount(0).adaptiveStage(0).studentId(studentId).itemsAdmin(new ArrayList<String>())
					.itemScores(new ItemScores(new int[] {}, new double[] {}))
					.administeredPassageIndexSequence(new ArrayList<>()).exposureControlData(new ExposureControlData())
					.passageOrItemEligibilityOverall(eligibility).itemsToAdminister(new ArrayList<String>())
					.shadowTest(new ArrayList<String>()).previousTheta(0).previousThetaSe(1).build();
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
				
				if (examineeIndex == getExamineeNum() -1) {
				Map<ThetaRange, Map<String, Double>> itemExposureRates = SimulationFunctions.calItemExposureRates(
						exposureItemUsageRangeMap, getExamineeNum());
				simOutput.setItemExposureRates(itemExposureRates);
				}
			}
			if (isGenSimResult()) {
				simOutputs.add(simOutput);
			}
			LOGGER.info("Simulation ends for examinee #" + (examineeIndex + 1));

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
		Map<String, ExposureItemUsage> exposureItemUsageMap = new HashMap<String, ExposureItemUsage>();
		for (String itemId : itemIds) {
			exposureItemUsageMap.put(itemId, new ExposureItemUsage(itemId, thetaRange, 0, 0));
		}
		exposureItemUsageRangeMap.put(thetaRange, exposureItemUsageMap);
		return exposureItemUsageRangeMap;
	}
}
