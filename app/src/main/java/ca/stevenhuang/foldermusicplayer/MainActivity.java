package ca.stevenhuang.foldermusicplayer;

import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.os.Environment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import com.mikepenz.aboutlibraries.Libs;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.typeface.FontAwesome;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import java.io.File;

import ca.stevenhuang.foldermusicplayer.MusicEqualizer.EqualizerFragment;
import ca.stevenhuang.foldermusicplayer.MusicLibraryNav.LibraryFragment;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class MainActivity extends ActionBarActivity {
    public static final int     DRAWER_MUSIC_ID = 10;
    public static final String  DRAWER_MUSIC_TAG = "TAG1";
    public static final int     DRAWER_SETTINGS_ID = 20;
    public static final String  DRAWER_SETTINGS_TAG = "TAG2";
    public static final int     DRAWER_INFO_ID = 30;
    public static final String  DRAWER_INFO_TAG = "TAG3";

	private static MediaMetadataRetriever mMetadata;
	private static IPlayer mPlayer;
	private Drawer.Result leftDrawer;
	private Drawer.Result rightDrawer;
	private File rootMusicDir;

	public void notifyResult(boolean played) {
		if(mPlayer != null) {
			if(played) {
				Crouton.makeText(this, "Now playing " + mPlayer.getPlayable(), Style.CONFIRM).show();
			} else {
				Crouton.makeText(this, "File " + mPlayer.getPlayable() + " cannot be played", Style.ALERT).show();
			}
		}
	}
	public void playFile(File filePath) {
		Crouton.cancelAllCroutons();
		Playable p = new Playable(filePath.getPath());
		mPlayer.setPlayable(p);
		new Thread(new Runnable() {
			@Override
			public void run() {
				boolean playing = mPlayer.play();
				notifyResult(playing);
				if(playing) {
					mMetadata.setDataSource(mPlayer.getPlayable().getPath());
					String s1 = mMetadata.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE);
					String s2 = mMetadata.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
					String s3 = mMetadata.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
					String s4 = mMetadata.extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE);
					String s5 = mMetadata.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
					debug("metadata %s,%s,%s,%s,%s", s1, s2, s3, s4, s5);
				}
			}
		}).start();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		debug("onCreate");
		setTheme(R.style.AppTheme_Dark);
		setContentView(R.layout.activity_main);

		final Toolbar toolbar = (Toolbar) findViewById(R.id.activity_main_toolbar);
		toolbar.setTitleTextColor(Color.WHITE);
		setSupportActionBar(toolbar);

		rootMusicDir = new File(Environment.getExternalStorageDirectory(), "Music");

		Bundle data = new Bundle(1);
		data.putString(Const.KEY_ROOT, rootMusicDir.getPath());

		FragmentTransaction fragTrans = getSupportFragmentManager().beginTransaction();
		final LibraryFragment fragment = new LibraryFragment();
		fragment.setOnFileSelectionListener(new LibraryFragment.OnFileSelectionListener() {
			@Override
			public void onFileSelection(File file) {
				playFile(file);
			}
		});
		fragment.setArguments(data);
		fragTrans.add(R.id.fragment_container, fragment, DRAWER_MUSIC_TAG);
		fragTrans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		fragTrans.commit();

		if(mPlayer == null) {
			debug("mPlayer is null, recreating...");
			mPlayer = new Player();
		}
		mMetadata  = new MediaMetadataRetriever();


		leftDrawer = new Drawer()
			.withActivity(this)
			.withToolbar(toolbar)
			.withDrawerGravity(Gravity.LEFT)
			.addDrawerItems(
				new PrimaryDrawerItem().withIdentifier(DRAWER_MUSIC_ID).withName("Home"),
				new DividerDrawerItem(),
				new SecondaryDrawerItem().withIdentifier(DRAWER_SETTINGS_ID).withName("Settings")
			)
			.withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id, IDrawerItem drawerItem) {
                    FragmentTransaction fragTrans = getSupportFragmentManager().beginTransaction();
                    switch(drawerItem.getIdentifier()) {
                        case DRAWER_MUSIC_ID: {
                            Bundle data = new Bundle(1);
                            data.putString(Const.KEY_ROOT, rootMusicDir.getPath());
                            final LibraryFragment fragment = new LibraryFragment();
                            fragment.setOnFileSelectionListener(new LibraryFragment.OnFileSelectionListener() {
                                @Override
                                public void onFileSelection(File file) {
                                    playFile(file);
                                }
                            });
                            fragment.setArguments(data);
                            fragTrans.replace(R.id.fragment_container, fragment, DRAWER_MUSIC_TAG);
                            fragTrans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                            fragTrans.commit();
                            break;
                        }
                        case DRAWER_SETTINGS_ID: {
                            final EqualizerFragment fragment = new EqualizerFragment();
                            fragTrans.replace(R.id.fragment_container, fragment, DRAWER_SETTINGS_TAG);
                            fragTrans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                            fragTrans.commit();
                            break;
                        }
                    }
				}
			})
			.build();

		rightDrawer = new Drawer()
			.withActivity(this)
			.withDrawerGravity(Gravity.RIGHT)
			.addDrawerItems(
				new PrimaryDrawerItem().withName("Volume"),
				new DividerDrawerItem(),
				new SecondaryDrawerItem().withName("Misc")
			)
			.withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id, IDrawerItem drawerItem) {
				}
			})
			.append(leftDrawer);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			if(leftDrawer != null && leftDrawer.isDrawerOpen()) {
				leftDrawer.closeDrawer();
				return true;
			} else if (rightDrawer != null && rightDrawer.isDrawerOpen()) {
				rightDrawer.closeDrawer();
				return true;
			}
		}
		BaseFragment f = (BaseFragment)getSupportFragmentManager().findFragmentByTag(DRAWER_MUSIC_TAG);
		return f != null && f.onKeyDown(keyCode, event);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Crouton.cancelAllCroutons();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_main, menu);
		menu.findItem(R.id.action_about).setIcon(new IconicsDrawable(this, FontAwesome.Icon.faw_info_circle).color(Color.WHITE).actionBarSize());
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_about) {
			new Libs.Builder()
					.withFields(R.string.class.getFields())
					.withAboutIconShown(true)
					.withAboutVersionShown(true)
					.withAboutDescription("Your alternative folder-based music player")
					.withActivityTitle(getString(R.string.action_about))
							//.withActivityTheme(R.style.MaterialDrawerTheme_ActionBar)
					.withLibraries("rxJava", "rxAndroid")
					.start(this);
			return true;
		}
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void debug(String s, Object ... args) {
		Log.d(this.getClass().getSimpleName(), String.format(s, args));
	}
}
