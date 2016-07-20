package net.biospherecorp.umorse;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

// The whole point of this tool is to be able
// to deactivate the logs when in production
public abstract class L{


	// change this to active / deactivate this tool
	private static boolean IS_ACTIVATED = false;




	// DEFAULT MESSAGE
	static void m(String message){
		if (IS_ACTIVATED) {
			Log.i(">>>> Message >>>> ", message);
		}
	}

	// DEFAULT MESSAGE (with TAG)
	static void m(String TAG, String message){
		if (IS_ACTIVATED) {
			Log.i(">>>> Message >>>> ", TAG + " : " + message);
		}
	}

	// DEFAULT TOAST--
	static void t(Context context, String message){

		if (IS_ACTIVATED) {
			Toast.makeText(context, message, Toast.LENGTH_LONG).show();
		}
	}

	//DEFAULT TOAST >> DEFAULT MESSAGE
	//
	// Easy way to turn a L.t() into a L.m()
	static void m(Context context, String message){

		// do nothing with context
		m(message);
	}





	// BY COLORS


	// GREEN
	static void green (String message){
		if (IS_ACTIVATED) {
			Log.i(">>> ", message);
		}
	}

	// GREEN
	static void green (String TAG, String message){
		if (IS_ACTIVATED) {
			Log.i(">>> ", TAG + " : " + message);
		}
	}


	// YELLOW
	static void yellow(String message){
		if (IS_ACTIVATED) {
			Log.w(">>>> ", message);
		}
	}

	// YELLOW
	static void yellow(String TAG, String message){
		if (IS_ACTIVATED) {
			Log.w(">>>> ", TAG + " : " + message);
		}
	}


	// RED
	static void red(String message){
		if (IS_ACTIVATED) {
			Log.e(">>>>> ", message);
		}
	}

	// RED
	static void red(String TAG, String message){
		if (IS_ACTIVATED) {
			Log.e(">>>>> ", TAG + " : " + message);
		}
	}


	// BLUE
	static void blue(String message){
		if (IS_ACTIVATED) {
			Log.d(">>>>>> ", message);
		}
	}

	// BLUE
	static void blue(String TAG, String message){
		if (IS_ACTIVATED) {
			Log.d(">>>>>> ", TAG + " : " + message);
		}
	}






	// BY FUNCTION (long version)


	// INFO
	//
	// color green in Android Studio console/monitor
	static void info (String message){
		if (IS_ACTIVATED) {
			Log.i(">> Info msg >> ", message);
		}
	}

	// INFO (to turn a normal Log.i into a L.i easily)
	//
	// color green in Android Studio console/monitor
	static void info (String TAG, String message){
		if (IS_ACTIVATED) {
			Log.i(">> Info msg >> ", TAG + message);
		}
	}


	// WARNING
	//
	// color yellow in Android Studio console/monitor
	static void warning (String message){
		if (IS_ACTIVATED) {
			Log.w(">> Warning msg >> ", message);
		}
	}

	//WARNING (to turn a normal Log.w into a L.w easily)
	//
	// color yellow in Android Studio console/monitor
	static void warning (String TAG, String message){
		if (IS_ACTIVATED) {
			Log.w(">> Warning msg >> ", TAG + " : " + message);
		}
	}


	// ERROR
	//
	// color red in Android Studio console/monitor
	static void error (String message){
		if (IS_ACTIVATED) {
			Log.e(">> Error msg >> ", message);
		}
	}

	// ERROR (to turn a normal Log.e into a L.e easily)
	//
	// color red in Android Studio console/monitor
	static void error (String TAG, String message){
		if (IS_ACTIVATED) {
			Log.e(">> Error msg >> ", TAG + " : " + message);
		}
	}


	// DEBUG
	//
	// color blue in Android Studio console/monitor
	static void debug (String message){
		if (IS_ACTIVATED) {
			Log.d(">> Debug msg >> ", message);
		}
	}

	// DEBUG (to turn a normal Log.d into a L.d easily)
	//
	// color blue in Android Studio console/monitor
	static void debug (String TAG, String message){
		if (IS_ACTIVATED) {
			Log.d(">> Debug msg >> ", TAG + " : " + message);
		}
	}





	// BY FUNCTION (short version)


	// INFO
	//
	// color green in Android Studio console/monitor
	static void i (String message){
		if (IS_ACTIVATED) {
			Log.i(">> Info msg >> ", message);
		}
	}

	// INFO (to turn a normal Log.i into a L.i easily)
	//
	// color green in Android Studio console/monitor
	static void i (String TAG, String message){
		if (IS_ACTIVATED) {
			Log.i(">> Info msg >> ", TAG + message);
		}
	}


	// WARNING
	//
	// color yellow in Android Studio console/monitor
	static void w(String message){
		if (IS_ACTIVATED) {
			Log.w(">> Warning msg >> ", message);
		}
	}

	//WARNING (to turn a normal Log.w into a L.w easily)
	//
	// color yellow in Android Studio console/monitor
	static void w(String TAG, String message){
		if (IS_ACTIVATED) {
			Log.w(">> Warning msg >> ", TAG + " : " + message);
		}
	}


	// ERROR
	//
	// color red in Android Studio console/monitor
	static void e(String message){
		if (IS_ACTIVATED) {
			Log.e(">> Error msg >> ", message);
		}
	}

	// ERROR (to turn a normal Log.e into a L.e easily)
	//
	// color red in Android Studio console/monitor
	static void e(String TAG, String message){
		if (IS_ACTIVATED) {
			Log.e(">> Error msg >> ", TAG + " : " + message);
		}
	}


	// DEBUG
	//
	// color blue in Android Studio console/monitor
	static void d(String message){
		if (IS_ACTIVATED) {
			Log.d(">> Debug msg >> ", message);
		}
	}

	// DEBUG (to turn a normal Log.d into a L.d easily)
	//
	// color blue in Android Studio console/monitor
	static void d(String TAG, String message){
		if (IS_ACTIVATED) {
			Log.d(">> Debug msg >> ", TAG + " : " + message);
		}
	}
}
