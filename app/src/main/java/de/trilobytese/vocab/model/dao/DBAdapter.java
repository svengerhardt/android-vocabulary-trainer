package de.trilobytese.vocab.model.dao;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import de.trilobytese.vocab.Constants;

import java.util.concurrent.atomic.AtomicInteger;

public class DBAdapter {

    private static final String TAG = DBAdapter.class.getSimpleName();

    private AtomicInteger mOpenCounter = new AtomicInteger();

    private static DBAdapter instance;
    private static SQLiteOpenHelper mDatabaseHelper;
    private SQLiteDatabase mDatabase;

    public static synchronized void initializeInstance(Application application) {
        if (instance == null) {
            instance = new DBAdapter();
            mDatabaseHelper = new DatabaseHelper(application);
        }
    }

    public static synchronized DBAdapter getInstance() {
        if (instance == null) {
            throw new IllegalStateException(TAG + " is not initialized, call initializeInstance(..) method first.");
        }
        return instance;
    }

    public synchronized SQLiteDatabase openToRead() {
        if(mOpenCounter.incrementAndGet() == 1) {
            mDatabase = mDatabaseHelper.getReadableDatabase();
        }
        return mDatabase;
    }

    public synchronized SQLiteDatabase openToWrite() {
        if(mOpenCounter.incrementAndGet() == 1) {
            mDatabase = mDatabaseHelper.getWritableDatabase();
        }
        return mDatabase;
    }

    public synchronized void closeDatabase() {
        if(mOpenCounter.decrementAndGet() == 0) {
            mDatabase.close();
        }
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {

        public DatabaseHelper(Application application) {
            super(application, Constants.DATABASE_NAME, null, Constants.DATABASE_VERSION);
        }

        @Override
        public void onOpen(SQLiteDatabase db) {
            super.onOpen(db);
            if (!db.isReadOnly()) {
                // enable foreign key constraints
                db.execSQL("PRAGMA foreign_keys=ON;");
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqlitedatabase, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion);
            sqlitedatabase.execSQL("DROP TABLE IF EXISTS " + CategoryDAOImpl.TABLE_NAME);
            sqlitedatabase.execSQL("DROP TABLE IF EXISTS " + DeckDAOImpl.TABLE_NAME);
            sqlitedatabase.execSQL("DROP TABLE IF EXISTS " + FlashcardDAOImpl.TABLE_NAME);
            onCreate(sqlitedatabase);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.beginTransaction();
            try {
                db.execSQL(CategoryDAOImpl.CREATE_TABLE_STMT);
                db.execSQL(DeckDAOImpl.CREATE_TABLE_STMT);
                db.execSQL(FlashcardDAOImpl.CREATE_TABLE_STMT);
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
        }
    }
}
