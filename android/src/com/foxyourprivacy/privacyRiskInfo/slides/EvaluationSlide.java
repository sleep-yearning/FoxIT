package com.foxyourprivacy.privacyRiskInfo.slides;

/**
 * Evaluation slides were used for the study of the app but will be used again probably
 * Created by Tim on 25.06.2016.
 */
public abstract class EvaluationSlide extends Slide {
    protected boolean evaluated = false;

    public boolean getEvaluated() {
        return evaluated;
    }

    public boolean evaluation() {
        return true;
    }


}