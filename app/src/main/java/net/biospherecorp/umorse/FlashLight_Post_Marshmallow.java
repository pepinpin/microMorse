package net.biospherecorp.umorse;


import android.annotation.TargetApi;
import android.content.Context;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;


// To deal with devices > API 23 (Marshmallow and up)
class FlashLight_Post_Marshmallow implements MorseLight.FlashLight {

	private Context _ctxt;

	private CameraManager mCameraManager;
	private String mCameraId;

	FlashLight_Post_Marshmallow(Context _ctxt) {
		this._ctxt = _ctxt;
	}

	@TargetApi(23)
	@Override
	public void open() {

		L.i("FlashLight_Post_Marshmallow", ">>>> open()");

		mCameraManager = (CameraManager) _ctxt.getSystemService(Context.CAMERA_SERVICE);

		try {
			mCameraId = mCameraManager.getCameraIdList()[0];
		} catch (CameraAccessException e) {
			e.printStackTrace();
		}
	}

	// Resets the fields to null
	@Override
	public void release() {
		_ctxt = null;
		mCameraManager = null;
		mCameraId = null;
	}

	// turn the light ON
	@TargetApi(23)
	@Override
	public void on() {
		try {
			mCameraManager.setTorchMode(mCameraId, true);
		} catch (CameraAccessException e) {
			e.printStackTrace();
		}
	}

	// turn the light OFF
	@TargetApi(23)
	@Override
	public void off() {
		try {
			mCameraManager.setTorchMode(mCameraId, false);
		} catch (CameraAccessException e) {
			e.printStackTrace();
		}
	}
}
