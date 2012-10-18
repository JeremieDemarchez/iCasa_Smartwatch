package models;

import play.db.ebean.Model;
import javax.persistence.*;

/**
 * @author Thomas Leveque
 */
@Entity
public class Device extends Model {

    @Id
    public Long id;
    public String name;

    public static Finder<Long, Device> find =
            new Finder<Long, Device>(Long.class, Device.class);
}
