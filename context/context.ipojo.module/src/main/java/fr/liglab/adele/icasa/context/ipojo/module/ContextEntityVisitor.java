package fr.liglab.adele.icasa.context.ipojo.module;

import fr.liglab.adele.icasa.context.annotation.Entity;
import org.apache.felix.ipojo.manipulator.Reporter;
import org.apache.felix.ipojo.manipulator.metadata.annotation.ComponentWorkbench;
import org.apache.felix.ipojo.metadata.Attribute;
import org.apache.felix.ipojo.metadata.Element;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.util.HashSet;
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

    private final Reporter myReporter;

    private final ComponentWorkbench myWorkbench;

    private Element myComponent = getComponentElement();

    private Set<String> myInternalTypeNames = new HashSet<>();

    private String myProvides;

    /**
     * Creates the visitor.
     * @param workbench the workbench.
     * @param reporter the reporter.
     */
    public ContextEntityVisitor(ComponentWorkbench workbench, Reporter reporter) {
        super(Opcodes.ASM5);
        this.myReporter = reporter;
        this.myWorkbench = workbench;
    }

    @Override
    public AnnotationVisitor visitArray(String name){
        if ("spec".equals(name)){
            return new InterfaceArrayVisitor();
        }else {
            return null;
        }
    }
    @Override
    public void visitEnd() {


        String classname = myWorkbench.getType().getClassName();

        for (String spec : myInternalTypeNames){
            if (!myWorkbench.getClassNode().interfaces.contains(spec)){
                myReporter.error("Cannot ensure that the class " + myWorkbench.getType().getClassName() + " is the implementation of the  " +
                        spec + " context entity description.");
            }
        }

        myComponent.addAttribute(new Attribute("classname", classname));
        myComponent.addAttribute(new Attribute("immediate", "true"));

        Element provideElement = getProvidesElement(myProvides);
        provideElement.addElement(getPropertyElement(Entity.FACTORY_OF_ENTITY, null, String.class.getName(), Entity.FACTORY_OF_ENTITY_VALUE, "false", "true"));
        provideElement.addElement(getPropertyElement(Entity.FACTORY_OF_ENTITY_TYPE, null, String.class.getName(), classname, "false", "true"));

        myWorkbench.getElements().put(provideElement,null);

        Element contextElement = getContextEntityElement();
        myWorkbench.getElements().put(contextElement,null);

        if (myWorkbench.getRoot() == null) {
            myWorkbench.setRoot(myComponent);
            myWorkbench.getIds().put(CONTEXT_ENTITY_ELEMENT,contextElement);
        } else {
            // Error case: 2 component type's annotations (@Component and @Handler for example) on the same class
            myReporter.error("Multiple 'component type' annotations on the class '{%s}'.", classname);
            myReporter.warn("@Entity is ignored.");
        }
    }


    // Utility method

    public Element getContextEntityElement() {
        return  new Element(CONTEXT_ENTITY_ELEMENT,"");
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
        private String myItfs;

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
        @Override
        public void visit(String arg0, Object arg1) {
            if (myItfs == null) {
                myItfs = "{" + ((Type) arg1).getClassName();
            } else {
                myItfs += "," + ((Type) arg1).getClassName();
            }
            myInternalTypeNames.add(((Type) arg1).getInternalName());
        }

        /**
         * End of the array visit.
         * Add the attribute to 'provides' element.
         *
         * @see org.objectweb.asm.AnnotationVisitor#visitEnd()
         */
        @Override
        public void visitEnd() {
            myProvides = myItfs + "}";
        }

    }

}
