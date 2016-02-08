package fr.liglab.adele.icasa.context.ipojo.module;

import fr.liglab.adele.icasa.context.model.annotations.entity.State;

/**
 * Created by aygalinc on 15/01/16.
 */
public class ApplyFieldProcessor extends StateProcessor<State.Apply> {


    public ApplyFieldProcessor(ClassLoader classReferenceLoader) {
		super(State.Apply.class,classReferenceLoader);
	}

	@Override
	protected String getStateId() {
		return State.ID(getAnnotation().service(), getAnnotation().state());
	}

	@Override
	protected void processStateAttributes() {
		addStateAttribute("apply", getAnnotatedField().name, false);
	}

}
