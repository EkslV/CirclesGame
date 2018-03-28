package com.eksler.vadim.pulsinggame.models;

/**
 * Created by vadimeksler on 27/03/2018.
 */

public class Arc {
    private int startDegrees;
    private int endDegrees;

    public Arc(int startDegrees, int endDegrees) {
        this.startDegrees = startDegrees;
        this.endDegrees = endDegrees;
    }

    public int getStartDegrees() {
        return startDegrees;
    }

    public void setStartDegrees(int startDegrees) {
        this.startDegrees = startDegrees;
    }

    public int getEndDegrees() {
        return endDegrees;
    }

    public void setEndDegrees(int endDegrees) {
        this.endDegrees = endDegrees;
    }
}
