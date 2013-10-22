package fr.liglab.adele.icasa.frontend.services.utils;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * User: garciai@imag.fr
 * Date: 10/21/13
 * Time: 1:26 PM
 */
public class ICasaLibs {

    private String id;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Set<String> getWidgets() {
        return Collections.unmodifiableSet(widgets);
    }

    private String name;

    private Set<String> widgets;


    public ICasaLibs(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public void addWidget(String widget){
        widgets.add(widget);
    }
}
