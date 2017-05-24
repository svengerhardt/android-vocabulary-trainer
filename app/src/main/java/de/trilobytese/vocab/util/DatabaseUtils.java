package de.trilobytese.vocab.util;

import android.content.Context;
import de.trilobytese.vocab.Constants;

import java.io.File;

public final class DatabaseUtils {
	
	public static boolean databaseExists(Context context) {
		File dbFile = new File(getDatabasePath(context));
		return dbFile.exists();
	}
	
	public static String getDatabasePath(Context context) {
		return context.getFilesDir().getParentFile().getAbsolutePath() 
			+ "/databases/" + Constants.DATABASE_NAME;
	}
}
