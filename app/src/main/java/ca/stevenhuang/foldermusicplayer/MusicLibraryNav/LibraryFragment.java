package ca.stevenhuang.foldermusicplayer.MusicLibraryNav;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import ca.stevenhuang.foldermusicplayer.BaseFragment;
import ca.stevenhuang.foldermusicplayer.MainActivity;
import ca.stevenhuang.foldermusicplayer.R;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

/**
 * Created by Steven on 3/5/2015.
 */
public class LibraryFragment extends BaseFragment {
	private LibraryAdapter mAdapter;
	private ListView fileList;

	private File mRootDir;
	private File mCurrDir;
	private File[] mCurrDirFolders;
	private File[] mCurrDirFiles;
	private int mCurrDirMarker = -1;
	private ArrayList<LibraryItem> mCurrDirAdapterData;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		debug("onCreate");
		final Activity activity = getActivity();
		if(activity instanceof MainActivity) {
			mRootDir = ((MainActivity)activity).rootMusicDir;
		} else {
			mRootDir = Environment.getExternalStorageDirectory();
		}
		mCurrDir = mRootDir;

		if(mAdapter == null) {
			debug("mAdapter null, new adapter created");
			mCurrDirAdapterData = new ArrayList<>();
			mAdapter = new LibraryAdapter(activity, R.layout.library_row, mCurrDirAdapterData);
		} else {
			debug("mAdapter not null, new adapter not created");
		}
		changeDirTo(mRootDir);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		debug("onCreateView");

		View view = inflater.inflate(R.layout.library_fragment, container, false);
		fileList = (ListView)view.findViewById(R.id.fragment_main_listview);
		fileList.setAdapter(mAdapter);

		fileList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (mCurrDirFolders != null && mCurrDirFiles != null && position >= 0) {
					File clicked;
					if (position < mCurrDirFolders.length) {
						mCurrDirMarker = fileList.getFirstVisiblePosition();
						clicked = mCurrDirFolders[position];
						changeDirTo(clicked);
						scrollTo(0);
					} else if (position - mCurrDirFolders.length < mCurrDirFiles.length) {
						clicked = mCurrDirFiles[position - mCurrDirFolders.length];
					} else {
						return;
					}
					debug("Clicked %d : %s", position, clicked.getName());
					Crouton.cancelAllCroutons();
					Crouton.makeText((MainActivity) view.getContext(), clicked.getName(), Style.CONFIRM).show();
				}
			}
		});
		fileList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				File clicked;
				if (position < mCurrDirFolders.length) {
					mCurrDirMarker = fileList.getFirstVisiblePosition();
					clicked = mCurrDirFolders[position];
				} else if (position - mCurrDirFolders.length < mCurrDirFiles.length) {
					clicked = mCurrDirFiles[position - mCurrDirFolders.length];
				} else {
					return false;
				}
				Crouton.cancelAllCroutons();
				Crouton.makeText((MainActivity) view.getContext(), "LongClick: " + clicked.getName(), Style.CONFIRM).show();
				return true;
			}
		});
		return view;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		debug("onKeyDown %d %s", keyCode, event.toString());
		if(keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			// navigate up
			if(mCurrDir != null && !mCurrDir.equals(mRootDir)) {
				File parentFile = mCurrDir.getParentFile();
				changeDirTo(parentFile);
				scrollTo(mCurrDirMarker);
				return true;
			}
		}
		return false;
	}

	public boolean changeDirTo(File dir) {
		if(dir == null || !dir.isDirectory()) {
			return false;
		}
		debug("Changing dir to %s", dir.getPath());
		mCurrDir = dir;

		int numFolders = 0, numFiles = 0;
		File[] contents = dir.listFiles();
		for(File file : contents) {
			if(file.isDirectory()) {
				numFolders++;
			} else {
				numFiles++;
			}
		}
		mCurrDirFolders = new File[numFolders];
		mCurrDirFiles = new File[numFiles];
		for(int i = 0, folderIndex = 0, fileIndex = 0; i < numFolders + numFiles; i++) {
			final File currFile = contents[i];
			if(currFile.isDirectory()) {
				mCurrDirFolders[folderIndex++] = currFile;
			} else {
				mCurrDirFiles[fileIndex++] = currFile;
			}
		}
		Arrays.sort(mCurrDirFolders);
		Arrays.sort(mCurrDirFiles);
		ArrayList<LibraryItem> t1 = LibraryItem.fromFileList(mCurrDirFolders);
		ArrayList<LibraryItem> t2 = LibraryItem.fromFileList(mCurrDirFiles);
		mCurrDirAdapterData.clear();
		mCurrDirAdapterData.ensureCapacity(numFiles + numFolders);
		mCurrDirAdapterData.addAll(t1);
		mCurrDirAdapterData.addAll(t2);
		debug("size of data %s", mCurrDirAdapterData.size());
		mAdapter.notifyDataSetChanged();
		return true;
	}

	public void scrollTo(int pos) {
		fileList.setSelection(pos);
	}

	private void debug(String s, Object ... args) {
		Log.d(this.getClass().getSimpleName(), String.format(s, args));
	}
}
