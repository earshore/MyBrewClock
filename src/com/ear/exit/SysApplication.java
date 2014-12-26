package com.ear.exit;

import java.util.LinkedList;
import java.util.List;
import android.app.Activity;
import android.app.Application;

/**
 * 一个类 用来结束所有后台activity
 * 
 * @author Administrator
 * 
 */
public class SysApplication extends Application {
	// 运用list来保存们每一个activity是关键
	private List<Activity> aList = new LinkedList<Activity>();
	// 为了实现每次使用该类时不创建新的对象而创建的静态对象
	private static SysApplication instance;

	// 构造方法
	private SysApplication() {
	}

	// 实例化一次
	public synchronized static SysApplication getInstance() {
		if (null == instance) {
			instance = new SysApplication();
		}
		return instance;
	}

	// add Activity
	public void addActivity(Activity activity) {
		aList.add(activity);
	}

	// 关闭每一个list内的activity
	public void exit() {
		try {
			for (Activity activity : aList) {
				if (activity != null)
					activity.finish();
			}
		} catch (Exception exc) {
			exc.printStackTrace();
		} finally {
			System.exit(0);
		}
	}

	// 杀进程
	public void onLowMemory() {
		super.onLowMemory();
		System.gc();
	}
}
