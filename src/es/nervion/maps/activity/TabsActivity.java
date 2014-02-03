package es.nervion.maps.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.Location;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Menu;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;

import es.nervion.maps.adapter.SectionsPagerAdapter;
import es.nervion.maps.fragment.InicioFragment;
import es.nervion.maps.fragment.MyMapFragment;
import es.nervion.maps.fragment.PreferenciasFragment;
import es.nervion.maps.listener.InicioListener;
import es.nervion.maps.listener.MapLoadedListener;
import es.nervion.maps.listener.PreferencesListener;
import es.nervion.maps.service.PosicionesBroadcastReceiver;
import es.nervion.maps.service.ObtenerPosicionesIntentService;
import es.nervion.maps.service.ServicioPosiciones;

public class TabsActivity extends Activity implements MapLoadedListener, InicioListener, PreferencesListener, OnPageChangeListener {

	private SectionsPagerAdapter mSectionsPagerAdapter;

	private ViewPager mViewPager;

	private PreferenciasFragment preferenciasFragment;
	private InicioFragment inicioFragment;
	private MyMapFragment myMapFragment;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tabs);

		/* Registrar acciones de servicio y broadcastReceiver */
		IntentFilter filter = new IntentFilter();
		filter.addAction(ObtenerPosicionesIntentService.ACTION_ACTIVO);
		filter.addAction(ObtenerPosicionesIntentService.ACTION_FIN);
		PosicionesBroadcastReceiver broadcastReceiver = new PosicionesBroadcastReceiver(this);
		registerReceiver(broadcastReceiver, filter);

		preferenciasFragment = new PreferenciasFragment();
		preferenciasFragment.setPreferencesListener(this);

		myMapFragment = new MyMapFragment();	
		myMapFragment.setMapLoadedListener(this);

		inicioFragment = new InicioFragment();		
		inicioFragment.setInicioLoadedListener(this);

		ArrayList<Fragment> fragments = new ArrayList<Fragment>();
		fragments.add(preferenciasFragment);
		fragments.add(inicioFragment);
		fragments.add(myMapFragment);


		mSectionsPagerAdapter = new SectionsPagerAdapter(this, getFragmentManager(), fragments);
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		mViewPager.setCurrentItem(1);

		mViewPager.setOnPageChangeListener(this);


	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		getMenuInflater().inflate(R.menu.tabs, menu);
		return true;
	}



	public void peticionPost(MyMapFragment mmf){

		Location location = mmf.getMap().getMyLocation();
		String latitud = Uri.encode(location.getLatitude()+"");
		String longitud = Uri.encode(location.getLongitude()+"");
		String nombre = Uri.encode(recuperarPreferenciaString("nombre"));
		String mensaje = Uri.encode(recuperarPreferenciaString("estado"));
		String radio = Uri.encode((recuperarPreferenciaInteger("radio")/1000)+"");
		System.out.println("Latitud: "+location.getLatitude()+", Longitud: "+location.getLongitude()+"\n ");

		//Obtenemos la MAC del dispositivo a traves del objeto WifiManager
		WifiManager manager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = manager.getConnectionInfo();
		String macAddress = Uri.encode(info.getMacAddress());

		//Indicamos la url del servicio
		/***
		 * @TODO
		 */
		String fecha = Uri.encode(new SimpleDateFormat("dd/MM/yy HH:mm:ss", Locale.getDefault()).format(new Date()));
		String urlPath = "http://wmap.herobo.com/wmap/servicio-obtener-posiciones.php?id_usuario="+macAddress+"&latitud="+latitud+"&longitud="+longitud+"&radio="+radio+"&fecha="+fecha+"&nombre="+nombre+"&mensaje="+mensaje+"&guardar=1&obtener=1";
		Map<String, String> parametros = new HashMap<String, String>();
		
		//Le pasamos los parámetros al Map
		parametros.put("host", urlPath);

		//Ejecutamos el servicio-obtener-posiciones
		new ServicioPosiciones(myMapFragment).execute(parametros);
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
		peticionPost(myMapFragment);
	}


	/* Implementamos el método onInicioClick recibido de MyMapFragment */
	@Override
	public void onInicioClick(Button btn) {		

		//		if(myMapFragment.getMap()!=null){
		//			peticionPost(myMapFragment);
		//		}



	}

	/* Implementamos el método onPreferencesChange recibido desde PreferenciasFragment */
	@Override
	public void onPreferencesChange() {

		//		if(recuperarPreferenciaBoolean("servicio")){
		//			Intent msgIntent = new Intent(this, PosicionesIntentService.class);
		//			msgIntent.putExtra("vivo", recuperarPreferenciaBoolean("servicio"));
		//			msgIntent.putExtra("refresco", 120000);
		//	        startService(msgIntent);
		//		}

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

		System.out.println("Cambiado a "+position);
		if(position==2 && !ObtenerPosicionesIntentService.vivo){
			if(this.recuperarPreferenciaBoolean("servicio")){
				Intent msgIntent = new Intent(this, ObtenerPosicionesIntentService.class);
				msgIntent.putExtra("vivo", this.recuperarPreferenciaBoolean("servicio"));
				msgIntent.putExtra("refresco", 20000);
				this.startService(msgIntent);
			}
		}else if(ObtenerPosicionesIntentService.vivo){
			ObtenerPosicionesIntentService.vivo = false;
		}


	}




}
