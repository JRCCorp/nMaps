package es.nervion.maps.fragment;

import java.util.ArrayList;

import android.app.Fragment;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.android.gms.internal.dr;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapLoadedCallback;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import es.nervion.maps.activity.R;
import es.nervion.maps.activity.TabsActivity;
import es.nervion.maps.adapter.MyDrawerListAdapter;
import es.nervion.maps.clase.Mensaje;
import es.nervion.maps.listener.MapListener;


public class MyMapFragment extends Fragment implements OnMapLoadedCallback {

	private MapListener mapLoadedListener;
	private ArrayList<Marker> marcadores;
	private ArrayList<Mensaje> mensajes;
	private GoogleMap map;

	private String[] opcionesMenu;
	private DrawerLayout drawerLayout;
	private ListView drawerList;
	
	
	public MyMapFragment(){
		marcadores = new ArrayList<Marker>();
		mensajes = new ArrayList<Mensaje>();
	}


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
		setHasOptionsMenu(true);
	}
	
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.mapa, menu);
        super.onCreateOptionsMenu(menu, inflater);
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
 
        ArrayList<Mensaje> mensajes = new ArrayList<Mensaje>();
        mensajes.add(new Mensaje());
        mensajes.add(new Mensaje());
        mensajes.add(new Mensaje());
        
        drawerList.setAdapter(new MyDrawerListAdapter(getActivity(), R.layout.row_drawer_list, mensajes));
		

		getMyMap().setMyLocationEnabled(true);
		getMyMap().setOnMapLoadedCallback(this);

		mapLoadedListener.onMapFragmentLoaded();
		
		if(marcadores.size()>0){
			for(Marker m: marcadores){
				getMyMap().addMarker(new MarkerOptions()
				.position(new LatLng(m.getPosition().latitude, m.getPosition().longitude))
				.title(m.getTitle())
				.snippet(m.getSnippet()));
			}
		}
		Log.d("MyMapFragment", "Tamanio Marcadores: "+marcadores.size());
        

	}
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    case R.id.action_chat:
	    	if(this.getDrawerLayout().isDrawerOpen(Gravity.END)){
	    		this.getDrawerLayout().closeDrawer(Gravity.END);
			}else{
				this.getDrawerLayout().openDrawer(Gravity.END);
			}
	    	break;
	    }
	    return true;
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
		
		drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
		//drawerLayout.openDrawer(Gravity.END);

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


	public DrawerLayout getDrawerLayout(){
		return drawerLayout;
	}
	
	public ListView getDrawerList(){
		return drawerList;
	}
	
	
	public ArrayList<Marker> getMarcadores(){
		return marcadores;
	}
	
	public void setMarcadores(ArrayList<Marker> marcadores){
		this.marcadores = marcadores;
	}
	
	public ArrayList<Mensaje> getMensajes(){
		return mensajes;
	}
	



}