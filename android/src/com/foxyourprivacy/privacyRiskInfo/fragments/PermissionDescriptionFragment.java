package com.foxyourprivacy.privacyRiskInfo.fragments;

import android.app.Fragment;
import android.content.pm.PackageManager;
import android.content.pm.PermissionGroupInfo;
import android.content.pm.PermissionInfo;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.foxyourprivacy.privacyRiskInfo.DBHandler;
import com.foxyourprivacy.privacyRiskInfo.R;


/**
 * A fragment that is shown when clicked on a permission in the app view (which can be reached from the applist)
 * Created by Tim on 25.06.2016.
 */
public class PermissionDescriptionFragment extends Fragment {
    private String permissionName; //the permission described by the fragment
    private String shortPermissionName; //english permission name without the file structure up front
    private int appRating;




    /**fills the layout with the permission name and description
     * @author Tim
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        View view = inflater.inflate(R.layout.fragment_permission_description, container, false);

        //entering the permissionName into the headline
        TextView permissionNameTextView = view.findViewById(R.id.text_permission_name);
        if (!(permissionName.equals("Dangerous:") || permissionName.equals("Normal:") || permissionName.equals("Harmless:"))) {
            String[] array = permissionName.split("\\.");
            if (array.length == 3) {
                shortPermissionName = array[2];
                permissionNameTextView.setText(array[2].replace("_", " "));
            } else {
                permissionNameTextView.setText(permissionName);
            }
        } else {
            permissionNameTextView.setText(permissionName);
        }

        DBHandler dbHandler = new DBHandler(this.getActivity());

        //entering the permissionDescription
        TextView permissionDescriptionTextView = view.findViewById(R.id.text_permission_description);
        permissionDescriptionTextView.setText(dbHandler.getPermissionDescription(shortPermissionName));
        dbHandler.close();

        //entering the permissionIcon into the headline
        Drawable drawable = getPermissionDrawable(permissionName);
        ImageView appIcon = view.findViewById(R.id.image_app_icon);
        appIcon.setImageDrawable(drawable);


        //links the onClickEvent for returning to PermissionListFragment to the headline
        RelativeLayout button = view.findViewById(R.id.permission_headline_frame);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        //setts the headline's color fitting to the app rating
        switch (appRating) {
            case (0):
                button.setBackgroundColor(Color.argb(200, 2, 174, 0));
                break; //Green
            case (1):
                button.setBackgroundColor(Color.argb(200, 255, 165, 0));
                break; //Orange
            case (2):
                button.setBackgroundColor(Color.argb(200, 174, 2, 0));
                break; //Red
        }

        return view;
    }

    /**
     * Enables to pass arguments to the fragment
     * @author Tim
     */
    @Override
    public void setArguments(Bundle arg) {

        permissionName = arg.getString("permissionName");
        appRating = arg.getInt("appRating");
    }

    /**
     * methodLeft for accessing the icon associated with the current Permission
     *
     * @param permission the current permissions name
     * @return icon drawable that depicts the permission given
     */
    @Nullable
    private Drawable getPermissionDrawable(String permission) {
        PackageManager mPackageManager = getActivity().getPackageManager();
        Drawable permissionIcon = null;  //the icon

        //try to find an icon for this permission
        try {
            PermissionInfo permissionInfo = mPackageManager.getPermissionInfo(permission, 0);
            //icons are only defined for a group of permissions.
            PermissionGroupInfo groupInfo = mPackageManager.getPermissionGroupInfo(permissionInfo.group, 0);
            //the groups icon is fetched
            permissionIcon = ContextCompat.getDrawable(getActivity(), groupInfo.icon);
        } catch (PackageManager.NameNotFoundException | Resources.NotFoundException e) {
            //if there is no icon do nothing
        }

        return permissionIcon;
    }

}
