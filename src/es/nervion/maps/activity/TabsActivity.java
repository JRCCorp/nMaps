package es.nervion.maps.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.Menu;
import android.widget.Button;
import android.widget.ImageButton;

import com.google.android.gcm.GCMRegistrar;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.maps.GoogleMap;

import es.nervion.maps.adapter.MyDrawerListAdapter;
import es.nervion.maps.adapter.SectionsPagerAdapter;
import es.nervion.maps.clase.Mensaje;
import es.nervion.maps.fragment.InicioFragment;
import es.nervion.maps.fragment.MyMapFragment;
import es.nervion.maps.fragment.PreferenciasFragment;
import es.nervion.maps.listener.InicioListener;
import es.nervion.maps.listener.MapListener;
import es.nervion.maps.listener.PreferencesListener;
import es.nervion.maps.service.ServicioPosiciones;
import es.nervion.maps.service.SubirPosicionIntentService;
import es.nervion.maps.service.TareaRegistroGCM;

public class TabsActivity extends Activity implements MapListener, InicioListener, PreferencesListener, OnPageChangeListener {

	private SectionsPagerAdapter mSectionsPagerAdapter;

	private ViewPager mViewPager;

	private PreferenciasFragment preferenciasFragment;
	private InicioFragment inicioFragment;
	private MyMapFragment myMapFragment;

	private ServicioPosiciones sp;


	public static final String PROPERTY_REG_ID = "registration_id";
	public static final String PROPERTY_APP_VERSION = "appVersion";
	public static final String PROPERTY_EXPIRATION_TIME = "onServerExpirationTimeMs";
	public static final String PROPERTY_USER = "user";

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

		//		mViewPager.setCurrentItem(1);

		mViewPager.setOnPageChangeListener(this);

		servicioGuardarPosicion();

		sp = (ServicioPosiciones) this.getLastNonConfigurationInstance();

		registroGCM();

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

	@Override
	protected void onRestart(){
		super.onRestart();
		peticionPost();

	}

	@Override
	protected void onPause(){
		super.onPause();
		if(sp!=null){
			sp.cancel(true);
		}
	}

	@Override
	protected void onResume(){
		super.onResume();
		checkPlayServices();
		peticionPost();
	}

	public void peticionPost(){

		//Ejecutamos el servicio-obtener-posiciones
		Log.d("TabsActivity", "Tab Actual: "+mViewPager.getCurrentItem());
		if(recuperarPreferenciaBoolean("servicio2") && mViewPager.getCurrentItem()==2){
			if(sp==null || sp.isCancelled()){
				sp = new ServicioPosiciones(this, 30000);
				sp.execute();
			}
		}else{
			if(sp!=null){
				sp.cancel(true);
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
			msgIntent.putExtra("refresco", 120000);
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

	public ServicioPosiciones getServicioPosiciones(){
		return sp;
	}

	public ViewPager getViewPager(){
		return mViewPager;
	}


	@Override
	public Object onRetainNonConfigurationInstance(){
		return sp;
	}


	/* Implementamos el método onMapLoaded recibido de MyMapFragment */
	@Override
	public void onMapLoaded(GoogleMap gm) {
		/* Registrar acciones de servicio y broadcastReceiver */
		peticionPost();

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

		Log.d("TabsActivity", "Tab: "+position);

		if(position==2){
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
			Log.d("GoogleMap", "Creando MapFragment");
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




	public void registroGCM(){
		Context context = getApplicationContext();

		//Chequemos si está instalado Google Play Services
		if(checkPlayServices())
		{
			GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(TabsActivity.this);

			//Obtenemos el Registration ID guardado
			String regid = getRegistrationId(context);

			//Si no disponemos de Registration ID comenzamos el registro
			if (regid.equals("")) {
				WifiManager manager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
				WifiInfo info = manager.getConnectionInfo();
				String macAddress = Uri.encode(info.getMacAddress());
				TareaRegistroGCM tarea = new TareaRegistroGCM(this);
				Log.d("TabsActivity", "Vamos a ejecutar TareaRegistroGCM");
				tarea.execute(macAddress);
			}
		}
		else
		{
			Log.i("RegistroGCM", "No se ha encontrado Google Play Services.");
		}
	}


	private boolean checkPlayServices() {
		int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
		if (resultCode != ConnectionResult.SUCCESS)
		{
			if (GooglePlayServicesUtil.isUserRecoverableError(resultCode))
			{
				GooglePlayServicesUtil.getErrorDialog(resultCode, this, 1).show();
			}
			else
			{
				Log.i("CheckPlayServices", "Dispositivo no soportado.");
				finish();
			}
			return false;
		}
		return true;
	}



	private String getRegistrationId(Context context)
	{
		SharedPreferences prefs = getSharedPreferences(
				TabsActivity.class.getSimpleName(),
				Context.MODE_PRIVATE);

		String registrationId = prefs.getString(PROPERTY_REG_ID, "");

		if (registrationId.length() == 0)
		{
			Log.d("GCM", "Registro GCM no encontrado.");
			return "";
		}

		String registeredUser =
				prefs.getString(PROPERTY_USER, "user");

		int registeredVersion =
				prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);

		long expirationTime =
				prefs.getLong(PROPERTY_EXPIRATION_TIME, -1);

		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
		String expirationDate = sdf.format(new Date(expirationTime));

		Log.d("GCM", "Registro GCM encontrado (usuario=" + registeredUser +
				", version=" + registeredVersion +
				", expira=" + expirationDate + ")");

		WifiManager manager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = manager.getConnectionInfo();
		String macAddress = Uri.encode(info.getMacAddress());

		int currentVersion = getAppVersion(context);

		if (registeredVersion != currentVersion)
		{
			Log.d("GCM", "Nueva versión de la aplicación.");
			return "";
		}
		else if (System.currentTimeMillis() > expirationTime)
		{
			Log.d("GCM", "Registro GCM expirado.");
			return "";
		}
		else if (macAddress.equals(registeredUser))
		{
			Log.d("GCM", "Nuevo nombre de usuario.");
			return "";
		}

		return registrationId;
	}

	private static int getAppVersion(Context context)
	{
		try
		{
			PackageInfo packageInfo = context.getPackageManager()
					.getPackageInfo(context.getPackageName(), 0);

			return packageInfo.versionCode;
		}
		catch (NameNotFoundException e)
		{
			throw new RuntimeException("Error al obtener versión: " + e);
		}
	}




	


}
