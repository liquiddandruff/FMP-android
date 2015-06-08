package ca.stevenhuang.foldermusicplayer;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.os.Environment;
import android.os.IBinder;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import com.mikepenz.aboutlibraries.LibsBuilder;
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

public class MainActivity extends AppCompatActivity {
	public static final int DRAWER_MUSIC_ID = 10;
	public static final String DRAWER_MUSIC_TAG = "TAG1";
	public static final int DRAWER_SETTINGS_ID = 20;
	public static final String DRAWER_SETTINGS_TAG = "TAG2";
	public static final int DRAWER_INFO_ID = 30;
	public static final String DRAWER_INFO_TAG = "TAG3";

	private static MediaPlayerService mpService;
	private static boolean mBound;
	private static MediaMetadataRetriever mMetadata;
	private IPlayer mPlayer;
	private Drawer.Result leftDrawer;
	private Drawer.Result rightDrawer;
	private File rootMusicDir;

	public void notifyResult(boolean played) {
		if (mPlayer != null) {
			if (played) {
				Crouton.makeText(this, "Now playing " + mPlayer.getPlayable(), Style.CONFIRM).show();
			} else {
				Crouton.makeText(this, "File " + mPlayer.getPlayable() + " cannot be played", Style.ALERT).show();
			}
		}
	}

	public void playFile(File filePath) {
		Crouton.cancelAllCroutons();
		if (!mBound) {
			debug("Not bound");
			return;
		}

		Playable p = new Playable(filePath.getPath());
		mPlayer.setPlayable(p);
		new Thread(new Runnable() {
			@Override
			public void run() {
				boolean playing = mPlayer.play();
				notifyResult(playing);
				if (playing) {
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

		Intent startIntent = new Intent(MainActivity.this, MediaPlayerService.class);
		startIntent.setAction(Const.ACTION.START_FOREGROUND);
		startService(startIntent);
	}

	private ServiceConnection mConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			debug("Service connected");
			mBound = true;
			mpService = ((MediaPlayerService.BackgroundPlayerBinder)service).getService();
			mPlayer = mpService.getPlayer();
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			debug("Service disconnected");
			mpService = null;
			mBound = false;
		}
	};

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

		switchToLibraryFragment(getSupportFragmentManager().beginTransaction());

		//if (mPlayer == null) {
		//	debug("mPlayer is null, recreating...");
		//	mPlayer = new Player(getApplicationContext());
		//}
		mMetadata = new MediaMetadataRetriever();


		leftDrawer = new Drawer()
				.withActivity(this)
				.withToolbar(toolbar)
				.withDrawerWidthDp(255)
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
						switch (drawerItem.getIdentifier()) {
							case DRAWER_MUSIC_ID: {
								switchToLibraryFragment(fragTrans);
								break;
							}
							case DRAWER_SETTINGS_ID: {
								switchToEqualizerFragment(fragTrans);
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

		Intent bindIntent = new Intent(MainActivity.this, MediaPlayerService.class);
		//bindIntent.setAction(Const.ACTION.START_FOREGROUND);
		//startService(bindIntent);
		bindService(bindIntent, mConnection, Context.BIND_AUTO_CREATE);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			if (leftDrawer != null && leftDrawer.isDrawerOpen()) {
				leftDrawer.closeDrawer();
				return true;
			} else if (rightDrawer != null && rightDrawer.isDrawerOpen()) {
				rightDrawer.closeDrawer();
				return true;
			}
		}
		BaseFragment f = (BaseFragment) getSupportFragmentManager().findFragmentByTag(DRAWER_MUSIC_TAG);
		return f != null && f.onKeyDown(keyCode, event);
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
			new LibsBuilder()
					.withFields(R.string.class.getFields())
					.withAboutIconShown(true)
					.withAboutVersionShown(true)
					.withAboutDescription("Your alternative folder-based music player")
					.withActivityTitle(getString(R.string.action_about))
					.withActivityTheme(R.style.AppTheme_Dark)
					.withLibraries("rxJava", "rxAndroid")
					.start(this);
			return true;
		}
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void switchToLibraryFragment(FragmentTransaction fragTrans) {
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
		fragTrans.replace(R.id.fragment_container, fragment, DRAWER_MUSIC_TAG)
				.commit();
	}

	public void switchToEqualizerFragment(FragmentTransaction fragTrans) {
		final EqualizerFragment fragment = new EqualizerFragment();
		fragTrans.replace(R.id.fragment_container, fragment, DRAWER_SETTINGS_TAG)
				.commit();
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (mBound) {
			mBound = false;
			unbindService(mConnection);
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Crouton.cancelAllCroutons();
	}

	private void debug(String s, Object... args) {
		Log.d(this.getClass().getSimpleName(), String.format(s, args));
	}
}
