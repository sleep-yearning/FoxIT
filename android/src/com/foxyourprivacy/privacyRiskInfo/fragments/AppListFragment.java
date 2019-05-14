package com.foxyourprivacy.privacyRiskInfo.fragments;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.app.usage.UsageStats;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PermissionInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.foxyourprivacy.privacyRiskInfo.R;
import com.foxyourprivacy.privacyRiskInfo.activities.AnalysisResults;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 *
 */
public class AppListFragment extends ListFragment implements AdapterView.OnItemClickListener {

    private List<ApplicationInfo> apps;
    private Context context;


    /**provides the presented apps
     * @author Tim
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        context = getActivity().getApplicationContext();
        apps = fetchALL_APPS();
        return inflater.inflate(R.layout.fragment_app_list, container, false);
    }


    /**fills the listView and manages the scroll hint
     * @author Tim
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //fills the ListView
        ArrayAdapter<ApplicationInfo> adapter = new MyListAdapter_app();
        setListAdapter(adapter);
        //adds the onClickAction defined by onItemClick()
        getListView().setOnItemClickListener(this);
        //makes the scrollHint disappear on scroll
        getListView().setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (getActivity() instanceof AnalysisResults) {
                    ((AnalysisResults) getActivity()).setScrollMessageVisiblility(false);
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            }
        });
    }


    /**
     * The function called by clicking on an item of the listView
     * It creates the fragment in charge of displaying the apps' permissions.
     * @author Tim
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        final PackageManager pm = context.getPackageManager();
        ApplicationInfo currentApp = apps.get(position);
        PackageInfo packageInfo;

        //Fragment is created
        PermissionListFragment fragment = new PermissionListFragment();
        fragment.setCurrentApp(currentApp); //IMPORTANT!!! tells the created Fragment which app it describes

        //Permissions are added to the fragment
        Bundle permissions = new Bundle();   //Bundle to pass arguments to the Fragment
        try {
            packageInfo = pm.getPackageInfo(currentApp.packageName, PackageManager.GET_PERMISSIONS);
            //Get permissions
            String[] requestedPermissions = packageInfo.requestedPermissions;
            //pass them to the newly created fragment
            if (requestedPermissions != null && requestedPermissions.length != 0) {
                permissions.putStringArrayList("permissions", createPermissionList(currentApp)); //new ArrayList<String>(Arrays.asList(requestedPermissions)));
                permissions.putInt("appRating", getLevelOfDanger(currentApp));
                fragment.setArguments(permissions);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        //add fragment so the activitys' context
        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(R.id.permissionFrame, fragment, "permission");
        transaction = transaction.addToBackStack("permission");
        transaction.commit();

        //make appListFragment and SettingsListFragment invisible after permissionListFragment is created
        FrameLayout settingFrame = getActivity().findViewById(R.id.settingFrame);
        FrameLayout appFrame = getActivity().findViewById(R.id.appFrame);
        settingFrame.setVisibility(View.GONE);
        appFrame.setVisibility(View.GONE);
        AnalysisResults a = (AnalysisResults) getActivity();
        a.mViewPager.setVisibility(View.GONE);

    }

    /**
     * a totally makeshift methodLeft to determine the dangerLevel of an app, not important to understand
     *
     * @param app the app data to get the name for which the danger level is to be assessed
     * @return int on a scale 0-2
     */
    private int getLevelOfDanger(ApplicationInfo app) {
        String[] harmlessReferenceValue = {};
        String[] normalReferenceValue = {
                "android.permission.ACCESS_LOCATION_EXTRA_COMMANDS",
                "android.permission.ACCESS_NETWORK_STATE",
                "android.permission.ACCESS_NOTIFICATION_POLICY",
                "android.permission.ACCESS_WIFI_STATE",
                "android.permission.BLUETOOTH",
                "android.permission.BLUETOOTH_ADMIN",
                "android.permission.BROADCAST_STICKY",
                "android.permission.CHANGE_NETWORK_STATE",
                "android.permission.CHANGE_WIFI_MULTICAST_STATE",
                "android.permission.CHANGE_WIFI_STATE",
                "android.permission.DISABLE_KEYGUARD",
                "android.permission.EXPAND_STATUS_BAR",
                "android.permission.GET_PACKAGE_SIZE",
                "com.android.launcher.permission.INSTALL_SHORTCUT",
                "android.permission.INTERNET", //strange
                "android.permission.KILL_BACKGROUND_PROCESSES",
                "android.permission.MODIFY_AUDIO_SETTINGS",
                "android.permission.NFC",
                "android.permission.READ_SYNC_SETTINGS",
                "android.permission.READ_SYNC_STATS",
                "android.permission.RECEIVE_BOOT_COMPLETED",
                "android.permission.REORDER_TASKS",
                "android.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS",
                "android.permission.REQUEST_INSTALL_PACKAGES",
                "com.android.alarm.permission.SET_ALARM",
                "android.permission.SET_TIME_ZONE",
                "android.permission.SET_WALLPAPER",
                "android.permission.SET_WALLPAPER_HINTS",
                "android.permission.TRANSMIT_IR",
                "com.android.launcher.permission.UNINSTALL_SHORTCUT",
                "android.permission.USE_FINGERPRINT",
                "android.permission.VIBRATE",
                "android.permission.WAKE_LOCK",
                "android.permission.WRITE_SYNC_SETTINGS"
        };
        String[] dangerousReferenceValue = {"android.permission.READ_CALENDAR", "android.permission.WRITE_CALENDAR",
                "android.permission.CAMERA", "android.permission.READ_CONTACTS",
                "android.permission.WRITE_CONTACTS", "android.permission.GET_ACCOUNTS", "android.permission.ACCESS_FINE_LOCATION",
                "android.permission.ACCESS_COARSE_LOCATION", "android.permission.RECORD_AUDIO", "android.permission.READ_PHONE_STATE", "android.permission.CALL_PHONE",
                "android.permission.READ_CALL_LOG", "android.permission.WRITE_CALL_LOG", "com.android.voicemail.permission.ADD_VOICEMAIL", "android.permission.USE_SIP",
                "android.permission.PROCESS_OUTGOING_CALLS", "android.permission.BODY_SENSORS", "android.permission.SEND_SMS",
                "android.permission.RECEIVE_SMS", "android.permission.READ_SMS", "android.permission.RECEIVE_WAP_PUSH",
                "android.permission.RECEIVE_MMS", "android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE"};
        // ArrayList<String> harmlessPermissions = new ArrayList<String>();
        ArrayList<String> normalPermissions = new ArrayList<>();
        ArrayList<String> dangerousPermissions = new ArrayList<>();
        // ArrayList<String> otherPermissions = new ArrayList<String>();
        String[] allPermissions = {};

        PackageInfo packageInfo;
        final PackageManager pm = context.getPackageManager();
        try {
            packageInfo = pm.getPackageInfo(app.packageName, PackageManager.GET_PERMISSIONS);
            //Get permissions
            allPermissions = packageInfo.requestedPermissions;

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        if (allPermissions != null && allPermissions.length != 0) {

            outerloop:
            for (String permissionApp : allPermissions) {
/*
                for (String permissionReference : harmlessReferenceValue) {
                    if (permissionApp.equals(permissionReference)) {
                        harmlessPermissions.add(permissionApp);
                        continue outerloop;
                    }
                }*/

                for (String permissionReference : normalReferenceValue) {
                    if (permissionApp.equals(permissionReference)) {
                        normalPermissions.add(permissionApp);
                        continue outerloop;
                    }
                }

                for (String permissionReference : dangerousReferenceValue) {
                    if (permissionApp.equals(permissionReference)) {
                        dangerousPermissions.add(permissionApp);
                        continue outerloop;
                    }
                }

                //otherPermissions.add(permissionApp);
            }

            if (dangerousPermissions.size() != 0) return 2;
            if (normalPermissions.size() > 0) return 1;
        } else {
            return 0;
        }

        return 0;
    }

    /**
     * methodLeft to retrieve all apps without entering the database
     *
     * @return list of retrieved apps
     * @author Tim
     */
    private List<ApplicationInfo> fetchALL_APPS() {
        if (context == null) {
            Log.d("MyApp", "context is Null");
            return null;
        }
        final PackageManager pm = context.getPackageManager();
        //get a list of installed apps.
        if (pm == null) {
            Log.d("MyApp", "pm is Null");
            return null;
        }
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        Collections.sort(packages, new Comparator<ApplicationInfo>() {
            @Override
            public int compare(ApplicationInfo lhs, ApplicationInfo rhs) {

                String leftAppName = pm.getApplicationLabel(lhs).toString();
                String rightAppName = pm.getApplicationLabel(rhs).toString();
                /*
                if (leftAppName == null) {
                    return -1;
                }

                if (rightAppName == null) {
                    return 1;
                }
                */

                if (leftAppName.equals(rightAppName)) {
                    return 0;
                }


                return leftAppName.compareTo(rightAppName);
            }

        });

        return packages;
    }

    /**
     * creates the sorted list of permissions the PermissionDescriptionFragment  displays
     *
     * @param app app for which the list is to be created
     * @return list of strings with the permission-names
     */
    private ArrayList<String> createPermissionList(ApplicationInfo app) {
        //the refereceValues are not needed at the moment because the classification provided by android is used
        String[] harmlessReferenceValue = {};
        String[] normalReferenceValue = {
                "android.permission.ACCESS_LOCATION_EXTRA_COMMANDS",
                "android.permission.ACCESS_NETWORK_STATE",
                "android.permission.ACCESS_NOTIFICATION_POLICY",
                "android.permission.ACCESS_WIFI_STATE",
                "android.permission.BLUETOOTH",
                "android.permission.BLUETOOTH_ADMIN",
                "android.permission.BROADCAST_STICKY",
                "android.permission.CHANGE_NETWORK_STATE",
                "android.permission.CHANGE_WIFI_MULTICAST_STATE",
                "android.permission.CHANGE_WIFI_STATE",
                "android.permission.DISABLE_KEYGUARD",
                "android.permission.EXPAND_STATUS_BAR",
                "android.permission.GET_PACKAGE_SIZE",
                "com.android.launcher.permission.INSTALL_SHORTCUT",
                "android.permission.INTERNET", //strange
                "android.permission.KILL_BACKGROUND_PROCESSES",
                "android.permission.MODIFY_AUDIO_SETTINGS",
                "android.permission.NFC",
                "android.permission.READ_SYNC_SETTINGS",
                "android.permission.READ_SYNC_STATS",
                "android.permission.RECEIVE_BOOT_COMPLETED",
                "android.permission.REORDER_TASKS",
                "android.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS",
                "android.permission.REQUEST_INSTALL_PACKAGES",
                "com.android.alarm.permission.SET_ALARM",
                "android.permission.SET_TIME_ZONE",
                "android.permission.SET_WALLPAPER",
                "android.permission.SET_WALLPAPER_HINTS",
                "android.permission.TRANSMIT_IR",
                "com.android.launcher.permission.UNINSTALL_SHORTCUT",
                "android.permission.USE_FINGERPRINT",
                "android.permission.VIBRATE",
                "android.permission.WAKE_LOCK",
                "android.permission.WRITE_SYNC_SETTINGS"
        };
        String[] dangerousReferenceValue = {"android.permission.READ_CALENDAR", "android.permission.WRITE_CALENDAR",
                "android.permission.CAMERA", "android.permission.READ_CONTACTS",
                "android.permission.WRITE_CONTACTS", "android.permission.GET_ACCOUNTS", "android.permission.ACCESS_FINE_LOCATION",
                "android.permission.ACCESS_COARSE_LOCATION", "android.permission.RECORD_AUDIO", "android.permission.READ_PHONE_STATE", "android.permission.CALL_PHONE",
                "android.permission.READ_CALL_LOG", "android.permission.WRITE_CALL_LOG", "com.android.voicemail.permission.ADD_VOICEMAIL", "android.permission.USE_SIP",
                "android.permission.PROCESS_OUTGOING_CALLS", "android.permission.BODY_SENSORS", "android.permission.SEND_SMS",
                "android.permission.RECEIVE_SMS", "android.permission.READ_SMS", "android.permission.RECEIVE_WAP_PUSH",
                "android.permission.RECEIVE_MMS", "android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE"};
        ArrayList<String> harmlessPermissions = new ArrayList<>(); //TODO implement own danger estimate for permissions, now its just empty every time
        //TODO in the other function it is commented out therefore
        ArrayList<String> normalPermissions = new ArrayList<>();
        ArrayList<String> dangerousPermissions = new ArrayList<>();
        ArrayList<String> otherPermissions = new ArrayList<>();
        ArrayList<String> result = new ArrayList<>();
        String[] allPermissions = {};

        PackageInfo packageInfo;  //is actually used
        final PackageManager pm = context.getPackageManager();
        try {
            packageInfo = pm.getPackageInfo(app.packageName, PackageManager.GET_PERMISSIONS);
            //Get permissions
            allPermissions = packageInfo.requestedPermissions;

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (allPermissions != null && allPermissions.length != 0) {

            outerloop:
            for (String permissionApp : allPermissions) {

                //leave it!!! we may need it as soon as we use our own classification system


                //for (String permissionReference : harmlessReferenceValue) {
                //    if (permissionApp.equals(permissionReference)) {
                //        harmlessPermissions.add(permissionApp);
                //        continue outerloop;
                //    }
                // }

                //for (String permissionReference : normalReferenceValue) {
                //    if (permissionApp.equals(permissionReference)) {
                //        normalPermissions.add(permissionApp);
                //        continue outerloop;
                //    }
                //}

                //for (String permissionReference : dangerousReferenceValue) {
                //    if (permissionApp.equals(permissionReference)) {
                //        dangerousPermissions.add(permissionApp);
                //        continue outerloop;
                //    }
                //}

                //for every permission the danger level is determined
                try {
                    PermissionInfo permissionInfo = pm.getPermissionInfo(permissionApp, 0);
                    int dangerLevel = permissionInfo.protectionLevel;

                    //and the permission is added to the associated list
                    if (dangerLevel == PermissionInfo.PROTECTION_DANGEROUS) {
                        dangerousPermissions.add(permissionApp);
                        continue;
                    }
                    if (dangerLevel == PermissionInfo.PROTECTION_NORMAL) {
                        normalPermissions.add(permissionApp);
                        continue;
                    }

                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }

                otherPermissions.add(permissionApp);
            }

            //the created lists are added onto each other divided by headlines
            if (dangerousPermissions.size() != 0)
                result.add(getString(R.string.dangerousPermissions));
            result.addAll(dangerousPermissions);
            if (normalPermissions.size() != 0) result.add(getString(R.string.normalPermissions));
            result.addAll(normalPermissions);
            if (harmlessPermissions.size() != 0)
                result.add(getString(R.string.harmlessPermissions));
            result.addAll(harmlessPermissions);
            if (dangerousPermissions.size() + normalPermissions.size() + harmlessPermissions.size() != allPermissions.length)
                result.add(getString(R.string.otherPermissions));
            result.addAll(otherPermissions);
        }
        return result;
    }

    /**
     * class to define the way the apps are displayed in the listView
     *
     * @author Tim
     */
    private class MyListAdapter_app extends ArrayAdapter<ApplicationInfo> {
        private MyListAdapter_app() {
            //Here is defined, that the adapter is using our list of apps (apps)
            //and the R.layout.layout_app-XMLLayout for single listViewItems
            super(context, R.layout.layout_app, apps);
        }

        /**
         * Here ist defined how the XML-Layout is filed by the data stored in the array.
         *
         * @param position    position in the array used
         * @param convertView variable used for recycling old elements of the listView
         * @param parent
         * @return
         * @author Tim
         */
        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            //convertView has to be filled with layout_app if it's null
            View itemView = convertView;
            final PackageManager pm = context.getPackageManager();      //only important for fetching the app info
            if (itemView == null) {
                itemView = getActivity().getLayoutInflater().inflate(R.layout.layout_app, parent, false);
            }

            //the current app is retrieved
            ApplicationInfo currentApp = apps.get(position);
            //setting the app's name
            TextView appName = itemView.findViewById(R.id.text_app_name);
            appName.setText(pm.getApplicationLabel(currentApp).toString());

            TextView appTime = itemView.findViewById(R.id.app_usage_time);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                UsageStats usage = ((AnalysisResults) getActivity()).usageStats.get(currentApp.packageName);

                if (usage != null) {
                    long time = Long.valueOf(usage.getTotalTimeInForeground()) / 1000L;
                    if (time > 60) {
                        appTime.setText(String.valueOf(time / 60) + " min");
                    } else {
                        appTime.setText(String.valueOf(time) + " sek");
                    }
                    appTime.setVisibility(View.VISIBLE);

                } else {
                    appTime.setVisibility(View.INVISIBLE);
                }

            } else {
                appTime.setVisibility(View.INVISIBLE);
            }


            //adding an icon describing the risk posed by the app
            ImageView ratingIcon = itemView.findViewById(R.id.image_permission_rating);
            int i = getLevelOfDanger(currentApp);

            switch (i) {
                case 0: {
                    ratingIcon.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.goodapprating));
                    break;
                }
                case 1: {
                    ratingIcon.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.mediumapprating));
                    break;
                }
                case 2: {
                    ratingIcon.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.badapprating));
                    break;
                }
            }

            //setting the appIcon
            ImageView icon = itemView.findViewById(R.id.image_app_icon);
            try {
                icon.setImageDrawable(pm.getApplicationIcon(currentApp.packageName));

            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

            return itemView;
        }
    }

}

