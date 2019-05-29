package com.foxyourprivacy.privacyRiskInfo.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.foxyourprivacy.privacyRiskInfo.DBHandler;
import com.foxyourprivacy.privacyRiskInfo.LessonObject;
import com.foxyourprivacy.privacyRiskInfo.R;
import com.foxyourprivacy.privacyRiskInfo.ValueKeeper;
import com.foxyourprivacy.privacyRiskInfo.asynctasks.DBWrite;

import java.util.ArrayList;

//Displayes the lessons corresponding to a certain class and manages their usage
public class LessonListActivity extends FoxITActivity implements AdapterView.OnItemClickListener {

    //List of lessonDescriptions, send by ClassListActivity
    private static String[] lessonStringArray; //has to be static for now
    private static String className;//has to be static for now
    private static String classDescriptionText; //Text describing the class currently on display
    //list of lessonObjects generated to fill the ListView
    private ArrayList<LessonObject> lessonObjectList = new ArrayList<>();
    private boolean descriptionVisible = false; //true if the classDescription is visible
    private ArrayAdapter<String> adapter;

    /**
     * @author Tim
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson_list);

        // sets our toolbar as the actionbar
        Toolbar toolbar = findViewById(R.id.foxit_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (toolbar != null) {
            toolbar.setTitle("Toolbar");
        }


        //retrieve the lessonDescriptions
        if (getIntent().getStringArrayExtra("lesson") != null) {
            lessonStringArray = getIntent().getStringArrayExtra("lesson");
        }

        //retrieve the className
        if (getIntent().getStringExtra("name") != null) {
            className = getIntent().getStringExtra("name");
        }

        if (getIntent().getStringExtra("description") != null) {
            classDescriptionText = getIntent().getStringExtra("description");
        }

        if (getIntent().getIntExtra("icon", 0) != 0) {
            ImageView icon = findViewById(R.id.image_class_icon);
            icon.setImageDrawable(ContextCompat.getDrawable(this, getIntent().getIntExtra("icon", 0)));
        }


        View.OnClickListener expandText = new View.OnClickListener() {
            /**
             * Display the descriptionText if the headline is pressed
             *
             * @author Tim
             */
            @Override
            public void onClick(View v) {
                if (descriptionVisible) {
                    //hide the description if it is visible
                    TextView classDescription = findViewById(R.id.description_text);
                    classDescription.setVisibility(View.GONE);
                    //make the icon visible
                    ImageView moreVertImage = findViewById(R.id.more_vert_image);
                    moreVertImage.setVisibility(View.VISIBLE);

                    descriptionVisible = false;
                } else {
                    //show the description Text if it is invisible
                    TextView classDescription = findViewById(R.id.description_text);
                    classDescription.setVisibility(View.VISIBLE);
                    //hide the icon
                    ImageView moreVertImage = findViewById(R.id.more_vert_image);
                    moreVertImage.setVisibility(View.GONE);

                    descriptionVisible = true;
                }
            }

        };

        // show the classDescription on Click
        RelativeLayout descriptionFrame = findViewById(R.id.description_frame);
        descriptionFrame.setOnClickListener(expandText);

        // show the classDescription on Click
        RelativeLayout classHeadline = findViewById(R.id.class_headline_frame);
        classHeadline.setOnClickListener(expandText);


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.toolbar_activities, menu);
        menu.findItem(R.id.action_options).setVisible(false);
        menu.findItem(R.id.settings).setVisible(false);
        setTitle(className);
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



            if (lessonObjectList.get(position).getNextfreetime() > (System.currentTimeMillis())) {

                //display the animation description
                //Log.d("nextfreetime: ", lessonObjectList.get(position).getNextfreetime() + " ");
                //Log.d("currenttime :", System.currentTimeMillis() + " ");
                Toast.makeText(getApplicationContext(),
                        "Diese Lektion ist erst wieder in " + (lessonObjectList.get(position).getNextfreetime() - System.currentTimeMillis()) / 60000 + " Minuten verfügbar.", Toast.LENGTH_LONG).show();


            } else {

                //Log.d("MyApp", "duTime" + Long.toString(lessonObjectList.get(position).getNextfreetime()));


                Intent intent = new Intent(getApplicationContext(), LessonActivity.class);
                //to inform lessonActivity which lesson is to be displayed

                intent.putExtra("lesson", lessonObjectList.get(position));
                intent.putExtra("classname", className);
                startActivity(intent);
            }


    }

    /**
     * @author Tim
     * reloads the listEntrys afterwards,
     */
    @Override
    public void onResume() {
        ListView lessonList = findViewById(R.id.headline_frame);
        DBHandler db = new DBHandler(this);
        lessonObjectList = db.getLessonsFromDB(className);
        db.close();

        lessonStringArray = new String[lessonObjectList.size()];

        TextView name = findViewById(R.id.text_class_name);
        name.setText(className);

        int lessonNumber = lessonObjectList.size();
        int solvedLessonNumber = 0;
        for (LessonObject l : lessonObjectList) {
            if (l.getProcessingStatus() == 3) {
                solvedLessonNumber++;
            }
        }

        ValueKeeper v = ValueKeeper.getInstance();


        TextView solved = findViewById(R.id.text_percentage_solved);
        solved.setText(getString(R.string.solvedLessonsCount, solvedLessonNumber, lessonNumber));

        TextView description = findViewById(R.id.description_text);
        description.setText(classDescriptionText);


        //creates the listView
        adapter = new MyListAdapter_lesson();
        lessonList.setAdapter(adapter);
        lessonList.setOnItemClickListener(this);
        adapter.notifyDataSetChanged();
        super.onResume();
    }

    /**
     * handles the purchase of a lesson
     *
     * @return if the purchase was successful
     * @author Tim
     */
    public boolean purchase(String articleOfCommerce) {
        for (LessonObject l : lessonObjectList) {
            if (l.getLessonName().equals(articleOfCommerce)) {
                //sets the lessons status to unlocked
                l.setProcessingStatus(1);
                //update the listView

                adapter.notifyDataSetChanged();
                new DBWrite().execute(this,"changeLessonToUnlocked", articleOfCommerce);
                return true;
            }
        }
        return false;
    }


    /**
     * class to define the way the settings are displayed in the listView by defining how the content is displayed in the list entries
     *
     * @author Tim
     */
    private class MyListAdapter_lesson extends ArrayAdapter<String> {
        public MyListAdapter_lesson() {
            //here is defined what layout the listView uses for a single entry
            super(getApplicationContext(), R.layout.layout_settings, lessonStringArray);

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
                itemView = getLayoutInflater().inflate(R.layout.layout_lesson_listentry, parent, false);
            }

            LessonObject lesson = lessonObjectList.get(position);
            //new LessonObject(lessonStringArray[position]);

            //setting the text for lessonName
            TextView lessonName = itemView.findViewById(R.id.text_lesson_name);
            lessonName.setText(lesson.getLessonName());
            if (lesson.getProcessingStatus() == 0) {
                lessonName.setTextColor(Color.GRAY);
            } else {
                lessonName.setTextColor(Color.BLACK);
            }


            //setting the stars
            ImageView solvedIcon = itemView.findViewById(R.id.image_lesson_solved);
            if (lesson.getNextfreetime() > System.currentTimeMillis()) {
                solvedIcon.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.mipmap.ic_hourglass_empty_black_48dp));
            } else {

                solvedIcon.setImageDrawable(getIcon(lesson.getProcessingStatus()));
            }
            return itemView;
        }

        /**
         * returns the fitting starIcon for a lessons state of solving
         *
         * @param processingStatus the lessons processingStatus accessible with "processingStatus"
         * @return the fitting drawable for the status of the lesson
         * @author Tim
         */
        Drawable getIcon(int processingStatus) {
            Context context = getApplicationContext();
            switch (processingStatus) {
                case 0:
                    return ContextCompat.getDrawable(context, R.mipmap.ic_lock_outline_black_48dp);
                case 1:
                    return ContextCompat.getDrawable(context, R.mipmap.stern_leer);
                case 2:
                    return ContextCompat.getDrawable(context, R.mipmap.stern_halbvoll);
                case 3:
                    return ContextCompat.getDrawable(context, R.mipmap.stern_voll2);
                default:
                    return ContextCompat.getDrawable(context, R.mipmap.stern_leer);
            }
        }

    }

}




