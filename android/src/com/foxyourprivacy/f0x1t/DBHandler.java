package com.foxyourprivacy.f0x1t;



import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;

/**
 * General Database-API
 * Created by noah on 29.05.16. :)
 *
 * @author Noah
 */
public class DBHandler extends SQLiteOpenHelper {

    public static final String TABLE_USERDATA = "userdata";
    public static final String COLUMN_KEY = "lookup";
    //column-names
    public static final String COLUMN_APPNAME = "name";
    public static final String COLUMN_PERMISSIONS = "permissions";
    public static final String COLUMN_APPDESCRIPTION = "description";
    public static final String COLUMN_SETTING = "parameter";
    public static final String COLUMN_INITIAL = "initial";
    //type: for readout - 0=String, 1=BOOLEAN, 2=int
    public static final String COLUMN_TYPE = "type";
    //file-name
    private static final String DB_NAME = "rawdata.db";
    //DB-Version is updated, when changes in structur apply
    private static final int DB_VERSION = 33;
    //table-names
    private static final String TABLE_APPS = "apps";
    private static final String TABLE_LESSONS = "lessons";
    private static final String TABLE_CLASSES = "classes";
    private static final String TABLE_PERMISSIONS = "permissions";
    private static final String TABLE_SETTINGS = "settings";
    private static final String COLUMN_GOODNAME = "propername";
    private static final String COLUMN_SETTINGDESCRIPTION = "sdescription";
    private static final String COLUMN_LATEST = "latestvalue";
    private static final String COLUMN_LESSONNAME = "name";
    private static final String COLUMN_SLIDES = "slides";
    private static final String COLUMN_CLASS = "class";
    private static final String COLUMN_CLASSPOSITION = "position";
    //0=not available yet; 1=not yet read; 2=not yet finished; 3=finished
    private static final String COLUMN_STATUS = "status";
    private static final String COLUMN_DELAY = "delay";
    private static final String COLUMN_FREETIME = "freeat";
    private static final String COLUMN_LESSONTYPE = "ltype";
    private static final String COLUMN_EICHELN = "eicheln";
    private static final String COLUMN_POSITION = "position";
    private static final String COLUMN_CLASSNAME = "class_name";
    private static final String COLUMN_CLASSDESCRIPTION = "description";
    private static final String COLUMN_PERMISSIONNAME = "pname";
    private static final String COLUMN_PERMISSIONDESCRIPTION = "pdescription";
    private static final String COLUMN_PERMISSIONNICENAME = "propername";
    private static final String COLUMN_PERMISSIONLEVEL = "plevel";
    private static final String COLUMN_VALUE = "value";

