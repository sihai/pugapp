/**
 * pug
 */
package com.ihome.pug;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.SystemClock;
import android.widget.Toast;

/**
 * 
 * @author sihai
 *
 */
public class LocationReporter {

	public static final int DEFAULT_INTERVAL = 60 * 5 * 1000;
	
	private Context context;
	private AlarmManager alarmManager;
	private LocationManager loctionManager;
	private Criteria criteria;
	private PendingIntent pendingIntent;
	private long interval = DEFAULT_INTERVAL;
	
	/**
	 * 
	 * @param loctionManager
	 * @param interval 
	 */
	public LocationReporter(Context context, AlarmManager alarmManager, LocationManager loctionManager, long interval) {
		this.context = context;
		this.alarmManager = alarmManager;
		this.loctionManager = loctionManager;
		this.interval = interval;
	}
	
	public boolean init() {
		criteria = new Criteria(); 
        criteria.setAccuracy(Criteria.ACCURACY_FINE);		//高精度 
        criteria.setAltitudeRequired(true);					//不要求海拔 
        criteria.setBearingRequired(true);					//不要求方位 
        criteria.setCostAllowed(true);						//允许有花费 
        criteria.setPowerRequirement(Criteria.POWER_LOW);	//低功耗 
        
        return true;
	}
	
	public boolean start() {
		Intent intent = new Intent(context, ReporterReceiver.class);
	    pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
	 
	    long firstime = SystemClock.elapsedRealtime();
	    alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, firstime, interval, pendingIntent);
	    
	    return true;
	}
	
	public boolean stop() {
		pendingIntent.cancel();
		return true;
	}
	
	private Location getLocation() {
		String provider = loctionManager.getBestProvider(criteria, true);
		return loctionManager.getLastKnownLocation(provider);
	}
	
	// 更新数据库的广播接收器
	public class ReporterReceiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
        	Location location = getLocation();
        	Toast.makeText(context, String.format("您的位置：latitude:%f,longitude:%f", location.getLatitude(), location.getLongitude()), Toast.LENGTH_LONG).show();
        }
	}
}
