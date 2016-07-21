package net.biospherecorp.umorse;

import android.os.AsyncTask;

import net.biospherecorp.umorse.SimpleCamera.FlashLight;


// the dot is the basic unit of time measurement :
// 1 dot = 1 unit
// 1 dash = 3 units
// 1 space between characters components = 1 unit
// 1 space between characters = 3 unit
// 1 space between words = 7 units

class SendMorseTask extends AsyncTask<String, Void, Void> {

	private MainActivity _main;

	private SimpleCamera _camera;
	private FlashLight _flashLight;

	SendMorseTask(MainActivity activity) {
		_main = activity;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();

		// Create a SimpleCamera object to send morse code
		_camera = new SimpleCamera(_main);

		if(!_camera.initCamera()){
//			_main.stopSending();
			stopSendingMorse();
		}

		// get the flashlight
		_flashLight = _camera.new FlashLight();
	}

	@Override
	protected Void doInBackground(String... params) {

		// only 1 argument is supplied to this AsyncTask, no need to iterate over them
		String toTranslate = params[0];

		// store the previous character to decide whether to add a delay or not
		String previousCharacter = "";

		try {
			for(String symbol : toTranslate.split("")){
				if (!isCancelled()){ // to make sure the task hasn't been canceled

					// This condition only applies to dots and dashes
					if (!previousCharacter.equals(" ") && // if previous character isn't a space
							!symbol.equals(" ")){ // and actual character isn't a space (if it is, let it be handled by the switch/case)
						Thread.sleep(MainActivity.delayTime); // add a delay before sending out the next signal
					}

					switch (symbol){

						case "." : // dot
							_flashLight.lightOn();
							Thread.sleep(MainActivity.delayTime); // a dot is 1 unit of time
							_flashLight.lightOff();
							break;

						case "-" : // dash
							_flashLight.lightOn();
							Thread.sleep(MainActivity.delayTime * 3); // a dash is 3 units of time
							_flashLight.lightOff();
							break;

						default : // space (or any character not recognized)
							Thread.sleep(MainActivity.delayTime); // a space is 1 unit of time
							break;
					}

					previousCharacter = symbol;

				}
			}
		} catch (InterruptedException e) {
				e.printStackTrace();
			return null;
		}finally {
			// release the camera
			// & clear the flashlight
			clearAll();
		}

		return null;
	}


	@Override
	protected void onPostExecute(Void aVoid) {
		super.onPostExecute(aVoid);

		// stop the tasks and reset the button
		//_main.stopSending();
		stopSendingMorse();

	}

	private void clearAll(){
		if (_camera != null){
			_camera.releaseCamera();
			_camera = null;
		}
		_flashLight = null;
	}

	private void stopSendingMorse(){
		TextToMorseFragment frag = (TextToMorseFragment) _main.getFragmentManager().findFragmentByTag("ttm");
		frag.stopSending();
	}

}
