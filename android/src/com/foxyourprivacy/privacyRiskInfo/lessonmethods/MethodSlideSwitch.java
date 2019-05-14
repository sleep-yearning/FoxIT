package com.foxyourprivacy.privacyRiskInfo.lessonmethods;

import com.foxyourprivacy.privacyRiskInfo.activities.LessonActivity;

/**
 * A method to be used inside the lessons that changes the slide shown
 * Created by Tim on 19.07.2016.
 */
public class MethodSlideSwitch extends Method {
    /**
     * jumps to the described slide
     * @author Tim
     * @param destination a string containing an int number describing the slide to jump to
     */
    @Override
    public void callClassMethod(String destination) {

        if (activity instanceof LessonActivity) {
            ((LessonActivity) activity).jumpToSlide(Integer.parseInt(destination));
        }
    }


}
