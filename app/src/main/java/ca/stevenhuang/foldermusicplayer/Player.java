package ca.stevenhuang.foldermusicplayer;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.PowerManager;
import android.util.Log;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;

/**
 * Created by Steven on 3/4/2015.
 */
public class Player implements IPlayer{
	private static final HashSet<String> PLAYABLE_FILE_TYPES = new HashSet<String>(Arrays.asList(
			"3gp", "mp4", "m4a", "aac", "ts",
			"flac", "mp3",
			"mid", "xmf", "mxmf", "rtttl", "rtx", "ota", "imy",
			"ogg", "mkv",
			"wav"
	));

	private MediaPlayer mPlayer;
	private Playable currentPlayable;

	public Player(Context context) {
		mPlayer = new MediaPlayer();
		mPlayer.setWakeMode(context, PowerManager.PARTIAL_WAKE_LOCK);
		mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
		mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer mp) {
				try {
					mp.stop();
					mp.prepare();
					mp.start();
				} catch (Exception e) {
				}
			}
		});
		currentPlayable = null;
	}

	@Override
	public boolean isPlaying() {
		return mPlayer.isPlaying();
	}

	@Override
	public boolean pause() {
		mPlayer.pause();
		return true;
	}

	@Override
	public boolean resume() {
		try {
			mPlayer.start();
			return currentPlayable != null;
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public boolean play() {
		return currentPlayable != null && play(currentPlayable);
	}

	@Override
	public boolean play(Playable playable) {
		try {
			mPlayer.reset();
			mPlayer.setDataSource(playable.toString());
			mPlayer.prepare();
			mPlayer.start();
			return true;
		} catch (Exception e) {
			Log.d("4242", e.toString());
			return false;
		}
	}

	@Override
	public void setPlayable(Playable playable) {
		currentPlayable = playable;
	}

	@Override
	public Playable getPlayable() {
		return currentPlayable;
	}

	static public boolean isExtensionPlayable(File file) {
		final String fileName = file.toString();
		final int lastDotIndex = fileName.lastIndexOf('.');
		String fileExt = fileName.substring(lastDotIndex + 1);
		return !fileExt.equals("") && PLAYABLE_FILE_TYPES.contains(fileExt);
	}

	@Override
	public void destroy() {
		mPlayer.release();
	}
}
