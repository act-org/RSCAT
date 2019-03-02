package org.act.cat;

/**
 * This class defines the passage information values used in CAT.
 */
public class CatPassageInfo {

    String id;
    double value;
    boolean passage;

    public CatPassageInfo(String id, double value, boolean passage) {
        this.id = id;
        this.value = value;
        this.passage = passage;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public boolean getPassage() {
        return passage;
    }

    public void setPassage(boolean passage) {
        this.passage = passage;
    }

}
