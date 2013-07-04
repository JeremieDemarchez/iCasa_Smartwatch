/**
 *
 *   Copyright 2011-2012 Universite Joseph Fourier, LIG, ADELE Research
 *   Group Licensed under the Apache License, Version 2.0 (the "License");
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
package fr.liglab.adele.icasa;

import java.util.Arrays;

/**
 * A Signature is a description of the parameters used in iCasa.
 */
public class Signature {



    private final String methodName;

    private static final String DEFAULT_METHOD ="execute";
	/**
	 * The list of parameters for a given iCasaCommand signature.
	 */
	private final String[] parameters;

	public Signature(String[] params) {
		this(DEFAULT_METHOD, params);
	}

    public Signature(String name, String[] params) {
        this.parameters = params;
        this.methodName = name;
    }


	/**
	 * Get the list of parameters of this signature.
	 * 
	 * @return the list of parameters.
	 */
	public String[] getParameters() {
		return parameters;
	}

	/**
	 * Determines if the signature contains a parameter
	 * @param parameter a parameter name
	 * @return true if contains the parameter
	 */
	public boolean hasParameter(String parameter) {
		for (String actualParameter : parameters) {
			if (actualParameter.equals(parameter))
				return true;
		}
		return false;
	}

    public String getMethodName() {
        return methodName;
    }

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		Signature signature = (Signature) o;

		if (!Arrays.equals(parameters, signature.parameters))
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = Arrays.hashCode(parameters);
		return result;
	}
}
