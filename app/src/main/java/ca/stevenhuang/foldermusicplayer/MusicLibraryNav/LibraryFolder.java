package ca.stevenhuang.foldermusicplayer.MusicLibraryNav;

import android.view.KeyEvent;
import android.view.View;

import com.mikepenz.iconics.typeface.FontAwesome;

import java.io.File;

/**
 * Created by Steven on 3/6/2015.
 */
public class LibraryFolder extends LibraryItem {
	LibraryFolder(File file) {
		this.file = file;
	}

	@Override
	boolean onClickInvalidatesData() {
		return true;
	}

	@Override
	boolean onKeyClicked(View view, int keyCode, KeyEvent event) {
		return false;
	}

	@Override
	int getType() {
		return 0;
	}

	@Override
	String getSubtitle() {
		return String.valueOf(file.getPath());
	}

	@Override
	int getTag() {
		return 0;
	}

	@Override
	FontAwesome.Icon getIcon() {
		return FontAwesome.Icon.faw_folder;
	}
}
