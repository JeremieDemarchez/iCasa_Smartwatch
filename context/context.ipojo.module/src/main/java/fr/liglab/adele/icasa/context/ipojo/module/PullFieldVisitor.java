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
     * Field name.
     */
    private  String m_timeUnit;

    /**
     * Field name.
     */
    private  String m_period;



    /**
     * Property name.
     */
    private String m_name;

    /**
     * Constructor.
     * @param parent : element element.
     * @param field : field name.
     */
    public PullFieldVisitor(String field, ComponentWorkbench parent, Reporter reporter) {
        super(Opcodes.ASM5);
        m_workbench = parent;
        m_field = field;
        m_reporter = reporter;
    }

    public void visit(String name, Object value) {
        if (name.equals("state")) {
            m_name = value.toString();
            return;
        }
        if (name.equals("period")) {

            m_period = value.toString();
            m_reporter.info("period " + m_period);
            return;
        }
        if (name.equals("unit")) {
            //NOT WORK
            m_timeUnit = value.toString();
            m_reporter.info("period " + m_timeUnit);
            return;
        }
    }
    @Override
    public void visitEnd() {
        Element stateVariableElement = m_workbench.getIds().get(m_name);

        if (stateVariableElement != null && stateVariableElement.getAttribute(STATE_VARIABLE_ATTRIBUTE_PULL) != null){
            m_reporter.error("Error on class " + m_workbench.getClassNode().name + " : Pull function for " + m_name + " is define more than once" );
        }

        if (stateVariableElement == null) {
            stateVariableElement = new Element(StateVariableFieldVisitor.STATE_VARIABLE_ELEMENT, "");
            stateVariableElement.addAttribute(new Attribute(StateVariableFieldVisitor.STATE_VARIABLE_ATTRIBUTE_NAME,m_name));
            m_workbench.getElements().put(stateVariableElement,ContextEntityVisitor.CONTEXT_ENTITY_ELEMENT);
            m_workbench.getIds().put(m_name, stateVariableElement);
        }

        stateVariableElement.addAttribute(new Attribute(STATE_VARIABLE_ATTRIBUTE_PULL, m_field));

    }
}
