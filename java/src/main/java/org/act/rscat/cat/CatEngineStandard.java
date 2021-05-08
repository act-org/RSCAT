package org.act.rscat.cat;

import static org.act.rscat.cat.CatFunctions.getEligiblePassageItems;
import static org.act.rscat.cat.CatFunctions.prepShadowTest;
import static org.act.rscat.cat.CatHelper.getItemParams;
import static org.act.rscat.cat.ExposureControlFunctions.applyLRandomToInfo;
import static org.act.rscat.cat.ExposureControlFunctions.findThetaInterval;
import static org.act.rscat.cat.ExposureControlFunctions.prepItemDataForSolver;
import static org.act.rscat.cat.ExposureControlFunctions.prepPassageDataForSolver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.act.rscat.mip.SolverInputSingleItem;
import org.act.rscat.mip.SolverInputSinglePassage;
import org.act.rscat.mip.SolverOutput;
import org.act.rscat.sol.InfeasibleTestConfigException;
import org.act.rscat.sol.ShadowTestRun;
import org.act.rscat.testdef.Item;
import org.act.rscat.util.ContentTable;
import org.act.rscat.util.PrimitiveArraySet;
import org.act.rscat.util.PrimitiveArrays;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.util.Precision;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is an implementation of {@link CatEngine} for a standard CAT
 * engine based on the 3-parameter item response theory (3PL IRT) model and the
 * shadow test approach.
 */
public class CatEngineStandard implements CatEngine {

    /**
     * The CAT engine singleton.
     */
    public static final CatEngine INSTANCE = new CatEngineStandard();

    /**
     * A Logger instance for the CAT engine.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(CatEngineStandard.class);

    /**
     * Header for item fisher information.
     */
    private static final String HEADER_FISHER_INFORMATION = CatEngine.MapIndiceHeader.FISHER_INFORMATION.name();

    /**
     * Header for item identifiers.
     */
    private static final String HEADER_ITEM_IDENTIFIERS = CatEngine.MapIndiceHeader.ITEM_IDENTIFIERS.name();

    /**
     * Header for item administration status.
     */
    private static final String HEADER_ITEMS_ADMINISTERED = CatEngine.MapIndiceHeader.ITEMS_ADMINISTERED.name();

    /**
     * Header for passage identifiers.
     */
    private static final String HEADER_PASSAGE_IDENTIFIERS = CatEngine.MapIndiceHeader.PASSAGE_IDENTIFIERS.name();

    /**
     * Header for item indices
     */
    private static final String HEADER_ITEM_INDICES = CatEngine.MapIndiceHeader.ITEM_INDICES.name();

    /**
     * The instance of ShadowTestRun. It assembles a shadow test based on the
     * provided input data.
     */
    private ShadowTestRun shadowTestRun;

    /**
     * Provides information about the remaining items to be administered.
     */
    private CatItemsToAdminister itemsToAdminister;

    /**
     * The theta estimate for an examinee at the current stage.
     */
    private ThetaEst thetaEst;

    /**
     * The flag indicating if the current stage is the final stage in the test.
     */
    private boolean testComplete = false;

    /**
     * The time used by the MIP solver for the shadow test assembly, with the unit
     * second.
     */
    private double solverTimeSecs = 0;

    /**
     * The passage or item eligibility data (at theta ranges)
     */
    private PassageOrItemEligibilityAtThetaRange passageOrItemEligibilityAtThetaRange;

    /**
     * The list of item IDs in the shadow test associated with the CAT. The list
     * will be updated every time when runsCatCycle is executed.
     */
    private List<String> shadowTest;

    /**
     * The scoring method configured for the CAT.
     */
    private ScoringMethod scoringMethod;

    /**
     * The test length (number of items) for the CAT.
     */
    private int testLength;

    /**
     * The number of completed stages (number of administered items).
     */
    private int completedCount;

    /**
     * The passage pool table for the CAT.
     */
    private ContentTable passagePoolTable;

    /**
     * Parameters of items as an instance of RealMatrix.
     */
    private RealMatrix itemPar;

    /**
     * Item identifiers extracted from the item pool.
     */
    private String[] itemIds;

    /**
     * Passage identifiers extracted from the item pool using the passage id column.
     */
    private String[] passageIdsFromItemTable;

