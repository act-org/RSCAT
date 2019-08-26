package org.act.sol;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.act.cat.ExposureControlType;
import org.act.mip.Constraint;
import org.act.mip.SolverConfig;
import org.act.mip.SolverOutput;
import org.act.testdef.Item;
import org.act.testdef.ItemRealTimeData;
import org.act.testdef.Passage;
import org.act.testdef.PassageRealTimeData;
import org.act.testdef.TestConfig;
import org.act.util.ContentTable;
import org.act.util.Xprm;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dashoptimization.XPRMInitializationFrom;
import com.dashoptimization.XPRMInitializeContext;
import com.dashoptimization.XPRMLicenseError;
import com.dashoptimization.XPRMModel;
import com.dashoptimization.XPRMTyped;
import com.dashoptimization.XPRMValue;

/**
 * This class implements functionalities to assemble a shadow-test according to
 * the provided item pool, passage pool, and test configuration.
 * <p>
 * Includes functionalities like loading item pool, loading constraint
 * configuration, loading shadow test assembly optimization model, updating item
 * attributes for shadow test assembly, solving shadow test assembly, and
 * returning test assembly results.
 */
public class TestAssembly {
    /**
     * Logger for solver performance metrics.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(TestAssembly.class);

    /**
     * MIP solver performance metric unit.
     */
    private static final String MILLIS = "ms";

    /**
     * Attribute delimiter.
     */
    private static final String ATTR_DELIMITER = "\\|";

    /**
     * DynamicModelInit object used for dynamic data initialization.
     */
    private DynamicModelInit dmInit;

    /**
     * The {@link TestConfig} used for the test assembly.
     */
    private final TestConfig testConfig;

    /**
     * The {@link SolverConfig} used for the test assembly.
     */
    private final SolverConfig solverConfig;

    /**
     * Number of items in the item pool.
     */
    private int itemNum;

    /**
     * Number of passages in the passage pool.
     */
    private int passageNum;

    /**
     * Number of constraints in the configuration.
     */
    private int constraintNum;

    /**
     * Required number of passages in the shadow test, lower bound.
     */
    private int reqPassageNumLB;

    /**
     * Required number of passages in the shadow test, upper bound.
     */
    private int reqPassageNumUB;

    /**
     * The list of {@link Item} in the item pool.
     */
    private List<Item> itemList = new ArrayList<>();

    /**
     * The list of {@link ItemRealTimeData} used for test assembly in CAT.
     */
    private List<ItemRealTimeData> itemRealTimeDataList = new ArrayList<>();

    /**
     * The list of item identifiers in the item pool.
     */
    private List<String> itemIdList = new ArrayList<>();

    /**
     * The list of passages in the passage pool.
     */
    private List<Passage> passageList = new ArrayList<>();

    /**
     * The list of {@link PassageRealTimeData}.
     */
    private List<PassageRealTimeData> passageRealTimeDataList = new ArrayList<>();

    /**
     * The list of passage identifiers in the passage pool.
     */
    private List<String> passageIdList = new ArrayList<>();

    /**
     * List of passages indices associated with items (index: item ID; value:
     * passage ID)
     */
    private List<Integer> itemPassageIndices = new ArrayList<>();

    /**
     * The optimal objective value of the shadow test.
     */
    private double objCost;

    /**
     * Map storing the selected passage IDs and their associated item IDs. The
     * key represents a passage ID and the value represents the list of item IDs
     * associated with the passage.
     */
    private Map<String, List<String>> selectedPassageItemMap = new HashMap<>();

    /**
     * List of administered item IDs in the shadow test.
     */
    private List<String> adminedItemList = new ArrayList<>();

    /**
     * List of {@link Constraint} from the constraint table.
     */
    private List<Constraint> constraintList = new ArrayList<>();

    /**
     * Input to the Mosel model.
     */
    private MoselInput moselInput;

    /**
     * Output from the Mosel model.
     */
    private MoselOutput moselOutput;

    /**
     * Instance of a {@code XPRMModel} model.
     */
    private XPRMModel mod = Xprm.newModel();

    /**
     * Performance metric, MIP building time.
     */
    private double buildingTime;

    /**
     * Performance metric, MIP solving time.
     */
    private double solvingTime;

    /**
     * Performance metric, other MIP time (e.g., result processing time).
     */
    private double otherTime;

    /**
     * Performance metric, the total MIP time. It is the sum of
     * {@code buildingTime}, {@code solvingTime}, and {@code otherTime}.
     */
    private double totalSolverTime;

