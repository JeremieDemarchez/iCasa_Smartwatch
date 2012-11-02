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
package org.osgi.play2.injection;

import java.lang.annotation.Annotation;
import java.lang.reflect.Member;

/**
 * @author Thomas Leveque
 */
public interface BeanSource {

    public <T> T getBeanOfType(Class<T> clazz, Class<?> from, Member m, Annotation... qualifiers);

    public <T> Iterable<T> getBeanCollectionOfType(Class<T> clazz, Class<?> from, Member m, Annotation... qualifiers);

}
