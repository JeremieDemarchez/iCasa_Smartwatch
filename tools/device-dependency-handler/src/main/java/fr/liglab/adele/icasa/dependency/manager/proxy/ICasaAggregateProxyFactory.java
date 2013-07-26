/**
 * 
 */
package fr.liglab.adele.icasa.dependency.manager.proxy;

import fr.liglab.adele.icasa.dependency.manager.DeviceDependency;

/**
 * @author Gabriel
 *
 */
public class ICasaAggregateProxyFactory extends ICasaProxyFactory {
	
	private Object m_service;
	
	public ICasaAggregateProxyFactory(DeviceDependency dependency, Object service) {
	   super(dependency);
	   m_service = service;
   }
	
	@Override
	protected Object getService() {
	   return m_service;
	}

}
