package es.nervion.maps.fragment;

import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapLoadedCallback;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;

import es.nervion.maps.listener.MapLoadedListener;


public class MyMapFragment extends MapFragment implements OnMapLoadedCallback {
	/**
	 * The fragment argument representing the section number for this
	 * fragment.
	 */
	
	private MapLoadedListener mapLoadedListener;
	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		setRetainInstance(true);
		getMap().setMyLocationEnabled(true);
		getMap().setOnMapLoadedCallback(this);
 
	}
	
	/* Implementacion de onMapLoadedCallback */
	@Override
	public void onMapLoaded() {
		
		Location location = getMap().getMyLocation();

	    LatLng myLocation = null;
		if (location != null) {
	        myLocation = new LatLng(location.getLatitude(),
	                location.getLongitude());
	        getMap().animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation,  12.0f));
	        
	        getMap().setMapType(GoogleMap.MAP_TYPE_HYBRID);
	    }
		
		mapLoadedListener.onMapLoaded(this.getMap());

	}
	
	/* Setter de MapLoadedListener */
	public void setMapLoadedListener(MapLoadedListener mll){
		this.mapLoadedListener = mll;
	}



}