package es.nervion.maps.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;

import es.nervion.maps.activity.R;
import es.nervion.maps.activity.TabsActivity;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Toast;

public class SubirPosicionIntentService extends Service {


	public static final String BROADCAST_ACTION = "es.nervion.maps.ACTION";
	public static final String BROADCAST_MUERE = "es.nervion.maps.MUERE";
	private static final int TWO_MINUTES = 1000 * 60 * 2;
	public LocationManager locationManager;
	public MyLocationListener listener;
	public Location previousBestLocation = null;
	
	/* Notificaciones servicio	*/
	public NotificationManager mNotificationManager;
	private int NOTIFICACION = R.string.lblServicioPrueba;

	Intent intentRecibido;
	Intent intent;
	int counter = 0;

	@Override
	public void onCreate() {
		super.onCreate();
		intent = new Intent(BROADCAST_ACTION);      
		if (android.os.Build.VERSION.SDK_INT > 9) {
			//Cambiamos el StrictMode para permitir la ejecucion de un HTTPClient en un hilo
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}
		
		String ns = Context.NOTIFICATION_SERVICE;
		mNotificationManager = (NotificationManager) this.getSystemService(ns);
		mostrarNotificacion();
	}

	private void mostrarNotificacion() {
		// TODO Auto-generated method stub
		 CharSequence text = getText(R.string.lblServicioIniciado);

	        // Set the icon, scrolling text and timestamp
	        Notification notification = new Notification(R.drawable.icono_pref, text,System.currentTimeMillis());

	        // The PendingIntent to launch our activity if the user selects this notification
	        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
	                new Intent(this, TabsActivity.class), 0);

	        // Set the info for the views that show in the notification panel.
	        notification.setLatestEventInfo(this, getText(R.string.lblServicioPrueba),
	                       text, contentIntent);

	        // Send the notification.
	        mNotificationManager.notify(NOTIFICACION, notification);
	}
	
	@Override
    public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
        Log.i("LocalService", "Received start id " + startId + ": " + intent);
        return START_STICKY;
    }

	@Override
	public void onStart(Intent intent, int startId) {  
		
		intentRecibido = intent;
		
		Log.d("START_SERVICE", "onStart()");
		
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		listener = new MyLocationListener();
		int refresco = 600000;
		if(intent!=null){
			refresco = intent.getIntExtra("refresco", 120000);
		}
		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, refresco, 0, listener);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, refresco, 0, listener);
		
//		String ns = Context.NOTIFICATION_SERVICE;
//		NotificationManager mNotificationManager = (NotificationManager) this.getSystemService(ns);
		
