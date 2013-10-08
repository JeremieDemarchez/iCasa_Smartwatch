/**
 *
 *   Copyright 2011-2012 Universite Joseph Fourier, LIG, ADELE Research Group
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
package fr.imag.adele.medical.androidsimulator;

import java.util.HashMap;
import java.util.Map;
import java.util.prefs.Preferences;

import fr.imag.adele.medical.androidsimulator.R;
import android.app.Activity;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.URLUtil;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

public class SimulatorActivity extends Activity {

	WebView simulatorWebView;

	private int _serverPort = 8080;

	private String _serverIp = "192.168.1.3";
	
	private String _name = "Paul";
	
	private Map<String /* name */, ServerDescription> _servers = new HashMap<String, ServerDescription>();

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// features MUST be requested before adding content
		requestWindowFeature(Window.FEATURE_PROGRESS);
		setContentView(R.layout.main);
		
		// load persisted data
		SharedPreferences settings = getPreferences(0);
		_servers.clear();
		int serverNb = settings.getInt("serverNb", 0);
		for (int idx = 0; idx < serverNb; idx++) {
			ServerDescription serverDesc = loadServerDesc(idx, settings);
			_servers.put(serverDesc.getName(), serverDesc);
		}

		// configure web page renderer
		simulatorWebView = (WebView) findViewById(R.id.webview);
		WebSettings webSettings = simulatorWebView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		final Activity activity = this;
		activity.setProgressBarVisibility(true);
		simulatorWebView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress)
            {
                activity.setTitle("Loading...");
                activity.setProgress(progress * 100);
 
                if(progress == 100)
                    activity.setTitle(R.string.app_name);
            }
        });
 
		simulatorWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl)
            {
                // Handle the error
            }
 
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url)
            {
                view.loadUrl(url);
                return true;
            }
        });
		reloadWebView();
	}
	
	private ServerDescription loadServerDesc(int idx, SharedPreferences settings) {
		String prefix = getPropPrefix(idx);
		
		String name = settings.getString(prefix + "name", "Undefined");
		int serverPort = settings.getInt(prefix + "serverPort", 80);
		String serverIp = settings.getString(prefix + "serverIP", "localhost");
		
		return new ServerDescription(name, serverIp, serverPort);
	}

	@Override
	protected void onStop() {
		super.onStop();
		
		SharedPreferences settings = getPreferences(0);
	    SharedPreferences.Editor editor = settings.edit();
	    
		int serverNb = _servers.size();
		editor.putInt("serverNb", serverNb);
		int idx = 0;
		for (String name : _servers.keySet()) {
			saveServerDesc(idx, _servers.get(name), editor);
			idx++;
		}
		editor.commit();
	}

	private void saveServerDesc(int idx, ServerDescription desc, Editor editor) {
		String prefix = getPropPrefix(idx);
		
		editor.putString(prefix + "name", desc.getName());
		editor.putInt(prefix + "serverPort", desc.getPort());
		editor.putString(prefix + "serverIP", desc.getIp());
	}

	private String getHouseSimulatorURL(String ipAddress, int port) {
		return "http://" + ipAddress + ":" + port
				+ "/simulator?isAndroid=true";
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.options, menu);
		return true;
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		
		int serverNb = _servers.size();
		savedInstanceState.putInt("serverNb", serverNb);
		int idx = 0;
		for (String name : _servers.keySet()) {
			saveServerDesc(idx, _servers.get(name), savedInstanceState);
			idx++;
		}
		
		super.onSaveInstanceState(savedInstanceState);
	}

	private void saveServerDesc(int idx, ServerDescription desc, Bundle savedInstanceState) {
		String prefix = getPropPrefix(idx);
		
		savedInstanceState.putString(prefix + "name", desc.getName());
		savedInstanceState.putInt(prefix + "serverPort", desc.getPort());
		savedInstanceState.putString(prefix + "serverIP", desc.getIp());
	}

	private String getPropPrefix(int idx) {
		return "server" + idx + ".";
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);

		_servers.clear();
		int serverNb = savedInstanceState.getInt("serverNb");
		for (int idx = 0; idx < serverNb; idx++) {
			ServerDescription serverDesc = loadServerDesc(idx, savedInstanceState);
			_servers.put(serverDesc.getName(), serverDesc);
		}
		
		reloadWebView();
	}

	private void reloadWebView() {
		simulatorWebView.loadUrl(getHouseSimulatorURL(_serverIp, _serverPort));
	}

	private ServerDescription loadServerDesc(int idx, Bundle savedInstanceState) {
		String prefix = getPropPrefix(idx);
		
		String name = savedInstanceState.getString(prefix + "name");
		int serverPort = savedInstanceState.getInt(prefix + "serverPort");
		String serverIp = savedInstanceState.getString(prefix + "serverIP");
		
		return new ServerDescription(name, serverIp, serverPort);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.choose:
			final Dialog chooseDialog = new Dialog(this);
			chooseDialog.setContentView(R.layout.choosedialog);
			chooseDialog.setTitle("Select an user");

			final ListView userList = (ListView) chooseDialog
					.findViewById(R.id.listUsersView);

			String[] users = new String[_servers.size()]; 
			int i = 0;
			for (String user : _servers.keySet()) {
				users[i] = user;
				i++;
			}
			userList.setAdapter(new ArrayAdapter<String>(this,
					android.R.layout.simple_list_item_1, users));
			userList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

				public void onItemClick(AdapterView<?> arg0, View arg1,
						int position, long arg3) {
					
					String userStr = (String) userList.getItemAtPosition(position);
					ServerDescription serverDesc = _servers.get(userStr);
					chooseDialog.cancel();
					updateServer(serverDesc);
				}
				
			});
			
			chooseDialog.show();
			return true;
		case R.id.go_to:
			final Dialog dialog = new Dialog(this);
			dialog.setContentView(R.layout.urldialog);
			dialog.setTitle("Enter Medical server settings below");

			Button goButton = (Button) dialog.findViewById(R.id.ok);
			final EditText nameText = (EditText) dialog
					.findViewById(R.id.nameText);
			final EditText serverIPText = (EditText) dialog
					.findViewById(R.id.serverIPText);
			final EditText serverPortText = (EditText) dialog
					.findViewById(R.id.serverPortText);
			nameText.setText(_name);
			serverIPText.setText(_serverIp);
			serverPortText.setText(Integer.toString(_serverPort));

			goButton.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {

					String serverIp = serverIPText.getText().toString();
					String serverPort = serverPortText.getText().toString();
					if (isValidString(serverIp) && isValidPort(serverPort)) {
						int serverPortNb = getNumber(serverPort);
						String houseSimulatorURL = getHouseSimulatorURL(
								serverIp, serverPortNb);
						if (URLUtil.isValidUrl(houseSimulatorURL)) {
							dialog.cancel();
							
							String name = nameText.getText().toString();
							
							ServerDescription newServerDesc = new ServerDescription(name, serverIp, serverPortNb);
							addServer(newServerDesc);
							updateServer(newServerDesc);
						} else {
							dialog.setTitle("Invalid server IP or port syntax");
						}
					}
				}

				private boolean isValidPort(String serverPort) {
					if (!isValidString(serverPort))
						return false;

					int serverPortNb = getNumber(serverPort);

					return ((serverPortNb >= 0) || (serverPortNb <= 65536));
				}
			});

			Button cancelButton = (Button) dialog.findViewById(R.id.cancel);
			cancelButton.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					// canceling dialog box
					dialog.cancel();
				}
			});
			dialog.show();
			return true;
		case R.id.exit:
			// move activity to background
			this.moveTaskToBack(true);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	private void addServer(ServerDescription serverDesc) {
		_servers.put(serverDesc.getName(), serverDesc);
	}

	private void updateServer(ServerDescription serverDesc) {
		_name = serverDesc.getName();
		_serverIp = serverDesc.getIp();
		_serverPort = serverDesc.getPort();
		
		updateWebBrowserView();
	}

	private void updateWebBrowserView() {
		String houseSimulatorURL = getHouseSimulatorURL(
				_serverIp, _serverPort);
		simulatorWebView.loadUrl(houseSimulatorURL);
	}

	private int getNumber(String serverPort) {
		Integer serverPortNb;
		try {
			serverPortNb = Integer.valueOf(serverPort);
		} catch (NumberFormatException e) {
			return -1;
		}

		return serverPortNb;
	}

	private boolean isValidString(String text) {
		return (text != null) && (text.length() != 0);
	}

}