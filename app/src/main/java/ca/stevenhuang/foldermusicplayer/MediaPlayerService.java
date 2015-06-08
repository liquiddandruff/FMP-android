package ca.stevenhuang.foldermusicplayer;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

/**
 * Created by Steven on 3/8/2015.
 */
public class MediaPlayerService extends Service {
	private final IBinder mBinder = new BackgroundPlayerBinder();
	private static IPlayer mPlayer;

	public class BackgroundPlayerBinder extends Binder {
		MediaPlayerService getService() {
			debug("getService()");
			return MediaPlayerService.this;
		}
	}

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
		debug("onBind()");
		return mBinder;
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
			PendingIntent toMainAppPI = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

			Intent previousIntent = new Intent(this, MediaPlayerService.class);
			previousIntent.setAction(Const.ACTION.PREV);
			PendingIntent prevPI = PendingIntent.getService(this, 0, previousIntent, 0);

			Intent playIntent = new Intent(this, MediaPlayerService.class);
			playIntent.setAction(Const.ACTION.PLAY);
			PendingIntent playPI = PendingIntent.getService(this, 0, playIntent, 0);

			Intent nextIntent = new Intent(this, MediaPlayerService.class);
			nextIntent.setAction(Const.ACTION.NEXT);
			PendingIntent nextPI = PendingIntent.getService(this, 0, nextIntent, 0);

			Intent stopIntent = new Intent(this, MediaPlayerService.class);
			stopIntent.setAction(Const.ACTION.STOP_FOREGROUND);
			PendingIntent stopPI = PendingIntent.getService(this, 0, stopIntent, 0);

			RemoteViews smallRV = new RemoteViews(getPackageName(), R.layout.background_service_small);
			smallRV.setOnClickPendingIntent(R.id.service_panel, toMainAppPI);
			smallRV.setOnClickPendingIntent(R.id.service_prev, prevPI);
			smallRV.setOnClickPendingIntent(R.id.service_play, playPI);
			smallRV.setOnClickPendingIntent(R.id.service_next, nextPI);

			RemoteViews bigRV = new RemoteViews(getPackageName(), R.layout.background_service_big);
			bigRV.setOnClickPendingIntent(R.id.service_panel, toMainAppPI);
			bigRV.setOnClickPendingIntent(R.id.service_prev, prevPI);
			bigRV.setOnClickPendingIntent(R.id.service_play, playPI);
			bigRV.setOnClickPendingIntent(R.id.service_next, nextPI);
			bigRV.setOnClickPendingIntent(R.id.service_exit, stopPI);

			Notification notification = new NotificationCompat.Builder(this)
					.setContent(smallRV)
					//.setContentIntent(pendingIntent)
					//.setContentTitle("FMP")
					//.setContentText("My Music")
					.setTicker("FMP")
					//.setLargeIcon(icon)
					.setSmallIcon(R.drawable.ic_launcher)
					.setOngoing(true)
					//.addAction(android.R.drawable.ic_media_previous, "Prev", previousPI)
					//.addAction(android.R.drawable.ic_media_play, "Play", playPI)
					//.addAction(android.R.drawable.ic_delete, "Stop", pstopIntent).build();
					.build();
			//NotificationManager nm = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
			//nm.notify(Const.NOTIFICATION_ID.FOREGROUND_SERVICE, notification.build());

			notification.bigContentView = bigRV;
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

	// client methods
	public IPlayer getPlayer() {
		debug("getPlayer()");
		if (mPlayer == null) {
			mPlayer = new Player(getApplicationContext());
		}
		return mPlayer;
	}



	private void debug(String s, Object ... args) {
		Log.d(this.getClass().getSimpleName(), String.format(s, args));
	}
}
