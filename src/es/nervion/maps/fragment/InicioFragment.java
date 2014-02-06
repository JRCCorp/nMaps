package es.nervion.maps.fragment;

import android.app.Fragment;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;
import es.nervion.maps.activity.R;
import es.nervion.maps.canvas.BrujulaCanvas;
import es.nervion.maps.listener.InicioListener;

public class InicioFragment extends Fragment implements View.OnClickListener{

	private ImageButton brujula;
	private static SensorManager manejaSensor;
	private BrujulaCanvas canvas;
	private Sensor sensor;
	private LinearLayout lyBrujula;
	private InicioListener inicioLoadedListener;


	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment	
		return inflater.inflate(R.layout.fragment_inicio, container, false);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setRetainInstance(true);

		
		canvas = new BrujulaCanvas(this.getActivity());
		lyBrujula = (LinearLayout) this.getActivity().findViewById(R.id.layoutBrujula);
		lyBrujula.addView(canvas);

		manejaSensor = (SensorManager) this.getActivity().getSystemService(Context.SENSOR_SERVICE);
		sensor = manejaSensor.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		if (sensor != null) {
			manejaSensor.registerListener(mSensor, sensor, SensorManager.SENSOR_DELAY_NORMAL);
		} else {
			Toast.makeText(this.getActivity(), "Sensor no reconocido", Toast.LENGTH_LONG).show();
			//getActivity().finish();
		}
		
		//imgCarga = (ImageView) this.getActivity().findViewById(R.id.imgCarga);
		//		Animation escala = AnimationUtils.loadAnimation(getActivity(), R.drawable.prueba_imagen_carga);
		//		imgCarga.startAnimation(escala);

		//		AnimationDrawable animacionCarga;
		//Animation giraAumenta = AnimationUtils.loadAnimation(getActivity(), R.drawable.gira_aumenta_imagen);
		//		imgCarga.setBackgroundResource(R.anim.cambio_imagen_carga);
		//		animacionCarga = (AnimationDrawable) imgCarga.getBackground();
		//		animacionCarga.start();
		//imgCarga.setAnimation(giraAumenta);
//		brujula = (ImageButton) this.getActivity().findViewById(R.id.brujula);
//		brujula.setOnClickListener(this);
//		Animation gira = AnimationUtils.loadAnimation(getActivity(), R.drawable.gira_imagen);
//		brujula.setAnimation(gira);

	}

	
	
	private SensorEventListener mSensor = new SensorEventListener() {

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
		}

		@Override
		public void onSensorChanged(SensorEvent event) {
			float datos = event.values[0];
			canvas.updateData(datos);
		}

	};
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		if (sensor != null) {
			manejaSensor.unregisterListener(mSensor);
		}
	}
	
	//Setter de InicioLoadedListener
	public void setInicioLoadedListener(InicioListener ill){
		inicioLoadedListener = ill;
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
//		case R.id.brujula:
//			inicioLoadedListener.onInicioClick(brujula);
//			break;
		default:
			break;
		}

	}




}
