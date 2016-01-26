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

    private final Reporter m_reporter;

    /**
     * Parent element element.
     */
    private final ComponentWorkbench m_workbench;

    /**
     * Field name.
     */
    private final String m_field;


    /**
     * Property name.
     */
    private String m_name;

    /**
     * Property name.
     */
    private String m_defaultValue;

    /**
     * Property name.
     */
    private boolean m_directAccess = false;

    /**
     * Constructor.
     * @param parent : element element.
     * @param field : field name.
     */
    public StateVariableFieldVisitor(String field, ComponentWorkbench parent,Reporter reporter) {
        super(Opcodes.ASM5);
        m_workbench = parent;
        m_field = field;
        m_reporter = reporter;
    }

    public void visit(String name, Object value) {
        if (name.equals(STATE_VARIABLE_ATTRIBUTE_NAME)) {
            m_name = value.toString();
            return;
        }
        if (name.equals(STATE_VARIABLE_ATTRIBUTE_VALUE)) {
            m_defaultValue = value.toString();
            return;
        }
        if (name.equals(STATE_VARIABLE_ATTRIBUTE_DIRECT_ACCESS)) {
            m_directAccess = Boolean.valueOf(value.toString());
            return;
        }
    }
    @Override
    public void visitEnd() {
        Element stateVariableElement = null;

        Element stateVariable = m_workbench.getIds().get(m_name);

        if ( (stateVariable != null) && (stateVariable.getAttribute(STATE_VARIABLE_ATTRIBUTE_NAME) != null) && (stateVariable.getAttribute(STATE_VARIABLE_ATTRIBUTE_NAME).equals(m_name)) ){
            if (!(stateVariable.getAttribute(STATE_VARIABLE_ATTRIBUTE_FIELD) == null)){
                m_reporter.error("State Variable " + m_name + " is attached to more than one field");
                return;
            } else {
                stateVariableElement = stateVariable;
            }
        }

        if (stateVariableElement == null) {
            stateVariableElement = new Element(STATE_VARIABLE_ELEMENT, "");
            stateVariableElement.addAttribute(new Attribute(STATE_VARIABLE_ATTRIBUTE_NAME, m_name));
            m_workbench.getElements().put(stateVariableElement,ContextEntityVisitor.CONTEXT_ENTITY_ELEMENT);
            m_workbench.getIds().put(m_name, stateVariableElement);
        }

        stateVariableElement.addAttribute(new Attribute(STATE_VARIABLE_ATTRIBUTE_FIELD, m_field));
        if (m_defaultValue != null && m_defaultValue.equals("")){
            stateVariableElement.addAttribute(new Attribute(STATE_VARIABLE_ATTRIBUTE_VALUE, m_name));
        }

        stateVariableElement.addAttribute(new Attribute(STATE_VARIABLE_ATTRIBUTE_DIRECT_ACCESS,String.valueOf(m_directAccess)));

        /**
         * Check if state variable have synchro function and log a warning in this case if direct access is true
         */
        if (m_directAccess){
            if (stateVariableElement.getAttribute(PullFieldVisitor.STATE_VARIABLE_ATTRIBUTE_PULL) != null || stateVariableElement.getAttribute(PushMethodVisitor.STATE_VARIABLE_ATTRIBUTE_PUSH) != null || stateVariableElement.getAttribute(ApplyFieldVisitor.STATE_VARIABLE_ATTRIBUTE_SET) != null){
                m_reporter.warn(" State Element " + m_name + " is in direct access but own synchro function (PUSH, PULL or APPLY). At runtime this function will not be used by the framework and affects the state.");
            }
        }
    }
}
