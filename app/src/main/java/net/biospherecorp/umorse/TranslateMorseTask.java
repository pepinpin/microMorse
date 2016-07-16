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
	interface AsyncResponse{
		void processResponse (String out);
	}

	// true = translate from text to Morse
	// false = translate from Morse to text
	private boolean textToMorse = false;

	private AsyncResponse delegate;
	private Map<String, String> translationTable = new HashMap<>();


	TranslateMorseTask(AsyncResponse delegate, Map<String, String> translationTable, boolean translateToMorse) {
		this.delegate = delegate;
		this.translationTable = translationTable;
		this.textToMorse = translateToMorse;
	}

	// only the first argument will be processed
	@Override
	protected String doInBackground(String... messagesToTranslate) {

		// to make sure to never return an empty string
		String result = "";

		// if text to morse
		if(textToMorse){

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

					result += getCharInTable(currentChar);

					// store the char so it can be evaluated during next iteration
					previousChar = currentChar;
				}
			}

		// if morse to text
		}else{
			// just get the translated character from the translation table
			result += getCharInTable(messagesToTranslate[0]);
		}

		// return the string
		return result;
	}


	// Called on the UI thread when doInBackground terminates
	@Override
	protected void onPostExecute(String strings) {
		super.onPostExecute(strings);

		delegate.processResponse(strings);

	}

	private String getCharInTable(String currentChar){

		String result = "";

		// get the translated currentChar from the translation table
		String translatedChar = translationTable.get(currentChar);

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