    /**
     * Constructs a new {@link TestAssembly}.
     *
     * @param testConfig the test configuration
     * @param solverConfig the solver configuration
     * @throws IOException if there is a data IO failure
     * @see TestConfig
     * @see SolverConfig
     */
    public TestAssembly(TestConfig testConfig, SolverConfig solverConfig) throws IOException {
        this.testConfig = testConfig;
        this.solverConfig = solverConfig;
        reqPassageNumLB = testConfig.getNumPassageLB();
        reqPassageNumUB = testConfig.getNumPassageUB();

        // Load item pool and constraint configuration data
        loadDataFromTable(testConfig.getItemPoolTable(), testConfig.getItemIdColumnIndex(),
                testConfig.getPassageIdColumnIndexItemPool(), testConfig.getItemNumericColumn(),
                testConfig.getPassageTable(), testConfig.getPassageIdColumnIndexPassagePool(),
                testConfig.getPassageNumericColumn(), testConfig.getConstraintTable());
        itemNum = itemList.size();
        constraintNum = constraintList.size();

        // Initialize dynamic type data for the shadow-test MIP model
        dmInit = new DynamicModelInit(itemList, itemIdList, passageList, constraintList);
    }

    /**
     * Assembles a single shadow test.
     * <p>
     * A shadow test is assembled at each adaptive testing stage by solving the
     * test assembly MIP.
     *
     * @param stageIndex test step index.
     * @param preSolutions shadow test solutions in the previous step.
     * @param preAdmPassageSequence previously administered passage sequence.
     * @param preSelPassageSequence previously selected passage sequence.
     * @param theta current student ability.
     * @param bigM big M value.
     * @param exposureType exposure control type.
     * @return optimization result
     * @throws XPRMLicenseError if there is a license error
     * @throws IOException if there is a data IO failure
     */

    /**
     * Assembles a single shadow test.
     * <p>
     * A shadow test is assembled at each adaptive testing stage by solving the
     * test assembly MIP. The CAT stage index starts with 0 for the first stage.
     *
     * @param stageIndex the stage index in adaptive testing
     * @param theta the ability of the student
     * @param bigM the big M penalty value
     * @param exposureType the exposure control type
     * @return the solutions from MIP solver
     * @throws IOException if there is an IO exception
     */
    public SolverOutput assembleTest(int stageIndex, double theta, double bigM, ExposureControlType exposureType)
            throws IOException {

        // Initialize the model for the first time.
        if (stageIndex == 0) {
            prepareModel();
        }
        updateModel(stageIndex);
        mod.setExecParam("STUDENT_THETA", theta);
        mod.setExecParam("BIG_M", bigM);

        // Pass exposure control type indicator
        int exposureTypeIndicator;
        switch (exposureType) {
            case ITEM:
                exposureTypeIndicator = 1;
                break;
            case PASSAGE:
                exposureTypeIndicator = 2;
                break;
            case NONE:
                exposureTypeIndicator = 0;
                break;
            default:
                exposureTypeIndicator = 0;

        }

        mod.setExecParam("EXPOSURE_TYPE", exposureTypeIndicator);

        // Run MIP model
        mod.run();

        SolverOutput optResult = processMIPSol(mod);
        mod.reset();
        return optResult;
    }

    /**
     * Updates item real-time data for a CAT stage.
     *
     * @param stepIndex the CAT stage index
     */
    private void updateModel(int stepIndex) {
        long updateModelStart = System.currentTimeMillis();

        // Update item information values, administration status,and eligibility
        for (ItemRealTimeData itemRealTimeData : moselInput.itemRealTimeDataArr) {
            itemRealTimeData.info = itemRealTimeDataList.get(itemRealTimeData.rowIndex).info;
            itemRealTimeData.isAdmined = itemRealTimeDataList.get(itemRealTimeData.rowIndex).isAdmined;
            itemRealTimeData.isEligible = itemRealTimeDataList.get(itemRealTimeData.rowIndex).isEligible;
            itemRealTimeData.isEligibleHard = itemRealTimeDataList.get(itemRealTimeData.rowIndex).isEligibleHard;
        }
        mod.setExecParam("STEP_INDEX", stepIndex);
        long updateModelEnd = System.currentTimeMillis();
        LOGGER.trace("Update model time: {} {}", (updateModelEnd - updateModelStart),  MILLIS);
    }

    /**
     * This class defines Mosel input data.
     */
    class MoselInput {
        /**
         * Array of {@link ItemRealTimeData}.
         */
        ItemRealTimeData[] itemRealTimeDataArr;

