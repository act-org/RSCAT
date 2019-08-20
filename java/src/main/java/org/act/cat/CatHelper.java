package org.act.cat;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.act.sim.SimulationFunctions;
import org.act.testdef.Item;
import org.act.util.PrimitiveArraySet;
import org.act.util.PrimitiveArrays;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math3.distribution.LogNormalDistribution;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;

import com.google.common.primitives.Doubles;

/**
 * A helper class providing CAT related utility methods.
 */
public class CatHelper {
    private static final String A_PARAM = Item.ColumnName.A_PARAM.getColName();
    private static final String B_PARAM = Item.ColumnName.B_PARAM.getColName();
    private static final String C_PARAM = Item.ColumnName.C_PARAM.getColName();
    private static final String A_PARAM_SE = Item.ColumnName.A_PARAM_SE.getColName();
    private static final String B_PARAM_SE = Item.ColumnName.B_PARAM_SE.getColName();
    private static final String C_PARAM_SE = Item.ColumnName.C_PARAM_SE.getColName();
    private static final String D_CONST = Item.ColumnName.D_CONSTANT.getColName();

    private CatHelper() {

        // not called
    }

    /**
     * Extracts item 3PL parameters and D-Constant value from item pool data set and reconstruct them into a {@link RealMatrix}
     * Object.
     *
     * @param itemPoolDataset the item pool data set in {@link PrimitiveArraySet}
     * @return the extracted item parameters in {@link RealMatrix}
     */
    public static RealMatrix getItemParams(PrimitiveArraySet itemPoolDataset) {
        double[] aPar = itemPoolDataset.getDoubleArrayCopy(A_PARAM);
        double[] bPar = itemPoolDataset.getDoubleArrayCopy(B_PARAM);
        double[] cPar = itemPoolDataset.getDoubleArrayCopy(C_PARAM);
        double[] dConst = itemPoolDataset.getDoubleArrayCopy(D_CONST);
        double[][] itemParData = { aPar, bPar, cPar, dConst };
        RealMatrix itemPar = MatrixUtils.createRealMatrix(itemParData);
        return itemPar.transpose();
    }

    /**
     * Helper method to generate samples of parameters of operational items.
     *
     * @param itemPoolDataset itemPoolDataset data
     * @param sampleSize sample size
     * @return generated samples, item by parameter type (0-ParaA, 1-ParaB,
     *         2-ParaC) by sample index
     */
    public static double[][][] getItemParamsSamples(PrimitiveArraySet itemPoolDataset, int sampleSize) {
        double[] aPar = itemPoolDataset.getDoubleArrayCopy(A_PARAM);
        double[] bPar = itemPoolDataset.getDoubleArrayCopy(B_PARAM);
        double[] cPar = itemPoolDataset.getDoubleArrayCopy(C_PARAM);
        double[] aParSE = itemPoolDataset.getDoubleArrayCopy(A_PARAM_SE);
        double[] bParSE = itemPoolDataset.getDoubleArrayCopy(B_PARAM_SE);
        double[] cParSE = itemPoolDataset.getDoubleArrayCopy(C_PARAM_SE);

        int itemNum = bPar.length;
        double[][][] itemParSamples = new double[itemNum][3][sampleSize];
        for (int i = 0; i < itemNum; i++) {
            // Sample parameter A
            double logaMean = Math.log(aPar[i] / Math.sqrt(1 + Math.pow(aParSE[i], 2) / Math.pow(aPar[i], 2)));
            double logaSD = Math.sqrt(Math.log(1 + Math.pow(aParSE[i], 2) / Math.pow(aPar[i], 2)));
            LogNormalDistribution logNDistA = new LogNormalDistribution(logaMean, logaSD);
            itemParSamples[i][0] = logNDistA.sample(sampleSize);

            // Sample parameter B
            NormalDistribution normDistB = new NormalDistribution(bPar[i], bParSE[i]);
            itemParSamples[i][1] = normDistB.sample(sampleSize);

            // Sample parameter C
            NormalDistribution normDistC = new NormalDistribution(cPar[i], cParSE[i]);
            double[] tempSamples = normDistC.sample(sampleSize);
            List<Double> filteredLogitSampleList = new ArrayList<>();
            for (double value : tempSamples) {
                if (value >= 0 && value <= 1) {
                    filteredLogitSampleList.add(Math.log(value / (1 - value)));
                }
            }
            double[] logitMeanSd = CatHelper.calMeanSD(filteredLogitSampleList);
            NormalDistribution normDistLogitC = new NormalDistribution(logitMeanSd[0], logitMeanSd[1]);
            double[] logitCSamples = normDistLogitC.sample(sampleSize);
            for (int n = 0; n < sampleSize; n++) {
                itemParSamples[i][2][n] = 1 / (1 + Math.exp(-logitCSamples[n]));
            }
        }
        return itemParSamples;
    }

