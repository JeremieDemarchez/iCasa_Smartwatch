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
package fr.liglab.adele.icasa.commands.impl;

import fr.liglab.adele.icasa.commands.AbstractCommand;
import fr.liglab.adele.icasa.commands.ScriptLanguage;
import fr.liglab.adele.icasa.commands.Signature;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Provides;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.PrintStream;

@Component(name = "SetDevicePropertyCommand")
@Provides
@Instantiate(name = "property-device-command")
public class SetDevicePropertyCommand extends AbstractCommand {

/**	@Requires
	private ContextManager contextManager;**/

    private static final String NAME = "set-device-property";

    private final Signature typedSignature;
    private final Signature unTypedSignature;

    public SetDevicePropertyCommand(){
        unTypedSignature = new Signature(new String[]{ScriptLanguage.DEVICE_ID, ScriptLanguage.PROPERTY, ScriptLanguage.VALUE});
        typedSignature =  new Signature(new String[]{ScriptLanguage.DEVICE_ID, ScriptLanguage.PROPERTY, ScriptLanguage.VALUE, ScriptLanguage.TYPE});
        addSignature(unTypedSignature);
        addSignature(typedSignature);
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
		//Object value = param.get(signature.getParameters()[2]);
        Object value = getValue(param,signature);
	/**	LocatedDevice device = contextManager.getDevice(deviceId);
		if (device != null)
			device.setPropertyValue(propertyId, value);**/
		return null;
	}

	@Override
	public String getDescription() {
		return "Set the value of a device property.\n\t Accepted types [int, float, double, boolean, string] .\n\t" + super.getDescription();
	}

    private Object getValue(JSONObject param, Signature signature) throws Exception{
        Object value = null;
        if (signature.equals(typedSignature)){
            String type = param.getString(ScriptLanguage.TYPE);
            if (type.equalsIgnoreCase("int") || type.equalsIgnoreCase("Integer") || type.equalsIgnoreCase("i")){
                value = Integer.valueOf(param.getString(ScriptLanguage.VALUE));
            } else if (type.equalsIgnoreCase("float") || type.equalsIgnoreCase("f")){
                value = Float.valueOf(param.getString(ScriptLanguage.VALUE));
            } else if (type.equalsIgnoreCase("double") || type.equalsIgnoreCase("d")){
                value = Double.valueOf(param.getString(ScriptLanguage.VALUE));
            } else if (type.equalsIgnoreCase("boolean") || type.equalsIgnoreCase("b")){
                value = Boolean.valueOf(param.getString(ScriptLanguage.VALUE));
            } else if (type.equalsIgnoreCase("String") || type.equalsIgnoreCase("s")){
                value = param.getString(ScriptLanguage.VALUE);
            } else {
                throw new Exception("Unknown Data type for zone variable " + type);
            }
            return value;
        }
        //If type is not given, it will try to see if it is Double or String
        try{
            value = param.getDouble(ScriptLanguage.VALUE);//See if is a number.
        }catch (Exception ex){
            value = param.getString(ScriptLanguage.VALUE);//If not, it will be treated as a string.
        }
        return value;

    }

}
