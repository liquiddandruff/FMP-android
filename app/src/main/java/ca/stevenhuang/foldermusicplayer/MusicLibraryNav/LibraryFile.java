package ca.stevenhuang.foldermusicplayer.MusicLibraryNav;

import com.mikepenz.fontawesome_typeface_library.FontAwesome;

import java.io.File;

/**
 * Created by Steven on 3/6/2015.
 */
public class LibraryFile extends LibraryItem {
	LibraryFile(File file) {
		this.file = file;
	}

	@Override
	boolean onClickInvalidatesData() {
		return false;
	}

	@Override
	int getType() {
		return 1;
	}

	@Override
	String getSubtitle() {
		return null;
	}

	@Override
	int getTag() {
		return 0;
	}

	@Override
	FontAwesome.Icon getIcon() {
		return FontAwesome.Icon.faw_music;
	}
}
