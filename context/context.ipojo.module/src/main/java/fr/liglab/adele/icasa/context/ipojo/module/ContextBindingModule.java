package fr.liglab.adele.icasa.context.ipojo.module;

import fr.liglab.adele.icasa.context.annotation.Entity;
import fr.liglab.adele.icasa.context.annotation.Pull;
import fr.liglab.adele.icasa.context.annotation.Set;
import fr.liglab.adele.icasa.context.annotation.StateField;
import org.apache.felix.ipojo.manipulator.metadata.annotation.ComponentWorkbench;
import org.apache.felix.ipojo.manipulator.spi.AbsBindingModule;
import org.apache.felix.ipojo.manipulator.spi.AnnotationVisitorFactory;
import org.apache.felix.ipojo.manipulator.spi.BindingContext;
import org.objectweb.asm.AnnotationVisitor;

import java.lang.annotation.ElementType;

import static org.apache.felix.ipojo.manipulator.spi.helper.Predicates.on;

/**
 * Created by aygalinc on 14/01/16.
 */
public class ContextBindingModule extends AbsBindingModule {
    /**
     * Adds the Wisdom annotation to the iPOJO manipulator.
     */
    @Override
    public void configure() {
        bind(Entity.class)
                .to(new AnnotationVisitorFactory() {
                    public synchronized AnnotationVisitor newAnnotationVisitor(BindingContext context) {
                        context.getReporter().info(" Entity Visitor ! ");
                        return new ContextEntityVisitor(context.getWorkbench(), context.getReporter());
                    }
                });

        bind(StateField.class)
                .when(on(ElementType.FIELD))
                .to(new AnnotationVisitorFactory() {
                    public synchronized AnnotationVisitor newAnnotationVisitor(BindingContext context) {
                        String name = context.getFieldNode().name;
                        ComponentWorkbench workbench = context.getWorkbench();

                        if(!workbench.getIds().containsKey(ContextEntityVisitor.CONTEXT_ENTITY_ELEMENT)) {
                            context.getReporter().warn("Class " + context.getClassNode().name + " must but annoted with " + Entity.class + " to use StateField annotation");
                            return null;
                        }else {
                            return new StateVariableFieldVisitor(name,workbench, context.getReporter());
                        }
                    }
                });
        bind(Pull.class)
                .when(on(ElementType.FIELD))
                .to(new AnnotationVisitorFactory() {
                    public synchronized AnnotationVisitor newAnnotationVisitor(BindingContext context) {
                        String name = context.getFieldNode().name;
                        ComponentWorkbench workbench = context.getWorkbench();

                        if(!workbench.getIds().containsKey(ContextEntityVisitor.CONTEXT_ENTITY_ELEMENT)) {
                            context.getReporter().warn("Class " + context.getClassNode().name + " must but annoted with " + Entity.class + " to use Pull annotation");
                            return null;
                        }else {
                            return new PullFieldVisitor(name,workbench, context.getReporter());
                        }
                    }
                });
        bind(Set.class)
                .when(on(ElementType.FIELD))
                .to(new AnnotationVisitorFactory() {
                    public synchronized AnnotationVisitor newAnnotationVisitor(BindingContext context) {
                        String name = context.getFieldNode().name;
                        ComponentWorkbench workbench = context.getWorkbench();

                        if(!workbench.getIds().containsKey(ContextEntityVisitor.CONTEXT_ENTITY_ELEMENT)) {
                            context.getReporter().warn("Class " + context.getClassNode().name + " must but annoted with " + Entity.class + " to use Set annotation");
                            return null;
                        }else {
                            return new SetFieldVisitor(name,workbench, context.getReporter());
                        }
                    }
                });
    }
}

