package ca.stevenhuang.foldermusicplayer;

import java.io.File;

/**
 * Created by Steven on 2/26/2015.
 */
public class Playable implements IPlayable {
	private File mPlayableFile;

	public Playable() {
		mPlayableFile = null;
	}

	public Playable(File file) {
		setFile(file);
	}

	@Override
	public boolean setFile(File playable) {
		if(playable != null) {
			mPlayableFile = playable;
			return true;
		} else {
			mPlayableFile = null;
			return false;
		}
	}

	@Override
	public File getFile() {
		return mPlayableFile;
	}

	@Override
	public String toString() {
		return mPlayableFile == null ? "" : mPlayableFile.toString();
	}
}
