package org.act.cat;

public class ItemScores {
    private int[] itemScores;
    private double[] respProbs;

    public ItemScores(int[] itemScores, double[] respProb) {
        this.itemScores = itemScores;
        this.respProbs = respProb;
    }

    public int[] getItemScores() {
        return itemScores;
    }

    public double[] getRespProbs() {
        return respProbs;
    }

    public ItemScores withItemScores(int[] newItemScores) {
        return new ItemScores(newItemScores, respProbs);
    }

    public ItemScores withRespProbs(double[] newRespProbs) {
        return new ItemScores(itemScores, newRespProbs);
    }
}
