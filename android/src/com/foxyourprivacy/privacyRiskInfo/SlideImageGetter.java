package com.foxyourprivacy.privacyRiskInfo;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * This is the latest class that loads the image from an url into TextImageSlides (asychronously).
 * Created by noah on 13.09.17.
 */

public class SlideImageGetter implements Html.ImageGetter {
    private final TextView container;
    private final WeakReference<Context> contextWeakReference;


    public SlideImageGetter(TextView v, Context context) {
        container = v;
        contextWeakReference = new WeakReference<>(context);
    }

    @Override
    public Drawable getDrawable(String source) {
        DownloadDrawable ddrawable = new DownloadDrawable();

        AsyncDrawableLoader loader = new AsyncDrawableLoader(ddrawable);
        loader.execute(source);

        return ddrawable;

    }

    private class AsyncDrawableLoader extends AsyncTask<String, Void, Drawable> {
        final DownloadDrawable ddrawable;

        public AsyncDrawableLoader(DownloadDrawable dd) {
            this.ddrawable = dd;
        }

        @Override
        protected Drawable doInBackground(String... params) {
            String src = params[0];
            Drawable result = null;
            try {
                result = loadDrawable(src);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(Drawable drawable) {
            if (drawable != null) {
                Log.d("onPost", "height: " + drawable.getIntrinsicHeight());
                Log.d("onPost", "width: " + drawable.getIntrinsicWidth());


                DisplayMetrics metrics = new DisplayMetrics();
                WindowManager wm = (WindowManager) contextWeakReference.get().getSystemService(Context.WINDOW_SERVICE);
                wm.getDefaultDisplay().getMetrics(metrics);

                int width = (int) (metrics.widthPixels * 0.95);
                int height = (width * drawable.getIntrinsicHeight() / drawable.getIntrinsicWidth());

                Log.d("SlideImageGetter", "width: " + width);
                Log.d("SlideImageGetter", "height: " + height);


                ddrawable.setBounds(0, 0, width, height);
                ddrawable.drawable = drawable;
                SlideImageGetter.this.container.setHeight(SlideImageGetter.this.container.getHeight() + height);
                SlideImageGetter.this.container.invalidate();

            }

        }

        public Drawable loadDrawable(String urls) throws IOException {
            URL url = new URL(urls);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            try {

                InputStream in = new BufferedInputStream(connection.getInputStream());
                Drawable drawable = Drawable.createFromStream(in, "src");


                DisplayMetrics metrics = new DisplayMetrics();
                WindowManager wm = (WindowManager) contextWeakReference.get().getSystemService(Context.WINDOW_SERVICE);
                wm.getDefaultDisplay().getMetrics(metrics);

                int width = (int) (metrics.widthPixels * 0.94);
                int height = (width * drawable.getIntrinsicHeight() / drawable.getIntrinsicWidth());
                Log.d("SlideImageGetter", "width: " + width);
                Log.d("SlideImageGetter", "height: " + height);

                drawable.setBounds(0, 0, width, height);
                return drawable;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            } finally {
                connection.disconnect();
            }
        }

    }
}

