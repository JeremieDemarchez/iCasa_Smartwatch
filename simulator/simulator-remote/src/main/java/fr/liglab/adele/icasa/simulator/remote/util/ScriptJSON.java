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
package fr.liglab.adele.icasa.simulator.remote.util;

import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import fr.liglab.adele.icasa.clock.util.DateTextUtil;

public class ScriptJSON {

	public static final String START_DATE_PROP = "startDate";
	public static final String STATE_PROP = "state";
	public static final String FACTOR_PROP = "factor";
	public static final String COMPLETE_PERCENT_PROP = "completePercent";
	public static final String NAME_PROP = "name";
	public static final String ID_PROP = "id";
	public static final String SCRIPT_ID_PROP = "scriptId";
	public static final String ACTION_NUMBER_PROP = "actionNumber";
	public static final String EXECUTION_TIME_PROP = "executionTime";
	

	private String name;
	private String id;
	private String state;
	private Integer completePercent;
	private Integer factor;
	private Date startDate;



	public static ScriptJSON fromString(String jsonStr) {
		ScriptJSON script = null;
		JSONObject json = null;
		try {
			json = new JSONObject(jsonStr);
			script = new ScriptJSON();

			if (json.has(ID_PROP))
				script.setId(json.getString(ID_PROP));
			else if (json.has(SCRIPT_ID_PROP))
				script.setId(json.getString(SCRIPT_ID_PROP));
			if (json.has(NAME_PROP))
				script.setName(json.getString(NAME_PROP));
			if (json.has(COMPLETE_PERCENT_PROP))
				script.setCompletePercent(json.getInt(COMPLETE_PERCENT_PROP));
			if (json.has(FACTOR_PROP))
				script.setFactor(json.getInt(FACTOR_PROP));
			if (json.has(STATE_PROP))
				script.setState(json.getString(STATE_PROP));
			if (json.has(START_DATE_PROP))
				script.setStartDate(json.getString(START_DATE_PROP));
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return script;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public Integer getCompletePercent() {
		return completePercent;
	}

	public void setCompletePercent(Integer completePercent) {
		this.completePercent = completePercent;
	}

	public Integer getFactor() {
		return factor;
	}

	public void setFactor(Integer factor) {
		this.factor = factor;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public void setStartDate(String startDateStr) {
		this.startDate = DateTextUtil.getDateFromText(startDateStr);
	}

}
