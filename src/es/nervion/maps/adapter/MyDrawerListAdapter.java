package es.nervion.maps.adapter;

import java.util.ArrayList;
import java.util.Date;

import es.nervion.maps.activity.R;
import es.nervion.maps.clase.Mensaje;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class MyDrawerListAdapter extends ArrayAdapter<Mensaje> {

	private ArrayList<Mensaje> mensajes;
	private Activity activity;

	public MyDrawerListAdapter(Activity context, int resource, ArrayList<Mensaje> mensajes) {
		super(context, resource, mensajes);

		this.activity = context;
		this.mensajes = mensajes;


	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent){
		//View row = super.getView(position, convertView, parent); Para heredear la vista del padre
		View row = convertView; //Vista "desaparecida" (Reciclaje, importante)

		ViewHolder holder; //Coleccion de objetos de lista (Ahorrar trabajo CPU)
		if(row == null){
			LayoutInflater inf = activity.getLayoutInflater();
			row = inf.inflate(R.layout.row_drawer_list, parent, false);

			holder = new ViewHolder();
			holder.setNombre((TextView) (row.findViewById(R.id.txtNombreDrawerList)));
			holder.setFecha((TextView) (row.findViewById(R.id.txtFechaDrawerList)));
			holder.setMensaje((TextView) (row.findViewById(R.id.txtMensajeDrawerList)));
			row.setTag(holder);
		}else{
			holder = (ViewHolder) row.getTag();
		}

		holder.getNombre().setText(mensajes.get(position).getNombre());
		//Date hoy = new Date();
		Date fechaMensaje = mensajes.get(position).getFecha();
		String tiempo = " hace ";
		String unidad = " segundos";
		Long mseg = segundosDiferencia(fechaMensaje);
		if(mseg>59){
//			mseg = horasDiferencia(mseg);
//			mseg -= mseg*3600;
//			long minutos = mseg /60;
//			mseg -= minutos*60;
			mseg = mseg / 60;
			unidad = " minutos";
			if (mseg < 2){
				unidad = " minuto";
			}else if (mseg >59){
				tiempo = " hace más de ";
				unidad = " hora";
			}
		}
		//holder.getFecha().setText(mensajes.get(position).getFecha().toLocaleString());
		holder.getFecha().setText(tiempo+mseg.toString()+unidad);
		holder.getMensaje().setText(mensajes.get(position).getMensaje());

		return row;
	}

	public long segundosDiferencia(Date date) {
		return (new Date().getTime() - date.getTime()) / 1000;
	}
	
	public long horasDiferencia(long segundos) {
		return (segundos / 3600);
	}
	
	
	

	class ViewHolder{

		private TextView nombre, fecha, mensaje;

		public ViewHolder(){
		}

		public TextView getNombre() {
			return nombre;
		}

		public void setNombre(TextView nombre) {
			this.nombre = nombre;
		}

		public TextView getFecha() {
			return fecha;
		}

		public void setFecha(TextView fecha) {
			this.fecha = fecha;
		}

		public TextView getMensaje() {
			return mensaje;
		}

		public void setMensaje(TextView mensaje) {
			this.mensaje = mensaje;
		}



	}

}
