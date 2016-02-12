package fr.liglab.adele.icasa.context.runtime.handler.entity;

import java.lang.reflect.Member;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import org.apache.felix.ipojo.ConfigurationException;
import org.apache.felix.ipojo.FieldInterceptor;
import org.apache.felix.ipojo.InstanceManager;
import org.apache.felix.ipojo.MethodInterceptor;
import org.apache.felix.ipojo.metadata.Element;
import org.apache.felix.ipojo.parser.FieldMetadata;
import org.apache.felix.ipojo.parser.MethodMetadata;
import org.apache.felix.ipojo.parser.PojoMetadata;

/**
 * Interceptor to handle state fields that are not handler by direct access, but using synchronization
 * functions (push,pull,apply) 
 */
public class SynchronisationInterceptor implements FieldInterceptor, MethodInterceptor {

	/**
	 * The invocation handlers used in every field access
	 */
	private final Map<String,BiConsumer<Object,Object>> applyFunctions	= new HashMap<>();
    private final Map<String,Function<Object,Object>> pullFunctions 	= new HashMap<>();

    /**
     * The associated entity handler in charge of keeping the context state
     */
	private final EntityHandler entityHandler;

	/**
	 * The mapping from fields handled by this interceptor to states of the context
	 */
	private final Map<String,String> fieldToState = new HashMap<>();

	/**
	 * The mapping from methods handled by this interceptor to states of the context
	 */
	private final Map<String,String> methodToState = new HashMap<>();
	
	/**
	 * @param entityHandler
	 */
	public SynchronisationInterceptor(EntityHandler entityHandler) {
		this.entityHandler = entityHandler;
	}

    @Override
    public Object onGet(Object pojo, String fieldName, Object value) {
    	Function<Object,Object> pullFunction = pullFunctions.get(fieldName);
    	if (pullFunction != null) {
    		return pullFunction.apply(pojo);
    	}
    	
    	return entityHandler.getStateValue(fieldToState.get(fieldName));
    }

	@Override
    public void onSet(Object pojo, String fieldName, Object value) {
    	BiConsumer<Object,Object> applyFunction = applyFunctions.get(fieldName);
    	if (applyFunction != null) {
    		applyFunction.accept(pojo,value);
    	}
    }

    @Override
    public void onExit(Object pojo, Member method, Object returnedValue) {
    	if (returnedValue != null){
        	this.entityHandler.update(methodToState.get(method.getName()),returnedValue);
    	}
    }
	
