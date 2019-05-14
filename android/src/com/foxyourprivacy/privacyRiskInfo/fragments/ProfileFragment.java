package com.foxyourprivacy.privacyRiskInfo.fragments;

import android.app.ListFragment;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.foxyourprivacy.privacyRiskInfo.ProfileListObject;
import com.foxyourprivacy.privacyRiskInfo.R;
import com.foxyourprivacy.privacyRiskInfo.ValueKeeper;

/**
 * This fragment is used in the settings to display and change user info
 * Created by Tim on 11.06.2016.
 */
public class ProfileFragment extends ListFragment implements AdapterView.OnItemClickListener {

    private ProfileListObject[] profileList = {new ProfileListObject("Name:", "Hier kannst du deinen Namen eintragen."), new ProfileListObject("Alter:", "Hier kannst du dein Alter eintragen.")};
    private String[] lengthArray;
    private Context context;
    private int markedProfile = -1;
    private View view;
    private MyListAdapter_permission adapter;

    /**fills the fragments layout and provides behavior for the permissionHeadline
     * @author Tim
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        lengthArray = new String[profileList.length];
        //ValueKeeper vk=ValueKeeper.getInstance();
        // profileList= vk.getProfilList(profileList);

        context = getActivity().getApplicationContext();
        view = getActivity().getLayoutInflater().inflate(R.layout.fragment_profile, container, false);

        final EditText inputField = view.findViewById(R.id.input_input);
        inputField.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (markedProfile != -1) {
                    if (event.getAction() == KeyEvent.ACTION_DOWN) {
                        switch (keyCode) {
                            case KeyEvent.KEYCODE_DPAD_CENTER:
                            case KeyEvent.KEYCODE_ENTER:
                                profileList[markedProfile].setInput(inputField.getText().toString());
                                adapter.notifyDataSetChanged();
                                return true;
                            default:
                                break;
                        }
                    }
                }
                return false;
            }
        });


        RelativeLayout answerFrame = view.findViewById(R.id.answer_frame);
        answerFrame.setVisibility(View.GONE);


        return view;
    }


    /** defines the listView
     * @author Tim
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        //filling the listView with data
        super.onActivityCreated(savedInstanceState);
        //creates the listView
        adapter = new MyListAdapter_permission();   //new ArrayAdapter(getActivity(),android.R.layout.simple_list_item_1,permissionArray);//new MyListAdapter_permission();
        setListAdapter(adapter);
        getListView().setOnItemClickListener(this);
    }

    /**
     * on Item Click the permissionDescriptionFragment is created
     * @author Tim
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        markedProfile = position;

        RelativeLayout answerFrame = this.view.findViewById(R.id.answer_frame);
        answerFrame.setVisibility(View.VISIBLE);

        TextView inputDescription = this.view.findViewById(R.id.text_input_description);
        inputDescription.setText(profileList[position].getInputDescription());
        EditText input = this.view.findViewById(R.id.input_input);
        input.setText(profileList[position].getInput());
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {

        ValueKeeper vk = ValueKeeper.getInstance();
        vk.setProfilList(profileList);
        super.onDestroyView();
    }

    /**
     * class to define the way the settings are displayed in the listView
     *
     * @author Tim
     */
    private class MyListAdapter_permission extends ArrayAdapter<String> {
        public MyListAdapter_permission() {
            //defining the listView's layout for single entries
            super(context, R.layout.layout_profil, lengthArray);
        }

        /**
         * Here ist defined how the XML-Layout is filed  by the data stored in the array.
         *
         * @param position    position in the array used
         * @param convertView
         * @param parent
         * @return
         * @author Tim
         */
        @Override
        @NonNull
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            //convertView has to be filled with layout_app if it's null
            View itemView = convertView;
            if (itemView == null) {
                itemView = getActivity().getLayoutInflater().inflate(R.layout.layout_profil, parent, false);
            }
            TextView inputType = itemView.findViewById(R.id.text_input_type);
            inputType.setText(profileList[position].getInputType());

            TextView input = itemView.findViewById(R.id.text_input_input);
            input.setText(profileList[position].getInput());

            RelativeLayout wholeEntry = itemView.findViewById(R.id.profil_frame);
            if (position == markedProfile) {
                wholeEntry.setBackgroundColor(Color.LTGRAY);
            } else {
                wholeEntry.setBackgroundColor(Color.WHITE);
            }

            return itemView;
        }

    }


}






