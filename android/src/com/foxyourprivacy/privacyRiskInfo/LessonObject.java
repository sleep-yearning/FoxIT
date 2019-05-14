package com.foxyourprivacy.privacyRiskInfo;

import android.os.Parcel;
import android.os.Parcelable;

import com.foxyourprivacy.privacyRiskInfo.slides.Slide;

import java.util.ArrayList;

/**
 * LessonObjects hold all the information needed to display and interact with a lesson in the LessonActivity
 * the Slides and everything that is shown in the LessonActivity is controlled in this class
 * Created by Tim on 18.07.2016.
 */
public class LessonObject implements Parcelable {
    public static final Parcelable.Creator<LessonObject> CREATOR = new Parcelable.Creator<LessonObject>() {
        public LessonObject createFromParcel(Parcel in) {
            return new LessonObject(in);
        }

        public LessonObject[] newArray(int size) {
            return new LessonObject[size];
        }
    };
    private final String lessonName; //name~
    private final String type;//Lesson-type
    private final int delaytime; //how much time has to pass if the lesson is blocked until it is unlocked again
    private final long nextfreetime; //the lesson is blocked to this point in time
    private final int reward; //the amount of acorn gained by solving this lesson
    private final String slides; //the slides stored as one large String
    //Hashmap that stores every slide already generated
    public Slide[] slidearray;
    //BackStack for tracking which slide the back button should access
    public ArrayList<Integer> slideBackStack = new ArrayList<>();
    //Score for lessons with QuizSlides
    public int score = 0;
    public int maxscore = 0;
    private int position = 99;
    private int processingStatus; //solved~

    /**
     * @author Tim
     */
    public LessonObject(String name, String slides, String type, int delay, long freetime, int status, int acorn, int position) {
        //filling the lessonInfoHashMap by spliting the lessonDescriptionString

        this.slides = slides;
        lessonName = name;
        processingStatus = status;
        this.type = type;
        delaytime = delay;    //how much time has to pass if the lesson is blocked until it is unlocked again
        nextfreetime = freetime; //the lesson is blocked to this point in time
        reward = acorn;   //the amount of acorn gained by solving this lesson
        this.position = position;

    }

    private LessonObject(Parcel in) {
        lessonName = in.readString();
        type = in.readString();
        slides = in.readString();
        delaytime = in.readInt();
        position = in.readInt();
        reward = in.readInt();
        processingStatus = in.readInt();
        nextfreetime = in.readLong();
    }

    /**
     * @author Tim
     */
    public String getLessonName() {
        return lessonName;
    }

    /**
     * @author Tim
     */
    public int getProcessingStatus() {
        return processingStatus;
    }

    public void setProcessingStatus(int status) {
        processingStatus = status;
    }

    /**
     * @return the slide to return to or "noBack" if the backstack is empty
     * @author Tim
     */
    public int getBackSlide() {

        if (slideBackStack.isEmpty()) {
            return -99;
        } else {
            return slideBackStack.get(slideBackStack.size() - 1);
        }


    }

    public String getType() {
        return type;
    }

    public int getDelaytime() {
        return delaytime;
    }

    public long getNextfreetime() {
        return nextfreetime;
    }

    public int getReward() {
        return reward;
    }

    public String getSlides() {
        return slides;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(lessonName);
        parcel.writeString(type);
        parcel.writeString(slides);
        parcel.writeInt(delaytime);
        parcel.writeInt(position);
        parcel.writeInt(reward);
        parcel.writeInt(processingStatus);
        parcel.writeLong(nextfreetime);
    }
}

