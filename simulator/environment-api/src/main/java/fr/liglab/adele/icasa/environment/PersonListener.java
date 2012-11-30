package fr.liglab.adele.icasa.environment;

/**
 * Created with IntelliJ IDEA.
 * User: thomas
 * Date: 30/11/12
 * Time: 15:20
 * To change this template use File | Settings | File Templates.
 */
public interface PersonListener {

    public void personAdded(Person person);

    public void personRemoved(Person person);

    public void personMoved(Person person, Position oldPosition);

    public void personDeviceAttached(Person person, LocatedDevice device);

    public void personDeviceDetached(Person person, LocatedDevice device);
}