    /**
     * Item indices from 0 to the number of items -1.
     */
    private int[] itemIndices;

    /**
     * Passage identifiers extracted from the passage pool.
     */
    private String[] passageIdsFromPassageTable;

    /**
     * The boolean array indicating whether items in the item pool have been
     * administered.
     */
    private boolean[] itemsAdministeredBoolean;

    /**
     * The string array indicating the identifiers of items that have already been
     * administered.
     */
    private String[] itemsAdministeredString;

    /**
     * The row indices (in the item pool) of items that have already been
     * administered.
     */
    private int[] rowIndicesItemsAdmin;

    /**
     * The exposure control type applied to the CAT engine.
     */
    private ExposureControlType exposureControlType;

    /**
     * Constructs a new {@link CatEngineStandard}.
     */
    private CatEngineStandard() {
    }

    /**
     * {@inheritDoc}
     *
     * @throws InfeasibleTestConfigException
     */
    @Override
    public CatOutput runsCatCycle(CatInput catInput) throws IOException, InfeasibleTestConfigException {
        completedCount = catInput.getCompletedCount();
        LOGGER.debug("runsCatCycle starts for stage {}", completedCount);
        long startTime = System.currentTimeMillis();
        initialize(catInput);
        if (completedCount > testLength) {
            throw new IllegalArgumentException("Number of items completed cannot exceed total items");
        }

        // If the test is complete, just estimate theta and change test status.
        // Also process the exposure information
        // (won't call solver)
        if (testIsComplete()) {
            setupShadowTestRun(catInput);
            processCompleteTest(catInput);
        } else {

            // if the test is not complete, call solver
            if (completedCount == 0) {
                initializeShadowTestRun(catInput);
            }
            startTime = System.currentTimeMillis();
            setupShadowTestRun(catInput);
            refreshShadowTest(catInput);
        }

        // Save shadow test and cat engine time
        double catEngineTime = (System.currentTimeMillis() - startTime) / 1000.00d;

        // Returns an instance of CatOutput.

        CatOutput catOutput = new CatOutputStandard(itemsToAdminister, thetaEst, testComplete,
                passageOrItemEligibilityAtThetaRange, shadowTest, catEngineTime, Precision.round(solverTimeSecs, 3));

        LOGGER.debug("runsCatCycle ends for stage {} with CAT engine time {}second", completedCount, catEngineTime);
        return catOutput;
    }

