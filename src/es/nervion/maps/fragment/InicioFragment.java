package es.nervion.maps.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import es.nervion.maps.activity.R;
import es.nervion.maps.listener.InicioListener;

public class InicioFragment extends Fragment implements View.OnClickListener{

	private Button btnPeticion;
	private TextView txtInicio;
	
	private InicioListener inicioLoadedListener;


	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment	
		return inflater.inflate(R.layout.fragment_inicio, container, false);
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setRetainInstance(true);
		txtInicio = (TextView) this.getActivity().findViewById(R.id.txtInicio);
		btnPeticion = (Button) this.getActivity().findViewById(R.id.btnPeticion);
		
			
	}
	
	//Setter de InicioLoadedListener
	public void setInicioLoadedListener(InicioListener ill){
		inicioLoadedListener = ill;
	}

	@Override
	public void onClick(View v) {
		
		switch (v.getId()) {
		case R.id.btnPeticion:
			inicioLoadedListener.onInicioClick(btnPeticion);
			break;

		default:
			break;
		}
		
	}
	



}
