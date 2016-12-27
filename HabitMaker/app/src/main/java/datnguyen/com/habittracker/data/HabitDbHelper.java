package datnguyen.com.habittracker.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;

import datnguyen.com.habittracker.data.HabitContract.*;

/**
 * Created by datnguyen on 12/27/16.
 */

public class HabitDbHelper extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 1; // first version
	private static final String DATABASE_NAME = "habit.db";
	public static final int INSERTION_FAIL_CODE = -1;

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
				HabitEntry.COLUMN_COMPLETED_DATE + " INTEGER" +
				" );";

		Log.v("", "SQL_CREATE_HABIT_TABLE: " + SQL_CREATE_HABIT_TABLE);
		// execute query to create habit table
		sqLiteDatabase.execSQL(SQL_CREATE_HABIT_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

		// drop table to create a new one
		sqLiteDatabase.execSQL("DROP TABLE IF EXISTS ");
		onCreate(sqLiteDatabase);
	}

	/**
	 * Insert a habit to database
	 * @param habit object to insert
	 * @return rowId of newly inserted habit, or -1 if insertion fails.
	 */
	public long insertHabit(Habit habit) {
		long insertResult = INSERTION_FAIL_CODE;

		SQLiteDatabase database = getWritableDatabase();
		// wrap insertion in transaction
		database.beginTransaction();

		try {
			// insert query
			ContentValues values = new ContentValues();

			// put info to content values
			values.put(HabitEntry.COLUMN_NAME, habit.getName());
			values.put(HabitEntry.COLUMN_IS_COMPLETED, habit.isCompleted());
			values.put(HabitEntry.COLUMN_REMIND_TIME, habit.getRemindTime());
			values.put(HabitEntry.COLUMN_REPEAT_MODE, habit.getRepeatMode());

			if (habit.getCreatedDate() != null) {
				values.put(HabitEntry.COLUMN_CREATED_DATE, habit.getCreatedDate().getTime());
			}

			if (habit.getCompletedDate() != null) {
				values.put(HabitEntry.COLUMN_COMPLETED_DATE, habit.getCompletedDate().getTime());
			}

			// run the insert statement
			insertResult = database.insert(HabitEntry.TABLE_NAME, null, values);
			database.setTransactionSuccessful();

		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			// end transaction
			database.endTransaction();
		}

		return insertResult;
	}

	/**
	 * Delete all habits in database
	 * @return rowId of newly deleted habit, or -1 if deletion fails.
	 */
	public long deleteAllHabits() {
		SQLiteDatabase database = getWritableDatabase();

		long countRowsAffected = 0;
		// wrap deletion in transaction
		database.beginTransaction();
		try {
			// run the delete statement
			countRowsAffected = database.delete(HabitEntry.TABLE_NAME, null, null);
			database.setTransactionSuccessful();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			// end transaction
			database.endTransaction();
		}

		return countRowsAffected;
	}

	/**
	 * Fetch habit entries from database
	 * @param selection WHERE condition, e.g. "NAME LIKE ?"
	 * @param selectionArgs values to pass into selection
	 * @param sortOrder sortOrder to sort result records
	 * @return list of sorted Habits from database matching conditions
	 */
	public ArrayList<Habit> getAllHabits(String selection, String[] selectionArgs, String sortOrder) {

		ArrayList<Habit> results = new ArrayList();

		SQLiteDatabase database = getReadableDatabase();
		// a projection specifies which columns from database will be querying
		String[] projection = {
				HabitEntry._ID,
				HabitEntry.COLUMN_NAME,
				HabitEntry.COLUMN_IS_COMPLETED,
				HabitEntry.COLUMN_REMIND_TIME,
				HabitEntry.COLUMN_REPEAT_MODE,
				HabitEntry.COLUMN_REPEAT_MODE_VALUE,
				HabitEntry.COLUMN_CREATED_DATE,
				HabitEntry.COLUMN_COMPLETED_DATE
		};

		Cursor cursor = database.query(HabitEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
		while(cursor.moveToNext()) {

			// get info out of cursor
			long id = cursor.getLong(cursor.getColumnIndex(HabitEntry._ID));
			String name = cursor.getString(cursor.getColumnIndex(HabitEntry.COLUMN_NAME));
			boolean isCompleted = cursor.getInt(cursor.getColumnIndex(HabitEntry.COLUMN_IS_COMPLETED)) == 0 ? false : true;
			int remindTime = cursor.getInt(cursor.getColumnIndex(HabitEntry.COLUMN_REMIND_TIME));
			int repeatMode = cursor.getInt(cursor.getColumnIndex(HabitEntry.COLUMN_REPEAT_MODE));
			int repeatModeValue = cursor.getInt(cursor.getColumnIndex(HabitEntry.COLUMN_REPEAT_MODE_VALUE));
			long createdDate = cursor.getLong(cursor.getColumnIndex(HabitEntry.COLUMN_CREATED_DATE));
			long completedDate = cursor.getLong(cursor.getColumnIndex(HabitEntry.COLUMN_COMPLETED_DATE));

			Habit habit = new Habit();
			habit.setId(id);
			habit.setName(name);
			habit.setCompleted(isCompleted);
			habit.setRemindTime(remindTime);
			habit.setRepeatMode(repeatMode);
			habit.setRepeatModeValue(repeatModeValue);
			habit.setCreatedDate(new Date(createdDate));
			habit.setCompletedDate(new Date(completedDate));

			results.add(habit);
		}

		cursor.close();
		return results;
	}

	/**
	 * Fetch habit entries from database
	 * @return list of all Habit entries from database
	 */
	public ArrayList<Habit> getAllHabits() {
		return getAllHabits(null, null, null);
	}

	/**
	 * Fetch habit entries from database matching name starting with provided string
	 * @return list of sorted Habits from database matching conditions
	 */
	public ArrayList<Habit> getHabitsNameStartWith(String prefix) {
		// filter result WHERE name starts with T
		String selection = HabitEntry.COLUMN_NAME + " LIKE ?";
		String[] selectionArgs = { prefix + "%" };

		String sortOrder = HabitEntry.COLUMN_NAME + " ASC";

		return getAllHabits(selection, selectionArgs, sortOrder);
	}

}
