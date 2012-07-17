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
package org.medical.device.manager.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.medical.device.manager.DetailedFault;

/**
 * Utilities to manipulate device faults.
 * 
 * @author Thomas Leveque
 *
 */
public class FaultUtil {

	public static boolean sameFault(DetailedFault fault, DetailedFault otherFault) {
		return same(fault.getCause(), otherFault.getCause()) && 
				same(fault.getFault(), otherFault.getFault()) &&
				same(fault.getSource(), otherFault.getSource());
	}
	
	public static boolean same(Object oldValue, Object newValue) {
		return ((oldValue == null) && (newValue == null)) || ((oldValue != null) && oldValue.equals(newValue));
	}
	
	public static List<DetailedFault> clone(List<DetailedFault> detailedFaults) {
		List<DetailedFault> clonedList = new ArrayList<DetailedFault>();
		for (DetailedFault fault : detailedFaults) {
			try {
				clonedList.add(fault.clone());
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
			}
		}
		
		return Collections.unmodifiableList(clonedList);
	}
	
	/**
	 * Removes specified old faults from specified fault list and add all specified new faults.
	 * 
	 * @param oldFaults fault list
	 * @param newFaults fault list
	 * @param faults fault list
	 */
	public static void mergeFaults(List<DetailedFault> oldFaults, List<DetailedFault> newFaults, List<DetailedFault> faults) {
		if (oldFaults != null)
			removeFaults(oldFaults, faults);
		addFaults(newFaults,faults);
	}

	public static void addFaults(List<DetailedFault> newFaults,
			List<DetailedFault> faults) {
		faults.addAll(newFaults);
		removeDuplicates(faults);
	}

	public static void removeFaults(List<DetailedFault> oldFaults,
			List<DetailedFault> faults) {
		faults.removeAll(oldFaults);
	}
	
	@SuppressWarnings(value = { "rawtypes", "unchecked" })
	public static void removeDuplicates(List arlList) {
		Set set = new HashSet();
		List newList = new ArrayList();
		for (Iterator iter = arlList.iterator(); iter.hasNext();) {
			Object element = iter.next();
			if (set.add(element))
				newList.add(element);
		}
		arlList.clear();
		arlList.addAll(newList);
	}
}
