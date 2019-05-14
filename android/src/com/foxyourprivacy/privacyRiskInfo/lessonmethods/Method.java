package com.foxyourprivacy.privacyRiskInfo.lessonmethods;

import android.app.Activity;

/**
 * generic abstract Method-Class for all the different Method classes to inherit from
 * Created by Tim on 19.07.2016.
 */
public abstract class Method {
    //the activity the MethodFactory is called in
    Activity activity;

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    //the generic methodLeft to be overwritten in the childclasses
    public abstract void callClassMethod(String s);
}
