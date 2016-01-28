package fr.liglab.adele.icasa.context.runtime.handler;

import fr.liglab.adele.icasa.context.annotation.EntityType;
import fr.liglab.adele.icasa.context.ipojo.module.ApplyFieldVisitor;
import fr.liglab.adele.icasa.context.ipojo.module.ContextEntityVisitor;
import fr.liglab.adele.icasa.context.ipojo.module.PullFieldVisitor;
import fr.liglab.adele.icasa.context.ipojo.module.StateVariableFieldVisitor;
import fr.liglab.adele.icasa.context.model.ContextEntity;
import org.apache.felix.ipojo.*;
import org.apache.felix.ipojo.annotations.Handler;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.ServiceController;
import org.apache.felix.ipojo.architecture.HandlerDescription;
import org.apache.felix.ipojo.handlers.providedservice.ProvidedServiceHandler;
import org.apache.felix.ipojo.metadata.Attribute;
import org.apache.felix.ipojo.metadata.Element;
import org.apache.felix.ipojo.parser.FieldMetadata;
import org.apache.felix.ipojo.parser.MethodMetadata;
import org.wisdom.api.concurrent.ManagedScheduledExecutorService;
import org.wisdom.api.concurrent.ManagedScheduledFutureTask;

import java.lang.annotation.Annotation;
import java.lang.reflect.Member;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

@Handler(name ="entity" ,namespace = "fr.liglab.adele.icasa.context.runtime.handler.EntityHandler")
@Provides(specifications = ContextEntity.class)
public class EntityHandler extends PrimitiveHandler implements ContextEntity  {

    @ServiceController(value=false, specification=ContextEntity.class)
    private boolean myController;

    /**
     * Component Management
     */
    private ProvidedServiceHandler myProvidedServiceHandler;

    private InstanceManager myInstanceManager;

    private String myComponentName;


    /**
     * State Field
     */
    private final List<String> myStateSpecifications = new ArrayList<>();

    private final Map<String,String> myStatesFields = new HashMap<>();

    private final Map<String,Function> mySetFunction = new HashMap<>();

    private final Map<String,String> mySetFunctionField = new HashMap<>();

    private final Map<String,ScheduledFunction> myPullFunction = new HashMap<>();

    private final Map<String,ScheduledFunctionConfiguration> myPullFunctionField = new HashMap<>();

    private final Map<String,String> myPushMethod = new HashMap<>();

    private final Map<String,Object> myStateValue = new HashMap<>();

    private final Object myStateLock = new Object();

    /**
     * State Field Interceptor
     */
    private final StateFieldWithNoDirectAccessInterceptor myStateFieldWithNoDirectAccessInterceptor = new StateFieldWithNoDirectAccessInterceptor();

    private final StateFieldWithDirectAccessInterceptor myStateFieldWithDirectAccessInterceptor = new StateFieldWithDirectAccessInterceptor();

    /**
     * Wisdom Scheduler dependency
     */
    @Requires(specification = ManagedScheduledExecutorService.class,id="scheduler",proxy = false)
    public ManagedScheduledExecutorService scheduler;

    private void extractStateFromInterface(Class interfaz){
        Annotation[] entityTypeAnnotations = interfaz.getDeclaredAnnotationsByType(EntityType.class);
        for(Annotation entityTypeAnnotation : entityTypeAnnotations){
            EntityType cast = (EntityType)entityTypeAnnotation;
            String[] statesInEntityType = cast.states();
            //TODO : Test if state Variable are not already defined
            myStateSpecifications.addAll(Arrays.asList(statesInEntityType));
        }
        for (Class superInterfaz : interfaz.getInterfaces()){
            extractStateFromInterface(superInterfaz);
        }
    }

