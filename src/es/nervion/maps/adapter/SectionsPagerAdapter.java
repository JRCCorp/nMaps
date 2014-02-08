package es.nervion.maps.adapter;

import java.util.ArrayList;
import java.util.Locale;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;
import android.view.ViewGroup;
import es.nervion.maps.activity.R;
import es.nervion.maps.activity.TabsActivity;
import es.nervion.maps.fragment.InicioFragment;
import es.nervion.maps.fragment.MyMapFragment;

public class SectionsPagerAdapter extends FragmentPagerAdapter {
	
	private TabsActivity activity;
	private ArrayList<Fragment> fragments;

	public SectionsPagerAdapter(TabsActivity activity, FragmentManager fm, ArrayList<Fragment> fragments) {
		super(fm);
		this.activity = activity;
		this.fragments = fragments;
	}

	@Override
	public Fragment getItem(int position) {
		return fragments.get(position);		
	}

	@Override
	public int getCount() {
		return fragments.size();
	}

	@Override
	public CharSequence getPageTitle(int position) {
		Locale l = Locale.getDefault();
		switch (position) {
		case 0:
			return activity.getString(R.string.titulo_preferencias).toUpperCase(l);
		case 1:
			return activity.getString(R.string.titulo_inicio).toUpperCase(l);
		case 2:
			return activity.getString(R.string.titulo_mapa).toUpperCase(l);
		}
		return "Seccion";
	}
	
	@Override
	public void destroyItem(ViewGroup container, int position, Object object){
		if(position==1){
			((InicioFragment) this.getItem(position)).onMyDestroy();
		}else if(position==2){
			((MyMapFragment) this.getItem(position)).onMyDestroy();
		}else{
			super.destroyItem(container, position, object);
		}
		
	}
	
	@Override
	public Object instantiateItem(ViewGroup container, int position){
		return super.instantiateItem(container, position);
	}

}
