package es.nervion.maps.service;

import es.nervion.maps.activity.TabsActivity;
import es.nervion.maps.fragment.MyMapFragment;
import android.os.AsyncTask;

public class ObtenerPosicionesAsyncTask extends AsyncTask<MyMapFragment, Boolean, Boolean> {
	
	private TabsActivity activity;
	private int refresco;
	
	public ObtenerPosicionesAsyncTask(TabsActivity activity, int refresco){
		this.activity = activity;
		this.refresco = refresco;
	}
	
	@Override
	protected Boolean doInBackground(MyMapFragment... params) {
		
		try {
			activity.peticionPost();
			Thread.sleep(refresco);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}

}
