package ca.stevenhuang.foldermusicplayer;

import java.util.HashSet;

/**
 * Created by Steven on 3/4/2015.
 */
public interface IPlayer {
	boolean play();
	boolean play(IPlayable playable);
	boolean setPlayable(IPlayable playable);
	IPlayable getPlayable();
	boolean isPlayable(IPlayable file);
	HashSet<String> getPlayableFileTypes();
}
