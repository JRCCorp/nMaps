package es.nervion.maps.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
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

import android.content.Context;
import android.location.Location;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import es.nervion.maps.activity.TabsActivity;
import es.nervion.maps.fragment.MyMapFragment;

public class ServicioPosiciones extends AsyncTask<Void, JSONArray, JSONArray>{

	private TabsActivity activity;
	private MyMapFragment mmf;
	private int refresco;
	private String url = "http://wmap.herobo.com/wmap/servicio-obtener-posiciones.php?";

	public ServicioPosiciones(TabsActivity activity, int refresco){
		super();
		this.activity = activity;
		this.mmf = activity.getMyMapFragment();
		this.refresco = refresco;
	}

	@Override
	protected  void onPreExecute()
	{
		obtenerNuevaLocalizacion();
	}

	@Override
	protected JSONArray doInBackground(Void... params) {
		Log.d("Dani", "ServicioPosiciones Empezado");
		while(true){
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
					throw new IOException(statusLine.getReasonPhrase());
				}
			} catch (ClientProtocolException e) {
				Log.d("AsyncDoIn", e.getMessage());
			} catch (IOException e) {
				Log.d("AsyncDoIn", e.getMessage());
			}
			if(isCancelled()){
				Log.d("Dani", "ServicioPosiciones Cancelado");
				break;
			}
			publishProgress(finalResult);			


			try {
				Thread.sleep(refresco);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block

			}
		}
		return null;
	}

	@Override
	protected void onProgressUpdate(JSONArray... progress) {
		try{

			JSONArray result = progress[0];
			try {
				if(mmf!=null && mmf.getMyMap()!=null){			
					mmf.eliminarMarcadores();
					mmf.getMyMap().clear();
					if(result!=null){				
						for(int i=0; i<result.length(); i++){
							Log.d("ServicioPosiciones", result.getString(i));
							Marker m = mmf.getMyMap().addMarker(new MarkerOptions()
							.position(new LatLng(result.getJSONArray(i).getDouble(2), result.getJSONArray(i).getDouble(3)))
							.title(result.getJSONArray(i).getString(5))
							.snippet(result.getJSONArray(i).getString(6)));
							mmf.anadirMarcador(m);
						}
					}

					obtenerNuevaLocalizacion();

				}

			} catch (JSONException e) {
				Log.d("AsyncPost", e.getMessage());
			}
		}catch(Exception e){
			Log.d("ServicioPosiciones", "Mapa no cargado");
		}
	}


	@Override
	protected void onPostExecute(JSONArray result) {
		super.onPostExecute(result);

	}


	private void obtenerNuevaLocalizacion(){
		try{
			Location location = mmf.getMyMap().getMyLocation();
			String latitud = Uri.encode(location.getLatitude()+"");
			String longitud = Uri.encode(location.getLongitude()+"");
			String nombre = Uri.encode(activity.recuperarPreferenciaString("nombre"));
			String mensaje = Uri.encode(activity.recuperarPreferenciaString("estado"));
			String radio = Uri.encode((activity.recuperarPreferenciaInteger("radio"))+"");
			System.out.println("Latitud: "+location.getLatitude()+", Longitud: "+location.getLongitude()+"\n ");

			//Obtenemos la MAC del dispositivo a traves del objeto WifiManager
			WifiManager manager = (WifiManager) activity.getSystemService(Context.WIFI_SERVICE);
			WifiInfo info = manager.getConnectionInfo();
			String macAddress = Uri.encode(info.getMacAddress());
			String fecha = Uri.encode(new SimpleDateFormat("dd/MM/yy HH:mm:ss", Locale.getDefault()).format(new Date()));

			url = "http://wmap.herobo.com/wmap/servicio-obtener-posiciones.php?id_usuario="+macAddress+"&latitud="+latitud+"&longitud="+longitud+"&radio="+radio+"&fecha="+fecha+"&nombre="+nombre+"&mensaje="+mensaje+"&guardar=1&obtener=1";
			Log.d("_ ServicioPosiciones", url);
		}catch(Exception e){
			Log.d("ServicioPosiciones", "mapa no cargado");
			ServicioPosiciones.this.cancel(true);
		}
	}

}