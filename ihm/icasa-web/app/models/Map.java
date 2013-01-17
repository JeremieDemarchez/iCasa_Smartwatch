package models;

import play.db.ebean.Model;
import javax.persistence.Entity;

/**
 * @author Thomas Leveque
 */
@Entity
public class Map extends Model {

    public String imageURL;

    public String name;

    public String gatewayURL;
}