//		Notification not = new Notification(R.drawable.icono_pref, "Iniciando servicio", System.currentTimeMillis());
//		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, TabsActivity.class), Notification.FLAG_ONGOING_EVENT);        
//		not.flags = Notification.DEFAULT_LIGHTS | Notification.FLAG_AUTO_CANCEL;
//		not.setLatestEventInfo(this, "WorstMAP", "Servicio de posicionamiento", contentIntent);
//		mNotificationManager.notify(1, not);
		
	}

	
	private final IBinder mBinder = new SubirPosicionIntentServiceBinder();
	 
	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}
	
	public class SubirPosicionIntentServiceBinder extends Binder {
		SubirPosicionIntentService getService() {
            return SubirPosicionIntentService.this;
        }
    }
	
	

	protected boolean isBetterLocation(Location location, Location currentBestLocation) {
		if (currentBestLocation == null) {
			// A new location is always better than no location
			return true;
		}

		// Check whether the new location fix is newer or older
		long timeDelta = location.getTime() - currentBestLocation.getTime();
		boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
		boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
		boolean isNewer = timeDelta > 0;

		// If it's been more than two minutes since the current location, use the new location
		// because the user has likely moved
		if (isSignificantlyNewer) {
			return true;
			// If the new location is more than two minutes older, it must be worse
		} else if (isSignificantlyOlder) {
			return false;
		}

		// Check whether the new location fix is more or less accurate
		int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
		boolean isLessAccurate = accuracyDelta > 0;
		boolean isMoreAccurate = accuracyDelta < 0;
		boolean isSignificantlyLessAccurate = accuracyDelta > 200;

		// Check if the old and new location are from the same provider
		boolean isFromSameProvider = isSameProvider(location.getProvider(),
				currentBestLocation.getProvider());

		// Determine location quality using a combination of timeliness and accuracy
		if (isMoreAccurate) {
			return true;
		} else if (isNewer && !isLessAccurate) {
			return true;
		} else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
			return true;
		}
		return false;
	}

	/** Checks whether two providers are the same */
	private boolean isSameProvider(String provider1, String provider2) {
		if (provider1 == null) {
			return provider2 == null;
		}
		return provider1.equals(provider2);
	}

	@Override
	public void onDestroy() {       
		// handler.removeCallbacks(sendUpdatesToUI);  
		Log.d("STOP_SERVICE", "onDestroy()");
		//locationManager.removeUpdates(listener); 
		sendBroadcast(intent);
		mNotificationManager.cancel(NOTIFICACION);
		super.onDestroy();
		
	}   

	public static Thread performOnBackgroundThread(final Runnable runnable) {
		final Thread t = new Thread() {
			@Override
			public void run() {

				try {
					runnable.run();
				} finally {

				}
			}
		};
		t.start();
		return t;
	}


	public class MyLocationListener implements LocationListener
	{

		public void onLocationChanged(final Location loc)
		{
			Log.i("**************************************", "Location changed");
			if(isBetterLocation(loc, previousBestLocation)) {
				loc.getLatitude();
				loc.getLongitude();             
				intent.putExtra("Latitude", loc.getLatitude());
				intent.putExtra("Longitude", loc.getLongitude());     
				intent.putExtra("Provider", loc.getProvider());                 
				sendBroadcast(intent); 

				Log.d("Posicion Servicio", loc.getLatitude()+","+loc.getLongitude());
				String latitud = Uri.encode(loc.getLatitude()+"");
				String longitud = Uri.encode(loc.getLongitude()+"");
				String nombre = Uri.encode(intentRecibido.getStringExtra("nombre"));
				String mensaje = Uri.encode(intentRecibido.getStringExtra("estado"));
				String radio = Uri.encode((intentRecibido.getIntExtra("radio", 500))+"");

				//Obtenemos la MAC del dispositivo a traves del objeto WifiManager
				WifiManager manager = (WifiManager) SubirPosicionIntentService.this.getSystemService(Context.WIFI_SERVICE);
				WifiInfo info = manager.getConnectionInfo();
				String macAddress = Uri.encode(info.getMacAddress());
				String url = "http://wmap.herobo.com/wmap/servicio-obtener-posiciones.php?id_usuario="+macAddress+"&latitud="+latitud+"&longitud="+longitud+"&radio="+radio+"&nombre="+nombre+"&mensaje="+mensaje+"&guardar=1";
				Log.i("SubirPosiciones", url);
				if(guardarPosicion(url)){
					Log.d("Posicion Servicio", "Posicion guardada");
					Log.i("SubirPosiciones", url);
				}else{
					Log.d("Posicion Servicio", "Posicion NO guardada");
				}

			}                               
		}

		public void onProviderDisabled(String provider)
		{
			Toast.makeText( getApplicationContext(), "Gps Disabled", Toast.LENGTH_SHORT ).show();
		}


		public void onProviderEnabled(String provider)
		{
			Toast.makeText( getApplicationContext(), "Gps Enabled", Toast.LENGTH_SHORT).show();
		}


		public void onStatusChanged(String provider, int status, Bundle extras)
		{

		}




		public boolean guardarPosicion(String url){
			HttpClient httpclient = new DefaultHttpClient();
			HttpResponse response;
			JSONArray finalResult = null;
			try {
				response = httpclient.execute(new HttpGet(url));
				StatusLine statusLine = response.getStatusLine();
				if(statusLine.getStatusCode() == HttpStatus.SC_OK){	                

					BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
					String json = reader.readLine();
					JSONTokener tokener = new JSONTokener(json);         
					try {
						finalResult = new JSONArray(tokener);
					} catch (JSONException e) {
						Log.d("AsyncDoIn", e.getMessage());
						e.printStackTrace();
					}

				} else{
					//Closes the connection.
					response.getEntity().getContent().close();
					System.out.println("FALLA JSON: "+statusLine.getReasonPhrase());
					//throw new IOException(statusLine.getReasonPhrase());
				}
			} catch (ClientProtocolException e) {
				Log.d("AsyncDoIn", e.getMessage());
			} catch (IOException e) {
				Log.d("AsyncDoIn", e.getMessage());
			}

			int valor = 0;
			if(finalResult!=null){
				valor = finalResult.optInt(0);
			}
			return valor>0;
		}



	}
	
	
	
}
