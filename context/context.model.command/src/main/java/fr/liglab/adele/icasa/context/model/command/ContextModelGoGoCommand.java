package fr.liglab.adele.icasa.context.model.command;

import fr.liglab.adele.icasa.command.handler.Command;
import fr.liglab.adele.icasa.command.handler.CommandProvider;
import fr.liglab.adele.icasa.context.model.Relation;
import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Requires;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;


@Component
@Instantiate
@CommandProvider(namespace = "HomeLive")
public class ContextModelGoGoCommand  {

    private static final Logger LOG = LoggerFactory.getLogger(ContextModelGoGoCommand.class);

    @Requires(specification = Relation.class)
    List<Relation> relations;

    @Command
    public void relations(){
        for (Relation relation: relations){

        LOG.info("Relation Name : " + relation.getName());
            LOG.info(relation.getEnd() + " --------> " + relation.getEnd());
        }
    }

}
