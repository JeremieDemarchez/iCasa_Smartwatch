package fr.liglab.adele.icasa.context.ipojo.module;

import org.apache.felix.ipojo.metadata.Attribute;
import org.apache.felix.ipojo.metadata.Element;

import fr.liglab.adele.icasa.context.model.annotations.provider.Relation;

public class RelationProviderProcessor extends ProviderProcessor<Relation.Creator.Field> {

	protected RelationProviderProcessor(ClassLoader classReferenceLoader) {
		super(Relation.Creator.Field.class,classReferenceLoader);
	}

	@Override
	protected void processCreator(Element creator) {
		creator.addAttribute(new Attribute("relation",getAnnotation().value()));
	}

}
