package ca.stevenhuang.foldermusicplayer;

import android.media.MediaPlayer;
import android.util.Log;

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

	public Player() {
		mPlayer = new MediaPlayer();
		currentPlayable = null;
	}

	@Override
	public boolean play() {
		return play(currentPlayable);
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

	@Override
	public boolean isExtensionPlayable(Playable playable) {
		final String fileName = playable.toString();
		final int lastDotIndex = fileName.lastIndexOf('.');
		String fileExt = fileName.substring(lastDotIndex + 1);
		return !fileExt.equals("") && PLAYABLE_FILE_TYPES.contains(fileExt);
	}

	@Override
	public HashSet<String> getPlayableFileTypes() {
		return PLAYABLE_FILE_TYPES;
	}
}
