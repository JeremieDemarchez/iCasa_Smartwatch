package fr.liglab.adele.icasa.context.ipojo.module;

import org.apache.felix.ipojo.metadata.Attribute;
import org.apache.felix.ipojo.metadata.Element;

import fr.liglab.adele.icasa.context.model.annotations.entity.ContextEntity.Relation;
import fr.liglab.adele.icasa.context.model.annotations.internal.HandlerReference;

public class RelationProcessor extends AnnotationProcessor<Relation.Field> {


	public RelationProcessor(ClassLoader classReferenceLoader) {
		super(Relation.Field.class,classReferenceLoader);
	}

	@Override
	public final void process(Relation.Field annotation) {

		String relationId	= Relation.ID(getAnnotatedClassName(true),annotation.value());

		Element relation 	= new Element(HandlerReference.RELATION_HANDLER,HandlerReference.NAMESPACE);
		relation.addAttribute(new Attribute("relation",relationId));
		relation.addAttribute(new Attribute("field",getAnnotatedField().name));
		
		addMetadataElement(relation);
	}


}
