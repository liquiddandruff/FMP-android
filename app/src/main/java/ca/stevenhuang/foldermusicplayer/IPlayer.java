package ca.stevenhuang.foldermusicplayer;

import java.util.HashSet;

/**
 * Created by Steven on 3/4/2015.
 */
public interface IPlayer {
	boolean isPlaying();
	boolean pause();
	boolean resume();
	boolean play();
	boolean play(Playable playable);
	void setPlayable(Playable playable);
	Playable getPlayable();
	void destroy();
}
