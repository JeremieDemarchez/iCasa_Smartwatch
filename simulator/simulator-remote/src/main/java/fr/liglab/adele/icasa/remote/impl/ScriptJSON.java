package fr.liglab.adele.icasa.remote.impl;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class ScriptJSON {

    public static final String START_DATE_PROP = "startDate";
    public static final String STATE_PROP = "state";
    public static final String FACTOR_PROP = "factor";
    public static final String COMPLETE_PERCENT_PROP = "completePercent";
    public static final String NAME_PROP = "name";
    public static final String ID_PROP = "id";

    private String name;
    private String id;
    private String state;
    private Integer completePercent;
    private Integer factor;
    private Date startDate;

    public ScriptJSON() {
        //do nothing
    }

    public static ScriptJSON fromString(String jsonStr) {
        ScriptJSON script = null;
        JSONObject json = null;
        try {
            json = new JSONObject(jsonStr);
            script = new ScriptJSON();
            script.setId(json.getString(ID_PROP));
            if (json.has(NAME_PROP))
                script.setName(json.getString(NAME_PROP));
            if (json.has(COMPLETE_PERCENT_PROP))
                script.setCompletePercent(json.getInt(COMPLETE_PERCENT_PROP));
            if (json.has(FACTOR_PROP))
                script.setFactor(json.getInt(FACTOR_PROP));
            if (json.has(STATE_PROP))
                script.setState(json.getString(STATE_PROP));
            if (json.has(START_DATE_PROP))
                script.setState(json.getString(START_DATE_PROP));
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
}
