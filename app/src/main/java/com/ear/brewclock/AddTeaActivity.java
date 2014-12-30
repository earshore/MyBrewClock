package com.ear.brewclock;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.ear.exit.SysApplication;

@SuppressLint("CutPasteId")
public class AddTeaActivity extends Activity implements OnSeekBarChangeListener {
	protected EditText teaName;
	protected SeekBar brewTimeSeekBar;
	protected TextView brewTimeLabel;

	private static final String[] teanames = new String[] { "Tie Guan Yin", "Oolong",
			"Da Hong Pao", "Loose", "Masala chai", "Blooming", "Wu Long", "Pu'er",
			"Tisanes","Black" };
	private AutoCompleteTextView autoCompleteTextView = null;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.add_tea_activity);

		teaName = (EditText) findViewById(R.id.tea_name);
		brewTimeSeekBar = (SeekBar) findViewById(R.id.brew_time_seekbar);
		brewTimeLabel = (TextView) findViewById(R.id.brew_time_value);
		brewTimeSeekBar.setOnSeekBarChangeListener(this);

		autoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.tea_name);

		// ����������
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_dropdown_item_1line, teanames);
		autoCompleteTextView.setAdapter(adapter);
		// ������������ַ����ʾ��Ĭ��ֵΪ2
		autoCompleteTextView.setThreshold(2);
	}

	public void SaveTea(View view) { // Save Buttom
		saveTea();
	}

	public void CancelSaveTea(View view1) { // Cancel Buttom
		AddTeaActivity.this.finish();
	}


	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.add_tea_menu, menu);

		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) { // AddTeaActivityֻ�������Ŀ¼ѡ��
		case R.id.save_tea:
			if (saveTea()) {
				Toast.makeText(
						this,
						getString(R.string.save_tea_success, teaName.getText()
								.toString()), Toast.LENGTH_SHORT).show();
				teaName.setText("");
			}
			return true;
		case R.id.exit:
			SysApplication.getInstance().exit(); // ɱ������activity,�˳�����
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		// TODO Detect change in progress
		if (seekBar == brewTimeSeekBar) {
			// Update the brew time label with the chosen value.
			brewTimeLabel.setText((progress + 1) + " m\n");
		}
	}

	public void onStartTrackingTouch(SeekBar seekBar) {
	}

	public void onStopTrackingTouch(SeekBar seekBar) {
	}

	public boolean saveTea() {
		// Read values from the interface
		String teaNameText = teaName.getText().toString();
		int brewTimeValue = brewTimeSeekBar.getProgress() + 1;

		// Validate a name has been entered for the tea
		if (teaNameText.length() < 2) {
			AlertDialog.Builder dialog = new AlertDialog.Builder(this);
			dialog.setTitle(R.string.invalid_tea_title);
			dialog.setPositiveButton("OK", null);
			dialog.setNegativeButton("Cancel", new OnClickListener() {
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					// TODO Auto-generated method stub
					AddTeaActivity.this.finish();
				}
			});
			dialog.setMessage(R.string.invalid_tea_no_name);
			dialog.show();
			return false;
		}
		// Right issue entered for the tea
		TeaData teaData = new TeaData(this);
		teaData.insert(teaNameText, brewTimeValue);
		teaData.close();
		Toast.makeText(
				this,
				getString(R.string.save_tea_success, teaName.getText()
						.toString()), Toast.LENGTH_SHORT).show();
		teaName.setText("");
		AddTeaActivity.this.finish();
		return true;

	}

}
