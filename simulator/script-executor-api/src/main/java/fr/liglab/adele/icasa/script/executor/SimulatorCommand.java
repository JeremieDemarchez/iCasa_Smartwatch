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
package fr.liglab.adele.icasa.script.executor;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import org.json.JSONObject;

/**
 * An ICommandService is a service that can be executed with a set of arguments.
 * It can execute a block of code and then returns the result object of
 * executing this code.
 */
public interface SimulatorCommand {

	
	
	/**
	 * @serviceproperty Namespace is the command namespace (same as xml
	 *                  namespace or gogo shell scope)
	 * @mandatory
	 */
	public static final String PROP_NAMESPACE = "namespace";
	
	/**
	 * Default value for PROP_COMMAND_NAMESPACE
	 */
	public static final String DEFAULT_NAMESPACE = "simulator" ;
	

	/**
	 * @serviceproperty The name of the command
	 * @mandatory
	 */
	public static final String PROP_NAME = "name";

	/**
	 * @serviceproperty The description of the command
	 * @optional
	 */
	public static final String PROP_DESCRIPTION = "description";

	/**
	 * Execute a block of code and then returns the result from execution. This
	 * method should use {@code in} and {@code out} stream to print result instead of the direct
	 * use of {@code System.in} and {@code System.out}
	 * 
	 * @param in
	 *            : the input steam.
	 * @param out
	 *            : the output stream.
	 * @param param
	 *            a json object of parameters
	 * 
	 * @return the result from the execution
	 * @throws Exception
	 *             if anything goes wrong
	 */
	Object execute(InputStream in, OutputStream out, JSONObject param) throws Exception;

}