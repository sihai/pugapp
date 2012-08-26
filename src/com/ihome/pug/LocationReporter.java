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
        criteria.setAccuracy(Criteria.ACCURACY_FINE);		//�߾��� 
        criteria.setAltitudeRequired(true);					//��Ҫ�󺣰� 
        criteria.setBearingRequired(true);					//��Ҫ��λ 
        criteria.setCostAllowed(true);						//�����л��� 
        criteria.setPowerRequirement(Criteria.POWER_LOW);	//�͹��� 
        
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
	
	// �������ݿ�Ĺ㲥������
	public class ReporterReceiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
        	Location location = getLocation();
        	Toast.makeText(context, String.format("����λ�ã�latitude:%f,longitude:%f", location.getLatitude(), location.getLongitude()), Toast.LENGTH_LONG).show();
        }
	}
}
