package es.nervion.maps.activity;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.Menu;
import android.widget.Button;
import android.widget.ImageButton;

import com.google.android.gms.maps.GoogleMap;

import es.nervion.maps.adapter.SectionsPagerAdapter;
import es.nervion.maps.fragment.InicioFragment;
import es.nervion.maps.fragment.MyMapFragment;
import es.nervion.maps.fragment.PreferenciasFragment;
import es.nervion.maps.listener.InicioListener;
import es.nervion.maps.listener.MapListener;
import es.nervion.maps.listener.PreferencesListener;
import es.nervion.maps.service.ServicioPosiciones;
import es.nervion.maps.service.SubirPosicionIntentService;

public class TabsActivity extends Activity implements MapListener, InicioListener, PreferencesListener, OnPageChangeListener {

	private SectionsPagerAdapter mSectionsPagerAdapter;

	private ViewPager mViewPager;

	private PreferenciasFragment preferenciasFragment;
	private InicioFragment inicioFragment;
	private MyMapFragment myMapFragment;

	private ServicioPosiciones sp;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tabs);	

		preferenciasFragment = new PreferenciasFragment();
		preferenciasFragment.setPreferencesListener(this);		

		inicioFragment = new InicioFragment();		
		inicioFragment.setInicioLoadedListener(this);

		crearMapFragment();

		ArrayList<Fragment> fragments = new ArrayList<Fragment>();
		fragments.add(preferenciasFragment);
		fragments.add(inicioFragment);		
		if(myMapFragment!=null){			
			fragments.add(myMapFragment);
		}		


		mSectionsPagerAdapter = new SectionsPagerAdapter(this, getFragmentManager(), fragments);
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		mViewPager.setCurrentItem(1);

		mViewPager.setOnPageChangeListener(this);		

		servicioGuardarPosicion();


	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		getMenuInflater().inflate(R.menu.tabs, menu);
		return true;
	}

	@Override
	protected void onDestroy(){
		super.onDestroy();
		if(sp!=null){
			sp.cancel(true);
		}
	}

	public void peticionPost(){

		//Ejecutamos el servicio-obtener-posiciones
		if(recuperarPreferenciaBoolean("servicio2")){
			if(sp==null || sp.isCancelled()){
				sp = new ServicioPosiciones(this, 30000);
				sp.execute();
			}			
		}		
	}


	public void servicioGuardarPosicion(){
		if(recuperarPreferenciaBoolean("servicio1") && !recuperarPreferenciaBoolean("servicioActivo")){
			Log.d("TabsActivity", "Activar servicio");
			SharedPreferences sp = this.getSharedPreferences("es.nervion.maps.activity_preferences", Context.MODE_PRIVATE);
			SharedPreferences.Editor spe = sp.edit();
			spe.putBoolean("pref_servicioActivo", true);
			spe.commit();
			Intent msgIntent = new Intent(this, SubirPosicionIntentService.class);
			msgIntent.setAction(SubirPosicionIntentService.BROADCAST_ACTION);
			msgIntent.putExtra("vivo", true);
			msgIntent.putExtra("refresco", 30000);
			msgIntent.putExtra("nombre", recuperarPreferenciaString("nombre"));
			msgIntent.putExtra("estado", recuperarPreferenciaString("estado"));
			msgIntent.putExtra("radio", recuperarPreferenciaInteger("radio"));
			startService(msgIntent);
		}else if (!recuperarPreferenciaBoolean("servicio1") && recuperarPreferenciaBoolean("servicioActivo")){
			Log.d("TabsActivity", "Desactivar servicio");
			Intent intentPararServicio = new Intent (this, SubirPosicionIntentService.class);
			intentPararServicio.setAction(SubirPosicionIntentService.BROADCAST_MUERE);
			stopService(intentPararServicio);
			SharedPreferences sp = this.getSharedPreferences("es.nervion.maps.activity_preferences", Context.MODE_PRIVATE);
			SharedPreferences.Editor spe = sp.edit();
			spe.putBoolean("pref_servicioActivo", false);
			spe.commit();
		}
	}


	public String recuperarPreferenciaString(String campo){
		SharedPreferences prefs = getSharedPreferences("es.nervion.maps.activity_preferences",Context.MODE_PRIVATE);
		return String.valueOf(prefs.getString("pref_"+campo, ""));
	}

	public int recuperarPreferenciaInteger(String campo){
		SharedPreferences prefs = getSharedPreferences("es.nervion.maps.activity_preferences",Context.MODE_PRIVATE);
		return prefs.getInt("pref_"+campo, 500);
	}

	public boolean recuperarPreferenciaBoolean(String campo){
		SharedPreferences prefs = getSharedPreferences("es.nervion.maps.activity_preferences",Context.MODE_PRIVATE);
		return prefs.getBoolean("pref_"+campo, false);
	}



	public MyMapFragment getMyMapFragment() {
		return myMapFragment;
	}

	public InicioFragment getInicioFragment() {
		return inicioFragment;
	}


	/* Implementamos el método onMapLoaded recibido de MyMapFragment */
	@Override
	public void onMapLoaded(GoogleMap gm) {
		/* Registrar acciones de servicio y broadcastReceiver */
		if(mViewPager.getCurrentItem()==2){
			peticionPost();
		}

	}


	/* Implementamos el método onInicioClick recibido de MyMapFragment */
	@Override
	public void onInicioClick(Button btn) {

	}

	/* Implementamos el método onPreferencesChange recibido desde PreferenciasFragment */
	@Override
	public void onPreferencesChange() {
		servicioGuardarPosicion();
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPageSelected(int position) {

		System.out.println("Cambiado "+position);

		if(myMapFragment!=null && myMapFragment.getMap()!=null && position==2){
			peticionPost();
		}else{
			if(sp!=null){
				sp.cancel(true);
			}			
		}

	}

	@Override
	public void onMapFragmentLoaded() {
		// quitar splAsh
		
	}

	@Override
	public void onInicioClick(ImageButton brujula) {
		// TODO Auto-generated method stub

		Handler handler=new Handler();

		final Runnable r = new Runnable()
		{
			public void run() 
			{
				mViewPager.setCurrentItem(2, true);
			}
		};

		handler.postDelayed(r, 1000);

	}




	public void crearMapFragment() {
		if(isGoogleMapsInstalled())
		{
			//Log.d("GoogleMap", "instalado");
			myMapFragment = new MyMapFragment();
			myMapFragment.setMapLoadedListener(this);
		}
		else
		{
			Log.d("GoogleMap", "NO instalado");
			Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("Por favor, instala Google Maps");
			builder.setCancelable(false);
			builder.setPositiveButton("Instalar", getGoogleMapsListener());
			AlertDialog dialog = builder.create();
			dialog.show();
		}

	}

	public boolean isGoogleMapsInstalled()
	{
		try
		{			
			ApplicationInfo info = getPackageManager().getApplicationInfo("com.google.android.apps.maps", 0 );
			return true;
		} 
		catch(PackageManager.NameNotFoundException e)
		{
			return false;
		}
	}

	public android.content.DialogInterface.OnClickListener getGoogleMapsListener()
	{
		return new android.content.DialogInterface.OnClickListener() 
		{
			@Override
			public void onClick(DialogInterface dialog, int which) 
			{
				Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.google.android.apps.maps"));
				startActivity(intent);

				//Finish the activity so they can't circumvent the check
				finish();
			}
		};
	}






}
