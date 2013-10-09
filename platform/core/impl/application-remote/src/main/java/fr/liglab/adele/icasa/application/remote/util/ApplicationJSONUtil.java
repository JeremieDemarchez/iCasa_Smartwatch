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
package fr.liglab.adele.icasa.application.remote.util;

import org.json.JSONException;
import org.json.JSONObject;

import fr.liglab.adele.icasa.application.Application;

public class ApplicationJSONUtil {

	
	public static JSONObject getApplicationJSON(Application app) {
		JSONObject appJSON = null;
		try {
			String appId = app.getId();
			appJSON = new JSONObject();
			appJSON.putOnce("id", appId);
			appJSON.putOnce("name", app.getName());
			appJSON.putOnce("version", app.getVersion());
		} catch (JSONException e) {
			e.printStackTrace();
			appJSON = null;
		}

		return appJSON;
	}
	
}
