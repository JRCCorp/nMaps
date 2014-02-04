package es.nervion.maps.service;

import android.app.IntentService;
import android.content.Intent;

public class ObtenerPosicionesIntentService extends IntentService {

	public static final String ACTION_ACTIVO =
			"net.sgoliver.intent.action.ACTIVO";
	public static final String ACTION_FIN =
			"net.sgoliver.intent.action.FIN";
	
	public static boolean vivo = false;
	private int refresco;

	public ObtenerPosicionesIntentService() {
		super("Servicio de Posiciones");
	}
	
	
	

	@Override
	protected void onHandleIntent(Intent intent) {
		
		vivo = intent.getBooleanExtra("vivo", false);
		refresco = intent.getIntExtra("refresco", 5000);

		while(vivo){
			
			try {
				Thread.sleep(refresco);
			} catch(InterruptedException e) {}

			//Comunicamos el progreso
			Intent bcIntent = new Intent();
			bcIntent.setAction(ACTION_ACTIVO);
			sendBroadcast(bcIntent);

		}

		Intent bcIntent = new Intent();
		bcIntent.setAction(ACTION_FIN);
		sendBroadcast(bcIntent);

	}




}
