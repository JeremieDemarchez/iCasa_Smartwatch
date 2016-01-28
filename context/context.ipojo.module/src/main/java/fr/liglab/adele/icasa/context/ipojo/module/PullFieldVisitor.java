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
public class PullFieldVisitor extends AnnotationVisitor {

    public static final String STATE_VARIABLE_ATTRIBUTE_PULL = "pull";

    private final Reporter myReporter;

    /**
     * Parent element element.
     */
    private final ComponentWorkbench myWorkbench;

    /**
     * Field name.
     */
    private final String myField;

    /**
     * Field name.
     */
    private  String myTimeUnit;

    /**
     * Field name.
     */
    private  String myPeriod;

    /**
     * Property name.
     */
    private String myName;

    /**
     * Constructor.
     * @param parent : element element.
     * @param field : field name.
     */
    public PullFieldVisitor(String field, ComponentWorkbench parent, Reporter reporter) {
        super(Opcodes.ASM5);
        myWorkbench = parent;
        myField = field;
        myReporter = reporter;
    }
    @Override
    public void visit(String name, Object value) {
        if ("state".equals(name)) {
            myName = value.toString();
            return;
        }
        if ("period".equals(name)) {

            myPeriod = value.toString();
            myReporter.info("period " + myPeriod);
            return;
        }
        if ("unit".equals(name)) {
            //NOT WORK
            myTimeUnit = value.toString();
            myReporter.info("period " + myTimeUnit);
            return;
        }
    }
    @Override
    public void visitEnd() {
        Element stateVariableElement = myWorkbench.getIds().get(myName);

        if (stateVariableElement != null && stateVariableElement.getAttribute(STATE_VARIABLE_ATTRIBUTE_PULL) != null){
            myReporter.error("Error on class " + myWorkbench.getClassNode().name + " : Pull function for " + myName + " is define more than once");
        }

        if (stateVariableElement == null) {
            stateVariableElement = new Element(StateVariableFieldVisitor.STATE_VARIABLE_ELEMENT, "");
            stateVariableElement.addAttribute(new Attribute(StateVariableFieldVisitor.STATE_VARIABLE_ATTRIBUTE_NAME, myName));
            myWorkbench.getElements().put(stateVariableElement,ContextEntityVisitor.CONTEXT_ENTITY_ELEMENT);
            myWorkbench.getIds().put(myName, stateVariableElement);
        }

        stateVariableElement.addAttribute(new Attribute(STATE_VARIABLE_ATTRIBUTE_PULL, myField));

        if (stateVariableElement.getAttribute(StateVariableFieldVisitor.STATE_VARIABLE_ATTRIBUTE_DIRECT_ACCESS) != null && Boolean.valueOf(stateVariableElement.getAttribute(StateVariableFieldVisitor.STATE_VARIABLE_ATTRIBUTE_DIRECT_ACCESS))){
            myReporter.warn(" State Element " + myName + " is in direct access but own synchro function (PUSH, PULL or APPLY). At runtime this function will not be used by the framework and affects the state.");
        }
    }
}
