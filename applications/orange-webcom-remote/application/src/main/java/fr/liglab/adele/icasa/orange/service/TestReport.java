package fr.liglab.adele.icasa.orange.service;

/**
 * Created by aygalinc on 24/08/16.
 */
public class TestReport {

    public final String testMessage;

    public final ZwaveTestResult testResult;

    public TestReport(String testMessage, ZwaveTestResult testResult) {
        this.testMessage = testMessage;
        this.testResult = testResult;
    }
}
