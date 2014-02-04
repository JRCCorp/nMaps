package es.nervion.maps.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

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

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import es.nervion.maps.fragment.MyMapFragment;

public class ServicioPosiciones extends AsyncTask<Map<String, String>, JSONArray, JSONArray>{

	private MyMapFragment mmf;
	private int refresco;

	public ServicioPosiciones(MyMapFragment mmf, int refresco){
		super();
		this.mmf = mmf;
		this.refresco = refresco;
	}

	@Override
	protected JSONArray doInBackground(Map<String, String>... uri) {
		while(true){
			HttpClient httpclient = new DefaultHttpClient();
			HttpResponse response;
			JSONArray finalResult = null;
			try {
				response = httpclient.execute(new HttpGet(uri[0].get("host")));
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
			publishProgress(finalResult);
			try {
				Thread.sleep(refresco);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				
			}
		}
	}
	
	@Override
	protected void onProgressUpdate(JSONArray... progress) {
		JSONArray result = progress[0];
		try {
			mmf.eliminarMarcadores();
			mmf.getMap().clear();
			if(result!=null){				
				for(int i=0; i<result.length(); i++){
					Log.d("ServicioPosiciones", result.getString(i));
					Marker m = mmf.getMap().addMarker(new MarkerOptions()
					.position(new LatLng(result.getJSONArray(i).getDouble(2), result.getJSONArray(i).getDouble(3)))
					.title(result.getJSONArray(i).getString(5))
					.snippet(result.getJSONArray(i).getString(6)));
					mmf.anadirMarcador(m);
				}
			}
		} catch (JSONException e) {
			Log.d("AsyncPost", e.getMessage());
			e.printStackTrace();
		}
    }
	

	@Override
	protected void onPostExecute(JSONArray result) {
		super.onPostExecute(result);
		
	}	
	
}