    /**
     * Constructor
     *
     * @param context is most times the reference 'this' for refering
     */
    public DBHandler(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    /**
     * executed at creation of a DB
     *
     * @param db DB that is created
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        //SQL-queries to create the tables with specified properties
        String query = "CREATE TABLE " + TABLE_SETTINGS + "(" +
                COLUMN_SETTING + " TEXT PRIMARY KEY, " +
                COLUMN_TYPE + " INTEGER DEFAULT 0, " +
                COLUMN_INITIAL + " TEXT DEFAULT '-99', " +
                COLUMN_GOODNAME + " TEXT, " +
                COLUMN_LATEST + " TEXT DEFAULT '-99', " +
                COLUMN_SETTINGDESCRIPTION + " TEXT DEFAULT 'Hier fehlt leider eine Beschreibung. Du kannst gerne eine anfordern!'" +
                ");";
        db.execSQL(query);
        String query2 = "CREATE TABLE " + TABLE_APPS + "(" +
                COLUMN_APPNAME + " TEXT PRIMARY KEY, " +
                COLUMN_PERMISSIONS + " TEXT, " +
                COLUMN_APPDESCRIPTION + " TEXT " +
                ");";
        db.execSQL(query2);
        String query3 = "CREATE TABLE " + TABLE_CLASSES + "(" +
                COLUMN_CLASSNAME + " TEXT PRIMARY KEY, " +
                COLUMN_CLASSDESCRIPTION + " TEXT, " +
                COLUMN_CLASSPOSITION + " INTEGER " +
                ");";
        db.execSQL(query3);
        String query4 = "CREATE TABLE " + TABLE_LESSONS + "(" +
                COLUMN_LESSONNAME + " TEXT, " +
                COLUMN_STATUS + " INTEGER, " +
                COLUMN_CLASS + " TEXT, " +
                COLUMN_SLIDES + " TEXT, " +
                COLUMN_DELAY + " INTEGER, " +
                COLUMN_LESSONTYPE + " TEXT, " +
                COLUMN_FREETIME + " INTEGER, " +
                COLUMN_EICHELN + " INTEGER, " +
                COLUMN_POSITION + " INTEGER, " +
                "PRIMARY KEY (" + COLUMN_LESSONNAME + ", " + COLUMN_CLASS + ")" +
                ");";
        db.execSQL(query4);
        String query5 = "CREATE TABLE " + TABLE_PERMISSIONS + "(" +
                COLUMN_PERMISSIONNAME + " TEXT PRIMARY KEY, " +
                COLUMN_PERMISSIONDESCRIPTION + " TEXT DEFAULT 'Diese Permission hat leider bisher keine Beschreibung', " +
                COLUMN_PERMISSIONNICENAME + " TEXT DEFAULT 'Diese Permission hat leider bisher keinen besseren Namen', " +
                COLUMN_PERMISSIONLEVEL + " INTEGER DEFAULT -1" +
                ");";
        db.execSQL(query5);
        String query6 = "CREATE TABLE " + TABLE_USERDATA + "(" +
                COLUMN_KEY + " TEXT PRIMARY KEY, " +
                COLUMN_VALUE + " TEXT" +
                ");";
        db.execSQL(query6);

    }

    /**
     * executed when the version number has changed, overrides the DB
     *
     * @param db         updated DB
     * @param oldVersion old version number
     * @param newVersion new version number
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //deletes all tables and creates new (empty) DB
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SETTINGS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_APPS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CLASSES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LESSONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PERMISSIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERDATA);
        onCreate(db);
    }


    /**
     * Inserts a whole Array of Settings to the DB if it's empty or creates a new Column with the
     * current time to it and adds the value to that. In both cases the 'latest' column ist updated
     * with the current value
     *
     * @param values the Array of ContentValues to be inserted
     */
    public void addParamColumn(ContentValues[] values) {
        SQLiteDatabase db = getWritableDatabase();
        //Cursor to count the current Entries in the table
        Cursor count = db.rawQuery("SELECT COUNT(*) AS count FROM " + TABLE_SETTINGS + ";", null);
        count.moveToFirst();
        //output for the user, information of existing entries
        //Log.d("DBHandler", "Rawdata hat einträge: " + count.getInt(0));
        count.close();
        //Here should an output for the user happen instead of the Log but with similiar values
        //Log.d("DBHandler", "PARAM-Column Länge von values:" + values.length);

        int i = 0;
        //constructing a name for the new column
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss", Locale.GERMANY);
        String date = df.format(Calendar.getInstance(TimeZone.getDefault()).getTime());
        //adding the new column
        db.execSQL("ALTER TABLE " + TABLE_SETTINGS + " ADD COLUMN '(" + date + ")' TEXT;");
        for (ContentValues value : values
                ) {
            if (value != null) {
                String escapedValue = value.getAsString(COLUMN_INITIAL).replace("\"", ""); // replaces all " with *nothing* to avoid syntax errors in the database
                db.execSQL("UPDATE " + TABLE_SETTINGS + " SET '(" + date + ")' = \"" + escapeQuote(escapedValue) + "\", " + COLUMN_LATEST + " = \'" + value.getAsString(COLUMN_INITIAL).replaceAll("'", "''") + "\' WHERE " + COLUMN_SETTING + " = '" + value.getAsString(COLUMN_SETTING).replaceAll("'", "''") + "'");
                db.execSQL("INSERT OR IGNORE INTO " + TABLE_SETTINGS + " (" + COLUMN_SETTING + ", " + COLUMN_TYPE + ", " + COLUMN_INITIAL + ", " + COLUMN_LATEST + ") VALUES(\'" + escapeQuote(value.getAsString(COLUMN_SETTING)) + "\', \'" + value.getAsInteger(COLUMN_TYPE) + "\', \'" + escapeQuote(escapedValue) + "\', \'" + escapeQuote(escapedValue) + "\')");
                i++;

            }
        }
        renameEmptySettings();

        //output for user please
        //Log.d("DBHandler", "Rawdata eingefügt: " + i);
        db.close();

    }


    /**
     * function to add apps in a whole list of ContentValues
     *
     * @param values the list
     */
    public void addAppColumn(ContentValues[] values) {
        SQLiteDatabase db = getWritableDatabase();
        //Cursor for counting the entries
        Cursor count = db.rawQuery("SELECT COUNT(*) AS count FROM " + TABLE_APPS + ";", null);
        count.moveToFirst();
        //output for User here
        //Log.d("DBHandler", "APP-Column Länge von values:" + values.length);
        int i = 0;
        //if there are entries in the table
        if (count.getInt(0) > 0) {
            //output for User here
            //Log.d("DBHandler", "Apps hat einträge");
            count.close();
            //creating the name for the new column
            SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss", Locale.GERMANY);
            String date = df.format(Calendar.getInstance(TimeZone.getDefault()).getTime());
            //creating the new column
            db.execSQL("ALTER TABLE " + TABLE_APPS + " ADD COLUMN '(" + date + ")' TEXT;");
            //updating the new column with an active tag to signalise, that it's still installed
            for (ContentValues value : values
                    ) {
                if (value != null) {
                    db.execSQL("UPDATE " + TABLE_APPS + " SET '(" + date + ")' = 'active' WHERE " + COLUMN_APPNAME + " = '" + value.getAsString(COLUMN_APPNAME).replaceAll("'", "''") + "'");
                    i++;
                }
            }
        } else {
            //output for User here
            //Log.d("DBHandler", "Apps hat keine Einträge");
            count.close();
            //inserting all the Apps with their values into the table
            for (ContentValues value : values
                    ) {
                if (value != null) {
                    db.insert(TABLE_APPS, null, value);
                    i++;
                }
            }
        }
        //output for User here
        //Log.d("DBHandler", "Apps eingefügt: " + i + 1);
        db.close();

    }

    /**
     * function to retrieve information about a specific app
     *
     * @param name name of app of interest
     * @return String of semicolon-parted permissions of the app     *
     */
    public String getAppPermissions(String name) {
        SQLiteDatabase db = getWritableDatabase();
        //create a cursor, that gets the row that contains the asked app
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_APPS + " WHERE " + COLUMN_APPNAME + "=\"" + name + "\";", null);
        if (c != null && c.getCount() > 0) {
            c.moveToFirst();
            //permissions are saved in result String
            String result = (c.getString(c.getColumnIndex(COLUMN_PERMISSIONS)));
            c.close();
            db.close();
            return result;
        }
        db.close();
        return "App not found";


    }


