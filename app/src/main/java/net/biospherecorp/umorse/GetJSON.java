package net.biospherecorp.umorse;


import android.app.Activity;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

class GetJSON {

	private JSONObject _resultJSONobject;
	private Map<String, String> _resultHashMap = new HashMap<>();

	private int tryCounter = 0;


	private void getObjectFromAssets(Activity activity, String fileName){

		try {
			InputStream is = activity.getAssets().open(fileName);
			int size = is.available();
			byte[] buffer = new byte[size];
			is.read(buffer);
			is.close();
			String json = new String(buffer, "UTF-8");

			_resultJSONobject = new JSONObject(json);

		} catch (IOException | JSONException ex) {
			ex.printStackTrace();
		}
	}

	Map<String, String> getHashMapFromAssets(Activity activity, String fileName) {

		// to handle the possibility of some problems accessing the JSON file
		// we try 10 times to get the file and display a toast if it fails

		while(tryCounter <= 10 && _resultJSONobject == null){

			tryCounter++;
			getObjectFromAssets(activity, fileName);
		}

		if (_resultJSONobject != null){

			tryCounter = 0;

			Iterator<String> keys = _resultJSONobject.keys();
			while(keys.hasNext()){

				final String currentKey = keys.next();
				String currentValue = "";

				try {
					currentValue = (String) _resultJSONobject.get(currentKey);
				} catch (JSONException e1) {
					e1.printStackTrace();
				}
				_resultHashMap.put(currentKey, currentValue);
			}

		}else{
			tryCounter = 0;
			Toast.makeText(activity, R.string.error_no_json_toast, Toast.LENGTH_SHORT).show();
		}
		return _resultHashMap;
	}
}
