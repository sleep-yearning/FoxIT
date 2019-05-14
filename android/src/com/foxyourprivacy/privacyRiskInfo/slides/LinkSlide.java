package com.foxyourprivacy.privacyRiskInfo.slides;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.foxyourprivacy.privacyRiskInfo.R;

import java.util.regex.Pattern;

/**
 * A slide that contains a webview to show a Website of interest
 * Created by noah on 04.06.17.
 */

//TODO implement webview
public class LinkSlide extends Slide {

    View rootView;
    @Override
    public void fillLayout() {
        WebView webView = rootView.findViewById(R.id.linkWebView);
        webView.loadUrl(slideInfo);
    }

    @Override
    public void setArguments(Bundle arg) {
        super.setArguments(arg);
        slideInfo = slideInfo.replaceFirst(Pattern.quote("[LINK]"), "");
        slideInfo = slideInfo.replaceAll(";", "");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.layout_slide_link, container, false);
        fillLayout();
        return rootView;
    }
}
