/**
 *
 *   Copyright 2011-2012 Universite Joseph Fourier, LIG, ADELE team
 *   Licensed under a specific end user license agreement;
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *     http://adeleresearchgroup.github.com/iCasa-Simulator/snapshot/license.html
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package fr.liglab.adele.osgi.shell.converter;

import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Map;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Property;
import org.apache.felix.service.command.Converter;

@Component
@Instantiate
@Deprecated
public class DictionaryConverter implements Converter {

	public final static String NEWLINE = System.getProperty("line.separator");
	public final static String TAB = "\t";
	
	@Property(name = Converter.CONVERTER_CLASSES, mandatory = true)
	private String[] m_class = new String[] { Dictionary.class.getName() };

	public Object convert(Class<?> desiredType, Object in) throws Exception {
		if (in instanceof Map) {
			return new Hashtable((Map) in);
		}
		return null;
	}

	public CharSequence format(Object target, int level, Converter escape)
			throws Exception {
		StringBuilder buff = new StringBuilder();
		if (target instanceof Dictionary) {
			Dictionary sourceDict = (Dictionary) target;
			Enumeration keys = ((Dictionary) target).keys();
			while (keys.hasMoreElements()) {
				Object key = (Object) keys.nextElement();
				buff.append(key).append(TAB).append(sourceDict.get(key)).append(NEWLINE);
			}
		}
		return buff;
	}

}
