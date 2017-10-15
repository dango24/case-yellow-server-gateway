package com.caseyellow.server.central.domain.test.model;

import java.io.File;
import java.util.Collections;
import java.util.Map;

public class TestWrapper {

    private Test test;
    private Map<String, File> snapshotLocation;

    public TestWrapper() {
    }

    public TestWrapper(Test test) {
        this(test, Collections.EMPTY_MAP);
    }

    public TestWrapper(Test test, Map<String, File> snapshotLocation) {
        this.test = test;
        this.snapshotLocation = snapshotLocation;
    }

    public Test getTest() {
        return test;
    }

    public void setTest(Test test) {
        this.test = test;
    }

    public Map<String, File> getSnapshotLocalLocation() {
        return snapshotLocation;
    }

    public void setSnapshotLocation(Map<String, File> snapshotLocation) {
        this.snapshotLocation = snapshotLocation;
    }
}
