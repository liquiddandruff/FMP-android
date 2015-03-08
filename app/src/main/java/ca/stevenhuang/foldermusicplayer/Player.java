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
			"mp3", "flac", "midi", "vorbis", "pcm", "wave", "aac"
	));

	private MediaPlayer mPlayer;
	private IPlayable currentPlayable;

	public Player() {
		mPlayer = new MediaPlayer();
		currentPlayable = new Playable();
	}

	@Override
	public boolean play() {
		return play(currentPlayable);
	}

	@Override
	public boolean play(IPlayable playable) {
		try {
			mPlayer.setDataSource(playable.getFile().toString());
			mPlayer.prepare();
			mPlayer.start();
			return true;
		} catch (Exception e) {
			Log.d("4242", e.toString());
			return false;
		}
	}

	@Override
	public boolean setPlayable(IPlayable playable) {
		if(isPlayable(playable)) {
			currentPlayable = playable;
			return true;
		} else {
			return false;
		}
	}

	@Override
	public IPlayable getPlayable() {
		return currentPlayable;
	}

	@Override
	public boolean isPlayable(IPlayable playable) {
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