    /**
     * function to give a String of the complete table of parameters
     *
     * @return a String that contains the TEXT from the parameter-table
     */
    public String printParams() {
        StringBuilder sb = new StringBuilder();
        //String dbstring = "";
        SQLiteDatabase db = getWritableDatabase();
        // get all rows of rawdata
        String query = "SELECT * FROM " + TABLE_SETTINGS + " WHERE 1;";
        //Cursor over all rows
        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();
        //repeat over all rows
        while (!c.isAfterLast()) {
            //append all info from the row to the String where there is a parameter name
            if (c.getString(c.getColumnIndex(COLUMN_SETTING)) != null) {
                sb.append(c.getString(c.getColumnIndex(COLUMN_SETTING)));
                sb.append(" ");
                sb.append(c.getString(c.getColumnIndex(COLUMN_TYPE)));
                sb.append(" ");
                sb.append(c.getString(c.getColumnIndex(COLUMN_INITIAL)));
                sb.append("\n");
               /* dbstring += c.getString(c.getColumnIndex(COLUMN_SETTING)) + " " +
                        c.getString(c.getColumnIndex(COLUMN_TYPE)) + " " +
                        c.getString(c.getColumnIndex(COLUMN_INITIAL));
                //append newline
                dbstring += "\n"; */
            }
            c.moveToNext();
        }
        c.close();
        db.close();
        return sb.toString();
    }

    /**
     * give a String that contains the info from App-table
     *
     * @return a String that contains the TEXT from the app-table
     */
    public String printApps() {
        StringBuilder sb = new StringBuilder();
        // String dbstring = "";
        SQLiteDatabase db = getWritableDatabase();
        // get all rows of apps
        String query = "SELECT * FROM " + TABLE_APPS + " WHERE 1;";
        //Cursor over all rows
        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();
        //repeat over all rows
        while (!c.isAfterLast()) {
            //append all info from the row to the String where there is a parameter name
            if (c.getString(c.getColumnIndex(COLUMN_APPNAME)) != null) {
                sb.append(c.getString(c.getColumnIndex(COLUMN_APPNAME)));
                sb.append(" ");
                sb.append(c.getString(c.getColumnIndex(COLUMN_PERMISSIONS)));
                sb.append(" ");
                sb.append(c.getString(c.getColumnIndex(COLUMN_APPDESCRIPTION)));
                sb.append("\n");

                /*
                dbstring += c.getString(c.getColumnIndex(COLUMN_APPNAME)) + " " +
                        c.getString(c.getColumnIndex(COLUMN_PERMISSIONS)) + " " +
                        c.getString(c.getColumnIndex(COLUMN_APPDESCRIPTION));
                //append newline
                dbstring += "\n";
                */

            }
            c.moveToNext();
        }
        c.close();
        db.close();
        return sb.toString();
    }

