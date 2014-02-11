package es.nervion.maps.activity;

import java.util.Date;

import es.nervion.maps.adapter.MyDrawerListAdapter;
import es.nervion.maps.clase.Mensaje;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

public class GCMBroadcastReceiver extends WakefulBroadcastReceiver
{

	
	@Override
	public void onReceive(Context context, Intent intent)
	{
		Log.d("GCMBroadCast", "Recibe Mensaje");

		Bundle extras = intent.getExtras();

		//		if(this!=null){
		//			try{
		//				TabsActivity.this.getMyMapFragment().getMensajes().add(new Mensaje(extras.getString("nombre"), extras.getString("mensaje"), new Date()));
		//				MyDrawerListAdapter adapter = new MyDrawerListAdapter(TabsActivity.this, R.layout.fragment_my_map, TabsActivity.this.getMyMapFragment().getMensajes());
		//				TabsActivity.this.getMyMapFragment().getDrawerList().setAdapter(adapter);  
		//			}catch(Exception e){
		//				Log.d("GCMBroadcast", "Activity null");
		//			}
		//		}



		ComponentName comp =
				new ComponentName(context.getPackageName(),
						GCMIntentService.class.getName());

		startWakefulService(context, (intent.setComponent(comp)));

		setResultCode(Activity.RESULT_OK);
	}



}