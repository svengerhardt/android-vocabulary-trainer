package de.trilobytese.vocab.model.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import de.trilobytese.vocab.model.Flashcard;

import java.util.ArrayList;
import java.util.List;

public class FlashcardDAOImpl implements FlashcardDAO {

    public static final String COL_ID = "_id";
    public static final String COL_SOURCE = "source";
    public static final String COL_TARGET = "target";
    public static final String COL_COUNTER_CORRECT = "counter_correct";
    public static final String COL_COUNTER_WRONG = "counter_wrong";
    public static final String COL_TAG = "tag";
    public static final String COL_DECK_ID = "_deck_id";

    public static final String TABLE_NAME = "flashcard";

    public static final String CREATE_TABLE_STMT = "CREATE TABLE " + TABLE_NAME + " ("
            + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COL_SOURCE + " TEXT DEFAULT '', "
            + COL_TARGET + " TEXT DEFAULT '', "
            + COL_COUNTER_CORRECT + " INTEGER DEFAULT 0 NOT NULL, "
            + COL_COUNTER_WRONG + " INTEGER DEFAULT 0 NOT NULL, "
            + COL_TAG + " TEXT, "
            + COL_DECK_ID + " INTEGER NOT NULL, "
            + "FOREIGN KEY (" + COL_DECK_ID + ") REFERENCES "
            + DeckDAOImpl.TABLE_NAME + " (" + DeckDAOImpl.COL_ID + ") ON DELETE CASCADE);";

    private String[] PROJECTION = {
            COL_ID,
            COL_SOURCE,
            COL_TARGET,
            COL_DECK_ID
    };

    @Override
    public List<Flashcard> findAll() {
        List<Flashcard> result = new ArrayList<>();
        SQLiteDatabase db = DBAdapter.getInstance().openToRead();
        Cursor cursor = db.query(TABLE_NAME, PROJECTION, null, null, null, null, null);

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
    public List<Flashcard> getByDeckId(long deckId) {
        List<Flashcard> result = new ArrayList<>();
        SQLiteDatabase db = DBAdapter.getInstance().openToRead();
        Cursor cursor = db.query(TABLE_NAME, PROJECTION, COL_DECK_ID + "=" + deckId, null, null, null, COL_ID + " ASC");

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
    public List<Flashcard> getByDeckIdNotEmpty(long deckId) {
        List<Flashcard> result = new ArrayList<>();
        SQLiteDatabase db = DBAdapter.getInstance().openToRead();
        Cursor cursor = db.query(TABLE_NAME, PROJECTION, COL_DECK_ID + "=" + deckId +
            " AND (" + COL_SOURCE + " <> '') AND (" + COL_TARGET + " <> '')", null, null, null, COL_ID + " ASC");

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
    public boolean persist(Flashcard toPersist) {

        if (toPersist == null) return false;

        SQLiteDatabase db = DBAdapter.getInstance().openToWrite();

        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_SOURCE, toPersist.getSource());
        contentValues.put(COL_TARGET, toPersist.getTarget());
        contentValues.put(COL_DECK_ID, toPersist.getDeckId());

        long id = db.insert(TABLE_NAME, null, contentValues);
        DBAdapter.getInstance().closeDatabase();

        if(id == -1) {
            return false;
        }

        toPersist.setId(id);
        return true;
    }

    @Override
    public boolean update(Flashcard toUpdate) {
        SQLiteDatabase db = DBAdapter.getInstance().openToWrite();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_SOURCE, toUpdate.getSource());
        contentValues.put(COL_TARGET, toUpdate.getTarget());
        int affected = db.update(TABLE_NAME, contentValues, COL_ID + " = " + String.valueOf(toUpdate.getId()), null);
        DBAdapter.getInstance().closeDatabase();
        return (affected != 0);
    }

    @Override
    public boolean delete(Flashcard toDelete) {
        SQLiteDatabase db = DBAdapter.getInstance().openToWrite();
        int affected = db.delete(TABLE_NAME, COL_ID + " = " + String.valueOf(toDelete.getId()), null);
        DBAdapter.getInstance().closeDatabase();
        return (affected != 0);
    }

    @Override
    public boolean deleteAllByDeckId(long deckId) {
        SQLiteDatabase db = DBAdapter.getInstance().openToWrite();
        int affected = db.delete(TABLE_NAME, COL_DECK_ID + " = " + String.valueOf(deckId), null);
        DBAdapter.getInstance().closeDatabase();
        return (affected != 0);
    }

    @Override
    public void insertEmptyRows(long deckId, int count) {
        SQLiteDatabase db = DBAdapter.getInstance().openToWrite();

        for (int i = 0; i < count; i++) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(COL_DECK_ID, deckId);
            db.insert(TABLE_NAME, null, contentValues);
        }
        DBAdapter.getInstance().closeDatabase();
    }

    @Override
    public void insert(List<Flashcard> items) {
        SQLiteDatabase db = DBAdapter.getInstance().openToWrite();
        for (Flashcard item : items) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(COL_DECK_ID, item.getDeckId());
            contentValues.put(COL_SOURCE, item.getSource());
            contentValues.put(COL_TARGET, item.getTarget());
            db.insert(TABLE_NAME, null, contentValues);
        }
        DBAdapter.getInstance().closeDatabase();
    }

    public void insert(long deckId, String[][] items) {
        SQLiteDatabase db = DBAdapter.getInstance().openToWrite();
        for (String[] item : items) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(COL_DECK_ID, deckId);
            contentValues.put(COL_SOURCE, item[0]);
            contentValues.put(COL_TARGET, item[1]);
            db.insert(TABLE_NAME, null, contentValues);
        }
        DBAdapter.getInstance().closeDatabase();
    }

    @Override
    public int count(long deckId) {
        SQLiteDatabase db = DBAdapter.getInstance().openToRead();
        Cursor cursor = db.rawQuery("SELECT COUNT (*) FROM " + TABLE_NAME + " WHERE " + COL_DECK_ID + "=" + deckId, null);
        int count = 0;
        if(cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                count = cursor.getInt(0);
            }
            cursor.close();
        }

        DBAdapter.getInstance().closeDatabase();
        return count;
    }

    private Flashcard makeObject(Cursor cursor) {
        Flashcard object = new Flashcard();
        object.setId(cursor.getLong(cursor.getColumnIndex(COL_ID)));
        object.setTarget(cursor.getString(cursor.getColumnIndex(COL_TARGET)));
        object.setSource(cursor.getString(cursor.getColumnIndex(COL_SOURCE)));
        object.setDeckId(cursor.getLong(cursor.getColumnIndex(COL_DECK_ID)));
        return object;
    }
}
