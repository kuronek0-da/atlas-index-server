package com.atlasindex.model.enums;

public enum EloResult {
    WIN(1.0),
    LOSS(0.0);

    private double value;

    EloResult(double value) {
        this.value = value;
    }

    public double getScore() {
        return value;
    }
}
