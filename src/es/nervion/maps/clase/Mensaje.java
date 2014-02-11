package es.nervion.maps.clase;

import java.util.Date;

public class Mensaje {

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




}
