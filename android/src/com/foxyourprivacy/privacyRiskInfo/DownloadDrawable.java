package com.foxyourprivacy.privacyRiskInfo;

import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

/**
 * A drawable class to load images from the web and insert them in the lesson-slides
 * Created by noah on 13.09.17.
 */

class DownloadDrawable extends BitmapDrawable {
    Drawable drawable;


    @Override
    public void draw(Canvas canv) {
        if (drawable != null) {
            drawable.draw(canv);
        }
    }
}
