package ca.stevenhuang.foldermusicplayer;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;

/**
 * Created by Steven on 3/7/2015.
 */
public abstract class BaseFragment extends Fragment {
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return false;
	}
}
