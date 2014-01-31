package es.nervion.maps.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.widget.Button;

import com.google.android.gms.maps.GoogleMap;

import es.nervion.maps.adapter.SectionsPagerAdapter;
import es.nervion.maps.async.ServicioPosiciones;
import es.nervion.maps.fragment.InicioFragment;
import es.nervion.maps.fragment.MyMapFragment;
import es.nervion.maps.fragment.PreferenciasFragment;
import es.nervion.maps.listener.InicioListener;
import es.nervion.maps.listener.MapLoadedListener;

public class TabsActivity extends FragmentActivity implements MapLoadedListener, InicioListener {

	private SectionsPagerAdapter mSectionsPagerAdapter;

	private ViewPager mViewPager;
	
	private PreferenciasFragment preferenciasFragment;
	private InicioFragment inicioFragment;
	private MyMapFragment myMapFragment;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tabs);
		
		preferenciasFragment = new PreferenciasFragment();

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
		

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		getMenuInflater().inflate(R.menu.tabs, menu);
		return true;
	}

	
	
	public void peticionPost(GoogleMap gm){

		Location location = gm.getMyLocation();
		Double latitud = location.getLatitude();
		Double longitud = location.getLongitude();
		String nombre = recuperarPreferenciaTexto("nombre");
		String mensaje = recuperarPreferenciaTexto("mensaje");
		System.out.println("Latitud: "+location.getLatitude()+", Longitud: "+location.getLongitude()+"\n ");

		//Obtenemos la MAC del dispositivo a traves del objeto WifiManager
		WifiManager manager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = manager.getConnectionInfo();
		String macAddress = info.getMacAddress();
		
		//Indicamos la url del servicio
		/***
		 * @TODO
		 */
		String tiempoAhora = new SimpleDateFormat("dd/MM/yy HH:mm:ss", Locale.getDefault()).format(new Date());
		String urlPath = "http://wmap.herobo.com/wmap/servicio-obtener-posiciones.php?id_usuario="+macAddress+"&latitud="+latitud+"&longitud="+longitud+"&radio=10&fecha="+tiempoAhora+"&nombre="+nombre+"&mensaje="+mensaje+"&guardar=1&obtener=1";
		Map<String, String> parametros = new HashMap<String, String>();
		
		//Le pasamos los parámetros al Map
		parametros.put("host", urlPath);
		parametros.put("latitud", latitud.toString());
		parametros.put("longitud", longitud.toString());
		
		//Ejecutamos el servicio-obtener-posiciones
		new ServicioPosiciones(gm).execute(parametros);
	}

	
	public String recuperarPreferenciaTexto(String campo){
		SharedPreferences prefs = getSharedPreferences("es.nervion.maps.activity_preferences",Context.MODE_PRIVATE);
		return String.valueOf(prefs.getString("pref_"+campo, ""));
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
		peticionPost(gm);
	}
	
	
	/* Implementamos el método onInicioClick recibido de MyMapFragment */
	@Override
	public void onInicioClick(Button btn) {		

		switch (btn.getId()) {
		case R.id.btnPeticion:
			if(myMapFragment.getMap()!=null){
				peticionPost(myMapFragment.getMap());
			}			
			break;

		default:
			break;
		}
		
	}
	


}
