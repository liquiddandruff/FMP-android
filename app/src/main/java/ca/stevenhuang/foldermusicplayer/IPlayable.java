package ca.stevenhuang.foldermusicplayer;

import java.io.File;

/**
 * Created by Steven on 2/26/2015.
 */
public interface IPlayable {
	boolean setFile(File playable);
	File getFile();
	String toString();
}
