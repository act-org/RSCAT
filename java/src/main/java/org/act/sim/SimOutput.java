package org.act.sim;

import java.util.List;
import java.util.Map;

import org.act.cat.ItemScores;
import org.act.cat.PassageOrItemEligibilityAtThetaRange;
import org.act.cat.ThetaEst;
import org.act.cat.ThetaRange;

/**
 * Define internal object for sim output results.
 *
 */
public class SimOutput {

	private final String studentId;
	private final double trueThetaVal;
	private final List<String> itemsAdministered;
	private final List<ItemScores> itemScoresList;
	private final double initTheta;
	public final ThetaEst finalTheta;
	private final List<PassageOrItemEligibilityAtThetaRange> passageOrItemEligibilityAtThetaRangeList;
	private final List<List<String>> shadowTestList;
	private final List<ThetaEst> thetaEstList;
	private final List<Double> catEngineTimeList;
	private final List<Integer> adaptiveStageList;
	private final List<List<Integer>> passageRowIndexSequences;
	private Map<ThetaRange, Map<String, Double>> itemExposureRates;

	private SimOutput(String studentId, double trueThetaVal, List<String> itemsAdministered,
			List<ItemScores> itemScoresList, double initTheta, ThetaEst finalTheta,
			List<PassageOrItemEligibilityAtThetaRange> passageOrItemEligibilityAtThetaRangeList,
			List<List<String>> shadowTestList, List<ThetaEst> thetaEstList, List<Double> catEngineTimeList,
			List<Integer> adaptiveStageList, List<List<Integer>> passageRowIndexSequences, 
			Map<ThetaRange, Map<String, Double>> itemExposureRates) {
		this.studentId = studentId;
		this.trueThetaVal = trueThetaVal;
		this.itemsAdministered = itemsAdministered;
		this.itemScoresList = itemScoresList;
		this.initTheta = initTheta;
		this.finalTheta = finalTheta;
		this.passageOrItemEligibilityAtThetaRangeList = passageOrItemEligibilityAtThetaRangeList;
		this.shadowTestList = shadowTestList;
		this.thetaEstList = thetaEstList;
		this.catEngineTimeList = catEngineTimeList;
		this.adaptiveStageList = adaptiveStageList;
		this.passageRowIndexSequences = passageRowIndexSequences;
		this.itemExposureRates = itemExposureRates;
	}

	public String getStudentId() {
		return studentId;
	}

	public double getTrueThetaVal() {
		return trueThetaVal;
	}

	public List<String> getItemsAdministered() {
		return itemsAdministered;
	}

	public List<ItemScores> getItemScoresList() {
		return itemScoresList;
	}

	public double getInitTheta() {
		return initTheta;
	}

	public ThetaEst getFinalTheta() {
		return finalTheta;
	}

	public List<PassageOrItemEligibilityAtThetaRange> getPassageOrItemEligibilityAtThetaRangeList() {
		return passageOrItemEligibilityAtThetaRangeList;
	}

	public List<List<String>> getShadowTestList() {
		return shadowTestList;
	}

	public List<ThetaEst> getThetaEstList() {
		return thetaEstList;
	}

	public List<Double> getCatEngineTimeList() {
		return catEngineTimeList;
	}

	public List<Integer> getAdaptiveStageList() {
		return adaptiveStageList;
	}

	public List<List<Integer>> getPassageRowIndexSequences() {
		return passageRowIndexSequences;
	}

	public Map<ThetaRange, Map<String, Double>> getItemExposureRates() {
		return itemExposureRates;
	}

	public void setItemExposureRates(Map<ThetaRange, Map<String, Double>> itemExposureRates) {
		this.itemExposureRates = itemExposureRates;
	}

	public static class Builder {

		// Required fields
		private final String studentId;
		private final double trueThetaVal;

		// Optional fields
		private List<String> itemsAdministered;
		private List<ItemScores> itemScoresList;
		private double initTheta;
		private ThetaEst finalTheta;
		private List<PassageOrItemEligibilityAtThetaRange> passageOrItemEligibilityAtThetaRangeList;
		private List<List<String>> shadowTestList;
		private List<ThetaEst> thetaEstList;
		private List<Double> catEngineTimeList;
		private List<Integer> adaptiveStageList;
		private List<List<Integer>> passageRowIndexSequences;
		private Map<ThetaRange, Map<String, Double>> itemExposureRates;

		public Builder(String studentId, double trueThetaVal) {
			this.studentId = studentId;
			this.trueThetaVal = trueThetaVal;
		}

		public Builder itemsAdministered(List<String> itemsAdministered) {
			this.itemsAdministered = itemsAdministered;
			return this;
		}

		public Builder itemScoresList(List<ItemScores> itemScoresList) {
			this.itemScoresList = itemScoresList;
			return this;
		}

		public Builder initTheta(double initTheta) {
			this.initTheta = initTheta;
			return this;
		}

		public Builder finalTheta(ThetaEst finalTheta) {
			this.finalTheta = finalTheta;
			return this;
		}

		public Builder itemEligibilityList(
				List<PassageOrItemEligibilityAtThetaRange> passageOrItemEligibilityAtThetaRangeList) {
			this.passageOrItemEligibilityAtThetaRangeList = passageOrItemEligibilityAtThetaRangeList;
			return this;
		}

		public Builder shadowTestList(List<List<String>> shadowTestList) {
			this.shadowTestList = shadowTestList;
			return this;
		}

		public Builder thetaEstList(List<ThetaEst> thetaEstList) {
			this.thetaEstList = thetaEstList;
			return this;
		}

		public Builder catEngineTimeList(List<Double> catEngineTimeList) {
			this.catEngineTimeList = catEngineTimeList;
			return this;
		}

		public Builder adaptiveStageList(List<Integer> adaptiveStageList) {
			this.adaptiveStageList = adaptiveStageList;
			return this;
		}

		public Builder passageRowIndexSequences(List<List<Integer>> passageRowIndexSequences) {
			this.passageRowIndexSequences = passageRowIndexSequences;
			return this;
		}

		public SimOutput build() {
			return new SimOutput(studentId, trueThetaVal, itemsAdministered, itemScoresList, initTheta, finalTheta,
					passageOrItemEligibilityAtThetaRangeList, shadowTestList, thetaEstList, catEngineTimeList,
					adaptiveStageList, passageRowIndexSequences, itemExposureRates);

		}

	}
}
