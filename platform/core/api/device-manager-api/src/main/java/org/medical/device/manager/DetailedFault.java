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
public final class DetailedFault implements Cloneable {
	
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((_cause == null) ? 0 : _cause.hashCode());
		result = prime * result + ((_fault == null) ? 0 : _fault.hashCode());
		result = prime * result + ((_source == null) ? 0 : _source.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DetailedFault other = (DetailedFault) obj;
		if (_cause == null) {
			if (other._cause != null)
				return false;
		} else if (!_cause.equals(other._cause))
			return false;
		if (_fault != other._fault)
			return false;
		if (_source == null) {
			if (other._source != null)
				return false;
		} else if (!_source.equals(other._source))
			return false;
		return true;
	}
	
	@Override
	public DetailedFault clone() throws CloneNotSupportedException {
		return (DetailedFault) super.clone();
	}
	
	
}
