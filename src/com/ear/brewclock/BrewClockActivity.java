package com.ear.brewclock;

import com.ear.brewclock.R;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;

public class BrewClockActivity extends Activity implements OnClickListener, OnItemSelectedListener {
	/** Properties **/
	protected Button brewAddTime;
	protected Button brewDecreaseTime;
	protected Button startBrew;
	protected TextView brewCountLabel;
	protected TextView brewTimeLabel;

	protected int brewTime = 3;
	protected CountDownTimer brewCountDownTimer;
	protected int brewCount = 0;
	protected boolean isBrewing = false;
	protected Spinner teaSpinner;
	protected TeaData teaData;
	protected static final String SHARED_PREFS_NAME = "brew_count_preferences";
	protected static final String BREW_COUNT_SHARED_PREF = "brew_count";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// Connect interface elements to properties
		brewAddTime = (Button) findViewById(R.id.brew_time_up);
		brewDecreaseTime = (Button) findViewById(R.id.brew_time_down);
		startBrew = (Button) findViewById(R.id.brew_start);
		brewCountLabel = (TextView) findViewById(R.id.brew_count_label);
		brewTimeLabel = (TextView) findViewById(R.id.brew_time);

		// Setup ClickListeners
		brewAddTime.setOnClickListener(this);
		brewDecreaseTime.setOnClickListener(this);
		startBrew.setOnClickListener(this);

		// Set the initial brew values
		setBrewCount(0);
		setBrewTime(3);
		teaData = new TeaData(this);
		teaSpinner = (Spinner) findViewById(R.id.tea_spinner);
		if(teaData.count() == 0) {
			teaData.insert("Earl Grey", 3);
			teaData.insert("Assam", 3);
			teaData.insert("Jasmine Green", 1);
			teaData.insert("Darjeeling", 2);
		}
		Cursor cursor = teaData.all(this);

		SimpleCursorAdapter teaCursorAdapter = new SimpleCursorAdapter(
				this,
				android.R.layout.simple_spinner_item,
				cursor,
				new String[] { TeaData.NAME },
				new int[] { android.R.id.text1 }
				);

		teaSpinner.setAdapter(teaCursorAdapter);
		teaCursorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		teaSpinner.setOnItemSelectedListener(this);

	}

	/** Methods **/

	/**
	 * Set an absolute value for the number of minutes to brew. Has no effect if a brew
	 * is currently running.
	 * @param minutes The number of minutes to brew.
	 */
	public void onCreate() {
		  // ��
		 
		  // Set the initial brew values
		  SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE);
		  brewCount = sharedPreferences.getInt(BREW_COUNT_SHARED_PREF, 0);
		  setBrewCount(brewCount);
		 
		  // ��
		}
	public void setBrewTime(int minutes) {
		if(isBrewing)
			return;

		brewTime = minutes;

		if(brewTime < 1)
			brewTime = 1;

		brewTimeLabel.setText(String.valueOf(brewTime) + "m");
	}

	/**
	 * Set the number of brews that have been made, and update the interface. 
	 * @param count The new number of brews
	 */
	public void setBrewCount(int count) {
		brewCount = count;
		brewCountLabel.setText(String.valueOf(brewCount));
		
		// Update the brewCount and write the value to the shared preferences.
		   SharedPreferences.Editor editor = getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE).edit();
		   editor.putInt(BREW_COUNT_SHARED_PREF, brewCount);
		   editor.commit();
		
	}

	/**
	 * Start the brew timer
	 */
	public void startBrew() {
		// Create a new CountDownTimer to track the brew time
		brewCountDownTimer = new CountDownTimer(brewTime * 60 * 1000, 1000) {
			@Override
			public void onTick(long millisUntilFinished) {
				brewTimeLabel.setText(String.valueOf(millisUntilFinished / 1000) + "s");
			}

			@Override
			public void onFinish() {
				isBrewing = false;
				setBrewCount(brewCount + 1);

				brewTimeLabel.setText("Brew Up!");
				startBrew.setText("Start");
			}
		};

		brewCountDownTimer.start();
		startBrew.setText("Stop");
		isBrewing = true;
	}

	/**
	 * Stop the brew timer
	 */
	public void stopBrew() {
		if(brewCountDownTimer != null)
			brewCountDownTimer.cancel();

		isBrewing = false;
		startBrew.setText("Start");
	}

	/** Interface Implementations **/
	/* (non-Javadoc)
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	public void onClick(View v) {
		if(v == brewAddTime)
			setBrewTime(brewTime + 1);
		else if(v == brewDecreaseTime)
			setBrewTime(brewTime -1);
		else if(v == startBrew) {
			if(isBrewing)
				stopBrew();
			else
				startBrew();
		}
	}
	public void onItemSelected(AdapterView<?> spinner, View view, int position, long id) {
		if(spinner == teaSpinner) {
			// Update the brew time with the selected tea��s brewtime
			Cursor cursor = (Cursor) spinner.getSelectedItem();
			setBrewTime(cursor.getInt(2));
		}
	}

	public void onNothingSelected(AdapterView<?> adapterView) {
		// Do nothing
	}
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);

		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case R.id.add_tea:
			Intent intent = new Intent(this, AddTeaActivity.class);
			startActivity(intent);
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}

	}

}