        /**
         * Array of {@link PassageRealTimeData}.
         */
        PassageRealTimeData[] passageRealTimeDataArr;

        /**
         * Array of item ids in the item pool.
         */
        String[] itemIdArr;

        /**
         * Array of constraints in the constraint table.
         */
        Constraint[] constraintArr;

        /**
         * Array of passages indices associated with items (bucket index: item
         * ID; value: passage ID)
         */
        int[] itemPassageIndicesArr;

        /**
         * Constructs a new {@link MoselInput}.
         *
         * @param itemList the list of items in the item pool
         * @param passageList the list of passages in the passage pool
         * @param constraintList the list of constraints in the constraint table
         * @param itemPassageIndices the itemPassageIndices array
         */
        MoselInput(List<Item> itemList, List<Passage> passageList, List<Constraint> constraintList,
                int[] itemPassageIndices) {
            itemIdArr = new String[itemList.size()];
            itemRealTimeDataArr = new ItemRealTimeData[itemList.size()];
            passageRealTimeDataArr = new PassageRealTimeData[passageList.size()];
            constraintArr = new Constraint[constraintList.size()];
            itemPassageIndicesArr = itemPassageIndices;

            for (int i = 0; i < itemList.size(); i++) {
                itemIdArr[i] = itemList.get(i).getId();
                itemRealTimeDataArr[i] = itemRealTimeDataList.get(i);
            }

            for (int i = 0; i < passageList.size(); i++) {
                passageRealTimeDataArr[i] = passageRealTimeDataList.get(i);
            }

            for (int i = 0; i < constraintList.size(); i++) {
                constraintArr[i] = constraintList.get(i);
            }
        }
    }

    /**
     * This class defines Mosel output raw data.
     *
     */
    class MoselOutput {

        /**
         * Item selection solutions from the MIP solver.
         */
        double[] xSolutions;

        /**
         * Passage selection solutions from the MIP solver.
         */
        double[] zSolutions;

        /**
         * Constructs a new {@link MoselOutput}
         *
         * @param itemNum the number of items in the item pool
         * @param passagePoolSize the number of passages in the passage pool
         * @param cnstNum the number of constraints in the constraint table
         */
        MoselOutput(int itemNum, int passagePoolSize, int cnstNum) {
            xSolutions = new double[itemNum];
            zSolutions = new double[passagePoolSize];
        }
    }

    /**
     * Build the optimization MIP model according to loaded information.
     *
     * @throws XPRMLicenseError if there is a XPRM license error
     */
    private void prepareModel() throws XPRMLicenseError {
        long prepareModelStart = System.currentTimeMillis();

        // Initialize FICO input and output data object
        moselInput = new MoselInput(itemList, passageList, constraintList,
                ArrayUtils.toPrimitive(itemPassageIndices.toArray(new Integer[itemPassageIndices.size()])));
        moselOutput = new MoselOutput(itemNum, passageNum, constraintNum);

        // Associate Java objects with Mosel names
        mod.unbindAll();
        mod.bind("dti", moselInput.itemRealTimeDataArr);
        mod.bind("dtp", moselInput.passageRealTimeDataArr);
        mod.bind("cnst", moselInput.constraintArr);
        mod.bind("pidc", moselInput.itemPassageIndicesArr);
        mod.bind("solx", moselOutput.xSolutions);
        mod.bind("solz", moselOutput.zSolutions);
        mod.bind("dmInitInst", dmInit);

        // Set the execution parameters and bind the variable
        mod.setExecParam("ITEM_REALTIME_DATA", "dti(rowIndex,info,isEligible,isEligibleHard,isAdmined)");
        mod.setExecParam("PASSAGE_REALTIME_DATA", "dtp(rowIndex,isEligible)");
        mod.setExecParam("LENGTH", testConfig.getTestLength());
        mod.setExecParam("PASSAGE_NUM_LB", reqPassageNumLB);
        mod.setExecParam("PASSAGE_NUM_UB", reqPassageNumUB);
        mod.setExecParam("ITEM_NUM_PER_PASSAGE_LB", testConfig.getNumItemPerPassageLB());
        mod.setExecParam("ITEM_NUM_PER_PASSAGE_UB", testConfig.getNumItemPerPassageUB());
        mod.setExecParam("CNST_DATA", "cnst(rowIndex,type,level,calAttr,calLB,calUB)");
        mod.setExecParam("ITEM_PASSAGE_INDEX_DATA", "noindex,pidc");
        //mod.setExecParam("GAP_ABS", solverConfig.getAbsGap());
        //mod.setExecParam("GAP_RELV", solverConfig.getRelGap());
        //mod.setExecParam("TOL_INT", solverConfig.getIntTol());
        //mod.setExecParam("TIME_MAX", solverConfig.getMaxTime());
        mod.setExecParam("DYNAMIC_DATAFILE", "java:dmInitInst");
        mod.setExecParam("SOL_X", "noindex,solx");
        mod.setExecParam("SOL_Z", "noindex,solz");
        mod.setExecParam("WEIGHT_ELG", testConfig.getEligibilityPriority());
        mod.setExecParam("WEIGHT_LENGTH", testConfig.getLengthPriority());
        mod.setExecParam("ENABLE_ENEMY_ITEM", testConfig.isEnableEnemyItemConstraint());
        mod.setExecParam("USED_BIT_LENGTH", SolverConfig.USED_BIT_LENGTH);
        mod.setExecParam("CNST_GA_BIT_ARRAY_ITEM_NUM", itemNum / SolverConfig.USED_BIT_LENGTH + 1);
        mod.setExecParam("CNST_GA_BIT_ARRAY_PASSAGE_NUM", passageNum / (SolverConfig.USED_BIT_LENGTH + 1) + 1);
        mod.setExecParam("STAND_ALONE", false);
        mod.setExecParam("SAVE_INPUT", solverConfig.isSaveInput());

        long prepareModelEnd = System.currentTimeMillis();
        LOGGER.trace("prepare model time: {}{}", (prepareModelEnd - prepareModelStart), MILLIS);
    }