    @Override
    public void configure(Element element, Dictionary dictionary) throws ConfigurationException {

        myInstanceManager = getInstanceManager();
        myComponentName = myInstanceManager.getInstanceName();

        /**
         * Check if dictionnary contains context entity id
         */
        if (dictionary.get(CONTEXT_ENTITY_ID) == null){
            throw new ConfigurationException("Try to instantiate a context entity without and context.entity.id element");
        }else {
            myStateValue.put(CONTEXT_ENTITY_ID, dictionary.get(CONTEXT_ENTITY_ID));
        }
        /**
         * Introspect Interface Implemented by the component POJO and construct the
         * state specification of the entitytype ( basically a set of state variable)
         */
        Class clazz = getInstanceManager().getClazz();
        Class[] interfaces = clazz.getInterfaces();

        for(Class interfaz : interfaces){
            extractStateFromInterface(interfaz);
        }

        /**
         * Parse the manifest and compare if all the state variable declared in the specification is
         * referenced in the implementation.
         * Construct also the different map of function
         */
        Element[] entityElements = element.getElements(ContextEntityVisitor.CONTEXT_ENTITY_ELEMENT);

        if(entityElements != null) {
            for (String state : myStateSpecifications) {
                for (Element entityElement : entityElements) {
                    Element[] stateVariableElements = entityElement.getElements(StateVariableFieldVisitor.STATE_VARIABLE_ELEMENT);
                    if (stateVariableElements != null) {
                        boolean findInEntity = false;
                        for (Element stateVariableElement : stateVariableElements) {
                            if(stateVariableElement.getAttribute(StateVariableFieldVisitor.STATE_VARIABLE_ATTRIBUTE_NAME) != null) {
                                if (stateVariableElement.getAttribute(StateVariableFieldVisitor.STATE_VARIABLE_ATTRIBUTE_NAME).equals(state)) {
                                    findInEntity = true;

                                    /**
                                     * Retrieve fieldMetadata
                                     */
                                    FieldMetadata fieldMetadata = getPojoMetadata().getField(stateVariableElement.getAttribute(StateVariableFieldVisitor.STATE_VARIABLE_ATTRIBUTE_FIELD));
                                    myStatesFields.put(state, fieldMetadata.getFieldName());

                                    /**
                                     * No Direct Access Case : Register synchro Function
                                     */
                                    if (!Boolean.valueOf(stateVariableElement.getAttribute(StateVariableFieldVisitor.STATE_VARIABLE_ATTRIBUTE_DIRECT_ACCESS))) {
                                        String pullField = stateVariableElement.getAttribute(PullFieldVisitor.STATE_VARIABLE_ATTRIBUTE_PULL);
                                        if (pullField != null) {
                                            /**
                                             * Defaut Config for the moment
                                             */
                                            myPullFunctionField.put(state, new ScheduledFunctionConfiguration(pullField, -1L, TimeUnit.SECONDS));
                                        }

                                        String setField = stateVariableElement.getAttribute(ApplyFieldVisitor.STATE_VARIABLE_ATTRIBUTE_SET);
                                        if (setField != null) {
                                            mySetFunctionField.put(state, setField);
                                        }

                                        String pushMethod = stateVariableElement.getAttribute(ApplyFieldVisitor.STATE_VARIABLE_ATTRIBUTE_SET);
                                        if (pushMethod != null) {
                                            myPushMethod.put(state, pushMethod);
                                            MethodMetadata methodMetadata = getPojoMetadata().getMethod(stateVariableElement.getAttribute(StateVariableFieldVisitor.STATE_VARIABLE_ATTRIBUTE_FIELD));
                                            myInstanceManager.register(methodMetadata, new PushMethodInterceptor(state));
                                        }

                                        /**
                                         * Add a field interceptor for No direct Access State Variable
                                         */

                                        myInstanceManager.register(fieldMetadata, myStateFieldWithNoDirectAccessInterceptor);
                                    } else {
                                        /**
                                         * Add a field interceptor for direct Access State Variable
                                         */

                                        myInstanceManager.register(fieldMetadata, myStateFieldWithDirectAccessInterceptor);

                                    }

                                    /**
                                     * Default Value Affectation
                                     */
                                    String defaultValue = stateVariableElement.getAttribute(StateVariableFieldVisitor.STATE_VARIABLE_ATTRIBUTE_VALUE);

                                    if (defaultValue != null){
                                        /**
                                         * Init always with a string, TODO must introspect type and try to create the appropriate Value
                                         */
                                        myStateValue.put(state, defaultValue);
                                    }
                                    break;
                                }
                            }else {
                                throw new ConfigurationException("Malformed Manifest : a " + StateVariableFieldVisitor.STATE_VARIABLE_ATTRIBUTE_FIELD + " is declared with no " + StateVariableFieldVisitor.STATE_VARIABLE_ATTRIBUTE_NAME + " attribute");
                            }
                        }
                        if (!findInEntity) {
                            throw new ConfigurationException("State variable " + state + " is defined in entityType but never referenced in " + myComponentName);
                        }
                    }else {
                        throw new ConfigurationException("State variable " + state + " is defined in entityType but never referenced in " + myComponentName +" reason : no " + StateVariableFieldVisitor.STATE_VARIABLE_ELEMENT + " element in entity ");
                    }
                }
            }
        } else {
            throw new ConfigurationException("Entity Handler cannot be attached to a component with no " + ContextEntityVisitor.CONTEXT_ENTITY_ELEMENT + " element");
        }

        /**
         * Initialisation value are put in the buffer by default. Can change in future.
         */
        if (dictionary.get("context.entity.init") != null){
            Map<String,Object> initialStateValue = (Map<String, Object>) dictionary.get("context.entity.init");
            for (Map.Entry<String,Object> entry : initialStateValue.entrySet()){
                if (myStateSpecifications.contains(entry.getKey())){
                    /**
                     * Maybe need to check if type is equal
                     */
                    myStateValue.put(entry.getKey(), entry.getValue());
                }else{
                    warn(" State " + entry.getKey() +" is not defined in " + myComponentName + " , so it cannot be used to configured initial value");
                }

            }
        }
    }

