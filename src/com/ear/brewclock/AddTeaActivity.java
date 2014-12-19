package com.ear.brewclock;

import com.ear.brewclock.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;



public class AddTeaActivity extends Activity implements OnSeekBarChangeListener {
	protected EditText teaName;
	protected SeekBar brewTimeSeekBar;
	protected TextView brewTimeLabel;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_tea);
		teaName = (EditText) findViewById(R.id.tea_name);
		brewTimeSeekBar = (SeekBar) findViewById(R.id.brew_time_seekbar);
		brewTimeLabel = (TextView) findViewById(R.id.brew_time_value);
		brewTimeSeekBar.setOnSeekBarChangeListener(this);

	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.add_tea, menu);

		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case R.id.save_tea:
			if(saveTea()) {
				Toast.makeText(this, getString(R.string.save_tea_success, 
						teaName.getText().toString()), 
						Toast.LENGTH_SHORT).show();
				teaName.setText("");
			}

		default:
			return super.onOptionsItemSelected(item);
		}
	}
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		// TODO Detect change in progress
		if(seekBar == brewTimeSeekBar) {
			// Update the brew time label with the chosen value.
			brewTimeLabel.setText((progress ) + " m");
		}
	}

	public void onStartTrackingTouch(SeekBar seekBar) {}

	public void onStopTrackingTouch(SeekBar seekBar) {}
	public boolean saveTea() {
		// Read values from the interface
		String teaNameText = teaName.getText().toString();
		int brewTimeValue = brewTimeSeekBar.getProgress();
		
		// Validate a brewTimeValue has been entered for the tea
		if(brewTimeValue <= 0)
		{
			AlertDialog.Builder dialog = new AlertDialog.Builder(this);
			dialog.setTitle(R.string.invalid_tea_title);
			dialog.setMessage(R.string.invalid_brew_Time_Value);  //在res/values目录下的string.xml文件里新建一个项目
			dialog.setPositiveButton("Ok",null);
			dialog.setNegativeButton("Cancel",new OnClickListener(){
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					// TODO Auto-generated method stub
					AddTeaActivity.this.finish();
				}
			}); 
			dialog.show();
			
			return false;
			
		}
		else {}
		
		// Validate a name has been entered for the tea
		if(teaNameText.length() < 2) {
			AlertDialog.Builder dialog = new AlertDialog.Builder(this);
			dialog.setTitle(R.string.invalid_tea_title);
			dialog.setMessage(R.string.invalid_tea_no_name);
			dialog.setPositiveButton("Ok",null); 
			dialog.setNegativeButton("Cancel",new OnClickListener(){
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					// TODO Auto-generated method stub
					AddTeaActivity.this.finish();
				}
			}); 
			dialog.show();

			return false;
		}
		TeaData teaData = new TeaData(this);
		teaData.insert(teaNameText, brewTimeValue);
		teaData.close();

		return true;
	}
}
