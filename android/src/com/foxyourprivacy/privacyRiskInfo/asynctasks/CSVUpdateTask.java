package com.foxyourprivacy.privacyRiskInfo.asynctasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.foxyourprivacy.privacyRiskInfo.DBHandler;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

/**
 * This async task loads csv files from the server and updates the content in the app
 * Created by noah on 11/8/16.
 */

public class CSVUpdateTask extends AsyncTask<Object, Void, Integer> {


    public CSVUpdateTask() {

    }

    @Override
    protected Integer doInBackground(Object... objects) {
        Context thecontext = (Context) objects[0];
        String url = (String) objects[1];
        if (objects[2] != null) {
            DBHandler dbHandler = new DBHandler(thecontext);
            Log.d("CSVUpdater", (String) objects[2]);
            InputStream is = null;
            try {
                is = getStream(url);
                if (is == null) {
                    is = getHttpStream(url);
                }
            } catch (Exception e) {
                Log.d("CSVDownTask", "inputStream not valid" + e);
            }

            if (is != null) {
                if (objects[2].equals("lessons")) dbHandler.updateLessons(readLessonStream(is));
                if (objects[2].equals("classes")) dbHandler.updateClasses(readStream(is));
                dbHandler.close();
                return 0;
            } else if (objects.length > 2 && objects[3] != null) {
                Log.d("CSVDownTask", "InputStream null, Internet Failure.");
                if (objects[2].equals("lessions"))
                    dbHandler.updateLessons((ArrayList<String[]>) objects[3]);
                if (objects[2].equals("classes"))
                    dbHandler.updateClasses((ArrayList<String[]>) objects[3]);
                dbHandler.close();

                return -1;
            }
        }
        return -2;
    }


    private InputStream getStream(String url) {
        InputStream is = null;
        try {
            //configuration of the connection
            URL theURL = new URL(url);
            if (theURL.getProtocol().toLowerCase().equals("https")) {
                HttpsURLConnection scon = (HttpsURLConnection) theURL.openConnection();
                if (scon != null) {
                    scon.setConnectTimeout(5000);
                    scon.setReadTimeout(5000);
                    scon.setRequestMethod("GET");
                    scon.setDoInput(true);
                    //start of the connection
                    scon.connect();
                    int resCode = scon.getResponseCode();
                    Log.d("CSVUpdateTask", "con response is: " + resCode);
                    is = scon.getInputStream();
                }
            }

        } catch (MalformedURLException e) {
            Log.d("CSVUpdateTask:", "URL misformed! " + e);
        } catch (ProtocolException e) {
            Log.d("CSVUpdateTask:", "protocol error! " + e);
        } catch (IOException e) {
            Log.d("CSVUpdateTask:", "https input stream not valid! :(" + e);
        }
        return is;
    }

    private InputStream getHttpStream(String url) {
        InputStream is = null;
        try {

            URL theURL = new URL(url.replace("https", "http"));
            HttpURLConnection con = (HttpURLConnection) theURL.openConnection();
            if (con != null) {
                con.setConnectTimeout(15000);
                con.setReadTimeout(10000);
                con.setRequestMethod("GET");
                con.setDoInput(true);
                //start of the connection
                con.connect();
                int resCode = con.getResponseCode();
                Log.d("CSVUpdateTask", "con response is: " + resCode);
                is = con.getInputStream();
            }
        } catch (ProtocolException e) {
            Log.d("CSVUpdateTask:", "protocol error! " + e);
        } catch (MalformedURLException e) {
            Log.d("CSVUpdateTask:", "URL misformed! " + e);
        } catch (IOException e) {
            Log.d("CSVUpdateTask:", "http input stream not valid! :(" + e);
        }
        return is;
    }

    private ArrayList<String[]> readStream(InputStream is) {
        ArrayList<String[]> result = new ArrayList<>();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        try {
            String templine;
            while ((templine = br.readLine()) != null) {
                String[] csvrow = templine.split(";");
                result.add(csvrow);
            }
        } catch (IOException e) {
            throw new RuntimeException("Input Stream couldn't be read properly: " + e);
        } finally {
            cleanup(is);
            cleanup(br);
        }
        return result;

    }

    private void cleanup(Closeable stream) {
        try {
            stream.close();
        } catch (IOException e) {
            Log.d("CSVUpdateTask", "Input Stream couldn't be closed properly");
            e.printStackTrace();
        }

    }

    private ArrayList<String[]> readLessonStream(InputStream is) {
        ArrayList<String[]> result = new ArrayList<>();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        try {
            String templine;
            StringBuilder sb = new StringBuilder();
            while ((templine = br.readLine()) != null) {
                sb.append(templine);
                if (templine.matches(".*;;;")) {
                    String[] lessonarray = sb.toString().split(";");
                    result.add(lessonarray);
                    sb.setLength(0);
                    // Log.d("SettingsActivity", "row: " + csvrow);
                }
            }


        } catch (IOException e) {
            throw new RuntimeException("Lession stream couldn't be read properly: " + e);
        } finally {
            cleanup(is);
            cleanup(br);
        }
        return result;

    }

}