    /**
     * updates the Lession-Table from CSV-input
     *
     * @param theLessions the List of the lessions
     */
    public void updateLessons(ArrayList<String[]> theLessions) {
        SQLiteDatabase db = getWritableDatabase();

        long time = System.currentTimeMillis();
        // db.execSQL("DROP TABLE IF EXISTS "+TABLE_LESSONS);
        for (String[] lessonArray : theLessions) {
            try{
                String slidearray = createSlidesString(lessonArray).replace("'", "''");
                //Log.d("DBHANDLER", slidearray);
                if (checkIfInside(db, TABLE_LESSONS, COLUMN_LESSONNAME + " = \'" + escapeQuote(lessonArray[1]) + "\' AND " + COLUMN_CLASS + "= \'" + escapeQuote(lessonArray[0]) + "\'")) {
                    db.execSQL("UPDATE " + TABLE_LESSONS + " SET " + COLUMN_SLIDES + " = \'" + slidearray + "\', " + COLUMN_CLASS + " = \'" + escapeQuote(lessonArray[0]) + "\', " +
                            "\'" + COLUMN_DELAY + "\' = \'" + lessonArray[3] + "\', \'" + COLUMN_LESSONTYPE + "\' = \'" + lessonArray[2] + "\', \'" + COLUMN_EICHELN + "\' = \'" + lessonArray[4] +
                            "\', \'" + COLUMN_POSITION + "\' = \'" + lessonArray[5] + "\' WHERE " + COLUMN_LESSONNAME + " = \'" + escapeQuote(lessonArray[1]) + "\' AND " + COLUMN_CLASS + "= \'" + escapeQuote(lessonArray[0]) + "\';");
                } else {
                    db.execSQL("INSERT INTO " + TABLE_LESSONS + " VALUES(\'" + escapeQuote(lessonArray[1]) + "\', \'" + lessonArray[6] + "\', \'" + escapeQuote(lessonArray[0]) + "\', \'" + slidearray + "\', \'" + lessonArray[3] + "\', \'" + lessonArray[2] + "\', \'" + time + "\', \'" + lessonArray[4] + "\', \'" + lessonArray[5] + "\');");
                }
            }catch (IndexOutOfBoundsException ioobe){
                if (lessonArray.length > 1)
                    Log.e("DBHandler", "Fehler in Lektion: " + lessonArray[1]);
                else if (lessonArray.length == 1)
                    Log.e("DBHandler", "Fehler in Lektion: " + lessonArray[0]);
                else
                    Log.e("DBHandler", "Fehler in Lektion, Länge des lessonArrays: " + lessonArray.length);
                ioobe.printStackTrace();
            } catch (SQLiteException sqle) {
                Log.d("DBH.updateLessons", "There was an SQLiteException. Please review escaping of relevant fields. Lesson was: " + lessonArray[1] + " in Class " + lessonArray[0]);
                sqle.printStackTrace();
            }
        }
        db.close();

    }

    private String escapeQuote(String input) {
        return input.replaceAll("'", "''");
    }

    /**
     * updates the Classes-Table
     */
    public void updateClasses(ArrayList<String[]> classesList) {
        //insert all the classes from the file
        SQLiteDatabase db = getWritableDatabase();
        for (String[] clas : classesList) {
            db.execSQL("INSERT OR REPLACE INTO " + TABLE_CLASSES + " VALUES(\'" + escapeQuote(clas[0]) + "\', \'" + escapeQuote(clas[1]) + "\', \'" + escapeQuote(clas[2]) + "\');");
        }

        //safety-measure: inserting all classes that are specified from a lession in the DB,
        // so that every lession is reachable
        Cursor cursor = db.rawQuery("SELECT DISTINCT " + COLUMN_CLASS + " FROM " + TABLE_LESSONS + ";", null);
        cursor.moveToFirst();
        //repeat over all rows
        while (!cursor.isAfterLast()) {
            if (cursor.getString(cursor.getColumnIndex(COLUMN_CLASS)) != null) {
                db.execSQL("INSERT OR IGNORE INTO " + TABLE_CLASSES + " VALUES(\'" + escapeQuote(cursor.getString(cursor.getColumnIndex(COLUMN_CLASS))) + "\', \'keine Beschreibung vorhanden\', 99);");
            }
            cursor.moveToNext();
        }
        cursor.close();
        db.close();

    }

