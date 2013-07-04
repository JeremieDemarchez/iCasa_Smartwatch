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
/**
 * 
 */
package fr.liglab.adele.icasa.service.preferences.impl;

import java.util.Map;
import java.util.Set;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.ow2.chameleon.sharedprefs.SharedPreferences;
import org.ow2.chameleon.sharedprefs.SharedPreferencesService;

import fr.liglab.adele.icasa.service.preferences.Preferences;

/**
 * @author Gabriel Pedraza Ferreira
 * 
 */
@Component(name = "iCasaPreferences")
@Provides
@Instantiate
public class PreferencesImpl implements Preferences {

	private static final String GLOBAL_PREFIX = "Platform";

	private static final String USER_PREFIX = "User-";

	private static final String APP_PREFIX = "App-";

	@Requires
	private SharedPreferencesService preferenceService;

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.liglab.adele.icasa.service.preferences.Preferences#getGlobalPropertyValue(java.lang.String)
	 */
	@Override
	public Object getGlobalPropertyValue(String name) {
		return getPropertyValue(name, GLOBAL_PREFIX);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.liglab.adele.icasa.service.preferences.Preferences#getUserPropertyValue(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public Object getUserPropertyValue(String user, String name) {
		return getPropertyValue(name, USER_PREFIX + user);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.liglab.adele.icasa.service.preferences.Preferences#getApplicationPropertyValue(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public Object getApplicationPropertyValue(String applicationId, String name) {
		return getPropertyValue(name, APP_PREFIX + applicationId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.liglab.adele.icasa.service.preferences.Preferences#setGlobalPropertyValue(java.lang.String,
	 * java.lang.Object)
	 */
	@Override
	public void setGlobalPropertyValue(String name, Object value) {
		setPropertyValue(name, value, GLOBAL_PREFIX);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.liglab.adele.icasa.service.preferences.Preferences#setUserPropertyValue(java.lang.String,
	 * java.lang.String, java.lang.Object)
	 */
	@Override
	public void setUserPropertyValue(String user, String name, Object value) {
		setPropertyValue(name, value, USER_PREFIX + user);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.liglab.adele.icasa.service.preferences.Preferences#setApplicationPropertyValue(java.lang.String,
	 * java.lang.String, java.lang.Object)
	 */
	@Override
	public void setApplicationPropertyValue(String applicationId, String name, Object value) {
		setPropertyValue(name, value, APP_PREFIX + applicationId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.liglab.adele.icasa.service.preferences.Preferences#getGlobalProperties()
	 */
	@Override
	public Set<String> getGlobalProperties() {
		return getPropertiesNames(GLOBAL_PREFIX);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.liglab.adele.icasa.service.preferences.Preferences#getUserProperties(java.lang.String)
	 */
	@Override
	public Set<String> getUserProperties(String user) {
		return getPropertiesNames(USER_PREFIX + user);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.liglab.adele.icasa.service.preferences.Preferences#getApplicationProperties(java.lang.String)
	 */
	@Override
	public Set<String> getApplicationProperties(String applicationId) {
		return getPropertiesNames(APP_PREFIX + applicationId);
	}

	
	private Object getPropertyValue(String name, String storeName) {
		SharedPreferences preferences = preferenceService.getSharedPreferences(storeName);
		if (preferences.contains(name))
			return preferences.getAll().get(name);
		return null;
	}

	private Set<String> getPropertiesNames(String storeName) {
		SharedPreferences preferences = preferenceService.getSharedPreferences(storeName);
		Map<String, ?> preferencesMap = preferences.getAll();
		return preferencesMap.keySet();		
	}
	
	private void setPropertyValue(String name, Object value, String storeName) {
		SharedPreferences preferences = preferenceService.getSharedPreferences(storeName);
		SharedPreferences.Editor editor = preferences.edit();

		if (value instanceof String) {
			String newValue = (String) value;
			editor.putString(name, newValue);
			editor.commit();
		} else if (value instanceof Boolean) {
			Boolean newValue = (Boolean) value;
			editor.putBoolean(name, newValue);
			editor.commit();
		} else if (value instanceof Float) {
			Float newValue = (Float) value;
			editor.putFloat(name, newValue);
			editor.commit();
		} else if (value instanceof Integer) {
			Integer newValue = (Integer) value;
			editor.putInt(name, newValue);
			editor.commit();
		} else if (value instanceof Long) {
			Long newValue = (Long) value;
			editor.putLong(name, newValue);
			editor.commit();
		} 
	}

}
