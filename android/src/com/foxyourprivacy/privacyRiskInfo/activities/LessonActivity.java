package com.foxyourprivacy.privacyRiskInfo.activities;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.foxyourprivacy.privacyRiskInfo.DBHandler;
import com.foxyourprivacy.privacyRiskInfo.LessonObject;
import com.foxyourprivacy.privacyRiskInfo.R;
import com.foxyourprivacy.privacyRiskInfo.ValueKeeper;
import com.foxyourprivacy.privacyRiskInfo.lessonmethods.Method;
import com.foxyourprivacy.privacyRiskInfo.lessonmethods.MethodFactory;
import com.foxyourprivacy.privacyRiskInfo.slides.CertSlide;
import com.foxyourprivacy.privacyRiskInfo.slides.EvaluationSlide;
import com.foxyourprivacy.privacyRiskInfo.slides.GenQuizSlide;
import com.foxyourprivacy.privacyRiskInfo.slides.LinkSlide;
import com.foxyourprivacy.privacyRiskInfo.slides.QuestSlide;
import com.foxyourprivacy.privacyRiskInfo.slides.Slide;
import com.foxyourprivacy.privacyRiskInfo.slides.TextImageSlide;
import com.foxyourprivacy.privacyRiskInfo.slides.VideoSlide;

import java.util.HashMap;

/**
 * manages the visualisation of slides
 */
public class LessonActivity extends FoxITActivity {
    public LessonObject lesson;
    boolean isEvaluation = false;
    boolean didEvaluationStart = false;
    private int slideNumber = 0;
    private String className = "nothing";
    private HashMap<String, String> evaluationResults = new HashMap<>(); //inspection wants final because changing methods are not called now without proper evaluations
    private Handler handler;

