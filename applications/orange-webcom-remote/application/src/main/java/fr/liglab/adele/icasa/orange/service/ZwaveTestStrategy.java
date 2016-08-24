package fr.liglab.adele.icasa.orange.service;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Created by aygalinc on 22/08/16.
 */
public interface ZwaveTestStrategy {

    List<String> getTestTargets();

    void beginTest(String nodeId, BiConsumer<String,TestReport> callback,boolean interrupt) throws TestRunningException;

    ZwaveTestResult getLastTestResult(String nodeId);

}
