package ca.stevenhuang.foldermusicplayer.MusicLibraryNav;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ca.stevenhuang.foldermusicplayer.MusicLibraryNav.LibraryItem.NavigationData;
import ca.stevenhuang.foldermusicplayer.BaseFragment;
import ca.stevenhuang.foldermusicplayer.Const;
import ca.stevenhuang.foldermusicplayer.MainActivity;
import ca.stevenhuang.foldermusicplayer.Player;
import ca.stevenhuang.foldermusicplayer.R;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

/**
 * Created by Steven on 3/5/2015.
 */
public class LibraryFragment extends BaseFragment implements LibraryAdapter.ILibraryItemOnClickListener {
	private LibraryAdapter mAdapter;
	private RecyclerView fileList;

	private File mRootDir;
	private File mCurrDir;
	private ArrayList<LibraryItem> mCurrDirFolders;
	private ArrayList<LibraryItem> mCurrDirFiles;
	private List<NavigationData> navigationBreadcrumb;
	private int numDirectoriesAboveDeepest = 0;
	private List<NavigationData> nowPlayingBreadcrumb;
	private ArrayList<LibraryItem> mCurrDirAdapterData;

	private LinearLayoutManager llm;
	private OnFileSelectionListener mOnFileSelectionListener;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle data = getArguments();
		String root =  data.getString(Const.KEY_ROOT);
		debug("onCreate, mybundle: %s %s      savedbundle: %s", data.toString(), root, savedInstanceState);
		final Activity activity = getActivity();
		mRootDir = new File(root);
		mCurrDir = mRootDir;

