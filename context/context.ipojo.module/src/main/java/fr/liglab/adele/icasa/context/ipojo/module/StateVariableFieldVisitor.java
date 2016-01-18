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
        if (name.equals("name")) {
            m_name = value.toString();
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
            m_workbench.getIds().put(m_name,stateVariableElement);
        }

        stateVariableElement.addAttribute(new Attribute(STATE_VARIABLE_ATTRIBUTE_FIELD, m_field));

    }
}
