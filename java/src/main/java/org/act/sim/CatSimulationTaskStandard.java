package org.act.sim;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.act.cat.CatEngine;
import org.act.cat.CatHelper;
import org.act.cat.CatInput;
import org.act.cat.CatOutput;
import org.act.cat.ItemScores;
import org.act.cat.PassageOrItemEligibilityAtThetaRange;
import org.act.cat.PassageOrItemEligibilityOverall;
import org.act.cat.ThetaEst;
import org.act.sol.InfeasibleTestConfigException;
import org.apache.commons.math3.linear.RealMatrix;

public class CatSimulationTaskStandard extends AbstractCatSimulationTask {
    private PassageOrItemEligibilityOverall eligibility;

    public CatSimulationTaskStandard(String studentId, double trueTheta, CatEngine engine, CatInput catInput,
            PassageOrItemEligibilityOverall eligibility) {
        super(studentId, trueTheta, engine, catInput);
        this.eligibility = eligibility;
    }

    @Override
    public SimOutput runSimTask(boolean generateOutput) throws IOException, InfeasibleTestConfigException {
        List<String> itemIds = getCatInput().getItemIds();
        RealMatrix itemPoolParams = CatHelper.getItemParams(getCatInput().getItemPoolDataSet());
        Map<String, Integer> itemToPassageIndexMap = SimulationFunctions.getItemIdToPassageIndexMap(
                getCatInput().getTestConfig().getItemPoolTable(), getCatInput().getTestConfig().getPassageTable());

        List<ItemScores> itemScoresList = new ArrayList<ItemScores>();
        List<String> itemsToAdminThisStage = null;

        int testLength = getCatInput().getTestConfig().getTestLength();
        List<PassageOrItemEligibilityAtThetaRange> passageOrItemEligibilityAtThetaRangeList = new ArrayList<PassageOrItemEligibilityAtThetaRange>(
                getCatInput().getTestConfig().getItemPoolTable().rowCount());
        List<List<String>> shadowTestList = new ArrayList<List<String>>(testLength);
        List<ThetaEst> thetaEstList = new ArrayList<ThetaEst>(testLength);
        List<String> itemsAdministered = new ArrayList<String>(testLength);
        List<Double> catEngineTimeList = new ArrayList<Double>();
        List<List<Integer>> passageRowIndexSequences = new ArrayList<List<Integer>>();
        List<Integer> adaptiveStageList = new ArrayList<Integer>();
        ThetaEst finalThetaEst = null;

        for (int stage = 0; stage < getCatInput().getTestConfig().getTestLength(); stage++) {
            CatOutput catOutput = getEngine().runsCatCycle(getCatInput());

            // Get items to administer
            itemsToAdminThisStage = catOutput.getItemsToAdminister().getItemsToAdmin();

            if (itemsToAdminThisStage.size() > 0) {
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
                    catOutput.getItemsToAdminister().getListItemsAlreadyAdministered(),
                    catOutput.getItemsToAdminister().getListItemsToAdminister(), catOutput.getShadowTest(),
                    catOutput.getThetaEst().getTheta(), catOutput.getThetaEst().getSe()));
        }
        CatOutput catOutput = getEngine().runsCatCycle(getCatInput());
        finalThetaEst = catOutput.getThetaEst();
        itemsAdministered = new ArrayList<String>(
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
