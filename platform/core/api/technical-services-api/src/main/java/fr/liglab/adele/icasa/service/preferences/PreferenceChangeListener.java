package fr.liglab.adele.icasa.service.preferences;

/**
 * Listener used to listen on preference property value changes.
 *
 * @author Thomas Leveque
 */
public interface PreferenceChangeListener {

    /**
     * Called when a property value changes.
     *
     * @param propertyName property name
     * @param oldValue the old value of the property
     * @param newValue the new value of the property
     */
    void changedProperty(String propertyName, Object oldValue, Object newValue);
}
