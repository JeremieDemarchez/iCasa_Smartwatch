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

import java.util.Map;

/**
 * TODO comments.
 * 
 * @author bourretp
 */
public interface Context {

    /**
     * TODO comments.
     * 
     * @return
     */
    Context getParent();

    /**
     * TODO comments.
     * 
     * @return
     */
    String getName();

    /**
     * TODO comments.
     * 
     * @return
     */
    String getAbsoluteName();

    // ======================================================

    /**
     * TODO comments.
     * 
     * @return
     */
    Map<String, Context> getChildren();

    /**
     * TODO comments.
     * 
     * @param childName
     * @return
     */
    Context getChild(String childName);

    /**
     * TODO comments.
     * 
     * @param childName
     * @return
     */
    Context createChild(String childName);

    /**
     * TODO comments.
     * 
     * @param childName
     * @return
     */
    void removeChild(String childName);

    // ======================================================

    /**
     * TODO comments.
     * 
     * @return
     */
    Map<String, Object> getProperties();

    /**
     * TODO comments.
     * 
     * @param key
     * @return
     */
    Object getProperty(String key);

    /**
     * TODO comments.
     * 
     * @param key
     * @param value
     * @return
     */
    Object setProperty(String key, Object value);

}
