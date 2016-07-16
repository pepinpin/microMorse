package net.biospherecorp.umorse;

import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

class SimpleCamera {

	private MainActivity _main;

	// deprecated but still the best way
	// to get the camera as Camera2 is only available
	// from API 21
	private Camera _camera;
	private Camera.Parameters _parameters;

	private boolean isFlashOn = false;

	private static final String TAG = "SimpleCamera";

	SimpleCamera(MainActivity activity){
		_main = activity;
	}


	// checks to make sure the device has a flash,
	// displays an AlertDialog and quit the app if not
	private void _checkIfCameraHasFlash(){

		// Checks to see if the device has a camera flash (flash light)
		boolean hasFlash = _main.getApplicationContext()
				.getPackageManager()
				.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);

		// if it hasn't, show an AlertDialog
		if(!hasFlash){
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

			// stop the tasks and reset the button
			_main.getTtmFragment().cancelSending();
		}
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
			}finally {
				// 2nd test to check if the camera has been open properly
				// if not, display a Toast
				if (_camera == null){
					Toast.makeText(_main, R.string.error_no_camera_toast, Toast.LENGTH_LONG).show();
				}
			}
		}
	}

	// checks for flash and gets the camera device
	void initCamera(){
		_checkIfCameraHasFlash();
		_getCamera();
	}

	// releases the camera and resets the
	// camera & parameters fields to null
	void releaseCamera(){
		if (_camera != null){
			_camera.release();
			_camera = null;
			_parameters = null;
		}
	}

	// inner class representing just the light torch
	class FlashLight{

		// turn the light ON
		void lightOn(){

			// if light is off and there is a camera object
			if(!isFlashOn && _camera != null){

				_parameters = _camera.getParameters();
				_parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);

				_camera.setParameters(_parameters);
				_camera.startPreview();

				isFlashOn = true;
			}
		}


		// turn the light OFF
		void lightOff(){

			// if light is on and there is a camera object
			if (isFlashOn && _camera != null){

				_parameters = _camera.getParameters();
				_parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);

				_camera.setParameters(_parameters);
				_camera.stopPreview();

				isFlashOn = false;
			}
		}
	}
}