    /**
     * Generates item parameter {@link RealMatrix} for the item(s) to be
     * administered in the current stage.
     *
     * @param itemIds the <code>String</code> list of item identifiers in the
     *            item pool
     * @param itemsToAdminThisStage the <code>String</code> list of item
     *            identifiers to be administered in the current stage
     * @param itemParams the instance of <code>RealMatrix</code> including
     *            parameters of all items in the item pool
     * @return the item parameters in {@link RealMatrix}
     */
    public static RealMatrix getItemParamsForScoring(List<String> itemIds, List<String> itemsToAdminThisStage,
            RealMatrix itemParams) {
        int[] rowIndices = PrimitiveArrays.select(itemIds.toArray(new String[0]),
                itemsToAdminThisStage.toArray(new String[0]));
        int[] colIndices = {0, 1, 2, 3};
        return itemParams.getSubMatrix(rowIndices, colIndices);
    }

    /**
     * Creates {@code CatInput} for the next adaptive stage.
     *
     * @param catInput the {@code CatInput} at the current adaptive stage
     * @param itemScores the {@link ItemScores} of the item administered in the current adaptive stage
     * @param itemsToAdminThisStage the identifiers of items administered at the current adaptive stage
     * @param stage the index of the next stage
     * @param itemToPassageIndexMap a {@link Map} collection that contains item identifiers as keys and passage
     *                              row indices as values
     * @param passageOrItemEligibilityOverall the exposure control eligibility indicators
     * @param itemsToAdminister the remaining item identifiers in the shadow test that haven't been administered. The items in
     *                          the list are ordered in the administration sequence.
     * @param shadowTest the item identifiers selected in the shadow test at the current adaptive stage. Items identifiers are in
     *                   their original row orders in the item pool.
     * @param previousTheta the estimate of the examinee's ability that is used to administer {@code itemsToAdminThisStage}
     * @param previousThetaSE the estimate standard error associated with {@code previousTheta}
     * @return the {@code CatInput} for the next adaptive stage
     * @see CatInput
     */
    public static CatInput createNextCatInput(CatInput catInput, ItemScores itemScores,
            List<String> itemsToAdminThisStage, int stage, Map<String, Integer> itemToPassageIndexMap,
            PassageOrItemEligibilityOverall passageOrItemEligibilityOverall,
            List<String> itemsToAdminister, List<String> shadowTest, double previousTheta, double previousThetaSE) {

        // Update item scores.
        int[] itemScoresAllInt = ArrayUtils.addAll(catInput.getItemScores().getItemScores(),
                itemScores.getItemScores());
        double[] respProbAll = ArrayUtils.addAll(catInput.getItemScores().getRespProbs(), itemScores.getRespProbs());
        ItemScores allItemScores = new ItemScores(itemScoresAllInt, respProbAll);

        // Update administered items.
        List<String> itemsAdminAll = new ArrayList<>(catInput.getItemsAdmin());
        itemsAdminAll.addAll(itemsToAdminThisStage);

        // Update item passage sequence
        List<Integer> administeredPassagesIndexSequence = SimulationFunctions
                .getPassageIndexOrderForAdministeredItems(itemToPassageIndexMap, itemsAdminAll);

        return new CatInputStandard.Builder().catConfig(catInput.getCatConfig()).testConfig(catInput.getTestConfig())
                .itemScores(allItemScores).itemsAdmin(itemsAdminAll).completedCount(itemsAdminAll.size())
                .adaptiveStage(stage).administeredPassageIndexSequence(administeredPassagesIndexSequence)
                .passageOrItemEligibilityOverall(passageOrItemEligibilityOverall).itemsToAdminister(itemsToAdminister)
                .shadowTest(shadowTest).previousTheta(previousTheta)
                .previousThetaSe(previousThetaSE).build();

    }


