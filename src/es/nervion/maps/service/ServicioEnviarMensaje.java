package es.nervion.maps.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

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

import es.nervion.maps.clase.Mensaje;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

public class ServicioEnviarMensaje extends AsyncTask<Mensaje, Boolean, Boolean> {
	
	private String codigo;
	private int radio;
	
	public ServicioEnviarMensaje(String codigo, int radio){
		this.codigo = codigo;
		this.radio = radio;
	}

	@Override
	protected Boolean doInBackground(Mensaje... params) {
		String nombre = Uri.encode(params[0].getNombre());
		String mensaje = Uri.encode(params[0].getMensaje());
		HttpClient httpclient = new DefaultHttpClient();
		HttpResponse response;
		JSONArray finalResult = null;
		boolean res = false;
		try {
			String url = "http://wmap.herobo.com/wmap/registro-test.php?id_usuario="+codigo+"&radio="+radio+"&nombre="+nombre+"&mensaje="+mensaje;
			Log.d("_ ServicioEnviarMensaje", url);
			response = httpclient.execute(new HttpGet(url));
			StatusLine statusLine = response.getStatusLine();
			if(statusLine.getStatusCode() == HttpStatus.SC_OK){	                

				BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
				String json = reader.readLine();
				JSONTokener tokener = new JSONTokener(json);         
				try {
					finalResult = new JSONArray(tokener);
					res = true;
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
		
		Log.d("ServicoEnviarMensaje", "finalResult: "+finalResult);
		Log.d("ServicoEnviarMensaje", "Estado de peticion: "+res);
		return res;
	}

}
