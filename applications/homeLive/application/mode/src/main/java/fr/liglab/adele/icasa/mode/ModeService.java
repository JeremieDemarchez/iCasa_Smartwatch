package fr.liglab.adele.icasa.mode;

import java.util.List;

/**
 * Created by aygalinc on 10/04/15.
 */
public interface ModeService {

    String getCurrentMode();

    void setCurrentMode(String modeName);

    List<String> getListOfMode();

}
