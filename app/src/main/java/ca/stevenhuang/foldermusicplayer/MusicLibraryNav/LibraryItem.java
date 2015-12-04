package ca.stevenhuang.foldermusicplayer.MusicLibraryNav;

import android.view.KeyEvent;
import android.view.View;

import com.mikepenz.fontawesome_typeface_library.FontAwesome;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;

import ca.stevenhuang.foldermusicplayer.Player;

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

	static ArrayList<LibraryItem> fromFileList (ArrayList<File> fileList) {
		ArrayList<LibraryItem> items = new ArrayList<>(fileList.size());
		for(int i = 0; i < fileList.size(); i++) {
			final File file = fileList.get(i);
			if(file.isDirectory()){
				items.add(new LibraryFolder(file));
			} else {
				items.add(new LibraryFile(file));
			}
		}
		return items;
	}

	public static class Comparators {
		public static Comparator<LibraryItem> FILE = new Comparator<LibraryItem>() {
			@Override
			public int compare(LibraryItem a, LibraryItem b) {
				return a.file.compareTo(b.file);
			}
		};
	}

	public static class NavigationData {
		public File dir;
		public int firstVisibleItemPosition;
		public int getFirstVisibleItemPositionTopOffset;

		public NavigationData(File dir, int firstVisibleItemPosition, int firstVisibleItemPositionTopOffset) {
			this.dir = dir;
			this.firstVisibleItemPosition = firstVisibleItemPosition;
			this.getFirstVisibleItemPositionTopOffset = firstVisibleItemPositionTopOffset;
		}

		public NavigationData(NavigationData other) {
			this.dir = other.dir;
			this.firstVisibleItemPosition = other.firstVisibleItemPosition;
			this.getFirstVisibleItemPositionTopOffset = other.getFirstVisibleItemPositionTopOffset;
		}
	}
}
