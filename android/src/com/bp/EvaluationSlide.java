package com.bp;

/**
 * Created by Ich on 25.06.2016.
 */
public abstract class EvaluationSlide extends Slide {
protected boolean evaluated=false;

    public boolean getEvaluated(){
        return evaluated;
    }

    public boolean evaluation(){return true;}


}