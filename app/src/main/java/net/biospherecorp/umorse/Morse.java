package net.biospherecorp.umorse;

import android.app.Activity;

import org.apache.commons.collections4.MapUtils;

import java.util.HashMap;
import java.util.Map;


class Morse{

	private static Morse INSTANCE = null;

	// the keys AND the values in the translation tables MUST be unique !!
	private static Map<String, String> TRANSLATION_TABLE = new HashMap<>();
	static Map<String, String> REVERSED_TRANSLATION_TABLE;


	private Morse(){}

	static Morse getInstance(Activity activity){

		// if no INSTANCE, instantiate
		if(INSTANCE == null){
			INSTANCE = new Morse();
		}

		// if translation table is empty, fill it up
		if (TRANSLATION_TABLE.isEmpty()){

			fillTranslationTable(activity);
			REVERSED_TRANSLATION_TABLE = MapUtils.invertMap(TRANSLATION_TABLE);

			L.m("TranslationTable : " + "Ordered size :" + TRANSLATION_TABLE.size());
			L.m("TranslationTable Ordered : " + TRANSLATION_TABLE.toString());
			L.m("TranslationTable Reversed Order size :" + REVERSED_TRANSLATION_TABLE.size());
			L.m("TranslationTable Reversed Order : " + REVERSED_TRANSLATION_TABLE.toString());
		}

		return INSTANCE;
	}

	// Creates and execute an AsyncTask to translate String to Morse
	void translateToMorse(TranslateMorseTask.Delegate delegate, String text){

		if (!text.equals("")){

			TranslateMorseTask task = new TranslateMorseTask(delegate, TRANSLATION_TABLE, true);
			task.execute(text);
		}
	}

	// Creates and execute an AsyncTask to translate Morse to String
	void translateToText(TranslateMorseTask.Delegate delegate, String text){

		if (!text.equals("")){

			TranslateMorseTask task = new TranslateMorseTask(delegate, REVERSED_TRANSLATION_TABLE, false);
			task.execute(text);
		}
	}

	private static void fillTranslationTable(Activity activity){

		TRANSLATION_TABLE.putAll(new GetJSON()
				.getHashMapFromAssets(activity, "morse_code.json"));
	}
}
