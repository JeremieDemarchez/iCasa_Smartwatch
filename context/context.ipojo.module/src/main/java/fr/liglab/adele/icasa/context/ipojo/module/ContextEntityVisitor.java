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

    private String m_typeName;
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
    public void visit(String s,Object o){
        if (o instanceof Type) {
            m_className = ((Type) o).getClassName();
            m_typeName = ((Type) o).getInternalName();
        }
    }
    @Override
    public void visitEnd() {


        String classname = m_workbench.getType().getClassName();

        // Detect if Context Entity is Implemented
        if (!m_workbench.getClassNode().interfaces.contains(Type.getInternalName(ContextEntity.class))){
            m_reporter.error("Cannot ensure that the class " + m_workbench.getType().getClassName() + " implements the " +
                    ContextEntity.class.getName() + " interface.");
        }

        if (!m_workbench.getClassNode().interfaces.contains((m_typeName))){
            m_reporter.error("Cannot ensure that the class " + m_workbench.getType().getClassName() + " is the implementation of the  " +
                    m_className + " context entity description.");
        }


        m_component.addAttribute(new Attribute("classname", classname));

        Element provideElement = getProvidesElement("{" + classname + "," + ContextEntity.class.getName() + "}");
        provideElement.addElement(getPropertyElement(Entity.FACTORY_OF_ENTITY, null, String.class.getName(), Entity.FACTORY_OF_ENTITY_VALUE, "true", "true"));
        provideElement.addElement(getPropertyElement(Entity.FACTORY_OF_ENTITY_TYPE, null, String.class.getName(), m_className, "true", "true"));

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
}
