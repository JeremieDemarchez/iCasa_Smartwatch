package fr.liglab.adele.icasa.context.handler.synchronization;

import org.apache.felix.ipojo.*;
import org.apache.felix.ipojo.handlers.providedservice.ProvidedServiceHandler;
import org.apache.felix.ipojo.metadata.Element;
import org.apache.felix.ipojo.parser.FieldMetadata;
import org.apache.felix.ipojo.parser.MethodMetadata;
import org.apache.felix.ipojo.parser.ParseUtils;
import org.apache.felix.ipojo.parser.PojoMetadata;

import java.lang.reflect.Member;
import java.util.*;
import java.util.function.Function;

//TODO : SYnchro on different map access
//TODO : More defensive programming when function of synchro is call
public class SynchronizationHandler extends PrimitiveHandler {

    public static final String SYNCHRONISATION_HANDLER_NAMESPACE = "fr.liglab.adele.icasa.context.handler.synchronization";

    private final SetStateMethodInterceptor m_setStateMethodInterceptor = new SetStateMethodInterceptor();

    private final GetStateMethodInterceptor m_getStateMethodInterceptor = new GetStateMethodInterceptor();

    private final PushStateMethodInterceptor m_pushStateMethodInterceptor = new PushStateMethodInterceptor();

    private final List<String> m_statesId = new ArrayList<>();

    private final Map<String,Function> m_setFunction = new HashMap<>();

    private final Map<String,Function> m_pullFunction = new HashMap<>();

    private final Map<String,Object> m_stateValue = new HashMap<>();

    private ProvidedServiceHandler m_providedServiceHandler;

    private InstanceManager m_instanceManager;

    @Override
    public void configure(Element metadata, Dictionary configuration) throws ConfigurationException {
        m_instanceManager = getInstanceManager();
        PojoMetadata pojoMetadata = getPojoMetadata();

        MethodMetadata[] methodMetadatas = pojoMetadata.getMethods();

        for (MethodMetadata method : methodMetadatas){
            if (method.getMethodName().equals("setState")){
                m_instanceManager.register(method, m_setStateMethodInterceptor);
            }

            if (method.getMethodName().equals("pushState")){
                m_instanceManager.register(method, m_pushStateMethodInterceptor);
            }

            if (method.getMethodName().equals("getStateValue")){
                m_instanceManager.register(method,m_getStateMethodInterceptor);
            }
        }

        FieldMetadata injectedState = pojoMetadata.getField("injectedState");
        m_instanceManager.register(injectedState,this);

        Element[] stateElements = getInstanceManager().getFactory().getComponentMetadata().getElements("State", SYNCHRONISATION_HANDLER_NAMESPACE);
        String[] statesId =  ParseUtils.parseArrays(stateElements[0].getAttribute("states"));
        for (String stateId : statesId){
            m_statesId.add(stateId);
        }


    }

    public void onCreation(Object instance) {
        PojoMetadata pojoMetadata = getPojoMetadata();

        Element[] stateElements = getInstanceManager().getFactory().getComponentMetadata().getElements("State", SYNCHRONISATION_HANDLER_NAMESPACE);

        Element[] setElements = stateElements[0].getElements("Set", SYNCHRONISATION_HANDLER_NAMESPACE);
        if (setElements != null ) {
            for (Element element : setElements) {
                String stateId = element.getAttribute("state");
                FieldMetadata setFunctionField = pojoMetadata.getField(element.getAttribute("field"));
                Function setFunction = (Function) getInstanceManager().getFieldValue(setFunctionField.getFieldName());
                m_setFunction.put(stateId, setFunction);
            }
        }

        Element[] pullElements = stateElements[0].getElements("Pull", SYNCHRONISATION_HANDLER_NAMESPACE);
        if (pullElements !=null) {
            for (Element element : pullElements) {
                String stateId = element.getAttribute("state");
                FieldMetadata setFunctionField = pojoMetadata.getField(element.getAttribute("field"));
                Function pullFunction = (Function) getInstanceManager().getFieldValue(setFunctionField.getFieldName());
                m_pullFunction.put(stateId, pullFunction);
            }
        }
    }

