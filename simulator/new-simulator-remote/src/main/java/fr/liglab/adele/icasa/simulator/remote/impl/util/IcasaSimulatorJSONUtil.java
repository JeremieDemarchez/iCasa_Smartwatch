package fr.liglab.adele.icasa.simulator.remote.impl.util;

import org.json.JSONException;
import org.json.JSONObject;

import fr.liglab.adele.icasa.clock.util.DateTextUtil;
import fr.liglab.adele.icasa.location.Position;
import fr.liglab.adele.icasa.remote.util.IcasaJSONUtil;
import fr.liglab.adele.icasa.simulator.Person;
import fr.liglab.adele.icasa.simulator.remote.impl.PersonJSON;
import fr.liglab.adele.icasa.simulator.remote.impl.ScriptJSON;
import fr.liglab.adele.icasa.simulator.script.executor.ScriptExecutor;

public class IcasaSimulatorJSONUtil extends IcasaJSONUtil{

	public static JSONObject getPersonJSON(Person person) {
		JSONObject personJSON = null;
		try {
			personJSON = new JSONObject();
			personJSON.putOnce(PersonJSON.ID_PROP, person.getName());
			personJSON.putOnce(PersonJSON.NAME_PROP, person.getName());
			personJSON.putOnce(PersonJSON.TYPE_PROP, person.getPersonType());

			Position personPosition = person.getCenterAbsolutePosition();
			if (personPosition != null) {
				personJSON.put(PersonJSON.POSITION_X_PROP, personPosition.x);
				personJSON.put(PersonJSON.POSITION_Y_PROP, personPosition.y);
			}
			personJSON.putOnce(PersonJSON.LOCATION_PROP, person.getLocation());
		} catch (JSONException e) {
			e.printStackTrace();
			personJSON = null;
		}

		return personJSON;
	}

	public static JSONObject getPersonTypeJSON(String personTypeStr) {
		JSONObject personTypeJSON = null;
		try {
			personTypeJSON = new JSONObject();
			personTypeJSON.putOnce("id", personTypeStr);
			personTypeJSON.putOnce("name", personTypeStr);
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
