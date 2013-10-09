package models.values;

import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;
import play.data.validation.Constraints;
import play.db.ebean.Model;
import play.libs.Json;

import javax.annotation.Generated;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * User: torito
 * Date: 4/30/13
 * Time: 7:43 PM
 */
@Entity
@Table(name="Product_has_Category")
public class ProductHasCategory extends Model {
    @Id
    @GeneratedValue
    @Column(name="id")
    public int id;

    @OneToOne
    @JoinTable(name = "Product")
    public Product product;

    @OneToOne
    @JoinTable(name = "Category")
    public Category category ;



}
