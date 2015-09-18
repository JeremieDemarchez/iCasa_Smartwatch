package fr.liglab.adele.icasa.context.model.example;

import fr.liglab.adele.icasa.context.model.ContextEntity;
import org.apache.felix.ipojo.annotations.*;

import java.util.List;

/**
 * Created by Gerbert on 18/09/2015.
 */
@Component
@Instantiate
public class TestFilter {
    @Requires(id = "context.entity.test", optional = true, filter = "(|(context.entity.state=kitchen)(context.entity.state=bathroom))")
    List<ContextEntity> contextEntities;

    public TestFilter (){
    }

    @Validate
    public void start(){

    }

    @Invalidate
    public void stop(){

    }

    @Bind (id = "context.entity.test", aggregate = true, optional = true)
    public void bindContextEntity (ContextEntity contextEntity){
    }
    @Unbind (id = "context.entity.test")
    public void unbindContextEntity (ContextEntity contextEntity){
    }
}
