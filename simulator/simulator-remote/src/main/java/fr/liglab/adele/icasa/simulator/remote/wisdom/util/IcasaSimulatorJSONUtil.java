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
package fr.liglab.adele.icasa.simulator.remote.wisdom.util;

import fr.liglab.adele.icasa.remote.wisdom.util.IcasaJSONUtil;
import fr.liglab.adele.icasa.simulator.person.PersonType;
import org.json.JSONException;
import org.json.JSONObject;

import fr.liglab.adele.icasa.clockservice.util.DateTextUtil;
import fr.liglab.adele.icasa.location.Position;
import fr.liglab.adele.icasa.simulator.person.Person;
import fr.liglab.adele.icasa.simulator.script.executor.ScriptExecutor;

public class IcasaSimulatorJSONUtil extends IcasaJSONUtil {

	public static JSONObject getPersonJSON(Person person) {
		JSONObject personJSON = null;
		try {
			personJSON = new JSONObject();
			personJSON.putOnce(PersonJSON.ID_PROP, person.getName());
			personJSON.putOnce(PersonJSON.NAME_PROP, person.getName());
			personJSON.putOnce(PersonJSON.TYPE_PROP, person.getPersonType().getName());

			Position personPosition = person.getPosition();
			if (personPosition != null) {
				personJSON.put(PersonJSON.POSITION_X_PROP, personPosition.x);
				personJSON.put(PersonJSON.POSITION_Y_PROP, personPosition.y);
			}
			personJSON.putOnce(PersonJSON.LOCATION_PROP, person.getZone());
		} catch (JSONException e) {
			e.printStackTrace();
			personJSON = null;
		}

		return personJSON;
	}

	public static JSONObject getPersonTypeJSON(PersonType personTypeStr) {
		JSONObject personTypeJSON = null;
		try {
			personTypeJSON = new JSONObject();
			personTypeJSON.putOnce("id", personTypeStr.getName());
			personTypeJSON.putOnce("name", personTypeStr.getName());
		} catch (JSONException e) {
			e.printStackTrace();
			personTypeJSON = null;
		}

		return personTypeJSON;
	}
	

	
	
	public static JSONObject getScriptJSON(String scriptName, ScriptExecutor _scriptExecutor) {
		JSONObject scriptJSON = null;
		try {
			scriptJSON = new JSONObject();
			scriptJSON.putOnce(ScriptJSON.ID_PROP, scriptName);
			scriptJSON.putOnce(ScriptJSON.NAME_PROP, scriptName);
			scriptJSON.putOnce(ScriptJSON.STATE_PROP, _scriptExecutor.getState(scriptName));
			scriptJSON.putOnce(ScriptJSON.COMPLETE_PERCENT_PROP, _scriptExecutor.getExecutedPercentage());
			scriptJSON.putOnce(ScriptJSON.ACTION_NUMBER_PROP, _scriptExecutor.getActionsNumber(scriptName));
			scriptJSON.putOnce(ScriptJSON.START_DATE_PROP, DateTextUtil.getTextDate(_scriptExecutor.getStartDate(scriptName)));
			scriptJSON.putOnce(ScriptJSON.FACTOR_PROP, _scriptExecutor.getFactor(scriptName));
			scriptJSON.putOnce(ScriptJSON.EXECUTION_TIME_PROP, _scriptExecutor.getExecutionTime(scriptName));
		} catch (JSONException e) {
			e.printStackTrace();
			scriptJSON = null;
		}
		return scriptJSON;
	}
	


	
	
	
	
	
}
