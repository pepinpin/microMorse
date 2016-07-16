package net.biospherecorp.umorse;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class TextToMorseFragment extends Fragment implements TranslateMorseTask.AsyncResponse{

	private Morse _morse;
	private SendMorseTask _task;

	private TextView _translatedTextView;
	private final int MAX_NUMBER_OF_CHARS = 200;

	private Button sendButton;

	private String _translatedString = "";

	private int sendingColor;
	private int notSendingColor;

	private boolean isSending = false;

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


	// Colors for the send button
		//
		sendingColor = getResources().getColor(R.color.sendButtonSending);
		notSendingColor = getResources().getColor(R.color.sendButtonNotSending);

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
		EditText textToTranslate = (EditText) view.findViewById(R.id.toTranslateEditText);

		// set the max number of char in the editText
		textToTranslate.setFilters(new InputFilter[]{new InputFilter.LengthFilter(MAX_NUMBER_OF_CHARS)});

		// hide the keyboard when the enter/done button is pressed
		textToTranslate.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
				// hide the keyboard when the enter key
				// button is pressed
				if (actionId == EditorInfo.IME_ACTION_GO
						|| actionId == EditorInfo.IME_ACTION_DONE
						|| actionId == EditorInfo.IME_ACTION_NEXT
						|| actionId == EditorInfo.IME_ACTION_SEND
						|| actionId == EditorInfo.IME_ACTION_SEARCH
						|| (keyEvent.getAction() == KeyEvent.KEYCODE_ENTER)) {

					((MainActivity)getActivity()).hideSoftKeyboard();

					return true;
				}
				return false;
			}
		});

		// set a TextChangeWatcher on the editText view
		textToTranslate.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

				// clear the translatedTextView if textToTranslate is empty
				if (charSequence.length() <= 1){
					_translatedTextView.setText("");
				}
			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

				// if the number of chars is less than the max char number
				if (charSequence.length() <= MAX_NUMBER_OF_CHARS){

					// update the chars counter
					counterDisplay.setText(""+charSequence.length());

					// translate the string
					_morse.translateToMorse(TextToMorseFragment.this, charSequence.toString());
				}
			}

			@Override
			public void afterTextChanged(Editable editable) {}
		});



	// Send Button
		//
		sendButton = (Button) view.findViewById(R.id.sendMorseCodeButton);
		sendButton.setBackgroundColor(notSendingColor);

		sendButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				if (!_translatedString.equals("")){

					// to change the state of the button
					setButton();

					clearTask();

					if (isSending){
						_task = new SendMorseTask((MainActivity) (TextToMorseFragment.this.getActivity()));
						_task.execute(_translatedString);
					}
				}
			}
		});
	}

	@Override
	public void onPause() {
		super.onPause();

		cancelSending();
	}


	@Override
	public void processResponse(String out) {

		_translatedString = out;

		_translatedTextView.setText(_translatedString);
		L.m("Translated Text :", out);
	}


	void cancelSending(){
		clearTask();
		setButton(false);
	}

	private void setButton(){
		isSending = !isSending;
		toggleButton();
	}


	private void setButton(boolean isSending){
		this.isSending = isSending;
		toggleButton();
	}

	private void toggleButton(){

		if(sendButton != null){

			if (isSending){
				sendButton.setBackgroundColor(sendingColor);
				sendButton.setText(R.string.cancel);

			}else{
				sendButton.setBackgroundColor(notSendingColor);
				sendButton.setText(R.string.sending_code);
			}
		}
	}

	private void clearTask(){

		if (_task != null){
			_task.cancel(true);
			_task = null;
		}
	}

}
