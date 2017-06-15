package course.lab.dailyselfie;

import java.text.DateFormat;
import java.util.Date;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;

/**
 * setup alarm and start the alarm after reboot
 *
 */
public class AlarmBootReceiver extends BroadcastReceiver {

	private static final long ALARM_TIME_INTERVAL = 120000;

	@Override
	public void onReceive(Context context, Intent intent) {
		// Log receipt of the Intent with timestamp
		Log.i(AlarmBootReceiver.class.getName(), "Logging alarm at:"
				+ DateFormat.getDateTimeInstance().format(new Date()));
		setupAlarm(context);

	}

	public static void setupAlarm(Context context) {
		Log.i(AlarmBootReceiver.class.getName(), "in setup alarm module");
		Intent alarmIntent = new Intent(context,
				AlarmNotificationReceiver.class);
		PendingIntent mPendingIntent = PendingIntent.getBroadcast(context, 0,
				alarmIntent, 0);

		AlarmManager manager = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		manager.setRepeating(AlarmManager.ELAPSED_REALTIME,
				SystemClock.elapsedRealtime(), ALARM_TIME_INTERVAL,
				mPendingIntent);
	}
}
