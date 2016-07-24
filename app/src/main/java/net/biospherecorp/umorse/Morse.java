package net.biospherecorp.umorse;

import android.app.Activity;

import org.apache.commons.collections4.MapUtils;

import java.util.HashMap;
import java.util.Map;


class Morse{

	private static Morse instance = null;

	// the keys AND THE VALUES in the translation tables MUST be unique !!
	private static Map<String, String> translationTable = new HashMap<>();
	static Map<String, String> translationTableReversed;


	private Morse(){}

	static Morse getInstance(Activity activity){

		// if no instance, instantiate
		if(instance == null){
			instance = new Morse();
		}

		// if translation table is empty, fill it up
		if (translationTable.isEmpty()){

			fillTranslationTable(activity);
			translationTableReversed = MapUtils.invertMap(translationTable);

			L.m("TranslationTable : " + "Ordered size :" + translationTable.size());
			L.m("TranslationTable Ordered : " + translationTable.toString());
			L.m("TranslationTable Reversed Order size :" + translationTableReversed.size());
			L.m("TranslationTable Reversed Order : " + translationTableReversed.toString());
		}

		return instance;
	}

	// Creates and execute an AsynTask to translate String to Morse
	void translateToMorse(TranslateMorseTask.AsyncResponse delegate, String text){

		if (!text.equals("")){

			TranslateMorseTask task = new TranslateMorseTask(delegate, translationTable, true);
			task.execute(text);
		}
	}

	// Creates and execute an AsynTask to translate Morse to String
	void translateToText(TranslateMorseTask.AsyncResponse delegate, String text){

		if (!text.equals("")){

			TranslateMorseTask task = new TranslateMorseTask(delegate, translationTableReversed, false);
			task.execute(text);
		}
	}

	private static void fillTranslationTable(Activity activity){

		translationTable.putAll(new GetJSON()
				.getHashMapFromAssets(activity, "morse_code.json"));
	}
}
