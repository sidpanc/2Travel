package com.a2travel.core;

import android.os.Parcel;
import android.os.Parcelable;

import com.a2travel.models.Elemento;
import com.a2travel.models.ElementoAdapter;

import java.util.ArrayList;

public class Viaje implements Parcelable {
    private int ID;
    private String pais;
    private String nombre_viaje;
    private String ciudad;
    private String fecha_salida;
    private String fecha_regreso;
    private ArrayList<Elemento> elementos;

    /** Constructor para crear un viaje a partir de un Parcel (Ver Parcelable.Creator más abajo) */
    public Viaje(Parcel p){
        this(p.readString(), p.readString(), p.readString(), p.readString(), p.readString());
    }

    public Viaje(String nombre_viaje, String pais, String ciudad, String fecha_salida, String fecha_regreso){
        this(-1, nombre_viaje, pais, ciudad, fecha_salida, fecha_regreso);
    }

    public Viaje(int ID, String nombre_viaje, String pais, String ciudad, String fecha_salida, String fecha_regreso){
        this.ID = ID;
        this.nombre_viaje = nombre_viaje;
        this.pais = pais;
        this.ciudad = ciudad;
        this.fecha_salida = fecha_salida;
        this.fecha_regreso = fecha_regreso;

        elementos = new ArrayList<Elemento>();
    }

    public static boolean hasTiempo(Viaje v){
        if(v.elementos.size() > 0){
            for(Elemento iter_e : v.elementos){
                if(ElementoAdapter.getTipo(iter_e) == Elemento.TIPO_PREDICCION)
                    return true;
            }
        }

        return false;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    /** Almacena en el parcel todos los campos del viaje */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(nombre_viaje);
        dest.writeString(pais);
        dest.writeString(ciudad);
        dest.writeString(fecha_salida);
        dest.writeString(fecha_regreso);
    }

    /** Se llama en intent.getParcelableExtra */
    public static Parcelable.Creator<Viaje> CREATOR = new Parcelable.Creator<Viaje>(){
        public Viaje createFromParcel(Parcel source){
            return new Viaje(source);
        }
        public Viaje[] newArray(int size){
            return new Viaje[size];
        }
    };


    public void setID(int ID) {
        this.ID = ID;
    }

    public int getID(){ return ID; }

    public void addElemento(Elemento e){
        elementos.add(e);
    }

    public void resetElementos(){
        elementos.removeAll(elementos);
    }

    public ArrayList<Elemento> getElementos(){
        return elementos;
    }

    public int getElementosCount(){
        return elementos.size();
    }

    public String getPais() {
        return pais;
    }

    public String getNombre_viaje() {
        return nombre_viaje;
    }

    public String getCiudad() {
        return ciudad;
    }

    public String getFecha_salida() {
        return fecha_salida;
    }

    public String getFecha_regreso() {
        return fecha_regreso;
    }

}