    /**
     * Processes MIP solutions and results to the {@link SolverOutput} format.
     *
     * @param modSol the XPRMModel object containing solutions and results
     * @return the processed result in a {@code SolverOutput} instance
     */
    private SolverOutput processMIPSol(XPRMModel modSol) {

        // Retrieve solver status
        int solverStatusCode = ((XPRMValue) modSol.findIdentifier("probstat")).asInteger();

        // Retrieve time information
        buildingTime = ((XPRMValue) modSol.findIdentifier("build_time")).asReal();
        solvingTime = ((XPRMValue) modSol.findIdentifier("solve_time")).asReal();
        otherTime = ((XPRMValue) modSol.findIdentifier("other_time")).asReal();
        totalSolverTime = buildingTime + solvingTime + otherTime;

        // Retrieve objective value
        objCost = modSol.getObjectiveValue();
        LOGGER.trace("Shadow test assembly objective value = {}", objCost);
        selectedPassageItemMap.clear();

        // Retrieve selected items in the current shadow test
        List<String> selectedItemIdentifiers = new ArrayList<>();
        List<Integer> selectedItemRowIndices = new ArrayList<>();
        for (int i = 0; i < moselOutput.xSolutions.length; i++) {

            // Count selected items in each contg\tent category
            if (moselOutput.xSolutions[i] >= 0.9) {
                Item selectedItem = itemList.get(i);

                // Collect passage information
                // TODO: change 1 to passage id column index
                String selectedPassageId = selectedItem.getCategAttrs().get(1);
                if (!"".equals(selectedPassageId)) {
                    if (selectedPassageItemMap.keySet() == null ||
                            !selectedPassageItemMap.keySet().contains(selectedPassageId)) {
                        List<String> selectedItemList = new ArrayList<>();
                        selectedItemList.add(itemIdList.get(i));
                        selectedPassageItemMap.put(selectedPassageId, selectedItemList);
                    } else {
                        selectedPassageItemMap.get(selectedPassageId).add(itemIdList.get(i));
                    }
                }

                selectedItemIdentifiers.add(itemIdList.get(i));
                selectedItemRowIndices.add(i);

            }
        }

        LOGGER.trace("Total number of items in the shadow test: {}", selectedItemIdentifiers.size());

        // Retrieve passage level solutions
        List<String> selectedPassageIdentifiers = new ArrayList<>();
        List<Integer> selectedPassageRowIndices = new ArrayList<>();
        List<Integer> passageSequence = new ArrayList<>();
        Map<Double, Integer> map = new TreeMap<>();
        for (int j = 0; j < moselOutput.zSolutions.length; j++) {
            if (moselOutput.zSolutions[j] >= 0.9) {
                selectedPassageIdentifiers.add(passageIdList.get(j));
                selectedPassageRowIndices.add(j);
            }
        }

        for (int i : map.values()) {
            passageSequence.add(i);
        }

        return new SolverOutput.SolverOutputBuilder().selectedItemIdentifiers(selectedItemIdentifiers)
                .selectedItemRowIndices(selectedItemRowIndices)
                .selectedPassageIdentifiers(selectedPassageIdentifiers)
                .selectedPassageRowIndices(selectedPassageRowIndices).passageRowIndexSequence(passageSequence)
                .objective(objCost)
                .solverStatus(solverStatusCode)
                .build();
    }

