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
package fr.liglab.adele.icasa.device.zigbee.driver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents a type of device.
 * 
 * @author Thomas Leveque
 */
public enum TypeCode {

	IC001("PUSH_BUTTON"), IC002("POWER_SWITCH"), IC003("MOTION_SENSOR"), IC004("PRESENCE_SENSOR"), IA001("BINARY_LIGHT");

	private static final Logger logger = LoggerFactory
			.getLogger(TypeCode.class);

	private String friendlyName;

	private TypeCode(String name) {
		this.friendlyName = name;
	}

	public String getFriendlyName() {
		return friendlyName;
	}

	public static TypeCode getTypeCodeByFriendlyName(String friendlyName) {

		if (IC001.friendlyName.equals(friendlyName)) {
			return TypeCode.IC001;
		} else if (IC002.friendlyName.equals(friendlyName)) {
            return TypeCode.IC002;
        } else if (IC003.friendlyName.equals(friendlyName)) {
            return TypeCode.IC003;
        }else if (IC004.friendlyName.equals(friendlyName)) {
            return TypeCode.IC004;
        }else if (IA001.friendlyName.equals(friendlyName)) {
			return TypeCode.IA001;
        }else {
			logger.error("unknown device type friendly name : " + friendlyName);
			return null;
		}
	}
	
	public String toString() {
		switch (this) {
		case IC001 :
			return "IC001";
        case IC002:
            return "IC002";
		case IC003 :
			return "IC003";
        case IC004 :
            return "IC004";
		case IA001 :
			return "IA001";
		default :
			return null;
		}
	}
}
