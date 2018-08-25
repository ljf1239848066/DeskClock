package com.lxzh123.deskclock;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;
import android.widget.RemoteViews;

public class LxzhAppWidgetProvider extends AppWidgetProvider{
	
	//格式化时间工具
	private static SimpleDateFormat dateFm = new SimpleDateFormat("hh:mm:ss");
	public static boolean IsScreenOn=true;
	//当桌面组件删除时调用
	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		super.onDeleted(context, appWidgetIds);
	}

	//当AppWidgetProvider提供的最后一个组件删除时调用
	@Override
	public void onDisabled(Context context) {
		super.onDisabled(context);
	}

	//当AppWidgetProvider提供的第一个组件创建时调用
	@Override
	public void onEnabled(Context context) {
		super.onEnabled(context);
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);
		if(ScreenObserver.ScreenState){
			final RemoteViews views = new RemoteViews(context.getPackageName(),R.layout.appwidget);
			new Thread(){
	
				@Override
				public void run() {
					super.run();
					while(true){
						if(IsScreenOn){
							updateView(views);
						}
						SystemClock.sleep(100);
					}
				}
			}.start();
		}
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		if(ScreenObserver.ScreenState){
			//启动服务
			context.startService(new Intent(context, LxzhService.class));
		}
		super.onUpdate(context, appWidgetManager, appWidgetIds);
	}
	
	private static void updateView(RemoteViews views){
		String time = dateFm.format(new Date());//格式化时间
		String date=new LunarCalendar().getFullLunarName();
//		String date=new LunarCalendar().toLongDateString();//调用LunarCalendar类中的方法返回日期，格式为：(阴历)xx月xx日 周x xx月xx日，也可以自己定义
		views.setTextViewText(R.id.widget_time, time);//设置时间
//		Log.d("设置时间", time);
		views.setTextViewText(R.id.widget_date, date);//设置日期
//		Log.d("设置日期", date);
	}
	
	//服务中调用次方法
	public static RemoteViews updateTime(Context context){
		RemoteViews views = new RemoteViews(context.getPackageName(),R.layout.appwidget);
		updateView(views);
		return views;
	}
	/**
	 * 桌面组件布局在layout文件夹下，组件描述文件在xml文件夹下
	 */
}
