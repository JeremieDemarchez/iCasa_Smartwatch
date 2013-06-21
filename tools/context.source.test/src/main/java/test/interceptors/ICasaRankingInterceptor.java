/**
 *
 *   Copyright 2011-2012 Universite Joseph Fourier, LIG, ADELE team
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package test.interceptors;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Instantiate;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.StaticServiceProperty;
import org.apache.felix.ipojo.dependency.interceptors.ServiceRankingInterceptor;
import org.apache.felix.ipojo.util.DependencyModel;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;

//@Component(name = "ICasaRankingInterceptor")
//@Provides(properties = { @StaticServiceProperty(name = "target", value = "(objectClass=fr.liglab.adele.icasa.device.GenericDevice)", type = "java.lang.String") })
//@Instantiate
public class ICasaRankingInterceptor implements ServiceRankingInterceptor {

	@Override
	public void open(DependencyModel dependency) {
		// TODO Auto-generated method stub

	}

	@Override
	public void close(DependencyModel dependency) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<ServiceReference> getServiceReferences(DependencyModel model, List<ServiceReference> matching) {
		return onRankingInterceptorCall(model, matching, "getServiceReferences");
	}

	@Override
	public List<ServiceReference> onServiceArrival(DependencyModel model, List<ServiceReference> matching,
	      ServiceReference<?> reference) {
		return onRankingInterceptorCall(model, matching, "onServiceArrival");
	}

	@Override
	public List<ServiceReference> onServiceDeparture(DependencyModel model, List<ServiceReference> matching,
	      ServiceReference<?> reference) {
		return onRankingInterceptorCall(model, matching, "onServiceDeparture");
	}


	
	public List<ServiceReference> onServiceModified(DependencyModel model, List<ServiceReference> matching,
	      ServiceReference<?> reference) {
		return onRankingInterceptorCall(model, matching, "onServiceModified");
	}

	
	private List<ServiceReference> onRankingInterceptorCall(DependencyModel model, List<ServiceReference> matching, String method) {
		System.out.println("-----------------------------------------------------------------------");
		System.out.println("Ranking Interceptor - Method :: " + method);
		System.out.println("Ranking Interceptor - Component :: " + getInstanceName(model));
		//System.out.println("Ranking Interceptor - List :: " + matching);
		System.out.println("Ranking Interceptor - List Size :: " + matching.size());				
		Collections.sort(matching, new RankComparator());
		for (ServiceReference reference : matching) {
			System.out.println("\t Ranking Interceptor - Service Reference :: " + reference.getProperty(Constants.SERVICE_ID) + " - Rank: " + getRankInServiceReference(reference));
      }
		System.out.println("-----------------------------------------------------------------------");
		return matching;
	}

	
	private String getInstanceName(DependencyModel model) {
		return model.getComponentInstance().getInstanceName();
	}

	private int getRankInServiceReference(ServiceReference reference) {
		Integer rank = (Integer) reference.getProperty("rank");
		if (rank==null)
			return 100;
		return rank;
	}
	
	class RankComparator implements Comparator<ServiceReference> {

		@Override
      public int compare(ServiceReference reference1, ServiceReference reference2) {
			return getRankInServiceReference(reference1) - getRankInServiceReference(reference2);
      }
		
		
	}
}
