package org.act.rscat.sim;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.act.rscat.cat.CatEngine;
import org.act.rscat.cat.CatHelper;
import org.act.rscat.cat.CatInput;
import org.act.rscat.cat.CatOutput;
import org.act.rscat.cat.ItemScores;
import org.act.rscat.cat.PassageOrItemEligibilityAtThetaRange;
import org.act.rscat.cat.PassageOrItemEligibilityOverall;
import org.act.rscat.cat.ThetaEst;
import org.act.rscat.sol.InfeasibleTestConfigException;
import org.apache.commons.math3.linear.RealMatrix;

/**
 * This class defines the standard CAT simulation for an individual examinee.
 */
public class CatSimulationTaskStandard extends AbstractCatSimulationTask {
    private PassageOrItemEligibilityOverall eligibility;

    /**
     * Constructs a new {@link CatSimulationTaskStandard}.
     *
     * @param studentId the identifier of the simulated examinee
     * @param trueTheta the true ability value of the simulated examinee
     * @param engine the CAT engine used for the CAT simulation
     * @param catInput the initial CAT input
     * @param eligibility the eligibility of items or passages
     */
    public CatSimulationTaskStandard(String studentId, double trueTheta, CatEngine engine, CatInput catInput,
            PassageOrItemEligibilityOverall eligibility) {
        super(studentId, trueTheta, engine, catInput);
        this.eligibility = eligibility;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SimOutput runSimTask(boolean generateOutput) throws IOException, InfeasibleTestConfigException {
        List<String> itemIds = getCatInput().getItemIds();
        RealMatrix itemPoolParams = CatHelper.getItemParams(getCatInput().getItemPoolDataSet());
        Map<String, Integer> itemToPassageIndexMap = SimulationFunctions.getItemIdToPassageIndexMap(
                getCatInput().getTestConfig().getItemPoolTable(), getCatInput().getTestConfig().getPassageTable());

        List<ItemScores> itemScoresList = new ArrayList<>();
        List<String> itemsToAdminThisStage = null;

        int testLength = getCatInput().getTestConfig().getTestLength();
        List<PassageOrItemEligibilityAtThetaRange> passageOrItemEligibilityAtThetaRangeList = new ArrayList<>(
                getCatInput().getTestConfig().getItemPoolTable().rowCount());
        List<List<String>> shadowTestList = new ArrayList<>(testLength);
        List<ThetaEst> thetaEstList = new ArrayList<>(testLength);
        List<String> itemsAdministered;
        List<Double> catEngineTimeList = new ArrayList<>();
        List<List<Integer>> passageRowIndexSequences = new ArrayList<>();
        List<Integer> adaptiveStageList = new ArrayList<>();
        ThetaEst finalThetaEst = null;

        for (int stage = 0; stage < getCatInput().getTestConfig().getTestLength(); stage++) {
            CatOutput catOutput = getEngine().runsCatCycle(getCatInput());

            // Get items to administer
            itemsToAdminThisStage = catOutput.getItemsToAdminister().getItemsToAdmin();

            if (!itemsToAdminThisStage.isEmpty()) {
                RealMatrix itemParamsForScoring;
                // Item param subset for items to be administered.
                itemParamsForScoring = CatHelper.getItemParamsForScoring(itemIds, itemsToAdminThisStage,
                        itemPoolParams);

                // Simulate student response.
                itemScoresList.add(SimulationFunctions.simItemScores(itemParamsForScoring, getTrueTheta()));
            }

            // Record audit data.
            if (generateOutput) {
                passageOrItemEligibilityAtThetaRangeList.add(catOutput.getPassageOrItemEligibilityAtThetaRange());
                shadowTestList.add(catOutput.getShadowTest());
                thetaEstList.add(catOutput.getThetaEst());
                catEngineTimeList.add(catOutput.getCatEngineTime());
                passageRowIndexSequences.add(getCatInput().getAdministeredPassageIndexSequence());
                adaptiveStageList.add(stage);
            }

            // Update CAT input for next stage, stage index starts from 0
            int nextStage = stage + 1;
            setCatInput(CatHelper.createNextCatInput(getCatInput(), itemScoresList.get(stage), itemsToAdminThisStage,
                    nextStage, itemToPassageIndexMap, eligibility,
                    catOutput.getItemsToAdminister().getListItemsToAdminister(), catOutput.getShadowTest(),
                    catOutput.getThetaEst().getTheta(), catOutput.getThetaEst().getSe()));
        }
        CatOutput catOutput = getEngine().runsCatCycle(getCatInput());
        finalThetaEst = catOutput.getThetaEst();
        itemsAdministered = new ArrayList<>(
                catOutput.getItemsToAdminister().getListItemsAlreadyAdministered());
        // Record final audit data.
        if (generateOutput) {
            passageOrItemEligibilityAtThetaRangeList.add(catOutput.getPassageOrItemEligibilityAtThetaRange());
            shadowTestList.add(catOutput.getShadowTest());
            thetaEstList.add(catOutput.getThetaEst());
            catEngineTimeList.add(catOutput.getCatEngineTime());
            passageRowIndexSequences.add(getCatInput().getAdministeredPassageIndexSequence());
            adaptiveStageList.add(getCatInput().getTestConfig().getTestLength());
        }

        return new SimOutput.Builder(getStudentId(), getTrueTheta()).adaptiveStageList(adaptiveStageList)
                .catEngineTimeList(catEngineTimeList).finalTheta(finalThetaEst)
                .initTheta(getCatInput().getCatConfig().initTheta())
                .itemEligibilityList(passageOrItemEligibilityAtThetaRangeList).itemsAdministered(itemsAdministered)
                .itemScoresList(itemScoresList).passageRowIndexSequences(passageRowIndexSequences)
                .shadowTestList(shadowTestList).thetaEstList(thetaEstList).build();
    }

}
