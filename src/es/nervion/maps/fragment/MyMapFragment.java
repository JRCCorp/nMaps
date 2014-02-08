package es.nervion.maps.fragment;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Fragment;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.DrawerLayout.DrawerListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapLoadedCallback;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import es.nervion.maps.activity.R;
import es.nervion.maps.activity.TabsActivity;
import es.nervion.maps.listener.MapListener;


public class MyMapFragment extends Fragment implements OnMapLoadedCallback {

	private MapListener mapLoadedListener;
	private ArrayList<Marker> marcadores;
	private GoogleMap map;

	private String[] opcionesMenu;
	private DrawerLayout drawerLayout;
	private ListView drawerList;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {


		View rootView = inflater.inflate(R.layout.fragment_my_map, container, false);

		map = ((MapFragment) getActivity().getFragmentManager()
				.findFragmentById(R.id.map)).getMap();

		return rootView;
	}

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);

	}

	public void onDestroyView() {
		super.onDestroyView();

		if(((TabsActivity) getActivity()).getServicioPosiciones()!=null){
			((TabsActivity) getActivity()).getServicioPosiciones().cancel(true);
		}


	}


	public void onMyDestroy(){
		if(getActivity()!=null){

			if(((TabsActivity) getActivity()).getServicioPosiciones()!=null){
				((TabsActivity) getActivity()).getServicioPosiciones().cancel(true);
			}

			Fragment f = getActivity().getFragmentManager().findFragmentById(R.id.map);

			if (f != null) {
				getActivity().getFragmentManager()
				.beginTransaction().remove(f).commit();
			}
		}
	}


	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		setRetainInstance(true);
		
		
		opcionesMenu = new String[] {"Opción 1", "Opción 2", "Opción 3"};
        drawerLayout = (DrawerLayout) getActivity().findViewById(R.id.drawer_layout);
        drawerList = (ListView) getActivity().findViewById(R.id.right_drawer);
 
        drawerList.setAdapter(new ArrayAdapter<String>(getActivity(), R.layout.row_drawer_list, R.id.txtItemDrawerList, opcionesMenu));
		

		marcadores = new ArrayList<Marker>();

		getMyMap().setMyLocationEnabled(true);
		getMyMap().setOnMapLoadedCallback(this);

		mapLoadedListener.onMapFragmentLoaded();		
        

	}

	/* Implementacion de onMapLoadedCallback */
	@Override
	public void onMapLoaded() {

		Location location = getMyMap().getMyLocation();

		LatLng myLocation = null;
		if (location != null) {
			myLocation = new LatLng(location.getLatitude(),
					location.getLongitude());
			getMyMap().animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation,  12.0f));

			getMyMap().setMapType(GoogleMap.MAP_TYPE_HYBRID);
		}

		mapLoadedListener.onMapLoaded(this.getMyMap());
		
		
		drawerLayout.openDrawer(Gravity.END);

	}

	/* Setter de MapLoadedListener */
	public void setMapLoadedListener(MapListener mll){
		this.mapLoadedListener = mll;
	}


	public void eliminarMarcadores(){
		synchronized (marcadores) {
			for(Marker m: marcadores){
				m.remove();
			}
		}
	}


	public void anadirMarcador(Marker m){
		synchronized (marcadores) {
			marcadores.add(m);
		}
	}


	public GoogleMap getMyMap(){
		return map;
	}




}