    /**
     * Loads data from content tables to Java objects.
     *
     * @param itemPoolTable item pool row oriented content table
     * @param constraintTable constraint row oriented content table
     * @throws IOException if there is an IO failure
     */
    /**
     * Loads data from content tables to related Java objects for the
     * initialization.
     *
     * @param itemPoolTable the item pool {@link ContentTable.RowOriented} table
     * @param itemIdColumnIndex the index of item ID column in the item table
     * @param passageIdColumnIndexItemPool the index of the passage ID column in
     *            the item table
     * @param itemNumericColumns the boolean indicators for numeric columns in
     *            the item table
     * @param passageTable the passage pool {@link ContentTable.RowOriented}
     *            table
     * @param passageIdColumnIndex the index of passage ID column in the passage
     *            table
     * @param passageNumericColumns the boolean indicators for numeric columns
     *            in the passage table
     * @param constraintTable the constraint {@link ContentTable.RowOriented}
     *            table
     */
    private void loadDataFromTable(ContentTable itemPoolTable, int itemIdColumnIndex, int passageIdColumnIndexItemPool,
            boolean[] itemNumericColumns, ContentTable passageTable, int passageIdColumnIndex,
            boolean[] passageNumericColumns, ContentTable constraintTable) {
        int rowIndex = 0;

        // Load passage table if it is not null
        List<String> columnNames;
        if (passageTable != null) {
            columnNames = passageTable.columnNames();
            for (List<String> row : passageTable.rows()) {
                String passageId = row.get(passageIdColumnIndex);
                Passage passage = new Passage(passageId, row, columnNames, passageNumericColumns, rowIndex);
                passageList.add(passage);
                PassageRealTimeData realTimeData = new PassageRealTimeData(passageId, rowIndex, true);
                passageRealTimeDataList.add(realTimeData);
                passageIdList.add(passageId);
                rowIndex++;
            }
        }
        passageNum = passageList.size();

        // Load item pool table
        rowIndex = 0;
        columnNames = itemPoolTable.columnNames();
        for (List<String> row : itemPoolTable.rows()) {
            String itemId = row.get(itemIdColumnIndex);
            Item item = new Item(itemId, row, columnNames, itemNumericColumns, rowIndex);
            itemList.add(item);
            itemIdList.add(itemId);

            ItemRealTimeData itemRealTimeData = new ItemRealTimeData(itemId, rowIndex, 0, true, true, false);
            itemRealTimeDataList.add(itemRealTimeData);

            String passageId = row.get(passageIdColumnIndexItemPool);
            int passageIndex = passageIdList.indexOf(passageId);
            itemPassageIndices.add(passageIndex);
            item.getCategAttrsNames().add("IsDiscreteItem");
            item.getCategAttrs().add(passageIndex >= 0 ? String.valueOf(false) : String.valueOf(true));

            // Create generic item
            rowIndex++;
        }
        itemNum = itemList.size();

        // Load constraint table if it is not null
        if (constraintTable != null) {
            int loadColumnIndex = constraintTable.columnNames().indexOf(Constraint.ColumnName.isLoaded.getName());
            int constraintTypeIndex = constraintTable.columnNames().indexOf(Constraint.ColumnName.type.getName());
            int objectTypeIndex = constraintTable.columnNames().indexOf(Constraint.ColumnName.level.getName());
            //int bitArrayIndex = constraintTable.columnNames().indexOf("Qualified Items");
            int lBIndex = constraintTable.columnNames().indexOf(Constraint.ColumnName.calLB.getName());
            int uBIndex = constraintTable.columnNames().indexOf(Constraint.ColumnName.calUB.getName());

            for (List<String> row : constraintTable.rows()) {
                String[] data = row.toArray(new String[0]);
                if (String.valueOf(true).equalsIgnoreCase(data[loadColumnIndex])) {
                    Constraint constraint = new Constraint(constraintTable.columnNames(), data, constraintList.size());
                    constraintList.add(constraint);

                    // Retrieve passage number LB and UB from constraints
/*                    if ("Passage".equals(data[objectTypeIndex])) {
                        int bitCount = StringUtils.countMatches(data[bitArrayIndex], "1");
                        if ("Include".equals(data[constraintTypeIndex]) && bitCount == passageNum) {
                            reqPassageNumLB = Math.max(Integer.parseInt(data[lBIndex]), reqPassageNumLB);
                            reqPassageNumUB = Math.min(Integer.parseInt(data[uBIndex]), reqPassageNumUB);
                        }
                    }*/
                }
            }

        }
        constraintNum = constraintList.size();
    }

