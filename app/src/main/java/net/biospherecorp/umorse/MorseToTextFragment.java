package net.biospherecorp.umorse;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v4.view.GestureDetectorCompat;
import android.text.method.ScrollingMovementMethod;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import ru.katso.livebutton.LiveButton;

import static android.content.Context.VIBRATOR_SERVICE;


public class MorseToTextFragment extends Fragment implements TranslateMorseTask.Delegate,
		GestureDetector.OnGestureListener,
		GestureDetector.OnDoubleTapListener{

	// the object who will handle the translation
	private Morse _morse;

	// the TextView who will display the result of the translation
	private EditText _translatedTextView;

	// the string holding the symbols to translate
	private String _toTranslate = "";

	// the string holding the text retrieved from textView
	private String _tmpString;

	// to know if the user wants a small space (between characters)
	// or a big space (between words)
	//
	// Used by the processResponse method
	private boolean _bigSpace = false;

	// to handle gestures (tap, double taps, long press, pinch...)
	private GestureDetectorCompat _gDetector;

	// to give a haptic feedback when the space button is triggered
	// as the GestureDetectorCompat doesn't give any
	private Vibrator _mVib;


	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		return inflater.inflate(R.layout.mtt_fragment, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

	// The user interactions
		//
		// Instantiate the gesture detector with the
		// application context and an implementation of
		// GestureDetector.OnGestureListener
		_gDetector = new GestureDetectorCompat(getActivity(),this);
		// Set the double tap listener on the GestureDetector
		_gDetector.setOnDoubleTapListener(this);

		// Instantiate the vibrator
		_mVib = (Vibrator) getActivity().getSystemService(VIBRATOR_SERVICE);

		// Instantiate the Morse object, that will handle the translations (MTT and TTM)
		_morse = Morse.getInstance(getActivity());

		// get the buttons from the view
		final TextView delButton = (TextView) view.findViewById(R.id.delButton);
		final LiveButton dotButton = (LiveButton) view.findViewById(R.id.dot_button);
		final LiveButton dashButton = (LiveButton) view.findViewById(R.id.dash_button);

		final Button spaceButton = (Button) view.findViewById(R.id.space_button);

		// get the TextView from the view
		_translatedTextView = (EditText) view.findViewById(R.id.resultTextView);

		// turns the vertical scrolling on
		_translatedTextView.setMovementMethod(new ScrollingMovementMethod());


		// the dot button
		dotButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				//request the focus to display the cursor
				_translatedTextView.requestFocus();

				_toTranslate += ".";
				_tmpString = _translatedTextView.getText().toString() + ".";

				_displayTranslatedText(_tmpString);
			}
		});


		// the dash button
		dashButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				//request the focus to display the cursor
				_translatedTextView.requestFocus();

				_toTranslate += "-";
				_tmpString = _translatedTextView.getText().toString() + "-";

				_displayTranslatedText(_tmpString);
			}
		});


		// set an onTouch listener that will pass the event
		// to the GestureDetectorCompat object
		//
		// the space button
		spaceButton.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View view, MotionEvent motionEvent) {

				_gDetector.onTouchEvent(motionEvent);

				// return true if the event has been consumed
				// false otherwise
				//
				// false is return so the onClickListener can
				// work (to fix the lack of animation)
				return false;
			}
		});

		spaceButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				// little hack to allow the on click animation
				// on this custom button
			}
		});


		delButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				_tmpString = _translatedTextView.getText().toString();

				if (_tmpString.length() > 0){
					_tmpString = _tmpString.substring(0, _tmpString.length() -1);
					_displayTranslatedText(_tmpString);

					if (_toTranslate.length() > 0){
						_toTranslate = _toTranslate.substring(0, _toTranslate.length() -1);
					}
				}
			}
		});
	}

	// called by the TranslateMorseTask AsyncTask
	// onPostExecute method
	@Override
	public void processTranslationTask(String out) {

		// get the text from the text view
		_tmpString = _translatedTextView.getText().toString();

		// strip off the temporary morse code from the string
		_tmpString = _tmpString.substring(0, _tmpString.indexOf(_toTranslate));

		// append the result from the TranslateMorseTask asynctask
		_tmpString += out;

		// if _processSpace has been triggered by a big space,
		// add a space after the string
		if (_bigSpace){
			_tmpString += " ";
			_bigSpace = false;
		}

		L.i("Translated Text :", _tmpString);

		// set the textView

		_displayTranslatedText(_tmpString);

		// clear the string holding the text to translate
		_toTranslate = "";

	}

	// 1 tap = small space
	@Override
	public boolean onSingleTapConfirmed(MotionEvent motionEvent) {

		_processSpace(false);
		return true;
	}

	// 2 taps = big space
	@Override
	public boolean onDoubleTap(MotionEvent motionEvent) {

		_processSpace(true);
		return true;
	}

	// long press = big space
	@Override
	public void onLongPress(MotionEvent motionEvent) {

		_processSpace(true);
	}

	private void _displayTranslatedText(String txt){

		_translatedTextView.setText(txt);

		// force the cursor to show up at the end of the line
		_translatedTextView.setSelection(_translatedTextView.getText().length());

	}

	// called by the space button
	private void _processSpace(boolean isBigSpace){

		//request the focus to display the cursor
		_translatedTextView.requestFocus();

		// if the string to translate is not empty
		if (!_toTranslate.equals("")){

			// vibrate for 50 ms
			_mVib.vibrate(50);

			// has this method been called by a small or big space ?
			_bigSpace = isBigSpace;

			// start the ASyncTask
			_morse.translateToText(this, _toTranslate);
		}else{
			// if no text to translate, just add a normal space
			_tmpString = _translatedTextView.getText().toString();

			_displayTranslatedText(_tmpString += " ");
		}
	}








	// NOT USED, but still need to be implemented

	@Override
	public boolean onDoubleTapEvent(MotionEvent motionEvent) {return false;}

	@Override
	public boolean onDown(MotionEvent motionEvent) {return false;}

	@Override
	public void onShowPress(MotionEvent motionEvent) {}

	@Override
	public boolean onSingleTapUp(MotionEvent motionEvent) {return false;}

	@Override
	public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {return false;}

	@Override
	public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {return false;}
}
