package fr.liglab.adele.icasa.context.ipojo.module;

import fr.liglab.adele.icasa.context.annotation.*;
import org.apache.felix.ipojo.manipulator.metadata.annotation.ComponentWorkbench;
import org.apache.felix.ipojo.manipulator.spi.AbsBindingModule;
import org.apache.felix.ipojo.manipulator.spi.BindingContext;
import org.objectweb.asm.Type;

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
                .to((BindingContext context) ->
                                new ContextEntityVisitor(context.getWorkbench(), context.getReporter())
                );

        bind(StateField.class)
                .when(on(ElementType.FIELD))
                .to((BindingContext context) -> {
                            String name = context.getFieldNode().name;
                            ComponentWorkbench workbench = context.getWorkbench();

                            if(!workbench.getIds().containsKey(ContextEntityVisitor.CONTEXT_ENTITY_ELEMENT)) {
                                context.getReporter().warn("Class " + context.getClassNode().name + " must but annoted with " + Entity.class + " to use StateField annotation");
                                return null;
                            }else {
                                return new StateVariableFieldVisitor(name,workbench, context.getReporter());
                            }
                        }

                );
        bind(Pull.class)
                .when(on(ElementType.FIELD))
                .to((BindingContext context) -> {
                            String name = context.getFieldNode().name;
                            ComponentWorkbench workbench = context.getWorkbench();

                            if(!workbench.getIds().containsKey(ContextEntityVisitor.CONTEXT_ENTITY_ELEMENT)) {
                                context.getReporter().warn("Class " + context.getClassNode().name + " must but annoted with " + Entity.class + " to use Pull annotation");
                                return null;
                            }else {
                                return new PullFieldVisitor(name,workbench, context.getReporter());
                            }
                        }
                );
        bind(Apply.class)
                .when(on(ElementType.FIELD))
                .to((BindingContext context) -> {
                            String name = context.getFieldNode().name;
                            ComponentWorkbench workbench = context.getWorkbench();

                            if(!workbench.getIds().containsKey(ContextEntityVisitor.CONTEXT_ENTITY_ELEMENT)) {
                                context.getReporter().warn("Class " + context.getClassNode().name + " must but annoted with " + Entity.class + " to use Apply annotation");
                                return null;
                            }else {
                                return new ApplyFieldVisitor(name,workbench, context.getReporter());
                            }
                        }
                );
        bind(Push.class)
                .when(on(ElementType.METHOD))
                .to((BindingContext context) -> {
                            String name = context.getMethodNode().name;
                            ComponentWorkbench workbench = context.getWorkbench();
                            if(!workbench.getIds().containsKey(ContextEntityVisitor.CONTEXT_ENTITY_ELEMENT)) {
                                context.getReporter().warn("Class " + context.getClassNode().name + " must but annoted with " + Entity.class + " to use push annotation");
                                return null;
                            }else if (Type.getReturnType(context.getMethodNode().desc).equals(Type.VOID_TYPE)){
                                context.getReporter().error("A method annotate with pull must have a return. The value of this return is affected in the state buffer each time the method is called.");
                                return null;
                            } else {
                                return new PushMethodVisitor(name,workbench, context.getReporter());
                            }
                        }
                );
    }
}

