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
package org.osgi.play2;

import org.osgi.framework.Bundle;

public class OSGiUtil {

    public static String getState(int i) {
        if (i == Bundle.ACTIVE) {
            return "ACTIVE";
        }
        if (i == Bundle.INSTALLED) {
            return "INSTALLED";
        }
        if (i == Bundle.RESOLVED) {
            return "RESOLVED";
        }
        if (i == Bundle.STARTING) {
            return "STARTING";
        }
        if (i == Bundle.STOPPING) {
            return "STOPPING";
        }
        if (i == Bundle.UNINSTALLED) {
            return "UNINSTALLED";
        }
        return "UNKNOWN";
    }
}