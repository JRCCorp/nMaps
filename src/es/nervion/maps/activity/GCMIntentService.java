package es.nervion.maps.activity;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;
import com.google.android.gms.gcm.GoogleCloudMessaging;


public class GCMIntentService extends IntentService
{
	private static final int NOTIF_ALERTA_ID = 1;

	public GCMIntentService() {
		super("834884443249");
	}

	@Override
	protected void onHandleIntent(Intent intent)
	{
		//Log.d("GCMIntent", "Maneja Mensaje GCM");
		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);

		String messageType = gcm.getMessageType(intent);
		if(intent.getExtras()!=null){
			Bundle extras = intent.getExtras();

			if (!extras.isEmpty())
			{
				//Log.d("GCMIntent", "GCM tiene informacion");
				if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType))
				{
					Log.d("GCMIntent", "GCM tiene MENSAJE: "+extras.getString("mensaje"));
					mostrarNotification(extras.getString("msg"));
				}
			}
			GCMBroadcastReceiver.completeWakefulIntent(intent);
		}

	}

	private void mostrarNotification(String msg)
	{
		NotificationManager mNotificationManager =
				(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

		NotificationCompat.Builder mBuilder =
				new NotificationCompat.Builder(this)
		.setSmallIcon(android.R.drawable.stat_sys_warning)
		.setContentTitle("Notificación GCM")
		.setContentText(msg);

		Intent notIntent =  new Intent(this, TabsActivity.class);
		PendingIntent contIntent = PendingIntent.getActivity(
				this, 0, notIntent, 0);

		mBuilder.setContentIntent(contIntent);

		mNotificationManager.notify(NOTIF_ALERTA_ID, mBuilder.build());
	}



}