    /**
     * @author Tim
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_lesson_activity);

        lesson = getIntent().getExtras().getParcelable("lesson");
        String[] slideStringArray = lesson.getSlides().split("_slide_;");
        className = getIntent().getStringExtra("classname");

        int textsize = R.dimen.text_normal;
        DBHandler dbHandler = new DBHandler(this);
        if (!dbHandler.getIndividualValue("textsize").equals("notfound")) {
            int textchoice = Integer.valueOf(dbHandler.getIndividualValue("textsize"));
            switch (textchoice) {
                case R.id.textsizesmall:
                    textsize = R.dimen.text_small;
                    break;
                case R.id.textsizesmaller:
                    textsize = R.dimen.text_smaller;
                    break;
                case R.id.textsizenormal:
                    textsize = R.dimen.text_normal;
                    break;
                case R.id.textsizebigger:
                    textsize = R.dimen.text_bigger;
                    break;
                case R.id.textsizebig:
                    textsize = R.dimen.text_big;
                    break;
            }
        }




        lesson.slidearray = new Slide[slideStringArray.length];
        int i = 0;
        for (String slideString : slideStringArray) {
            if (slideString.startsWith(" ")) slideString = slideString.replaceFirst(" ", "");
            Bundle args = new Bundle();
            args.putString("slide", slideString);
            args.putInt("textsize", textsize);
            lesson.slidearray[i] = createSlide(slideString);
            lesson.slidearray[i].setArguments(args);
            i++;
        }
        //initializes handler for going back to lessons
        handler = new Handler();

        // sets our toolbar as the actionbar
        Toolbar toolbar = findViewById(R.id.foxit_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (toolbar != null) {
            toolbar.setTitle(lesson.getLessonName());
        }

        //changes status of lesson to read if it was below that
        if (lesson.getProcessingStatus() < 2 && lesson.getProcessingStatus() != -99) {
            DBHandler db = new DBHandler(this);
            db.changeLessonToRead(lesson.getLessonName(), className);
            db.close();
        }


        //add the first slide to the activities' context
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

        Slide firstSlide = lesson.slidearray[0];
        //if the first slide is a QuizSlide show the question mark
        if (firstSlide instanceof GenQuizSlide) {
            setImage("check");
        } else if (lesson.slidearray.length < 2) {
            setImage("kreuz");
        }
        int j = 0;
        //transaction.add(R.id.lesson_frame, firstSlide, "slide");
        for (Slide s : lesson.slidearray) {
            transaction.add(R.id.lesson_frame, s, "slide");
            transaction.hide(s);
            j++;
        }
        transaction.show(lesson.slidearray[0]);
        transaction.commit();


        if (lesson.getType().equals("evaluation")) {
            isEvaluation = true;
            RelativeLayout outerFrame = findViewById(R.id.count_frame);
            outerFrame.setBackgroundColor(Color.BLUE);
        }

        //if the first slide does not have a previous slide, hide the backArrow
        final ImageButton backButton = findViewById(R.id.back_button);
        if (lesson.slidearray[slideNumber].back() == -99) {
            backButton.setVisibility(View.GONE);
        }


        // define the proper button behavior for switching slides, and change the button image accordingly
        ImageButton nextButton = findViewById(R.id.next_button);
        nextButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                int slideToJumpTo; //aka the next slideNumber
                Slide currentSlide = lesson.slidearray[slideNumber];



                //if the next slide is a QuizSlide show the question mark
                if (currentSlide.type.equals("quiz") && !((GenQuizSlide) currentSlide).gotEvaluated()) {
                    ((GenQuizSlide) currentSlide).evaluation();
                    //if there is no next slide after the next show the cross
                    if ((currentSlide.next() == -99) && (lesson.slidearray.length < (slideNumber + 3))) {
                        setImage("kreuz");
                        //if there is a next slide after the next slide set the arrow
                    } else {
                        setImage("pfeil_rechts");
                    }

                } else if (!(currentSlide instanceof EvaluationSlide) || ((EvaluationSlide) currentSlide).evaluation()) {

                        //if the current slide defines a next slides that's the slideNumberToJumpTo
                    if (currentSlide.next() != -99) {
                        slideToJumpTo = currentSlide.next();
                    } else {
                            //otherwise its the slide with the next higher number
                        slideToJumpTo = slideNumber + 1;
                    }
                        //if the slide with the retrieved number exists jump to it
                    if (lesson.slidearray.length > slideToJumpTo) {
                        jumpToSlide(slideToJumpTo);
                        if (lesson.slidearray[slideToJumpTo].type.equals("cert")) {
                            ((CertSlide) lesson.slidearray[slideToJumpTo]).evaluate();
                        }
                    } else {
                        //otherwise close the Activity
                        goBackToLessonList(slideNumber);
                    }


                }

                ft.commit();
            }
        });


        // define the proper button behavior for going back one slide, and change the button image accordingly
        backButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                Slide currentSlide = lesson.slidearray[slideNumber];
                //if the slide defines a backSlide jump to it
                if (currentSlide.back() != -99) {
                    jumpToSlide(currentSlide.back());
                } else {
                    //otherwise if its not prevented to jump back jump to the last slide in the backStack of slides
                    if (lesson.getBackSlide() != -99) {

                        ft.show(lesson.slidearray[lesson.getBackSlide()]);
                        //if the last slide has a nextSlide show the arrow otherwise show the cross symbol
                        if ((lesson.slidearray[lesson.getBackSlide()].next() == -99) && (lesson.slidearray.length < (lesson.getBackSlide() + 1))) {
                            setImage("kreuz");
                        } else {
                            setImage("pfeil_rechts");
                        }
                        ft.hide(currentSlide);
                        //set the right slideNumber
                        slideNumber = lesson.getBackSlide();
                        //remove the last slide from the slideBackStack
                        lesson.slideBackStack.remove(lesson.slideBackStack.size() - 1);
                    }
                    //if the slide currently open has no backSlide, hide the backButton
                    if (lesson.getBackSlide() == -99 && lesson.slidearray[slideNumber].back() < 0) {
                        backButton.setVisibility(View.GONE);
                    }
                    ft.commit();
                }
            }
        });

        /*

        //defines the exact same behavior as the nextButton for the whole slide except that quizSlides are not evaluated
        RelativeLayout lessonFrame = (RelativeLayout) findViewById(R.id.lesson_frame);
        lessonFrame.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if ((!lesson.getLessonName().contains(":")) || !(lesson.getLessonName().contains("timeEval") || lesson.getLessonName().contains("appEval"))) {
                    String nextSlideNumber = lesson.slideHashMap.get(Integer.toString(slideNumber)).next();

                    Slide slide = lesson.slideHashMap.get(Integer.toString(slideNumber));
                    if (!(didEvaluationStart) && (!(slide instanceof QuizSlide) || ((QuizSlide) slide).gotEvaluated())) {
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        int slideToJumpTo; //aka the next SlideNumber
                        //if the next slide is a quizSlide show the question mark
                        if (nextSlideNumber != null && nextSlideNumber.equals("quiz")) {
                            ((QuizSlide) lesson.slideHashMap.get(Integer.toString(slideNumber))).evaluation();
                            //if there is no next slide after the next show the cross
                            if (lesson.lessonInfoHashMap.get(Integer.toString(slideNumber + 1)) == null) {
                                setImage("kreuz");
                                //if there is a next slide after the next slide set the arrow
                            } else {
                                setImage("pfeil_rechts");
                            }
                        } else {
                            //if the current slide defines a next slide that's the slideNumberToJumpTo
                            if (nextSlideNumber != null) {
                                slideToJumpTo = Integer.parseInt(nextSlideNumber);
                            } else {
                                //otherwise it's the slide with the next higher number
                                slideToJumpTo = slideNumber + 1;
                            }
                            //if the slide with the retrieved number exists, jump to it
                            if (lesson.lessonInfoHashMap.get(Integer.toString(slideToJumpTo)) != null) {
                                jumpToSlide(Integer.toString(slideToJumpTo));
                            } else {
                                //otherwise close the Activity
                                goBackToLessonList(slideNumber);
                            }
                        }
                        ft.commit();
                    }
                }
            }
        });

        */
    }

    /**
     * set the right icon for the right button
     *
     * @param image kreuz, pfeil_rechts, question
     * @author Tim
     */
    private void setImage(String image) {
        ImageButton nextButton = findViewById(R.id.next_button);
        Context context = getApplicationContext();
        switch (image) {
            case ("kreuz"):
                nextButton.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_close));
                break;
            case ("pfeil_rechts"):
                nextButton.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_arrow_forward_red_100_48dp));
                break;
            case ("check"):
                nextButton.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_check));
                break;
            default:
                nextButton.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.kreuz));
        }
    }

    /**
     * jump to a specific slide
     *
     * @param next the slide Number of the slide to switch to
     * @author Tim
     */
    public void jumpToSlide(int next) {


        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ImageButton backButton = findViewById(R.id.back_button);
        Slide slide = lesson.slidearray[next];
        Log.d("nextSlide", slide.getSlideInfo());
        ft.show(slide);
        //hide the current Slide
        ft.hide(lesson.slidearray[slideNumber]);
        ft.show(lesson.slidearray[next]);
        //add the current Slide to the slideBackStack
        lesson.slideBackStack.add(slideNumber);
        slideNumber = next;
        //if the new Slide has no last slide hide the backButton
        if (lesson.getBackSlide() == -99 && slide.back() == -99) {
            backButton.setVisibility(View.GONE);
        } else backButton.setVisibility(View.VISIBLE);

        //if the next slide is a quizSlide set the questionMark
        if (slide instanceof GenQuizSlide && !((GenQuizSlide) slide).gotEvaluated()) {
            setImage("check");
        } else {
            //if the nextSlide has no slide thereafter show the cross
            if ((slide.next() == -99) && (lesson.slidearray.length < (next + 2))) {
                setImage("kreuz");
            } else {
                setImage("pfeil_rechts");
            }
        }

        ft.commit();
    }

    /**
     * wrap up the lesson, count points and return to lesson overview
     *
     * @param currentSlide position index of the slide from where the user left the lesson
     */
    private void goBackToLessonList(int currentSlide) {
        didEvaluationStart = true;
        ImageView button = findViewById(R.id.next_button);
        button.setVisibility(View.GONE);
        button = findViewById(R.id.back_button);
        button.setVisibility(View.GONE);
        RelativeLayout layout = findViewById(R.id.lesson_frame);
        layout.setEnabled(true);

        ValueKeeper v = ValueKeeper.getInstance();
        boolean isEval = false;
        if (lesson.getLessonName().contains("EVALUATION")) {
            isEval = true;
            String switc = lesson.getLessonName().substring(0, lesson.getLessonName().indexOf("EVALUATION"));
            switch (switc) {
                //TODO: implement relevant evaluations for production
                case "timeEval":
                    v.setEvaluationResults(evaluationResults);
                    v.increaseCurrentEvaluation();
                    break;
                case "appEval":

                    //v.removeFirstFromAppList();
                    v.setEvaluationResults(evaluationResults);
                    v.increaseCurrentEvaluation();
                    break;
                default:
                    isEval = false;
            }


        }
        if (lesson.slidearray[currentSlide].isLessonSolved() && (lesson.getProcessingStatus() != 3 && !isEval)) {
            DBHandler db = new DBHandler(this);

            if (lesson.getProcessingStatus() != -99) {
                db.changeLessonToSolved(lesson.getLessonName());
            }

            MethodFactory factory = new MethodFactory(this);
            if (className != null && !className.equals("nothing") && !className.equals("Daily Lessons")) {
                Method method = factory.createMethod("changeTokenCount");
                method.callClassMethod("1");
            }

            //raise the acornCount on success
            Method method2 = factory.createMethod("changeAcornCount");
            method2.callClassMethod(Integer.toString(lesson.getReward()));
            db.close();


            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    onBackPressed();
                }
            }, 4250);
        } else if (lesson.getProcessingStatus() != 3) {

            long nextFreeTime = (System.currentTimeMillis() % Long.MAX_VALUE) + lesson.getDelaytime() * 60000;
            DBHandler db = new DBHandler(this);
            db.setLessonNextFreeTime(lesson.getLessonName(), className, nextFreeTime);
            db.close();
            onBackPressed();
        } else onBackPressed();
    }

    private Slide createSlide(String slideString) {
        if (slideString.startsWith("[LINK]")) {
            return new LinkSlide();
        } else if (slideString.startsWith("[VIDEO]")) {
            return new VideoSlide();
        } else if (slideString.startsWith("[QUIZ]")) {
            return new GenQuizSlide();
        } else if (slideString.startsWith("[CERT]")) {
            return new CertSlide();
        } else if (slideString.startsWith("[QUEST]")) {
            return new QuestSlide();
        } else {
            return new TextImageSlide();
        }
    }


    public void addEvaluationResult(String question, String answer) {

        String prefix = lesson.getLessonName().substring(lesson.getLessonName().indexOf(":") + 1);
        evaluationResults.put(prefix + ":" + question, answer);

    }

    @Override
    public void onPause() {
        super.onPause();
        DBHandler db = new DBHandler(this);
        db.close();
        handler.removeCallbacksAndMessages(null);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.toolbar_activities, menu);
        menu.findItem(R.id.action_options).setVisible(false);
        menu.findItem(R.id.settings).setVisible(false);
        setTitle(lesson.getLessonName());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);

    }
}