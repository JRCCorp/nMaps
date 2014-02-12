package es.nervion.maps.fragment;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import es.nervion.maps.activity.R;
import es.nervion.maps.activity.TabsActivity;
import es.nervion.maps.listener.InicioListener;

public class InicioFragment extends Fragment{

	private InicioListener inicioLoadedListener;
	private MenuItem actualizarServidor;
	private ImageView imgEstadoServidor;
	
	private ImageButton btnSwitch;
	private boolean activo = true;
	private MediaPlayer mp;



	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment	
		return inflater.inflate(R.layout.fragment_inicio, container, false);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);			
		setHasOptionsMenu(true);
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setRetainInstance(true);
		((TabsActivity) getActivity() ).getViewPager().setCurrentItem(1);
		//imgEstadoServidor = (ImageView) this.getActivity().findViewById(R.id.imgEstadoServidor);
		btnSwitch = (ImageButton) this.getActivity().findViewById(R.id.btnSwitch);
		
		btnSwitch.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (activo) {
					playSound();
					activo = false;
					btnSwitch.setImageResource(R.drawable.btn_switch_on);
				} else {
					playSound();
					activo = true;
					btnSwitch.setImageResource(R.drawable.btn_switch_off);
				}
			}
		});
	}

	private void playSound(){
		if(activo){
			mp = MediaPlayer.create(getActivity(), R.raw.light_switch_off);
		}else{
			mp = MediaPlayer.create(getActivity(), R.raw.light_switch_on);
		}
		mp.setOnCompletionListener(new OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                // TODO Auto-generated method stub
                mp.release();
            }
        }); 
		mp.start();
	}

	public void   onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		menu.clear();
		inflater.inflate(R.menu.inicio, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_refresh:
			actualizarServidor = item;
			new SyncData().execute();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public void onMyDestroy(){
		if(getActivity()!=null){

			Fragment f = getActivity().getFragmentManager().findFragmentById(R.layout.fragment_inicio);

			if (f != null) {
				getActivity().getFragmentManager()
				.beginTransaction().remove(f).commit();
			}
		}
	}

	//Setter de InicioLoadedListener
	public void setInicioLoadedListener(InicioListener ill){
		inicioLoadedListener = ill;
	}


	private class SyncData extends AsyncTask<String, Void, Boolean> {

		private String url = "http://wmap.herobo.com/wmap/servicio-obtener-posiciones.php?";


		@SuppressLint("NewApi")
		@Override
		protected void onPreExecute() {
			actualizarServidor.setActionView(R.layout.action_progressbar);
			actualizarServidor.expandActionView();
		}

		@Override
		protected Boolean doInBackground(String... params) {
			HttpClient httpclient = new DefaultHttpClient();
			HttpResponse response;
			Boolean resultado = false; // 0: ROJO, 1: VERDE, 2: AMARILLO
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block

			}

			try {
				response = httpclient.execute(new HttpGet(url));
				StatusLine statusLine = response.getStatusLine();
				if(statusLine.getStatusCode() == HttpStatus.SC_OK){	                
					resultado = true;
				}else if(statusLine.getStatusCode() == HttpStatus.SC_BAD_REQUEST ){	   
					resultado = false;
				}else if(statusLine.getStatusCode() == HttpStatus.SC_MULTIPLE_CHOICES || statusLine.getStatusCode() == HttpStatus.SC_INTERNAL_SERVER_ERROR){	   
					resultado = false;
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

			return resultado;
		}

		@SuppressLint("NewApi")
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			actualizarServidor.collapseActionView();
			actualizarServidor.setActionView(null);
			if (result){
				imgEstadoServidor.setImageResource(R.drawable.estado_on);
			}else{
				imgEstadoServidor.setImageResource(R.drawable.estado_off);
			}
		}
	};




}
