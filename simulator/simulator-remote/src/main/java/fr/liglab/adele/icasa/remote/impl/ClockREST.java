package fr.liglab.adele.icasa.remote.impl;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.json.JSONException;
import org.json.JSONObject;

import fr.liglab.adele.icasa.clock.api.Clock;

/**
 * 
 * @author Gabriel Pedraza Ferreira
 * 
 */
@Component(name = "remote-rest-clock")
@Instantiate(name = "remote-rest-clock-0")
@Provides(specifications = { ClockREST.class })
@Path(value = "/clock/")
public class ClockREST extends AbstractREST {

	@Requires
	private Clock clock;
	

   @GET
   @Produces(MediaType.APPLICATION_JSON)
   public Response clock() {
       return makeCORS(Response.ok(getClockJSON()));
   }
   
   @OPTIONS
   @Produces(MediaType.APPLICATION_JSON)
   public Response clockOptions() {
       return makeCORS(Response.ok());
   }
   
   @PUT
   @Produces(MediaType.APPLICATION_JSON)
   @Consumes(MediaType.APPLICATION_JSON)
   public Response updateClock(String content) {
   	try {
	      JSONObject clockObject = new JSONObject(content);
	      int factor = clockObject.getInt("factor");
	      boolean pause = clockObject.getBoolean("pause");
	      long startDate = clockObject.getLong("startDate");
	      
	      synchronized (clock) {
	      	if (clock.getStartDate()!=startDate)
	      		clock.setStartDate(startDate);
	      	
	      	if (clock.getFactor() != factor)
	      		clock.setFactor(factor);
	      	
	      	if (pause) {
	      		if (!clock.isPaused()) {
	      			clock.pause();
	      		}	      				      		
	      	} else {
	      		if (clock.isPaused())
	      			clock.resume();
	      	}	 
	      	
         }
	      	      
      } catch (JSONException e) {
	      e.printStackTrace();
      }
   	return makeCORS(Response.ok(getClockJSON()));
   }
   
   @OPTIONS
   @Produces(MediaType.APPLICATION_JSON)
   @Consumes(MediaType.APPLICATION_JSON)
   public Response updateClockOptions(String content) {
   	return makeCORS(Response.ok());
   }
	
	private String getClockJSON() {
		JSONObject scriptJSON = null;
		try {
			scriptJSON = new JSONObject();
			scriptJSON.putOnce("startDateStr", getDate(clock.getStartDate()));
			scriptJSON.putOnce("startDate", clock.getStartDate());
			scriptJSON.putOnce("currentDateStr", getDate(clock.currentTimeMillis())); 
			scriptJSON.putOnce("currentTime", clock.currentTimeMillis());
			scriptJSON.putOnce("factor", clock.getFactor());			
			scriptJSON.putOnce("pause", clock.isPaused());
			
		} catch (JSONException e) {
			e.printStackTrace();
			scriptJSON = null;
		}

		return scriptJSON.toString();
	}

	private String getDate(long timeInMs) {
		return getDate((new Date(timeInMs)));
	}
	
	private String getDate(Date date) {
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy - HH:mm:ss");
		return format.format(date);
	}
	
}
