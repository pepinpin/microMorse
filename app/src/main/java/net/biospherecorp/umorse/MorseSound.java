package net.biospherecorp.umorse;


import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

class MorseSound implements SendMorseTask.SendMechanism {

	private Thread t;

	private int sampleRate = 44100;
	private int frequence = 550;

	private boolean isRunning = true;
	boolean isPlaying = false;


	private double fr = frequence * 2;


	@Override
	public boolean isPlaying() {
		return isPlaying;
	}

	@Override
	public boolean init() {

		// start a new thread to synthesise audio
		t = new Thread() {

			public void run() {
				// set process priority
				setPriority(Thread.MAX_PRIORITY);
				// set the buffer size
				int buffsize = AudioTrack.getMinBufferSize(sampleRate,
						AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT);
				// create an audiotrack object
				AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
						sampleRate, AudioFormat.CHANNEL_OUT_MONO,
						AudioFormat.ENCODING_PCM_16BIT, buffsize,
						AudioTrack.MODE_STREAM);

				short samples[] = new short[buffsize];
				int amp = 10000;
				double twopi = 8.*Math.atan(1.);
				double ph = 0.0;

				// start audio
				audioTrack.play();

				// synthesis loop
				while(isRunning){

					while (isPlaying){
						for(int i=0; i < buffsize; i++){
							samples[i] = (short) (amp*Math.sin(ph));
							ph += twopi*fr/ sampleRate;
						}
						audioTrack.write(samples, 0, buffsize);
					}

				}
				audioTrack.stop();
				audioTrack.release();
			}
		};

		t.start();

		return true;
	}

	@Override
	public void generate(int duration) throws InterruptedException {

		isPlaying = true;

		Thread.sleep(duration);

		isPlaying = false;
	}

	@Override
	public void release() {

		isRunning = isPlaying = false;

		if (!t.isInterrupted()){
			try {
				t.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}
}
