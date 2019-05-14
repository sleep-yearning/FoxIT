package com.foxyourprivacy.privacyRiskInfo.activities;

import android.annotation.TargetApi;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;

import com.foxyourprivacy.privacyRiskInfo.R;
import com.foxyourprivacy.privacyRiskInfo.TapAdapter_onboarding;
import com.foxyourprivacy.privacyRiskInfo.ValueKeeper;

public class OnboardingActivity extends FoxITActivity {

    public boolean askedForStats = false;

    /**
     * @author Hannah
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);
        //defining the tabs and the tab bar
        TapAdapter_onboarding adapter = new TapAdapter_onboarding(getFragmentManager());
        ViewPager mViewPager = findViewById(R.id.pager);
        mViewPager.setAdapter(adapter);
        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

    }



    @Override
    public void onStart() {
        super.onStart();
        ValueKeeper v = ValueKeeper.getInstance();
        //DBHandler db=new DBHandler(this,null,null,1);
        if (!v.onboardingStartedBefore) {

            //db.changeIndividualValue("onboardingStartedBefore",Boolean.toString(true));
            SettingsActivity sa = new SettingsActivity();
            sa.updateLessions(this, (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE));
            sa.updatePermissions(this, (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE));
            sa.updateSettings(this, (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE));
            v.onboardingStartedBefore = true;
        }


        //db.close();
    }

    public void requestUsageStatsPermission() {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
                && !hasUsageStatsPermission(this)) {
            startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public boolean hasUsageStatsPermission(Context context) {
        AppOpsManager appOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow("android:get_usage_stats",
                android.os.Process.myUid(), context.getPackageName());
        return mode == AppOpsManager.MODE_ALLOWED;
    }

    @Override
    public void onBackPressed() {

    }
}
