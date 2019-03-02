package org.act.cat;

import java.util.Objects;

public class ThetaRange {
    private double minThetaInclusive;
    private double maxThetaExclusive;

    public ThetaRange(double minThetaInclusive, double maxThetaExclusive) {
    	this.minThetaInclusive = minThetaInclusive;
    	this.maxThetaExclusive = maxThetaExclusive;
    }

    public ThetaRange (ThetaRange otherRange) {
        this.minThetaInclusive = otherRange.getMinThetaInclusive();
        this.maxThetaExclusive = otherRange.getMaxThetaExclusive();
    }

    public double getMinThetaInclusive() {
        return minThetaInclusive;
    }

    public void setMinThetaInclusive(Double minThetaInclusive) {
        this.minThetaInclusive = minThetaInclusive;
    }

    public double getMaxThetaExclusive() {
        return maxThetaExclusive;
    }

    public void setMaxThetaExclusive(Double maxThetaExclusive) {
        this.maxThetaExclusive = maxThetaExclusive;
    }
    
    @Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ThetaRange other = (ThetaRange) obj;
		if (Double.doubleToLongBits(maxThetaExclusive) != Double.doubleToLongBits(other.maxThetaExclusive))
			return false;
		if (Double.doubleToLongBits(minThetaInclusive) != Double.doubleToLongBits(other.minThetaInclusive))
			return false;
		return true;
	}

    
    @Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(maxThetaExclusive);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(minThetaInclusive);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}
}
