package android.content;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.IntentSender.SendIntentException;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

public class Context {

	public void unregisterReceiver(BroadcastReceiver arg0) {
		// TODO Auto-generated method stub
		
	}
	
	 
	public void unbindService(ServiceConnection arg0) {
		// TODO Auto-generated method stub
		
	}
	
	 
	public boolean stopService(Intent arg0) {
		// TODO Auto-generated method stub
		return false;
	}
	
	 
	public ComponentName startService(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	 
	public void startIntentSender(IntentSender arg0, Intent arg1, int arg2,
			int arg3, int arg4) throws SendIntentException {
		// TODO Auto-generated method stub
		
	}
	
	 
	public boolean startInstrumentation(ComponentName arg0, String arg1,
			Bundle arg2) {
		// TODO Auto-generated method stub
		return false;
	}
	
	 
	public void startActivity(Intent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	 
	@Deprecated
	public void setWallpaper(InputStream arg0) throws IOException {
		// TODO Auto-generated method stub
		
	}
	
	 
	@Deprecated
	public void setWallpaper(Bitmap arg0) throws IOException {
		// TODO Auto-generated method stub
		
	}
	
	 
	public void setTheme(int arg0) {
		// TODO Auto-generated method stub
		
	}
	
	 
	public void sendStickyOrderedBroadcast(Intent arg0, BroadcastReceiver arg1,
			Handler arg2, int arg3, String arg4, Bundle arg5) {
		// TODO Auto-generated method stub
		
	}
	
	 
	public void sendStickyBroadcast(Intent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	 
	public void sendOrderedBroadcast(Intent arg0, String arg1,
			BroadcastReceiver arg2, Handler arg3, int arg4, String arg5,
			Bundle arg6) {
		// TODO Auto-generated method stub
		
	}
	
	 
	public void sendOrderedBroadcast(Intent arg0, String arg1) {
		// TODO Auto-generated method stub
		
	}
	
	 
	public void sendBroadcast(Intent arg0, String arg1) {
		// TODO Auto-generated method stub
		
	}
	
	 
	public void sendBroadcast(Intent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	 
	public void revokeUriPermission(Uri arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}
	
	 
	public void removeStickyBroadcast(Intent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	 
	public Intent registerReceiver(BroadcastReceiver arg0, IntentFilter arg1,
			String arg2, Handler arg3) {
		// TODO Auto-generated method stub
		return null;
	}
	
	 
	public Intent registerReceiver(BroadcastReceiver arg0, IntentFilter arg1) {
		// TODO Auto-generated method stub
		return null;
	}
	
	 
	@Deprecated
	public Drawable peekWallpaper() {
		// TODO Auto-generated method stub
		return null;
	}
	
	 
	public SQLiteDatabase openOrCreateDatabase(String arg0, int arg1,
			CursorFactory arg2) {
		// TODO Auto-generated method stub
		return null;
	}
	
	 
	public FileOutputStream openFileOutput(String arg0, int arg1)
			throws FileNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}
	
	 
	public FileInputStream openFileInput(String arg0)
			throws FileNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}
	
	 
	public void grantUriPermission(String arg0, Uri arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}
	
	 
	@Deprecated
	public int getWallpaperDesiredMinimumWidth() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	 
	@Deprecated
	public int getWallpaperDesiredMinimumHeight() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	 
	@Deprecated
	public Drawable getWallpaper() {
		// TODO Auto-generated method stub
		return null;
	}
	
	 
	public Theme getTheme() {
		// TODO Auto-generated method stub
		return null;
	}
	
	 
	public Object getSystemService(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	 
	public File getSharedPrefsFile(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	 
	public SharedPreferences getSharedPreferences(String arg0, int arg1) {
		// TODO Auto-generated method stub
		return null;
	}
	
	 
	public Resources getResources() {
		// TODO Auto-generated method stub
		return null;
	}
	
	 
	public String getPackageResourcePath() {
		// TODO Auto-generated method stub
		return null;
	}
	
	 
	public String getPackageName() {
		// TODO Auto-generated method stub
		return null;
	}
	
	 
	public PackageManager getPackageManager() {
		// TODO Auto-generated method stub
		return null;
	}
	
	 
	public String getPackageCodePath() {
		// TODO Auto-generated method stub
		return null;
	}
	
	 
	public Looper getMainLooper() {
		// TODO Auto-generated method stub
		return null;
	}
	
	 
	public File getFilesDir() {
		// TODO Auto-generated method stub
		return null;
	}
	
	 
	public File getFileStreamPath(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	 
	public File getExternalFilesDir(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	 
	public File getExternalCacheDir() {
		// TODO Auto-generated method stub
		return null;
	}
	
	 
	public File getDir(String arg0, int arg1) {
		// TODO Auto-generated method stub
		return null;
	}
	
	 
	public File getDatabasePath(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	 
	public ContentResolver getContentResolver() {
		// TODO Auto-generated method stub
		return null;
	}
	
	 
	public ClassLoader getClassLoader() {
		// TODO Auto-generated method stub
		return null;
	}
	
	 
	public File getCacheDir() {
		// TODO Auto-generated method stub
		return null;
	}
	
	 
	public AssetManager getAssets() {
		// TODO Auto-generated method stub
		return null;
	}
	
	 
	public ApplicationInfo getApplicationInfo() {
		// TODO Auto-generated method stub
		return null;
	}
	
	 
	public Context getApplicationContext() {
		// TODO Auto-generated method stub
		return null;
	}
	
	 
	public String[] fileList() {
		// TODO Auto-generated method stub
		return null;
	}
	
	 
	public void enforceUriPermission(Uri arg0, String arg1, String arg2,
			int arg3, int arg4, int arg5, String arg6) {
		// TODO Auto-generated method stub
		
	}
	
	 
	public void enforceUriPermission(Uri arg0, int arg1, int arg2, int arg3,
			String arg4) {
		// TODO Auto-generated method stub
		
	}
	
	 
	public void enforcePermission(String arg0, int arg1, int arg2, String arg3) {
		// TODO Auto-generated method stub
		
	}
	
	 
	public void enforceCallingUriPermission(Uri arg0, int arg1, String arg2) {
		// TODO Auto-generated method stub
		
	}
	
	 
	public void enforceCallingPermission(String arg0, String arg1) {
		// TODO Auto-generated method stub
		
	}
	
	 
	public void enforceCallingOrSelfUriPermission(Uri arg0, int arg1,
			String arg2) {
		// TODO Auto-generated method stub
		
	}
	
	 
	public void enforceCallingOrSelfPermission(String arg0, String arg1) {
		// TODO Auto-generated method stub
		
	}
	
	 
	public boolean deleteFile(String arg0) {
		// TODO Auto-generated method stub
		return false;
	}
	
	 
	public boolean deleteDatabase(String arg0) {
		// TODO Auto-generated method stub
		return false;
	}
	
	 
	public String[] databaseList() {
		// TODO Auto-generated method stub
		return null;
	}
	
	 
	public Context createPackageContext(String arg0, int arg1)
			throws NameNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}
	
	 
	@Deprecated
	public void clearWallpaper() throws IOException {
		// TODO Auto-generated method stub
		
	}
	
	 
	public int checkUriPermission(Uri arg0, String arg1, String arg2, int arg3,
			int arg4, int arg5) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	 
	public int checkUriPermission(Uri arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	 
	public int checkPermission(String arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	 
	public int checkCallingUriPermission(Uri arg0, int arg1) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	 
	public int checkCallingPermission(String arg0) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	 
	public int checkCallingOrSelfUriPermission(Uri arg0, int arg1) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	 
	public int checkCallingOrSelfPermission(String arg0) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	 
	public boolean bindService(Intent arg0, ServiceConnection arg1, int arg2) {
		// TODO Auto-generated method stub
		return false;
	}

}
