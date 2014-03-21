package fr.liglab.adele.icasa.device.light;

import java.awt.*;

/**
 * Created by jnascimento on 21/03/14.
 * Represents a light device in which its color can be changed
 */
public interface ColorLight extends BinaryLight,DimmerLight{

    String COLOR_LIGHT_COLOR_LEVEL = "colorLight.colorLevel";


    /**
     * Changes the colorlight
     * @param color
     */
    public Color setColor(Color color);

    /**
     * Fetches last color state
     * @return color
     */
    public Color getColor();

}
