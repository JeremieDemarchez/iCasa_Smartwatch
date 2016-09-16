/**
 *
 *   Copyright 2011-2013 Universite Joseph Fourier, LIG, ADELE Research
 *   Group Licensed under a specific end user license agreement;
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *     http://adeleresearchgroup.github.com/iCasa/snapshot/license.html
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package fr.liglab.adele.icasa.helpers.device.provider;

import fr.liglab.adele.icasa.device.testable.TestReport;
import fr.liglab.adele.icasa.device.testable.TestResult;
import fr.liglab.adele.icasa.device.testable.Testable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Created by aygalinc on 22/08/16.
 */
public abstract class AbstractTestableService implements Testable {

    private final Object lock = new Object();

    private final List<Consumer<TestReport>> testResultCallback = new ArrayList<>();

    private TestReport testReport;

    private volatile boolean isRunning = false;

    protected void stop(){
        synchronized (lock){
            cancelTest();
        }
    }

    @Override
    public void beginTest(Consumer<TestReport> callback){
        synchronized (lock) {
            testResultCallback.add(callback);
        }
        if (!isRunning){
            isRunning=true;
            testLaunch();
        }
    }

    protected abstract void testLaunch();

    protected boolean isTestRunning(){
        return isRunning;
    }

    @Override
    public TestReport getLastTestReport() {
        synchronized (lock) {
            if (testReport == null) {
                testReport = new TestReport("Device not tested", TestResult.NOTTESTED);
            }
            return testReport;
        }
    }


    protected void cancelTest() {
        synchronized (lock) {
            isRunning = false;
            testReport = new TestReport("Test has been Cancel",TestResult.ABORTED);
            notifyAndClearConsumer();
        }
    }

    protected void finishTest(TestResult testResult,String testMessage){
        synchronized (lock) {
            isRunning = false;
            testReport = new TestReport(testMessage,testResult);
            notifyAndClearConsumer();
        }
    }

    private void notifyAndClearConsumer(){
        for (Consumer<TestReport> consumer : testResultCallback){
            consumer.accept(testReport);
        }
        testResultCallback.clear();
    }

}
