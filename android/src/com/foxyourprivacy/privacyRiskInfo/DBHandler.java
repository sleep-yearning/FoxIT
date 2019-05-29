package com.foxyourprivacy.privacyRiskInfo;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

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
    public static final String COLUMN_INITIAL = "initial";
    //type: for readout - 0=String, 1=BOOLEAN, 2=int
    public static final String COLUMN_TYPE = "type";
    //file-name
    private static final String DB_NAME = "rawdata.db";
    //DB-Version is updated, when changes in structur apply
    private static final int DB_VERSION = 40;
    //table-names

    private static final String TABLE_LESSONS = "lessons";
    private static final String TABLE_CLASSES = "classes";
    private static final String COLUMN_LESSONNAME = "name";
    private static final String COLUMN_SLIDES = "slides";
    private static final String COLUMN_CLASS = "class";
    private static final String COLUMN_CLASSPOSITION = "position";
    //0=not available yet; 1=not yet read; 2=not yet finished; 3=finished
    private static final String COLUMN_STATUS = "status";
    private static final String COLUMN_DELAY = "delay";
    private static final String COLUMN_FREETIME = "freeat";
    private static final String COLUMN_LESSONTYPE = "ltype";
    private static final String COLUMN_POSITION = "position";
    private static final String COLUMN_CLASSNAME = "class_name";
    private static final String COLUMN_CLASSDESCRIPTION = "description";
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
                COLUMN_POSITION + " INTEGER, " +
                "PRIMARY KEY (" + COLUMN_LESSONNAME + ", " + COLUMN_CLASS + ")" +
                ");";
        db.execSQL(query4);
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
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CLASSES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LESSONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERDATA);
        onCreate(db);
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
            try {
                String slidearray = createSlidesString(lessonArray).replace("'", "''");
                //Log.d("DBHANDLER", slidearray);
                if (checkIfInside(db, TABLE_LESSONS, COLUMN_LESSONNAME + " = \'" + escapeQuote(lessonArray[1]) + "\' AND " + COLUMN_CLASS + "= \'" + escapeQuote(lessonArray[0]) + "\'")) {
                    db.execSQL("UPDATE " + TABLE_LESSONS + " SET " + COLUMN_SLIDES + " = \'" + slidearray + "\', " + COLUMN_CLASS + " = \'" + escapeQuote(lessonArray[0]) + "\', " +
                            "\'" + COLUMN_DELAY + "\' = \'" + lessonArray[3] + "\', \'" + COLUMN_LESSONTYPE + "\' = \'" + lessonArray[2] +
                            "\', \'" + COLUMN_POSITION + "\' = \'" + lessonArray[5] + "\' WHERE " + COLUMN_LESSONNAME + " = \'" + escapeQuote(lessonArray[1]) + "\' AND " + COLUMN_CLASS + "= \'" + escapeQuote(lessonArray[0]) + "\';");
                } else {
                    db.execSQL("INSERT INTO " + TABLE_LESSONS + " VALUES(\'" + escapeQuote(lessonArray[1]) + "\', \'" + lessonArray[6] + "\', \'" + escapeQuote(lessonArray[0]) + "\', \'" + slidearray + "\', \'" + lessonArray[3] + "\', \'" + lessonArray[2] + "\', \'" + time + "\', \'" + lessonArray[5] + "\');");
                }
            } catch (IndexOutOfBoundsException ioobe) {
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

    public ClassObject getSingleClass(String name) {
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_CLASSES + " WHERE " + COLUMN_CLASSNAME + " = \'" + escapeQuote(name) + "\';", null);
        cursor.moveToFirst();
        ClassObject result = new ClassObject(cursor.getString(cursor.getColumnIndex(COLUMN_CLASSNAME)),
                cursor.getString(cursor.getColumnIndex(COLUMN_CLASSDESCRIPTION)), cursor.getInt(cursor.getColumnIndex(COLUMN_CLASSPOSITION)));
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
                    cursor.getInt(cursor.getColumnIndex(COLUMN_POSITION))));
            cursor.moveToNext();
        }
        cursor.close();
        db.close();
        return result;
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
     * @param lessonName   name of the lesson to be changed
     * @param nextFreeTime the time the lesson is available again in ms
     */
    public void setLessonNextFreeTime(String lessonName, String className, long nextFreeTime) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("UPDATE " + TABLE_LESSONS + " SET " + COLUMN_FREETIME + " = \'" + nextFreeTime + "\' WHERE " + COLUMN_LESSONNAME + " = \'" + escapeQuote(lessonName) + "\' AND " + COLUMN_CLASS + " = \'" + className + "\';");
        db.close();
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

}

