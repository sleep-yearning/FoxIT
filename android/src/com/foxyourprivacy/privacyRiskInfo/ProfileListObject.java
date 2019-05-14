package com.foxyourprivacy.privacyRiskInfo;

/**
 * Items to be displayed on the profileFragment
 * Created by Tim on 29.09.2016.
 */

public class ProfileListObject {
    private final String inputType;
    private final String inputDescription;
    private String input = "";

    public ProfileListObject(String inputType, String inputDescription) {
        this.inputType = inputType;
        this.inputDescription = inputDescription;
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public String getInputDescription() {
        return inputDescription;
    }

    public String getInputType() {
        return inputType;
    }
}