    public void onCreation(Object instance) {

        for (Map.Entry<String,String> entry: mySetFunctionField.entrySet()){
            Function setFunction = (Function) myInstanceManager.getFieldValue(entry.getValue());
            mySetFunction.put(entry.getKey(), setFunction);
        }

        for (Map.Entry<String,ScheduledFunctionConfiguration> entry: myPullFunctionField.entrySet()) {
            ScheduledFunctionConfiguration config =  entry.getValue();
            Function pullFunction = (Function) myInstanceManager.getFieldValue(config.getFieldName());
            ScheduledFunction scheduledFunction = new ScheduledPullFunctionImpl(pullFunction,config.getPeriod(),config.getUnit(),entry.getKey(),this);
            myPullFunction.put(entry.getKey(), scheduledFunction);
        }

    }

    @Override
    public synchronized void stop() {
        myProvidedServiceHandler = null;
    }

    @Override
    public synchronized void start() {
        myProvidedServiceHandler = (ProvidedServiceHandler) getHandler(HandlerFactory.IPOJO_NAMESPACE + ":provides");

    }

    @Override
    public synchronized void stateChanged(int state) {

        /**
         * When component become Valid , Entity handler calls all the available pull function
         */
        if (state == InstanceManager.VALID) {

            /**
             * Managed Context Entity Service Exposition
             */
            myController = true;

            /**
             * Initialise with State Default Value
             */
            addStateServiceProperties(new Hashtable<>(myStateValue));


            for (String stateId : myStateSpecifications){
                if (myPullFunction.containsKey(stateId)) {
                    ScheduledFunction getFunction = myPullFunction.get(stateId);
                    Object returnObj = getFunction.apply(stateId);
                    update(stateId,returnObj);

                    if (getFunction.getPeriod() > 0){
                        ManagedScheduledFutureTask futur = scheduler.scheduleAtFixedRate(getFunction, getFunction.getPeriod(), getFunction.getPeriod(), getFunction.getUnit());
                        getFunction.submitted(futur);

                        /**   ManagedFutureTask.SuccessCallback<Object> onSucces =  (ManagedFutureTask<Object> var1, Object var2) -> {
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
                         **/
                    }

                }
            }
        }

        if (state == InstanceManager.INVALID) {

            /**
             * Managed Context Entity Service Exposition
             */
            myController = false;

            for (String stateId : myStateSpecifications){
                if (myPullFunction.containsKey(stateId)) {
                    ScheduledFunction getFunction = myPullFunction.get(stateId);
                    if (getFunction.getPeriod() > 0){
                        getFunction.task().cancel(true);
                        getFunction.submitted(null);
                    }
                }
            }
        }
    }

