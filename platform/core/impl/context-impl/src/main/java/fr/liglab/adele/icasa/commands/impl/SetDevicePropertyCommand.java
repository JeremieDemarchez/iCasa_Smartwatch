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
package fr.liglab.adele.icasa.commands.impl;

import fr.liglab.adele.icasa.ContextManager;
import fr.liglab.adele.icasa.commands.Signature;
import fr.liglab.adele.icasa.commands.AbstractCommand;
import fr.liglab.adele.icasa.commands.ScriptLanguage;
import fr.liglab.adele.icasa.location.LocatedDevice;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.PrintStream;

@Component(name = "SetDevicePropertyCommand")
@Provides
@Instantiate(name = "property-device-command")
public class SetDevicePropertyCommand extends AbstractCommand {

	@Requires
	private ContextManager contextManager;

    private static final String NAME = "set-device-property";


    public SetDevicePropertyCommand(){
        addSignature(new Signature(new String[]{ScriptLanguage.DEVICE_ID, ScriptLanguage.PROPERTY,
                ScriptLanguage.VALUE}));
    }


	/**
	 * Get the name of the Script and command gogo.
	 * 
	 * @return The command name.
	 */
	@Override
	public String getName() {
		return NAME;
	}


	@Override
	public Object execute(InputStream in, PrintStream out,JSONObject param, Signature signature) throws Exception {
		String deviceId = param.getString(signature.getParameters()[0]);
		String propertyId = param.getString(signature.getParameters()[1]);
		Object value = param.get(signature.getParameters()[2]);
		LocatedDevice device = contextManager.getDevice(deviceId);
		System.out.println("Trying to modifiy " + propertyId + " property ");
		if (device != null)
			device.setPropertyValue(propertyId, value);
		return null;
	}

	@Override
	public String getDescription() {
		return "Set the value of a device property.\n\t" + super.getDescription();
	}

}
