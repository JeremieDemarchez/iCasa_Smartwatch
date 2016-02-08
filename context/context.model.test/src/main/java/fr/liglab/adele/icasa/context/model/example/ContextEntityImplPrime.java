package fr.liglab.adele.icasa.context.model.example;

import fr.liglab.adele.icasa.context.model.annotations.entity.ContextEntity;

@ContextEntity(services = ContextEntityDescription.class)
public class ContextEntityImplPrime implements ContextEntityDescription{

    @Override
    public String hello() {
        return null;
    }

    @Override
    public String getSerialNumber() {
        return null;
    }

	@Override
	public void setHello(String hello) {
	}

	@Override
	public String externalNotification(String externalEvent) {
		return null;
	}
}