package es.nervion.maps.fragment;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;
import es.nervion.maps.activity.R;
import es.nervion.maps.activity.TabsActivity;
import es.nervion.maps.canvas.BrujulaCanvas;
import es.nervion.maps.listener.InicioListener;

public class InicioFragment extends Fragment implements View.OnClickListener{

	private ImageButton brujula;
	private static SensorManager manejaSensor;
	private BrujulaCanvas canvas;
	private Sensor sensorAccelerometer;
	private Sensor sensorMagneticField;
	private LinearLayout lyBrujula;
	private InicioListener inicioLoadedListener;
	private Button btnServicios;
	private float[] valuesAccelerometer;
	private float[] valuesMagneticField;

	private float[] matrixR;
	private float[] matrixI;
	private float[] matrixValues;

	private MenuItem actualizarServidor;
	

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment	
		return inflater.inflate(R.layout.fragment_inicio, container, false);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);			
		setHasOptionsMenu(true);
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setRetainInstance(true);

		((TabsActivity) getActivity() ).getViewPager().setCurrentItem(1);


		canvas = new BrujulaCanvas(this.getActivity());
		lyBrujula = (LinearLayout) this.getActivity().findViewById(R.id.layoutBrujula);
		lyBrujula.addView(canvas);

		manejaSensor = (SensorManager) this.getActivity().getSystemService(Context.SENSOR_SERVICE);
		sensorAccelerometer = manejaSensor.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		sensorMagneticField = manejaSensor.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
		if (sensorAccelerometer != null) {
			manejaSensor.registerListener(mSensor, sensorAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
		} else {
			Toast.makeText(this.getActivity(), "Sensor no reconocido", Toast.LENGTH_LONG).show();
			//getActivity().finish();
		}
		if(sensorMagneticField != null){
			manejaSensor.registerListener(mSensor, sensorMagneticField, SensorManager.SENSOR_DELAY_NORMAL);
		}else{
			Toast.makeText(this.getActivity(), "Sensor no reconocido", Toast.LENGTH_LONG).show();
			//getActivity().finish();
		}

		valuesAccelerometer = new float[3];
		valuesMagneticField = new float[3];

		matrixR = new float[9];
		matrixI = new float[9];
		matrixValues = new float[3];

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

		btnServicios = (Button) getActivity().findViewById(R.id.btnServicioMaestro);
		btnServicios.setOnClickListener(this);

	}

<<<<<<< HEAD

	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		menu.clear();
		inflater.inflate(R.menu.inicio, menu);
		super.onCreateOptionsMenu(menu, inflater);

=======
	public void   onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.inicio, menu);
        super.onCreateOptionsMenu(menu, inflater);
   }
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_refresh:
			actualizarServidor = item;
			new SyncData().execute();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
>>>>>>> 39012c442189721070414471512564491da5f10c
	}



	private SensorEventListener mSensor = new SensorEventListener() {

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
		}

		@Override
		public void onSensorChanged(SensorEvent event) {
			switch(event.sensor.getType()){
			case Sensor.TYPE_ACCELEROMETER:
				for(int i =0; i < 3; i++){
					valuesAccelerometer[i] = event.values[i];
				}
				break;
			case Sensor.TYPE_MAGNETIC_FIELD:
				for(int i =0; i < 3; i++){
					valuesMagneticField[i] = event.values[i];
				}
				break;
			}

			boolean success = SensorManager.getRotationMatrix(
					matrixR,
					matrixI,
					valuesAccelerometer,
					valuesMagneticField);

			if(success){
				SensorManager.getOrientation(matrixR, matrixValues);

				double azimuth = Math.toDegrees(matrixValues[0]);
				double pitch = Math.toDegrees(matrixValues[1]);
				double roll = Math.toDegrees(matrixValues[2]);

				//		   readingAzimuth.setText("Azimuth: " + String.valueOf(azimuth));
				//		   readingPitch.setText("Pitch: " + String.valueOf(pitch));
				//		   readingRoll.setText("Roll: " + String.valueOf(roll));
				canvas.updateData(matrixValues[0]);
			}
		}


	};

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (sensorAccelerometer != null || sensorMagneticField != null) {
			manejaSensor.unregisterListener(mSensor);
		}
	}


	public void onMyDestroy(){
		if(getActivity()!=null){

			Fragment f = getActivity().getFragmentManager().findFragmentById(R.layout.fragment_inicio);

			if (f != null) {
				getActivity().getFragmentManager()
				.beginTransaction().remove(f).commit();
			}
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
		case R.id.btnServicioMaestro:
			inicioLoadedListener.onInicioClick(btnServicios);
			break;
		default:
			break;
		}

	}
	
	private class SyncData extends AsyncTask<String, Void, String> {
		@SuppressLint("NewApi")
		@Override
		protected void onPreExecute() {
			actualizarServidor.setActionView(R.layout.action_progressbar);
			actualizarServidor.expandActionView();
		}

		@Override
		protected String doInBackground(String... params) {
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return null;
		}

		@SuppressLint("NewApi")
		@Override
		protected void onPostExecute(String result) {
			actualizarServidor.collapseActionView();
			actualizarServidor.setActionView(null);
		}
	};




}
