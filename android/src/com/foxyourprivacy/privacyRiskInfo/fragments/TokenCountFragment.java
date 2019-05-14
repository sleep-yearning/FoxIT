package com.foxyourprivacy.privacyRiskInfo.fragments;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.foxyourprivacy.privacyRiskInfo.R;
import com.foxyourprivacy.privacyRiskInfo.ValueKeeper;

/**
 * This fragment is shown when the count of tokens changes. Either a lesson has been solved (+1) or a lesson has been unlocked (-1)
 * Created by Tim on 25.06.2016.
 */
public class TokenCountFragment extends Fragment {
    private View view; //the fragments view, useful for usages outside of onCreateView

    /**
     * @author Tim
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //fetch the current tokenCount from the ValueKeeper
        int tokenCount = ValueKeeper.getInstance().getTokenCount();
        //and display it
        TextView text = view.findViewById(R.id.text_token_count);
        text.setText(String.format("%d", tokenCount));
    }

    /**
     * @author Tim
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        view = inflater.inflate(R.layout.fragment_token_count, container, false);
        return view;
    }

    /**
     * Method for changing the displayed tokenCount from the outside
     * Called in MethodChangeTokenCount
     *
     * @param newText the new text resembling the new acornCount
     * @author Tim
     */
    public void changeText(String newText) {
        TextView text = view.findViewById(R.id.text_token_count);
        text.setText(newText);
        text.setTextColor(Color.BLACK);

    }


}