    @Override
    public void stop() {

    }

    @Override
    public void start() {
        m_providedServiceHandler = (ProvidedServiceHandler) getHandler(HandlerFactory.IPOJO_NAMESPACE + ":provides");
    }

    @Override
    public void stateChanged(int state) {
        if (state == InstanceManager.VALID) {
            for (String stateId : m_statesId){
                if (m_pullFunction.containsKey(stateId)){
                    Function getFunction = m_pullFunction.get(stateId);
                    Object returnObj = getFunction.apply(stateId);
                    m_stateValue.put(stateId,returnObj);
                    addState(stateId,returnObj);
                }
            }
        }
    }

    private void addState(String propertyId,Object value){
        Hashtable<String,Object> hashtable = new Hashtable();
        hashtable.put(propertyId, value);
        m_providedServiceHandler.addProperties(hashtable);
    }

    private void removeState(String propertyId){
        Hashtable<String,Object> hashtable = new Hashtable();
        hashtable.put(propertyId, new Object());
        m_providedServiceHandler.removeProperties(hashtable);
    }

    private void updateState(String propertyId,Object value){
        Hashtable<String,Object> hashtable = new Hashtable();
        hashtable.put(propertyId, value);
        m_providedServiceHandler.reconfigure(hashtable);
    }

    public Object onGet(Object pojo, String fieldName, Object value){
        return new HashMap<>(m_stateValue);
    }

    private class SetStateMethodInterceptor implements MethodInterceptor{

        @Override
        public void onEntry(Object pojo, Member method, Object[] args) {
            String stateId = (String)args[0];
            Object value = args[1];

            if(m_setFunction.containsKey(stateId)){
                Function setFunction = m_setFunction.get(stateId);
                setFunction.apply(value);
            }
        }

        @Override
        public void onExit(Object pojo, Member method, Object returnedObj) {

        }

        @Override
        public void onError(Object pojo, Member method, Throwable throwable) {

        }

        @Override
        public void onFinally(Object pojo, Member method) {

        }
    }

    private class GetStateMethodInterceptor implements MethodInterceptor{

        @Override
        public void onEntry(Object pojo, Member method, Object[] args) {
            String stateId = (String)args[0];

            if (m_statesId.contains(stateId)){
                if (m_pullFunction.containsKey(stateId)){
                    Function getFunction = m_pullFunction.get(stateId);
                    Object returnObj = getFunction.apply(stateId);
                    if (m_stateValue.containsKey(stateId)) {
                        if (returnObj.equals(m_stateValue.get(stateId))) {

                        } else {
                            m_stateValue.replace(stateId,returnObj);
                            updateState(stateId,returnObj);
                        }
                    }
                }
            }
        }

        @Override
        public void onExit(Object pojo, Member method, Object returnedObj) {

        }

        @Override
        public void onError(Object pojo, Member method, Throwable throwable) {

        }

        @Override
        public void onFinally(Object pojo, Member method) {

        }
    }

    private class PushStateMethodInterceptor implements MethodInterceptor{

        @Override
        public void onEntry(Object pojo, Member method, Object[] args) {
            String stateId = (String)args[0];
            Object value = args[1];

            if (m_statesId.contains(stateId)){
                if (m_stateValue.containsKey(stateId)){
                    m_stateValue.put(stateId,value);
                    updateState(stateId,value);
                }else {
                    m_stateValue.put(stateId,value);
                    addState(stateId,value);
                }
            }
        }

        @Override
        public void onExit(Object pojo, Member method, Object returnedObj) {

        }

        @Override
        public void onError(Object pojo, Member method, Throwable throwable) {

        }

        @Override
        public void onFinally(Object pojo, Member method) {

        }
    }
}
