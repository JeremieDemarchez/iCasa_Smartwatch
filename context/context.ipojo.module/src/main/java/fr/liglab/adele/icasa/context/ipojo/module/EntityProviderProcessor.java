package fr.liglab.adele.icasa.context.ipojo.module;

import org.apache.felix.ipojo.metadata.Attribute;
import org.apache.felix.ipojo.metadata.Element;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.signature.SignatureReader;
import org.objectweb.asm.signature.SignatureVisitor;

import fr.liglab.adele.icasa.context.model.annotations.provider.Entity;

public class EntityProviderProcessor extends ProviderProcessor<Entity.Creator.Field> {

	protected EntityProviderProcessor(ClassLoader classReferenceLoader) {
		super(Entity.Creator.Field.class,classReferenceLoader);
	}

	@Override
	protected void processCreator(Element creator) {
		String entityType = new TypeArgumentExtractor(getAnnotatedField().signature).getTypeArgument();
		
		if (entityType != null) {
			creator.addAttribute(new Attribute("entity",entityType));
		} else {
			error("Entity creator field '%s' in class %s must parameterize type Entity.Creator with the entity class",
					getAnnotatedField().name, getAnnotatedClass().name);
		}
	}
	
	/**
	 * Utility class to extract the type argument of a generic type field declaration.
	 * 
	 * This extractor can handle generic classes with a single type parameter, and the
	 * actual argument must be a class.
	 *
	 */
	private static class TypeArgumentExtractor extends SignatureVisitor {

		private boolean visitingParameterizedClass 	= false;
		private boolean visitingTypeArgument		= false;
		private String 	typeArgument 				= null; 
		

		public TypeArgumentExtractor(String signature) {
			super(Opcodes.ASM5);
			new SignatureReader(signature).acceptType(this);
		}
		
		public String getTypeArgument() {
			return typeArgument;
		}

		@Override
		public SignatureVisitor visitTypeArgument(char wildcard) {
			visitingTypeArgument = true;
			return super.visitTypeArgument(wildcard);
		}
		
		@Override
		public void visitClassType(String name) {
			if (visitingTypeArgument) {
				typeArgument				= Type.getObjectType(name).getClassName();
			}
			else {
				visitingParameterizedClass 	= true;
				visitingTypeArgument		= false;
			}
		}

		@Override
		public void visitEnd() {
			if (visitingTypeArgument) {
				visitingTypeArgument 		= false;
			}
			else if (visitingParameterizedClass) {
				visitingParameterizedClass	= false;
			}
		}
		
		
	}

}
