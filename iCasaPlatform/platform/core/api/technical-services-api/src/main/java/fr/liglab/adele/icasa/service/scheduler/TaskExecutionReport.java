/**
 *
 *   Copyright 2011-2012 Universite Joseph Fourier, LIG, ADELE Research
 *   Group Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package fr.liglab.adele.icasa.service.scheduler;

/**
 * User: torito
 * Date: 6/14/13
 * Time: 2:54 PM
 */
public interface TaskExecutionReport {

    /**
     * Gets the name under which the task was registered.
     * @return the name under which the task was registered.
     */
    String getTaskName();

    /**
     * Gets the number of times this task was executed.
     * @return the task execution count of this execution.
     */
    long getExecutionCount();

    /**
     * Gets the start time of the execution.
     * @return the start time, -1 is not started.
     */
    long getStartupTime();

    /**
     * Gets the termination time of the execution.
     * @return the termination task, -1 is not stopped.
     */
    long getTerminationTime();

    /**
     * Gets the object returned by the task execution, if any.
     *
     * @return the object returned by the task execution, or {@code null} if the
     *         task does not return something or has failed ({@link #getException()}
     *         returns the cause of the task execution failure).
     * @see #getException()
     */
    Object getResult();

    /**
     * Gets the cause of the task execution failure.
     *
     * @return the cause of the task execution failure, or {@code null} if the
     *         task execution was successful.
     * @see #getResult()
     */
    Throwable getException();

}
