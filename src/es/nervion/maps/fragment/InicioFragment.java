package es.nervion.maps.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import es.nervion.maps.activity.R;
import es.nervion.maps.listener.InicioLoadedListener;

public class InicioFragment extends Fragment{

	private Button btnPeticion;
	private TextView txtInicio;
	
	private InicioLoadedListener inicioLoadedListener;


	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment		
		return inflater.inflate(R.layout.fragment_inicio, container, false);
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		txtInicio = (TextView) this.getActivity().findViewById(R.id.txtInicio);

		btnPeticion = (Button) this.getActivity().findViewById(R.id.btnPeticion);
		
		inicioLoadedListener.onInicioLoaded(btnPeticion);
		
		
	}
	
	//Setter de InicioLoadedListener
	public void setInicioLoadedListener(InicioLoadedListener ill){
		inicioLoadedListener = ill;
	}
	



}
