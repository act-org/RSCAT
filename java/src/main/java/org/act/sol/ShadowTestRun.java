package org.act.sol;

import java.io.IOException;
import java.util.List;

import org.act.cat.CatInput;
import org.act.cat.ExposureControlType;
import org.act.mip.SolverConfig;
import org.act.mip.SolverInputSingleItem;
import org.act.mip.SolverInputSinglePassage;
import org.act.mip.SolverOutput;
import org.act.testdef.ItemRealTimeData;
import org.act.testdef.PassageRealTimeData;
import org.act.testdef.TestConfig;

/**
 * Provides access to optimization engine.
 */
public final class ShadowTestRun {
    private static final double BIG_M_DEFAULT = 10;
    private final TestConfig testConfig;
    private final SolverConfig solverConfig;
    private TestAssembly testAssembly;

    /**
     * Constructs a new {@link ShadowTestRun}.
     *
     * @param catInput the CAT input data
     * @throws IOException if there is an IO exception
     * @see CatInput
     */
    public ShadowTestRun(CatInput catInput) throws IOException {

        testConfig = catInput.getTestConfig();
        solverConfig = catInput.getCatConfig().solverConfig();
        testAssembly = new TestAssembly(testConfig, solverConfig);
    }

    /**
     * This method takes runtime test input data and run shadow test assembly
     * optimization in FICO. After the optimization is solved, shadow test
     * results are returned from FICO to Echo. This functionality is used in the
     * whole CAT/FICO operation cycle.
     *
     * @param stageIndex the index of current CAT stage. The first stage is 0.
     * @param itemInput an encapsulated real-time item input data object
     * @param passageInput an encapsulated real-time passage input data object
     * @param theta current student ability level
     * @param bigM The big M value for exposure control.
     * @param exposureType Whether exposure control is at the PASSAGE or ITEM
     *            level
     * @return an encapsulated output data object from FICO as the shadow test
     *         assembly result, including the selected item ID array, array of
     *         item ids whose ineligibility constraints have been relaxed,
     *         constraint activity array, and array of relaxed constraint ids
     * @throws IOException if there is a data IO failure
     * @throws ClassNotFoundException if class is not found
     */
    public SolverOutput runShadowTestAssembly(int stageIndex, List<SolverInputSingleItem> itemInput,
            List<SolverInputSinglePassage> passageInput, double theta, double bigM, ExposureControlType exposureType)
            throws IOException {

        int[] preSolutions = new int[itemInput.size()];

        // Update item information and attributes
        int count = 0;
        for (SolverInputSingleItem singleItemInput : itemInput) {
            // Update item administration information
            int itemIdex = testAssembly.getItemIdList().indexOf(singleItemInput.getItemIdentifier());
            ItemRealTimeData itemRealTimeData = testAssembly.getItemRealTimeDataList().get(itemIdex);
            if (singleItemInput.isAdministered() && !itemRealTimeData.isAdmined) {
                testAssembly.getAdminedItemList().add(singleItemInput.getItemIdentifier());
                itemRealTimeData.isAdmined = true;
            }
            // Update item information
            itemRealTimeData.info = singleItemInput.getInformation();
            // Update item eligibility
            itemRealTimeData.isEligible = singleItemInput.isEligible();
            // Update item hard eligibility
            itemRealTimeData.isEligibleHard = singleItemInput.isEligibleHard();

            // Retrieve solutions of previous shadow test
            preSolutions[count] = singleItemInput.isSelected() ? 1 : 0;

            count++;
        }

        // Update passage information and attributes
        if (passageInput != null) {
            for (SolverInputSinglePassage singlePassageInput : passageInput) {
                int passageIndex = testAssembly.getPassageIdList().indexOf(singlePassageInput.getPassageIdentifier());
                PassageRealTimeData passageRealTimeData = testAssembly.getPassageRealTimeDataList().get(passageIndex);
                // Update passage eligibility
                passageRealTimeData.isEligible = singlePassageInput.isEligible();
            }
        }

        // Assemble shadow test
        SolverOutput optResult = testAssembly.assembleTest(stageIndex, theta, bigM, exposureType);

        // Return output data
        return optResult;
    }

    /**
     * Assemble a shadow test without loading previous passage sequence.
     *
     * @param stageIndex the index of current CAT stage. The first stage is 0.
     * @param itemInput an encapsulated real-time input data object, including
     *            item information values and item eligibility marks
     * @param theta current student ability level
     * @return an encapsulated output data object
     * @throws IOException if there is a data IO failure
     */
    public SolverOutput runShadowTestAssembly(int stageIndex, List<SolverInputSingleItem> itemInput, double theta)
            throws IOException {
        return runShadowTestAssembly(stageIndex, itemInput, null, theta, BIG_M_DEFAULT, ExposureControlType.NONE);
    }

    /**
     * Returns the instance of {@link TestAssembly} used for shadow testing.
     *
     * @return the instance of {@code TestAssembly}
     */
    public TestAssembly getTestAssembly() {
        return testAssembly;
    }
}
