package com.foxyourprivacy.privacyRiskInfo.fragments;

import android.app.ListFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.foxyourprivacy.privacyRiskInfo.R;

/**
 * This fragment is used in the settings and answers basic questions of the user
 * Created by Tim on 11.06.2016.
 */
public class FAQFragment extends ListFragment implements AdapterView.OnItemClickListener {
    private String[] questionArray;

    private Context context;
    private boolean[] isAnswerVisible;
    private MyListAdapter_permission adapter;

    /**fills the fragments layout and provides behavior for the permissionHeadline
     * @author Tim
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        context = getActivity().getApplicationContext();
        View itemView = getActivity().getLayoutInflater().inflate(R.layout.fragment_faq, container, false);
        questionArray = new String[]{
                getString(R.string.faqq1) + getString(R.string.faqa1),
                getString(R.string.faqq3) + getString(R.string.faqa3),
                getString(R.string.faqq4) + getString(R.string.faqa4),
                getString(R.string.faqq6) + getString(R.string.faqa6),
                getString(R.string.faqq7) + getString(R.string.faqa7),
                getString(R.string.faqq8) + getString(R.string.faqa8),

        };
        isAnswerVisible = new boolean[questionArray.length];

        return itemView;
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

        if (isAnswerVisible[position]) {
            isAnswerVisible[position] = false;
            adapter.notifyDataSetChanged();
        } else {
            isAnswerVisible[position] = true;
            adapter.notifyDataSetChanged();
        }

    }


    /**
     * class to define the way the settings are displayed in the listView
     *
     * @author Tim
     */
    private class MyListAdapter_permission extends ArrayAdapter<String> {
        public MyListAdapter_permission() {
            //defining the listView's layout for single entries
            super(context, R.layout.layout_faq, questionArray);
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
                itemView = getActivity().getLayoutInflater().inflate(R.layout.layout_faq, parent, false);
            }

            TextView question = itemView.findViewById(R.id.text_question);
            question.setText(questionArray[position].substring(0, questionArray[position].indexOf(";")));

            TextView answer = itemView.findViewById(R.id.text_answer);
            answer.setText(questionArray[position].substring(questionArray[position].indexOf(";") + 1));

            if (isAnswerVisible[position]) {
                answer.setVisibility(View.VISIBLE);
            } else {
                answer.setVisibility(View.GONE);
            }
            return itemView;
        }

    }

}






