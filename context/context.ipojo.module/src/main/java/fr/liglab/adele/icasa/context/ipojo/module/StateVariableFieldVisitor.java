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
public class StateVariableFieldVisitor extends AnnotationVisitor {

    public static final String STATE_VARIABLE_ELEMENT = "stateVariable";

    public static final String STATE_VARIABLE_ATTRIBUTE_NAME = "name";

    public static final String STATE_VARIABLE_ATTRIBUTE_FIELD = "field";

    public static final String STATE_VARIABLE_ATTRIBUTE_VALUE = "value";

    public static final String STATE_VARIABLE_ATTRIBUTE_DIRECT_ACCESS = "directAccess";

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
     * Property name.
     */
    private String myName;

    /**
     * Property name.
     */
    private String myDefaultValue;

    /**
     * Property name.
     */
    private boolean myDirectAccess = false;

    /**
     * Constructor.
     * @param parent : element element.
     * @param field : field name.
     */
    public StateVariableFieldVisitor(String field, ComponentWorkbench parent,Reporter reporter) {
        super(Opcodes.ASM5);
        myWorkbench = parent;
        myField = field;
        myReporter = reporter;
    }

    @Override
    public void visit(String name, Object value) {
        if (STATE_VARIABLE_ATTRIBUTE_NAME.equals(name)) {
            myName = value.toString();
            return;
        }
        if (STATE_VARIABLE_ATTRIBUTE_VALUE.equals(name)) {
            myDefaultValue = value.toString();
            return;
        }
        if (STATE_VARIABLE_ATTRIBUTE_DIRECT_ACCESS.equals(name)) {
            myDirectAccess = Boolean.valueOf(value.toString());
            return;
        }
    }

    @Override
    public void visitEnd() {
        Element stateVariableElement = null;

        Element stateVariable = myWorkbench.getIds().get(myName);

        if ( (stateVariable != null) && (stateVariable.getAttribute(STATE_VARIABLE_ATTRIBUTE_NAME) != null) && (stateVariable.getAttribute(STATE_VARIABLE_ATTRIBUTE_NAME).equals(myName)) ){
            if (!(stateVariable.getAttribute(STATE_VARIABLE_ATTRIBUTE_FIELD) == null)){
                myReporter.error("State Variable " + myName + " is attached to more than one field");
                return;
            } else {
                stateVariableElement = stateVariable;
            }
        }

        if (stateVariableElement == null) {
            stateVariableElement = new Element(STATE_VARIABLE_ELEMENT, "");
            stateVariableElement.addAttribute(new Attribute(STATE_VARIABLE_ATTRIBUTE_NAME, myName));
            myWorkbench.getElements().put(stateVariableElement,ContextEntityVisitor.CONTEXT_ENTITY_ELEMENT);
            myWorkbench.getIds().put(myName, stateVariableElement);
        }

        stateVariableElement.addAttribute(new Attribute(STATE_VARIABLE_ATTRIBUTE_FIELD, myField));
        if ("".equals(myDefaultValue)){
            stateVariableElement.addAttribute(new Attribute(STATE_VARIABLE_ATTRIBUTE_VALUE, myName));
        }

        stateVariableElement.addAttribute(new Attribute(STATE_VARIABLE_ATTRIBUTE_DIRECT_ACCESS,String.valueOf(myDirectAccess)));

        /**
         * Check if state variable have synchro function and log a warning in this case if direct access is true
         */
        if (myDirectAccess && haveSynchroAttribute(stateVariableElement)){
                myReporter.warn(" State Element " + myName + " is in direct access but own synchro function (PUSH, PULL or APPLY). At runtime this function will not be used by the framework and affects the state.");
        }
    }

    private boolean haveSynchroAttribute(Element stateVariableElement){
        return stateVariableElement.getAttribute(PullFieldVisitor.STATE_VARIABLE_ATTRIBUTE_PULL) != null || stateVariableElement.getAttribute(PushMethodVisitor.STATE_VARIABLE_ATTRIBUTE_PUSH) != null || stateVariableElement.getAttribute(ApplyFieldVisitor.STATE_VARIABLE_ATTRIBUTE_SET) != null ;
    }
}
