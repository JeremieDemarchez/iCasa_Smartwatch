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
package fr.liglab.adele.icasa.command.impl;

import java.io.InputStream;
import java.io.PrintStream;

import fr.liglab.adele.icasa.Signature;
import fr.liglab.adele.icasa.ICasaCommand;
import org.apache.felix.ipojo.Factory;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.json.JSONObject;



@Component
@Provides
@Instantiate
public class CommandFactory implements ICasaCommand {

	@Requires
	Factory[] m_factories;
    Signature empty = new Signature(new String[0]);

    public CommandFactory(){

    }

	public Object execute(InputStream in, PrintStream out, JSONObject param)
			throws Exception {
		String factoryName = param.getString("name");
		for (Factory f : m_factories) {
			if(f.getName().equals(factoryName)){
				return f;
			}
		}
		System.out.println("Factory not found "+ factoryName);
		return null;
	}

    /**
     * To validate the given parameters.
     *
     * @param param The parameters in JSON format
     * @return true if parameters are valid, false if not.
     * @throws Exception
     */
    @Override
    public boolean validate(JSONObject param, Signature signature) throws Exception {
        return true;  //To change body of implemented methods use File | Settings | File Templates.
    }


    /**
     * Get the command description.
     *
     * @return The description of the Script and command gogo.
     */
    @Override
    public String getDescription() {
        return "Expose an ipojo factory";
    }

    /**
     * Get the name of the  Script and command gogo.
     *
     * @return The command name.
     */
    @Override
    public String getName() {
        return "getFactory";
    }

    /**
     * Get signature by passing the number of arguments..
     *
     * @return
     */
    @Override
    public Signature getSignature(int arguments) {
        return empty;
    }

}
