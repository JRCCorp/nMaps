package es.nervion.maps.activity;

import java.util.ArrayList;
import java.util.Locale;

import com.google.android.gms.maps.GoogleMap;

import es.nervion.maps.activity.R;
import es.nervion.maps.adapter.SectionsPagerAdapter;

import es.nervion.maps.fragment.InicioFragment;
import es.nervion.maps.fragment.MyMapFragment;
import es.nervion.maps.fragment.PreferenciasFragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;

public class TabsActivity extends FragmentActivity {

	
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
		inicioFragment = new InicioFragment();
		myMapFragment = new MyMapFragment();
		
		ArrayList<Fragment> fragments = new ArrayList<Fragment>();
		fragments.add(preferenciasFragment);
		fragments.add(inicioFragment);
		fragments.add(myMapFragment);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				this, getSupportFragmentManager(), fragments);

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);
		
		mViewPager.setCurrentItem(1);		
		

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		getMenuInflater().inflate(R.menu.tabs, menu);
		return true;
	}

	
	
	
	public MyMapFragment getMyMapFragment() {
		return myMapFragment;
	}
	
	public InicioFragment getInicioFragment() {
		return inicioFragment;
	}


}
