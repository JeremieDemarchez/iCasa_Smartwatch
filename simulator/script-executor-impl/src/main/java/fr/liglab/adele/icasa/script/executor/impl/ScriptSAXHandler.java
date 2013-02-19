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
import java.util.Date;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import fr.liglab.adele.icasa.clock.util.DateTextUtil;

public class ScriptSAXHandler extends DefaultHandler {

	
	List<ActionDescription> list = new ArrayList<ActionDescription>();
	
	private int delay;

	private int factor = 1;
	
	private String startdateStr;


	@Override
	public void startDocument() throws SAXException {
		delay = 0;
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if (qName.equals("behavior")) {
			startdateStr = attributes.getValue("startdate");
			String factorStr = attributes.getValue("factor");
			factor = Integer.parseInt(factorStr);
		} else if (qName.equals("delay")) {
			delay += Integer.valueOf(attributes.getValue("value"));
		} else {
			list.add(new ActionDescription(delay, qName, createParameters(attributes)));
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

	public long getStartDate() {
		/*
		if (startdateStr != null && (!startdateStr.isEmpty())) {
			SimpleDateFormat formatter = new SimpleDateFormat(ScriptExecutor.SIMULATOR_DATE_FORMAT);
			Date startDate = null;

			try {
				startDate = formatter.parse(startdateStr);
			} catch (ParseException e) {
				// e.printStackTrace();
			} finally {
				if (startDate == null)
					startDate = new Date(System.currentTimeMillis());
			}
			return startDate.getTime();
		} else
			return System.currentTimeMillis();
			
		*/
		
		
		System.out.println("Text Date ------------------------------------------> " + startdateStr);
		Date date = DateTextUtil.getDateFromText(startdateStr);
		System.out.println("Date ------------------------------------------> " + date);
		if (date!=null)
			return date.getTime();
		return System.currentTimeMillis();
			
	}
	
	public int getFactor() {
	   return factor;
   }
	
	public int getExecutionTime() {
		return delay;
	}

}
