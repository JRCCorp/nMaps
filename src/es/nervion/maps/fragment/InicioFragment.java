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
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import es.nervion.maps.activity.R;
import es.nervion.maps.activity.TabsActivity;
import es.nervion.maps.listener.InicioListener;

public class InicioFragment extends Fragment{

	private InicioListener inicioLoadedListener;
	private MenuItem actualizarServidor;
	private ImageView imgEstadoServidor;



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
		imgEstadoServidor = (ImageView) this.getActivity().findViewById(R.id.imgEstadoServidor);
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
			Boolean resultado = false;
			try {
				Thread.sleep(1500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block

			}

			try {
				response = httpclient.execute(new HttpGet(url));
				StatusLine statusLine = response.getStatusLine();
				if(statusLine.getStatusCode() == HttpStatus.SC_OK){	                
					resultado = true;
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
		@Override
		protected void onPostExecute(Boolean result) {
			actualizarServidor.collapseActionView();
			actualizarServidor.setActionView(null);
			if (result){
				imgEstadoServidor.setImageResource(R.drawable.estado_on);
			}
		}
	};




}
