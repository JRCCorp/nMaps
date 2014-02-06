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

import android.app.IntentService;
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
	}

	@Override
	public void onStart(Intent intent, int startId) {      
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		listener = new MyLocationListener();
		int refresco = 600000;
		if(intent!=null){
			refresco = intent.getIntExtra("refresco", 120000);
		}
		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, refresco, 0, listener);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, refresco, 0, listener);
		
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
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
		super.onDestroy();
		Log.v("STOP_SERVICE", "DONE");
		locationManager.removeUpdates(listener);        
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
				String nombre = Uri.encode(intent.getStringExtra("nombre"));
				String mensaje = Uri.encode(intent.getStringExtra("estado"));
				String radio = Uri.encode((intent.getIntExtra("radio", 500)/1000)+"");

				//Obtenemos la MAC del dispositivo a traves del objeto WifiManager
				WifiManager manager = (WifiManager) SubirPosicionIntentService.this.getSystemService(Context.WIFI_SERVICE);
				WifiInfo info = manager.getConnectionInfo();
				String macAddress = Uri.encode(info.getMacAddress());
				String fecha = Uri.encode(new SimpleDateFormat("dd/MM/yy HH:mm:ss", Locale.getDefault()).format(new Date()));
				String url = "http://wmap.herobo.com/wmap/servicio-obtener-posiciones.php?id_usuario="+macAddress+"&latitud="+latitud+"&longitud="+longitud+"&radio="+radio+"&fecha="+fecha+"&nombre="+nombre+"&mensaje="+mensaje+"&guardar=1";
				if(guardarPosicion(url)){
					Log.d("Posicion Servicio", "Posicion guardada");
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
