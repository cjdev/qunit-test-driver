package com.cj.qunitTestDriver

class Status {
    public final int numPassed;
    public final int numFailed;
    public final int numFailedAssertions;
    public final String brokenTests;
    public final boolean isComplete;
    public final List<TestStatus> tests;
    
    public Status(int numPassed, int numFailed, int numFailedAssertions, String brokenTests, boolean isComplete, List<TestStatus> tests) {
        super();
        this.numPassed = numPassed;
        this.numFailed = numFailed;
        this.numFailedAssertions = numFailedAssertions;
        this.brokenTests = brokenTests;
        this.isComplete = isComplete;
        this.tests = tests;
    }
    
        
    
}
