package de.trilobytese.vocab.model.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import de.trilobytese.vocab.model.Category;

import java.util.ArrayList;
import java.util.List;

public class CategoryDAOImpl implements CategoryDAO {

    public static final String COL_ID = "_id";
    public static final String COL_NAME = "name";
    public static final String COL_COUNTER = "counter";
    public static final String COL_ORDER = "cat_order";
    public static final String COL_TAG = "tag";

    public static final String TABLE_NAME = "category";

    public static final String CREATE_TABLE_STMT = "CREATE TABLE " + TABLE_NAME + " ("
            + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COL_NAME + " TEXT NOT NULL, "
            + COL_COUNTER + " INTEGER DEFAULT 0 NOT NULL, "
            + COL_ORDER + " INTEGER DEFAULT 0 NOT NULL, "
            + COL_TAG + " TEXT);";

    private String[] PROJECTION = {
            COL_ID, COL_NAME
    };

    @Override
    public List<Category> findAll() {
        List<Category> result = new ArrayList<>();

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
    public Category get(long categoryId) {
        SQLiteDatabase db = DBAdapter.getInstance().openToRead();
        Cursor cursor = db.query(TABLE_NAME, PROJECTION, COL_ID + "=" + categoryId, null, null, null, null);

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
    public boolean persist(Category toPersist) {
        if (toPersist == null) return false;

        SQLiteDatabase db = DBAdapter.getInstance().openToWrite();

        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_NAME, toPersist.getName());

        long id = db.insert(TABLE_NAME, null, contentValues);
        DBAdapter.getInstance().closeDatabase();

        if(id == -1) {
            return false;
        }

        toPersist.setId(id);
        return true;
    }

    @Override
    public boolean delete(Category toDelete) {
        SQLiteDatabase db = DBAdapter.getInstance().openToWrite();
        int affected = db.delete(TABLE_NAME, COL_ID + " = " + String.valueOf(toDelete.getId()), null);
        DBAdapter.getInstance().closeDatabase();
        return (affected != 0);
    }

    private Category makeObject(Cursor cursor) {
        Category object = new Category();
        object.setId(cursor.getInt(cursor.getColumnIndex(COL_ID)));
        object.setName(cursor.getString(cursor.getColumnIndex(COL_NAME)));
        return object;
    }
}
