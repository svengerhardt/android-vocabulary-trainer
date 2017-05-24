package de.trilobytese.vocab.util;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

public class ImportUtils {

    public static String getNameFromMediaStore(Context context, Uri uri) {
        String name = null;
        Cursor cursor = context.getContentResolver().query(uri, new String[]{ MediaStore.MediaColumns.DISPLAY_NAME }, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            int nameIndex = cursor.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME);
            if (nameIndex >= 0) {
                name = cursor.getString(nameIndex);
            }
            cursor.close();
        }
        return name;
    }

}
