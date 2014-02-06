package es.nervion.maps.service;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
public class PosicionesBroadcastReceiver extends BroadcastReceiver {


	public PosicionesBroadcastReceiver(){
		
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		if(intent.getAction().equals(SubirPosicionIntentService.BROADCAST_ACTION)) {
			Log.d("Servicio SubirPosicion", "Lanzado");
		}else if(intent.getAction().equals(SubirPosicionIntentService.BROADCAST_MUERE)){
			SharedPreferences sp = context.getSharedPreferences("es.nervion.maps.activity_preferences", Context.MODE_PRIVATE);
			SharedPreferences.Editor spe = sp.edit();
			spe.putBoolean("servicioActivo", false);
			Log.d("Servicio SubirPosicion", "Muere");
		}

	}



	


}
