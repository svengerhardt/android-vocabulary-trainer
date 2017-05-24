package de.trilobytese.vocab.model.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import de.trilobytese.vocab.model.Deck;

import java.util.ArrayList;
import java.util.List;

public class DeckDAOImpl implements DeckDAO {

    public static final String COL_ID = "_id";
    public static final String COL_NAME = "name";
    public static final String COL_TIMESTAMP_CREATED = "timestamp_created";
    public static final String COL_TIMESTAMP_MODIFIED = "timestamp_modified";
    public static final String COL_STARRED = "starred";
    public static final String COL_COUNTER = "counter";
    public static final String COL_ORDER = "deck_order";
    public static final String COL_TAG = "tag";
    public static final String COL_CATEGORY_ID = "_category_id";

    public static final String TABLE_NAME = "deck";

    public static final String CREATE_TABLE_STMT = "CREATE TABLE " + TABLE_NAME + " ("
            + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COL_NAME + " TEXT NOT NULL, "
            + COL_TIMESTAMP_CREATED + " INTEGER DEFAULT 0 NOT NULL, "
            + COL_TIMESTAMP_MODIFIED + " INTEGER DEFAULT 0 NOT NULL, "
            + COL_STARRED + " INTEGER DEFAULT 0 NOT NULL, "
            + COL_COUNTER + " INTEGER DEFAULT 0 NOT NULL, "
            + COL_ORDER + " INTEGER DEFAULT 0 NOT NULL, "
            + COL_TAG + " TEXT, "
            + COL_CATEGORY_ID + " INTEGER NOT NULL, "
            + "FOREIGN KEY (" + COL_CATEGORY_ID + ") REFERENCES "
            + CategoryDAOImpl.TABLE_NAME + " (" + CategoryDAOImpl.COL_ID + ") ON DELETE CASCADE);";

    private String[] PROJECTION = {
            COL_ID, COL_NAME, COL_TIMESTAMP_CREATED, COL_TIMESTAMP_MODIFIED, COL_STARRED, COL_CATEGORY_ID
    };

    @Override
    public List<Deck> findAll() {
        List<Deck> result = new ArrayList<>();

        SQLiteDatabase db = DBAdapter.getInstance().openToRead();
        Cursor cursor = db.query(TABLE_NAME, PROJECTION, null, null, null, null, COL_ID + " DESC");

        if (cursor != null) {
            if  (cursor.moveToFirst()) {
                do {
                    result.add(makeObject(cursor));
                } while (cursor.moveToNext());
            }

            cursor.close();
        }

        DBAdapter.getInstance().closeDatabase();
        return result;
    }

    @Override
    public Deck get(long deckId) {

        SQLiteDatabase db = DBAdapter.getInstance().openToRead();
        Cursor cursor = db.query(TABLE_NAME, PROJECTION, COL_ID + "=" + deckId, null, null, null, null);

        if (cursor == null) { return null; }

        try {
            if(!cursor.moveToFirst()) { return null; }
            return makeObject(cursor);
        } finally {
            cursor.close();
            DBAdapter.getInstance().closeDatabase();
        }
    }

    @Override
    public List<Deck> getByCategoryId(long categoryId) {
        List<Deck> result = new ArrayList<>();
        SQLiteDatabase db = DBAdapter.getInstance().openToRead();
        Cursor cursor = db.query(TABLE_NAME, PROJECTION, COL_CATEGORY_ID + "=" + categoryId, null, null, null, COL_ID + " DESC");

        if (cursor != null) {
            if  (cursor.moveToFirst()) {
                do {
                    result.add(makeObject(cursor));
                } while (cursor.moveToNext());
            }

            cursor.close();
        }

        DBAdapter.getInstance().closeDatabase();
        return result;
    }

    @Override
    public List<Deck> getStarred() {
        List<Deck> result = new ArrayList<>();
        SQLiteDatabase db = DBAdapter.getInstance().openToRead();
        Cursor cursor = db.query(TABLE_NAME, PROJECTION, COL_STARRED + ">0", null, null, null, null);
        if (cursor != null) {
            if  (cursor.moveToFirst()) {
                do {
                    result.add(makeObject(cursor));
                } while (cursor.moveToNext());
            }

            cursor.close();
        }

        DBAdapter.getInstance().closeDatabase();
        return result;
    }

    @Override
    public boolean persist(Deck toPersist) {

        if (toPersist == null) return false;

        SQLiteDatabase db = DBAdapter.getInstance().openToWrite();

        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_NAME, toPersist.getName());
        contentValues.put(COL_TIMESTAMP_CREATED, System.currentTimeMillis());
        contentValues.put(COL_TIMESTAMP_MODIFIED, System.currentTimeMillis());
        contentValues.put(COL_STARRED, toPersist.isStarred());
        contentValues.put(COL_CATEGORY_ID, toPersist.getCategoryId());

        long id = db.insert(TABLE_NAME, null, contentValues);
        DBAdapter.getInstance().closeDatabase();

        if(id == -1) {
            return false;
        }

        toPersist.setId(id);
        return true;
    }

    @Override
    public boolean update(Deck toUpdate) {
        if (toUpdate == null) return false;
        SQLiteDatabase db = DBAdapter.getInstance().openToWrite();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_NAME, toUpdate.getName());
        contentValues.put(COL_TIMESTAMP_MODIFIED, toUpdate.getTimestampModified());
        contentValues.put(COL_STARRED, toUpdate.isStarred());
        int affected = db.update(TABLE_NAME, contentValues, COL_ID + " = " + String.valueOf(toUpdate.getId()), null);
        DBAdapter.getInstance().closeDatabase();
        return (affected != 0);
    }

    @Override
    public boolean delete(Deck toDelete) {
        if (toDelete == null) return false;
        SQLiteDatabase db = DBAdapter.getInstance().openToWrite();
        int affected = db.delete(TABLE_NAME, COL_ID + " = " + String.valueOf(toDelete.getId()), null);
        DBAdapter.getInstance().closeDatabase();
        return (affected != 0);
    }

    private Deck makeObject(Cursor cursor) {
        Deck object = new Deck();
        object.setId(cursor.getInt(cursor.getColumnIndex(COL_ID)));
        object.setName(cursor.getString(cursor.getColumnIndex(COL_NAME)));
        object.setTimestampCreated(cursor.getLong(cursor.getColumnIndex(COL_TIMESTAMP_CREATED)));
        object.setTimestampModified(cursor.getLong(cursor.getColumnIndex(COL_TIMESTAMP_MODIFIED)));
        object.setStarred(cursor.getInt(cursor.getColumnIndex(COL_STARRED)) > 0);
        object.setCategoryId(cursor.getLong(cursor.getColumnIndex(COL_CATEGORY_ID)));
        return object;
    }
}
