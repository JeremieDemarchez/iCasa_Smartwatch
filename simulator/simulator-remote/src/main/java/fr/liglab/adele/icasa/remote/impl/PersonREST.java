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
package fr.liglab.adele.icasa.remote.impl;

import fr.liglab.adele.icasa.environment.Person;
import fr.liglab.adele.icasa.environment.SimulationManager;
import fr.liglab.adele.icasa.environment.SimulationManager.Position;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

/**
 * @author Thomas Leveque
 *
 */
@Component(name="remote-rest-person")
@Instantiate(name="remote-rest-person-0")
@Provides(specifications={PersonREST.class})
@Path(value="/")
public class PersonREST {

    @Requires
    SimulationManager _simulationMgr;

    /*
     * Methods to manage cross domain requests
     */
    private String _corsHeaders;

    private Response makeCORS(Response.ResponseBuilder req, String returnMethod) {
        Response.ResponseBuilder rb = req
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");

        if (!"".equals(returnMethod)) {
            rb.header("Access-Control-Allow-Headers", returnMethod);
        }

        return rb.build();
    }

    private Response makeCORS(Response.ResponseBuilder req) {
        return makeCORS(req, _corsHeaders);
    }

    /**
     * Returns a JSON array containing all persons.
     *
     * @return a JSON array containing all persons.
     */
    public String getPersonIds() {
        boolean atLeastOne = false;
        JSONArray currentPersons = new JSONArray();
        for (Person person : _simulationMgr.getPersons()) {
            JSONObject personJSON = getPersonJSON(person);
            if (personJSON == null)
                continue;

            currentPersons.put(personJSON);
        }

        return currentPersons.toString();
    }

    private JSONObject getPersonJSON(Person person) {
        JSONObject personJSON = null;
        try {
            personJSON = new JSONObject();
            personJSON.putOnce("id", person.getName());
            personJSON.putOnce("name", person.getName());
            personJSON.put("positionX", person.getPosition().x);
            personJSON.put("positionY", person.getPosition().y);
        } catch (JSONException e) {
            e.printStackTrace();
            personJSON = null;
        }

        return personJSON;
    }

    @GET
    @Produces("application/json")
    @Path(value="/persons/")
    public Response persons() {
        return makeCORS(Response.ok(getPersonIds()));
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
    @Produces("application/json")
    @Path(value="/person/{personId}")
    public Response device(@PathParam("personId") String deviceId) {
        if (deviceId == null || deviceId.length()<1){
            return makeCORS(Response.ok(getPersonIds()));
        }

        Person foundPerson = findPerson(deviceId);
        if (foundPerson == null) {
            return makeCORS(Response.status(404));
        } else {
            JSONObject foundPersonJSON = getPersonJSON(foundPerson);

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
     * @param personId
     * @param name
     * @param positionX
     * @param positionY
     *
     * @return
     */
    @POST
    @Produces("application/json")
    @Path(value="/person/{personId}")
    public Response createDevice(@PathParam("personId") String personId, @FormParam("name") String name,
                                 @FormParam("positionX") Integer positionX, @FormParam("positionY") Integer positionY) {

        Person newPerson = null;

        // Create the person
        _simulationMgr.addUser(name);
        _simulationMgr.setUserPosition(name, new Position(positionX, positionY));

        if (newPerson == null)
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();

        JSONObject newPersonJSON = getPersonJSON(newPerson);

        return makeCORS(Response.ok(newPersonJSON.toString())); //TODO check that newPerson must be included in the response body
    }

    /**
     * Delete specified person.
     *
     * @param personId person identifier
     * @return ok if person is successful deleted, 404 response if it does not exist.
     */
    @DELETE
    @Produces("application/json")
    @Path(value="/person/{personId}")
    public Response deleteDevice(@PathParam("personId") String personId) {

        Person foundPerson = findPerson(personId);
        if (foundPerson == null)
            return Response.status(404).build();

        try {
            _simulationMgr.removeUser(foundPerson.getName());
        } catch (Exception e) {
            e.printStackTrace();
            return makeCORS(Response.status(Response.Status.INTERNAL_SERVER_ERROR));
        }

        return makeCORS(Response.ok());
    }
}
