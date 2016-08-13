package net.biospherecorp.umorse;


import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.NumberPicker;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;
import static net.biospherecorp.umorse.MainActivity.delayTime;

class SettingsAlert {

	SettingsAlert(MainActivity activity) {
		_show(activity);
	}

	// Displays the Settings AlertDialog
	private void _show(final MainActivity activity){

		// create the AlertDialog builder
		final AlertDialog.Builder adb = new AlertDialog.Builder(activity);

		// set the title and message
		adb.setTitle(R.string.settings_title);

		// create the numberPicker
		final NumberPicker np = new NumberPicker(activity);

		// to make sure the soft keyboard doesn't pop up
		np.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);

		// create the array whose going to hold the values for the nu
		final String[] tmpValues = new String[MainActivity.Delays.values().length];

		for (MainActivity.Delays value : MainActivity.Delays.values()){
			tmpValues[value.ordinal()] = activity.getResources().getString(value.name);
		}

		// set the values to display (values set)
		np.setDisplayedValues(tmpValues);

		// set the min index in the Wheel
		np.setMinValue(0);
		// set the max index in the Wheel
		np.setMaxValue(MainActivity.Delays.values().length - 1);

		// set the saved position from the SharedPreferences
		np.setValue(activity.loadDelayPositionFromSharedPreferences());

		// set the wheel to NOT go around indefinitely
		np.setWrapSelectorWheel(false);

		// create the layout for the AlertDialog
		final FrameLayout parent = new FrameLayout(activity);

		// add the number picker to this layout
		parent.addView(np, new FrameLayout.LayoutParams(
				FrameLayout.LayoutParams.WRAP_CONTENT,
				FrameLayout.LayoutParams.WRAP_CONTENT,
				Gravity.CENTER));

		// set the layout in the AlertDialog
		adb.setView(parent);

		// set the onClickListener for the "Set" button
		adb.setPositiveButton(R.string.settings_set_button, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialogInterface, int i) {

				int positionInWheel = np.getValue();

				// Save the position to the SharedPreferences
				_saveDelayPositionToSharedPreferences(activity, positionInWheel);

				// update the delayTime
				delayTime = MainActivity.Delays.values()[positionInWheel].speed;

				dialogInterface.dismiss();
			}
		});

		// set the onClickListener for the "Cancel" button
		adb.setNeutralButton(R.string.cancel, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int i) {

				dialog.dismiss();
			}
		});

		// _show the AlertDialog
		adb.show();
	}

	// save the "delay" position (from the wheel) in the default SharedPreferences
	//
	private void _saveDelayPositionToSharedPreferences(MainActivity activity, int value){
		SharedPreferences.Editor editor = getDefaultSharedPreferences(activity).edit();
		editor.putInt("delay", value);
		editor.apply();
	}
}
