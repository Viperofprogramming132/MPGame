package com.Viper.Sound;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * Controls all the sound system for the game
 * 
 * All clips are played from here
 * @author Aidan
 *
 */
public class SoundController {

	/**
	 * Clip containing the background music
	 * 
	 * Thanks for the Copyright free music from:
	 * https://ozzed.net/
	 * https://soundcloud.com/ozzednet/lingonsalt
	 */
	private Clip _BackgroundMusic;
	
	/**
	 * The Clip of the sound that is played when the vehicle crashes
	 */
	private Clip _CrashSound;
	
	/**
	 * Creates a sound controller 
	 * Attempts to read in the sound clips
	 */
	public SoundController()
	{
		try
		{
			_BackgroundMusic = AudioSystem.getClip();
			_CrashSound = AudioSystem.getClip();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		AudioInputStream _BackgroundMusicStream = GetStream("/music/Background.wav");
		AudioInputStream _CrashMusicStream = GetStream("/music/Crash.wav");
		
		try {
			_BackgroundMusic.open(_BackgroundMusicStream);
			_CrashSound.open(_CrashMusicStream);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Starts the background music on a continuous loop
	 */
	public void StartBackgroundMusic()
	{
		if(!_BackgroundMusic.isRunning())
		{
			_BackgroundMusic.loop(Clip.LOOP_CONTINUOUSLY);
		}
	}
	
	/**
	 * Gets the audio input stream from the file input
	 * @param f The file to open to read the audio file
	 * @return The AudioInputStream of the given file
	 */
    private AudioInputStream GetStream(String f)
    {
        try
        {
        	return AudioSystem.getAudioInputStream(this.getClass().getResourceAsStream(f));
        } catch (UnsupportedAudioFileException | IOException e)
        {
            e.printStackTrace();
        }
		return null;
    }

    /**
     * Plays the crash sound
     */
	public void PlayCrash() {
		if(!_CrashSound.isRunning())
		{
			_CrashSound.setFramePosition(0);
			_CrashSound.start();
		}
		
	}
}