    /**
     * Refreshes the shadow test for the current cat cycle based on the updated cat
     * input.
     *
     * @param catInput the instance of {@link CatInput}
     * @throws IOException                   if there is an IO error
     * @throws InfeasibleTestConfigException if test configuration is infeasible
     */
    private void refreshShadowTest(CatInput catInput) throws IOException, InfeasibleTestConfigException {

        // Calculate information values for all items
        double[] fisherInformation = null;
        if (catInput.getCatConfig().scoringMethodConfig().scoringMethod().equals(ScoringMethod.SUPPORTED_METHODS.EAP)) {
            fisherInformation = ItemSelectionMethodFactory.getInstance(catInput.getCatConfig().itemSelectionMethod(),
                    itemPar, thetaEst.getTheta(), thetaEst.getSe()).getSelectionCriteria();
        } else {
            throw new IllegalArgumentException("The scoring method specified is not supported!");
        }

        // Call L randomization method
        if (catInput.getCatConfig().lValue() > catInput.getAdaptiveStage()) {
            fisherInformation = applyLRandomToInfo(catInput.getCatConfig().lValue(), catInput.getAdaptiveStage(),
                    fisherInformation);
        }

        // Initialize eligibility indicators array
        boolean[] eligibilityIndicatorsItemSoft = new boolean[itemIds.length];
        boolean[] eligibilityIndicatorsPassageSoft = new boolean[passageIdsFromPassageTable.length];

        // Initialize bigM value
        double bigM = 0;

        if (exposureControlEnabled()) {
            bigM = initalizeExposureControl(catInput, eligibilityIndicatorsItemSoft, eligibilityIndicatorsPassageSoft,
                    fisherInformation);
        } else {
            clearExposureControl(eligibilityIndicatorsItemSoft, eligibilityIndicatorsPassageSoft);
        }

        // make copy of eligibility indicators for audit
        boolean[] eligibilityIndicatorsItemSoftForAudit = new boolean[eligibilityIndicatorsItemSoft.length];
        System.arraycopy(eligibilityIndicatorsItemSoft, 0, eligibilityIndicatorsItemSoftForAudit, 0,
                eligibilityIndicatorsItemSoftForAudit.length);

        // update eligibility indicators based on already administered items
        for (int adminIndex = 0; adminIndex < rowIndicesItemsAdmin.length; adminIndex++) {
            int alreadyAdministered = rowIndicesItemsAdmin[adminIndex];
            if (!eligibilityIndicatorsItemSoft[alreadyAdministered]) {
                eligibilityIndicatorsItemSoft[alreadyAdministered] = true;
            }
        }

        // array for storing eligibility results (set all values to true)
        boolean[] eligiblePassageItemsHard = new boolean[itemIds.length];
        Arrays.fill(eligiblePassageItemsHard, true);
        PrimitiveArraySet mapIndices = new PrimitiveArraySet();

        if (catInput.getTestConfig().getPassageTable().rowCount() > 0) {

            // create table of data for pre and post-processing steps in passage
            // management logic
            mapIndices = mapIndices.withStringArray(HEADER_ITEM_IDENTIFIERS, itemIds)
                    .withStringArray(HEADER_PASSAGE_IDENTIFIERS, passageIdsFromItemTable)
                    .withIntArray(HEADER_ITEM_INDICES, itemIndices)
                    .withDoubleArray(HEADER_FISHER_INFORMATION, fisherInformation)
                    .withBooleanArray(HEADER_ITEMS_ADMINISTERED, itemsAdministeredBoolean);

            // if any items in pool are associated with passages, then update
            // eligiblePassageItemsHard array
            eligiblePassageItemsHard = getEligiblePassageItems(itemsAdministeredString, mapIndices,
                    catInput.getTestConfig().getItemPoolTable());
        } else {
            mapIndices = mapIndices.withStringArray(HEADER_ITEM_IDENTIFIERS, itemIds)
                    .withIntArray(HEADER_ITEM_INDICES, itemIndices)
                    .withDoubleArray(HEADER_FISHER_INFORMATION, fisherInformation)
                    .withBooleanArray(HEADER_ITEMS_ADMINISTERED, itemsAdministeredBoolean);
        }

        // put data in form that solver can use (item level)
        List<SolverInputSingleItem> solverInputSingleItemList = prepItemDataForSolver(itemIds, fisherInformation,
                itemsAdministeredBoolean, eligibilityIndicatorsItemSoft, eligiblePassageItemsHard,
                catInput.getShadowTest().toArray(new String[0]));

        // put data in form that solver can use (passage level)
        List<SolverInputSinglePassage> solverInputSinglePassageList = prepPassageDataForSolver(
                passageIdsFromPassageTable, eligibilityIndicatorsPassageSoft);

        // Call solver
        SolverOutput outputData = shadowTestRun.runShadowTestAssembly(catInput.getAdaptiveStage(),
                solverInputSingleItemList, solverInputSinglePassageList, thetaEst.getTheta(), bigM,
                passageOrItemEligibilityAtThetaRange.getExposureType());

        // CBC solver doesn't return correct infeasible status. Need additional checking
        // on solutions
        if (outputData.getSolverStatus().equals(SolverOutput.SOLVER_STATS.INFEASIBLE) ||
                outputData.getSelectedItemIdentifiers().isEmpty()) {
            throw new InfeasibleTestConfigException("Test configuration is not feasible." +
                    "Please check the configuration parameters and/or constraitns");
        }

        solverTimeSecs = shadowTestRun.getTestAssembly().getTotalSolverTime();

        // prepare shadow test for administration
        itemsToAdminister = prepShadowTest(itemsAdministeredString, outputData.getSelectedItemRowIndicesArray(),
                mapIndices, catInput.getTestConfig().getItemPoolTable(), passagePoolTable,
                outputData.getPassageRowIndexSequence());
        shadowTest = outputData.getSelectedItemIdentifiers();
    }

