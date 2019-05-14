package com.foxyourprivacy.privacyRiskInfo.asynctasks;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.Settings;

import com.foxyourprivacy.privacyRiskInfo.activities.Analysis;

import java.lang.ref.WeakReference;

import static com.foxyourprivacy.privacyRiskInfo.activities.Analysis.combineArraysP2;


/**
 * fetches all installed apps and the device settings to pass it into the database
 * does the phone analysis in an async task and executes another async task at the end, which continues the app-flow
 * Created by noah on 11/8/16.
 * @author Noah
 */
public class ExternAnalysis extends AsyncTask {

    private final WeakReference<Context> analysisRef;

    public ExternAnalysis(Context con) {
        analysisRef = new WeakReference<>(con);
    }

    /**
     * fetches all installed apps and the device settings to pass it into the database
     *
     * @author Noah
     */
    @Override
    protected Object doInBackground(Object[] objects) {
        // Get all apps
        ((Analysis) objects[0]).getALL_APPS();
        // Get all settings
        Uri uri_global = Settings.Global.CONTENT_URI;
        String[] proj_global = new String[]{Settings.Global.NAME, Settings.Global.VALUE};
        Uri uri_secure = Settings.Secure.CONTENT_URI;
        String[] proj_secure = new String[]{Settings.Secure.NAME, Settings.Secure.VALUE};
        ContentValues[] firstArray = ((Analysis) objects[0]).getSETTINGS(uri_global, proj_global);
        ContentValues[] secondArray = ((Analysis) objects[0]).getSETTINGS(uri_secure, proj_secure);
        ContentValues[] resultArray = combineArraysP2(firstArray, secondArray);
        resultArray[resultArray.length - 2] = ((Analysis) objects[0]).getLOCATION_MODE();
        resultArray[resultArray.length - 1] = ((Analysis) objects[0]).getPASSWORT_QUALITY();
        return resultArray;
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        //TODO: effektiverer Weg möglich? in DB schreiben und Array als intent übergeben gleichzeitig z.b.?
        new DBWrite().execute(analysisRef.get(), "addParamColumn", o);
        new GetSettingsAsync(analysisRef.get()).execute();
    }
}
