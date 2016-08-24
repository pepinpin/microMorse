package net.biospherecorp.umorse;

import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

class MorseLight implements SendMorseTask.SendMechanism {

	private MainActivity _main;

	// deprecated but still the best way
	// to get the camera as Camera2 is only available
	// from API 21
	private Camera _camera;
	private Camera.Parameters _parameters;

	private boolean isFlashOn = false;

	private static final String TAG = "MorseLight";

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
	private void _getCamera(){

		// if the camera is not already opened
		if (_camera == null){
			try {
				// open it
				_camera = Camera.open();
			}catch (RuntimeException e){
				e.printStackTrace();
				L.e(TAG, "Camera Error : Couldn't get the camera, it may be used by another app !");
				L.e(TAG, "Camera Error : " + e.getMessage());
			}
		}
	}

	// checks for flash and gets the camera device
	@Override
	public boolean init(){

		// check if the device has a flash
		//
	// it it DOES have a flash
		if(_checkIfCameraHasFlash()){

			// check if the camera
			// is available (not already in use)
			_getCamera();

			// if the camera is not available
			if (_camera == null){

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

	// releases the camera and resets the
	// camera & parameters fields to null
	@Override
	public void release(){
		if (_camera != null){
			_camera.release();
			_camera = null;
			_parameters = null;
		}
	}


	// turn the light ON
	@Override
	public void on(){

		// if light is off and there is a camera object
		if(_camera != null && !isFlashOn){

			_parameters = _camera.getParameters();
			_parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);

			_camera.setParameters(_parameters);
			_camera.startPreview();

			isFlashOn = true;
		}
	}


	// turn the light OFF
	@Override
	public void off(){

		// if light is on and there is a camera object
		if (_camera != null && isFlashOn){

			_parameters = _camera.getParameters();
			_parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);

			_camera.setParameters(_parameters);
			_camera.stopPreview();

			isFlashOn = false;
		}
	}
}
