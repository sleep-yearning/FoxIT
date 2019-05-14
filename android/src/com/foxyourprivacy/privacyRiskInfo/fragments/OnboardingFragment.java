package com.foxyourprivacy.privacyRiskInfo.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.foxyourprivacy.privacyRiskInfo.R;

/**
 * The first fragment of the onboarding screen
 * Created by Hannah on 25.06.2016.
 */
public class OnboardingFragment extends Fragment {


    /**
     * @author Hannah
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        return inflater.inflate(R.layout.fragment_onboarding1, container, false);


    }


}
