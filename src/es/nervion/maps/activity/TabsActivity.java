package es.nervion.maps.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.google.android.gms.maps.GoogleMap;

import es.nervion.maps.activity.R;
import es.nervion.maps.adapter.SectionsPagerAdapter;
import es.nervion.maps.async.ServicioPosiciones;

import es.nervion.maps.fragment.InicioFragment;
import es.nervion.maps.fragment.MyMapFragment;
import es.nervion.maps.listener.InicioListener;
import es.nervion.maps.listener.MapLoadedListener;

import android.content.Context;
import android.location.Location;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.widget.Button;

public class TabsActivity extends FragmentActivity implements MapLoadedListener, InicioListener {

	
	private SectionsPagerAdapter mSectionsPagerAdapter;

	private ViewPager mViewPager;
	
	private InicioFragment inicioFragment;
	private MyMapFragment myMapFragment;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tabs);

		myMapFragment = new MyMapFragment();	
		myMapFragment.setMapLoadedListener(this);
		
		inicioFragment = new InicioFragment();		
		inicioFragment.setInicioLoadedListener(this);
		
		ArrayList<Fragment> fragments = new ArrayList<Fragment>();
		fragments.add(inicioFragment);
		fragments.add(myMapFragment);		
		

		mSectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager(), fragments);
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);
		
		mViewPager.setCurrentItem(0);		
		

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

		System.out.println("Latitud: "+location.getLatitude()+", Longitud: "+location.getLongitude()+
				"\n ");

		//Obtenemos la MAC del dispositivo a traves del objeto WifiManager
		WifiManager manager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = manager.getConnectionInfo();
		String macAddress = info.getMacAddress();
		
		//Indicamos la url del servicio
		String urlPath = "http://wmap.herobo.com/wmap/servicio-obtener-posiciones.php?id_usuario="+macAddress.hashCode()+"&latitud="+latitud+"&longitud="+longitud+"&radio=10&fecha=2000-10-10&nombre="+macAddress.hashCode()+"&mensaje=hola&guardar=1&obtener=1";
		Map<String, String> parametros = new HashMap<String, String>();
		
		//Le pasamos los parámetros al Map
		parametros.put("host", urlPath);
		parametros.put("latitud", latitud.toString());
		parametros.put("longitud", longitud.toString());
		
		//Ejecutamos el servicio-obtener-posiciones
		new ServicioPosiciones(gm).execute(parametros);
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
