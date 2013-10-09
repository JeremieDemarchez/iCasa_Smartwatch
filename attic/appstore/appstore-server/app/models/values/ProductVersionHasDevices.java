package models.values;

import play.db.ebean.Model;

import javax.persistence.*;

/**
 * User: torito
 * Date: 5/3/13
 * Time: 11:05 AM
 */
@Entity
@Table(name="ProductVersion_has_Device")
public class ProductVersionHasDevices extends Model {

    @OneToOne
    @JoinColumn(name = "productVersion_id")
    public ProductVersion productVersion;

    @OneToOne
    @JoinColumn(name = "device_id")
    public Device device;

}