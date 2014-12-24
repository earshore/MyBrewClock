package com.ear.brewclock;

import java.io.IOException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.view.KeyEvent;
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
import android.widget.Toast;

import com.ear.sysapplication.SysApplicationActivity;

@SuppressWarnings("deprecation")
public class BrewClockActivity extends Activity implements OnClickListener,
		OnItemSelectedListener {
	/** Properties **/
	protected Button brewAddTime;
	protected Button brewDecreaseTime;
	protected Button startBrew;
	protected TextView brewCountLabel;
	protected TextView brewTimeLabel;
	protected String teaName;

	protected boolean isExit; // 定义一个变量，用于标识是否退出
	protected int brewTime = 3;
	protected CountDownTimer brewCountDownTimer;
	protected int brewCount = 0;
	protected boolean isBrewing = false;
	protected Spinner teaSpinner;
	protected TeaData teaData;
	protected static final String SHARED_PREFS_NAME = "brew_count_preferences";
	protected static final String BREW_COUNT_SHARED_PREF = "brew_count";
	MediaPlayer mediaPlayer = new MediaPlayer();// 这个我定义了一个成员函数

	/** Called when the activity is first created. */

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		SysApplicationActivity.getInstance().addActivity(this);

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
		if (teaData.count() == 0) {
			teaData.insert("Earl Grey", 3);
			teaData.insert("Assam", 3);
			teaData.insert("Jasmine Green", 1);
			teaData.insert("Darjeeling", 2);
		}
		Cursor cursor = teaData.all(this);

		SimpleCursorAdapter teaCursorAdapter = new SimpleCursorAdapter(this,
				android.R.layout.simple_spinner_item, cursor,
				new String[] { TeaData.NAME }, new int[] { android.R.id.text1 });

		teaSpinner.setAdapter(teaCursorAdapter);
		teaCursorAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		teaSpinner.setOnItemSelectedListener(this);

		// getActionBar().setHomeButtonEnabled(true);

		TextView textView = new TextView(this);
		this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
		mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
		mediaPlayer
				.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
					@Override
					public void onCompletion(MediaPlayer player) {
						player.seekTo(0);
					}
				});
		AssetFileDescriptor file = this.getResources().openRawResourceFd(
				R.raw.beep);
		try {
			mediaPlayer.setDataSource(file.getFileDescriptor(),
					file.getStartOffset(), file.getLength());
			file.close();
			// mediaPlayer.setVolume(BEEP_VOLUME,BEEP_VOLUME);
			mediaPlayer.prepare();
		} catch (IOException ioe) {
			textView.setText("Couldn't load music file, " + ioe.getMessage());
			// Log.w(TAG, ioe);
			mediaPlayer = null;
		}
	}

	/** Methods **/

	/**
	 * Set an absolute value for the number of minutes to brew. Has no effect if
	 * a brew is currently running.
	 * 
	 * @param minutes
	 *            The number of minutes to brew.
	 */
	public void onCreate() {
		// …

		// Set the initial brew values
		SharedPreferences sharedPreferences = getSharedPreferences(
				SHARED_PREFS_NAME, MODE_PRIVATE);
		brewCount = sharedPreferences.getInt(BREW_COUNT_SHARED_PREF, 0);
		setBrewCount(brewCount);
		// getActionBar().setDisplayHomeAsUpEnabled(true); //
		// ActionBar添加返回上个Activity
		// …
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) { // 重写Activity中onKeyDown方法
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			exit();
			return false;
		} else {
			return super.onKeyDown(keyCode, event);
		}
	}

	public void exit() { // 写一个退出方法，名称就是onKeyDown中的exit()
		if (!isExit) {
			isExit = true;
			Toast.makeText(getApplicationContext(), "Press again to exit.",
					Toast.LENGTH_SHORT).show();
			mHandler.sendEmptyMessageDelayed(0, 2000);
		} else {
			Intent intent = new Intent(Intent.ACTION_MAIN);
			intent.addCategory(Intent.CATEGORY_HOME);
			startActivity(intent);
			System.exit(0);
		}
	}

	@SuppressLint("HandlerLeak")
	Handler mHandler = new Handler() { // 根据exit()方法中的的消息，写一个Handler
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stu b
			super.handleMessage(msg);
			isExit = false;
		}
	};

	public void setBrewTime(int minutes) {
		if (isBrewing)
			return;
		brewTime = minutes;
		if (brewTime < 1)
			brewTime = 1;
		brewTimeLabel.setText(String.valueOf(brewTime) + "m");
	}

	/**
	 * Set the number of brews that have been made, and update the interface.
	 * 
	 * @param count
	 *            The new number of brews
	 */
	public void setBrewCount(int count) {
		brewCount = count;
		brewCountLabel.setText(String.valueOf(brewCount));

		// Update the brewCount and write the value to the shared preferences.
		SharedPreferences.Editor editor = getSharedPreferences(
				SHARED_PREFS_NAME, MODE_PRIVATE).edit();
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
				brewTimeLabel.setText(String
						.valueOf(millisUntilFinished / 1000) + "s");
			}

			@Override
			public void onFinish() {
				isBrewing = false;
				setBrewCount(brewCount + 1);
				brewTimeLabel.setText("Brew Up!");

				if (mediaPlayer != null) {
					mediaPlayer.start();
				}
				// Vibrator
				Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
				// 1. 振动为1000毫秒，既1秒
				long milliseconds = 1000;
				vibrator.vibrate(milliseconds);
				// 2. 振动模式：手机等待0秒就开始震动1秒，再等待0.8秒，开始震动1秒
				long[] pattern = { 0, 1000, 800, 1000, 800, 1000 }; // OFF/ON/OFF/ON......
				vibrator.vibrate(pattern, -1);

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
		if (brewCountDownTimer != null)
			brewCountDownTimer.cancel();
		isBrewing = false;
		startBrew.setText("Start");
	}

	/** Interface Implementations **/
	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	public void onClick(View v) {
		if (v == brewAddTime)
			setBrewTime(brewTime + 1);
		else if (v == brewDecreaseTime)
			setBrewTime(brewTime - 1);
		else if (v == startBrew) {
			if (isBrewing)
				stopBrew();
			else
				startBrew();
		}
	}

	public void onItemSelected(AdapterView<?> spinner, View view, int position,
			long id) {
		if (spinner == teaSpinner) {
			// Update the brew time with the selected tea’s brewtime
			Cursor cursor = (Cursor) spinner.getSelectedItem();
			setBrewTime(cursor.getInt(2));
			teaName = cursor.getString(1); // 搜索茶叶信息，亮点！！！！
			getActionBar().setTitle(teaName); // actionbar设置标题为茶名
		}
	}

	public void onNothingSelected(AdapterView<?> adapterView) {
		// Do nothing
		getActionBar().setTitle("BrewClock");
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		// Pass the event to ActionBarDrawerToggle, if it returns
		// true, then it has handled the app icon touch event
		switch (item.getItemId()) {
		case R.id.action_search:
			Intent intentas = new Intent(Intent.ACTION_WEB_SEARCH);
			intentas.putExtra(SearchManager.QUERY, teaName + " tea");
			// catch event that there's no activity to handle intent
			if (intentas.resolveActivity(getPackageManager()) != null) {
				startActivity(intentas);
			} else {
				Toast.makeText(this, R.string.app_not_available,
						Toast.LENGTH_LONG).show();
			}
			return true;
		case R.id.add_tea:
			Intent intent = new Intent(this, AddTeaActivity.class);
			startActivity(intent);
			return true;
		case R.id.about:
			AlertDialog.Builder dialog = new AlertDialog.Builder(this);
			dialog.setTitle(R.string.about_label);
			dialog.setMessage(R.string.about_content);
			dialog.show();
			return true;
		case R.id.feedback:
			Intent email = new Intent(android.content.Intent.ACTION_SEND);
			email.setType("plain/text");
			String[] emailReciver = new String[] { "earshore@gmail.com" };
			String emailSubject = "BrewClock Feedback";
			String emailBody = "";
			// 设置邮件默认地址
			email.putExtra(android.content.Intent.EXTRA_EMAIL, emailReciver);
			// 设置邮件默认标题
			email.putExtra(android.content.Intent.EXTRA_SUBJECT, emailSubject);
			// 设置要默认发送的内容
			email.putExtra(android.content.Intent.EXTRA_TEXT, emailBody);
			// 调用系统的邮件系统
			startActivity(Intent.createChooser(email, "Choose a way to email."));
			return true;
		case R.id.exit:
			finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}

	}
}
