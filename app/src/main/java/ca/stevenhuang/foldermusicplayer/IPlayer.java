package ca.stevenhuang.foldermusicplayer;

import java.util.HashSet;

/**
 * Created by Steven on 3/4/2015.
 */
public interface IPlayer {
	boolean play();
	boolean play(Playable playable);
	void setPlayable(Playable playable);
	Playable getPlayable();
	boolean isExtensionPlayable(Playable file);
	HashSet<String> getPlayableFileTypes();
}
