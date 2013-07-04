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
package fr.liglab.adele.icasa.service.preferences.impl.commands;

import java.io.InputStream;
import java.io.PrintStream;

import org.json.JSONObject;

import fr.liglab.adele.icasa.Signature;
import fr.liglab.adele.icasa.commands.impl.AbstractCommand;
import fr.liglab.adele.icasa.commands.impl.ScriptLanguage;
import fr.liglab.adele.icasa.service.preferences.Preferences;

public abstract class AbstractSetPropertyCommand extends AbstractCommand {

	private Preferences preferenceService;
	
	public static final String GLOBAL_PREFERENCE = "Global";
	public static final String USER_PREFERENCE = "User";
	public static final String APPLICATION_PREFERENCE = "Application";

	@Override
	public Object execute(InputStream in, PrintStream out, JSONObject param, Signature signature) throws Exception {

		preferenceService = getPreferenceService();

		String property = param.getString(ScriptLanguage.NAME);
		
		String type = "String";
		if (signature.hasParameter(ScriptLanguage.TYPE))		
			type = param.getString(ScriptLanguage.TYPE);
		String commandType = getPreferencesType();		
		Object value = null;
		
		if (type.equals("String"))
			value = param.getString(ScriptLanguage.VALUE);
		else if (type.equals("Boolean"))
			value = param.getBoolean(ScriptLanguage.VALUE);
		else if (type.equals("Float"))
			value = (float) param.getDouble(ScriptLanguage.VALUE);
		else if (type.equals("Integer"))
			value = param.getInt(ScriptLanguage.VALUE);
		else if (type.equals("Long"))
			value = param.getLong(ScriptLanguage.VALUE);
		else
			value = param.get(ScriptLanguage.VALUE);

		String extraParameterName = getExtraParameterName();
		String extraParameter = null;
		if (param.has(getExtraParameterName()))
			extraParameter = param.getString(extraParameterName);

		if (value != null) {
			if (commandType.equals(GLOBAL_PREFERENCE))
				preferenceService.setGlobalPropertyValue(property, value);
			else if (commandType.equals(USER_PREFERENCE))
				preferenceService.setUserPropertyValue(extraParameter, property, value);
			else if (commandType.equals(APPLICATION_PREFERENCE))
				preferenceService.setApplicationPropertyValue(extraParameter, property, value);
		}

		return null;
	}

	
	
	protected abstract String getPreferencesType();

	protected abstract String getExtraParameterName();

	protected abstract Preferences getPreferenceService();

}
