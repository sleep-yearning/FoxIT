package com.foxyourprivacy.privacyRiskInfo.slides;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.foxyourprivacy.privacyRiskInfo.R;
import com.foxyourprivacy.privacyRiskInfo.activities.LessonActivity;

import java.util.regex.Pattern;

/**
 * This Slide evaluates a lesson and is used for quiz lessons
 * Created by noah on 04.06.17.
 */

public class CertSlide extends Slide {
    private Boolean solved = false;
    private View view;
    private int completepoints = 0;
    private int reachedpoints = 0;
    private String positiveAnswer;
    private String negativeAnswer;

    @Override
    public void fillLayout() {
        TextView answer = view.findViewById(R.id.certificate_answer);
        TextView points = view.findViewById(R.id.certificate_points);
        Log.d("points", reachedpoints + " / " + completepoints);
        if (reachedpoints >= completepoints) {
            answer.setText(positiveAnswer);
            points.setTextColor(Color.GREEN);
            solved = true;

        } else {
            answer.setText(negativeAnswer);
            points.setTextColor(Color.RED);
        }

        points.setText(getString(R.string.reachedPoints, reachedpoints, ((LessonActivity) getActivity()).lesson.maxscore));
        answer.setTextSize(TypedValue.COMPLEX_UNIT_PX, getActivity().getResources().getDimension(textsize));
        points.setTextSize(TypedValue.COMPLEX_UNIT_PX, getActivity().getResources().getDimension(textsize));

    }

    @Override
    public void setArguments(Bundle arg) {
        super.setArguments(arg);
        slideInfo = slideInfo.replaceFirst(Pattern.quote("[CERT]"), "");
        String[] informations = slideInfo.split(";");
        Log.d("Certslide", slideInfo);
        completepoints = Integer.valueOf(informations[0]);
        Log.d("completepoints", completepoints + "");
        positiveAnswer = informations[1];
        negativeAnswer = informations[2];


    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.layout_slide_certificate, container, false);
        type = "cert";
        return view;
    }

    public void evaluate() {
        reachedpoints = ((LessonActivity) getActivity()).lesson.score;
        fillLayout();

    }

    @Override
    public boolean isLessonSolved() {
        return solved;
    }
}