    /**
     * Defines the data structure (key, value) for data transfer between Java
     * and FICO.
     *
     */
    public class DoubleWithIntIdx {
        /**
         * An integer key.
         */
        public int key = -1;

        /**
         * The data value.
         */
        public double value;
    }

    /**
     * Defines the data structure (key, value) for data transfer between Java
     * and FICO.
     *
     */
    public class DoubleWithStrIdx {
        /**
         * A String index.
         */
        public String key;

        /**
         * The data value.
         */
        public double value;
    }

    /**
     * This class includes the definition of the dynamic data structure and
     * related methods for the data exchange between Java and Mosel MIP model.
     *
     */
    public static class DynamicModelInit implements XPRMInitializationFrom {

        /**
         * The list of items in the item pool.
         */
        private List<Item> itemList;

        /**
         * The list of item identifiers.
         */
        private List<String> itemIdList;

        /**
         * The list of passages in the passage pool.
         */
        private List<Passage> passageList;

        /**
         * The list of constraints in the constraint table.
         */
        private List<Constraint> constraintList;

        /**
         * Constructs a new {@DynamicModelInit}.
         *
         * @param itemList the list of items in the item pool
         * @param itemIdList the list of item identifiers
         * @param passageList the list of passages in the passage pool
         * @param constraintList the list of constraints in the constraint table
         */
        public DynamicModelInit(List<Item> itemList, List<String> itemIdList, List<Passage> passageList,
                List<Constraint> constraintList) {
            this.itemList = itemList;
            this.itemIdList = itemIdList;
            this.passageList = passageList;
            this.constraintList = constraintList;
        }

