package com.foxyourprivacy.privacyRiskInfo.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.foxyourprivacy.privacyRiskInfo.R;

/**
 * This fragment shows legal information about the libGDX framework and the apache 2 license in the settings
 * Created by Tim on 25.06.2016.
 */
public class LegalInformationFragment_libGDX extends Fragment {



    /**fills the layout with the permission name and description
     * @author Tim
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        return inflater.inflate(R.layout.fragment_legal_information_libgdx, container, false);

    }

    /**
     * Enables to pass arguments to the fragment
     * @author Tim
     */
    @Override
    public void setArguments(Bundle arg) {

    }

}
