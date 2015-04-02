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
package fr.liglab.adele.icasa.simulator.script.executor.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import fr.liglab.adele.icasa.Constants;
import fr.liglab.adele.icasa.clockservice.util.DateTextUtil;

public class ScriptSAXHandler extends DefaultHandler {

    protected static Logger logger = LoggerFactory.getLogger(Constants.ICASA_LOG);
    
	List<ActionDescription> list = new ArrayList<ActionDescription>();
	
	private int delay;

	private int factor = 1;
	
	private String startdateStr;

    protected boolean useClockDateToStart = true;

    @Override
	public void startDocument() throws SAXException {
		delay = 0;
	}

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        String factorStr = null;
        if (qName.equals("behavior")) {
            if (attributes.getValue("startdate") != null) {
                startdateStr = attributes.getValue("startdate");
                useClockDateToStart = false;
            } else {
                useClockDateToStart = true;
                startdateStr = DateTextUtil.getTextDate(new Date());
            }
            if (attributes.getValue("factor") != null) {
                factorStr = attributes.getValue("factor");
                try {
                    factor = Integer.parseInt(factorStr);
                } catch (Exception e) {
                    factor = 1;
                }
            }

        } else if (qName.equals("delay")) {
            try {
                long delta = 0;
                
                long tempDelay = Long.valueOf(attributes.getValue("value"));
                String unit = attributes.getValue("unit");
                
                if (unit!=null) {
                    if (unit.equals("h")) {
                        delta = tempDelay * 60 * 60 * 1000; // (h * 60mn * 60s * 1000ms) convert into milliseconds
                    } else if (unit.equals("m")) {
                        delta = tempDelay * 60 * 1000; //(m * 60s * 1000ms) convert into milliseconds
                    } else if (unit.equals("s")) {
                        delta = tempDelay * 1000; // (s * 1000ms) convert into milliseconds
                    }                    
                } else { // default unit is minutes
                    //logger.warn("value of unit attribute not provided. Default minutes will be used");
                    delta = tempDelay * 60 * 1000; //(m * 60s * 1000ms) convert into milliseconds
                }
                delay += delta;
            } catch (Exception ex) {
                logger.error("value of delay attribute must be an integer");
            }
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

	public Long getStartDate() {
		Date date = DateTextUtil.getDateFromText(startdateStr);
		if (date!=null)
			return date.getTime();
		return System.currentTimeMillis();
			
	}
	
	public int getFactor() {
	   return factor;
   }
	
	public int getExecutionTime() {
		return delay ;
	}

}
