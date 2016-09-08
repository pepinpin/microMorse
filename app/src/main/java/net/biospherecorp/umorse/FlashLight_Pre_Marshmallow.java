package net.biospherecorp.umorse;

import android.hardware.Camera;

// To deal with devices before API 23 (Marshmallow)
class FlashLight_Pre_Marshmallow implements MorseLight.FlashLight {

	// deprecated but used
	// on devices before API 23 (Marshmallow)
	private Camera _camera;
	private Camera.Parameters _parameters;

	@Override
	public void open() {

		L.i("FlashLight_Pre_Marshmallow", ">>>> open()");

		// if the camera is not already opened
		if (_camera == null){
			try {
				// open it
				_camera = Camera.open();
			}catch (RuntimeException e){
				e.printStackTrace();
			}
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
		if(_camera != null){

			_parameters = _camera.getParameters();
			_parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);

			_camera.setParameters(_parameters);
			_camera.startPreview();
		}
	}

	// turn the light OFF
	@Override
	public void off(){

		// if light is on and there is a camera object
		if (_camera != null){

			_parameters = _camera.getParameters();
			_parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);

			_camera.setParameters(_parameters);
			_camera.stopPreview();
		}
	}
}
