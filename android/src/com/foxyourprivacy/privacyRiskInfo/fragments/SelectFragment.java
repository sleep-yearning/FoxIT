package com.foxyourprivacy.privacyRiskInfo.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.foxyourprivacy.privacyRiskInfo.DBHandler;
import com.foxyourprivacy.privacyRiskInfo.R;

public class SelectFragment extends android.app.Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_textsize, container, false);
        DBHandler dbHandler = new DBHandler(getActivity());
        String current = dbHandler.getIndividualValue("textsize");
        dbHandler.close();
        if (current == "notfound")
            ((RadioButton) view.findViewById(R.id.textsizenormal)).setChecked(true);
        else ((RadioButton) view.findViewById(Integer.valueOf(current))).setChecked(true);

        RadioGroup radio = view.findViewById(R.id.textsizeselection);
        radio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // checkedId is the RadioButton selected
                int size = R.id.textsizenormal;

                switch (checkedId) {
                    case R.id.textsizesmall:
                        size = R.id.textsizesmall;
                        break;
                    case R.id.textsizesmaller:
                        size = R.id.textsizesmaller;
                        break;
                    case R.id.textsizenormal:
                        size = R.id.textsizenormal;
                        break;
                    case R.id.textsizebigger:
                        size = R.id.textsizebigger;
                        break;
                    case R.id.textsizebig:
                        size = R.id.textsizebig;
                        break;

                }
                DBHandler dbHandler = new DBHandler(getActivity());
                dbHandler.insertIndividualValue("textsize", String.valueOf(size));
                dbHandler.close();
            }
        });

        return view;
    }
}
