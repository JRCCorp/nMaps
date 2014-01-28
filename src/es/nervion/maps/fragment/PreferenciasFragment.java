package es.nervion.maps.fragment;

import es.nervion.maps.activity.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class PreferenciasFragment extends Fragment implements View.OnClickListener {

	Button btn;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment		
		return inflater.inflate(R.layout.fragment_preferencias, container, false);
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		btn = (Button) this.getActivity().findViewById(R.id.btnHola);
		btn.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {



	}






}
