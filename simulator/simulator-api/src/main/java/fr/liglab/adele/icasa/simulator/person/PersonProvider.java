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
package fr.liglab.adele.icasa.simulator.person;

import java.util.Set;

/**
 * Created by Eva on 12/02/2016.
 */
public interface PersonProvider {

    public void createPerson(String personId, String personName, String personType);

    public String createPerson(String personName, String personType);

    public void removePersonById(String personId);

    public boolean removePersonByName(String personName);

    public Set<String> getPersonTypes();

    public void removeAllPersons();
}
