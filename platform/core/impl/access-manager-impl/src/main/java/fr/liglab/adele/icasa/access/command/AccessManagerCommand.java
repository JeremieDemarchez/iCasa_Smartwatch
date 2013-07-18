/*
 * Copyright 2011-2012 Universite Joseph Fourier, LIG, ADELE Research
 * Group Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package fr.liglab.adele.icasa.access.command;

import fr.liglab.adele.icasa.commands.AbstractCommand;
import fr.liglab.adele.icasa.commands.Signature;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.PrintStream;

/**
 * User: garciai@imag.fr
 * Date: 7/17/13
 * Time: 5:54 PM
 */
public class AccessManagerCommand extends AbstractCommand {
    @Override
    public Object execute(InputStream in, PrintStream out, JSONObject param, Signature signature) throws Exception {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * Get the name of the Script and command gogo.
     *
     * @return The command name.
     */
    @Override
    public String getName() {
        return "";
    }
}
