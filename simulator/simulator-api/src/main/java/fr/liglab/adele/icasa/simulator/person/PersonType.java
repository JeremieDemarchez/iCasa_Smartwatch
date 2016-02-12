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

/**
 * This class represents a person type
 *
 * 
 */
public enum PersonType {
	OLD_MAN("Old man"),OLD_WOMAN("Old woman"),WOMAN("Woman"), MAN("Man"),
	GRANDFATHER("Grandfather"),GRANDMOTHER("Grandmother"),FATHER("Father"),MOTHER("Mother"),
	BOY("Boy"),GIRL("Girl");

	private final String name;

	/**
	 * Default constructor
	 * @param name person type name
	 */
	private PersonType(String name) {
		this.name = name;
	}

	/**
	 * Gets the person type name (id)
	 * 
	 * @return person type name
	 */
	public String getName() {
		return name;
	}

	/**
	 *
	 *
	 * @return person type name
	 */
	public static PersonType getPersonType(String personType) {
		if (personType == null){
			return null;
		}
		if (personType.equals(OLD_MAN.getName())){
			return OLD_MAN;
		}
		if (personType.equals(OLD_WOMAN.getName())){
			return OLD_WOMAN;
		}
		if (personType.equals(WOMAN.getName())){
			return WOMAN;
		}
		if (personType.equals(MAN.getName())){
			return MAN;
		}
		if (personType.equals(GRANDFATHER.getName())){
			return GRANDFATHER;
		}
		if (personType.equals(GRANDMOTHER.getName())){
			return GRANDMOTHER;
		}
		if (personType.equals(FATHER.getName())){
			return FATHER;
		}
		if (personType.equals(MOTHER.getName())){
			return MOTHER;
		}
		if (personType.equals(BOY.getName())){
			return BOY;
		}
		if (personType.equals(GIRL.getName())){
			return GIRL;
		}
		return null;
	}
}
