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
package fr.liglab.adele.icasa.dependency.handler.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Target;
import java.util.Comparator;

/**
 * This annotation declares a service requirement.
 * @author <a href="mailto:dev@felix.apache.org">Felix Project Team</a>
 */
@Target({ ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD, ElementType.ANNOTATION_TYPE })
@Inherited
public @interface RequiresDevice {

    /**
     * Set the LDAP filter of the dependency.
     * Default : no filter
     */
    String filter() default "";

    /**
     * Set if the dependency is optional.
     * Default : false
     */
    boolean optional() default false;

    /**
     * Set the dependency id.
     * Default : empty
     */
    String id();

    /**
     * Set the binding policy.
     * Acceptable policy are dynamic, static and dynamic-priority.
     * Default: dynamic.
     */
    String policy() default "dynamic";

    /**
     * Set the comparator.
     * The indicated class must implement {@link Comparator}
     */
    Class comparator() default Comparator.class;

    /**
     * Set the from attribute.
     */
    String from() default "";

    /**
     * Set the required service specification.
     * This attribute is required for Collection field.
     */
    String specification() default "";

    /**
     * Set to true if the service dependency is injected
     * as a proxy.
     * Default: true
     */
    boolean proxy() default true;
    
    boolean aggregate() default true;
    
    /**
     * List of mandatory properties in dependency
     * @return
     */
    String[] mandatoryProps() default {""};
    
    /**
     * Dependency type (field, bind or unbind)
     * @return
     */
    String type();
}
