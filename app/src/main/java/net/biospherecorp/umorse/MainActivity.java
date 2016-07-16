package net.biospherecorp.umorse;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.NumberPicker;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

public class MainActivity extends AppCompatActivity{

	private TextToMorseFragment ttmFragment;

	static int delayTime;

	// Array holding the values to be displayed
	// in the settings AlertDialog/NumberPicker
	private final static String[] DELAYS = {"600","700","800","900","1000", "1100", "1200", "1300", "1400", "1500"};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		delayTime = Integer.valueOf(DELAYS[loadDelayPositionFromSharedPreferences()]);
		ttmFragment = new TextToMorseFragment();

		getFragmentManager().beginTransaction()
				.replace(android.R.id.content, ttmFragment)
				.commit();
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//return super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.main_options, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		// hide the keyboard when menu is clicked
		hideSoftKeyboard();

		switch(item.getItemId()){
			case R.id.ttm_menu:

				ttmFragment = new TextToMorseFragment();

					getFragmentManager().beginTransaction()
							.replace(android.R.id.content, ttmFragment)
							.commit();
				break;

			case R.id.mtt_menu:

				//MorseToTextFragment mttFragment = new MorseToTextFragment();
				MorseToTextFragment mttFragment = new MorseToTextFragment();

				getFragmentManager().beginTransaction()
						.replace(android.R.id.content, mttFragment)
						.commit();
				break;

			case R.id.settings_menu:

				// show the settings AlertDialog
				show();
				break;
		}

		return super.onOptionsItemSelected(item);
	}

	// Displays the Settings AlertDialog
	private void show(){

		// create the AlertDialog builder
		final AlertDialog.Builder adb = new AlertDialog.Builder(this);

		// set the title and message
		adb.setTitle(R.string.settings_title);
		adb.setMessage(R.string.settings_message);

		// create the numberPicker
		final NumberPicker np = new NumberPicker(this);

		// set the values to display (values set)
		np.setDisplayedValues(DELAYS);

		// set the min value position in the values set
		np.setMinValue(0);
		// set the max value position in the values set
		np.setMaxValue(9);

		// set the default value position in the values set
		np.setValue(loadDelayPositionFromSharedPreferences());
		// set the wheel to NOT go around indefinitely
		np.setWrapSelectorWheel(false);

		// create the layout for the AlertDialog
		final FrameLayout parent = new FrameLayout(this);

		// add the number picker to this layout
		parent.addView(np, new FrameLayout.LayoutParams(
				FrameLayout.LayoutParams.WRAP_CONTENT,
				FrameLayout.LayoutParams.WRAP_CONTENT,
				Gravity.CENTER));

		// set the layout in the AlertDialog
		adb.setView(parent);

		// set the onClickListener for the "Set" button
		adb.setNeutralButton(R.string.settings_set_button, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialogInterface, int i) {

				int position = np.getValue();

				saveDelayPositionToSharedPreferences(position);
				delayTime = Integer.valueOf(DELAYS[position]);

				dialogInterface.dismiss();
			}
		});

		// set the onClickListener for the "Cancel" button
		adb.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialogInterface, int i) {

				dialogInterface.dismiss();
			}
		});

		// show the AlertDialog
		adb.show();
	}

	// save the "delay" value in the default SharedPreferences
	private void saveDelayPositionToSharedPreferences(int value){
		SharedPreferences.Editor editor = getDefaultSharedPreferences(getApplicationContext()).edit();
		editor.putInt("delay", value);
		editor.apply();
	}

	// load the "delay" value from the SharedPreferences
	private int loadDelayPositionFromSharedPreferences(){
		SharedPreferences sharedPreferences = getDefaultSharedPreferences(getApplicationContext());
		return sharedPreferences.getInt("delay", 0);
	}

	// hide the soft keyboard
	//
	// called when the done button (from the TTM fragment) is pressed
	// or when the menu is showing
	void hideSoftKeyboard() {
		InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);

		if (getCurrentFocus() != null){
			inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
		}
	}

	// get the textToMorseFragment
	//
	// called by SendMorseTask AsyncTask and by SimpleCamera
	// to reset the button when task is finished or canceled
	TextToMorseFragment getTtmFragment() {
		return ttmFragment;
	}
}
