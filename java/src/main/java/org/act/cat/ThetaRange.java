package org.act.cat;

/**
 * This class defines a range of ability value theta.
 * <p>
 * An instance of {@code ThetaRange} is used for exposure control. For the
 * exposure control at overall ability level, defines a single large range to
 * represent all possible theta values.
 */
public class ThetaRange {
    private final double minThetaInclusive;
    private final double maxThetaExclusive;

    /**
     * Constructs a new {@link ThetaRange}.
     *
     * @param minThetaInclusive the lower bound of the range, inclusive.
     * @param maxThetaExclusive the upper bound of the range, exclusive.
     */
    public ThetaRange(double minThetaInclusive, double maxThetaExclusive) {
        this.minThetaInclusive = minThetaInclusive;
        this.maxThetaExclusive = maxThetaExclusive;
    }

    /**
     * Returns the lower bound of the theta range.
     *
     * @return the lower bound of the theta range
     */
    public double getMinThetaInclusive() {
        return minThetaInclusive;
    }

    /**
     * Returns the upper bound of the theta range.
     *
     * @return the upper bound of the theta range
     */
    public double getMaxThetaExclusive() {
        return maxThetaExclusive;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        ThetaRange other = (ThetaRange) obj;
        return !(Double.doubleToLongBits(maxThetaExclusive) != Double.doubleToLongBits(other.maxThetaExclusive)
        		|| Double.doubleToLongBits(minThetaInclusive) != Double.doubleToLongBits(other.minThetaInclusive));
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
