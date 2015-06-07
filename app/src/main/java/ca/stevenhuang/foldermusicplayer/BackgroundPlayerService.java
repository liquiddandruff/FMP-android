package ca.stevenhuang.foldermusicplayer;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.mikepenz.iconics.Iconics;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.typeface.FontAwesome;

/**
 * Created by Steven on 3/8/2015.
 */
public class BackgroundPlayerService extends Service {
	IPlayer mPlayer;

	@Override
	public void onCreate() {
		super.onCreate();
		mPlayer = new Player(getApplicationContext());
		debug("onCreate");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		debug("onDestroy");
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		debug("intent received: %s", intent.toString());
		if(intent.getAction().equals(Const.ACTION.START_FOREGROUND)) {
			debug("starting");
			Intent notificationIntent = new Intent(this, MainActivity.class);
			notificationIntent.setAction(Const.ACTION.MAIN_ACTIVITY);
			notificationIntent.addCategory(Intent.CATEGORY_HOME);
			//notificationIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
			PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

			Intent previousIntent = new Intent(this, BackgroundPlayerService.class);
			previousIntent.setAction(Const.ACTION.PREV);
			PendingIntent ppreviousIntent = PendingIntent.getService(this, 0, previousIntent, 0);

			Intent playIntent = new Intent(this, BackgroundPlayerService.class);
			playIntent.setAction(Const.ACTION.PLAY);
			PendingIntent pplayIntent = PendingIntent.getService(this, 0, playIntent, 0);

			Intent nextIntent = new Intent(this, BackgroundPlayerService.class);
			nextIntent.setAction(Const.ACTION.NEXT);
			PendingIntent pnextIntent = PendingIntent.getService(this, 0, nextIntent, 0);

			Intent stopIntent = new Intent(this, BackgroundPlayerService.class);
			stopIntent.setAction(Const.ACTION.STOP_FOREGROUND);
			PendingIntent pstopIntent = PendingIntent.getService(this, 0, stopIntent, 0);

			Bitmap icon = new IconicsDrawable(this, FontAwesome.Icon.faw_music).color(Color.BLACK).sizePx(120).toBitmap();

			Notification notification = new NotificationCompat.Builder(this)
					.setContentIntent(pendingIntent)
					.setContentTitle("FMP")
					.setContentText("My Music")
					.setTicker("FMP")
					.setLargeIcon(icon)
					.setSmallIcon(R.drawable.ic_launcher)
					.setOngoing(true)
					.addAction(android.R.drawable.ic_media_previous, "Prev", ppreviousIntent)
					.addAction(android.R.drawable.ic_media_play, "Play", pplayIntent)
					.addAction(android.R.drawable.ic_delete, "Stop", pstopIntent).build();
			//NotificationManager nm = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
			//nm.notify(Const.NOTIFICATION_ID.FOREGROUND_SERVICE, notification.build());

			startForeground(Const.NOTIFICATION_ID.FOREGROUND_SERVICE, notification);

		} else if (intent.getAction().equals(Const.ACTION.PREV)) {
			debug("Clicked prev");
		} else if (intent.getAction().equals(Const.ACTION.PLAY)) {
			debug("Clicked play");
		} else if (intent.getAction().equals(Const.ACTION.NEXT)) {
			debug("Clicked next");
		} else if (intent.getAction().equals(Const.ACTION.STOP_FOREGROUND)) {
			debug("Clicked stop");
			stopForeground(true);
			stopSelf();
		}
		//intent.getStringArrayExtra(Const.SERVICE_KEY_META)
		//return super.onStartCommand(intent, flags, startId);
		return START_STICKY;
	}

	private void debug(String s, Object ... args) {
		Log.d(this.getClass().getSimpleName(), String.format(s, args));
	}
}
