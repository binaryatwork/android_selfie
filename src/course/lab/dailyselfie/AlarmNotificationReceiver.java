package course.lab.dailyselfie;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

/**
 * notification of the alarm.
 * if it is clicked, an intent will be sent to start the selfie app
 *
 */
public class AlarmNotificationReceiver extends BroadcastReceiver {

	private static final int MY_SELFIE_NOTIFICATION_ID = 100;

	@Override
	public void onReceive(Context context, Intent intent) {
		ActivityManager activityManager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> tasks = activityManager
				.getRunningTasks(Integer.MAX_VALUE);

		if (!tasks.get(0).topActivity.getPackageName().toString()
				.equalsIgnoreCase(context.getPackageName().toString())) {
			long[] vibratePattern = { 0, 200, 200, 300 };
			Intent notificationIntent = new Intent(context,
					SelfieViewListActivity.class);
			PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
					notificationIntent, Intent.FLAG_ACTIVITY_NEW_TASK);
			Notification.Builder notificationBuilder = new Notification.Builder(
					context)
					.setTicker("It is time to take a selfie")
					.setSmallIcon(android.R.drawable.ic_dialog_alert)
					.setAutoCancel(true)
					.setContentIntent(contentIntent)
					.setVibrate(vibratePattern)
					.setContent(
							new RemoteViews(
									"course.lab.dailyselfie",
									course.lab.dailyselfie.R.layout.custom_notification));
			// Pass the Notification to the NotificationManager:
			NotificationManager mNotificationManager = (NotificationManager) context
					.getSystemService(Context.NOTIFICATION_SERVICE);
			mNotificationManager.notify(MY_SELFIE_NOTIFICATION_ID,
					notificationBuilder.build());
		}

	}

}
