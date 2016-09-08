package net.biospherecorp.umorse;

import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

class MorseLight implements SendMorseTask.SendMechanism {

	private MainActivity _main;

	private FlashLight _flash;
	private boolean isFlashOn = false;

	private static final String TAG = "MorseLight";


	interface FlashLight{
		void open();
		void release();
		void on();
		void off();
	}


	MorseLight(MainActivity activity){
		_main = activity;
	}


	// checks to make sure the device has a flash,
	// displays an AlertDialog and quit the app if not
	private boolean _checkIfCameraHasFlash(){

		// Checks to see if the device has a camera flash (flash light)
		boolean hasFlash = _main.getApplicationContext()
				.getPackageManager()
				.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);

		return hasFlash;
	}


	// get the camera device and the parameters
	private boolean _getCamera(){

		try {

			// instantiate flash light object depending on SDK version
			if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
				_flash = new FlashLight_Pre_Marshmallow();
			}else{
				_flash = new FlashLight_Post_Marshmallow(_main);
			}

			// open the flash
			_flash.open();

		} catch (Exception e) {
			e.printStackTrace();
			L.e(TAG, "Camera Error : Couldn't get the camera, it may be used by another app !");
			L.e(TAG, "Camera Error : " + e.getMessage());

			return false;
		}

		return true;
	}


	// checks for flash and gets the camera device
	@Override
	public boolean init(){

	// check if the device has a flash
	//
	// if it DOES have a flash
		if(_checkIfCameraHasFlash()){

			// check if the camera
			// is available (not already in use)
			//
			// if the camera is not available
			if (!_getCamera()){

				// display a toast
				Toast.makeText(_main, R.string.error_no_camera_toast, Toast.LENGTH_LONG).show();

				// it has a flash but the camera ISN'T available
				return false;
			}

			// it has a flash and the camera IS available
			return true;

	// if it DOESN'T have a flash
		}else{

			// Show an alertDialog
			final AlertDialog alertDialog = new AlertDialog.Builder(_main).create();
			alertDialog.setTitle(_main.getString(R.string.error_no_flash_title));
			alertDialog.setMessage(_main.getString(R.string.error_no_flash_message));
			alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, _main.getString(R.string.error_no_flash_dismiss_button), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialogInterface, int i) {
					// close the alert dialog
					alertDialog.dismiss();
				}
			});

			alertDialog.show();

			// it DOESN'T have a flash
			return false;
		}
	}


	@Override
	public void generate(int duration) throws InterruptedException {

		on();
		Thread.sleep(duration);
		off();
	}


	// releases the flash
	@Override
	public void release(){
		if (_flash != null){
			_flash.release();
			_flash = null;
		}
	}


	// turn the light ON
	private void on(){

		// if light is off and there is a flash object
		if(_flash != null && !isFlashOn){

			_flash.on();
			isFlashOn = true;
		}
	}


	// turn the light OFF
	private void off(){

		// if light is on and there is a flash object
		if (_flash != null && isFlashOn){

			_flash.off();
			isFlashOn = false;
		}
	}
}
