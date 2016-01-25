package fr.liglab.adele.icasa.context.ipojo.module;

import fr.liglab.adele.icasa.context.annotation.Entity;
import fr.liglab.adele.icasa.context.model.ContextEntity;
import org.apache.felix.ipojo.manipulator.Reporter;
import org.apache.felix.ipojo.manipulator.metadata.annotation.ComponentWorkbench;
import org.apache.felix.ipojo.metadata.Attribute;
import org.apache.felix.ipojo.metadata.Element;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by aygalinc on 14/01/16.
 */
public class ContextEntityVisitor extends AnnotationVisitor {

    public static final String CONTEXT_ENTITY_ELEMENT = "fr.liglab.adele.icasa.context.runtime.handler.EntityHandler:entity";
    /**
     * Component attribute.
     */
    public static final String COMPONENT = "component";

    private final Reporter m_reporter;
    private final ComponentWorkbench m_workbench;

    private Element m_component = getComponentElement();

    private Set<String> m_internalTypeNames = new HashSet<>();
    private String m_provides;
    private String m_className;

    private Class spec;
    /**
     * Creates the visitor.
     * @param workbench the workbench.
     * @param reporter the reporter.
     */
    public ContextEntityVisitor(ComponentWorkbench workbench, Reporter reporter) {
        super(Opcodes.ASM5);
        this.m_reporter = reporter;
        this.m_workbench = workbench;
    }

    @Override
    public AnnotationVisitor visitArray(String name){
        if (name.equals("spec")){
            return new InterfaceArrayVisitor();
        }else {
            return null;
        }
    }
    @Override
    public void visitEnd() {


        String classname = m_workbench.getType().getClassName();

        for (String spec : m_internalTypeNames){
            if (!m_workbench.getClassNode().interfaces.contains((spec))){
                m_reporter.error("Cannot ensure that the class " + m_workbench.getType().getClassName() + " is the implementation of the  " +
                        spec + " context entity description.");
            }
        }

        m_component.addAttribute(new Attribute("classname", classname));

        Element provideElement = getProvidesElement(m_provides);
        provideElement.addElement(getPropertyElement(Entity.FACTORY_OF_ENTITY, null, String.class.getName(), Entity.FACTORY_OF_ENTITY_VALUE, "false", "true"));
        provideElement.addElement(getPropertyElement(Entity.FACTORY_OF_ENTITY_TYPE, null, String.class.getName(), m_className, "false", "true"));

        m_workbench.getElements().put(provideElement,null);

        Element contextElement = getContextEntityElement();
        m_workbench.getElements().put(contextElement,null);

        if (m_workbench.getRoot() == null) {
            m_workbench.setRoot(m_component);
            m_workbench.getIds().put(CONTEXT_ENTITY_ELEMENT,contextElement);
        } else {
            // Error case: 2 component type's annotations (@Component and @Handler for example) on the same class
            m_reporter.error("Multiple 'component type' annotations on the class '{%s}'.", classname);
            m_reporter.warn("@Entity is ignored.");
        }
    }


    // Utility method

    public Element getContextEntityElement() {
        Element entity = new Element(CONTEXT_ENTITY_ELEMENT,"");
        return entity;
    }

    public Element getProvidesElement(String specifications) {
        Element provides = new Element("provides", "");
        if (specifications == null) {
            return provides;
        } else {
            Attribute attribute = new Attribute("specifications", specifications);
            provides.addAttribute(attribute);
            return provides;
        }
    }

    public Element getComponentElement() {
        return new Element(COMPONENT, "");
    }

    public Element getPropertyElement(String name,String field,String type,String value,String mandatory,String immutable) {
        Element prop  = new Element("property", "");
        if (name != null) {
            prop.addAttribute(new Attribute("name", name));
        }
        if (field != null) {
            prop.addAttribute(new Attribute("field", field));
        }
        if (type != null) {
            prop.addAttribute(new Attribute("type", type));
        }

        if (value != null) {
            prop.addAttribute(new Attribute("value", value));
        }
        if (mandatory != null) {
            prop.addAttribute(new Attribute("mandatory", mandatory));
        }
        if (immutable != null) {
            prop.addAttribute(new Attribute("immutable", immutable));
        }
        return prop;
    }

    private class InterfaceArrayVisitor extends AnnotationVisitor {
        /**
         * List of parsed interface.
         */
        private String m_itfs;

        public InterfaceArrayVisitor() {
            super(Opcodes.ASM5);
        }

        /**
         * Visit one element of the array.
         *
         * @param arg0 : null
         * @param arg1 : element value.
         * @see org.objectweb.asm.AnnotationVisitor#visit(java.lang.String, java.lang.Object)
         */
        public void visit(String arg0, Object arg1) {
            if (m_itfs == null) {
                m_itfs = "{" + ((Type) arg1).getClassName();
            } else {
                m_itfs += "," + ((Type) arg1).getClassName();
            }
            m_internalTypeNames.add(((Type) arg1).getInternalName());
        }

        /**
         * End of the array visit.
         * Add the attribute to 'provides' element.
         *
         * @see org.objectweb.asm.AnnotationVisitor#visitEnd()
         */
        public void visitEnd() {
            m_provides = m_itfs + "}";
        }

    }

}
