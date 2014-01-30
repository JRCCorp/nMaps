package es.nervion.maps.fragment;

import es.nervion.maps.activity.R;
import android.os.Bundle;
import android.preference.PreferenceFragment;

public class PreferenciasFragment extends PreferenceFragment {
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.fragment_preferences);
    }

}
