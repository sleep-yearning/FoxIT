package com.foxyourprivacy.privacyRiskInfo.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.foxyourprivacy.privacyRiskInfo.ClassObject;
import com.foxyourprivacy.privacyRiskInfo.DBHandler;
import com.foxyourprivacy.privacyRiskInfo.R;

import java.util.ArrayList;

/**
 * In this activity the classes are listed to choose on and go on to lesson selection
 * the number of solved lessons per class is shown and classes are ordered after their index in the classes.csv
 * Created by Tim on 01.08.2016.
 */
public class ClassListActivity extends FoxITActivity implements AdapterView.OnItemClickListener {

    private ArrayList<ClassObject> classObjectList = new ArrayList<>();
    private Parcelable listState;
    private ListView theListView;


    @Override
    public void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_list);
        // sets our toolbar as the actionbar
        Toolbar toolbar = findViewById(R.id.foxit_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        DBHandler dbHandler = new DBHandler(this);

        //classObjectList.add(test);
        classObjectList = dbHandler.getClasses();

        //sort Erstes Tapsen and Daily Lessons on the first 2 slots
        ArrayList<ClassObject> sortedList = new ArrayList<>();
        int pointer = 0;
        boolean inserted = false;
        for (ClassObject co : classObjectList) {

            if (sortedList.isEmpty()) {
                sortedList.add(co);
                inserted = true;
            }
            int position = co.getPosition();
            //as long as the correct position was not found
            while (!inserted) {
                //is the class after the current class
                if (position >= sortedList.get(pointer).getPosition()) {
                    //is the class before the next class or is the last element reached
                    if (pointer + 1 >= sortedList.size() || position < sortedList.get(pointer + 1).getPosition()) {
                        //add as last, new highest index or after the next
                        pointer++;
                        sortedList.add(pointer, co);
                        inserted = true;
                        //the class belongs further in the list
                    } else {
                        pointer = (sortedList.size() + pointer) / 2;
                    }
                    //class is before the current class
                } else {
                    //the first element is reached or the class belongs after the previous class
                    if (pointer == 0 || position > sortedList.get(pointer - 1).getPosition()) {
                        sortedList.add(pointer, co);
                        inserted = true;
                        //class belongs earlier in the list
                    } else {
                        pointer = pointer / 2;
                    }
                }
            }
            inserted = false;
            pointer = (sortedList.size() / 2);



        }
        classObjectList = sortedList;

        dbHandler.close();

        theListView = findViewById(R.id.headline_frame);
        //creates the listView
        ArrayAdapter<ClassObject> adapter = new MyListAdapter_class();
        theListView.setAdapter(adapter);
        theListView.setOnItemClickListener(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.toolbar_activities, menu);
        menu.findItem(R.id.action_options).setVisible(false);
        menu.findItem(R.id.settings).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * onItemClick the Activity is switched to lessonActivity
     *
     * @author Tim
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(getApplicationContext(), LessonListActivity.class);
        //to inform lessonActivity which lesson is to be displayed
        intent.putExtra("description", classObjectList.get(position).getDescriptionText());
        intent.putExtra("name", classObjectList.get(position).getName());
        intent.putExtra("icon", getClassIcon(classObjectList.get(position).getName()));
        startActivity(intent);
    }

    /**
     * just an experiment if you can change the listEntrys afterwards
     */
    @Override
    public void onResume() {
        super.onResume();
        if (listState != null) {
            theListView.onRestoreInstanceState(listState);
        }
        listState = null;
        //TODO: sollte vielleicht auskommentiert werden oder verändert, so dass die Liste beim zurück-gehen nicht immer wieder von oben angezeigt wird.

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        listState = savedInstanceState.getParcelable("liststate");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        listState = theListView.onSaveInstanceState();
        outState.putParcelable("liststate", listState);
    }

    private int getClassIcon(String className) {
        switch (className) {
            case "Erstes Tapsen":
                return R.mipmap.literature;
            case "Facebook":
                return R.mipmap.class_facebook;
            case "Google":
                return R.mipmap.class_google;
            case "Berechtigungen":
                return R.mipmap.class_berechtigungen;
            case "Messaging-Apps":
                return R.mipmap.class_messanging;
            case "Die Gesellschaft":
                return R.mipmap.class_lighthouse;
            case "Passwörter":
                return R.mipmap.class_passwort;
            case "Datenschutzgesetze":
                return R.mipmap.class_law;
            case "Daily Lessons":
                return R.mipmap.class_daily;
            case "Verschlüsselung":
                return R.mipmap.class_verschlusselung;
            case "root & superuser":
                return R.mipmap.class_hashtag;
            case "Deep Web":
                return R.mipmap.onion;
            default:
                return R.mipmap.ring;

        }
    }

    @Override
    public void onPause() {
        listState = theListView.onSaveInstanceState();
        super.onPause();
    }


    /**
     * class to define the way the settings are displayed in the listView
     *
     * @author Tim
     */
    private class MyListAdapter_class extends ArrayAdapter<ClassObject> {
        MyListAdapter_class() {
            //here is defined what layout the listView uses for a single entry
            super(getApplicationContext(), R.layout.layout_settings, classObjectList);

        }

        /**
         * Here ist defined how the XML-Layout is filed  by the data stored in the lessonStringArray.
         *
         * @param position    position in the array used
         * @param convertView
         * @param parent
         * @return
         * @author Tim
         */
        @Override
        @NonNull
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            //convertView has to be filled with layout_app if it's null
            View itemView = convertView;
            if (itemView == null) {
                itemView = getLayoutInflater().
                        inflate(R.layout.layout_class_listentry,
                                parent,
                                false);
            }

            ImageView classIcon = itemView.findViewById(R.id.image_class_icon);

            classIcon.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), getClassIcon(classObjectList.get(position).getName())));


            //setting the text for className
            TextView className = itemView.findViewById(R.id.text_class_name);
            className.setText(classObjectList.get(position).getName());

            DBHandler dbHandler = new DBHandler(getApplicationContext());
            TextView solved = itemView.findViewById(R.id.textView_solvedLessons);
            solved.setText(dbHandler.getNumberOfSolvedLessons(classObjectList.get(position).getName()));
            dbHandler.close();


            return itemView;
        }


    }
}
