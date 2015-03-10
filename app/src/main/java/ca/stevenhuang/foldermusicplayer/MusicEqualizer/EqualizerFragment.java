package ca.stevenhuang.foldermusicplayer.MusicEqualizer;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ca.stevenhuang.foldermusicplayer.BaseFragment;
import ca.stevenhuang.foldermusicplayer.R;

/**
 * Created by steven on 10/03/15.
 */
public class EqualizerFragment extends BaseFragment {
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        debug("onCreateView");
        View view = inflater.inflate(R.layout.equalizer_fragment, container, false);
        return view;
    }

    private void debug(String s, Object ... args) {
        Log.d(this.getClass().getSimpleName(), String.format(s, args));
    }
}
