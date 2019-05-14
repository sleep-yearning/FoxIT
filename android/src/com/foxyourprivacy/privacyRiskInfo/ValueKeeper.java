package com.foxyourprivacy.privacyRiskInfo;

import android.content.Context;
import android.util.Log;

import com.foxyourprivacy.privacyRiskInfo.asynctasks.DBWrite;

import java.util.ArrayList;
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
    public HashMap<String, Boolean> trophyList = new HashMap<>();
    public Boolean onboardingStartedBefore = false;
    public Boolean analysisDoneBefore = false;
    int dailyLessonsUnlocked = 0;
    boolean valueKeeperAlreadyRefreshed = false;
    private int currentEvaluation;
    private HashMap<String, Boolean> animationList = new HashMap<>();
    private HashMap<String, String> profilList = new HashMap<>();
    private HashMap<Long, Long> applicationAccessAndDuration = new HashMap<>();
    private long timeOfLastAccess = 0;
    private long timeOfFirstAccess = 0;
    private HashMap<Long, Long> applicationStartAndDuration = new HashMap<>();
    private HashMap<Long, Long> applicationStartAndActiveDuration = new HashMap<>();
    private Boolean notDisplayed = true;
    //the absolute first start
    private long timeOfFirstStart = 0;
    private int numberOfTimesOpenedAtNight = 0;
    private int numberOfTimesOpenedAtMorning = 0;
    private ArrayList<String> solvedClasses = new ArrayList<>();
    private ArrayList<Long> appStartsTheLastTwoDays = new ArrayList<>();
    private HashMap<String, String> evaluationResults = new HashMap<>();
    private Boolean freshlyStarted = true;
    private String username;
    private int acornCount = 0; //amount of acorn the player collected
    private int tokenCount = 0; //amount of token the player collected

    private boolean notificationsWanted = true;



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

    public void setEvaluationResults(HashMap<String, String> evaluationResults) {
        this.evaluationResults.putAll(evaluationResults);
        Log.d("MyApp", "EvaluationsResults: " + this.evaluationResults.toString());
    }

    public void reviveInstance(Context context) {//TODO: make async and a loading screen


        //HashMap<String,Boolean> trophyList=new HashMap<>();

        DBHandler db = new DBHandler(context);
        HashMap<String, String> data = db.getIndividualData();
        dailyLessonsUnlocked = db.howManyDailiesUnlocked();
        //Log.d("ValuesTest","Revived Data:\n" + data.toString()+"\nData size: "+Integer.toString(data.size()));
        db.close();
        analysisDoneBefore = Boolean.valueOf(data.get("analysisDoneBefore"));
        onboardingStartedBefore = Boolean.valueOf(data.get("onboardingStartedBefore"));


        if (data.containsKey("timeOfFirstStart")) {
            timeOfFirstStart = Long.valueOf(data.get("timeOfFirstStart"));
        } else {
            timeOfFirstStart = System.currentTimeMillis();
        }

        if(data.containsKey("timeOfLastServerAccess")){
            timeOfLastServerAccess=Long.valueOf(data.get("timeOfLastServerAccess"));
        }

        if (data.containsKey("notificationsWanted")) {
            notificationsWanted = Boolean.valueOf(data.get("notificationsWanted"));
        }


        if (data.containsKey("acornCount")) {

            acornCount = Integer.valueOf(data.get("acornCount"));
        }
        if (data.containsKey("tokenCount")) {
            tokenCount = Integer.valueOf(data.get("tokenCount"));
        }
        if (data.containsKey("username")) {
            username = data.get("username");
        }
        if (data.containsKey("notDisplayed")) {
            notDisplayed = Boolean.valueOf(data.get("notDisplayed"));
        }
        if (data.containsKey("currentEvaluation")) {
            currentEvaluation = Integer.valueOf(data.get("currentEvaluation"));
        }
        if (data.containsKey("dailyLessonsUnlocked")) {
            //TODO fix together with unlocking functionality
            // dailyLessonsUnlocked = Integer.valueOf(data.get("dailyLessonsUnlocked"));
        }
        if (data.containsKey("numberOfTimesOpenedAtNight")) {
            numberOfTimesOpenedAtNight = numberOfTimesOpenedAtNight + Integer.valueOf(data.get("numberOfTimesOpenedAtNight"));
        }
        if (data.containsKey("numberOfTimesOpenedAtMorning")) {
            numberOfTimesOpenedAtMorning = numberOfTimesOpenedAtMorning + Integer.valueOf(data.get("numberOfTimesOpenedAtMorning"));
        }


        animationList = new HashMap<>();

        profilList = new HashMap<>();
        applicationAccessAndDuration = new HashMap<>();
        applicationStartAndActiveDuration = new HashMap<>();
        applicationStartAndActiveDuration = new HashMap<>();
        evaluationResults = new HashMap<>();
        trophyList = new HashMap<>();
        for (String e : data.keySet()) {
            if (e.contains("ani:")) {

                animationList.put(e.substring(4), Boolean.parseBoolean(data.get(e)));

            } else {
                if (e.contains("stD:")) {
                    applicationStartAndDuration.put(Long.parseLong(e.substring(4)), Long.parseLong(data.get(e)));
                } else {
                    if (e.contains("stA:")) {
                        applicationStartAndActiveDuration.put(Long.parseLong(e.substring(4)), Long.parseLong(data.get(e)));
                    } else {
                        if (e.contains("evl:")) {
                            evaluationResults.put(e.substring(4), data.get(e));
                        } else {
                            if (e.contains("scl:")) {
                                solvedClasses.add(data.get(e));
                            } else {
                                if (e.contains("asd:")) {
                                    appStartsTheLastTwoDays.add(Long.valueOf(data.get(e)));
                                } else {
                                    if (e.contains("tro:")) {
                                        trophyList.put(e.substring(4), Boolean.valueOf(data.get(e)));
                                    } else {
                                        if (e.contains("dur:")) {
                                            applicationAccessAndDuration.put(Long.parseLong(e.substring(4)), Long.parseLong(data.get(e)));

                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        animationList.put("Schwanzwedeln",true);
        valueKeeperAlreadyRefreshed = true;
    }

    public void saveInstance(Context context) {
        HashMap<String, String> values = new HashMap<>();
        values.put("onboardingStartedBefore", Boolean.toString(onboardingStartedBefore));
        values.put("analysisDoneBefore", Boolean.toString(analysisDoneBefore));
        values.put("acornCount", Integer.toString(acornCount));
        values.put("tokenCount", Integer.toString(tokenCount));
        values.put("notDisplayed", Boolean.toString(true));
        values.put("currentEvaluation", Integer.toString(currentEvaluation));
        values.put("numberOfTimesOpenedAtMorning", Integer.toString(numberOfTimesOpenedAtMorning));
        values.put("numberOfTimesOpenedAtNight", Integer.toString(numberOfTimesOpenedAtNight));
        values.put("username", username);
        values.put("dailyLessonsUnlocked", Integer.toString(dailyLessonsUnlocked));
        values.put("timeOfFirstStart", Long.toString(timeOfFirstStart));
        values.put("timeOfLastServerAccess",Long.toString(timeOfLastServerAccess));
        values.put("notificationsWanted", Boolean.toString(notificationsWanted));


        //Log.d("ValueKeeper", "currentEval:" + Integer.toString(currentEvaluation));
        //ArrayList<String> deinstalledApps=new ArrayList<>();


        for (Map.Entry<String, String> entry : evaluationResults.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            values.put("evl:" + key, value);
        }


        for (Map.Entry<String, Boolean> entry : animationList.entrySet()) {
            String key = entry.getKey();
            String value = Boolean.toString(entry.getValue());
            values.put("ani:" + key, value);
        }
        for (Map.Entry<String, Boolean> entry : trophyList.entrySet()) {
            String key = entry.getKey();
            String value = Boolean.toString(entry.getValue());
            values.put("tro:" + key, value);

        }

        for (Map.Entry<Long, Long> entry : applicationAccessAndDuration.entrySet()) {
            Long key = entry.getKey();
            Long value = entry.getValue();
            values.put("dur:" + Long.toString(key), Long.toString(value));

        }
        for (Map.Entry<Long, Long> entry : applicationStartAndDuration.entrySet()) {
            Long key = entry.getKey();
            Long value = entry.getValue();
            values.put("stD:" + Long.toString(key), Long.toString(value));

        }
        for (Map.Entry<Long, Long> entry : applicationStartAndActiveDuration.entrySet()) {
            Long key = entry.getKey();
            Long value = entry.getValue();
            values.put("stA:" + Long.toString(key), Long.toString(value));

        }

        for (Map.Entry<String, String> entry : evaluationResults.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            values.put("eva:" + key, value);

        }


        int i = 0;
        // Log.d()
        for (String e : solvedClasses) {
            values.put("scl:" + Integer.toString(i), e);
            i++;
        }

        int x = 0;
        for (Long e : appStartsTheLastTwoDays) {
            values.put("asd:" + Integer.toString(x), Long.toString(e));
            x++;
        }

        new DBWrite().execute(context, "insertIndividualData", values);
    }


    public void changeTokenCountBy(int amount) {
        if (amount != 0) {
            tokenCount = tokenCount + amount;
        }
    }

    public int getTokenCount() {
        return tokenCount;
    }

    public int increaseCurrentEvaluation() {
        return currentEvaluation++;
    }

    public void setProfilList(ProfileListObject[] profilList) {
        for (ProfileListObject p : profilList) {
            this.profilList.put(p.getInputType(), p.getInput());
        }

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

    public String getUsername() {
        return username;
    }

    public void setUsername(String userna) {
        this.username = userna;
    }

    public void insertSolvedClass(String className) {
        if (!solvedClasses.contains(className)) {
            solvedClasses.add(className);
        }
    }

    public void addAppStarts(Long time) {
        for (int i = 0; i < appStartsTheLastTwoDays.size(); i++) {
            if (appStartsTheLastTwoDays.get(i) < time - 2 * 86400000) {
                appStartsTheLastTwoDays.remove(i);
            }}
            appStartsTheLastTwoDays.add(time);
           

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

    public boolean isNotificationsWanted() {
        return notificationsWanted;
    }

    public void setNotificationsWanted(boolean notificationsWanted) {
        this.notificationsWanted = notificationsWanted;
    }


}


