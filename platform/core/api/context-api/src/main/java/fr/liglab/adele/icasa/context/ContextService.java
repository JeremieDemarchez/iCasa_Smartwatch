/**
 *
 *   Copyright 2011-2012 Universite Joseph Fourier, LIG, ADELE team
 *   Licensed under the Apache License, Version 2.0 (the "License");
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
package fr.liglab.adele.icasa.context;

/**
 * Service definition of the iCASA context service.
 * 
 * @author bourretp
 */
public interface ContextService {

    String CONTEXT_PATH_SEPARATOR = "/";

    String CONTEXT_PATH_WILDCARD = "*";

    /**
     * TODO comments.
     * 
     * @return
     */
    Context getRootContext();
    
    /**
     * TODO comments.
     * 
     * @param contextPath
     * @param listener
     */
    void addContextListener(ContextListener listener, String contextPath);

    /**
     * TODO comments.
     * 
     * @param listener
     */
    void removeContextListener(ContextListener listener);

}
