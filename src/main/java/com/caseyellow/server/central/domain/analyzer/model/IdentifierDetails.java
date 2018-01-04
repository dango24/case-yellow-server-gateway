package com.caseyellow.server.central.domain.analyzer.model;

public class IdentifierDetails {

    private String identifier;
    private double meanRatio;
    private int size;

    public IdentifierDetails() {
    }

    public IdentifierDetails(String identifier, double meanRatio, int size) {
        this.identifier = identifier;
        this.meanRatio = meanRatio;
        this.size = size;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public double getMeanRatio() {
        return meanRatio;
    }

    public void setMeanRatio(double meanRatio) {
        this.meanRatio = meanRatio;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
