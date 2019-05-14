package com.foxyourprivacy.privacyRiskInfo.fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.foxyourprivacy.privacyRiskInfo.R;
import com.foxyourprivacy.privacyRiskInfo.activities.Analysis;
import com.foxyourprivacy.privacyRiskInfo.activities.OnboardingActivity;

/**
 * The fourth fragment of the onboarding screen
 * Created by Hannah on 25.06.2016.
 */
public class OnboardingFragment4 extends Fragment {

    private Button analysisButton;


    /**
     * @author Hannah
     */
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle saveInstanceState) {

        View view = inflater.inflate(R.layout.fragment_onboarding4, container, false);
        Button button = view.findViewById(R.id.button_accept);
        analysisButton = button;
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if usage stats permission hasn't been given, android >=5 and has not been answered with no
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
                        && !(((OnboardingActivity) getActivity()).askedForStats)
                        && !(((OnboardingActivity) getActivity()).hasUsageStatsPermission(getActivity()))) {
                    //show requesting fragment
                    FragmentManager fm = getFragmentManager();
                    FragmentTransaction transaction = fm.beginTransaction();
                    UsageStatsRequestFragment requestFragment = new UsageStatsRequestFragment();
                    transaction.add(R.id.count_frame, requestFragment, "usagestatrequest");
                    transaction.addToBackStack("requestFragment");
                    transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                    transaction.commit();

                } else {

                    analysisButton.setText(null);
                    analysisButton.setVisibility(View.GONE);
                    Intent i = new Intent(getActivity(), Analysis.class);
                    startActivity(i);
                }
            }
        });

        return view;
    }

}