    /**
     * returns an ArrayList of all existant Classes in the DB
     *
     * @return ArrayList of ClassObjects
     */
    public ArrayList<ClassObject> getClasses() {
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_CLASSES + " WHERE 1;", null);
        ArrayList<ClassObject> result = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            result.add(new ClassObject(cursor.getString(cursor.getColumnIndex(COLUMN_CLASSNAME)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_CLASSDESCRIPTION)), cursor.getInt(cursor.getColumnIndex(COLUMN_CLASSPOSITION))));
            cursor.moveToNext();
        }
        cursor.close();
        db.close();
        return result;
    }

    public String getNumberOfSolvedLessons(String className) {
        SQLiteDatabase db = getWritableDatabase();
        Cursor unlocked = db.rawQuery("SELECT COUNT(*) AS count FROM " + TABLE_LESSONS + " WHERE "
                + COLUMN_CLASS + "=\'" + className + "\' AND " + COLUMN_STATUS + " IS NOT -99", null);
        Cursor solved = db.rawQuery("SELECT COUNT(*) AS count FROM " + TABLE_LESSONS + " WHERE "
                + COLUMN_CLASS + "=\'" + className + "\' AND " + COLUMN_STATUS + " IS 3", null);
        if (unlocked != null && solved != null) {
            unlocked.moveToFirst();
            solved.moveToFirst();
            int ul = unlocked.getInt(0);
            int s = solved.getInt(0);
            unlocked.close();
            solved.close();
            db.close();
            return s + "/" + ul;
        }
        db.close();
        return "notfound";

    }

    private String createSlidesString(String[] slides) {
        StringBuilder sb = new StringBuilder();
        for (int i = 7; i < slides.length; i++) {
            sb.append(slides[i]);
            sb.append(";");
        }
        return sb.toString();
    }

    /**
     * checks, if a where-clause results in entries in the specified table
     *
     * @param table       table to be searched in
     * @param whereclause description of content searched for
     * @return true/false
     */
    public Boolean checkIfInside(SQLiteDatabase db, String table, String whereclause) {
        Cursor cursor = db.rawQuery("SELECT * FROM " + table + " WHERE " + whereclause + "", null);
        if (cursor.getCount() <= 0) {
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }

    /**
     * gives all Lessons that belong to the specified class and are activated
     *
     * @param className identifier of the class
     * @return ArrayList of LessonObjects
     * @author Noah
     */
    public ArrayList<LessonObject> getLessonsFromDB(String className) {
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_LESSONS + " WHERE "
                + COLUMN_CLASS + "=\'" + className + "\' AND " + COLUMN_STATUS + " IS NOT -99", null);
        ArrayList<LessonObject> result = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            result.add(new LessonObject(
                    cursor.getString(cursor.getColumnIndex(COLUMN_LESSONNAME)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_SLIDES)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_LESSONTYPE)),
                    cursor.getInt(cursor.getColumnIndex(COLUMN_DELAY)),
                    cursor.getLong(cursor.getColumnIndex(COLUMN_FREETIME)),
                    cursor.getInt(cursor.getColumnIndex(COLUMN_STATUS)),
                    cursor.getInt(cursor.getColumnIndex(COLUMN_EICHELN)),
                    cursor.getInt(cursor.getColumnIndex(COLUMN_POSITION))));
            cursor.moveToNext();
        }
        cursor.close();
        db.close();
        return result;
    }


    /**
     * updates the permission-table with their data from CSV-input
     *
     * @param permissionlist the List of permissions from CSV
     * @author Noah
     */
    public void updatePermissions(ArrayList<String[]> permissionlist) {
        SQLiteDatabase db = getWritableDatabase();
        for (String[] array : permissionlist) {
            if (array.length == 1) {
                db.execSQL("INSERT OR REPLACE INTO " + TABLE_PERMISSIONS + " (\'" + COLUMN_PERMISSIONNAME + "\') VALUES(\'" + escapeQuote(array[0]) + "\');");
            }
            if (array.length == 2) {
                db.execSQL("INSERT OR REPLACE INTO " + TABLE_PERMISSIONS + " (\'" + COLUMN_PERMISSIONNAME + "\', \'" + COLUMN_PERMISSIONDESCRIPTION + "\') VALUES(\'" + escapeQuote(array[0]) + "\', \'" + array[1].replace("'", "''") + "\');");
            }
            if (array.length == 3) {
                db.execSQL("INSERT OR REPLACE INTO " + TABLE_PERMISSIONS + " (\'" + COLUMN_PERMISSIONNAME + "\', \'" + COLUMN_PERMISSIONDESCRIPTION + "\', \'" + COLUMN_PERMISSIONNICENAME + "\') VALUES(\'" + escapeQuote(array[0]) + "\', \'" + array[1].replace("'", "''") + "\', \'" + array[2].replace("'", "''") + "\');");
            }
            if (array.length == 4) {
                db.execSQL("INSERT OR REPLACE INTO " + TABLE_PERMISSIONS + " (\'" + COLUMN_PERMISSIONNAME + "\', \'" + COLUMN_PERMISSIONDESCRIPTION + "\', \'" + COLUMN_PERMISSIONNICENAME + "\', \'" + COLUMN_PERMISSIONLEVEL + "\') VALUES(\'" + escapeQuote(array[0]) + "\', \'" + array[1].replace("'", "''") + "\', \'" + array[2].replace("'", "''") + "\', \'" + array[3] + "\');");
            }
        }
        db.close();
    }

    /**
     * get the description of a permissionidentifier
     *
     * @param permissionname the identifier of the permission
     * @return a String with the text of description
     * @author Noah
     */
    public String getPermissionDescription(String permissionname) {
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_PERMISSIONS + " WHERE " + COLUMN_PERMISSIONNAME + " = \'" + permissionname + "\';", null);
        //if more than the name column is filled and there is an entry
        if (cursor.getColumnCount() > 1 && cursor.getCount() > 0) {
            cursor.moveToFirst();
            String result = cursor.getString(cursor.getColumnIndex(COLUMN_PERMISSIONDESCRIPTION));
            cursor.close();
            return result;
            //if there is no description for the permission
        } else {
            cursor.close();
            return "Diese Permission hat noch keine Beschreibung.";
        }
    }

    /**
     * get the good name of a permissionidentifier
     *
     * @param permissionname the identifier
     * @return the good name as String (or default apologise)
     * @author Noah
     */
    String getPermissionNiceName(String permissionname) {
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_PERMISSIONS + " WHERE " + COLUMN_PERMISSIONNAME + " = \'" + permissionname + "\';", null);
        //if there are more than 2 Columns are filled, a description is there
        if (cursor.getColumnCount() > 2) {
            cursor.moveToFirst();
            String result = cursor.getString(cursor.getColumnIndex(COLUMN_PERMISSIONNICENAME));
            cursor.close();
            return result;
            //if no description is found
        } else {
            cursor.close();
            return "Diese Permission hat noch keinen besseren Namen.";
        }
    }

    /**
     * get the level of the asked permission
     *
     * @param permissionname permissionidentifier
     * @return level as integer
     * @author
     */
    int getPermissionLevel(String permissionname) {
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_PERMISSIONS + " WHERE " + COLUMN_PERMISSIONNAME + " = \'" + permissionname + "\';", null);
        //if there are more than 3 Columns found, there is a level found
        if (cursor.getColumnCount() > 3) {
            cursor.moveToFirst();
            int result = cursor.getInt(cursor.getColumnIndex(COLUMN_PERMISSIONLEVEL));
            cursor.close();
            return result;
            //when there is no level in the DB
        } else {
            cursor.close();
            return -1;
        }

    }

    /**
     * change the lesson's status from unread (1) to read (2)
     *
     * @param lessonName name of the lesson to be changed
     */
    public void changeLessonToRead(String lessonName, String className) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("UPDATE " + TABLE_LESSONS + " SET " + COLUMN_STATUS + " = 2 WHERE " + COLUMN_LESSONNAME + " = \'" + escapeQuote(lessonName) + "\' AND " + COLUMN_CLASS + " = \'" + escapeQuote(className) + "\';");
        db.close();
    }

    /**
     * change the lesson's status from unread (1) or read (2) to solved (3)
     *
     * @param lessonName name of the lesson to be changed
     */
    public void changeLessonToSolved(String lessonName) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("UPDATE " + TABLE_LESSONS + " SET " + COLUMN_STATUS + " = 3 WHERE " + COLUMN_LESSONNAME + " = \'" + escapeQuote(lessonName) + "\';");
        db.close();

    }

    /**
     * change the lesson's status from unread (1) or read (2) to solved (3)
     *
     * @param lessonName name of the lesson to be changed
     */
    public void changeEvaluationToSolved(String lessonName) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("UPDATE " + TABLE_LESSONS + " SET " + COLUMN_STATUS + " = -98 WHERE " + COLUMN_LESSONNAME + " = \'" + escapeQuote(lessonName) + "\';");
        db.close();

    }

    /**
     * try to change the lesson's status from locked (0) to unlocked (1) and return true on
     * success or false if the lesson could not been found
     *
     * @param lessonName name of the lesson to be changed
     * @return true/false wether the lesson was found
     */
    public boolean changeLessonToUnlocked(String lessonName) {
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_LESSONS + " WHERE " + COLUMN_LESSONNAME + " = \'" + lessonName + "\';", null);
        if (cursor != null) {
            cursor.moveToFirst();
            if (cursor.getCount() > 0) {
                db.execSQL("UPDATE " + TABLE_LESSONS + " SET " + COLUMN_STATUS + " = 1 WHERE " + COLUMN_LESSONNAME + " = \'" + lessonName + "\';");
                cursor.close();
                db.close();
                return true;
            }
        }
        db.close();
        return false;
    }

    /**
     * set a lessons nextFreeTime according to the input
     *
     * @param lessonName  name of the lesson to be changed
     * @param nextFreeTime the time the lesson is available again in ms
     */
    public void setLessonNextFreeTime(String lessonName, String className, long nextFreeTime) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("UPDATE " + TABLE_LESSONS + " SET " + COLUMN_FREETIME + " = \'" + nextFreeTime + "\' WHERE " + COLUMN_LESSONNAME + " = \'" + escapeQuote(lessonName) + "\' AND " + COLUMN_CLASS + " = \'" + className + "\';");
        db.close();
    }


    public void updateSettingDescriptions(ArrayList<String[]> settingsData) {
        SQLiteDatabase db = getWritableDatabase();
        for (String[] setting : settingsData) {
            if (checkIfInside(db, TABLE_SETTINGS, COLUMN_SETTING + " = \'" + escapeQuote(setting[0]) + "\'")) {
                if (setting.length == 2) {
                    db.execSQL("UPDATE " + TABLE_SETTINGS + " SET "
                            + COLUMN_TYPE + " = " + setting[1]
                            + " WHERE " + COLUMN_SETTING + " = \'" + escapeQuote(setting[0]) + "\';");
                } else if (setting.length == 3) {
                    db.execSQL("UPDATE " + TABLE_SETTINGS + " SET "
                            + COLUMN_TYPE + " = " + setting[1] + ", "
                            + COLUMN_GOODNAME + " = \'" + escapeQuote(setting[2]) + "\' "
                            + "WHERE " + COLUMN_SETTING + " = \'" + escapeQuote(setting[0]) + "\';");
                } else if (setting.length >= 4) {
                    db.execSQL("UPDATE " + TABLE_SETTINGS + " SET "
                            + COLUMN_TYPE + " = " + setting[1] + ", "
                            + COLUMN_GOODNAME + " = \'" + escapeQuote(setting[2]) + "\', "
                            + COLUMN_SETTINGDESCRIPTION + " = \'"
                            + setting[3] + "\' " +
                            "WHERE " + COLUMN_SETTING + " = \'" + escapeQuote(setting[0]) + "\';");
                }
            } else {
                if (setting.length == 1) {
                    db.execSQL("INSERT INTO " + TABLE_SETTINGS + "("
                            + COLUMN_SETTING
                            + ") VALUES(\'" +
                            escapeQuote(setting[0]) + "\')");
                } else if (setting.length == 2) {
                    db.execSQL("INSERT INTO " + TABLE_SETTINGS + "("
                            + COLUMN_SETTING + ", "
                            + COLUMN_TYPE
                            + ") VALUES(\'" +
                            escapeQuote(setting[0]) + "\', \'" + setting[1] + "\')");
                } else if (setting.length == 3) {
                    db.execSQL("INSERT INTO " + TABLE_SETTINGS + "("
                            + COLUMN_SETTING + ", "
                            + COLUMN_TYPE + ", "
                            + COLUMN_GOODNAME
                            + ") VALUES(\'" +
                            escapeQuote(setting[0]) + "\', \'" + setting[1] + "\', \'" + escapeQuote(setting[2]) + "\')");
                } else
                    db.execSQL("INSERT INTO " + TABLE_SETTINGS + "("
                            + COLUMN_SETTING + ", "
                            + COLUMN_TYPE + ", "
                            + COLUMN_GOODNAME + ", "
                            + COLUMN_SETTINGDESCRIPTION + ") VALUES(\'" +
                            escapeQuote(setting[0]) + "\', \'" + setting[1] + "\', \'" + escapeQuote(setting[2]) + "\', \'" + escapeQuote(setting[3]) + "\')");
            }
        }
        renameEmptySettings();
        db.close();
    }

    private void renameEmptySettings() {
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COLUMN_SETTING + " FROM " + TABLE_SETTINGS + " WHERE " + COLUMN_GOODNAME + " IS NULL;", null);
        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                String name = cursor.getString(0).replace(".", " ");
                //Log.d("renameEmptySettings", "name from cursor and . " + name);
                name = escapeQuote(name.replace("_", " "));
                //Log.d("renameEmptySettings", "name ' and _ " + name);

                name = name.substring(0, 1).toUpperCase() + name.substring(1);
                //Log.d("renameEmptySettings", "name substring " + name);

                db.execSQL("UPDATE " + TABLE_SETTINGS + " SET " + COLUMN_GOODNAME + " = \'" + name + "\' WHERE " + COLUMN_SETTING + " = \'" + escapeQuote(cursor.getString(0)) + "\';");
                cursor.moveToNext();
            }
            cursor.close();
        }
        db.close();

    }

    public String[] getSettingsFromDB() {
        //create String array with correct length
        String[] settingsArray;
        Cursor count = getWritableDatabase().rawQuery("SELECT COUNT(*) AS count FROM " + TABLE_SETTINGS + " WHERE " + COLUMN_LATEST + " IS NOT '-99';", null);
        count.moveToFirst();
        settingsArray = new String[count.getInt(0)];
        count.close();
        //fill array with DB-entries
        Cursor c = getWritableDatabase().rawQuery("SELECT " + COLUMN_GOODNAME + ", " + COLUMN_LATEST + ", " + COLUMN_SETTING + ", " + COLUMN_SETTINGDESCRIPTION + " FROM " + TABLE_SETTINGS + " WHERE " + COLUMN_LATEST + " IS NOT '-99';", null);
        if (c != null) c.moveToFirst();
        int i = 0;
        while (c != null && !c.isAfterLast()) {
            settingsArray[i] = (c.getString(c.getColumnIndex(COLUMN_GOODNAME)) + "|t1|" + c.getString(c.getColumnIndex(COLUMN_LATEST)) + "|t2|" + c.getString(c.getColumnIndex(COLUMN_SETTING)) + "|t3|" + c.getString(c.getColumnIndex(COLUMN_SETTINGDESCRIPTION)));
            i++;
            c.moveToNext();
        }
        if (c != null) c.close();
        return settingsArray;

    }

    public void insertIndividualData(HashMap<String, String> hashMap) {
        SQLiteDatabase db = getWritableDatabase();
        Set<String> keys = hashMap.keySet();
        String value;
        for (String key : keys) {
            if (hashMap.get(key) != null) value = hashMap.get(key).replaceAll("'", "''");
            else value = "null";
            db.execSQL("INSERT OR REPLACE INTO " + TABLE_USERDATA + " VALUES(\'" + key.replaceAll("'", "\'") + "\', \'" + value + "\');");
        }
        db.close();
    }

    public HashMap<String, String> getIndividualData() {
        SQLiteDatabase db = getWritableDatabase();
        HashMap<String, String> result = new HashMap<>();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERDATA + " WHERE 1", null);
        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                result.put(cursor.getString(0), cursor.getString(1));
                cursor.moveToNext();
            }
            cursor.close();
        } else {
            result.put("leer", "leer");
        }
        return result;
    }

    public void changeIndividualValue(String key, String value) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("UPDATE " + TABLE_USERDATA + " SET " + COLUMN_VALUE + " = \'" + escapeQuote(value) + "\' WHERE " + COLUMN_KEY + " = \'" + key + "\';");
        db.close();
    }

    public void insertIndividualValue(String key, String value) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("INSERT OR REPLACE INTO " + TABLE_USERDATA + " VALUES(\'" + key + "\', \'" + escapeQuote(value) + "\');");
        db.close();
    }

    public String getIndividualValue(String key) {
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COLUMN_VALUE + " FROM " + TABLE_USERDATA + " WHERE " + COLUMN_KEY + " = \'" + key + "\';", null);
        if (cursor == null) {
            db.close();
            return "notfound";
        }
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            String res = cursor.getString(0);
            cursor.close();
            db.close();
            return res;
        }
        cursor.close();
        db.close();
        return "notfound";
    }

    public void clearAppsFromVK() {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_USERDATA, COLUMN_KEY + " LIKE \'app:%\'", null);
        db.close();
    }

    public void clearDAppsFromVK() {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_USERDATA, COLUMN_KEY + " LIKE \'dap:%\'", null);
        db.close();
    }

    public String unlockDaily() {
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COLUMN_LESSONNAME + " FROM " + TABLE_LESSONS + " WHERE " + COLUMN_STATUS + " IS -99", null);
        if (cursor != null) {
            cursor.moveToFirst();

            String name = cursor.getString(0);
            cursor.close();
            db.execSQL("UPDATE " + TABLE_LESSONS + " SET " + COLUMN_STATUS + " = 1 WHERE " + COLUMN_LESSONNAME + " = \'" + escapeQuote(name) + "\';");
            return name;
        }
        return "There is no new lesson.";
    }


    public void exportDB() {
        //Log.d("DBHandler", "exporting DB");
        String user = ValueKeeper.getInstance().getUsername();
        String timestamp = String.valueOf(System.currentTimeMillis());
        File stordir = Environment.getExternalStorageDirectory();
        File data = Environment.getDataDirectory();
        FileChannel src;
        FileChannel dest;
        String currentDBPath = "/data/" + "com.foxyourprivacy.f0x1t" + "/databases/" + DB_NAME;
        String backupDBPath = user + DB_NAME + timestamp;
        File currentDB = new File(data, currentDBPath);
        File backupDB = new File(stordir, backupDBPath);
        try {
            src = new FileInputStream(currentDB).getChannel();
            dest = new FileOutputStream(backupDB).getChannel();
            dest.transferFrom(src, 0, src.size());
            src.close();
            dest.close();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }

    }

    public int howManyDailiesUnlocked() {
        SQLiteDatabase db = getWritableDatabase();
        Cursor count = db.rawQuery("SELECT COUNT(*) AS count FROM " + TABLE_LESSONS + " WHERE " + COLUMN_CLASS + " IS \'Daily Lessons\' AND " + COLUMN_STATUS + " IS NOT -99;", null);
        if (count != null) {
            count.moveToFirst();
            int result = count.getInt(0);
            count.close();
            return result;
        }
        return 0;
    }
}
