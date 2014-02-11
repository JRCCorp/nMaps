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
import org.json.JSONObject;
import org.json.JSONTokener;

import com.google.android.gcm.GCMRegistrar;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import es.nervion.maps.activity.TabsActivity;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.util.Log;

public class TareaRegistroGCM extends AsyncTask<String,Integer,String>
{

	private Activity activity;

	public TareaRegistroGCM(Activity activity){
		this.activity = activity;
		if (android.os.Build.VERSION.SDK_INT > 9) {
			//Cambiamos el StrictMode para permitir la ejecucion de un HTTPClient en un hilo
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}
	}

	@Override
	protected String doInBackground(String... params)
	{
		String msg = "";

		try
		{

			GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(activity);
			//Nos registramos en los servidores de GCM
			String regid = gcm.register("834884443249");

			Log.d("TareaRegistroGCM", "Registrado en GCM: registration_id=" + regid);

			//Nos registramos en nuestro servidor
			boolean registrado = registroServidor(params[0], regid);

			//Guardamos los datos del registro
			if(registrado)
			{
				setRegistrationId(activity, params[0], regid);
			}
		}
		catch (IOException ex)
		{
			Log.d("TareaRegistroGCM", "Error registro en GCM:" + ex.getMessage());
		}

		return msg;
	}




	private boolean registroServidor(String usuario, String regId)
	{
		Log.d("TareaRegistroServidor", "Entra en registroServidor");
		boolean reg = false;

		final String URL="http://wmap.herobo.com/wmap/registro-cliente.php?id_usuario="+usuario+"&reggcm="+regId;		
		
		
		HttpClient httpclient = new DefaultHttpClient();
		HttpResponse response;
		JSONArray finalResult = null;
		JSONObject result = null;
		try {
			response = httpclient.execute(new HttpGet(URL));
			StatusLine statusLine = response.getStatusLine();
			if(statusLine.getStatusCode() == HttpStatus.SC_OK){	                

				BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
				String json = reader.readLine();
				System.out.println("URL: "+URL);
				Log.d("TareaRegistroServidor", "URL: "+URL);
				Log.d("TareaRegistroServidor", "Comparamos resultado con leido "+json);
				if(json.equals("1") || json == "1"){
					Log.d("TareaRegistroServidor", "Resultado = 1");
					reg = true;
				}
//				JSONTokener tokener = new JSONTokener(json);         
//				try {
//					result = new JSONObject(tokener);
//				} catch (JSONException e) {
//					Log.d("AsyncDoIn", e.getMessage());
//					e.printStackTrace();
//				}

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
			

		return reg;
	}




	private void setRegistrationId(Context context, String user, String regId)
	{
		SharedPreferences prefs = activity.getPreferences(Context.MODE_PRIVATE);

		int appVersion = getAppVersion(context);

		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(TabsActivity.PROPERTY_USER, user);
		editor.putString(TabsActivity.PROPERTY_REG_ID, regId);
		editor.putInt(TabsActivity.PROPERTY_APP_VERSION, appVersion);
		editor.putLong(TabsActivity.PROPERTY_EXPIRATION_TIME, System.currentTimeMillis() + 600000);

		editor.commit();
	}


	
	private static int getAppVersion(Context context)
	{
	    try
	    {
	        PackageInfo packageInfo = context.getPackageManager()
	                .getPackageInfo(context.getPackageName(), 0);
	 
	        return packageInfo.versionCode;
	    }
	    catch (NameNotFoundException e)
	    {
	        throw new RuntimeException("Error al obtener versión: " + e);
	    }
	}











}