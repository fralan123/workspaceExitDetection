package demo;

 
import java.io.IOException;
import java.io.InputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;
 
public class Player extends Thread {
 
	private String filename;
 
	private final int EXTERNAL_BUFFER_SIZE = 524288; // 128Kb
 
	private volatile boolean doPlay = false;
 
	public Player(String wavfile) {
		filename = wavfile;
	}
 

	public void run() {
		for (;;) {
			if (doPlay) {
				doPlay = false;
				
				AudioInputStream audioInputStream;
				try {
					audioInputStream = getAudioStream();
				} catch (UnsupportedAudioFileException e1) {
					e1.printStackTrace();
					return;
				} catch (IOException e1) {
					e1.printStackTrace();
					return;
				}
		 
				AudioFormat format = audioInputStream.getFormat();
				DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
		 
				SourceDataLine sdLine;
				try {
					sdLine = getDataLine(format, info);
				} catch (LineUnavailableException e1) {
					e1.printStackTrace();
					return;
				} 
		 
				playWav(audioInputStream, sdLine);
			}
			else {
				try {
					sleep(1000);
				} catch (InterruptedException e) {
				}
			}
		}
	}

	/**
	 * @param audioInputStream
	 * @param sdLine
	 */
	private void playWav(AudioInputStream audioInputStream, SourceDataLine sdLine) {
		sdLine.start();
		int bytesRead = 0;
		byte[] abData = new byte[EXTERNAL_BUFFER_SIZE];
 
		try {
			while (bytesRead != -1) {
				bytesRead = audioInputStream.read(abData, 0, abData.length);
				if (bytesRead >= 0)
					sdLine.write(abData, 0, bytesRead);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			sdLine.drain();
			sdLine.close();
		}
	}

	/**
	 * @param format
	 * @param auline
	 * @param info
	 * @return
	 * @throws LineUnavailableException 
	 */
	private SourceDataLine getDataLine(AudioFormat format, DataLine.Info info) throws LineUnavailableException {
		SourceDataLine sdLine = null;
		
		sdLine = (SourceDataLine) AudioSystem.getLine(info);
		sdLine.open(format);

 
		if (sdLine.isControlSupported(FloatControl.Type.PAN)) {
			FloatControl pan = (FloatControl) sdLine
					.getControl(FloatControl.Type.PAN);
			pan.setValue(-1.0f);
		}

		return sdLine;
	}

	/**
	 * @return
	 * @throws IOException 
	 * @throws UnsupportedAudioFileException 
	 */
	private AudioInputStream getAudioStream() throws UnsupportedAudioFileException, IOException {
		InputStream stream = getClass().getResourceAsStream(filename);
 
		AudioInputStream audioInputStream = null;
		audioInputStream = AudioSystem.getAudioInputStream(stream);

		return audioInputStream;
	}


	/**
	 * 
	 */
	public void play() {
		doPlay = true;
		interrupt();
	}
}