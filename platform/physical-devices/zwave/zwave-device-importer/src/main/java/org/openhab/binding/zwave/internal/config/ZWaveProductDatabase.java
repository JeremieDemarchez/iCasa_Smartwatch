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
package org.openhab.binding.zwave.internal.config;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;

/**
 * Implements the top level functions for the XML product database This class
 * includes helper functions to manipulate the database and facilitate access to
 * the database.
 * 
 * @author Chris Jackson
 * @since 1.4.0
 * 
 */

/**
 * FAKE CLASS TO AVOID THREAD INTERRUPTION
 */
public class ZWaveProductDatabase {
	private static final Logger logger = LoggerFactory.getLogger(ZWaveProductDatabase.class);

	public ZWaveProductDatabase() {

	}

	/**
	 * Constructor for the product database
	 * 
	 * @param Language
	 *            defines the language in which all labels will be returned
	 */
	public ZWaveProductDatabase(Languages Language) {

	}

	/**
	 * Constructor for the product database
	 * 
	 * @param Language
	 *            defines the language in which all labels will be returned
	 */
	public ZWaveProductDatabase(String Language) {

	}

	public List<ZWaveDbManufacturer> GetManufacturers() {
		return Collections.emptyList();
	}

	public List<ZWaveDbProduct> GetProducts() {

			return Collections.emptyList();


	}

	/**
	 * Finds the manufacturer in the database.
	 * 
	 * @param manufacturerId
	 * @return true if the manufacturer was found
	 */
	public boolean FindManufacturer(int manufacturerId) {
		return false;
	}

	/**
	 * Finds a product in the database
	 * 
	 * @param manufacturerId
	 *            The manufacturer ID
	 * @param productType
	 *            The product type
	 * @param productId
	 *            The product ID
	 * @return true if the product was found
	 */
	public boolean FindProduct(int manufacturerId, int productType, int productId, String version) {
			return false;
	}

	/**
	 * Finds a product in the database. FindManufacturer must be called before
	 * this function.
	 * 
	 * @param productType
	 *            The product type
	 * @param productId
	 *            The product ID
	 * @return true if the product was found
	 */
	public boolean FindProduct(int productType, int productId, String version) {
			return false;

	}

	/**
	 * Returns the manufacturer name. FindManufacturer or FindProduct must be
	 * called before this method.
	 * 
	 * @return String with the manufacturer name, or null if not found.
	 */
	public String getManufacturerName() {
			return null;
	}

	/**
	 * Returns the manufacturer ID. FindManufacturer or FindProduct must be
	 * called before this method.
	 * 
	 * @return Integer with the manufacturer ID, or null if not found.
	 */
	public Integer getManufacturerId() {
	return null;
	}

	/**
	 * Returns the product name. FindProduct must be called before this method.
	 * 
	 * @return String with the product name, or null if not found.
	 */
	public String getProductName() {

			return null;

	}

	/**
	 * Returns the product model. FindProduct must be called before this method.
	 * 
	 * @return String with the product model, or null if not found.
	 */
	public String getProductModel() {
	return null;

	}

	/**
	 * Returns the number of endpoints from the database. FindProduct must be
	 * called before this method.
	 * 
	 * @return number of endpoints
	 */
	public Integer getProductEndpoints() {
		return null;


	}

	/**
	 * Checks if a specific command class is implemented by the device.
	 * FindProduct must be called before this method.
	 * 
	 * @param classNumber
	 *            the class number to check
	 * @return true if the class is supported
	 */
	public boolean doesProductImplementCommandClass(Integer classNumber) {
		return false;
	}

	/**
	 * Returns the command classes implemented by the device.
	 * FindProduct must be called before this method.
	 * 
	 * @return true if the class is supported
	 */
	public List<ZWaveDbCommandClass> getProductCommandClasses() {


		return null;


	}

	/**
	 * Returns the configuration parameters list. FindProduct must be called
	 * before this method.
	 * 
	 * @return List of configuration parameters
	 */
	public List<ZWaveDbConfigurationParameter> getProductConfigParameters() {
	return Collections.emptyList();

	}

	/**
	 * Returns the associations list. FindProduct must be called before this
	 * method.
	 * 
	 * @return List of association groups
	 */
	public List<ZWaveDbAssociationGroup> getProductAssociationGroups() {
		return null;

	}



	/**
	 * Helper function to find the label associated with the specified database
	 * language If no language is defined, or if the label cant be found in the
	 * specified language the english label will be returned.
	 * 
	 * @param labelList
	 *            A List defining the label
	 * @return String of the respective language
	 */
	public String getLabel(List<ZWaveDbLabel> labelList) {
		return null;
	}

	/**
	 * enum defining the languages used for the multilingual labels in the
	 * product database
	 * 
	 */
	public enum Languages {
		ENGLISH("en"), GERMAN("de");

		private String value;

		private Languages(String value) {
			this.value = value;
		}

		public static Languages fromString(String text) {
			if (text != null) {
				for (Languages c : Languages.values()) {
					if (text.equalsIgnoreCase(c.value)) {
						return c;
					}
				}
			}
			return ENGLISH;
		}

		public String toString() {
			return this.value;
		}
	}
}
