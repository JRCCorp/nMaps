package es.nervion.maps.activity;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
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
	
	@Override
	protected void onDestroy(){
		super.onDestroy();
		if(sp!=null){
			sp.cancel(true);
		}		
	}
	
	public void peticionPost(){
				
		//Ejecutamos el servicio-obtener-posiciones
		sp = new ServicioPosiciones(this, 30000);
		sp.execute();
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


	/* Implementamos el m�todo onMapLoaded recibido de MyMapFragment */
	@Override
	public void onMapLoaded(GoogleMap gm) {
		/* Registrar acciones de servicio y broadcastReceiver */
		if(mViewPager.getCurrentItem()==2){
			peticionPost();
		}
		
	}


	/* Implementamos el m�todo onInicioClick recibido de MyMapFragment */
	@Override
	public void onInicioClick(Button btn) {

	}

	/* Implementamos el m�todo onPreferencesChange recibido desde PreferenciasFragment */
	@Override
	public void onPreferencesChange() {

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
		
		if(myMapFragment.getMap()!=null && position==2){
			peticionPost();
		}else{
			if(sp!=null){
				sp.cancel(true);
			}			
		}

	}

	@Override
	public void onMapFragmentLoaded() {
		
		
		
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



}
