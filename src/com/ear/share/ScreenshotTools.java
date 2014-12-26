package com.ear.share;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Environment;
import android.os.StatFs;
import android.view.View;
import android.widget.Toast;

public class ScreenshotTools {
	/***
	 * @author Johnson
	 * 
	 * */
	public static long minSizeSDcard = 50;
	public static String filePath = Environment.getExternalStorageDirectory()
			+ "/BrewClock";
	private static SimpleDateFormat sdf = new SimpleDateFormat(
			"yyyy-MM-dd_HH-mm-ss", Locale.US);
	@SuppressLint("SdCardPath")
	public static String fileName = sdf.format(new Date()) + ".png";
	public static String detailPath = filePath + File.separator + fileName;
	public static final int SEND_EMAIL = 1;

	// public static String detailPath="/sdcard/FjbiCache/currentTime.png";
	/**
	 * 调用系统程序发送邮件
	 * 
	 * @author Johnson
	 * 
	 * */
	private static void sendEmail(Context context, String[] to, String subject,
			String body, String path) {
		Intent email = new Intent(android.content.Intent.ACTION_SEND);
		if (to != null) {
			email.putExtra(android.content.Intent.EXTRA_EMAIL, to);
		}
		if (subject != null) {
			email.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);
		}
		if (body != null) {
			email.putExtra(android.content.Intent.EXTRA_TEXT, body);
		}
		if (path != null) {
			/*
			 * 用email.setType("image/png");或者email.setType(
			 * "application/octet-stream"); 都不会影响邮件的发送
			 * 为什么email.setType("image/png"
			 * );而不用email.setType("application/octet-stream"); ?
			 * 因为在开发中发现setType("image/png")，系统会同时给你调用彩信，邮件，等.....
			 */
			File file = new File(path);
			email.putExtra(android.content.Intent.EXTRA_STREAM,
					Uri.fromFile(file));
			email.setType("image/png");
		}
		context.startActivity(Intent.createChooser(email,
				"Choose a way to share:"));
	}

	/**
	 * 获取指定Activity的截屏，保存到png文件
	 * 
	 * @author Johnson
	 * **/
	@SuppressWarnings("deprecation")
	private static Bitmap takeScreenShot(Activity activity) {
		// View是你需要截图的View
		View view = activity.getWindow().getDecorView();
		view.setDrawingCacheEnabled(true);
		view.buildDrawingCache();
		Bitmap bmap = view.getDrawingCache();
		// 获取状态栏高度
		Rect frame = new Rect();
		activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
		int statusBarHeight = frame.top;
		System.out.println(statusBarHeight);
		// 获取屏幕长和高
		int width = activity.getWindowManager().getDefaultDisplay().getWidth();
		int height = activity.getWindowManager().getDefaultDisplay()
				.getHeight();
		// 去掉标题栏
		// Bitmap b = Bitmap.createBitmap(bmap, 0, 25, 320, 455);
		Bitmap bitmap = Bitmap.createBitmap(bmap, 0, statusBarHeight, width,
				height - statusBarHeight);
		view.destroyDrawingCache();
		return bitmap;
	}

	/**
	 * 截图保存
	 * 
	 * @author Johnson
	 * **/
	private static void savePic(Bitmap bmap, String filePath, String fileName) {
		File file = new File(filePath);
		if (!file.exists()) {
			file.mkdir();
		}
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(filePath + File.separator + fileName);
			if (null != fos) {
				bmap.compress(Bitmap.CompressFormat.PNG, 90, fos);
				fos.flush();
				fos.close();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * 截屏并发送邮件
	 * 
	 * @author Johnson
	 * **/
	public static void takeScreenShotToEmail(Context context, Activity a) {
		if (getAvailableSDcard(context)) {
			savePic(takeScreenShot(a), filePath, fileName);
			// selectDialog(context);
			sendEmail(context, null, null, null, detailPath);
		}
	}

	/***
	 * Sd判断SD卡是否可用
	 * 
	 * @author Johnson minSizeSDcard>50kb
	 * */
	@SuppressWarnings("deprecation")
	public static boolean getAvailableSDcard(Context context) {
		boolean sdCardExist = Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
		System.out.println("+++" + sdCardExist);
		if (sdCardExist) {
			File path = Environment.getExternalStorageDirectory();
			StatFs stat = new StatFs(path.getPath());
			long blockSize = stat.getBlockSize();
			long availableBlocks = stat.getAvailableBlocks();
			long sdCardSize = (availableBlocks * blockSize) / 1024;// KB值
			if (sdCardSize > minSizeSDcard) {
				System.out.println("SDcardSize:::" + minSizeSDcard + "KB");
				return true;
			} else {
				Toast.makeText(context, "Insufficient SD storage！",
						Toast.LENGTH_SHORT).show();
			}
		} else {
			Toast.makeText(context, "Insert a SD card before share.",
					Toast.LENGTH_SHORT).show();
		}
		return false;
	}
}