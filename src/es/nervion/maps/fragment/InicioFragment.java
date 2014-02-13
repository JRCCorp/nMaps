package es.nervion.maps.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import es.nervion.maps.activity.R;
import es.nervion.maps.activity.TabsActivity;
import es.nervion.maps.listener.InicioListener;
import android.graphics.Bitmap;

public class InicioFragment extends Fragment{

	private InicioListener inicioLoadedListener;
	private WebView webview;
	private ImageView imgLogo;

	private boolean cargadoFinal = false, noCargado = true;



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
		imgLogo = (ImageView) this.getActivity().findViewById(R.id.imgLogo);
		webview = (WebView) this.getActivity().findViewById(R.id.webview);
		
		
		webview.setWebViewClient(new WebViewClient(){
			
			@Override
			public void onReceivedError(WebView view,int errorCode,String description,String failingUrl){
				super.onReceivedError(view, errorCode, description, failingUrl);
				webview.setVisibility(View.INVISIBLE);
		    	imgLogo.setVisibility(View.VISIBLE);
			}
			
			@Override
			public void onLoadResource(WebView view, String url){
				super.onLoadResource(view, url);
				imgLogo.setVisibility(View.INVISIBLE);
				webview.setVisibility(View.VISIBLE);
			}


		});
		
		webview.loadUrl("http://wmap.herobo.com");


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

}