    /**
     * Converts the previous shadow test string array to the previous shadow test boolean array.
     *
     * @param itemIds an array of identifiers of all items from item pool
     * @param previousShadowTest an array of item identifiers from the shadow test
     * @return a {@code boolean} array that indicates if items in {@code itemIds} are selected in
     *         {@code previousShadowTest}
     */
    public static boolean[] getPreviousShadowBoolean(String[] itemIds, String[] previousShadowTest) {

        // initialize boolean array for previous shadow test to false
        boolean[] previousShadowBoolean = new boolean[itemIds.length];

        if (previousShadowTest.length > 0) {
            // get indices of previous shadow test
            int[] indicesPreviousShadow = PrimitiveArrays.select(itemIds, previousShadowTest);

            // set values to true that match shadow test indices
            for (int idx = 0; idx < indicesPreviousShadow.length; idx++) {
                int previousShadowItem = indicesPreviousShadow[idx];
                previousShadowBoolean[previousShadowItem] = true;
            }
        }
        return previousShadowBoolean;
    }

    // helper method for finding list of previously selected passages (their
    // indices)

    /**
     * Finds the list of previously selected passages (their indices).
     *
     * @param itemIds an array of identifiers of all items from the item pool
     * @param passageIdsFromItemTable an array of passage identifiers associated with items from the item pool
     * @param passageIdsFromPassageTable an array of passage identifiers from the passage pool
     * @param previousShadowTest an array of item identifiers from the shadow test
     * @return a list of indices of previously selected passages
     */
    public static List<Integer> getPreSelPassageSeqRowIndex(String[] itemIds, String[] passageIdsFromItemTable,
            String[] passageIdsFromPassageTable, String[] previousShadowTest) {

        // get indices of shadow test items
        int[] previousShadowTestIndices = PrimitiveArrays.select(itemIds, previousShadowTest);

        // initialize return object
        List<Integer> prePassageSeqRowIndex = new ArrayList<>();

        for (int i = 0; i < previousShadowTestIndices.length; i++) {

            // store passage ids in list unless they are redundant or have value "NONE"
            String previousPassageString = passageIdsFromItemTable[previousShadowTestIndices[i]];
            if (!previousPassageString.equalsIgnoreCase("none")) {
                int previousPassageIdx = PrimitiveArrays.select(passageIdsFromPassageTable, previousPassageString)[0];
                if (prePassageSeqRowIndex.isEmpty()) {
                    prePassageSeqRowIndex.add(previousPassageIdx);
                } else {
                    if (!(prePassageSeqRowIndex.get(prePassageSeqRowIndex.size() - 1).equals(previousPassageIdx))) {
                        prePassageSeqRowIndex.add(previousPassageIdx);
                    }
                }
            }
        }

        return prePassageSeqRowIndex;
    }

    /**
     * Calculates mean and standard deviation from provided data.
     *
     * @param data a list of data
     * @return an array with mean as the first element, standard deviation as the second element.
     */
    public static double[] calMeanSD(List<Double> data) {
        double mean = 0;
        double sd = 0;

        for (int n = 0; n < data.size(); n++) {
            mean += data.get(n);
        }
        mean /= data.size();
        double sqrDiff = 0;
        for (int n = 0; n < data.size(); n++) {
            sqrDiff += Math.pow(data.get(n) - mean, 2);
        }
        sd = Math.sqrt(sqrDiff / (data.size() - 1));
        return new double[] {mean, sd};
    }

    /**
     * Calculates mean and standard deviation from provided data.
     *
     * @param data a data array
     * @return an array with mean as the first element, standard deviation as the second element.
     */
    public static double[] calMeanSD(double[] data) {
        return CatHelper.calMeanSD(Doubles.asList(data));
    }
}
