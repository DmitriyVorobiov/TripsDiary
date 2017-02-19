package org.vorobjev.tripsdiary;

import android.support.multidex.MultiDexApplication;

import org.vorobjev.tripsdiary.db.TripsDiaryDatabaseHelper;

public class TripsDiaryApplication extends MultiDexApplication {

    static TripsDiaryApplication instance;
    TripsDiaryDatabaseHelper dbHelper;

    public void onCreate() {
        super.onCreate();
        instance = this;
        dbHelper = new TripsDiaryDatabaseHelper(instance);
    }

    public static TripsDiaryApplication getInstance() {
        return instance;
    }

    public TripsDiaryDatabaseHelper getDbHelper() {
        return dbHelper;
    }

    public void setDbHelper(TripsDiaryDatabaseHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

}
