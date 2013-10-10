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
package fr.liglab.adele.m2mappbuilder.upnp.presence.detector;

import org.osgi.service.upnp.UPnPStateVariable;

public class DeviceStateStateVariable implements UPnPStateVariable {

	final private String NAME = "Device State";
	final private String DEFAULT_VALUE = "activated";
	// private UPnPEventNotifier notifier;
	//private Boolean presence = Boolean.FALSE;
	private String state = "activated";

	public DeviceStateStateVariable() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.service.upnp.UPnPStateVariable#getName()
	 */
	public String getName() {
		return NAME;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.service.upnp.UPnPStateVariable#getJavaDataType()
	 */
	public Class getJavaDataType() {
		return String.class;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.service.upnp.UPnPStateVariable#getUPnPDataType()
	 */
	public String getUPnPDataType() {
		return TYPE_STRING;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.service.upnp.UPnPStateVariable#getDefaultValue()
	 */
	public Object getDefaultValue() {
		return DEFAULT_VALUE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.service.upnp.UPnPStateVariable#getAllowedValues()
	 */
	public String[] getAllowedValues() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.service.upnp.UPnPStateVariable#getMinimum()
	 */
	public Number getMinimum() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.service.upnp.UPnPStateVariable#getMaximum()
	 */
	public Number getMaximum() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.service.upnp.UPnPStateVariable#getStep()
	 */
	public Number getStep() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.service.upnp.UPnPStateVariable#sendsEvents()
	 */
	public boolean sendsEvents() {
		return true;
	}


	public void setPresence(String value) {
		if (!value.equals(state)) {
			String oldValue = state;
			state = value;
			// if (notifier != null)
			// notifier.propertyChange(new
			// PropertyChangeEvent(this,"Power",oldValue,value));
		}
	}

	public Object getCurrentValue() {
		return state;
	}
}
