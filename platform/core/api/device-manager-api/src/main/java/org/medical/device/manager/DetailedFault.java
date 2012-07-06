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
package org.medical.device.manager;

/**
 * Represents details of a detected device fault.
 * 
 * @author Thomas Leveque
 *
 */
public class DetailedFault {
	
	private Fault _fault;
	private String _cause;
	private String _source;
	
	public DetailedFault(Fault fault, String cause, String source) {
		super();
		_fault = fault;
		_cause = cause;
		_source = source;
	}
	
	public final Fault getFault() {
		return _fault;
	}
	
	public final String getCause() {
		return _cause;
	}
	
	public final String getSource() {
		return _source;
	}
}
