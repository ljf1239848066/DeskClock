package com.lxzh123.deskclock;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;

//服务须在AndroidManifest中声明
public class LxzhService extends Service {
	private static final String TAG = "LxzhService";
	private ScreenObserver mScreenObserver;
	private boolean isScreenOn = true;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onRebind(Intent intent) {
		super.onRebind(intent);
	}

	@Override
	public void onStart(final Intent intent, int startId) {
		Log.i(TAG, "start service");
		mScreenObserver = new ScreenObserver(this);
		mScreenObserver.requestScreenStateUpdate(new ScreenObserver.ScreenStateListener() {
			public void onScreenOn() {
				doSomethingOnScreenOn(intent);
			}

			public void onScreenOff() {
				doSomethingOnScreenOff();
			}
		});
		super.onStart(intent, startId);
	}

	private void doSomethingOnScreenOn(Intent intent) {
		isScreenOn = true;
		Log.i(TAG, "Screen is on");
		LxzhAppWidgetProvider.IsScreenOn=isScreenOn;
		AppWidgetManager manager = AppWidgetManager.getInstance(this);

		// 调用LxzhAppWidgetProvider.updateTime方法更新组件
		RemoteViews updateView = LxzhAppWidgetProvider.updateTime(this);

		if (updateView != null) {
			// 更新桌面组件
			manager.updateAppWidget(new ComponentName(this,
					LxzhAppWidgetProvider.class), updateView);
		}

		// 设置下次执行时间,每秒刷新一次
		long now = System.currentTimeMillis();
		long updateMilis = 1000;

		PendingIntent pendingIntent = PendingIntent.getService(this, 0,
				intent, 0);
		// Schedule alarm, and force the device awake for this update
		AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		alarmManager.set(AlarmManager.RTC_WAKEUP, now + updateMilis,
				pendingIntent);

		// 停止服务
		stopSelf();
	}

	private void doSomethingOnScreenOff() {
		isScreenOn = false;
		LxzhAppWidgetProvider.IsScreenOn=isScreenOn;
		Log.i(TAG, "Screen is off");
	}

	@Override
	public boolean onUnbind(Intent intent) {
		return super.onUnbind(intent);
	}

	@Override
	public void onDestroy() {
		// unregisterReceiver(receiver);
		mScreenObserver.stopScreenStateUpdate();
		super.onDestroy();
	}
}
