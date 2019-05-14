package com.foxyourprivacy.privacyRiskInfo.slides;

import android.os.Bundle;

/**
 * Slides are the base of lessons, there are different kinds of slides which all inherit from this class.
 * Created by Tim on 25.06.2016.
 */
public abstract class Slide extends android.support.v4.app.Fragment {
    public String type = "textimage";
    protected String slideInfo;
    protected int textsize;
    //Strings which describes which slide shall be next, null if its the succeeding number
    private int nextSlide = -99;
    private int backSlide = -99;

    /**
     * Enables to pass arguments to the fragment
     * @author Tim
     */
    @Override
    public void setArguments(Bundle arg) {
        //fetches the slide information
        slideInfo = arg.getString("slide");
        textsize = arg.getInt("textsize");
        //Log.d("Slide-setArguments", slideInfo);
        //TODO next und back aus slide bekommen
        //fetches which slide is the succeeding and previous Slide

    }

    public int next() {

        return nextSlide;
    }

    public int back() {
        return backSlide;
    }

    public boolean isLessonSolved() {
        return true;
    }

    public abstract void fillLayout();

    public String getSlideInfo() {
        return slideInfo;
    }


}