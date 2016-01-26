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

    private final Reporter m_reporter;

    /**
     * Parent element element.
     */
    private final ComponentWorkbench m_workbench;

    /**
     * Field name.
     */
    private final String m_method;

    /**
     * Property name.
     */
    private String m_name;

    /**
     * Constructor.
     * @param parent : element element.
     * @param method : method name.
     */
    public PushMethodVisitor(String method, ComponentWorkbench parent, Reporter reporter) {
        super(Opcodes.ASM5);
        m_workbench = parent;
        m_method = method;
        m_reporter = reporter;
    }

    public void visit(String name, Object value) {
        if (name.equals("state")) {
            m_name = value.toString();
            return;
        }
    }
    @Override
    public void visitEnd() {
        Element stateVariableElement = m_workbench.getIds().get(m_name);

        if (stateVariableElement != null && stateVariableElement.getAttribute(STATE_VARIABLE_ATTRIBUTE_PUSH) != null){
            m_reporter.error("Error on class " + m_workbench.getClassNode().name + " : Pull function for " + m_name + " is define more than once" );
        }

        if (stateVariableElement == null) {
            stateVariableElement = new Element(StateVariableFieldVisitor.STATE_VARIABLE_ELEMENT, "");
            stateVariableElement.addAttribute(new Attribute(StateVariableFieldVisitor.STATE_VARIABLE_ATTRIBUTE_NAME,m_name));
            m_workbench.getElements().put(stateVariableElement,ContextEntityVisitor.CONTEXT_ENTITY_ELEMENT);
            m_workbench.getIds().put(m_name, stateVariableElement);
        }

        stateVariableElement.addAttribute(new Attribute(STATE_VARIABLE_ATTRIBUTE_PUSH, m_method));

        if (stateVariableElement.getAttribute(StateVariableFieldVisitor.STATE_VARIABLE_ATTRIBUTE_DIRECT_ACCESS) != null && Boolean.valueOf(stateVariableElement.getAttribute(StateVariableFieldVisitor.STATE_VARIABLE_ATTRIBUTE_DIRECT_ACCESS))){
            m_reporter.warn(" State Element " + m_name + " is in direct access but own synchro function (PUSH, PULL or APPLY). At runtime this function will not be used by the framework and affects the state.");
        }
    }
}
