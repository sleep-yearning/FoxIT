package com.foxyourprivacy.privacyRiskInfo.slides;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.foxyourprivacy.privacyRiskInfo.R;
import com.foxyourprivacy.privacyRiskInfo.SlideImageGetter;

/**
 * A new Slide to replace TextSlide and WebImageSlide to show Text and Images in one slide together.
 * Fetches images with the SlideImageGetter.
 * Created by noah on 04.06.17.
 */

public class TextImageSlide extends Slide {
    private String slidetext = "";
    private View rootView;

    public TextImageSlide() {
        super();
    }

    public void setArguments(Bundle slidecontent) {
        super.setArguments(slidecontent);
        slidetext = slideInfo.replace(";", "<br>");


    }

    @Override
    public void fillLayout() {
        //sets the displayed text
        TextView text = rootView.findViewById(R.id.textimageslide);
        SlideImageGetter sig = new SlideImageGetter(text, getContext());
        if (getContext() == null) Log.d("isnull", ":(");
        Spanned htmlspanned;

        /*

        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(metrics);
        int width = (int)(metrics.widthPixels*0.95);
        slidetext = slidetext.replaceAll("style=\"","style=\"height:"+(width*2/3)+"px; width:"+width+"px; ");
        Log.d("TextImageSlide",slidetext);*/

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            htmlspanned = Html.fromHtml(slidetext, Html.FROM_HTML_MODE_LEGACY, sig, null);
        } else {
            htmlspanned = Html.fromHtml(slidetext, sig, null);
        }
        text.setText(htmlspanned);
        text.setTextSize(TypedValue.COMPLEX_UNIT_PX, getActivity().getResources().getDimension(textsize));
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.layout_slide_textimage, container, false);
        fillLayout();
        return rootView;
    }
}
