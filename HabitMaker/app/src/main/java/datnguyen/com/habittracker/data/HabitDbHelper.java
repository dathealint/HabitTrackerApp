package datnguyen.com.habittracker.data;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import datnguyen.com.habittracker.data.HabitContract.*;

/**
 * Created by datnguyen on 12/27/16.
 */

public class HabitDbHelper extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 1; // first version

	private static final String DATABASE_NAME = "habit.db";

	public HabitDbHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase sqLiteDatabase) {
		// create table habit here

		final String SQL_CREATE_HABIT_TABLE = "CREATE TABLE " + HabitEntry.TABLE_NAME + " (" +
				HabitEntry._ID + " INTEGER PRIMARY KEY," +
				HabitEntry.COLUMN_NAME + " TEXT NOT NULL," +
				HabitEntry.COLUMN_IS_COMPLETED + " INTEGER DEFAULT 0," +
				HabitEntry.COLUMN_REMIND_TIME + " INTEGER," +
				HabitEntry.COLUMN_REPEAT_MODE + " INTEGER," +
				HabitEntry.COLUMN_REPEAT_MODE_VALUE + " INTEGER," +
				HabitEntry.COLUMN_CREATED_DATE + " INTEGER," +
				HabitEntry.COLUMN_COMPLETED_DATE + " INTEGER," +
				" );";

		// execute query to create habit table
		sqLiteDatabase.execSQL(SQL_CREATE_HABIT_TABLE);

	}

	@Override
	public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

		// drop table to create a new one
		sqLiteDatabase.execSQL("DROP TABLE IF EXISTS ");
		onCreate(sqLiteDatabase);
	}
}
