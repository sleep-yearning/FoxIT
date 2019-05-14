package com.foxyourprivacy.privacyRiskInfo.asynctasks;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;

import com.foxyourprivacy.privacyRiskInfo.DBHandler;
import com.foxyourprivacy.privacyRiskInfo.ValueKeeper;
import com.foxyourprivacy.privacyRiskInfo.activities.AnalysisResults;

import java.lang.ref.WeakReference;

/**
 * async task that gets called from ExternAnalysis to continue the views for the user
 * Created by noah on 11/9/16.
 */

class GetSettingsAsync extends AsyncTask<Void, Void, String[]> {

    private final WeakReference<Context> context;

    public GetSettingsAsync(Context con) {
        context = new WeakReference<>(con);
    }

    @Override
    protected String[] doInBackground(Void... voids) {
        DBHandler dbHandler = new DBHandler(context.get());
        String[] result = dbHandler.getSettingsFromDB();
        //dbHandler.insertIndividualValue("firstrun","true");
        ValueKeeper v = ValueKeeper.getInstance();
        v.analysisDoneBefore = true;
        dbHandler.insertIndividualValue("analysisDoneBefore", Boolean.toString(true));
        dbHandler.close();
        return result;
    }

    @Override
    protected void onPostExecute(final String[] strings) {
        super.onPostExecute(strings);
        // Timer: Open AnalysisResults after 3 Seconds
        Handler mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(context.get().getApplicationContext(), AnalysisResults.class);
                i.putExtra("settings", strings);
                context.get().startActivity(i);
            }
        }, 3000L);
    }

}
