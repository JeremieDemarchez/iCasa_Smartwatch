(function(){var a,b,c;
a=typeof global!=="undefined"&&global!==null?global:this;
a.HUBU=(b=a.HUBU)!=null?b:{};
a.HUBU.extensions=(c=a.HUBU.extensions)!=null?c:{};
a.hubu=function(){return a.HUBU
};
a.getHubuExtensions=function(){return HUBU.extensions
};
a.getGlobal=function(){return a
}
}).call(this);
(function(){var a;
HUBU.AbstractComponent=a=(function(){function b(){}b.prototype.configure=function(c,d){throw"AbstractComponent is an abstract class"
};
b.prototype.start=function(){throw"AbstractComponent is an abstract class"
};
b.prototype.stop=function(){throw"AbstractComponent is an abstract class"
};
b.prototype.getComponentName=function(){throw"AbstractComponent is an abstract class"
};
return b
})()
}).call(this);
(function(){var d,b,c,a,e;
var f=Array.prototype.slice;
HUBU.UTILS=(e=HUBU.UTILS)!=null?e:{};
a=HUBU.UTILS;
getGlobal().namespace=function(l,g,j){var o,m,k,n,i,h;
if(arguments.length<3){i=[(typeof exports!=="undefined"?exports:window)].concat(f.call(arguments)),l=i[0],g=i[1],j=i[2]
}m=l;
h=g.split(".");
for(k=0,n=h.length;
k<n;
k++){o=h[k];
l=l[o]||(l[o]={})
}return j(l,m)
};
getGlobal().Logger=b=(function(){g.DEBUG=0;
g.INFO=1;
g.WARNING=2;
g.ERROR=3;
g.prototype._header="";
g.prototype._level=g.INFO;
function g(h){var i;
if(h==null){h=""
}if(h.length>0){i="["+h+"] "
}}g.prototype._getConsole=function(){if(((typeof window!=="undefined"&&window!==null?window.console:void 0)!=null)){return window.console
}if(((typeof global!=="undefined"&&global!==null?global.console:void 0)!=null)){return global.console
}return null
};
g.prototype.log=function(h){if((this._getConsole()!=null)){this._getConsole().log((""+this._header)+h);
return true
}return false
};
g.prototype.debug=function(h){if(this._level<=g.DEBUG){return this.log("DEBUG - "+h)
}return false
};
g.prototype.info=function(h){if(this._level<=g.INFO){return this.log("INFO - "+h)
}return false
};
g.prototype.warn=function(h){if(this._level<=g.WARNING){return this.log("WARN - "+h)
}return false
};
g.prototype.error=function(h){if(this._level<=g.ERROR){return log("ERROR - "+h)
}};
g.prototype.setLevel=function(h){return this._level=h
};
return g
})();
HUBU.logger=new b("hubu");
c=HUBU.logger;
getGlobal().Exception=d=(function(){g.prototype.data={};
function g(h){this.message=h
}g.prototype.add=function(h,i){this.data.key=i;
return this
};
g.prototype.toString=function(){return this.message
};
return g
})();
a.typeOf=function(l){var k,i,h,j,g,m;
if(!(l!=null)){return String(l)
}k=new Object;
m="Boolean Number String Function Array Date RegExp".split(" ");
for(j=0,g=m.length;
j<g;
j++){h=m[j];
k["[object "+h+"]"]=h.toLowerCase()
}i=Object.prototype.toString.call(l);
if(i in k){return k[i]
}return"object"
};
a.isObjectConformToContract=function(g,i){var h;
for(h in i){if(!(g[h]!=null)){c.warn("Object not conform to contract - property "+h+" missing");
return false
}else{if(this.typeOf(i[h])!==(this.typeOf(g[h]))){c.warn(("Object not conform to contract - the type of the property "+h+" does not match.          Expected '")+this.typeOf(i[h])+"' but found '"+this.typeOf(g[h])+"'");
return false
}}}return true
};
a.isFunction=function(g){return this.typeOf(g)==="function"
};
a.isObject=function(g){return this.typeOf(g)==="object"
};
a.invoke=function(h,i,g){if((h[i]!=null)&&this.isFunction(h[i])){return h[i].apply(h,g)
}return false
};
a.defineFunctionIfNotExist=function(i,g,h){if(!(i[g]!=null)){i[g]=h;
return true
}return false
};
a.clone=function(j,k){var g,i,h;
if(!(j!=null)||typeof j!=="object"){return j
}if(j instanceof Date){return new Date(j.getTime())
}if(j instanceof RegExp){g="";
if(j.global!=null){g+="g"
}if(j.ignoreCase!=null){g+="i"
}if(j.multiline!=null){g+="m"
}if(j.sticky!=null){g+="y"
}return new RegExp(j.source,g)
}h=new j.constructor();
k=k!=null?k:[];
for(i in j){if(this.indexOf(k,i)===-1){h[i]=this.clone(j[i],k)
}}return h
};
a.bind=function(g,h){return function(){return h.apply(g,Array.prototype.slice.call(arguments))
}
};
a.createProxyForContract=function(j,g){var i,h;
h={};
h.__proxy__=g;
for(i in j){if(this.isFunction(j[i])){h[i]=this.bind(g,g[i])
}else{h[i]=g[i]
}}return h
};
a.isComponent=function(g){if(!(g!=null)){return false
}return this.isObjectConformToContract(g,new HUBU.AbstractComponent())
};
a.isComponentPlugged=function(g,h){if(this.typeOf(g)==="string"){return h.getComponent(g)!==null
}if(this.typeOf(g)==="object"){return this.indexOf(h.getComponents(),g)!==-1
}return false
};
a.indexOf=function(i,h){var g;
if((Array.prototype.indexOf!=null)){return i.indexOf(h)
}else{for(g in i){if(i.v===h){return g
}}return -1
}};
a.removeElementFromArray=function(i,h){var g;
for(g in i){if(i[g]===h){i.splice(g,1)
}}return i
}
}).call(this);
(function(){var b;
var a=Array.prototype.slice;
HUBU.Eventing=b=(function(){c.prototype._hub=null;
c.prototype._listeners=null;
function c(d){var e;
this._hub=d;
this._listeners=[];
e=this;
this._hub.getListeners=function(){return e._listeners
};
this._hub.registerListener=function(){var g,f;
g=arguments[0],f=2<=arguments.length?a.call(arguments,1):[];
if(f.length>=2){e.registerListener(g,f[0],f[1]);
return this
}else{e.registerListener(g,f[1]);
return this
}};
this._hub.registerConfigurableListener=function(g,f){HUBU.logger.warn("registerConfigurableListener is a deprecated method and may disappear at any time, use registerListener instead");
e.registerListener(g,f);
return this
};
this._hub.unregisterListener=function(f,g){e.unregisterListener(f,g);
return this
};
this._hub.sendEvent=function(f,g){return e.sendEvent(f,g)
};
this._hub.subscribe=function(g,f,i,h){e.subscribe(g,f,i,h);
return this
};
this._hub.unsubscribe=function(f,g){e.unsubscribe(f,g);
return this
};
this._hub.publish=function(g,f,h){e.publish(g,f,h);
return this
}
}c.prototype._processEvent=function(f,h){var g,k,e,j,d,i;
if(!(h!=null)||!(f!=null)){HUBU.logger.warn("Can't process event - component or event not defined");
return false
}e=false;
i=this._listeners;
for(j=0,d=i.length;
j<d;
j++){k=i[j];
if(k.component!==f){g=HUBU.UTILS.clone(h);
g.source=f;
if(k.match.apply(k.component,[g])){k.callback.apply(k.component,[g]);
e=true
}}}return e
};
c.prototype.registerListener=function(){var h,e,g,d,f;
e=arguments[0],f=2<=arguments.length?a.call(arguments,1):[];
d=null;
h=null;
switch(f.length){case 2:d=f[0];
h=f[1];
break;
case 1:d=f[0].match;
h=f[0].callback
}if(!(e!=null)||!(d!=null)||!(h!=null)){throw new Exception("Cannot register event listener, component or match or callback is/are not defined").add("component",e).add("match",d).add("callback",h)
}if(!HUBU.UTILS.isComponentPlugged(e,this._hub)){throw new Exception("Cannot register event listener, the component is not plugged on the hub")
}g={component:e,callback:h,match:d};
return this._listeners.push(g)
};
c.prototype.unregisterListener=function(o,q){var n,g,f,l,k,i,p,e,d,m,h,j;
if(!(o!=null)){HUBU.logger.warn("Cannot unregister listener - component not defined");
return false
}n=this.getComponent(o);
if(!(n!=null)){HUBU.logger.warn("Cannot unregister listener - component not plugged on the hub");
return false
}f=[];
if((q!=null)){m=this._listeners;
for(l=0,p=m.length;
l<p;
l++){g=m[l];
if(g.component===n&&g.callback===q){f.push(g)
}}}else{h=this._listeners;
for(k=0,e=h.length;
k<e;
k++){g=h[k];
if(g.component===n){f.push(g)
}}}j=[];
for(i=0,d=f.length;
i<d;
i++){g=f[i];
j.push(this._listeners=HUBU.UTILS.removeElementFromArray(this._listeners,g))
}return j
};
c.prototype.getComponent=function(d){if(HUBU.UTILS.typeOf(d)==="string"){return this._hub.getComponent(d)
}if(HUBU.UTILS.isComponent(d)){return d
}return null
};
c.prototype.sendEvent=function(d,e){if(!(d!=null)||!(e!=null)){HUBU.logger.warn("Cannot send event, component or/and event are undefined");
return
}return this._processEvent(d,e)
};
c.prototype.subscribe=function(f,e,i,g){var d,h;
if(!(f!=null)||!(e!=null)||!(i!=null)){HUBU.logger.warn("Cannot subscribe to topic, component or/and topic and/or callback are undefined");
return
}h=new RegExp(e);
d=null;
if(!(g!=null)||!HUBU.UTILS.isFunction(g)){d=function(j){return h.test(j.topic)
}
}else{d=function(j){return h.test(j.topic)&&g(j)
}
}return this.registerListener(f,d,i)
};
c.prototype.unsubscribe=function(d,e){return this.unregisterListener(d,e)
};
c.prototype.publish=function(e,d,f){if(!(e!=null)||!(d!=null)||!(f!=null)){HUBU.logger.info("Cannot publish event - component and/or topic and/or event are missing");
return false
}f.topic=d;
return this.sendEvent(e,f)
};
c.prototype.reset=function(){return this._listeners=[]
};
c.prototype.unregisterComponent=function(d){return this.unregisterListener(d)
};
return c
})();
getHubuExtensions().eventing=b
}).call(this);
(function(){var a;
HUBU.Binding=a=(function(){b.prototype._hub=null;
function b(c){var d;
this._hub=c;
d=this;
this._hub.bind=function(e){d.bind(e);
return this
}
}b.prototype.getComponent=function(d){var c;
c=null;
if(HUBU.UTILS.typeOf(d)==="string"){return this._hub.getComponent(d)
}if(HUBU.UTILS.isComponent(d)){return d
}return null
};
b.prototype.getInjectedObject=function(d,c){if(d.contract!=null){if(!HUBU.UTILS.isObjectConformToContract(c,d.contract)){throw new Exception("Cannot bind components, the component is not conform to contract").add("component",c.getComponentName()).add("contract",d.contract)
}else{if(!(d.proxy!=null)||d.proxy){return HUBU.UTILS.createProxyForContract(d.contract,c)
}}}return c
};
b.prototype.bind=function(d){var c,e;
if(!(d!=null)||!(d!=null?d.to:void 0)||!(d!=null?d.component:void 0)||!(d!=null?d.into:void 0)){throw new Exception("Cannot bind components - component, to and into must be defined")
}c=this.getComponent(d.component);
if(!(c!=null)){throw new Exception("Cannot bind components - 'component' is invalid").add("component",d.component)
}e=this.getComponent(d.to);
if(!(e!=null)){throw new Exception("Cannot bind components - 'to' is invalid").add("component",d.to)
}c=this.getInjectedObject(d,c);
switch(HUBU.UTILS.typeOf(d.into)){case"function":return d.into.apply(e,[c]);
case"string":if(!(e[d.into]!=null)){return e[d.into]=c
}else{if(HUBU.UTILS.isFunction(e[d.into])){return e[d.into].apply(e,[c])
}else{return e[d.into]=c
}}break;
default:throw new Exception("Cannot bind components = 'into' must be either a function or a string").add("into",d.into)
}};
return b
})();
getHubuExtensions().binding=a
}).call(this);
(function(){var a,f,g,b,d,e;
var c=function(h,i){return function(){return h.apply(i,arguments)
}
};
getGlobal().SOC=(e=getGlobal().SOC)!=null?e:{};
a=getGlobal().SOC;
a.ServiceRegistration=b=(function(){h._nextId=1;
h.prototype._id=-1;
h.prototype._component=null;
h.prototype._contract=null;
h.prototype._hub=null;
h.prototype._registered=false;
h.prototype._properties={};
h.prototype._reference=null;
h.prototype._registry=null;
h.prototype._svcObject=null;
h.getAndIncId=function(){var i;
i=a.ServiceRegistration._nextId;
a.ServiceRegistration._nextId=a.ServiceRegistration._nextId+1;
return i
};
function h(n,k,j,l,m,i){this._id=-1;
if(!(k!=null)){throw new Exception("Cannot create a service registration without a valid component")
}if(!(j!=null)){throw new Exception("Cannot create a service registration without a valid service object")
}if(!(n!=null)){throw new Exception("Cannot create a service registration without a contract")
}if(!(m!=null)){throw new Exception("Cannot create a service registration without the hub")
}if(!(i!=null)){throw new Exception("Cannot create a service registration without the registry")
}this._component=k;
this._hub=m;
this._contract=n;
this._properties=l!=null?l:{};
this._registry=i;
this._svcObject=j;
this._properties["service.contract"]=this._contract;
this._properties["service.publisher"]=this._component
}h.prototype.register=function(){if(!(HUBU.UTILS.isComponentPlugged(this._component,this._hub)||this._component===this._hub)){throw new Exception("Invalid registration, the component is not plugged on the hub")
}this._id=a.ServiceRegistration.getAndIncId();
this._reference=new a.ServiceReference(this);
this._properties["service.id"]=this._id;
this._registered=this._id!==-1;
return this._id
};
h.prototype.unregister=function(){return this._registered=false
};
h.prototype.isRegistered=function(){return this._registered
};
h.prototype.getReference=function(){if(!(HUBU.UTILS.isComponentPlugged(this._component,this._hub)||this._component===this._hub)){throw new Exception("Invalid lookup, the component is not plugged on the hub")
}return this._reference
};
h.prototype.getProperties=function(){return this._properties
};
h.prototype.getService=function(i){if(!HUBU.UTILS.isFunction(this._svcObject)){return this._svcObject
}return this._svcObject.apply(this._component,[i])
};
h.prototype.setProperties=function(j){var l,i,k;
i=null;
if(this.isRegistered()){k=HUBU.UTILS.clone(this._properties,["service.contract","service.publisher"]);
i=new a.ServiceRegistration(this._contract,this._component,this._svcObject,k,this._hub,this._registry);
i._id=this._id;
i._reference=new a.ServiceReference(i)
}this._properties=j!=null?j:{};
this._properties["service.contract"]=this._contract;
this._properties["service.publisher"]=this._component;
this._properties["service.id"]=this._id;
if(this.isRegistered()&&(i!=null)){l=new a.ServiceEvent(a.ServiceEvent.MODIFIED,this.getReference());
return this._registry.fireServiceEvent(l,i.getReference())
}};
return h
})();
a.ServiceReference=g=(function(){h.prototype._registration=null;
function h(i){this._registration=i
}h.prototype.getContract=function(){return this._registration.getProperties()["service.contract"]
};
h.prototype.getProperties=function(){return this._registration.getProperties()
};
h.prototype.getProperty=function(i){return this._registration.getProperties()[i]
};
h.prototype.getId=function(){return this._registration.getProperties()["service.id"]
};
h.prototype.isValid=function(){return this._registration.isRegistered
};
return h
})();
a.ServiceEvent=f=(function(){h.REGISTERED=1;
h.MODIFIED=2;
h.UNREGISTERING=4;
h.MODIFIED_ENDMATCH=8;
h.prototype._type=0;
h.prototype._reference=null;
function h(i,j){this._type=i;
this._reference=j
}h.prototype.getReference=function(){return this._reference
};
h.prototype.getType=function(){return this._type
};
return h
})();
a.ServiceRegistry=d=(function(){h.prototype._registrations=null;
h.prototype._hub=null;
h.prototype._listeners=null;
function h(i){this._registrations=[];
this._listeners=[];
if(!(i!=null)){throw new Exception("Cannot initialize the service registry without a hub")
}this._hub=i
}h.prototype.getRegisteredServices=function(){var p,j,q,n,m,o,i,l,k;
q=[];
l=this._registrations;
for(n=0,o=l.length;
n<o;
n++){p=l[n];
k=p.registrations;
for(m=0,i=k.length;
m<i;
m++){j=k[m];
q.push(j.getReference())
}}return q
};
h.prototype._addRegistration=function(j,k){var n,l,m,i,o;
o=this._registrations;
for(m=0,i=o.length;
m<i;
m++){l=o[m];
if(l.component===j){n=l
}}if(!(n!=null)){n={component:j,registrations:[]};
this._registrations.push(n)
}return n.registrations.push(k)
};
h.prototype._removeRegistration=function(j){var m,k,l,i,n;
n=this._registrations;
for(l=0,i=n.length;
l<i;
l++){k=n[l];
if(HUBU.UTILS.indexOf(k.registrations,j)!==-1){m=k
}}if(!(m!=null)){return null
}HUBU.UTILS.removeElementFromArray(m.registrations,j);
if(m.registrations.length===0){HUBU.UTILS.removeElementFromArray(this._registrations,m)
}return m.component
};
h.prototype.registerService=function(j,m,k,i){var l;
if(!(m!=null)){throw new Exception("Cannot register a service without a proper contract")
}if(!(j!=null)){throw new Exception("Cannot register a service without a valid component")
}i=i!=null?i:j;
if(!HUBU.UTILS.isFunction(i)&&!HUBU.UTILS.isObjectConformToContract(i,m)){throw new Exception("Cannot register service - the service object does not implement the contract").add("contract",m).add("component",j)
}i=i!=null?i:j;
l=new b(m,j,i,k,this._hub,this);
this._addRegistration(j,l);
l.register();
this.fireServiceEvent(new a.ServiceEvent(a.ServiceEvent.REGISTERED,l.getReference()));
return l
};
h.prototype.unregisterService=function(j){var i,k;
if(!(j!=null)){throw new Exception("Cannot unregister the service - invalid registration")
}i=this._removeRegistration(j);
if((i!=null)){k=j.getReference();
j.unregister();
this.fireServiceEvent(new a.ServiceEvent(a.ServiceEvent.UNREGISTERING,k));
return true
}throw new Exception("Cannot unregister service - registration not found")
};
h.prototype.unregisterServices=function(p){var k,r,j,o,n,m,q,i,l;
if(!(p!=null)){throw new Exception("Cannot unregister the services - invalid component")
}l=this._registrations;
for(n=0,q=l.length;
n<q;
n++){r=l[n];
if(r.component===p){k=r
}}if(k!=null){o=k.registrations;
if(o!=null){for(m=0,i=o.length;
m<i;
m++){j=o[m];
this.unregisterService(j)
}}return HUBU.UTILS.removeElementFromArray(this._registrations,k)
}};
h.prototype.getServiceReferences=function(j,i){return this._match(this._buildFilter(j,i))
};
h.prototype._match=function(j){var l,k,i;
i=this.getRegisteredServices();
l=(function(){var o,n,m;
m=[];
for(o=0,n=i.length;
o<n;
o++){k=i[o];
if(j.match(k)){m.push(k)
}}return m
})();
return l
};
h.prototype._buildFilter=function(k,j){var i;
if(!(k!=null)&&!(j!=null)){return{match:function(l){return true
}}
}else{if((k!=null)&&!(j!=null)){i={};
i.contract=k;
i.match=c(function(l){return l.getProperty("service.contract")===i.contract
},this);
return i
}else{if((k!=null)&&(j!=null)){i={};
i.contract=k;
i.filter=j;
i.match=c(function(l){return(l.getProperty("service.contract")===i.contract)&&i.filter(l)
},this);
return i
}else{return{filter:j,match:function(l){return this.filter(l)
}}
}}}};
h.prototype.getService=function(i,j){if(!(j!=null)){throw new Exception("Cannot get service - the reference is null")
}if(!j.isValid()){HUBU.logger.warn("Cannot retrieve service for "+j+" - the reference is invalid");
return null
}return j._registration.getService(i)
};
h.prototype.ungetService=function(i,j){};
h.prototype.registerServiceListener=function(j){var m,k,n,l,i;
m=j.contract,k=j.filter,n=j.listener;
if(!(n!=null)){throw new Exception("Can't register the service listener, the listener is not set").add("listenerConfig",j)
}l=this._buildFilter(m,k);
i={listener:n,filter:l,contract:m};
if(HUBU.UTILS.isObject(n)){if(!HUBU.UTILS.isObjectConformToContract(n,a.ServiceListener)){throw new Exception("Can't register the service listener, the listener is not conform to the Service Listener contract")
}}return this._listeners.push(i)
};
h.prototype.unregisterServiceListener=function(q){var p,i,n,j,m,o,k,l;
p=q.contract,i=q.filter,j=q.listener;
if(!(j!=null)){throw new Exception("Can't unregister the service listener, the listener is not set").add("listenerConfig",q)
}k=this._listeners;
l=[];
for(m=0,o=k.length;
m<o;
m++){n=k[m];
l.push(n.contract===p&&n.listener===j?HUBU.UTILS.removeElementFromArray(this._listeners,n):void 0)
}return l
};
h.prototype.fireServiceEvent=function(i,q){var k,j,n,o,p,l,m;
l=this._listeners;
m=[];
for(o=0,p=l.length;
o<p;
o++){k=l[o];
j=!(k.filter!=null)||this._testAgainstFilter(k,i.getReference());
m.push(j?this._invokeServiceListener(k,i):i.getType()===a.ServiceEvent.MODIFIED&&(q!=null)?this._testAgainstFilter(k,q)?(n=new a.ServiceEvent(a.ServiceEvent.MODIFIED_ENDMATCH,i.getReference()),this._invokeServiceListener(k,n)):void 0:void 0)
}return m
};
h.prototype._testAgainstFilter=function(j,i){return j.filter.match(i)
};
h.prototype._invokeServiceListener=function(j,i){if(HUBU.UTILS.isFunction(j.listener)){return j.listener(i)
}else{if(HUBU.UTILS.isObject(j.listener)){return j.serviceChanged(i)
}}};
return h
})()
}).call(this);
(function(){var b,a,d,c;
HUBU.ServiceComponent=a=(function(){e.STOPPED=0;
e.INVALID=1;
e.VALID=2;
e.prototype._component=null;
e.prototype._providedServices=null;
e.prototype._requiredServices=null;
e.prototype._state=0;
function e(f){this._component=f;
this._providedServices=[];
this._requiredServices=[];
this._state=e.STOPPED
}e.prototype.getComponent=function(){return this._component
};
e.prototype.getState=function(){return this._state
};
e.prototype.addProvidedService=function(f){if(HUBU.UTILS.indexOf(this._providedServices,f)===-1){this._providedServices.push(f);
f.setServiceComponent(this);
if(this._state>e.STOPPED){f.onStart()
}if(this._state===e.VALID){f.onValidation()
}if(this._state===e.INVALID){return f.onInvalidation()
}}};
e.prototype.removeProvidedService=function(f){if(HUBU.UTILS.indexOf(this._providedServices,f)!==-1){HUBU.UTILS.removeElementFromArray(this._providedServices,f);
return f.onStop()
}};
e.prototype.addRequiredService=function(f){if(HUBU.UTILS.indexOf(this._requiredServices,f)===-1){this._requiredServices.push(f);
f.setServiceComponent(this);
if(this._state>e.STOPPED){f.onStart();
return this.computeState()
}}};
e.prototype.removeRequireService=function(f){if(HUBU.UTILS.indexOf(this._requiredServices,f)>-1){HUBU.UTILS.removeElementFromArray(this._requiredServices,f);
f.onStop();
if(this._state>e.STOPPED){return this.computeState()
}}};
e.prototype.computeState=function(){var k,g,h,j,f,i;
k=true;
i=this._requiredServices;
for(j=0,f=i.length;
j<f;
j++){h=i[j];
k=k&&h.isValid()
}g=this._state;
this._state=k?e.VALID:e.INVALID;
if(this._state>g&&this._state===e.VALID){this._validate()
}else{if(this._state<g&&this._state===e.INVALID){this._invalidate()
}}return this._state
};
e.prototype._validate=function(){var h,j,g,i,k,f;
HUBU.logger.debug("Validate instance "+((i=this._component)!=null?i.getComponentName():void 0));
k=this._providedServices;
f=[];
for(j=0,g=k.length;
j<g;
j++){h=k[j];
f.push(h.onValidation())
}return f
};
e.prototype._invalidate=function(){var h,j,g,i,f;
HUBU.logger.debug("Invalidate instance");
i=this._providedServices;
f=[];
for(j=0,g=i.length;
j<g;
j++){h=i[j];
f.push(h.onInvalidation())
}return f
};
e.prototype.onStart=function(){var h,j,l,i,g,f,k,m;
k=this._requiredServices;
for(l=0,g=k.length;
l<g;
l++){j=k[l];
j.onStart()
}m=this._providedServices;
for(i=0,f=m.length;
i<f;
i++){h=m[i];
h.onStart()
}return this.computeState()
};
e.prototype.onStop=function(){var h,j,l,i,g,f,k,m;
k=this._providedServices;
for(l=0,g=k.length;
l<g;
l++){h=k[l];
h.onStop()
}m=this._requiredServices;
for(i=0,f=m.length;
i<f;
i++){j=m[i];
j.onStop()
}return this._state=e.STOPPED
};
return e
})();
HUBU.ServiceDependency=d=(function(){var h,e,g,f;
i.UNRESOLVED=0;
i.RESOLVED=1;
i.prototype._component=null;
i.prototype._contract=null;
i.prototype._filter=null;
i.prototype._aggregate=false;
i.prototype._optional=false;
i.prototype._field=null;
i.prototype._bind=null;
i.prototype._unbind=null;
i.prototype._hub=null;
h=null;
f=null;
e=[];
g=null;
function i(o,q,j,k,n,p,m,s,l){var r;
this._component=o;
this._contract=q;
this._filter=j;
this._aggregate=k;
this._optional=n;
this._field=p;
if(m!=null){this._bind=HUBU.UTILS.isFunction(m)?m:this._component[m];
if(!(this._bind!=null)){throw new Exception("Bind method "+m+" not found on component")
}}if(s!=null){this._unbind=HUBU.UTILS.isFunction(s)?s:this._component[s];
if(!(this._unbind!=null)){throw new Exception("Unbind method "+s+" not found on component")
}}this._hub=l;
this._state=HUBU.ServiceDependency.UNRESOLVED;
this._refs=[];
r=this;
this._listener={contract:this._contract,filter:function(t){return t.getProperty("service.publisher")!==r._component&&(!(r._filter!=null)||r._filter(t))
},listener:function(t){switch(t.getType()){case SOC.ServiceEvent.REGISTERED:return r._onServiceArrival(t.getReference());
case SOC.ServiceEvent.MODIFIED:return r._onServiceModified(t.getReference());
case SOC.ServiceEvent.UNREGISTERING:return r._onServiceDeparture(t.getReference());
case SOC.ServiceEvent.MODIFIED_ENDMATCH:return r._onServiceDeparture(t.getReference())
}}}
}i.prototype.setServiceComponent=function(j){return this._serviceComponent=j
};
i.prototype.onStart=function(){this._state=HUBU.ServiceDependency.UNRESOLVED;
this._startTracking();
return this._computeDependencyState()
};
i.prototype.onStop=function(){this._stopTracking();
this._ungetAllServices();
this._refs=[];
return this._state=HUBU.ServiceDependency.UNRESOLVED
};
i.prototype._ungetAllServices=function(){var l,n,k,m,j;
m=this._refs;
j=[];
for(n=0,k=m.length;
n<k;
n++){l=m[n];
if(l.service!=null){l.service=null;
j.push(this._hub.ungetService(this._component,l.reference))
}}return j
};
i.prototype._startTracking=function(){var m,l,n,k,j;
this._hub.registerServiceListener(this._listener);
l=this._hub.getServiceReferences(this._contract,this._filter);
j=[];
for(n=0,k=l.length;
n<k;
n++){m=l[n];
j.push(this._onServiceArrival(m))
}return j
};
i.prototype._stopTracking=function(){return this._hub.unregisterServiceListener(this._listener)
};
i.prototype.isValid=function(){return this._state===HUBU.ServiceDependency.RESOLVED
};
i.prototype._computeDependencyState=function(){var j;
j=this._state;
if(this._optional||this._refs.length>0){this._state=HUBU.ServiceDependency.RESOLVED
}else{this._state=HUBU.ServiceDependency.UNRESOLVED
}if(j!==this._state){return this._serviceComponent.computeState()
}};
i.prototype._onServiceArrival=function(m){var l,j,o,k,n;
HUBU.logger.debug("Service arrival detected for "+this._component.getComponentName());
n=this._refs;
for(o=0,k=n.length;
o<k;
o++){l=n[o];
if(l.reference===m){j=l
}}if(!(j!=null)){j={reference:m,service:null};
this._refs.push(j);
this._computeDependencyState();
if(this._aggregate){return this._inject(j)
}else{if(this._refs.length===1){return this._inject(j)
}}}};
i.prototype._onServiceDeparture=function(n){var m,k,j,p,l,o;
HUBU.logger.debug("Service departure detected for "+this._component.getComponentName());
o=this._refs;
for(p=0,l=o.length;
p<l;
p++){m=o[p];
if(m.reference===n){j=m
}}if(j!=null){HUBU.UTILS.removeElementFromArray(this._refs,j);
if(j.service!=null){this._deinject(j);
this._hub.ungetService(this._component,n);
j.service=null
}if(this._refs.length>0){k=this._refs[0];
if(!this._aggregate){return this._inject(k)
}}else{return this._computeDependencyState()
}}};
i.prototype._onServiceModified=function(m){var l,j,o,k,n;
n=this._refs;
for(o=0,k=n.length;
o<k;
o++){l=n[o];
if(l.reference===m){j=l
}}if(!(j!=null)){return this._onServiceArrival(m)
}};
i.prototype._inject=function(k){var j;
j=this._hub.getService(this._serviceComponent,k.reference);
k.service=j;
if((this._field!=null)&&this._aggregate){if(!(this._component[this._field]!=null)){this._component[this._field]=[j]
}else{this._component[this._field].push(j)
}}if((this._field!=null)&&!this._aggregate){this._component[this._field]=j
}if(this._bind!=null){return this._bind.apply(this._component,[j,k.reference])
}};
i.prototype._deinject=function(j){if((this._field!=null)&&this._aggregate){HUBU.UTILS.removeElementFromArray(this._component[this._field],j.service)
}if((this._field!=null)&&!this._aggregate){this._component[this._field]=null
}if(this._unbind!=null){return this._unbind.apply(this._component,[j.service,j.reference])
}};
return i
})();
HUBU.ProvidedService=b=(function(){e.UNREGISTERED=0;
e.REGISTERED=1;
e.prototype._hub=null;
e.prototype._contract=null;
e.prototype._properties=null;
e.prototype._registration=null;
e.prototype._serviceComponent=null;
e.prototype._component=null;
e.prototype._preRegistration=null;
e.prototype._postRegistration=null;
e.prototype._preUnregistration=null;
e.prototype._postUnRegistration=null;
function e(f,j,g,i,m,k,l,h){this._component=f;
this._contract=j;
this._hub=h;
this._properties=g;
if(i!=null){this._preRegistration=HUBU.UTILS.isFunction(i)?i:this._component[i];
if(!(this._preRegistration!=null)){throw new Exception("preRegistration method "+i+" not found on component")
}}if(m!=null){this._postRegistration=HUBU.UTILS.isFunction(m)?m:this._component[m];
if(!(this._postRegistration!=null)){throw new Exception("postRegistration method "+m+" not found on component")
}}if(k!=null){this._preUnregistration=HUBU.UTILS.isFunction(k)?k:this._component[k];
if(!(this._preUnregistration!=null)){throw new Exception("preUnregistration method "+k+" not found on component")
}}if(l!=null){this._postUnRegistration=HUBU.UTILS.isFunction(l)?l:this._component[l];
if(!(this._postUnRegistration!=null)){throw new Exception("postUnregistration method "+l+" not found on component")
}}}e.prototype.setServiceComponent=function(f){return this._serviceComponent=f
};
e.prototype._register=function(){var f;
if(this._registration!=null){return false
}if((this._preRegistration!=null)){this._preRegistration.apply(this._component,[])
}f=HUBU.UTILS.createProxyForContract(this._contract,this._component);
this._registration=this._hub.registerService(this._component,this._contract,this._properties,f);
HUBU.logger.debug("Service from "+this._component.getComponentName()+" registered");
if((this._postRegistration!=null)){this._postRegistration.apply(this._component,[this._registration])
}return true
};
e.prototype._unregister=function(){if(!(this._registration!=null)){return false
}if(this._preUnregistration!=null){this._preUnregistration.apply(this._component,[this._registration])
}this._hub.unregisterService(this._registration);
this._registration=null;
if(this._postUnRegistration!=null){return this._postUnRegistration.apply(this._component,[])
}};
e.prototype.onStart=function(){};
e.prototype.onStop=function(){return this._unregister()
};
e.prototype.onValidation=function(){return this._register()
};
e.prototype.onInvalidation=function(){return this._unregister()
};
return e
})();
HUBU.ServiceOrientation=c=(function(){e.prototype._hub=null;
e.prototype._registry=null;
e.prototype._components=[];
function e(h){var g,f;
this._hub=h;
this._registry=new SOC.ServiceRegistry(this._hub);
this._components=[];
g=this._registry;
f=this;
this._hub.getServiceRegistry=function(){return g
};
this._hub.registerService=function(j,l,k,i){return g.registerService(j,l,k,i)
};
this._hub.unregisterService=function(i){return g.unregisterService(i)
};
this._hub.getServiceReferences=function(j,i){return g.getServiceReferences(j,i)
};
this._hub.getServiceReference=function(k,j){var i;
i=g.getServiceReferences(k,j);
if(i.length!==0){return i[0]
}return null
};
this._hub.getService=function(j,i){return g.getService(j,i)
};
this._hub.ungetService=function(j,i){return g.ungetService(j,i)
};
this._hub.registerServiceListener=function(i){return g.registerServiceListener(i)
};
this._hub.unregisterServiceListener=function(i){return g.unregisterServiceListener(i)
};
this._hub.requireService=function(i){f.requireService(i);
return this
};
this._hub.provideService=function(i){f.provideService(i);
return this
}
}e.prototype.unregisterComponent=function(h){var g,j,f,i;
i=this._components;
for(j=0,f=i.length;
j<f;
j++){g=i[j];
if(g.component===h){g.serviceComponent.onStop();
HUBU.UTILS.removeElementFromArray(this._components,g)
}}return this._registry.unregisterServices(h)
};
e.prototype.requireService=function(m){var g,h,k,n,l,f,i,j,o;
k=m.component,n=m.contract,f=m.filter,g=m.aggregate,i=m.optional,l=m.field,h=m.bind,o=m.unbind;
if(!(k!=null)){throw new Exception("Cannot require a service without a valid component")
}if(g==null){g=false
}if(i==null){i=false
}if(n==null){n=null
}if(f==null){f=null
}if(!(l!=null)&&!(h!=null)){throw new Exception("Cannot require a service - field or bind must be set")
}if(l==null){l=null
}if(h==null){h=null
}if(o==null){o=null
}j=new HUBU.ServiceDependency(k,n,f,g,i,l,h,o,this._hub);
return this._addServiceDependencyToComponent(k,j)
};
e.prototype.provideService=function(m){var j,n,g,k,l,h,i,f;
j=m.component,n=m.contract,i=m.properties,l=m.preRegistration,g=m.postRegistration,h=m.preUnregistration,k=m.postUnregistration;
if(!(j!=null)){throw new Exception("Cannot provided a service without a valid component")
}if(!(n!=null)){throw new Exception("Cannot provided a service without a valid contract")
}if(i==null){i={}
}f=new HUBU.ProvidedService(j,n,i,l,g,h,k,this._hub);
return this._addProvidedServiceToComponent(j,f)
};
e.prototype._addServiceDependencyToComponent=function(g,i){var l,h,m,k,f,j;
m=false;
j=this._components;
for(k=0,f=j.length;
k<f;
k++){h=j[k];
if(h.component===g){l=h
}}if(!(l!=null)){l={component:g,serviceComponent:new HUBU.ServiceComponent(g)};
this._components.push(l);
m=true
}l.serviceComponent.addRequiredService(i);
if(m&&this._hub.isStarted()){return l.serviceComponent.onStart()
}};
e.prototype._addProvidedServiceToComponent=function(g,m){var k,h,l,j,f,i;
l=false;
i=this._components;
for(j=0,f=i.length;
j<f;
j++){h=i[j];
if(h.component===g){k=h
}}if(!(k!=null)){k={component:g,serviceComponent:new HUBU.ServiceComponent(g)};
this._components.push(k);
l=true
}k.serviceComponent.addProvidedService(m);
if(this._hub.isStarted()&&l){return k.serviceComponent.onStart()
}};
e.prototype.start=function(){var h,j,g,i,f;
i=this._components;
f=[];
for(j=0,g=i.length;
j<g;
j++){h=i[j];
f.push(h.serviceComponent.onStart())
}return f
};
e.prototype.stop=function(){var h,j,g,i,f;
i=this._components;
f=[];
for(j=0,g=i.length;
j<g;
j++){h=i[j];
f.push(h.serviceComponent.onStop())
}return f
};
return e
})();
getHubuExtensions().service=c
}).call(this);
(function(){var a;
HUBU.Hub=a=(function(){b.prototype._components=null;
b.prototype._started=false;
b.prototype._extensions=null;
b.prototype._parentHub=null;
function b(){this._components=[];
this._started=false;
this._extensions=null
}b.prototype.configure=function(e){var d,c,f;
if((e!=null)){this._parentHub=e
}if(!(this._extensions!=null)){this._extensions=[];
f=getHubuExtensions();
for(c in f){d=f[c];
this._extensions.push(new d(this))
}}else{HUBU.logger.debug("Hub already initialized")
}return this
};
b.prototype.getParentHub=function(){return this._parentHub
};
b.prototype.getComponents=function(){return this._components
};
b.prototype.getComponent=function(d){var f,e,i,h,c,g;
if(!(d!=null)){return null
}g=this._components;
for(h=0,c=g.length;
h<c;
h++){f=g[h];
e=f.getComponentName;
if((e!=null)&&HUBU.UTILS.isFunction(e)){i=e.apply(f,[]);
if(i===d){return f
}}}return null
};
b.prototype.registerComponent=function(d,h){var e,g,c,f;
if(!(d!=null)){throw new Exception("Cannot register component - component is null")
}if(!HUBU.UTILS.isComponent(d)){if(d.getComponentName){throw new Exception(d.getComponentName()+" is not a valid component")
}else{throw new Exception(d+" is not a valid component")
}}if(this._extensions===null){this.configure()
}if(this.getComponent(d.getComponentName())!=null){HUBU.logger.info("Component "+d.getComponentName()+" already registered");
return this
}this._components.push(d);
if((h!=null)&&(h.component_name!=null)){d.__name__=h.component_name;
d.getComponentName=function(){return this["__name__"]
}
}if(!(d.__hub__!=null)&&!(d.hub!=null)){d.__hub__=this;
d.hub=function(){return this.__hub__
}
}HUBU.logger.debug("Registering component "+d.getComponentName());
f=this._extensions;
for(g=0,c=f.length;
g<c;
g++){e=f[g];
HUBU.UTILS.invoke(e,"registerComponent",[d,h])
}HUBU.logger.debug("Configuring component "+d.getComponentName());
d.configure(this,h);
if(this._started){HUBU.logger.debug("Starting component "+d.getComponentName());
d.start()
}HUBU.logger.debug("Component "+d.getComponentName()+" registered");
return this
};
b.prototype.unregisterComponent=function(e){var g,f,c,i,d,h;
if(!(e!=null)){return this
}g=null;
if(HUBU.UTILS.typeOf(e)==="string"){g=this.getComponent(e);
if(!(g!=null)){return this
}}else{if(!HUBU.UTILS.isComponent(e)){throw new Exception("Cannot unregister component, it's not a valid component").add("component",e)
}else{g=e
}}if(this._extensions===null){this.configure()
}c=HUBU.UTILS.indexOf(this._components,g);
if(c!==-1){h=this._extensions;
for(i=0,d=h.length;
i<d;
i++){f=h[i];
HUBU.UTILS.invoke(f,"unregisterComponent",[g])
}g.stop();
this._components.splice(c,1)
}else{HUBU.logger.info("Component "+g.getComponentName()+" not unregistered - not on the hub")
}return this
};
b.prototype.start=function(){var g,e,i,f,d,c,h,j;
if(this._started){return this
}if(this._extensions===null){this.configure()
}h=this._extensions;
for(i=0,d=h.length;
i<d;
i++){e=h[i];
HUBU.UTILS.invoke(e,"start",[])
}this._started=true;
j=this._components;
for(f=0,c=j.length;
f<c;
f++){g=j[f];
g.start()
}return this
};
b.prototype.stop=function(){var g,e,i,f,d,c,h,j;
if(!this._started){return this
}this._started=false;
h=this._components;
for(i=0,d=h.length;
i<d;
i++){g=h[i];
g.stop()
}j=this._extensions;
for(f=0,c=j.length;
f<c;
f++){e=j[f];
HUBU.UTILS.invoke(e,"start",[])
}return this
};
b.prototype.isStarted=function(){return this._started
};
b.prototype.reset=function(){var d,f,c,e;
this.stop();
if(this._extensions===null){this.configure()
}e=this._extensions;
for(f=0,c=e.length;
f<c;
f++){d=e[f];
HUBU.UTILS.invoke(d,"reset",[])
}this._components=[];
this._extensions=null;
return this
};
b.prototype.getComponentName=function(){return"Hub"
};
return b
})();
getGlobal().hub=new HUBU.Hub()
}).call(this);