package com.foxyourprivacy.privacyRiskInfo;

import android.content.Context;

import com.foxyourprivacy.privacyRiskInfo.asynctasks.DBWrite;

import java.util.HashMap;
import java.util.Map;


/**
 * The ValueKeeper is the place where all the information of the user is stored during the use
 * when the app is started, the VK gets all the information from the Database and stores it when the app is closed.
 * Created by Tim on 12.09.2016.
 */
//stores all the user's scores
public class ValueKeeper {
//this class implements the singleton design pattern therefor all instances are to be created by calling getInstance()


    private static ValueKeeper instance;
    public boolean startedBefore = false;
    boolean valueKeeperAlreadyRefreshed = false;
    private HashMap<Long, Long> applicationAccessAndDuration = new HashMap<>();
    private long timeOfLastAccess = 0;
    private long timeOfFirstAccess = 0;
    private HashMap<Long, Long> applicationStartAndDuration = new HashMap<>();
    private HashMap<Long, Long> applicationStartAndActiveDuration = new HashMap<>();
    private Boolean notDisplayed = true;
    //the absolute first start
    private long timeOfFirstStart = 0;
    private Boolean freshlyStarted = true;

    private long timeOfLastServerAccess =0;


    private ValueKeeper() {
        super();
    }

    /**
     * create a new instance of the class at first call, return this instance at every other call
     *
     * @return the instance of this class
     * @author Tim
     */
    public static ValueKeeper getInstance() {
        if (instance == null) {
            instance = new ValueKeeper();
        }
        return instance;
    }

    public void reviveInstance(Context context) {//TODO: make async and a loading screen


        //HashMap<String,Boolean> trophyList=new HashMap<>();

        DBHandler db = new DBHandler(context);
        HashMap<String, String> data = db.getIndividualData();
        //Log.d("ValuesTest","Revived Data:\n" + data.toString()+"\nData size: "+Integer.toString(data.size()));
        db.close();

        startedBefore = Boolean.valueOf(data.get("startedBefore"));

        if (data.containsKey("timeOfFirstStart")) {
            timeOfFirstStart = Long.valueOf(data.get("timeOfFirstStart"));
        } else {
            timeOfFirstStart = System.currentTimeMillis();
        }

        if(data.containsKey("timeOfLastServerAccess")){
            timeOfLastServerAccess=Long.valueOf(data.get("timeOfLastServerAccess"));
        }

        if (data.containsKey("notDisplayed")) {
            notDisplayed = Boolean.valueOf(data.get("notDisplayed"));
        }

        applicationAccessAndDuration = new HashMap<>();
        applicationStartAndActiveDuration = new HashMap<>();
        applicationStartAndActiveDuration = new HashMap<>();
        for (String e : data.keySet()) {

            if (e.contains("stD:")) {
                applicationStartAndDuration.put(Long.parseLong(e.substring(4)), Long.parseLong(data.get(e)));
            } else {
                if (e.contains("stA:")) {
                    applicationStartAndActiveDuration.put(Long.parseLong(e.substring(4)), Long.parseLong(data.get(e)));
                } else {

                    if (e.contains("dur:")) {
                        applicationAccessAndDuration.put(Long.parseLong(e.substring(4)), Long.parseLong(data.get(e)));
                    }
                }
            }
        }

        valueKeeperAlreadyRefreshed = true;
    }

    public void saveInstance(Context context) {
        HashMap<String, String> values = new HashMap<>();
        values.put("startedBefore", Boolean.toString(startedBefore));
        values.put("notDisplayed", Boolean.toString(true));
        values.put("timeOfFirstStart", Long.toString(timeOfFirstStart));
        values.put("timeOfLastServerAccess",Long.toString(timeOfLastServerAccess));

        //Log.d("ValueKeeper", "currentEval:" + Integer.toString(currentEvaluation));
        //ArrayList<String> deinstalledApps=new ArrayList<>();



        for (Map.Entry<Long, Long> entry : applicationAccessAndDuration.entrySet()) {
            Long key = entry.getKey();
            Long value = entry.getValue();
            values.put("dur:" + key, Long.toString(value));

        }
        for (Map.Entry<Long, Long> entry : applicationStartAndDuration.entrySet()) {
            Long key = entry.getKey();
            Long value = entry.getValue();
            values.put("stD:" + key, Long.toString(value));

        }
        for (Map.Entry<Long, Long> entry : applicationStartAndActiveDuration.entrySet()) {
            Long key = entry.getKey();
            Long value = entry.getValue();
            values.put("stA:" + key, Long.toString(value));

        }
        new DBWrite().execute(context, "insertIndividualData", values);
    }

    public void setTimeOfLastAccess(long time) {
        timeOfLastAccess = time;
    }

    public void fillApplicationAccessAndDuration(long time) {
        if (applicationAccessAndDuration.containsKey(timeOfLastAccess)) {
            applicationAccessAndDuration.put(timeOfLastAccess, time - timeOfLastAccess);
        } else {
            applicationAccessAndDuration.put(timeOfLastAccess, time - timeOfLastAccess);
        }
        // Log.d("MyApp",applicationAccessAndDuration.toString());
    }

    public Boolean getFreshlyStarted() {
        return freshlyStarted;
    }

    public void setFreshlyStarted(Boolean freshlyStartet) {
        this.freshlyStarted = freshlyStartet;
    }

    public void fillApplicationStartAndDuration(long time) {
        applicationStartAndDuration.put(timeOfFirstAccess, time - timeOfFirstAccess);
        //Log.d("MyApp","TotalTime:"+applicationStartAndDuration.toString());
    }

    public void setTimeOfFirstAccess(long time) {
        timeOfFirstAccess = time;
    }

    public void fillApplicationStartAndActiveCDuration(long time) {
        long totalTime = 0;
        for (Long t : applicationAccessAndDuration.values()) {
            totalTime = totalTime + t;
        }

        applicationStartAndActiveDuration.put(timeOfFirstAccess, totalTime);
        //Log.d("MyApp","ActualTotalTime:"+applicationStartAndActiveDuration.toString());
    }

    public long getTimeOfLastServerAccess() {
        return timeOfLastServerAccess;
    }

    public void setTimeOfThisServerAccess() {
        this.timeOfLastServerAccess = System.currentTimeMillis();
    }

    public void setTimeOfNextServerAccess(long nextTime) {
        this.timeOfLastServerAccess = nextTime - 259200000;
    }



}


