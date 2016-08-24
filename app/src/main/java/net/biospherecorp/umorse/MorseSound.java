package net.biospherecorp.umorse;


import android.media.AudioManager;
import android.media.SoundPool;

class MorseSound implements SendMorseTask.SendMechanism {

	private SoundPool _sp;
	private int soundId;
	private MainActivity _main;


	MorseSound(MainActivity main) {
		this._main = main;
	}

	@Override
	public boolean init() {

		try{
			_sp = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);

		}catch (Exception ex){
			return false;
		}

		return true;
	}

	@Override
	public void on() {

		// resources loaded here, as the stop() method releases all resources
		soundId = _sp.load(_main, R.raw.morse_sound, 1);

		_sp.play(soundId, 1, 1, 0, 0, 1);
	}

	@Override
	public void off() {

		_sp.stop(soundId);
	}

	@Override
	public void release() {

		if (_sp != null){

			_sp.release();
			_sp = null;
		}
	}
}
