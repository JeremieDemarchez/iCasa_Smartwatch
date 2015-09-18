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
        for (Relation relation: relations){
            LOG.info("Relation Name : " + relation.getName());
            LOG.info(relation.getSource() + " --------> " + relation.getEnd()+"\n"
                    +"Exented state : " + relation.getExtendedState().getName() + " : " + relation.getExtendedState().getValue());
            System.out.println("\n");
        }
    }

    @Command
    public void relationsByName(String name){
        for (Relation relation: relations) {
            if (relation.getName().equals(name)) {
                LOG.info("Relation Name : " + relation.getName());
                LOG.info(relation.getSource() + " --------> " + relation.getEnd());
                System.out.println("\n");
            }
        }
    }

    @Command
    public void relationsBySource(String source){
        for (Relation relation: relations) {
            if (relation.getSource().equals(source)) {
                LOG.info("Relation Name : " + relation.getName());
                LOG.info(relation.getSource() + " --------> " + relation.getEnd());
                System.out.println("\n");
            }
        }
    }

    @Command
    public void relationsByEnd(String end){
        for (Relation relation: relations) {
            if (relation.getEnd().equals(end)) {
                LOG.info("Relation Name : " + relation.getName());
                LOG.info(relation.getSource() + " --------> " + relation.getEnd());
                System.out.println("\n");
            }
        }
    }

    @Command
    public void entity(String name){
        for (ContextEntity entity : entities){
            if (entity.getId().equals(name)){
                LOG.info(" Entity : " + entity.getId() + " found !! ");
                for (String key : entity.getState().keySet()){
                    LOG.info(" Property : " + key + " with value : " + entity.getState().get(key));
                }
            }
        }
    }

}
