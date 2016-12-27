package datnguyen.com.habittracker;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;

import datnguyen.com.habittracker.data.Habit;
import datnguyen.com.habittracker.data.HabitDbHelper;

public class MainActivity extends AppCompatActivity {

	private final String TAG = getClass().getSimpleName();
	HabitDbHelper mDbHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mDbHelper = new HabitDbHelper(this);

		// test query database
		ArrayList<Habit> listAllHabits = mDbHelper.getAllHabits();
		Log.v(TAG, "listAllHabits count: " + listAllHabits.size());

		// test insert to db
		{
			Habit habit = new Habit();
			habit.setName("Test habit 1");
			habit.setCompleted(false);
			habit.setRemindTime(1420);
			habit.setRepeatMode(1);// weekly
			habit.setRepeatModeValue(2);// Tuesday
			habit.setCreatedDate(new Date());

			//insert to db
			long insertId = mDbHelper.insertHabit(habit);
			Log.v(TAG, "insertId: " + insertId);
		}

		{
			Habit habit = new Habit();
			habit.setName("Habit 2");
			habit.setCompleted(true);
			habit.setRemindTime(800);
			habit.setRepeatMode(2);// monthly
			habit.setRepeatModeValue(15);// day 15 of month
			habit.setCreatedDate(new Date());
			habit.setCompletedDate(new Date());

			//insert to db
			long insertId = mDbHelper.insertHabit(habit);
			Log.v(TAG, "insertId: " + insertId);
		}

		// test query
		listAllHabits = mDbHelper.getAllHabits();
		Log.v(TAG, "listAllHabits AFTER INSERT count: " + listAllHabits.size());
		for (Habit habit:listAllHabits) {
			Log.v(TAG, "\t\tName: " + habit.getName());
			Log.v(TAG, "\t\tIsCompleted: " + habit.isCompleted());
			Log.v(TAG, "\t\tRepeatMode: " + habit.getRepeatMode());
		}

		// test query select with condition
		listAllHabits = mDbHelper.getHabitsNameStartWith("T");
		Log.v(TAG, "listAllHabits Condition count: " + listAllHabits.size());
		for (Habit habit:listAllHabits) {
			Log.v(TAG, "\t\tName: " + habit.getName());
			Log.v(TAG, "\t\tIsCompleted: " + habit.isCompleted());
			Log.v(TAG, "\t\tRepeatMode: " + habit.getRepeatMode());
		}

		// delete for testing
		mDbHelper.deleteAllHabits();
		listAllHabits = mDbHelper.getAllHabits();
		Log.v(TAG, "listAllHabits AFTER DELETE count: " + listAllHabits.size());

	}

	@Override
	protected void onDestroy() {
		// close database helper, also close database connections if any
		mDbHelper.close();
		super.onDestroy();
	}
}