    /**
     * Management of Property exposed by the service
     *
     */

    private void addStateServiceProperties(Dictionary<String,Object> properties){
        if (myProvidedServiceHandler != null){
            myProvidedServiceHandler.addProperties(properties);
        }
    }
    private void addStateServiceProperty(String propertyId,Object value){
        Hashtable<String,Object> hashtable = new Hashtable();
        hashtable.put(propertyId, value);
        if (myProvidedServiceHandler != null){
            myProvidedServiceHandler.addProperties(hashtable);
        }
    }

    private void updateStateServiceProperty(String propertyId,Object value){
        Hashtable<String,Object> hashtable = new Hashtable();
        hashtable.put(propertyId, value);
        if (myProvidedServiceHandler != null){
            myProvidedServiceHandler.reconfigure(hashtable);
        }
    }

    public void update(String stateId,Object value){
        if (stateId != null) {
            if (value != null) {
                if (myStateSpecifications.contains(stateId)) {
                    synchronized (myStateLock) {
                        if (myStateValue.containsKey(stateId)) {
                            if (!value.equals(myStateValue.get(stateId))) {
                                myStateValue.put(stateId, value);
                                updateStateServiceProperty(stateId, value);
                            }
                        } else {
                            myStateValue.put(stateId, value);
                            addStateServiceProperty(stateId, value);
                        }
                    }
                }
            } else {
                error(" Cannot apply push for " + stateId + ", value is null ! ");
            }
        }
    }

    /**
     * Push Method Interceptor
     */
    private class PushMethodInterceptor implements MethodInterceptor {

        private final String myName;

        PushMethodInterceptor(String name){
            myName = name;
        }

        @Override
        public void onEntry(Object pojo, Member method, Object[] args) {

        }

        @Override
        public void onExit(Object pojo, Member method, Object returnedObj) {
            if (returnedObj != null){
                synchronized (myStateLock) {
                    update(myName,returnedObj);
                }
            }
        }

        @Override
        public void onError(Object pojo, Member method, Throwable throwable) {

        }

        @Override
        public void onFinally(Object pojo, Member method) {

        }
    }

    /**
     * State Field With No Direct Access Interceptor
     */
    private class StateFieldWithNoDirectAccessInterceptor implements FieldInterceptor {

        @Override
        public void onSet(Object pojo, String fieldName, Object value) {
            String state = fieldToStateName(fieldName);
            if(mySetFunction.containsKey(state)){
                Function setFunction = mySetFunction.get(state);
                setFunction.apply(value);
            }

        }

        @Override
        public Object onGet(Object pojo, String fieldName, Object value) {
            Object returnObj = null;
            String state = fieldToStateName(fieldName);
            if (myStateSpecifications.contains(state)){
                if (myPullFunction.containsKey(state)){
                    Function getFunction = myPullFunction.get(state);
                    returnObj = getFunction.apply(state);
                    update(state,returnObj);
                }

                /** Check if have a bufferised value in cas of null**/
                if (returnObj == null){
                    synchronized (myStateLock) {
                        if (myStateValue.containsKey(state)) {
                            returnObj = myStateValue.get(state);
                        }
                    }
                }
            }
            return returnObj;
        }
    }

    /**
     * State Field With Direct Access Interceptor
     */
    private class StateFieldWithDirectAccessInterceptor implements FieldInterceptor {

