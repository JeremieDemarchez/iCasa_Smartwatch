package fr.liglab.adele.icasa.context.model.command;

import fr.liglab.adele.icasa.command.handler.Command;
import fr.liglab.adele.icasa.command.handler.CommandProvider;
import fr.liglab.adele.icasa.context.model.ContextEntity;
import fr.liglab.adele.icasa.context.model.Relation;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Requires;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;


@Component
@Instantiate
@CommandProvider(namespace = "context")
public class ContextModelGoGoCommand  {

    private static final Logger LOG = LoggerFactory.getLogger(ContextModelGoGoCommand.class);

    @Requires(specification = Relation.class,optional = true,proxy = false)
    List<Relation> relations;

    @Requires(specification = ContextEntity.class,optional = true,proxy = false)
    List<ContextEntity> entities;


    @Command
    public void relations(){
        String out = new String();
        for (Relation relation: relations){
            out = out +"Relation Name : " + relation.getName()+ "\n";
            out = out +relation.getSource() + " --------> " + relation.getEnd()+"\n"
                    +"Exented state : " + relation.getExtendedState().getName() + " : " + relation.getExtendedState().getValue()+ "\n";
        }
        LOG.info(out);
    }

    @Command
    public void relationsByName(String name){
        String out = new String();
        for (Relation relation: relations) {
            if (relation.getName().equals(name)) {
                out = out +"Relation Name : " + relation.getName()+ "\n";
                out = out + relation.getSource() + " --------> " + relation.getEnd() + "\n"
                        +"Exented state : " + relation.getExtendedState().getName() + " : " + relation.getExtendedState().getValue()+ "\n";
            }
        }
        LOG.info(out);
    }

    @Command
    public void relationsBySource(String source){
        String out = new String();
        for (Relation relation: relations) {
            if (relation.getSource().equals(source)) {
                out = out +"Relation Name : " + relation.getName() + "\n";
                out = out +relation.getSource() + " --------> " + relation.getEnd()+"\n"
                        +"Exented state : " + relation.getExtendedState().getName() + " : " + relation.getExtendedState().getValue()+ "\n";
            }
        }
        LOG.info(out);
    }

    @Command
    public void relationsByEnd(String end){
        String out = new String();
        for (Relation relation: relations) {
            if (relation.getEnd().equals(end)) {
                out = out + "Relation Name : " + relation.getName()+"\n";
                out = out +relation.getSource() + " --------> " + relation.getEnd()+"\n"
                        +"Exented state : " + relation.getExtendedState().getName() + " : " + relation.getExtendedState().getValue()+ "\n";
            }
        }
        LOG.info(out);
    }

    @Command
    public void entity(String name){
        String out = new String();
        for (ContextEntity entity : entities){
            if (entity.getId().equals(name)){
                out = out + "Entity : " + name + "\n";
                out = out +"State : " + "\n";
                for (String key : entity.getStateAsMap().keySet()){
                    out = out +" Property : " + key + " with value : " + entity.getStateAsMap().get(key)+ "\n";
                }
                out = out +"Extension : " + "\n";
                for (String key : entity.getStateExtensionAsMap().keySet()){
                    out = out +" Property : " + key + " with value : " + entity.getStateExtensionAsMap().get(key)+ "\n";
                }
                LOG.info(out);
            }
        }
    }

}
