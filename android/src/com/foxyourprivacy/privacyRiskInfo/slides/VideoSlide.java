package com.foxyourprivacy.privacyRiskInfo.slides;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.foxyourprivacy.privacyRiskInfo.R;

import java.util.regex.Pattern;

/**
 * Created by noah on 04.06.17.
 * Video Slide shows either a video via MediaPlayer or a WebView, where "iframe" is found in the content to enable more embedded videos
 */

public class VideoSlide extends Slide {

    private String content;
    private VideoView videoView;


    @Override
    public void fillLayout() {

    }

    @Override
    public void setArguments(Bundle arg) {
        super.setArguments(arg);
        content = slideInfo.replaceFirst(Pattern.quote("[VIDEO]"), "").replace(";","");


    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_slide_video, container, false);
        videoView = view.findViewById(R.id.videoView);
        if (!content.contains("iframe")) {
            final ImageButton play = view.findViewById(R.id.playbutton);
            MediaController mc = new MediaController(getContext());
            final TextView waittext = view.findViewById(R.id.waittext);

            waittext.setText(R.string.videowaittext);
            waittext.setVisibility(View.VISIBLE);


            //"https://archive.org/download/ksnn_compilation_master_the_internet/ksnn_compilation_master_the_internet_512kb.mp4"

            Log.d("URL", content);
            Uri uri = Uri.parse(content);
            videoView.setVideoURI(uri);
            videoView.requestFocus();
            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.setLooping(true);
                    Log.d("prepared", mp.toString());
                    play.setVisibility(View.VISIBLE);
                    waittext.setVisibility(View.INVISIBLE);


                }
            });


            videoView.setMediaController(mc);
            mc.setAnchorView(videoView);


            play.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (videoView.isPlaying()) {
                        videoView.pause();
                        play.setImageResource(R.drawable.ic_play);
                    } else {
                        videoView.start();
                        play.setImageResource(R.drawable.ic_pause);
                    }
                }
            });
        }else {
            ViewGroup.LayoutParams params = videoView.getLayoutParams();
            params.height = 0;
            videoView.setLayoutParams(params);
            WebView webView = view.findViewById(R.id.webvideo);
            webView.getSettings().setLoadWithOverviewMode(true);
            webView.getSettings().setUseWideViewPort(true);

            String videoSite = "<html><body>" + content + "</body></html>";

            DisplayMetrics metrics = new DisplayMetrics();
            WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
            wm.getDefaultDisplay().getMetrics(metrics);

            int width = (metrics.widthPixels);

            int height = (int) (width * 0.75);
            videoSite = videoSite.replaceFirst("height=\"[0-9]*", "height=\"" + String.valueOf(height));
            videoSite = videoSite.replaceFirst("width=\"[0-9]*", "width=\"" + String.valueOf(width));
            webView.setWebViewClient(new WebViewClient(){

                @Override
                public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                    return false;
                }

            });
            WebSettings wsettings = webView.getSettings();
            wsettings.setJavaScriptEnabled(true);
            webView.loadData(videoSite, "text/html", "utf-8");
        }

        return view;
    }

}