        /**
         * Initializes dynamic data structure from Java to FICO.
         *
         * @param ictx the XPRMInitializeContext object
         * @param label the data label
         * @param type the XPRMTyped object
         * @return the boolean indicator of the initialization status, true if
         *         dynamic data are successfully initialized, otherwise return
         *         false
         */
        @Override
        public boolean initializeFrom(XPRMInitializeContext ictx, String label, XPRMTyped type) {
            try {
                switch (label) {

                    // Construct dynamic numeric data for items.
                    case "ITEM_NUM_ATTRS_DATA":
                        ictx.sendControl(XPRMInitializeContext.CONTROL_OPENLST);
                        for (Item item : itemList) {
                            int attrIndx = 0;
                            for (String attrName : item.getNumericAttrsNames()) {

                                // Construct index.
                                ictx.sendControl(XPRMInitializeContext.CONTROL_OPENNDX);
                                ictx.send(item.getRowIndex());
                                ictx.send(attrName);
                                ictx.sendControl(XPRMInitializeContext.CONTROL_CLOSENDX);

                                // Construct data.
                                ictx.send(item.getNumericAttrs().get(attrIndx));
                                attrIndx++;
                            }
                        }
                        ictx.sendControl(XPRMInitializeContext.CONTROL_CLOSELST);
                        return true;

                    // Construct dynamic categorical data for items.
                    case "ITEM_CATG_ATTRS_DATA":
                        ictx.sendControl(XPRMInitializeContext.CONTROL_OPENLST);
                        for (Item item : itemList) {
                            int attrIndx = 0;
                            for (String attrName : item.getCategAttrsNames()) {

                                // Construct index.
                                ictx.sendControl(XPRMInitializeContext.CONTROL_OPENNDX);
                                ictx.send(item.getRowIndex());
                                ictx.send(attrName);
                                ictx.sendControl(XPRMInitializeContext.CONTROL_CLOSENDX);

                                // Construct data.
                                ictx.send(item.getCategAttrs().get(attrIndx));
                                attrIndx++;
                            }
                        }
                        ictx.sendControl(XPRMInitializeContext.CONTROL_CLOSELST);
                        return true;

                    // Construct enemy item data.
                    case "ITEM_PRECLUDES_DATA":
                        ictx.sendControl(XPRMInitializeContext.CONTROL_OPENLST);
                        int precludesIdx = itemList.get(0).getCategAttrsNames().indexOf("Precludes");
                        if (precludesIdx != -1) {
                            for (Item item : itemList) {
                                ictx.sendControl(XPRMInitializeContext.CONTROL_OPENNDX);
                                ictx.send(item.getRowIndex());
                                ictx.sendControl(XPRMInitializeContext.CONTROL_CLOSENDX);
                                String precludesStr = item.getCategAttrs().get(precludesIdx);
                                ictx.sendControl(XPRMInitializeContext.CONTROL_OPENLST);
                                if (!"None".equalsIgnoreCase(precludesStr)) {
                                    String[] precludeItems = precludesStr.split(ATTR_DELIMITER);
                                    // Construct data
                                    for (String itemId : precludeItems) {

                                        // Add item row index only if the item
                                        // is in the item pool
                                        if (itemIdList.contains(itemId)) {
                                            ictx.send(itemIdList.indexOf(itemId));
                                        }
                                    }
                                }
                                ictx.sendControl(XPRMInitializeContext.CONTROL_CLOSELST);
                            }
                        }
                        ictx.sendControl(XPRMInitializeContext.CONTROL_CLOSELST);
                        return true;

                    // Construct dynamic numeric data for passages.
                    case "PASSAGE_NUM_ATTRS_DATA":
                        ictx.sendControl(XPRMInitializeContext.CONTROL_OPENLST);
                        // for each passage
                        for (Passage passage : passageList) {
                            int attrIndx = 0;
                            for (String attrName : passage.getNumericAttrsNames()) {
                                // construct index
                                ictx.sendControl(XPRMInitializeContext.CONTROL_OPENNDX);
                                ictx.send(passage.getRowIndex());
                                ictx.send(attrName);
                                ictx.sendControl(XPRMInitializeContext.CONTROL_CLOSENDX);
                                // construct data
                                ictx.send(passage.getNumericAttrs().get(attrIndx));
                                attrIndx++;
                            }
                        }
                        ictx.sendControl(XPRMInitializeContext.CONTROL_CLOSELST);
                        return true;

                    // Construct dynamic categorical data for passages.
                    case "PASSAGE_CATG_ATTRS_DATA":
                        ictx.sendControl(XPRMInitializeContext.CONTROL_OPENLST);
                        for (Passage passage : passageList) {
                            int attrIndx = 0;
                            for (String attrName : passage.getCategAttrsNames()) {

                                // Construct index
                                ictx.sendControl(XPRMInitializeContext.CONTROL_OPENNDX);
                                ictx.send(passage.getRowIndex());
                                ictx.send(attrName);
                                ictx.sendControl(XPRMInitializeContext.CONTROL_CLOSENDX);

                                // Construct data
                                ictx.send(passage.getCategAttrs().get(attrIndx));
                                attrIndx++;
                            }
                        }
                        ictx.sendControl(XPRMInitializeContext.CONTROL_CLOSELST);
                        return true;

                    case "CNST_FILTER_SET_ATTR_DATA":
                        ictx.sendControl(XPRMInitializeContext.CONTROL_OPENLST);
                        for (Constraint cnst : constraintList) {

                            // Constraint index
                            ictx.sendControl(XPRMInitializeContext.CONTROL_OPENNDX);
                            ictx.send(cnst.rowIndex);
                            ictx.sendControl(XPRMInitializeContext.CONTROL_CLOSENDX);

                            // Attributes with set filter
                            ictx.sendControl(XPRMInitializeContext.CONTROL_OPENLST);
                            for (String data : cnst.filterSetData.keySet()) {
                                ictx.send(data);
                            }
                            ictx.sendControl(XPRMInitializeContext.CONTROL_CLOSELST);
                            ictx.sendControl(XPRMInitializeContext.CONTROL_FLUSH);
                        }
                        ictx.sendControl(XPRMInitializeContext.CONTROL_CLOSELST);
                        return true;

                    case "CNST_FILTER_BOUND_ATTR_DATA":
                        ictx.sendControl(XPRMInitializeContext.CONTROL_OPENLST);
                        for (Constraint cnst : constraintList) {

                            // Constraint index
                            ictx.sendControl(XPRMInitializeContext.CONTROL_OPENNDX);
                            ictx.send(cnst.rowIndex);
                            ictx.sendControl(XPRMInitializeContext.CONTROL_CLOSENDX);

                            // Attributes with set filter
                            ictx.sendControl(XPRMInitializeContext.CONTROL_OPENLST);
                            for (String data : cnst.filterBoundsData.keySet()) {
                                ictx.send(data);
                            }
                            ictx.sendControl(XPRMInitializeContext.CONTROL_CLOSELST);
                            ictx.sendControl(XPRMInitializeContext.CONTROL_FLUSH);
                        }
                        ictx.sendControl(XPRMInitializeContext.CONTROL_CLOSELST);
                        return true;

                    case "CNST_FILTER_SET_DATA":
                        ictx.sendControl(XPRMInitializeContext.CONTROL_OPENLST);
                        for (Constraint cnst : constraintList) {
                            for (Entry<String, Set<String>> entry : cnst.filterSetData.entrySet()) {

                                // Constraint index and filter attribute
                                ictx.sendControl(XPRMInitializeContext.CONTROL_OPENNDX);
                                ictx.send(cnst.rowIndex);
                                ictx.send(entry.getKey());
                                ictx.sendControl(XPRMInitializeContext.CONTROL_CLOSENDX);

                                // Filter data
                                ictx.sendControl(XPRMInitializeContext.CONTROL_OPENLST);
                                for (String data : entry.getValue()) {
                                    ictx.send(data);
                                }
                                ictx.sendControl(XPRMInitializeContext.CONTROL_CLOSELST);
                            }
                            ictx.sendControl(XPRMInitializeContext.CONTROL_FLUSH);
                        }
                        ictx.sendControl(XPRMInitializeContext.CONTROL_CLOSELST);
                        return true;

                    case "CNST_FILTER_BOUND_DATA":
                        ictx.sendControl(XPRMInitializeContext.CONTROL_OPENLST);
                        for (Constraint cnst : constraintList) {
                            for (Entry<String, List<Double>> entry : cnst.filterBoundsData.entrySet()) {

                                // Constraint index and filter attribute
                                ictx.sendControl(XPRMInitializeContext.CONTROL_OPENNDX);
                                ictx.send(cnst.rowIndex);
                                ictx.send(entry.getKey());
                                ictx.sendControl(XPRMInitializeContext.CONTROL_CLOSENDX);

                                // Filter data
                                ictx.sendControl(XPRMInitializeContext.CONTROL_OPENLST);
                                for (double data : entry.getValue()) {
                                    ictx.send(data);
                                }
                                ictx.sendControl(XPRMInitializeContext.CONTROL_CLOSELST);
                            }
                            ictx.sendControl(XPRMInitializeContext.CONTROL_FLUSH);
                        }
                        ictx.sendControl(XPRMInitializeContext.CONTROL_CLOSELST);
                        return true;

                    default:
                    	LOGGER.error("Label {} not found.", label);
                        return false;
                }
            } catch (java.io.IOException e) {
            	LOGGER.error("{} could not be initialized", label);
                return false;
            }
        }

    }

