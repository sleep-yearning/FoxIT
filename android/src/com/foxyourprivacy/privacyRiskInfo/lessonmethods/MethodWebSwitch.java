package com.foxyourprivacy.privacyRiskInfo.lessonmethods;

import android.content.Intent;
import android.net.Uri;

/**
 * A method to be used inside the lessons that opens a webpage
 * Created by Tim on 30.07.2016.
 */
//provides a callClassMethod for jumping to web pages
public class MethodWebSwitch extends Method {

    /**
     * open the described website in the smartphone's browser
     *
     * @param webPageUrl url with http up front
     */

    @Override
    public void callClassMethod(String webPageUrl) {
        //open the website by intend
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(webPageUrl));
        activity.startActivity(i);
    }
}
