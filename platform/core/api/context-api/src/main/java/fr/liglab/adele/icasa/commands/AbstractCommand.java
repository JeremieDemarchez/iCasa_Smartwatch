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
package fr.liglab.adele.icasa.commands;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

public abstract class AbstractCommand implements ICasaCommand {

    protected static Signature EMPTY_SIGNATURE = new Signature(new String[0]);

    /**
     * Default namespace for iCasa commands
     */
    private static String NAMESPACE = "icasa";

    private List<Signature> signatureList = new ArrayList<Signature>(1);

	@Override
	public Object execute(InputStream in, PrintStream out, JSONObject param) throws Exception {
        Signature signature = getSignature(param.length());
		if (validate(param, signature)){
            return execute(in, out, param, signature);
        } else {
            out.println(getDescription());
            throw new Exception("Invalid parameters: " + param);
        }
	}

    protected void addSignature(Signature signature){
        for(Signature sign: signatureList){
            if(sign.getParameters().length == signature.getParameters().length){
                throw new InstantiationError("Unable to add two signature with the same number of parameters");
            }
        }
        signatureList.add(signature);

    }

    public Signature getSignature(int param){
        for (Signature signature: signatureList){
            if (signature.getParameters().length == param){
                return signature;
            }
        }
        return null;
    }

    /**
     * Get the command description.
     *
     * @return The description of the Script and command gogo.
     */
    @Override
    public String getDescription() {
        StringBuilder description = new StringBuilder("Parameters: \n");
        for(Signature sign: signatureList){
            String[] params = sign.getParameters();
            description.append("\t(");
            for (String param: params){
                description.append(" ");
                description.append(param);
                description.append(" ");
            }
            description.append(")\n");
        }
        return description.toString();
    }

    /**
     *
     * @param param The parameters in JSON format
     * @return true if all parameters are in the JSON object, false if not. For optional
     * parameters, this method should be override..
     * @throws Exception
     */
    @Override
	public boolean validate(JSONObject param, Signature signature) throws Exception {
   	 if (param==null || signature==null) {
   		 return false;
   	 }
        String[] params = signature.getParameters();
        for (String name: params){
            if(!param.has(name)){
                return false;
            }
        }
        return true;// All params are in the Json object.
    }
	
	public abstract Object execute(InputStream in, PrintStream out, JSONObject param, Signature signature) throws Exception;
}
