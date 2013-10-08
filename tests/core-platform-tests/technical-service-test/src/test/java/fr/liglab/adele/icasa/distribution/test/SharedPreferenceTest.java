/**
 *
 *   Copyright 2011-2012 Universite Joseph Fourier, LIG, ADELE Research
 *   Group Licensed under the Apache License, Version 2.0 (the "License");
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
package fr.liglab.adele.icasa.distribution.test;

import fr.liglab.adele.commons.distribution.test.AbstractDistributionBaseTest;
import fr.liglab.adele.icasa.service.preferences.PreferenceChangeListener;
import fr.liglab.adele.icasa.service.preferences.Preferences;
import junit.framework.Assert;
import org.apache.felix.ipojo.Factory;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.mockito.Mockito.*;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerMethod;
import org.osgi.framework.BundleContext;
import org.ow2.chameleon.sharedprefs.SharedPreferencesService;

import javax.inject.Inject;


/**
 * User: garciai@imag.fr
 * Date: 8/26/13
 * Time: 5:08 PM
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerMethod.class)
public class SharedPreferenceTest  extends AbstractDistributionBaseTest {

    @Inject
    Preferences preferences;

    @Inject
    BundleContext context;

    @Test
    public void testServiceExistence(){
        Factory serviceFactory =  (Factory)getService(context, Factory.class, "(&(factory.name=iCasaPreferences)(factory.state=1))");
        Assert.assertNotNull(serviceFactory);
        Assert.assertNotNull(getService(context, Factory.class, "(&(factory.name=org.ow2.chameleon.sharedprefs.XmlSharedPreferences)(factory.state=1))"));
        Assert.assertNotNull(getService(context, SharedPreferencesService.class));
        Preferences service =  (Preferences)getService(context, Preferences.class);
        Assert.assertNotNull(service);
    }

    /**
     * Test the callbacks for listener in user shared preference
     */
    @Test
    public void testUserSharedPreferenceSubscription(){
        String user = "issacUser-1";
        String key = "key1-1";
        String key2 = "key&-2";
        String value = "value1-1";
        String value2 = "value1-2";

        //add listener
        PreferenceChangeListener listener = mock(PreferenceChangeListener.class);
        preferences.addUserPreferenceChangeListener(user, listener);

        //set first pair
        preferences.setUserPropertyValue(user, key, value);

        //verify first pair call
        verify(listener, times(1)).changedProperty(key, null, value);

        //verify second pair call
        verify(listener, never()).changedProperty(key2, null, value2);
        preferences.setUserPropertyValue(user, key2, value2);
        verify(listener, times(1)).changedProperty(key2, null, value2);
    }

    /**
     * Test there is no call for listeners when another user property is modified
     */
    @Test
    public void testNoCallUserSharedPreferenceSubscription(){
        String user = "issacUser-1";
        String user2 = "issacUser-2";
        String key = "key1-1";
        String value = "value1-1";
        //add listener
        PreferenceChangeListener listener = mock(PreferenceChangeListener.class);
        preferences.addUserPreferenceChangeListener(user, listener);

        //change values for user 2
        preferences.setUserPropertyValue(user2, key, value);

        //must not call since modified only user2
        verify(listener, never()).changedProperty(key, null, value);

    }

    /**
     * Test it stop callbacks when listener is removed.
     */
    @Test
    public void testRemoveUserSharedPreferenceSubscription(){
        String user = "issacUser-1";
        String key = "key1-1";
        String value = "value1-1";
        String key2 = "key1-2";
        String value2 = "value1-2";
        //add listener
        PreferenceChangeListener listener = mock(PreferenceChangeListener.class);
        preferences.addUserPreferenceChangeListener(user, listener);

        //change values for user
        preferences.setUserPropertyValue(user, key, value);

        //test one call
        verify(listener, times(1)).changedProperty(key, null, value);
        //remove listener
        preferences.removeUserPreferenceChangeListener(user, listener);
        //change values for user
        preferences.setUserPropertyValue(user, key2, value2);
        //test any call since we remove listener
        verify(listener, never()).changedProperty(key2, null, value2);
    }

    /**
     * Test the callback for application shared preference listener.
     */
    @Test
    public void testApplicationSharedPreferenceSubscription(){
        String applicationId = "app2";
        String key = "key2-1";
        String key2 = "key2-2";
        String value = "value2-1";
        String value2 = "value2-2";

        //add listener
        PreferenceChangeListener listener = mock(PreferenceChangeListener.class);
        preferences.addApplicationPreferenceChangeListener(applicationId, listener);

        // set first key/value pair
        preferences.setApplicationPropertyValue(applicationId, key, value);
        //test one call for first key/value pair,

        verify(listener, times(1)).changedProperty(key, null, value);
        //test no call for second key/value pair
        verify(listener, never()).changedProperty(key2, null, value2);

        //set second key/value pair
        preferences.setApplicationPropertyValue(applicationId, key2, value2);

        //verify second key/value pair
        verify(listener, times(1)).changedProperty(key2, null, value2);
    }

    /**
     * test no callback is made when another app property is modified.
     */
    @Test
    public void testNoCallAppSharedPreferenceSubscription(){
        String app1 = "App1-1";
        String app2 = "App2-2";
        String key = "key1-1";
        String value = "value1-1";
        //add listener to app 1
        PreferenceChangeListener listener = mock(PreferenceChangeListener.class);
        preferences.addApplicationPreferenceChangeListener(app1, listener);

        //change values for app 2
        preferences.setApplicationPropertyValue(app2, key, value);

        //must not call since modified only app 2
        verify(listener, never()).changedProperty(key, null, value);
    }

    /**
     * Test it stop receive callbacks when listener is removed.
     */
    @Test
    public void testRemoveAppSharedPreferenceSubscription(){
        String app = "app-1";
        String key = "key1-1";
        String value = "value1-1";
        String key2 = "key1-2";
        String value2 = "value1-2";
        //add listener
        PreferenceChangeListener listener = mock(PreferenceChangeListener.class);
        preferences.addApplicationPreferenceChangeListener(app, listener);

        //change values for app
        preferences.setApplicationPropertyValue(app, key, value);

        //test one call
        verify(listener, times(1)).changedProperty(key, null, value);
        //remove listener
        preferences.removeApplicationPreferenceChangeListener(app, listener);
        //change values for app
        preferences.setApplicationPropertyValue(app, key2, value2);
        //test no call since we remove listener
        verify(listener, never()).changedProperty(key2, null, value2);
    }

    /**
     * Test the global shared preference listener.
     */
    @Test
    public void testGlobalSharedPreferenceSubscription(){
        String key = "key2-1";
        String key2 = "key2-2";
        String value = "value2-1";
        String value2 = "value2-2";

        //add listener
        PreferenceChangeListener listener = mock(PreferenceChangeListener.class);
        preferences.addGlobalPreferenceChangeListener(listener);

        // set first key/value pair
        preferences.setGlobalPropertyValue(key, value);
        //test one call for first key/value pair,

        verify(listener, times(1)).changedProperty(key, null, value);
        //test no call for second key/value pair
        verify(listener, never()).changedProperty(key2, null, value2);

        //set second key/value pair
        preferences.setGlobalPropertyValue(key2, value2);

        //verify second key/value pair
        verify(listener, times(1)).changedProperty(key2, null, value2);
    }


    /**
     * Test it stop the callbacks when listener is removed.
     */
    @Test
    public void testRemoveGlobalSharedPreferenceSubscription(){

        String key = "key1-1";
        String value = "value1-1";
        String key2 = "key1-2";
        String value2 = "value1-2";
        //add listener
        PreferenceChangeListener listener = mock(PreferenceChangeListener.class);
        preferences.addGlobalPreferenceChangeListener(listener);

        //change values for app
        preferences.setGlobalPropertyValue(key, value);

        //test one call
        verify(listener, times(1)).changedProperty(key, null, value);
        //remove listener
        preferences.removeGlobalPreferenceChangeListener(listener);
        //change values for app
        preferences.setGlobalPropertyValue(key2, value2);
        //test no call since we remove listener
        verify(listener, never()).changedProperty(key2, null, value2);
    }

}