    /**
     * Clears and resets the exposure control data.
     *
     * @param eligibilityIndicatorsItemSoft    the soft eligibility indicator for
     *                                         items
     * @param eligibilityIndicatorsPassageSoft the soft eligibility indicator for
     *                                         items
     */
    private void clearExposureControl(boolean[] eligibilityIndicatorsItemSoft,
            boolean[] eligibilityIndicatorsPassageSoft) {
        for (int i = 0; i < itemIds.length; i++) {
            eligibilityIndicatorsItemSoft[i] = true;
        }
        for (int j = 0; j < passageIdsFromPassageTable.length; j++) {
            eligibilityIndicatorsPassageSoft[j] = true;
        }

        // ExposureType needs to be set to NONE if exposure control is turned
        // off
        passageOrItemEligibilityAtThetaRange.setExposureControlType(ExposureControlType.NONE);
    }

    /**
     * Initializes the exposure control data.
     * <p>
     * Item fisher information is used to calculate the big M penalty for exposure
     * rate control.
     *
     * @param data                             the {@link CatInput} data
     * @param eligibilityIndicatorsItemSoft    the soft eligibility indicator for
     *                                         items
     * @param eligibilityIndicatorsPassageSoft the soft eligibility indicator for
     *                                         passages
     * @param fisherInformation                the fisher information of items
     * @return the big M penalty for exposure control
     */
    private double initalizeExposureControl(CatInput data, boolean[] eligibilityIndicatorsItemSoft,
            boolean[] eligibilityIndicatorsPassageSoft, double[] fisherInformation) {

        // find current theta interval and get associated eligibility indicators
        passageOrItemEligibilityAtThetaRange = findThetaInterval(data.getPassageOrItemEligibilityOverall(), thetaEst);

        // if exposure control is at the item level, then get item eligibility
        // and fix passage eligibility
        if (ExposureControlType.ITEM.equals(passageOrItemEligibilityAtThetaRange.getExposureType())) {
            boolean[] eligibilityIndicatorsItemSoftOriginal = passageOrItemEligibilityAtThetaRange
                    .getEligibilityIndicators();
            System.arraycopy(eligibilityIndicatorsItemSoftOriginal, 0, eligibilityIndicatorsItemSoft, 0,
                    eligibilityIndicatorsItemSoft.length);
            for (int j = 0; j < passageIdsFromPassageTable.length; j++) {
                eligibilityIndicatorsPassageSoft[j] = true;
            }
            // if exposure control is at the passage level, then get passage
            // eligibility and fix item
            // eligibility
        } else if (ExposureControlType.PASSAGE.equals(passageOrItemEligibilityAtThetaRange.getExposureType())) {
            for (int i = 0; i < itemIds.length; i++) {
                eligibilityIndicatorsItemSoft[i] = true;
            }
            boolean[] eligibilityIndicatorsPassageSoftOriginal = passageOrItemEligibilityAtThetaRange
                    .getEligibilityIndicators();
            System.arraycopy(eligibilityIndicatorsPassageSoftOriginal, 0, eligibilityIndicatorsPassageSoft, 0,
                    eligibilityIndicatorsPassageSoft.length);
        }

        // calculate value of bigM
        // get max info value
        double maxInfo = 0;
        for (int i = 0; i < fisherInformation.length; i++) {
            if (fisherInformation[i] > maxInfo) {
                maxInfo = fisherInformation[i];
            }
        }
        return maxInfo * 1.5;
    }

    /**
     * Sets up ability estimation before running shadow test.
     *
     * @param catInput the {@link CatInput} data
     * @throws IOException if there is an IO error
     */
    private void setupShadowTestRun(CatInput catInput) {
        if (catInput.getAdaptiveStage() == 0) {

            // if this is the first stage of the test, use initial theta value
            double initTheta = catInput.getCatConfig().initTheta();
            if (catInput.getCatConfig().scoringMethodConfig().scoringMethod()
                    .equals(ScoringMethod.SUPPORTED_METHODS.EAP)) {
                thetaEst = new ThetaEst(initTheta, 1.0d);
            } else {
                throw new IllegalArgumentException("The scoring method is not supported!");
            }
        } else {

            // if this is not the first stage of the test, estimate theta
            // take subset of item parameter matrix (i.e., the items
            // administered) to use for estimating theta
            RealMatrix itemParForScoring = itemPar.getSubMatrix(rowIndicesItemsAdmin, new int[] { 0, 1, 2, 3 });
            if (catInput.getCatConfig().scoringMethodConfig().scoringMethod() 
                    == ScoringMethod.SUPPORTED_METHODS.EAP) {
                scoringMethod = new ScoringMethodEap(itemParForScoring, catInput.getItemScores(),
                        (ScoringMethodConfigEap) catInput.getCatConfig().scoringMethodConfig());
            }
            thetaEst = scoringMethod.estimateTheta();
        }
    }

