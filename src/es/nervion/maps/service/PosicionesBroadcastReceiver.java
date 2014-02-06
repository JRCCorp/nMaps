package es.nervion.maps.service;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
public class PosicionesBroadcastReceiver extends BroadcastReceiver {


	public PosicionesBroadcastReceiver(){
		
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		if(intent.getAction().equals(SubirPosicionIntentService.BROADCAST_ACTION)) {
			Log.d("Servicio SubirPosicion", "Lanzado");
		}else if(intent.getAction().equals(SubirPosicionIntentService.BROADCAST_MUERE)){
			Log.d("Servicio SubirPosicion", "Muere");
		}

	}



	


}