	@SuppressWarnings("unchecked")
	public void handleState(InstanceManager component, PojoMetadata componentMetadata, Element state) throws ConfigurationException {
		
		String stateId		= state.getAttribute("id");
		String stateField	= state.getAttribute("field");
		
		/*
		 * Check the association field to state
		 */
		if (stateField == null) {
			throw new ConfigurationException("Malformed Manifest : a state variable is declared with no 'field' attribute");
		}
		
        FieldMetadata fieldMetadata = componentMetadata.getField(stateField);
		if (fieldMetadata == null) {
			throw new ConfigurationException("Malformed Manifest : the specified field doesn't exists "+stateField);
		}
		
        fieldToState.put(stateField,stateId);
        component.register(fieldMetadata,this);

        /*
         * If a pull function was defined, register a function that will be invoked on every field access 
         */
	    String pull = state.getAttribute("pull");
	    if (pull != null) {

	    	/*
	    	 * Verify the type of the pull field is a Supplier 
	    	 * 
	    	 * TODO iPOJO metadata doesn't handle generic types. We could use reflection on the component class to validate
	    	 * that the pull field is a Supplier of the type of the state field
	    	 */
	    	FieldMetadata pullFieldMetadata = componentMetadata.getField(pull);
	    	String pullFieldType			= FieldMetadata.getReflectionType(pullFieldMetadata.getFieldType());
	    	if (! pullFieldType.equals(Supplier.class.getCanonicalName())) {
	    		throw new ConfigurationException("Malformed Manifest : the specified pull field "+pull+" must be of type "+Supplier.class.getName());
	    	}
	    	
	    	/*
	    	 * The field access handler. 
	    	 * 
	    	 * Notice that the lambda expression used capture the value of some variables from configuration time to actual
	    	 * access time. 
	    	 */
	    	pullFunctions.put(stateField, (Object pojo) -> {
	    		Supplier<Object> supplier = (Supplier<Object>) component.getFieldValue(pull,pojo);
	    		return supplier.get();
	    	});
	    	
	    	
	    	/*
	    	 * Register periodic task if necessary
	    	 */
	    	Long period 	= Long.valueOf(state.getAttribute("period"));
	    	TimeUnit unit	= TimeUnit.valueOf(state.getAttribute("unit"));
	    	
	    	if (period != -1L) {
	    		entityHandler.schedule(
	    				(InstanceManager instance) -> {
	    					
	    					/*
	    					 * Execute the pull function on the pojo object and notify a state change
	    					 */
	    			    	Function<Object,Object> pullFunction = pullFunctions.get(stateField);
	    			    	if (pullFunction != null) {
	    			    		Object pulledValue = pullFunction.apply(instance.getPojoObject());
		    			    	entityHandler.update(stateId,pulledValue);
	    			    	}
	    				},
	    				period, unit);
	    	}
	    }

        /*
         * If an apply function was defined, register a function that will be invoked on every field access 
         */
	    String apply = state.getAttribute("apply");
	    if (apply != null) {

	    	/*
	    	 * Verify the type of the apply field is a Consumer 
	    	 * 
	    	 * TODO iPOJO metadata doesn't handle generic types. We could use reflection on the component class to validate
	    	 * that the apply field is a Consumer of the type of the state field
	    	 */
	    	FieldMetadata applyFieldMetadata = componentMetadata.getField(apply);
	    	String applyFieldType			= FieldMetadata.getReflectionType(applyFieldMetadata.getFieldType());
	    	if (! applyFieldType.equals(Consumer.class.getCanonicalName())) {
	    		throw new ConfigurationException("Malformed Manifest : the specified apply field "+apply+" must be of type "+Consumer.class.getName());
	    	}
	    	
	    	/*
	    	 * The field access handler. 
	    	 * 
	    	 * Notice that the lambda expression used capture the value of some variables from configuration time to actual
	    	 * access time. 
	    	 */
	    	applyFunctions.put(stateField, (Object pojo, Object value) -> {
	    		Consumer<Object> supplier = (Consumer<Object>) component.getFieldValue(apply,pojo);
	    		supplier.accept(value);
	    	});
	    }
	    
	    String push = state.getAttribute("push");
	    if (push != null) {
	    	
	    	/*
	    	 * Verify the push method is correctly defined
	    	 * 
	    	 * TODO we should verify the return type if the method matches the type of the state field
	    	 */
	    	MethodMetadata stateMethod = componentMetadata.getMethod(push);
			if (stateMethod == null) {
				throw new ConfigurationException("Malformed Manifest : the specified method doesn't exists "+stateMethod);
			}
	    	
	        methodToState.put(push,stateId);
	        component.register(stateMethod,this);
	    }
	}

	public void validate() {
		/*
        for (String stateId : stateIds){
            if (myPullFunction.containsKey(stateId)) {
                ScheduledFunction getFunction = myPullFunction.get(stateId);
                Object returnObj = getFunction.apply(stateId);
                update(stateId,returnObj);

                if (getFunction.getPeriod() > 0){
                    ManagedScheduledFutureTask futur = scheduler.scheduleAtFixedRate(getFunction, getFunction.getPeriod(), getFunction.getPeriod(), getFunction.getUnit());
                    getFunction.submitted(futur);

                       ManagedFutureTask.SuccessCallback<Object> onSucces =  (ManagedFutureTask<Object> var1, Object var2) -> {
                     LOG.info("On success called on  " + stateId + " with  " + var2);
                     if (var2 != null) {
                     synchronized (myStateLock) {
                     if (var2.equals(myStateValue.get(stateId))) {

                     } else {
                     myStateValue.replace(stateId, var2);
                     updateState(stateId, var2);
                     }
                     }
                     } else {
                     LOG.error("Pull fonction " + stateId + " return null Object ! ");
                     }
                     };
                     futur.onSuccess(onSucces);
                     
                }

            }
        }*/

	}
	
	public void invalidate() {
		/*
        for (String stateId : stateIds){
            if (myPullFunction.containsKey(stateId)) {
                ScheduledFunction getFunction = myPullFunction.get(stateId);
                if (getFunction.getPeriod() > 0){
                    getFunction.task().cancel(true);
                    getFunction.submitted(null);
                }
            }
        }*/
		
	}
    
    @Override
    public void onEntry(Object pojo, Member method, Object[] args) {}


    @Override
    public void onError(Object pojo, Member method, Throwable throwable) {}

    @Override
    public void onFinally(Object pojo, Member method) {}
    
}