package com.foxyourprivacy.privacyRiskInfo.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import com.foxyourprivacy.privacyRiskInfo.DBHandler;
import com.foxyourprivacy.privacyRiskInfo.R;
import com.foxyourprivacy.privacyRiskInfo.ValueKeeper;


public class Home extends FoxITActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);

        Toolbar toolbar = findViewById(R.id.foxit_toolbar);
        setSupportActionBar(toolbar);

        RelativeLayout lessonButton = findViewById(R.id.firstLayout);

        RelativeLayout analysisButton = findViewById(R.id.sixtLayout);

        analysisButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {


                DBHandler dbHandler = new DBHandler(getApplicationContext());
                String[] strings = dbHandler.getSettingsFromDB();
                dbHandler.close();
                Intent i = new Intent(getApplicationContext(), AnalysisResults.class);
                i.putExtra("settings", strings);
                startActivity(i);
            }
        });


        lessonButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), ClassListActivity.class);
                startActivity(i);
            }
        });
    }


    @Override
    public void onStart() {
        super.onStart();
        ValueKeeper v = ValueKeeper.getInstance();
        DBHandler dbHandler = new DBHandler(this);
        //TODO: könnte ein startup slowdown sein
        if ((!dbHandler.checkIfInside(dbHandler.getWritableDatabase(), DBHandler.TABLE_USERDATA, DBHandler.COLUMN_KEY + " = \'analysisDoneBefore\'") || dbHandler.getIndividualValue("analysisDoneBefore").equals("false")) && !v.analysisDoneBefore.equals(true)) {//v.analysisDoneBefore){//!dbHandler.checkIfInside(dbHandler.TABLE_USERDATA,dbHandler.COLUMN_KEY+" = \'firstrun\'")){//!v.wasEvaluationDisplayed){
            dbHandler.close();
            Intent intent = new Intent(getApplicationContext(), OnboardingActivity.class);
            startActivity(intent);
        } else dbHandler.close();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.toolbar_activities, menu);
        menu.findItem(R.id.settings).setVisible(true);
        menu.findItem(R.id.settings).setIcon(R.drawable.smartphone);
        menu.findItem(R.id.action_options).setVisible(false);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.settings) {
            Intent i = new Intent(getApplicationContext(), SettingsActivity.class);
            startActivity(i);
            return true;
        }
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
