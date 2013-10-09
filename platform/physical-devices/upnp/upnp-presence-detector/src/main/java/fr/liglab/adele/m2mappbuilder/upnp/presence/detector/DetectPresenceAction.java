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
package fr.liglab.adele.m2mappbuilder.upnp.presence.detector;

import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.service.upnp.UPnPAction;
import org.osgi.service.upnp.UPnPStateVariable;

public class DetectPresenceAction implements UPnPAction {

	final private String NAME = "DetectPresenceAction";
	
	//final private String NEW_RESULT_VALUE = "Result";
	
	
	
	private DetectedPresenceStateVariable detectedPresenceVariable;
	

	public DetectPresenceAction(DetectedPresenceStateVariable presence) {
	   this.detectedPresenceVariable = presence;
   }

	public String getName() {
		return NAME;
	}

	public String getReturnArgumentName() {
		return null;//"DetectedPresence";
	}

	public String[] getInputArgumentNames() {
		return new String[]{};
	}

	public String[] getOutputArgumentNames() {
		return  new String[]{"DetectedPresence"};
	}

	public UPnPStateVariable getStateVariable(String argumentName) {
		if (argumentName.equals(detectedPresenceVariable.getName())) return detectedPresenceVariable;
		return null;
	}

	public Dictionary invoke(Dictionary args) throws Exception {
		//Boolean value = (Boolean) args.get("Presence");
		//detectedPresenceVariable.setPresence(value);
		Hashtable result = new Hashtable();
		result.put("DetectedPresence", detectedPresenceVariable.getCurrentPresence());
		return result;
	}

}
