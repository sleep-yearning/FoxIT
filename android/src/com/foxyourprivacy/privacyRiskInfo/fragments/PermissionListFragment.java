package com.foxyourprivacy.privacyRiskInfo.fragments;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PermissionGroupInfo;
import android.content.pm.PermissionInfo;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.foxyourprivacy.privacyRiskInfo.R;
import com.foxyourprivacy.privacyRiskInfo.activities.AnalysisResults;

import java.util.ArrayList;

/**
 * This fragment shows the permissions of an app that is selected from the app list.
 * from here the user can open the permissionDescriptionFragment
 * Created by Tim on 11.06.2016.
 */
public class PermissionListFragment extends ListFragment implements AdapterView.OnItemClickListener {
    private static boolean firstTimeScrollhint = true;  //true=the scrollHint has to be displayed, false= the listView has been scrolled
    private String[] permissionArray = {"Diese App benötigt keine Berechtigungen."}; //contains the permissions displayed in the listView
    private Context context;
    private ApplicationInfo currentApp;           //the App whose permission are on display
    private int appRating;


    /**fills the fragments layout and provides behavior for the permissionHeadline
     * @author Tim
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        context = getActivity().getApplicationContext();
        setFragmentInActivity(); //IMPORTANT! informs AnalysisResults-activity of the existence of this fragment

        View itemView = getActivity().getLayoutInflater().inflate(R.layout.fragment_permission_list, container, false);

        //attaches the OnClickEvent for switching to the androidAppSetting
        Button settingsButton = itemView.findViewById(R.id.app_settings_button);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("PermissionListFragment", "onClick");
                Intent intentForSettingSwitch = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", currentApp.packageName, null));//pm.getApplicationLabel(currentApp).toString(), null));
                intentForSettingSwitch.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intentForSettingSwitch);
            }

        });

        //inserts the app name into the headline
        PackageManager pm = context.getPackageManager();
        TextView appName = itemView.findViewById(R.id.text_permission_app_name);
        appName.setText(pm.getApplicationLabel(currentApp).toString());

        //setting the headline's icon
        ImageView icon = itemView.findViewById(R.id.image_permission_app_icon);
        try {
            icon.setImageDrawable(pm.getApplicationIcon(currentApp.packageName));

        } catch (PackageManager.NameNotFoundException e) {
            //if no icon was found
            e.printStackTrace();
        }

        //determines the permissions for the app currently looked at
        try {
            PackageInfo packageInfo = pm.getPackageInfo(currentApp.packageName, PackageManager.GET_PERMISSIONS);

            //Get Permissions
            String[] requestedPermissions = packageInfo.requestedPermissions;
            //inserts the number of permissions into the headline
            TextView permissionCount = itemView.findViewById(R.id.text_app_permissions);

            if (requestedPermissions == null) {
                permissionCount.setText("0");
            } else {
                permissionCount.setText(String.format("%d", requestedPermissions.length));
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        //sets the scrollHint to visible
        if (firstTimeScrollhint) {
            if (getActivity() instanceof AnalysisResults) {
                ((AnalysisResults) getActivity()).setScrollMessageVisiblility(true);
            }
        }

        //links the onClickEvent for returning to AppListFragment to the headline
        RelativeLayout headline = itemView.findViewById(R.id.app_headline_frame);
        switch (appRating) {
            case (0):
                headline.setBackgroundColor(Color.argb(255, 2, 174, 0));
                break;  //Green
            case (1):
                headline.setBackgroundColor(Color.argb(255, 255, 165, 0));
                break; //Orange
            case (2):
                headline.setBackgroundColor(Color.argb(255, 174, 2, 0));
                break; //Red
        }
        headline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() instanceof AnalysisResults) {
                    ((AnalysisResults) getActivity()).onPermissionListHeadlinePressed();
                }
            }

        });

        return itemView;
    }


    /** defines the listView
     * @author Tim
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        //filling the listView with data
        super.onActivityCreated(savedInstanceState);
        //creates the listView
        ArrayAdapter<String> adapter = new MyListAdapter_permission();   //new ArrayAdapter(getActivity(),android.R.layout.simple_list_item_1,permissionArray);//new MyListAdapter_permission();
        setListAdapter(adapter);
        //on item click an PermissionDescriptionFragment is created. (the OnItemClick() methodLeft)
        getListView().setOnItemClickListener(this);
        //on scroll the scrollHint disappears
        getListView().setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (getActivity() instanceof AnalysisResults) {
                    ((AnalysisResults) getActivity()).setScrollMessageVisiblility(false);
                    firstTimeScrollhint = false;
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });

    }


    /**
     * if the fragment is closed the scrollHint disappears
     */
    @Override
    public void onStop() {
        super.onStop();
        if (getActivity() instanceof AnalysisResults) {
            ((AnalysisResults) getActivity()).setScrollMessageVisiblility(false);
        }

    }

