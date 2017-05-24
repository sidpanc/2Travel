package com.a2travel.models;

import android.os.Parcel;
import android.os.Parcelable;

public class VueloModel extends ElementoAdapter implements Parcelable{
    private String identificador;
    private String fecha_vuelo;
    private String hora_vuelo;

    public VueloModel(Parcel p){
        this(p.readString(), p.readString(), p.readString(), p.readString(), p.readString());
    }

    public VueloModel(String identificador, String nombre, String fecha_vuelo, String hora_vuelo, String nombre_viaje){
        this(-1, identificador, nombre, fecha_vuelo, hora_vuelo, nombre_viaje);
    }

    public VueloModel(int ID, String identificador, String nombre, String fecha_vuelo, String hora_vuelo, String nombre_viaje){
        super(ID, nombre, nombre_viaje);
        this.identificador = identificador;
        this.fecha_vuelo = fecha_vuelo;
        this.hora_vuelo = hora_vuelo;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(identificador);
        dest.writeString(getNombre());
        dest.writeString(fecha_vuelo);
        dest.writeString(hora_vuelo);
        dest.writeString(getNombreViaje());
    }

    public static Parcelable.Creator<VueloModel> CREATOR = new Parcelable.Creator<VueloModel>(){
        public VueloModel createFromParcel(Parcel source){
            return new VueloModel(source);
        }
        public VueloModel[] newArray(int size){
            return new VueloModel[size];
        }
    };

    //@Override
    public String getId_vuelo() {
        return identificador;
    }

    //@Override
    public String getFecha_vuelo() {
        return fecha_vuelo;
    }

    public String getHora_vuelo(){
        return hora_vuelo;
    }

}
