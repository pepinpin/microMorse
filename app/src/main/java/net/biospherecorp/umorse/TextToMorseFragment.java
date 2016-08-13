package net.biospherecorp.umorse;

import android.app.Fragment;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class TextToMorseFragment extends Fragment implements TranslateMorseTask.Delegate, SendMorseTask.Delegate{

	private static boolean REPEAT_SEND = false;

	private Morse _morse;
	private SendMorseTask _task;

	private EditText _textToTranslate;
	private FloatingActionButton _repeatButton,  _sendButton;

	private TextView _translatedTextView;

	// max amount of characters allowed
	private final int MAX_NUMBER_OF_CHARS = 200;

	// string holding the text to translate
	private String _translatedString = "";

	// the colors used for the buttons
	private int _sendingColor,  _notSendingColor,  _repeatOnColor, _repeatOffColor;

	// is it actually sending morse
	private boolean _isSending = false;

	// to display sending status
	//
	// declared globally so the text
	// can be updated without recreating
	// a new one (no useless animation)
	private Snackbar _snack;



	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

		return inflater.inflate(R.layout.ttm_fragment, container, false);
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		// get an instance of the class handling the morse translation
		_morse = Morse.getInstance(getActivity());

	// Characters counter
		// setup the counters
		final TextView counterDisplay = (TextView) view.findViewById(R.id.actualChar);
		TextView maxDisplay = (TextView) view.findViewById(R.id.maxChar);
		maxDisplay.setText(" / "+MAX_NUMBER_OF_CHARS);


	// TextView view
		// get the translated text TextView
		_translatedTextView = (TextView) view.findViewById(R.id.resultTextView);

		// turns the vertical scrolling on
		_translatedTextView.setMovementMethod(new ScrollingMovementMethod());



	// EditText view
		//
		_textToTranslate = (EditText) view.findViewById(R.id.toTranslateEditText);

		// set the max number of char in the editText
		_textToTranslate.setFilters(new InputFilter[]{new InputFilter.LengthFilter(MAX_NUMBER_OF_CHARS)});

		// Allow multiline with done button
		_textToTranslate.setHorizontallyScrolling(false);
		_textToTranslate.setMaxLines(Integer.MAX_VALUE);

		// set a TextChangeWatcher on the editText view
		_textToTranslate.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

				// clear the translatedTextView if _textToTranslate is empty
				if (charSequence.length() <= 1){
					_translatedTextView.setText("");
					_translatedString ="";
				}
			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

				// if the number of chars is less than the max char number
				if (charSequence.length() <= MAX_NUMBER_OF_CHARS){

					// update the chars counter
					counterDisplay.setText(String.valueOf(charSequence.length()));

					// translate the string
					_morse.translateToMorse(TextToMorseFragment.this, charSequence.toString());
				}
			}

			@Override
			public void afterTextChanged(Editable editable) {
				// nothing special here
			}
		});


	// Repeat Button
		//
		// the colors
		_repeatOnColor = getResources().getColor(R.color.repeatButtonOn);
		_repeatOffColor = getResources().getColor(R.color.repeatButtonOff);

		// the Floating Action Button => repeat button
		_repeatButton = (FloatingActionButton) view.findViewById(R.id.repeatButton);

		// set the color
		_repeatButton.setBackgroundTintList(ColorStateList.valueOf(_repeatOffColor));

		_repeatButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				REPEAT_SEND = !REPEAT_SEND;

				// set the bkg color
				if (REPEAT_SEND){
					_repeatButton.setBackgroundTintList(ColorStateList.valueOf(_repeatOnColor));
					Toast.makeText(getActivity(), "Repeat is ON", Toast.LENGTH_SHORT).show();
				}else{
					_repeatButton.setBackgroundTintList(ColorStateList.valueOf(_repeatOffColor));
					Toast.makeText(getActivity(), "Repeat is OFF", Toast.LENGTH_SHORT).show();
				}
			}
		});


	// Send Button
		//
		// Colors for the send button
		//
		_sendingColor = getResources().getColor(R.color.sendButtonSending);
		_notSendingColor = getResources().getColor(R.color.sendButtonNotSending);

		// the Floating Action Button => send button
		_sendButton = (FloatingActionButton) view.findViewById(R.id.sendMorseCodeButton);

		// set the bkg image
		_sendButton.setImageResource(android.R.drawable.ic_media_play);
		// set the bkg color
		_sendButton.setBackgroundTintList(ColorStateList.valueOf(_notSendingColor));

		// the _snack that is going to be shown
		_snack = Snackbar.make(_sendButton, "placeholder", Snackbar.LENGTH_SHORT);

		// set the onClickListener
		_sendButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				// if NOT sending and text NOT empty
				if (!_isSending && !_translatedString.equals("")){

					// change the state of the button
					_isSending = true;
					_toggleButton();

					// show the _snack
					showSnack("...", Snackbar.LENGTH_INDEFINITE);

					// send the morse code
					_task = new SendMorseTask(((MainActivity) (getActivity())),
							TextToMorseFragment.this);
					_task.execute(_translatedString);
				}else{

					// if the string isn't empty and
					// morse code is being sent
					if (!_translatedString.equals("")){

						//cancel
						stopSending();
					}else{
						// if string is empty and
						// no morse code is being sent
						showSnack(getString(R.string.snack_nothing_to_send), 800);
					}
				}
			}
		});
	}

	@Override
	public void onPause() {
		super.onPause();

		// if morse code is being sent,
		// stop it all and reset the button
		if (_isSending){
			stopSending();
		}
	}

	// called by the TranslateMorseTask asyncTask
	//
	@Override
	public void processTranslationTask(String out) {

		_translatedString = out;
		_translatedTextView.setText(_translatedString);
	}

	// Called by the sendMorseCode AsyncTask
	//
	@Override
	public void processSendingTask(){

		if (REPEAT_SEND){

			_cancelTasks();

			// send the morse code
			_task = new SendMorseTask(((MainActivity) (getActivity())),
					TextToMorseFragment.this);
			_task.execute(_translatedString);
		}else{

			stopSending();
		}
	}

	// stop everything,
	// reset the button
	// display a _snack
	//
	@Override
	public void stopSending(){

		// toggle the button back
		// to the not sending state
		_setButton(false);

		// show the _snack
		showSnack(getString(R.string.snack_stop_sending), 500);

		_cancelTasks();
	}

	private void _cancelTasks(){

		// if a task is running,
		// cancel it
		if (_task != null){
			_task.cancel(true);
			_task = null;
		}
	}

	// set the button is a specific state (sending/ not sending)
	private void _setButton(boolean isSending){
		this._isSending = isSending;
		_toggleButton();
	}

	// toggle mechanism
	private void _toggleButton(){

		if(_sendButton != null &&
				_textToTranslate != null){

			if (!_isSending){

				_textToTranslate.setEnabled(true);

				_sendButton.setBackgroundTintList(ColorStateList.valueOf(_notSendingColor));
				_sendButton.setImageResource(android.R.drawable.ic_media_play);
			}else{

				_textToTranslate.setEnabled(false);

				_sendButton.setBackgroundTintList(ColorStateList.valueOf(_sendingColor));
				_sendButton.setImageResource(android.R.drawable.ic_delete);
			}
		}
	}

	// easier than constructing a snackbar every time
	void showSnack(String text, int duration){
		_snack.setText(text)
				.setDuration(duration)
				.show();
	}
}
