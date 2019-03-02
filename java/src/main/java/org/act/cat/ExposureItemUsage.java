package org.act.cat;

public class ExposureItemUsage {
	private String itemId;
    private ThetaRange thetaRange;
    private double alpha;
    private double epsilon;

    public ExposureItemUsage(String itemId, ThetaRange thetaRange, double alpha, double epsilon) {
		this.itemId = itemId;
		this.thetaRange = thetaRange;
		this.alpha = alpha;
		this.epsilon = epsilon;
	}

	public String getItemId() {
		return itemId;
	}

	public ThetaRange getThetaRange() {
		return thetaRange;
	}

	public double getAlpha() {
		return alpha;
	}

	public double getEpsilon() {
		return epsilon;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

	public void setThetaRange(ThetaRange thetaRange) {
		this.thetaRange = thetaRange;
	}

	public void setAlpha(double alpha) {
		this.alpha = alpha;
	}

	public void setEpsilon(double epsilon) {
		this.epsilon = epsilon;
	}
	
	public void increaseAlpha() {
		alpha += 1;
	}
	
	public void increaseEpsilon() {
		epsilon += 1;
	}
	
}
