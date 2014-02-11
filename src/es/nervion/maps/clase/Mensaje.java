package es.nervion.maps.clase;

import java.util.Date;

import android.os.Parcel;
import android.os.Parcelable;

public class Mensaje implements Parcelable {

	private static int numMensaje = 0;
	private String nombre, mensaje;
	private Date fecha;

	public Mensaje(){

		nombre = "Nombre "+numMensaje;
		mensaje = "Mensaje "+numMensaje;
		fecha = new Date(1991, 10, 10);

		numMensaje++;

	}

	public Mensaje(String nombre, String mensaje, Date fecha){

		this.nombre = nombre;
		this.mensaje = mensaje;
		this.fecha = fecha;

		numMensaje++;

	}
	
	public Mensaje(Parcel in){
        String[] data = new String[3];

        in.readStringArray(data);
        this.nombre = data[0];
        this.mensaje = data[1];
        this.fecha = new Date();
    }

	public static int getNumMensaje() {
		return numMensaje;
	}

	public static void setNumMensaje(int numMensaje) {
		Mensaje.numMensaje = numMensaje;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getMensaje() {
		return mensaje;
	}

	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeStringArray(new String[] {this.nombre,
                this.mensaje,
                ""});
		
	}
	
	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Mensaje createFromParcel(Parcel in) {
            return new Mensaje(in); 
        }

        public Mensaje[] newArray(int size) {
            return new Mensaje[size];
        }
    };




}
