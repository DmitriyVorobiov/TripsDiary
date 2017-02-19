package org.vorobjev.tripsdiary.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.AndroidConnectionSource;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

/**
 * Created by pc on 24.12.2016.
 */
public class TripsDiaryDatabaseHelper extends OrmLiteSqliteOpenHelper {

    public static final String DATABASE_NAME = "td_db";
    public static final int DATABASE_VERSION = 1;

    Class[] TABLES = new Class[]{
            TripEntity.class
    };

    public TripsDiaryDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
        createTables(new AndroidConnectionSource(db));
    }

    public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        ConnectionSource source = new AndroidConnectionSource(db);
        try {
            for (Class table : TABLES) {
                TableUtils.dropTable(source, table, true);
            }
        } catch (SQLException e) {
        }
        createTables(source);
    }

    private void createTables(ConnectionSource source) {
        try {
            for (Class table : TABLES) {
                TableUtils.createTable(source, table);
            }
        } catch (Exception e) {
        }
    }
}