    /**
     * Returns the ItemList object.
     *
     * @return An ItemList object
     */
    public List<Item> getItemList() {
        return itemList;
    }

    /**
     * Returns the list of previously selected items.
     *
     * @return the list of previously selected items
     */
    public List<String> getAdminedItemList() {
        return adminedItemList;
    }

    /**
     * Returns the list of added user constraints.
     *
     * @return the list of user constraints
     */
    public List<Constraint> getConstraintList() {
        return constraintList;
    }

    /**
     * Returns the list of item identifiers.
     *
     * @return the list of item identifiers
     */
    public List<String> getItemIdList() {
        return itemIdList;
    }

    /**
     * Returns the MIP building time.
     *
     * @return the MIP building time in second.
     */
    public double getBuildingTime() {
        return buildingTime;
    }

    /**
     * Returns the MIP solving time.
     *
     * @return the MIP solving time in second.
     */
    public double getSolvingTime() {
        return solvingTime;
    }

    /**
     * Returns the MIP other time.
     *
     * @return the MIP other time.
     */
    public double getOtherTime() {
        return otherTime;
    }

    /**
     * Returns the total solver time.
     *
     * @return the total solver time in second.
     */
    public double getTotalSolverTime() {
        return totalSolverTime;
    }

    /**
     * Returns the passage list.
     *
     * @return the passage list
     */
    public List<Passage> getPassageList() {
        return passageList;
    }

    /**
     * Returns the passage identifier list.
     *
     * @return the passage identifier list
     */
    public List<String> getPassageIdList() {
        return passageIdList;
    }

    /**
     * Returns the shadow test objective value.
     *
     * @return the shadow test objective value
     */
    public double getObjCost() {
        return objCost;
    }

    /**
     * Returns the item real time data list.
     *
     * @return the item real time data list
     */
    public List<ItemRealTimeData> getItemRealTimeDataList() {
        return itemRealTimeDataList;
    }

    /**
     * Returns the passage real time data list.
     *
     * @return the passage real time data list
     */
    public List<PassageRealTimeData> getPassageRealTimeDataList() {
        return passageRealTimeDataList;
    }
}