		llm = new LinearLayoutManager(getContext());
		if(mAdapter == null) {
			debug("mAdapter null, new adapter created");
			mCurrDirAdapterData = new ArrayList<>();
			mAdapter = new LibraryAdapter(activity, mCurrDirAdapterData, this);
		} else {
			debug("mAdapter not null, new adapter not created");
		}
		navigationBreadcrumb = new ArrayList<>();
		nowPlayingBreadcrumb = new ArrayList<>();
		// Push root
		changeDirTo(mRootDir);
		pushCurrentDir();
	}

	@Override
	public void onRowClick(View view, int position) {
		if (mCurrDirFolders != null && mCurrDirFiles != null && position >= 0) {
			File clicked;
			if (position < mCurrDirFolders.size()) {
				clicked = mCurrDirFolders.get(position).getFile();

				if (changeDirTo(clicked)) {
					debug("clicked: %s, num: %d", clicked.getName(), numDirectoriesAboveDeepest);
					pushCurrentDir();
				};

				llm.scrollToPosition(0);
			} else if (position - mCurrDirFolders.size() < mCurrDirFiles.size()) {
				int index = position - mCurrDirFolders.size();
				mAdapter.clearSelected();
				mAdapter.setSelected(mCurrDirFolders.size() + index, true);
				clicked = mCurrDirFiles.get(index).getFile();
				nowPlayingBreadcrumb.clear();
				for(NavigationData crumb : navigationBreadcrumb) {
					nowPlayingBreadcrumb.add(new NavigationData(crumb));
				}
				nowPlayingBreadcrumb.add(new NavigationData(clicked, -1, -1));
				if (mOnFileSelectionListener != null) {
					mOnFileSelectionListener.onFileSelection(clicked);
				}
			} else {
				return;
			}
			//Crouton.cancelAllCroutons();
			//Crouton.makeText((MainActivity) view.getContext(), clicked.getName(), Style.CONFIRM).show();
		}
	}

	@Override
	public boolean onRowLongClick(View view, int position) {
		File clicked;
		if (position < mCurrDirFolders.size()) {
			clicked = mCurrDirFolders.get(position).getFile();
		} else if (position - mCurrDirFolders.size() < mCurrDirFiles.size()) {
			clicked = mCurrDirFiles.get(position - mCurrDirFolders.size()).getFile();
		} else {
			return false;
		}
		Crouton.cancelAllCroutons();
		Crouton.makeText((MainActivity) view.getContext(), "LongClick: " + clicked.getName(), Style.CONFIRM).show();
		return true;
		//return false;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		debug("onCreateView");

		View view = inflater.inflate(R.layout.library_fragment, container, false);
		fileList = (RecyclerView)view.findViewById(R.id.fragment_main_listview);
		fileList.setHasFixedSize(true);
		fileList.setItemAnimator(null);
		fileList.setLayoutManager(llm);
		fileList.setAdapter(mAdapter);

		return view;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		debug("onKeyDown %d %s", keyCode, event.toString());
		if(keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			// navigate up
			if(mCurrDir != null && !mCurrDir.equals(mRootDir)) {
				File parentFile = mCurrDir.getParentFile();
				if(changeDirTo(parentFile)) {
					popPreviousDir();
					return true;
				}
			}
		}
		debug("returned false");
		return false;
	}

	public boolean changeDirTo(File dir) {
		if(dir == null || !dir.isDirectory()) {
			return false;
		}
		try {
			debug("Changing dir to %s", dir.getCanonicalPath());
		} catch (IOException e) {
			return false;
		}

		mCurrDir = dir;

		mCurrDirFolders = new ArrayList<>();
		mCurrDirFiles = new ArrayList<>();
		File[] contents = dir.listFiles();

		for(File file : contents) {
			if(file.isDirectory()) {
				mCurrDirFolders.add(new LibraryFolder(file));
			} else {
				if(Player.isExtensionPlayable(file)) {
					mCurrDirFiles.add(new LibraryFile(file));
				}
			}
		}

		mAdapter.clearSelected();
		Collections.sort(mCurrDirFolders, LibraryFile.Comparators.FILE);
		for(int i = 0; i < mCurrDirFolders.size(); i++) {
			File file = mCurrDirFolders.get(i).getFile();
			for(NavigationData item : nowPlayingBreadcrumb) {
				if(file.getAbsolutePath().equals(item.dir.getAbsolutePath())) {
					//debug("setting dir: " + file.getName() + " selected at " + Integer.toString(i));
					mAdapter.setSelected(i, true);
					break;
				}
			}
		}
		Collections.sort(mCurrDirFiles, LibraryFile.Comparators.FILE);
		for(int i = 0; i < mCurrDirFiles.size(); i++) {
			if(nowPlayingBreadcrumb.size() == 0) break;
			File file = mCurrDirFiles.get(i).getFile();
			File nowPlaying = nowPlayingBreadcrumb.get(nowPlayingBreadcrumb.size() - 1).dir;
			if(file.getAbsolutePath().equals(nowPlaying.getAbsolutePath())) {
				debug("setting dir: " + file.getName() + " selected at " + Integer.toString(i));
				mAdapter.setSelected(mCurrDirFolders.size() + i, true);
				break;
			}
		}
		mCurrDirAdapterData.clear();
		mCurrDirAdapterData.ensureCapacity(mCurrDirFolders.size() + mCurrDirFiles.size());
		mCurrDirAdapterData.addAll(mCurrDirFolders);
		mCurrDirAdapterData.addAll(mCurrDirFiles);
		mAdapter.notifyDataSetChanged();
		if(fileList != null) fileList.startLayoutAnimation();
		return true;
	}

	private void markSongInCurrentDir(int position) {

	}
	private void debugPrintBreadcrumb() {
		String tmp = "";
		for(int i = 0; i < navigationBreadcrumb.size(); i++) {
			NavigationData nav = navigationBreadcrumb.get(i);
			if(i == navigationBreadcrumb.size() -1- numDirectoriesAboveDeepest) {
				tmp += "!";
			}
			tmp += nav.dir.getName() + " -> ";
		}
		debug("bread: " + tmp);
	}

	private void pushCurrentDir() {
		boolean isNewCrumb = true;
		if(navigationBreadcrumb.size() > 0 && numDirectoriesAboveDeepest > 0) {
			NavigationData sameDepth = navigationBreadcrumb.get(navigationBreadcrumb.size() - 1 - numDirectoriesAboveDeepest + 1);
			// if dirs don't match then this is a new crumb
			isNewCrumb = !sameDepth.dir.equals(mCurrDir);
		}
		debug("dir push: %s, num: %d", mCurrDir.getName(), numDirectoriesAboveDeepest);
		debug("isnewcrm: " + isNewCrumb);

		int itemPos = llm.findFirstVisibleItemPosition();
		View itemView = llm.findViewByPosition(itemPos);
		if(itemView == null) {
			navigationBreadcrumb.add(new NavigationData(mCurrDir, 0, 0));
			return;
		}
		int itemOffset = itemView.getTop();
		NavigationData crumb = new NavigationData(mCurrDir, itemPos, itemOffset);
		if(isNewCrumb) {
			while(numDirectoriesAboveDeepest > 0) {
				NavigationData temp = navigationBreadcrumb.get(navigationBreadcrumb.size() - 1);
				debug("removed: " + temp.dir.getName());
				numDirectoriesAboveDeepest--;
				navigationBreadcrumb.remove(navigationBreadcrumb.size() - 1);
			}
			navigationBreadcrumb.add(crumb);
		} else {
			numDirectoriesAboveDeepest--;
		}
		debugPrintBreadcrumb();
	}

	private void popPreviousDir() {
		NavigationData prev = navigationBreadcrumb.get(navigationBreadcrumb.size() - 1 - numDirectoriesAboveDeepest);
		numDirectoriesAboveDeepest++;
		debug("dir pop: %s, num: %d", prev.dir.getName(), numDirectoriesAboveDeepest);
		debugPrintBreadcrumb();
		llm.scrollToPositionWithOffset(prev.firstVisibleItemPosition, prev.getFirstVisibleItemPositionTopOffset);
	}

	public void setOnFileSelectionListener(OnFileSelectionListener listener) {
		mOnFileSelectionListener = listener;
	}

	public interface OnFileSelectionListener {
		abstract public void onFileSelection(File file);
	}

	private void debug(String s, Object ... args) {
		Log.d(this.getClass().getSimpleName(), String.format(s, args));
	}
}
