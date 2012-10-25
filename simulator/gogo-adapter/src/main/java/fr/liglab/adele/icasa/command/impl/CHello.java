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
package fr.liglab.adele.icasa.command.impl;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.StaticServiceProperty;
import org.json.JSONException;
import org.json.JSONObject;

import fr.liglab.adele.icasa.command.ICommandService;

@Component
@Provides(properties = {
        @StaticServiceProperty(type = "java.lang.String", name = ICommandService.PROP_NAMESPACE, value= ICommandService.DEFAULT_NAMESPACE),
        @StaticServiceProperty(type = "java.lang.String", name = ICommandService.PROP_NAME, value= "hello"),
        @StaticServiceProperty(type = "java.lang.String", name = ICommandService.PROP_DESCRIPTION, value="Hello say hello $1")
})
@Instantiate
public class CHello implements ICommandService{

	public Object execute(InputStream in, OutputStream out,
			JSONObject param) throws JSONException {
		System.out.println("Params = "+ param.toString());
		return "Hello "+ param.getString("name");
	}
	
	public static void main(String[] args) throws JSONException {
		Map param = new HashMap();
		param.put("name", "test");
		System.out.println(new CHello().execute(System.in, System.out, new JSONObject(param)));
	}
}
