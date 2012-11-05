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

import play.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Member;
import org.osgi.play2.injection.BeanSource;
import org.osgi.play2.injection.Injector;
import org.osgi.play2.injection.OSGiService;
import org.osgi.play2.*;

public class OSGiModule extends Plugin implements BeanSource {

    public static boolean started = true;

    private final Application application;

    public OSGiModule(Application application)
    {
        this.application = application;
    }

    public void $init$() {
        // do nothing
    }

    /**
     * Called when the application starts.
     */
    public void onStart() {
        started = OSGiBootstrap.initOSGiFramework();
        if (started) {
            Injector.inject(this);
        } else {
            throw new IllegalStateException("OSGi container isn't started");
        }
    }

    /**
     * Called when the application stops.
     */
    public void onStop() {
        if (started) {
            OSGiBootstrap.stopOSGiFramework();
            started = false;
        }
    }

    public <T> T getBeanOfType(Class<T> clazz, Class<?> from, Member m, Annotation... qualifiers) {
        if (started) {
            for (Annotation anno : qualifiers) {
                if (anno.annotationType().equals(OSGiService.class)) {
                    return OSGi.service(clazz);
                }
            }
            Logger.warn("OSGi injection : Ignoring injection point for " + from.getName() + "." + m.getName());
            return null;
        } else {
            throw new IllegalStateException("OSGi container isn't started");
        }
    }

    public <T> Iterable<T> getBeanCollectionOfType(Class<T> clazz, Class<?> from, Member m, Annotation... qualifiers) {
        if (started) {
            for (Annotation anno : qualifiers) {
                if (anno.annotationType().equals(OSGiService.class)) {
                    if (((OSGiService) anno).value().equals("")) {
                        return OSGi.services(clazz, null);
                    } else {
                        return OSGi.services(clazz, ((OSGiService) anno).value());
                    }
                }
            }
            Logger.warn("OSGi injection : Ignoring injection point for " + from.getName() + "." + m.getName());
            return null;
        } else {
            throw new IllegalStateException("OSGi container isn't started");
        }
    }
  
}