package com.ear.exit;

import android.app.Activity;
import android.app.Application;

import java.util.LinkedList;
import java.util.List;

/**
 * һ���� �����������к�̨activity
 * 
 * @author Administrator
 * 
 */
public class SysApplication extends Application {
	// ����list��������ÿһ��activity�ǹؼ�
	private List<Activity> aList = new LinkedList<Activity>();
	// Ϊ��ʵ��ÿ��ʹ�ø���ʱ�������µĶ�����ľ�̬����
	private static SysApplication instance;

	// ���췽��
	private SysApplication() {
	}

	// ʵ��һ��
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

	// �ر�ÿһ��list�ڵ�activity
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

	// ɱ���
	public void onLowMemory() {
		super.onLowMemory();
		System.gc();
	}
}
