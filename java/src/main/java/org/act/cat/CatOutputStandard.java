package org.act.cat;

import java.util.List;

/**
 * An implementation of {@link CatOutput} for the standard CAT engine.
 */
class CatOutputStandard implements CatOutput {

    private final CatItemsToAdminister itemsToAdminister;
    private final ThetaEst thetaEst;
    private final boolean testComplete;
    private final PassageOrItemEligibilityAtThetaRange passageOrItemEligibilityAtThetaRange;
    private final List<String> shadowTest;
    private final double catEngineTime;
    private final double solverTime;

    /**
     * Constructs a new {@link CatOutputStandard}.
     *
     * @param itemsToAdminister the items to be administered in the format of
     *            {@link CatItemsToAdminister}
     * @param thetaEst the estimated theta in the format of {@link ThetaEst}
     * @param testComplete the boolean indicator for the test complete status
     * @param passageOrItemEligibilityAtThetaRange the item eligibility
     *            indicators at the theta range
     * @param shadowTest the string arry storing the shadow test result
     * @param catEngineTime the CAT engine time in seconds
     * @param solverTime the total MIP solver time in seconds
     */
    CatOutputStandard(CatItemsToAdminister itemsToAdminister, ThetaEst thetaEst, boolean testComplete,
            PassageOrItemEligibilityAtThetaRange passageOrItemEligibilityAtThetaRange, List<String> shadowTest,
            double catEngineTime, double solverTime) {
        this.itemsToAdminister = itemsToAdminister;
        this.thetaEst = thetaEst;
        this.testComplete = testComplete;
        this.passageOrItemEligibilityAtThetaRange = passageOrItemEligibilityAtThetaRange;
        this.shadowTest = shadowTest;
        this.catEngineTime = catEngineTime;
        this.solverTime = solverTime;
    }

    @Override
    public CatItemsToAdminister getItemsToAdminister() {
        return itemsToAdminister;
    }

    @Override
    public ThetaEst getThetaEst() {
        return thetaEst;
    }

    @Override
    public boolean getTestComplete() {
        return testComplete;
    }

    @Override
    public PassageOrItemEligibilityAtThetaRange getPassageOrItemEligibilityAtThetaRange() {
        return passageOrItemEligibilityAtThetaRange;
    }

    @Override
    public List<String> getShadowTest() {
        return shadowTest;
    }

    @Override
    public double getCatEngineTime() {
        return catEngineTime;
    }

    @Override
    public double getSolverTime() {
        return this.solverTime;
    }

    //////////////////////////////////////////////
    // private

}
