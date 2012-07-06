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
package fr.imag.adele.device.dashboards.android;

public class ServerDescription {

	private String _name;
	private String _ip;
	private int _port;
	
	public ServerDescription(String name, String ip, int port) {
		_name = name;
		_ip = ip;
		_port = port;
	}

	public String getName() {
		return _name;
	}

	public void setName(String _name) {
		this._name = _name;
	}

	public String getIp() {
		return _ip;
	}

	public void setIp(String _ip) {
		this._ip = _ip;
	}

	public int getPort() {
		return _port;
	}

	public void setPort(int _port) {
		this._port = _port;
	}
}
