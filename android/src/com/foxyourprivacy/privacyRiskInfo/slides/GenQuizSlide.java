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
import android.widget.CheckBox;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.foxyourprivacy.privacyRiskInfo.R;
import com.foxyourprivacy.privacyRiskInfo.activities.LessonActivity;

import java.util.Arrays;
import java.util.Random;
import java.util.regex.Pattern;

/**
 * A quiz slide that takes as many options as you like
 * Created by noah on 04.06.17.
 */

public class GenQuizSlide extends Slide {

    private boolean evaluated = false;
    private View view;
    private String[] answers;
    private Boolean[] correctness;
    private int[] ids;
    private String rightAnswer;// = getString(R.string.genericQuizCorrect);
    private String wrongAnswer;// = getString(R.string.genericQuizIncorrect);
    private String question;
    private int points = 0;


    @Override
    public void setArguments(Bundle arg) {
        super.setArguments(arg);
        type = "quiz";
        slideInfo = slideInfo.replaceFirst(Pattern.quote("[QUIZ]"), "");
        Log.d("Quiz-content", slideInfo);

        //finds, extracts and deletes the positive response in the remaining String if inside
        if (slideInfo.contains("response+:")) {
            String correct = slideInfo.substring(slideInfo.indexOf("response+:"));
            correct = correct.substring(0, correct.indexOf(";") + 1);
            rightAnswer = correct.substring(10, correct.indexOf(";"));
            slideInfo = slideInfo.replace(correct, "");
        } else {
            //TODO generalize to multiple languages
            rightAnswer = "Richtig.";//getString(R.string.genericQuizCorrect);

        }

        //finds, extracts and deletes the negative response in the remaining String if inside
        if (slideInfo.contains("response-:")) {
            String wrong = slideInfo.substring(slideInfo.indexOf("response-:"));
            wrong = wrong.substring(0, wrong.indexOf(";") + 1);
            wrongAnswer = wrong.substring(10, wrong.indexOf(";"));
            slideInfo = slideInfo.replace(wrong, "");
        } else {
            //TODO generalize to multiple languages
            wrongAnswer = "Falsch.";//getString(R.string.genericQuizIncorrect);

        }


        String[] slideparts = slideInfo.split(";");
        //first element of array is the question-text
        question = slideparts[0];
        correctness = new Boolean[slideparts.length - 1];
        answers = new String[slideparts.length - 1];
        //shuffle order of answers on each new slidecreation
        String[] onlyanswers = shuffleAnswers(Arrays.copyOfRange(slideparts, 1, slideparts.length));

        //extraction of right/wrong and answers from the combined array
        for (int i = 0; i < onlyanswers.length; i++) {
            correctness[i] = (onlyanswers[i].substring(0, 1)).equals("1");
            answers[i] = onlyanswers[i].substring(1);
            Log.d(onlyanswers[i], correctness[i] + "  " + answers[i]);
        }



    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.layout_slide_quiz, container, false);
        fillLayout();
        //adds number of achievable points (in this slide) to the lesson maxpoints
        ((LessonActivity) getActivity()).lesson.maxscore += answers.length;
        Log.d("oncreateactivity", getActivity().toString());

        return view;
    }

    @Override
    public void fillLayout() {
        TextView questionText = view.findViewById(R.id.quiz_text);
        questionText.setText(question);
        questionText.setTextSize(TypedValue.COMPLEX_UNIT_PX, getActivity().getResources().getDimension(textsize));
        GridLayout boxcontainer = view.findViewById(R.id.quiz_boxes);

        ids = new int[answers.length];
        int k = 0;
        for (String str : answers) {
            CheckBox box = new CheckBox(getContext());
            box.setText(str);
            box.setTextSize(TypedValue.COMPLEX_UNIT_PX, getActivity().getResources().getDimension(textsize));
            box.setId(k + 9999);
            ids[k] = box.getId();
            k++;
            boxcontainer.addView(box);
        }
    }

    private String[] shuffleAnswers(String[] answers) {
        int index;
        String[] randAnswers = new String[answers.length];
        Random random = new Random();
        for (String answer : answers) {
            printArray(randAnswers);
            index = random.nextInt(answers.length - 1);
            while (randAnswers[index] != null) {
                Log.d(index + "", randAnswers[index]);
                index = random.nextInt(answers.length);
            }
            randAnswers[index] = answer;
        }
        return randAnswers;

    }

    private void printArray(String[] array) {
        for (String anArray : array) {
            if (anArray != null) Log.d("array", anArray);
        }
    }

    public boolean gotEvaluated() {
        return evaluated;
    }

    public boolean evaluation() {
        int i = 0;
        for (int id : ids) {
            CheckBox box = view.findViewById(id);
            box.setEnabled(false);
            if (box.isChecked() && correctness[i] || !box.isChecked() && !correctness[i]) {
                box.setBackgroundResource(R.color.rightAnswer);
                points++;
            } else {
                box.setBackgroundResource(R.color.wrongAnswer);
            }
            i++;
        }
        evaluated = true;
        Log.d("points", points + "");
        //adds points of this quizslide to lesson (for evaluation in Certificate)
        ((LessonActivity) getActivity()).lesson.score += points;
        Log.d("evaluationactivity", getActivity().toString());
        if (points >= answers.length) {
            Toast toast = Toast.makeText(getActivity(), rightAnswer, Toast.LENGTH_LONG);
            TextView v = toast.getView().findViewById(android.R.id.message);
            v.setBackgroundColor(Color.GREEN);
            toast.show();
            return true;
        } else {
            Toast toast = Toast.makeText(getActivity(), wrongAnswer, Toast.LENGTH_LONG);
            TextView v = toast.getView().findViewById(android.R.id.message);
            v.setBackgroundColor(Color.RED);
            toast.show();
            return false;
        }
    }

}
