package net.biospherecorp.umorse;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

public class MainActivity extends AppCompatActivity{

	//private TextToMorseFragment ttmFragment;

	// set by the settings and used
	// by the SendMorseTask AsyncTask
	static int delayTime;

	// Enum holding the name and values for the emitting speed
	// used in the settings
	enum Delays{
		VERY_FAST(R.string.emitting_speed_very_fast ,300),
		FAST(R.string.emitting_speed_fast,600),
		NORMAL(R.string.emitting_speed_normal, 900),
		SLOW(R.string.emitting_speed_slow, 1200),
		VERY_SLOW(R.string.emitting_speed_very_slow, 1500);

		int name;
		int speed;
		Delays(int stringId, int speedInMs){
			this.name = stringId;
			this.speed = speedInMs;
		}
	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setTitle(R.string.app_name);

		// get the "default" speed from SharedPreferences
		delayTime = Delays.values()[loadDelayPositionFromSharedPreferences()].speed;

		// create and display the default fragment (ttm)
		TextToMorseFragment ttmFragment = new TextToMorseFragment();
		//
		getFragmentManager().beginTransaction()
				.replace(android.R.id.content, ttmFragment, "ttm")
				.commit();
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		getMenuInflater().inflate(R.menu.main_options, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch(item.getItemId()){
			case R.id.ttm_menu:

				// show the Text to Morse Fragment
				TextToMorseFragment ttmFragment = new TextToMorseFragment();

					getFragmentManager().beginTransaction()
							.replace(android.R.id.content, ttmFragment, "ttm")
							.commit();
				break;

			case R.id.mtt_menu:

				// show the Morse to Text Fragment
				MorseToTextFragment mttFragment = new MorseToTextFragment();

				getFragmentManager().beginTransaction()
						.replace(android.R.id.content, mttFragment)
						.commit();
				break;

			case R.id.settings_menu:

				// show the settings AlertDialog
				new SettingsAlert(this);
				break;
		}

		return super.onOptionsItemSelected(item);
	}



	// load the "delay" position (in the Enum) from the default SharedPreferences
	int loadDelayPositionFromSharedPreferences(){
		SharedPreferences sharedPreferences = getDefaultSharedPreferences(this);
		return sharedPreferences.getInt("delay", 0);
	}
}
