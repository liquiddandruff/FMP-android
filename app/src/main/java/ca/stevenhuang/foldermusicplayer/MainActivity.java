package ca.stevenhuang.foldermusicplayer;

import android.graphics.Color;
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

import ca.stevenhuang.foldermusicplayer.MusicLibraryNav.LibraryFragment;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class MainActivity extends ActionBarActivity {
	public enum Section {
		MUSIC_ROOT(10, "TAG1"),
		INFO(20, "TAG2"),
		SETTINGS(30, "TAG3");
		public final int id;
		public final String tag;
		private Section(int id, String tag) {
			this.id = id;
			this.tag = tag;
		}
	}
	private static IPlayer mPlayer;
	private Drawer.Result leftDrawer;
	private Drawer.Result rightDrawer;
	public File rootMusicDir;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		final Toolbar toolbar = (Toolbar) findViewById(R.id.activity_main_toolbar);
		toolbar.setTitleTextColor(Color.WHITE);
		setSupportActionBar(toolbar);

		FragmentTransaction fragTrans = getSupportFragmentManager().beginTransaction();
		final BaseFragment libNav = new LibraryFragment();
		fragTrans.add(R.id.fragment_container, libNav, Section.MUSIC_ROOT.tag);
		fragTrans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		fragTrans.commit();

		if(mPlayer == null)
			mPlayer = new Player();

		rootMusicDir = new File(Environment.getExternalStorageDirectory(), "Music");
		File fileList[] = rootMusicDir.listFiles();
		for(File file : fileList) {
			Log.d("4242", file.toString());
		}
		File filePath = new File(rootMusicDir, "a.mp3");

		IPlayable test = new Playable(filePath);
		if(!mPlayer.setPlayable(test)) {
			Crouton.makeText(this, "File " + filePath + " is not a playable file type", Style.ALERT).show();
		}
		if(!mPlayer.play()) {
			Crouton.makeText(this, "File " + filePath + " cannot be played", Style.ALERT).show();
		}

		leftDrawer = new Drawer()
			.withActivity(this)
			.withToolbar(toolbar)
			.withDrawerGravity(Gravity.LEFT)
			.addDrawerItems(
				new PrimaryDrawerItem().withIdentifier(Section.MUSIC_ROOT.id).withName("Home"),
				new DividerDrawerItem(),
				new SecondaryDrawerItem().withIdentifier(Section.SETTINGS.id).withName("Settings")
			)
			.withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id, IDrawerItem drawerItem) {
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
		BaseFragment f = (BaseFragment)getSupportFragmentManager().findFragmentByTag(Section.MUSIC_ROOT.tag);
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
}
