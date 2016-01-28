package fr.liglab.adele.icasa.context.ipojo.module;

import org.apache.felix.ipojo.manipulator.Reporter;
import org.apache.felix.ipojo.manipulator.metadata.annotation.ComponentWorkbench;
import org.apache.felix.ipojo.metadata.Attribute;
import org.apache.felix.ipojo.metadata.Element;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Opcodes;

/**
 * Created by aygalinc on 15/01/16.
 */
public class PushMethodVisitor extends AnnotationVisitor {

    public static final String STATE_VARIABLE_ATTRIBUTE_PUSH = "push";

    private final Reporter myReporter;

    /**
     * Parent element element.
     */
    private final ComponentWorkbench myWorkbench;

    /**
     * Field name.
     */
    private final String myMethod;

    /**
     * Property name.
     */
    private String myName;

    /**
     * Constructor.
     * @param parent : element element.
     * @param method : method name.
     */
    public PushMethodVisitor(String method, ComponentWorkbench parent, Reporter reporter) {
        super(Opcodes.ASM5);
        myWorkbench = parent;
        myMethod = method;
        myReporter = reporter;
    }

    @Override
    public void visit(String name, Object value) {
        if ("state".equals(name)) {
            myName = value.toString();
            return;
        }
    }

    @Override
    public void visitEnd() {
        Element stateVariableElement = myWorkbench.getIds().get(myName);

        if (stateVariableElement != null && stateVariableElement.getAttribute(STATE_VARIABLE_ATTRIBUTE_PUSH) != null){
            myReporter.error("Error on class " + myWorkbench.getClassNode().name + " : Pull function for " + myName + " is define more than once");
        }

        if (stateVariableElement == null) {
            stateVariableElement = new Element(StateVariableFieldVisitor.STATE_VARIABLE_ELEMENT, "");
            stateVariableElement.addAttribute(new Attribute(StateVariableFieldVisitor.STATE_VARIABLE_ATTRIBUTE_NAME, myName));
            myWorkbench.getElements().put(stateVariableElement,ContextEntityVisitor.CONTEXT_ENTITY_ELEMENT);
            myWorkbench.getIds().put(myName, stateVariableElement);
        }

        stateVariableElement.addAttribute(new Attribute(STATE_VARIABLE_ATTRIBUTE_PUSH, myMethod));

        if (stateVariableElement.getAttribute(StateVariableFieldVisitor.STATE_VARIABLE_ATTRIBUTE_DIRECT_ACCESS) != null && Boolean.valueOf(stateVariableElement.getAttribute(StateVariableFieldVisitor.STATE_VARIABLE_ATTRIBUTE_DIRECT_ACCESS))){
            myReporter.warn(" State Element " + myName + " is in direct access but own synchro function (PUSH, PULL or APPLY). At runtime this function will not be used by the framework and affects the state.");
        }
    }
}
