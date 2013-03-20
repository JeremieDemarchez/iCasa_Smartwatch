/**
 *
 *   Copyright 2011-2012 Universite Joseph Fourier, LIG, ADELE team
 *   Licensed under a specific end user license agreement;
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *     http://adeleresearchgroup.github.com/iCasa-Simulator/snapshot/license.html
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package fr.liglab.adele.icasa.simulator.remote.impl;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.StaticServiceProperty;
import org.json.JSONArray;
import org.json.JSONObject;

import fr.liglab.adele.icasa.location.Position;
import fr.liglab.adele.icasa.remote.AbstractREST;
import fr.liglab.adele.icasa.simulator.Person;
import fr.liglab.adele.icasa.simulator.SimulationManager;
import fr.liglab.adele.icasa.simulator.remote.util.IcasaSimulatorJSONUtil;
import fr.liglab.adele.icasa.simulator.remote.util.PersonJSON;

/**
 * @author Thomas Leveque
 *
 */
@Component(name="remote-rest-person")
@Instantiate(name="remote-rest-person-0")
@Provides(specifications={PersonREST.class}, properties = {@StaticServiceProperty(name = AbstractREST.ICASA_REST_PROPERTY_NAME, value="true", type="java.lang.Boolean")} )
@Path(value="/persons/")
public class PersonREST extends AbstractREST {

    @Requires
    private SimulationManager _simulationMgr;



    /**
     * Returns a JSON array containing all persons.
     *
     * @return a JSON array containing all persons.
     */
    public String getPersons() {
        // boolean atLeastOne = false;
        JSONArray currentPersons = new JSONArray();
        for (Person person : _simulationMgr.getPersons()) {
            JSONObject personJSON = IcasaSimulatorJSONUtil.getPersonJSON(person);
            if (personJSON == null)
                continue;

            currentPersons.put(personJSON);
        }

        return currentPersons.toString();
    }


    /**
     * Returns a JSON array containing all person types.
     *
     * @return a JSON array containing all person types.
     */
    public String getPersonTypes() {
        //boolean atLeastOne = false;
        JSONArray currentPersonTypes = new JSONArray();
        for (String personTypeStr : _simulationMgr.getPersonTypes()) {
            JSONObject personType = IcasaSimulatorJSONUtil.getPersonTypeJSON(personTypeStr);
            if (personType == null)
                continue;

            currentPersonTypes.put(personType);
        }

        return currentPersonTypes.toString();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path(value="/personTypes/")
    public Response personTypes() {
        return makeCORS(Response.ok(getPersonTypes()));
    }

    @OPTIONS
    @Produces(MediaType.APPLICATION_JSON)
    @Path(value="/personTypes/")
    public Response getPersonTypesOptions() {
        return makeCORS(Response.ok());
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path(value="/persons/")
    public Response persons() {
        return makeCORS(Response.ok(getPersons()));
    }

    @OPTIONS
    @Produces(MediaType.APPLICATION_JSON)
    @Path(value="/persons/")
    public Response getPersonsOptions() {
        return makeCORS(Response.ok());
    }

    @OPTIONS
    @Produces(MediaType.APPLICATION_JSON)
    @Path(value="/person/{personId}")
    public Response updatesPersonOptions(@PathParam("personId") String personId) {
        return makeCORS(Response.ok());
    }

    @OPTIONS
    @Produces(MediaType.APPLICATION_JSON)
    @Path(value="/person/")
    public Response createsPersonOptions() {
        return makeCORS(Response.ok());
    }

    /**
     * Retrieve a device.
     *
     * @param deviceId The ID of the device to retrieve
     * @return The required device,
     * return <code>null<code> if the device does not exist.
     * @throws java.text.ParseException
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path(value="/person/{personId}")
    public Response person(@PathParam("personId") String deviceId) {
        if (deviceId == null || deviceId.length()<1){
            return makeCORS(Response.ok(getPersons()));
        }

        Person foundPerson = findPerson(deviceId);
        if (foundPerson == null) {
            return makeCORS(Response.status(404));
        } else {
            JSONObject foundPersonJSON = IcasaSimulatorJSONUtil.getPersonJSON(foundPerson);

            return makeCORS(Response.ok(foundPersonJSON.toString()));
        }
    }

    private Person findPerson(String personId) {
        Person foundPerson = null;
        for (Person person : _simulationMgr.getPersons()) {
            if (person.getName().equals(personId)) {
                foundPerson = person;
                break;
            }
        }
        return foundPerson;
    }

    /**
     * Create a new person.
     *
     * @param content JSON representation of person to create
     *
     * @return
     */
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path(value="/person/")
    public Response createPerson(String content) {

        PersonJSON personJSON = PersonJSON.fromString(content);

        _simulationMgr.addPerson(personJSON.getName(), personJSON.getType());
        _simulationMgr.setPersonPosition(personJSON.getName(), new Position(personJSON.getPositionX(), personJSON.getPositionY()));

        Person newPerson = findPerson(personJSON.getName());
        if (newPerson == null)
            return makeCORS(Response.status(Response.Status.INTERNAL_SERVER_ERROR));

        JSONObject newPersonJSON = IcasaSimulatorJSONUtil.getPersonJSON(newPerson);

        return makeCORS(Response.ok(newPersonJSON.toString()));
    }

    /**
     * Update an existing person.
     *
     * @param personId
     * @param content JSON representation of person to create
     *
     * @return
     */
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path(value="/person/{personId}")
    public Response updatesPerson(@PathParam("personId") String personId, String content) {

        PersonJSON personJSON = PersonJSON.fromString(content);

        Person foundPerson = findPerson(personId);
        if (foundPerson == null)
      	  return makeCORS(Response.status(Response.Status.NOT_FOUND));

        if ((personJSON.getPositionX() != null) || (personJSON.getPositionY() != null)) {
            Position personPosition = foundPerson.getCenterAbsolutePosition();
            if (personJSON.getPositionX() == null)
                personJSON.setPositionX(personPosition.x);
            if (personJSON.getPositionY() == null)
                personJSON.setPositionY(personPosition.y);
            _simulationMgr.setPersonPosition(personJSON.getName(), new Position(personJSON.getPositionX(), personJSON.getPositionY()));
        } else if (personJSON.getLocation() != null) {
            _simulationMgr.setPersonZone(personId, personJSON.getLocation());
        }

        
        JSONObject newPersonJSON = IcasaSimulatorJSONUtil.getPersonJSON(foundPerson);

        return makeCORS(Response.ok(newPersonJSON.toString()));
    }

    /**
     * Delete specified person.
     *
     * @param personId person identifier
     * @return ok if person is successful deleted, 404 response if it does not exist.
     */
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path(value="/person/{personId}")
    public Response deletePerson(@PathParam("personId") String personId) {

        Person foundPerson = findPerson(personId);
        if (foundPerson == null)
            return Response.status(404).build();

        try {
            _simulationMgr.removePerson(foundPerson.getName());
        } catch (Exception e) {
            e.printStackTrace();
            return makeCORS(Response.status(Response.Status.INTERNAL_SERVER_ERROR));
        }

        return makeCORS(Response.ok());
    }
}
