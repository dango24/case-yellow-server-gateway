package com.caseyellow.server.central.domain.analyzer.model;

public class AnalyzedImage {

    private String message;
    private boolean analyzed;
    private double result;

    public AnalyzedImage() {
        this(-1);
    }

    public AnalyzedImage(boolean analyzed) {
        this(analyzed, -1);
    }

    public AnalyzedImage(double result) {
        this(true, result);
    }

    public AnalyzedImage(boolean analyzed, double result) {
        this.analyzed = analyzed;
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isAnalyzed() {
        return analyzed;
    }

    public void setAnalyzed(boolean analyzed) {
        this.analyzed = analyzed;
    }

    public double getResult() {
        return result;
    }

    public void setResult(double result) {
        this.result = result;
    }
}
