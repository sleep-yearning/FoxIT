package com.foxyourprivacy.privacyRiskInfo;

/**
 * An object for the classes to hold their properties in the list and in the classview
 * they have an index, a description and a name
 * Created by Tim on 01.08.2016.
 */
public class ClassObject {

    private final String name;
    private final String descriptionText; //text describing the class content
    private final int position;

    /**
     * @param name             the className
     * @param classDescription the description of the class
     * @author Tim
     */
    public ClassObject(String name, String classDescription, int classposition) {
        this.name = name;
        this.descriptionText = classDescription;
        this.position = classposition;
    }


    //Getter
    public String getName() {
        return name;
    }

    public String getDescriptionText() {
        return descriptionText;
    }

    public int getPosition() {
        return position;
    }


}
