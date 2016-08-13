package net.biospherecorp.umorse;

import android.os.AsyncTask;

import java.util.HashMap;
import java.util.Map;


class TranslateMorseTask extends AsyncTask<String, Void, String> {

	// the dot is the basic unit of time measurement :
	// 1 dot = 1 unit
	// 1 dash = 3 units
	// 1 space between characters components = 1 unit
	// 1 space between characters = 3 unit
	// 1 space between words = 7 units

	// delegation design pattern
	interface Delegate{
		void processTranslationTask (String out);
	}

	// true = translate from text to Morse
	// false = translate from Morse to text
	private boolean _textToMorse = false;

	private Delegate _delegate;
	private Map<String, String> _translationTable = new HashMap<>();


	TranslateMorseTask(Delegate delegate, Map<String, String> translationTable, boolean translateToMorse) {
		this._delegate = delegate;
		this._translationTable = translationTable;
		this._textToMorse = translateToMorse;
	}

	// only the first argument will be processed
	@Override
	protected String doInBackground(String... messagesToTranslate) {

		// to make sure to never return an empty string
		String result = "";

		// if text to morse
		if(_textToMorse){

			String previousChar = "";

			// symbols can be letters or any other
			// symbol (01.- ...) representing a message or some morse code
			for (String currentChar : messagesToTranslate[0].toLowerCase().trim().split("")){

				// cleans up the first character of the string (always "" for some reason ^^)
				if(!currentChar.equals("")){

					// if previous char isn't a space
					// AND current char isn't a space,
					// add a space to the string
					//
					// basically it means : "if we are between 2 characters", add a small space (3 spaces)
					if (!previousChar.equals("") &&
							!previousChar.equals(" ") &&
							!currentChar.equals(" ")){
						result += "   ";
					}

					result += _getCharInTable(currentChar);

					// store the char so it can be evaluated during next iteration
					previousChar = currentChar;
				}
			}

		// if morse to text
		}else{
			// just get the translated character from the translation table
			result += _getCharInTable(messagesToTranslate[0]);
		}

		// return the string
		return result;
	}


	// Called on the UI thread when doInBackground terminates
	@Override
	protected void onPostExecute(String strings) {
		super.onPostExecute(strings);

		_delegate.processTranslationTask(strings);
	}

	private String _getCharInTable(String currentChar){

		String result = "";

		// get the translated currentChar from the translation table
		String translatedChar = _translationTable.get(currentChar);

		// if this currentChar isn't null
		if (translatedChar != null){
			// add it to the string
			result += translatedChar;
		}else{
			// if char is not found
			result += "¿";
		}

		return result;
	}
}
