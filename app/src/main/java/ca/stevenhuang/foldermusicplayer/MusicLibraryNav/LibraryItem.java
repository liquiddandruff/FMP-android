package ca.stevenhuang.foldermusicplayer.MusicLibraryNav;

import android.view.KeyEvent;
import android.view.View;

import com.mikepenz.iconics.typeface.FontAwesome;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Steven on 3/6/2015.
 */
public abstract class LibraryItem {
	File file;
	abstract int getType();

	File getFile() {
		return file;
	}

	File getParent() {
		return file.getParentFile();
	}

	String getTitle() {
		return file.getName();
	}
	String getSubtitle() {
		return file.getPath();
	}

	boolean onClickInvalidatesData() {
		return false;
	}

	boolean onKeyClicked(View view, int keyCode, KeyEvent event) {
		return false;
	}

	abstract int getTag();
	abstract FontAwesome.Icon getIcon();

	static ArrayList<LibraryItem> fromFileList (File fileList[]) {
		ArrayList<LibraryItem> items = new ArrayList<>(fileList.length);
		for(int i = 0; i < fileList.length; i++) {
			final File file = fileList[i];
			if(file.isFile()){
				items.add(new LibraryFile(file));
			} else if(file.isDirectory()) {
				items.add(new LibraryFolder(file));
			}
		}
		return items;
	}
}
