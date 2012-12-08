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

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import fr.liglab.adele.icasa.environment.SimulationManager;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import fr.liglab.adele.icasa.environment.Person;
import fr.liglab.adele.icasa.environment.Position;

/**
 * @author Thomas Leveque
 *
 */
@Component(name="remote-rest-person")
@Instantiate(name="remote-rest-person-0")
@Provides(specifications={PersonREST.class})
@Path(value="/persons/")
public class PersonREST {

    @Requires
    private SimulationManager _simulationMgr;

    /*
     * Methods to manage cross domain requests
     */
    private String _corsHeaders;

    private Response makeCORS(Response.ResponseBuilder req, String returnMethod) {
        Response.ResponseBuilder rb = req
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS")
                .header("Access-Control-Expose-Headers", "X-Cache-Date, X-Atmosphere-tracking-id")
                .header("Access-Control-Allow-Headers","Origin, Content-Type, X-Atmosphere-Framework, X-Cache-Date, X-Atmosphere-Tracking-id, X-Atmosphere-Transport")
                .header("Access-Control-Max-Age", "-1")
                .header("Pragma", "no-cache");

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
    public String getPersons() {
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
            personJSON.putOnce(PersonJSON.ID_PROP, person.getName());
            personJSON.putOnce(PersonJSON.NAME_PROP, person.getName());

            Position personPosition = person.getAbsolutePosition();
            if (personPosition != null) {
                personJSON.put(PersonJSON.POSITION_X_PROP, personPosition.x);
                personJSON.put(PersonJSON.POSITION_Y_PROP, personPosition.y);
            }
            personJSON.putOnce(PersonJSON.LOCATION_PROP, person.getLocation());
        } catch (JSONException e) {
            e.printStackTrace();
            personJSON = null;
        }

        return personJSON;
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

        // Create the person
        _simulationMgr.addPerson(personJSON.getName());
        _simulationMgr.setPersonPosition(personJSON.getName(), new Position(personJSON.getPositionX(), personJSON.getPositionY()));

        Person newPerson = findPerson(personJSON.getName());
        if (newPerson == null)
            return makeCORS(Response.status(Response.Status.INTERNAL_SERVER_ERROR));

        JSONObject newPersonJSON = getPersonJSON(newPerson);

        return makeCORS(Response.ok(newPersonJSON.toString())); //TODO check that newPerson must be included in the response body
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

        Person newPerson = null;

        PersonJSON personJSON = PersonJSON.fromString(content);

        Person foundPerson = findPerson(personId);
        if (foundPerson == null)
            return Response.status(404).build();

        if ((personJSON.getPositionX() != null) || (personJSON.getPositionY() != null)) {
            Position personPosition = foundPerson.getAbsolutePosition();
            if (personJSON.getPositionX() == null)
                personJSON.setPositionX(personPosition.x);
            if (personJSON.getPositionY() == null)
                personJSON.setPositionY(personPosition.y);
            _simulationMgr.setPersonPosition(personJSON.getName(), new Position(personJSON.getPositionX(), personJSON.getPositionY()));
        } else if (personJSON.getLocation() != null) {
            _simulationMgr.setPersonZone(personId, personJSON.getLocation());
        }

        if (newPerson == null)
            return makeCORS(Response.status(Response.Status.INTERNAL_SERVER_ERROR));

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