        @Override
        public void onSet(Object pojo, String fieldName, Object value) {
            String state = fieldToStateName(fieldName);
            if (myStateSpecifications.contains(state)) {
                synchronized (myStateValue) {
                    update(state, value);
                }
            }
        }

        @Override
        public Object onGet(Object pojo, String fieldName, Object value) {
            Object returnObj = null;
            String state = fieldToStateName(fieldName);
            if (myStateSpecifications.contains(state)){
                /** Check if have a bufferised value in cas of null**/
                synchronized (myStateLock) {
                    if (myStateValue.containsKey(state)) {
                        returnObj = myStateValue.get(state);
                    }
                }
            }
            return returnObj;
        }
    }

    private String fieldToStateName(String field){
        for (Map.Entry<String,String> state : myStatesFields.entrySet()){
            if (state.getValue().equals(field)){
                return state.getKey();
            }
        }
        return null;
    }

    /**
     * Utility class to create Scheduled Function
     */
    private class ScheduledFunctionConfiguration {
        private final String myField;

        private final Long myPeriod;

        private final TimeUnit myUnit;

        ScheduledFunctionConfiguration(String field,Long period,TimeUnit unit){
            myField = field;
            myPeriod = period;
            myUnit = unit;
        }

        String getFieldName(){
            return myField;
        }

        Long getPeriod(){
            return myPeriod;
        }

        TimeUnit getUnit(){
            return myUnit;
        }
    }

    /**
     *
     * Context Entity Implementation
     *
     */
    @Override
    public String getId() {
        synchronized (myStateLock){
            return (String) myStateValue.get(CONTEXT_ENTITY_ID);
        }
    }

    @Override
    public Object getStateValue(String property) {
        if (property != null){
            synchronized (myStateLock){
                return myStateValue.get(property);
            }
        }
        return null;
    }

    @Override
    public Set<String> getStates() {
        return new HashSet<>(myStateSpecifications);
    }

    @Override
    public Map<String, Object> dumpState(String property) {
        return new HashMap<>(myStateValue);
    }


    /**
     * HANDLER DESCRIPTION
     */
    @Override
    public HandlerDescription getDescription(){
        return new EntityHandlerDescription(this);
    }

    private class EntityHandlerDescription extends HandlerDescription {
        public EntityHandlerDescription(PrimitiveHandler h) { super(h); }

        // Method returning the custom description of this handler.
        @Override
        public Element getHandlerInfo() {
            // Needed to get the root description element.
            Element elem = super.getHandlerInfo();

            for (String stateId : myStateSpecifications){
                Element stateElement = new Element("State property","");
                stateElement.addAttribute(new Attribute("Name",stateId));
                if(myPullFunction.containsKey(stateId)){
                    Function pull = myPullFunction.get(stateId);
                    Object returnPull = pull.apply(stateId);
                    if (returnPull != null){
                        stateElement.addAttribute(new Attribute("Value",returnPull.toString()));
                        stateElement.addAttribute(new Attribute("Pull function","registered"));
                    }else{
                        stateElement.addAttribute(new Attribute("Value","Value return by Pull Function is null"));
                        stateElement.addAttribute(new Attribute("PullFunction","registered"));
                    }
                }
                else {
                    synchronized (myStateLock) {
                        if (myStateValue.containsKey(stateId)) {
                            stateElement.addAttribute(new Attribute("Value", myStateValue.get(stateId).toString()));
                            stateElement.addAttribute(new Attribute("Pull function", "unregistered"));
                        } else {
                            stateElement.addAttribute(new Attribute("Value", "No Value"));
                            stateElement.addAttribute(new Attribute("Pull function", "unregistered"));
                        }
                    }
                }

                if (mySetFunction.containsKey(stateId)){
                    stateElement.addAttribute(new Attribute("Set function","registered"));
                }
                else {
                    stateElement.addAttribute(new Attribute("Set function","unregistered"));
                }

                elem.addElement(stateElement);

            }

            return elem;
        }
    }
}
