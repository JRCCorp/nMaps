package es.nervion.maps.adapter;

import java.util.ArrayList;
import java.util.Locale;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
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
		// Show 3 total pages.
		return fragments.size();
	}

	@Override
	public CharSequence getPageTitle(int position) {
		Locale l = Locale.getDefault();
		switch (position) {
		case 0:
			return activity.getString(R.string.title_section1).toUpperCase(l);
		case 1:
			return activity.getString(R.string.title_section2).toUpperCase(l);
		case 2:
			return activity.getString(R.string.title_section3).toUpperCase(l);
		}
		return "Seccion";
	}
}
