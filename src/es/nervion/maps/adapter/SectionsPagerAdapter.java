package es.nervion.maps.adapter;

import java.util.ArrayList;
import java.util.Locale;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;
import es.nervion.maps.activity.R;

public class SectionsPagerAdapter extends FragmentPagerAdapter {
	
	private Activity activity;
	private ArrayList<Fragment> fragments;

	public SectionsPagerAdapter(Activity activity, FragmentManager fm, ArrayList<Fragment> fragments) {
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
}
