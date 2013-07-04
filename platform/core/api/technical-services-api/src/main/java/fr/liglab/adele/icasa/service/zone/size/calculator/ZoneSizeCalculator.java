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
package fr.liglab.adele.icasa.service.zone.size.calculator;

/**
 * The Interface of service ZoneSize provides information on the size of the
 * different Zones of the iCASA simulator
 * 
 * @author yoann.maurel@imag.fr
 */
public interface ZoneSizeCalculator {

	/**
	 * Gets the X size in meter of the given zone.
	 * 
	 * @param zoneId
	 *            unique id of the given zone
	 * @return the x size in meter
	 */
	public double getXInMeter(String zoneId);

	/**
	 * Gets the Y size in meter of the given zone.
	 * 
	 * @param zoneId
	 *            unique id of the given zone
	 * @return the y size in meter
	 */
	public double getYInMeter(String zoneId);

	/**
	 * Gets the surface in meter square.
	 * 
	 * @param zoneId
	 *            unique id of the given zone
	 * @return the surface in meter square
	 */
	public double getSurfaceInMeterSquare(String zoneId);

}