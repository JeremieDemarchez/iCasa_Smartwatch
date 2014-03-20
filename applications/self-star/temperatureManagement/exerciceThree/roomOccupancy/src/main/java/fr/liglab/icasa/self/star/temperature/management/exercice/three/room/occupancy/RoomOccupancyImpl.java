package fr.liglab.icasa.self.star.temperature.management.exercice.three.room.occupancy;

import fr.liglab.adele.icasa.clock.Clock;
import fr.liglab.adele.icasa.service.scheduler.PeriodicRunnable;
import org.apache.felix.ipojo.annotations.*;

import java.util.Map;

/**
 * Created by aygalinc on 20/03/14.
 */
@Component(name="RoomOccupancyImpl")
@Instantiate(name="RoomOccupancyImpl-0")
@Provides
public class RoomOccupancyImpl implements RoomOccupancy,PeriodicRunnable {

    Map<String,Map<Integer,Double>> mapProbaperRoomPerMinute ;


    @Requires
    private Clock clock;

    /** Component Lifecycle Method */
    @Invalidate
    public void stop() {
        System.out.println("Component is stopping...");


    }

    /** Component Lifecycle Method */
    @Validate
    public void start() {
        System.out.println("Component is starting...");
    }

    @Override
    public double getRoomOccupancy(double minuteOfTheDay, String room) {
        return 0;
    }

    @Override
    public long getPeriod() {
        return 60000;
    }

    @Override
    public String getGroup() {
        return "Room-Occupancy-Thread";
    }

    @Override
    public void run() {

    }


}
