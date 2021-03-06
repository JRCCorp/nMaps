package es.nervion.maps.fragment;

import es.nervion.maps.activity.R;
import es.nervion.maps.listener.PreferencesListener;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.view.Menu;
import android.view.MenuInflater;

public class PreferenciasFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener {

	private PreferencesListener preferencesListener;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setRetainInstance(true);
		setHasOptionsMenu(true);
		
		addPreferencesFromResource(R.xml.fragment_preferences);

		for (int i = 0; i < getPreferenceScreen().getPreferenceCount(); i++) {
			iniciarSumario(getPreferenceScreen().getPreference(i));
		}
		
	}
	
	public void   onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
       // inflater.inflate(R.menu.inicio, menu);
        super.onCreateOptionsMenu(menu, inflater);
   }
	

	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
      updatePreferences(findPreference(key));
      preferencesListener.onPreferencesChange();
    }


	private void iniciarSumario(Preference p) {
		if (p instanceof PreferenceCategory) {
			PreferenceCategory cat = (PreferenceCategory) p;
			for (int i = 0; i < cat.getPreferenceCount(); i++) {
				iniciarSumario(cat.getPreference(i));
			}
		} else {
			updatePreferences(p);
		}
	}

	private void updatePreferences(Preference p) {
		if (p instanceof EditTextPreference) {
			EditTextPreference editTextPref = (EditTextPreference) p;
			p.setSummary(editTextPref.getText());
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
	}
	
	
	public void setPreferencesListener(PreferencesListener pl){
		this.preferencesListener = pl;
	}


}
