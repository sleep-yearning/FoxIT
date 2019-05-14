package com.foxyourprivacy.privacyRiskInfo;

import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;

import com.foxyourprivacy.privacyRiskInfo.fragments.OnboardingFragment;
import com.foxyourprivacy.privacyRiskInfo.fragments.OnboardingFragment2;
import com.foxyourprivacy.privacyRiskInfo.fragments.OnboardingFragment3;
import com.foxyourprivacy.privacyRiskInfo.fragments.OnboardingFragment4;


/**
 * Created by Hannah on 04.08.2016.
 * Class which defines the Tabs in Onboarding
 */
public class TapAdapter_onboarding extends FragmentPagerAdapter {

    /**
     * @param fm the fragment manager to use
     * @author Hannah
     */
    public TapAdapter_onboarding(FragmentManager fm) {
        super(fm);
    }

    /**
     * Fills the TabAdapter with the Fragments to be displayed
     *
     * @param position describes which tab is pressed
     * @return Fragment that belongs on that position
     * @author Hannah
     */
    @Override
    public android.app.Fragment getItem(int position) {
        switch (position) {
            case 0: {
                return new OnboardingFragment();
            }
            case 1: {
                return new OnboardingFragment2();
            }
            case 2: {
                return new OnboardingFragment3();
            }

            case 3: {
                return new OnboardingFragment4();

            }
            default: {
                return new OnboardingFragment();
            }
        }

    }

    /**
     * defines the number of tabs presented (4)
     *
     * @return the number of Tabs
     * @author Hannah
     */
    @Override
    public int getCount() {
        return 4;
    }

    /**
     * define how the Tabs are called but we don't want them to be named
     *
     * @param position which tab is currently named
     * @return the name of the tab
     * @author Hannah
     */
    @Override
    public CharSequence getPageTitle(int position) {
        return "";
    }
}