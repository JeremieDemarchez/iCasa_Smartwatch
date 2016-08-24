package fr.liglab.adele.icasa.orange.utils;

import fr.liglab.adele.icasa.orange.service.TestReport;
import fr.liglab.adele.icasa.orange.service.TestRunningException;
import fr.liglab.adele.icasa.orange.service.ZwaveTestResult;
import fr.liglab.adele.icasa.orange.service.ZwaveTestStrategy;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * Created by aygalinc on 22/08/16.
 */
public abstract class AbstractZwaveTestStrategy implements ZwaveTestStrategy{

    private final Object lock = new Object();

    private final Map<String,ZwaveTestResult> testResultMap = new HashMap<>();

    private final Map<String,BiConsumer<String,TestReport>> testResultCallback = new HashMap<>();

    protected void stop(){
        synchronized (lock){
            testResultMap.clear();
            testResultCallback.clear();
        }
    }


    @Override
    public void beginTest(String nodeId, BiConsumer<String, TestReport> callback, boolean interrupt) throws TestRunningException {
        if (nodeId == null){
            return;
        }

        synchronized (lock) {
            ZwaveTestResult result = getLastTestResult(nodeId);

            if (result.equals(ZwaveTestResult.RUNNING)) {
                if (interrupt) {
                    cancelTest(nodeId);
                } else {
                    throw new TestRunningException();
                }
            }

            if (callback != null) {
                testResultCallback.put(nodeId, callback);
            }

            testResultMap.put(nodeId, ZwaveTestResult.RUNNING);
        }
    }


    @Override
    public ZwaveTestResult getLastTestResult(String nodeId) {
        if (nodeId == null){
            throw new NullPointerException();
        }

        synchronized (lock) {
            ZwaveTestResult testResult = testResultMap.get(nodeId);
            if (testResult == null) {
                return ZwaveTestResult.NOTTESTED;
            }
            return testResult;
        }
    }


    private void cancelTest(String nodeId) {
        synchronized (lock) {
            testResultCallback.remove(nodeId);
            testResultMap.put(nodeId, ZwaveTestResult.ABORTED);
        }
    }

    protected void finishTest(String nodeId,ZwaveTestResult testResult,String testMessage){
        if (nodeId == null || testResult == null){
            return;
        }
        synchronized (lock) {
            if (ZwaveTestResult.RUNNING.equals(testResultMap.get(nodeId))){
                testResultMap.put(nodeId, testResult);
                BiConsumer<String,TestReport> toCall = testResultCallback.get(nodeId);
                if (toCall != null){
                    toCall.accept(nodeId,new TestReport(testMessage,testResult));
                }
            }
        }


    }

}
