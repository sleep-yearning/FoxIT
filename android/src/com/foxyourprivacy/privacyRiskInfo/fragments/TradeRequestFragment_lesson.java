package com.foxyourprivacy.privacyRiskInfo.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.foxyourprivacy.privacyRiskInfo.R;
import com.foxyourprivacy.privacyRiskInfo.ValueKeeper;
import com.foxyourprivacy.privacyRiskInfo.activities.LessonListActivity;
import com.foxyourprivacy.privacyRiskInfo.lessonmethods.Method;
import com.foxyourprivacy.privacyRiskInfo.lessonmethods.MethodFactory;

/**
 * Fragment that asks a user wether a transaction should be executed (lesson unlocking)
 * Created by Tim on 25.06.2016.
 */
public class TradeRequestFragment_lesson extends Fragment {
    private View view; //the fragments view, useful for usages outside of onCreateView
    private int price = 1;  //the token amount to unlock this lesson
    private String articleOfCommerce; //the lesson to be unlocked

    /**
     * @author Tim
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        TextView priceText = view.findViewById(R.id.text_amount);
        priceText.setText(String.format("%d", price));
    }

    /**
     * defines the layout of this fragment and provides the yes and no button behavior
     * @author Tim
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        view = inflater.inflate(R.layout.fragment_trade_request_lesson, container, false);

        LinearLayout button = view.findViewById(R.id.whole_frame);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        //add the yes button's behavior
        Button yesButton = view.findViewById(R.id.button_yes);
        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValueKeeper o = ValueKeeper.getInstance();
                if (price > o.getTokenCount()) {
                    //display the animation description
                    Toast.makeText(getActivity().getApplicationContext(),
                            "Du hast nicht genug Tokens um diese Lektion zu kaufen.", Toast.LENGTH_SHORT).show();


                } else {
                    boolean tradeSuccessful = false;
                    Activity activity = getActivity();
                    //call the purchase callClassMethod of LessonListActivity
                    if (activity instanceof LessonListActivity) {
                        LessonListActivity lessonListActivity = (LessonListActivity) activity;
                        tradeSuccessful = lessonListActivity.purchase(articleOfCommerce);
                    }

                    //if the trade was successful change the tokenCount
                    if (tradeSuccessful) {
                        MethodFactory m = new MethodFactory(getActivity());
                        Method changeTokenCount = m.createMethod("changeTokenCount");
                        changeTokenCount.callClassMethod("-" + Integer.toString(price));
                    }
                }
                //remove this fragment after the trade is finished
                getActivity().onBackPressed();//getFragmentManager().beginTransaction().remove(thisFragment).commit();
            }
        });
        //remove this fragment if the no button is pressed
        Button noButton = view.findViewById(R.id.button_no);
        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();//getFragmentManager().beginTransaction().remove(thisFragment).commit();
            }

        });

        return view;
    }

    /**
     * tells the fragment the information needed for this trade, called whenever this fragment gets created
     *
     * @param arg bundle of information to be set (price and item to be bought)
     * @author Tim
     */
    public void setArguments(Bundle arg) {
        if (arg.getInt("price") != 0) {
            price = arg.getInt("price");
        }
        articleOfCommerce = arg.getString("target");
    }

}
