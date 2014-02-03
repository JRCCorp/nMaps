package es.nervion.maps.service;

import android.app.IntentService;
import android.content.Intent;

public class SubirPosicionIntentService extends IntentService {
	
	/*
	 * @TODO
	 * */
	
	public static final String ACTION_ACTIVO =
			"net.sgoliver.intent.action.SUBIR_ACTIVO";
	public static final String ACTION_FIN =
			"net.sgoliver.intent.action.SUBIR_FIN";
	
	public static boolean vivo = false;

	private int refresco;

	public SubirPosicionIntentService() {
		super("Servicio subir posicion");
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
