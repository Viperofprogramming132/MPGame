package com.Viper.Sound;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.UnsupportedAudioFileException;


public class SoundController {

	private Clip _BackgroundMusic;
	private Clip _CrashSound;
	
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
		
		AudioInputStream _BackgroundMusicStream = GetStream("src/music/Background.wav");
		AudioInputStream _CrashMusicStream = GetStream("src/music/Crash.wav");
		
		try {
			_BackgroundMusic.open(_BackgroundMusicStream);
			_CrashSound.open(_CrashMusicStream);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void StartBackgroundMusic()
	{
		if(!_BackgroundMusic.isRunning())
		{
			_BackgroundMusic.loop(Clip.LOOP_CONTINUOUSLY);
		}
	}
	
    private AudioInputStream GetStream(String f)
    {
        try
        {
        	return AudioSystem.getAudioInputStream(new File(f));
        } catch (UnsupportedAudioFileException | IOException e)
        {
            e.printStackTrace();
        }
		return null;
    }

	public void PlayCrash() {
		if(!_CrashSound.isRunning())
		{
			_CrashSound.setFramePosition(0);
			_CrashSound.start();
		}
		
	}
}
