package ca.stevenhuang.foldermusicplayer;

import java.io.File;

/**
 * Created by Steven on 2/26/2015.
 */
public class Playable extends File {
	public Playable(String path) {
		super(path);
	}

	public Playable(File dir, String name) {
		super(dir, name);
	}

}
