package org.act.cat;

/**
 * This class defines the item scores and related probability of correct answers.
 */
public class ItemScores {
    private int[] scores;
    private double[] respProbs;

    /**
     * Construct a new {@link ItemScores}.
     *
     * @param itemScores an array of item scores
     * @param respProb an array of correct responses
     */
    public ItemScores(int[] itemScores, double[] respProb) {
        this.scores = itemScores;
        this.respProbs = respProb;
    }

    /**
     * Returns item scores.
     *
     * @return item scores
     */
    public int[] getItemScores() {
        return scores;
    }

    /**
     * Returns correct response probability.
     *
     * @return correct response probability
     */
    public double[] getRespProbs() {
        return respProbs;
    }
}
