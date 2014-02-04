package es.nervion.maps.listener;

import com.google.android.gms.maps.GoogleMap;

public interface MapListener {
	
	public void onMapLoaded(GoogleMap gm);
	
	public void onMapFragmentLoaded();
	

}
