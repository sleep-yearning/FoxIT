package com.foxyourprivacy.privacyRiskInfo.asynctasks;

import android.content.Context;
import android.os.AsyncTask;

import com.foxyourprivacy.privacyRiskInfo.DBHandler;

/**
 * This async task can read the database contents, so it doesn't have to be done on main thread.
 * returns are not implemented though, that's why it is not used atm
 * Created by noah on 11/8/16.
 */


//TODO implement return in onpostexecute so that the class can be used
class DBRead extends AsyncTask<Object, Void, Object> {

    public DBRead() {

    }

    @Override
    protected Object doInBackground(Object... objects) {
        Context context = (Context) objects[0];
        DBHandler dbHandler = new DBHandler(context);
        Object result;
        if ("getClasses".equals(objects[1])) {
            result = dbHandler.getClasses();
        } else if ("getNumberOfSolvedLessons".equals(objects[1])) {
            result = dbHandler.getNumberOfSolvedLessons((String) objects[2]);
        } else if ("getLessonsFromDB".equals(objects[1])) {
            result = dbHandler.getLessonsFromDB((String) objects[2]);
        } else if ("getPermissionDescription".equals(objects[1])) {
            result = dbHandler.getPermissionDescription((String) objects[2]);
        } else if ("getSettingsFromDB".equals(objects[1])) {
            result = dbHandler.getSettingsFromDB();
        } else if ("getIndividualData".equals(objects[1])) {
            result = dbHandler.getIndividualData();
        } else if ("getIndividualValue".equals(objects[1])) {
            result = dbHandler.getIndividualValue((String) objects[2]);
        }else{
            result = "no method found";
        }


        dbHandler.close();
        return result;
    }
}
