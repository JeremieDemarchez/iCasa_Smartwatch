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
package fr.liglab.adele.icasa.script.executor.impl;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


public class ScenarioSAXHandler extends DefaultHandler {

	ScriptExecutorImpl scriptExecutorImpl;
	List<ActionDescription> list = new ArrayList<ActionDescription>();
	private int delay;
	
	public ScenarioSAXHandler(ScriptExecutorImpl scriptExecutorImpl) {
		this.scriptExecutorImpl = scriptExecutorImpl;
	}
	
	@Override
	public void startDocument() throws SAXException {
		delay = 0;
	}
	
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {		
		if (!qName.equals("delay")) {
			list.add(new ActionDescription(delay, qName, createParameters(attributes)));
		} else {
			delay += Integer.valueOf(attributes.getValue("value"));
		}
	}
	
	
	private JSONObject createParameters(Attributes attributes) {		
		JSONObject param = new JSONObject();
		for (int i = 0; i < attributes.getLength(); i++) {
	      String name = attributes.getQName(i);
	      String value = attributes.getValue(i);
	      try {
	         param.put(name, value);
         } catch (JSONException e) {
	         e.printStackTrace();
         }
      }
		return param;
	}
	
	public List<ActionDescription> getActionList() {
		return list;
	}
	
}
