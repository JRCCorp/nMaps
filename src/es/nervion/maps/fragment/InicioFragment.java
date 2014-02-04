package es.nervion.maps.fragment;

import android.app.Fragment;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import es.nervion.maps.activity.R;
import es.nervion.maps.listener.InicioListener;

public class InicioFragment extends Fragment implements View.OnClickListener{

	private ImageView imgCarga, imgCarga2;
	
	private InicioListener inicioLoadedListener;


	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment	
		return inflater.inflate(R.layout.fragment_inicio, container, false);
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setRetainInstance(true);
		
		//imgCarga = (ImageView) this.getActivity().findViewById(R.id.imgCarga);
		imgCarga2 = (ImageView) this.getActivity().findViewById(R.id.imgCarga2);
//		Animation escala = AnimationUtils.loadAnimation(getActivity(), R.drawable.prueba_imagen_carga);
//		imgCarga.startAnimation(escala);
		
//		AnimationDrawable animacionCarga;
		//Animation giraAumenta = AnimationUtils.loadAnimation(getActivity(), R.drawable.gira_aumenta_imagen);
//		imgCarga.setBackgroundResource(R.anim.cambio_imagen_carga);
//		animacionCarga = (AnimationDrawable) imgCarga.getBackground();
//		animacionCarga.start();
		//imgCarga.setAnimation(giraAumenta);
		Animation gira = AnimationUtils.loadAnimation(getActivity(), R.drawable.gira_imagen);
		imgCarga2.setAnimation(gira);
			
	}
	
	//Setter de InicioLoadedListener
	public void setInicioLoadedListener(InicioListener ill){
		inicioLoadedListener = ill;
	}

	@Override
	public void onClick(View v) {
		
		switch (v.getId()) {
		default:
			break;
		}
		
	}
	



}
