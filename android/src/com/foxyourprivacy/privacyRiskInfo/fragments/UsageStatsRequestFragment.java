package com.foxyourprivacy.privacyRiskInfo.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.foxyourprivacy.privacyRiskInfo.R;
import com.foxyourprivacy.privacyRiskInfo.activities.OnboardingActivity;

/**
 * Fragment for the onboarding to ask, if usage stats should be enabled
 * Created by Tim on 25.06.2016.
 */
public class UsageStatsRequestFragment extends Fragment {


    /**
     * defines the layout of this fragment and provides the yes and no button behavior
     *
     * @author Noah
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        View view = inflater.inflate(R.layout.fragment_usagestats_request, container, false);

        LinearLayout button = view.findViewById(R.id.whole_frame);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
            }
        });
        //add the yes button's behavior
        Button yesButton = view.findViewById(R.id.button_yes);
        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((OnboardingActivity) getActivity()).requestUsageStatsPermission();
                cancel();
            }
        });
        //remove this fragment if the no button is pressed
        Button noButton = view.findViewById(R.id.button_no);
        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((OnboardingActivity) getActivity()).askedForStats = true;
                cancel();
            }

        });


        return view;
    }

    private void cancel() {
        getFragmentManager().popBackStack();

    }


}
