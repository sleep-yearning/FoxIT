package com.foxyourprivacy.privacyRiskInfo.activities;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.foxyourprivacy.privacyRiskInfo.R;
import com.foxyourprivacy.privacyRiskInfo.TapAdapter_results;
import com.foxyourprivacy.privacyRiskInfo.fragments.AnalysisRequestFragment;
import com.foxyourprivacy.privacyRiskInfo.fragments.PermissionListFragment;

import java.util.Calendar;
import java.util.Map;

public class AnalysisResults extends FoxITActivity {

    public ViewPager mViewPager; //defines the tabView's content
    public PermissionListFragment permissionList;
    public boolean requestActive = false;
    public Map<String, UsageStats> usageStats;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_screen);


        //on Android versions >= 5 collect and combine usage statistics for installed apps.
        //to display foreground time in the app list
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            UsageStatsManager usageStatsManager = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);
            if (usageStatsManager != null) {
                usageStats = usageStatsManager.queryAndAggregateUsageStats(0, Calendar.getInstance().getTimeInMillis());
            }
        }

        //sets our toolbar as the actionbar
        Toolbar toolbar = findViewById(R.id.foxit_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        //get Settings from DB into ListView
        String[] settingsArray = getIntent().getStringArrayExtra("settings");

        //defining the tabs and the tab bar
        TapAdapter_results adapter = new TapAdapter_results(getFragmentManager(), settingsArray);
        mViewPager = findViewById(R.id.pager);
        mViewPager.setAdapter(adapter);
        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);


        //defining an event for clicking the tab bar, this is necessary for hiding the permissionList in case the tabBar is pressed
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            //if a new tab is pressed
            public void onTabSelected(TabLayout.Tab tab) {
                //switches the tabs manually, this is necessary because the default behavior is overwritten
                mViewPager.setCurrentItem(tab.getPosition());
                FragmentManager manager = getFragmentManager();
                //sets the Lists visible and the permissionList invisible if currently visible
                mViewPager.setVisibility(View.VISIBLE);
                if (manager.findFragmentByTag("permission") != null) {
                    manager.popBackStack();
                }
            }

            @Override
            //has to be implemented but is not used
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            //called if the current tab is pressed
            public void onTabReselected(TabLayout.Tab tab) {
                FragmentManager manager = getFragmentManager();
                //setts the Lists visible and the permissionList if visible invisible
                mViewPager.setVisibility(View.VISIBLE);
                if (manager.findFragmentByTag("permission") != null) {
                    manager.popBackStack();
                }
            }
        });


    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.settings:
                if (!requestActive) {
                    //add the TradeRequestFragment to the activity's context
                    FragmentManager manager = getFragmentManager();
                    FragmentTransaction transaction = manager.beginTransaction();
                    //AnalysisRequestFragment tradeRequest = new AnalysisRequestFragment();
                    AnalysisRequestFragment analysisRequest = new AnalysisRequestFragment();
                    //add the fragment to the count_frame RelativeLayout
                    transaction.replace(R.id.request_frame, analysisRequest, "count");
                    transaction.addToBackStack("analysisRequest");
                    transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                    transaction.commit();
                    requestActive = true;
                    return true;
                }
                return false;

            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.toolbar_activities, menu);
        menu.findItem(R.id.action_options).setVisible(false);
        return true;
    }


    /**
     * methodLeft that allows me to set the app list visible and destroy permissionListFragment
     * by clicking the fragments headline
     *
     * @author Tim
     */
    public void onPermissionListHeadlinePressed() {
        //if a fragment was created and added to the BackStack (must be PermissionListFragment)
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            if (permissionList != null) {
                permissionList = null;
            }
            getFragmentManager().popBackStack(); //destroy it
            FrameLayout appFrame = findViewById(R.id.appFrame);
            appFrame.setVisibility(View.VISIBLE); //and make the appList visible
            mViewPager.setVisibility(View.VISIBLE);

        } else {
            super.onBackPressed();
        }
    }

    /**
     * sets the visibility  of the scrollHint
     *
     * @param isVisible true= setts it visible, false=invisible
     * @author Tim
     */
    public void setScrollMessageVisiblility(boolean isVisible) {
        RelativeLayout scrollMessage = findViewById(R.id.hintFrame);
        if (isVisible) {
            scrollMessage.setVisibility(View.VISIBLE);
        } else {
            scrollMessage.setVisibility(View.GONE);
        }
    }

    /**
     * overrides the behavior of the backButton for it to properly support Fragments and Fragments in Fragments (ChildFragments)
     * @author Tim
     */
    @Override
    public void onBackPressed() {
        //if there is a fragment
        if (requestActive) {
            getFragmentManager().popBackStackImmediate();
            requestActive = false;
        } else if (getFragmentManager().getBackStackEntryCount() > 0) {
            //if PermissionDescriptionFragment exists
            if (permissionList != null && permissionList.getChildFragmentManager().getBackStackEntryCount() > 0) {
                permissionList.setListVisible();//make the hidden permissionList visible again
                permissionList.getChildFragmentManager().popBackStack();//destroy the PermissionDescriptionFragment
            } else {//if only PermissionListFragment exists
                if (permissionList != null) {
                    permissionList = null;
                }
                getFragmentManager().popBackStack(); //destroy PermissionListFragment
                FrameLayout appFrame = findViewById(R.id.appFrame);
                appFrame.setVisibility(View.VISIBLE); //make the hidden appList visible again
                mViewPager.setVisibility(View.VISIBLE);
            }
        } else {//if no fragments exist go to home
            Intent i = new Intent(this, Home.class);
            startActivity(i);
            //super.onBackPressed();
        }
    }

}