    /**
     * on Item Click the permissionDescriptionFragment is created
     * @author Tim
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String permissionName = permissionArray[position];
        if (!(permissionName.equals("Gefährliche Berechtigungen:") || permissionName.equals("Normale Berechtigungen:") || permissionName.equals("Harmlose Berechtigungen:") || permissionName.equals("Andere Berechtigungen:") || permissionName.equals("Diese App benötigt keine Berechtigungen."))) {
            //Fragment is created
            PermissionDescriptionFragment fragment = new PermissionDescriptionFragment();
            Bundle permissionNameBundle = new Bundle();
            permissionNameBundle.putString("permissionName", permissionArray[position]);
            permissionNameBundle.putInt("appRating", appRating);
            fragment.setArguments(permissionNameBundle);

            //add fragment so the activitys' context
            FragmentManager childFragmentManager = getChildFragmentManager();
            FragmentTransaction transaction = childFragmentManager.beginTransaction();

            transaction.add(R.id.list_frame, fragment, "permissionDescription");
            transaction = transaction.addToBackStack("permissionDescription");
            transaction.commit();
            RelativeLayout settingFrame = getActivity().findViewById(R.id.list_frame);
            settingFrame.setVisibility(View.GONE);
            if (getActivity() instanceof AnalysisResults) {
                ((AnalysisResults) getActivity()).setScrollMessageVisiblility(false);
            }
            firstTimeScrollhint = false;
        }
    }

    /**methodLeft to set the fragment's arguments from the outside
     * @author Tim
     * @param arg Bundle holding an ArrayList calles "permissions" holding an app's permissions
     *
     */
    @Override
    public void setArguments(Bundle arg) {
        ArrayList<String> permissions = arg.getStringArrayList("permissions");
        if (permissions != null)
            permissionArray = permissions.toArray(new String[permissions.size()]);
        appRating = arg.getInt("appRating");
    }

    /**
     * used in AppListFragment to tell this Fragment which app it is describing
     *
     * @param app
     * @author Tim
     */
    public void setCurrentApp(ApplicationInfo app) {
        currentApp = app;
    }

    /**
     * informs the startScreenActivity that this fragment is created
     * (used for removing PermissionDescriptionFragment without errors)
     *
     * @author Tim
     */
    private void setFragmentInActivity() {
        if (getActivity() instanceof AnalysisResults) {
            AnalysisResults analysisResults = (AnalysisResults) getActivity();
            analysisResults.permissionList = this;
        }
    }

    /**
     * setts the Permission's listView visible, called when permissionDescriptionFragment is destroid
     * otherwise permissionList would stay invisible
     *
     * @author Tim
     */
    public void setListVisible() {
        RelativeLayout settingFrame = getActivity().findViewById(R.id.list_frame);
        settingFrame.setVisibility(View.VISIBLE);
    }

    /**
     * class to define the way the settings are displayed in the listView
     *
     * @author Tim
     */
    private class MyListAdapter_permission extends ArrayAdapter<String> {
        public MyListAdapter_permission() {
            //defining the listView's layout for single entries
            super(context, R.layout.layout_permission, permissionArray);
        }

        /**
         * Here ist defined how the XML-Layout is filed  by the data stored in the array.
         *
         * @param position    position in the array used
         * @param convertView
         * @param parent
         * @return
         * @author Tim
         */
        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            //convertView has to be filled with layout_app if it's null
            View itemView = convertView;
            if (itemView == null) {
                itemView = getActivity().getLayoutInflater().inflate(R.layout.layout_permission, parent, false);
            }

            //setting the text for the permission's name
            TextView permissionName = itemView.findViewById(R.id.text_permission_name);
            // permissionName.setText(permissionArray[position]);
            if (!(permissionArray[position].equals("Gefährliche Berechtigungen:") || permissionArray[position].equals("Normale Berechtigungen:") || permissionArray[position].equals("Harmlose Berechtigungen:"))) {
                //permissionName.setText(permissionArray[position].substring(permissionArray[position].indexOf(".")+1,permissionArray[position].length()));
                String[] array = permissionArray[position].split("\\.");
                if (array.length == 3) {
                    permissionName.setText(array[2].replace("_", " "));
                    permissionName.setTextColor(Color.parseColor("#616161"));
                } else {
                    permissionName.setText(permissionArray[position]);
                    permissionName.setTextColor(Color.parseColor("#616161"));
                }
            } else {
                permissionName.setText(permissionArray[position]);
                permissionName.setTextColor(Color.BLACK);
            }


            //sets the permissionIcon
            Drawable permissionIcon = getPermissionDrawable(permissionArray[position]);
            ImageView imageView = itemView.findViewById(R.id.image_app_icon);
            imageView.setImageDrawable(permissionIcon);

            return itemView;
        }

        /**
         * determines the icon for a single permission
         * also defined in PermissionDescriptionFragment, probably bad
         * @author Tim
         * @param permission the permission whose icon is to be fetched
         */
        @Nullable
        private Drawable getPermissionDrawable(String permission) {
            PackageManager mPackageManager = context.getPackageManager();
            Drawable drawable = null; //the icon
            //try to get an icon for the permission
            try {
                PermissionInfo permissionInfo = mPackageManager.getPermissionInfo(permission, 0);
                //icons are only defined for a group of permissions.
                PermissionGroupInfo groupInfo = mPackageManager.getPermissionGroupInfo(permissionInfo.group, 0);
                drawable = ContextCompat.getDrawable(context, groupInfo.icon);
            } catch (PackageManager.NameNotFoundException | Resources.NotFoundException e) {
                //if there is no icon do nothing

            }

            return drawable;
        }
    }


}






