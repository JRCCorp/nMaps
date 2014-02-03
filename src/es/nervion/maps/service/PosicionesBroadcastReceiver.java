package es.nervion.maps.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
import es.nervion.maps.activity.TabsActivity;

public class PosicionesBroadcastReceiver extends BroadcastReceiver {

	private TabsActivity activity;

	public PosicionesBroadcastReceiver(TabsActivity activity){
		this.activity = activity;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		if(intent.getAction().equals(PosicionesIntentService.ACTION_ACTIVO)) {
			activity.peticionPost(activity.getMyMapFragment());
			PosicionesIntentService.vivo = activity.recuperarPreferenciaBoolean("servicio");
		}
		else if(intent.getAction().equals(PosicionesIntentService.ACTION_FIN)) {
			Toast.makeText(activity, "Servicio de posiciones parado", Toast.LENGTH_SHORT).show();
		}
	}


}