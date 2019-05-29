package com.foxyourprivacy.privacyRiskInfo.asynctasks;

import android.content.Context;
import android.os.AsyncTask;

import com.foxyourprivacy.privacyRiskInfo.DBHandler;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * This async task writes to the database so the main thread is not hung up while doing this
 * Created by noah on 11/8/16.
 */

public class DBWrite extends AsyncTask<Object, Void, Void> {

    public DBWrite() {

    }

    @Override
    protected Void doInBackground(Object... objects) {
        Context context = (Context) objects[0];
        DBHandler dbHandler = new DBHandler(context);

        if (objects[1] == "updateLessons") {
            dbHandler.updateLessons((ArrayList<String[]>) objects[2]);
        } else if (objects[1] == "updateClasses") {
            dbHandler.updateClasses((ArrayList<String[]>) objects[2]);
        } else if (objects[1] == "changeLessonToRead") {
            dbHandler.changeLessonToRead((String) objects[2], (String) objects[3]);
        } else if (objects[1] == "changeLessonToSolved") {
            dbHandler.changeLessonToSolved((String) objects[2]);
        } else if (objects[1] == "changeLessonToUnlocked") {
            dbHandler.changeLessonToUnlocked((String) objects[2]);
        } else if (objects[1] == "setLessonNextFreeTime") {
            dbHandler.setLessonNextFreeTime((String) objects[2], (String) objects[3], (long) objects[4]);
        } else if (objects[1] == "insertIndividualData") {
            dbHandler.insertIndividualData((HashMap<String, String>) objects[2]);
        } else if (objects[1] == "changeIndividualValue") {
            dbHandler.changeIndividualValue((String) objects[2], (String) objects[3]);
        } else if (objects[1] == "insertIndividualValue") {
            dbHandler.insertIndividualValue((String) objects[2], (String) objects[3]);
        } else if (objects[1] == "clearAndSetValueKeeper") {
            dbHandler.insertIndividualData((HashMap<String, String>) objects[2]);
        }

        dbHandler.close();
        return null;
    }
}