    /**
     * Checks if the test is completed.
     *
     * @return <code>true</code> if the test is completed; <code>false</code>
     *         otherwise.
     */
    private boolean testIsComplete() {
        return testLength == completedCount;
    }

    /**
     * Processes test data after test is completed.
     *
     * @param data the {@link CatInput} data
     */
    private void processCompleteTest(CatInput data) {
        itemsToAdminister = new CatItemsToAdminister(new ArrayList<>(), data.getItemsAdmin(), 1);
        testComplete = true;
    }

    /**
     * Checks if item or passage exposure control is enabled
     *
     * @return <code>true</code> if exposure control is enabled; <code>false</code>
     *         otherwise.
     */
    private boolean exposureControlEnabled() {
        return exposureControlType == ExposureControlType.ITEM || exposureControlType == ExposureControlType.PASSAGE;
    }

    /**
     * Creates an instance of {@link ShadowTestRun}.
     *
     * @param catInput an instance of {@link CatInput}
     * @throws IOException if there is an IO error
     */
    private void initializeShadowTestRun(CatInput catInput) throws IOException {
        shadowTestRun = new ShadowTestRun(catInput);
    }

    /**
     * Initializes data before running a CAT cycle.
     * <p>
     * Item and passage data are initialized from the {@link CatInput} data.
     *
     * @param catInput an instance of {@link CatInput}
     */
    private void initialize(CatInput catInput) {
        testLength = catInput.getTestConfig().getTestLength();
        passagePoolTable = catInput.getTestConfig().getPassageTable();

        // get item parameters
        itemPar = getItemParams(catInput.getItemPoolDataSet());

        // get arrays from item table
        itemIds = catInput.getItemPoolDataSet().getStringArrayCopy(Item.ColumnName.ITEM_ID.getColName());
        passageIdsFromItemTable = catInput.getItemPoolDataSet()
                .getStringArrayCopy(Item.ColumnName.ITEM_PASSAGE_ID.getColName());
        itemIndices = new int[itemIds.length];
        for (int i = 0; i < itemIndices.length; i++) {
            itemIndices[i] = i;
        }

        // get ids from passage table
        passageIdsFromPassageTable = new String[catInput.getTestConfig().getPassageTable().rowCount()];
        for (int i = 0; i < catInput.getTestConfig().getPassageTable().rowCount(); i++) {
            passageIdsFromPassageTable[i] = catInput.getTestConfig().getPassageTable().rows().get(i).get(0);
        }

        // initialize returned values
        shadowTest = new ArrayList<>(testLength);

        // set initial exposure control values
        // exposureType indicates whether exposure control is at the passage or
        // the item level (or none)
        exposureControlType = catInput.getPassageOrItemEligibility().getExposureType();

        // initialize exposure control output data object
        passageOrItemEligibilityAtThetaRange = new PassageOrItemEligibilityAtThetaRange();

        // set exposure type
        passageOrItemEligibilityAtThetaRange.setExposureControlType(exposureControlType);

        // initialize boolean array of items administered
        itemsAdministeredBoolean = new boolean[itemPar.getRowDimension()];

        // get indices of items administered
        itemsAdministeredString = catInput.getItemsAdmin().toArray(new String[0]);
        rowIndicesItemsAdmin = PrimitiveArrays.select(itemIds, itemsAdministeredString);

        // update items administered boolean array
        for (int adminIndex = 0; adminIndex < rowIndicesItemsAdmin.length; adminIndex++) {
            int alreadyAdministered = rowIndicesItemsAdmin[adminIndex];
            itemsAdministeredBoolean[alreadyAdministered] = true;
        }
    }

}
