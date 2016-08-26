package net.biospherecorp.umorse;

import android.os.AsyncTask;
import android.support.design.widget.Snackbar;


// the dot is the basic unit of time measurement :
// 1 dot = 1 unit
// 1 dash = 3 units
// 1 space between characters components = 1 unit
// 1 space between characters = 3 unit
// 1 space between words = 7 units

class SendMorseTask extends AsyncTask<String, String, Void> {

	// delegation design pattern
	interface PostTask {
		void processSendingTask ();
		void stopSending();
	}

	// the interface for the send mechanism (sound or light)
	interface SendMechanism{
		boolean init();
		void on();
		void off();
		void release();
	}

	private MainActivity _main;
	private TextToMorseFragment _ttmFragment;
	private PostTask _delegate;

	private SendMechanism _mechanism;

	private int _speedMultiplier = 1; // sound can be sent faster than light

	SendMorseTask(MainActivity activity, TextToMorseFragment ttmFrag) {
		_main = activity;
		_ttmFragment = ttmFrag;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();

		if (_ttmFragment.sendWithLight){
			_mechanism = new MorseLight(_main);
			_speedMultiplier = 1;
		}else {
			_mechanism = new MorseSound(_main);
			_speedMultiplier = 2;
		}

		if(!_mechanism.init()){
			_stopSendingMorse();
		}
	}

	@Override
	protected Void doInBackground(String... params) {

		// only 1 argument is supplied to this AsyncTask, no need to iterate over them
		String toSend = params[0];

		// store the previous character to decide whether to add a delay or not
		String previousCharacter = "";

		try {

			// split the string to get the Morse encoded letters
			for(String letter : toSend.split(" ")){

				if (!isCancelled()){ // to make sure the task hasn't been canceled

					// get the letter from the Reversed Translation Table &
					// display it in the snack bar
					publishProgress(Morse.REVERSED_TRANSLATION_TABLE.get(letter));

					// split the morse encoded letter into symbols (dots & dashes)
					for(String symbol : letter.split("")){

						// This condition only applies to dots and dashes
						if (!previousCharacter.equals(" ") && // if previous character isn't a space
								!symbol.equals(" ")){ // and actual character isn't a space (if it is, let it be handled by the switch/case)
							Thread.sleep(MainActivity.delayTime); // add a delay before sending out the next signal
						}

						switch (symbol){

							case "." : // dot
								_mechanism.on();
								Thread.sleep(MainActivity.delayTime / _speedMultiplier); // a dot is 1 unit of time
								_mechanism.off();
								break;

							case "-" : // dash
								_mechanism.on();
								Thread.sleep((MainActivity.delayTime * 3) / _speedMultiplier); // a dash is 3 units of time
								_mechanism.off();
								break;

							default : // space (or any character not recognized)
								Thread.sleep(MainActivity.delayTime / _speedMultiplier); // a space is 1 unit of time
								break;
						}

						previousCharacter = symbol;
					}
				}
			}
		} catch (InterruptedException e) {
				e.printStackTrace();
			return null;
		}finally {
			// release the camera
			// & clear the flashlight
			_clearAll();
		}

		return null;
	}

	@Override
	protected void onProgressUpdate(String... value) {

		// update the snackBar with the currently sending letter
		if (!value[0].equals("")){
			_ttmFragment.showSnack(_main.getString(R.string.snack_letter_sending) + value[0].toUpperCase(),
					Snackbar.LENGTH_INDEFINITE);
		}else{
			_ttmFragment.showSnack(_main.getString(R.string.snack_space_sending),
					Snackbar.LENGTH_INDEFINITE);
		}
	}

	@Override
	protected void onPostExecute(Void aVoid) {
		super.onPostExecute(aVoid);
		_delegate = (TextToMorseFragment) _main.getFragmentManager().findFragmentByTag("ttm");
		_delegate.processSendingTask();
	}

	// release the camera and reset
	// the camera & flashlight variables
	private void _clearAll(){

		if (_mechanism != null){

			_mechanism.release();
			_mechanism = null;
		}
	}

	// stop sending morse code procedure
	// (clear the task, reset the button and
	// the "isSending" variable & show a message
	// in the snackBar)
	private void _stopSendingMorse(){
		_delegate = (TextToMorseFragment) _main.getFragmentManager().findFragmentByTag("ttm");
		_delegate.stopSending();
	}
}
