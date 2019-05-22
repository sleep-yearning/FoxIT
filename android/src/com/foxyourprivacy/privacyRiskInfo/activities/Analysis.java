package com.foxyourprivacy.privacyRiskInfo.activities;

import android.app.KeyguardManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;

import com.foxyourprivacy.privacyRiskInfo.DBHandler;
import com.foxyourprivacy.privacyRiskInfo.R;
import com.foxyourprivacy.privacyRiskInfo.asynctasks.DBWrite;
import com.foxyourprivacy.privacyRiskInfo.asynctasks.ExternAnalysis;

import java.lang.reflect.Method;
import java.util.List;

/**
 * device and user specific content is retrieved and saved in database
 * including installed apps, respective permissions & settings.
 */
public class Analysis extends FoxITActivity {

    /**
     * combines two arrays
     *
     * @param first  array
     * @param second array
     * @return result a new array containing the content of input arrays
     * @author Noah
     */
    public static ContentValues[] combineArraysP2(ContentValues[] first, ContentValues[] second) {
        int length = first.length + second.length;
        ContentValues[] result = new ContentValues[length + 2];
        System.arraycopy(first, 0, result, 0, first.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }

    /**
     * while all the data is retrieved, a loading symbol will be shown on screen
     *
     * @param savedInstanceState bundle of informations, saved in onpause
     * @author Tim
     * @author Noah
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analysis);

        //sets our toolbar as the action bar
        Toolbar toolbar = findViewById(R.id.foxit_toolbar);
        setSupportActionBar(toolbar);
        //analyse();
        DBHandler dbHandler = new DBHandler(this);
        dbHandler.insertIndividualValue("analysisDoneBefore", "true");
        dbHandler.close();
        new ExternAnalysis(this).execute(this);

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    /**
     * retrieves the names of installed apps on device as well as additional info, such as requested permissions
     * inserts data into database
     *
     * @author Noah
     */
    public void getALL_APPS() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView progress = findViewById(R.id.progressComment);
                progress.setText(getString(R.string.progressApps));
            }
        });



        // DBHandler dbHandler = new DBHandler(this, null, null, 1);
        final PackageManager pm = getPackageManager();
        final TextView log = findViewById(R.id.log);

        // get a list of installed apps.
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        StringBuilder bb = new StringBuilder();

        ContentValues[] appValues = new ContentValues[packages.size()];
        ContentValues tempValue = new ContentValues(3);
        int counter = 0;

        for (final ApplicationInfo applicationInfo : packages) {
            try {
                PackageInfo packageInfo = pm.getPackageInfo(applicationInfo.packageName, PackageManager.GET_PERMISSIONS);

                // get Permissions
                String[] requestedPermissions = packageInfo.requestedPermissions;
                if (requestedPermissions != null) {
                    for (String requestedPermission : requestedPermissions) {
                        bb.append(requestedPermission);
                        bb.append(";");
                    }
                } else {
                    bb.append("NULL");
                }
            } catch (PackageManager.NameNotFoundException e) {
                Log.d("MyApp", "Error!" + e);
                e.printStackTrace();
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    log.append("APP: " + applicationInfo.packageName + "\n");
                }
            });

            tempValue.put(DBHandler.COLUMN_APPNAME, applicationInfo.packageName);
            tempValue.put(DBHandler.COLUMN_PERMISSIONS, bb.toString());
            tempValue.put(DBHandler.COLUMN_APPDESCRIPTION, pm.getApplicationLabel(applicationInfo).toString());
            appValues[counter] = new ContentValues(tempValue);
            tempValue.clear();
            counter++;
            bb.setLength(0);
        }
        new DBWrite().execute(this, "addAppColumn", appValues);
        //dbHandler.addAppColumn(appValues);
    }


    /**
     * determine the state of the lock screen and writes it into the database
     *
     * @return 0      -none
     * 131072 -pin
     * 65536  -pattern
     * 262144 -password
     * 1      -API 23: existent protection, unclear which kind
     * 66     -exception
     * @author Tim
     */
    public ContentValues getPASSWORT_QUALITY() {
        int value;
        String name;
        ContentValues cv = new ContentValues(3);

        //In case of APK != M there is a LOCK_PATTERN_UTILS
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            String LOCK_PATTERN_UTILS = "com.android.internal.widget.LockPatternUtils";
            name = "lock_pattern_utils";

            try {
                //TODO internal class reflection is dangerous. Is there another way?
                Class<?> lockPatternUtilsClass = Class.forName(LOCK_PATTERN_UTILS);
                Object lockPatternUtils = lockPatternUtilsClass.getConstructor(Context.class).newInstance(this);
                Method method = lockPatternUtilsClass.getMethod("getActivePasswordQuality");
                value = Integer.valueOf(String.valueOf(method.invoke(lockPatternUtils)));
                // Then check if lockProtectionLevel == DevicePolicyManager.TheConstantForWhicheverLevelOfProtectionYouWantToEnforce, and return true if the check passes, false if it fails
            } catch (Exception ex) {
                ex.printStackTrace();
                value = 66;
            }
        }
        //In case of APK = M there is a KeyguardManager
        else {
            name = "keyguard_manager";
            KeyguardManager a = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
            if (a != null && a.isDeviceSecure()) {
                value = 1;
            } else {
                value = 0;
            }
        }
        cv.put(DBHandler.COLUMN_SETTING, name);
        cv.put(DBHandler.COLUMN_TYPE, 2);
        cv.put(DBHandler.COLUMN_INITIAL, value);
        return cv;
    }

    /**
     * determines the status of location mode and writes it into the database
     *
     * @return 0- LOCATION_MODE_OFF
     * 1- LOCATION_MODE_SENSORS_ONLY
     * 2- LOCATION_MODE_BATTERY_SAVING
     * 3- LOCATION_MODE_HIGH_ACCURACY
     * 6- API 23: An aber keine genauen Angaben
     * @author Tim
     */
    public ContentValues getLOCATION_MODE() {
        int locationMode = 0;
        String name;
        // If APK < KITKAT Settings.Secure.LOCATION_PROVIDERS_ALLOWED
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            name = "location_providers_allowed";

            String providers = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            if (TextUtils.isEmpty(providers)) {
                locationMode = 0;
            } else {
                if (providers.contains(LocationManager.GPS_PROVIDER)) {
                    locationMode = 6;
                }
            }
        }
        // If APK >= KITKAT Settings.Secure.LOCATION_MODE
        else {
            name = "location_mode";

            try {
                locationMode = Settings.Secure.getInt(this.getContentResolver(),
                        Settings.Secure.LOCATION_MODE);
            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
                locationMode = 0;
            }
        }
        ContentValues cv = new ContentValues(3);
        cv.put(DBHandler.COLUMN_SETTING, name);
        cv.put(DBHandler.COLUMN_INITIAL, locationMode);
        cv.put(DBHandler.COLUMN_TYPE, 2);
        return cv;
    }


    /**
     * takes a location of the android provider and inserts its parameter to the database
     *
     * @param uri uri to look for when fetching informations e.g. global/secure settings
     * @param projection projection to look for when fetching informations e.g. global/secure
     * @return int number of inserted parameters
     * @author Lena
     */
    public ContentValues[] getSETTINGS(Uri uri, String[] projection) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView progress = findViewById(R.id.progressComment);
                progress.setText(getString(R.string.settingsProgress));
            }
        });


        ContentResolver cR = getContentResolver();
        String name;
        String value;
        Cursor cursor = cR.query(uri, projection, null, null, null);
        if (cursor == null) {
            ContentValues[] result = new ContentValues[1];
            ContentValues dummy = new ContentValues(3);
            dummy.put(DBHandler.COLUMN_SETTING, "nothing found");
            dummy.put(DBHandler.COLUMN_INITIAL, "N/A");
            dummy.put(DBHandler.COLUMN_TYPE, 0);
            result[0] = dummy;
            return result;

        }

        cursor.moveToFirst();
        ContentValues[] contentValues = new ContentValues[cursor.getCount() - 1];
        ContentValues tempValue = new ContentValues(3);
        int counter = 0;
        int col = 0;
        int type = 0; // type: String
        final TextView log = findViewById(R.id.log);


        // parse all lines of the table
        while (cursor.moveToNext()) {
            name = cursor.getString(col);
            // if (cursor.isLast()) return null;
            value = cursor.getString(col + 1);
            if (value != null) {
                // type check: 0 = String 1 = boolean 2 = Number 3 = null
                if (value.matches("-?\\d+(\\.\\d+)?")) type = 2; // type: Number
                if (type == 2 && (value.equals("1") || value.equals("0"))) {
                    type = 1; // type: Boolean
                }
                // ignore location_mode setting, since it is imported separately
                if (!value.equalsIgnoreCase("location_mode")) {
                    tempValue.put(DBHandler.COLUMN_SETTING, name);
                    tempValue.put(DBHandler.COLUMN_INITIAL, value);
                    final String namex = name;
                    final String valuex = value;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            log.append("CONF: " + namex + ": " + valuex + "\n");

                        }
                    });

                    //Log.d("SETTINGS", name + ": " + value + "\n");
                    tempValue.put(DBHandler.COLUMN_TYPE, type);
                    contentValues[counter] = new ContentValues(tempValue);
                    tempValue.clear();
                } else {
                    counter--;
                }
                counter++;
            }
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                log.append("\nLISTEN AUFSCHREIBEN...\n");
                log.append("LEKTIONEN LERNEN...\n");
                log.append("PILZE VERTEILEN...\n");
            }
        });
        cursor.close();
        return contentValues;
    }

    @Override
    public void onBackPressed() {

    }
}
