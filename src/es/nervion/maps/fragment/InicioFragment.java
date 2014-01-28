package es.nervion.maps.fragment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
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
import org.json.JSONObject;
import org.json.JSONTokener;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.content.Context;
import android.location.Location;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import es.nervion.maps.activity.R;
import es.nervion.maps.activity.TabsActivity;

public class InicioFragment extends Fragment implements View.OnClickListener {

	private Button btnPeticion;
	private TextView txtInicio;
	private JSONArray jsonPosiciones;
	private GoogleMap gm;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment		
		return inflater.inflate(R.layout.fragment_inicio, container, false);
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		txtInicio = (TextView) this.getActivity().findViewById(R.id.txtInicio);

		btnPeticion = (Button) this.getActivity().findViewById(R.id.btnPeticion);
		btnPeticion.setOnClickListener(this);

	}



	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.btnPeticion:			
			
			MyMapFragment mf = ((TabsActivity)this.getActivity()).getMyMapFragment();
			Location location = null;
			if(mf!=null){
				gm = mf.getMap();
				if(gm != null){
					location = gm.getMyLocation();
					txtInicio.setText("Latitud: "+location.getLatitude()+", Longitud: "+location.getLongitude()+
							"\n ");
				}
			}
			
			peticionPost(location.getLatitude(), location.getLongitude());

			break;

		default:
			break;
		}

	}
	
	
	public void peticionPost(Double latitud, Double longitud){
		WifiManager manager = (WifiManager) getActivity().getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = manager.getConnectionInfo();
		String macAddress = info.getMacAddress();
		String urlPath = "http://wmap.herobo.com/wmap/servicio-obtener-posiciones.php?id_usuario="+macAddress.hashCode()+"&latitud="+latitud+"&longitud="+longitud+"&fecha=2000-10-10&nombre="+macAddress.hashCode()+"&mensaje=hola&guardar=1&obtener=1";
		Map<String, String> parametros = new HashMap<String, String>();
		parametros.put("host", urlPath);
		parametros.put("latitud", latitud.toString());
		parametros.put("longitud", longitud.toString());
		new RequestTask().execute(parametros);
	}
	
	
	class RequestTask extends AsyncTask<Map<String, String>, JSONArray, JSONArray>{

	    @Override
	    protected JSONArray doInBackground(Map<String, String>... uri) {
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
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	                
	            } else{
	                //Closes the connection.
	                response.getEntity().getContent().close();
	                throw new IOException(statusLine.getReasonPhrase());
	            }
	        } catch (ClientProtocolException e) {
	            //TODO Handle problems..
	        } catch (IOException e) {
	            //TODO Handle problems..
	        }
	        return finalResult;
	    }

	    @Override
	    protected void onPostExecute(JSONArray result) {
	        super.onPostExecute(result);
	        //Do anything with response..
	        try {
	        	jsonPosiciones = result;
				for(int i=0; i<result.length(); i++){
					System.out.println(result.getString(i));
					gm.addMarker(new MarkerOptions()
			        .position(new LatLng(result.getJSONArray(i).getDouble(2), result.getJSONArray(i).getDouble(3)))
			        .title(result.getJSONArray(i).getString(5))
			        .snippet(result.getJSONArray(i).getString(6)));
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
	}

	
	

}
