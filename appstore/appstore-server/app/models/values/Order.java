package models.values;

import models.User;
import play.db.ebean.Model;

import javax.persistence.*;

/**
 * Created with IntelliJ IDEA.
 * User: torito
 * Date: 1/15/13
 * Time: 4:43 PM
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name="Orders")
public class Order extends Model {
    @Id
    @Column(name="id")
    public int id;

    @ManyToOne
    @JoinColumn(name="user_id", referencedColumnName = "id")
    public User user;

    @ManyToOne
    @JoinColumn(name="product_Price_id", referencedColumnName = "id")
    public ProductPrice price;
}
