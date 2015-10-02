package fr.liglab.adele.icasa.context.model.example.application;

import fr.liglab.adele.icasa.context.model.ContextEntity;
import fr.liglab.adele.icasa.context.transformation.Aggregation;
import fr.liglab.adele.icasa.device.light.BinaryLight;
import fr.liglab.adele.icasa.device.light.DimmerLight;
import org.apache.felix.ipojo.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public interface LightFollowRegulator {
    /**
     *
     * @param illuminanceFactor : between 0 and 1
     */
    public void setIlluminanceFactor (double illuminanceFactor);
}
