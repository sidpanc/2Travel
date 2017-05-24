package com.a2travel.models;

import android.os.Parcel;
import android.os.Parcelable;

public class HotelModel extends ElementoAdapter implements Parcelable {
    private String direccion_hotel;
    private String fecha_checkin;
    private String fecha_checkout;
    private String hora_checkin;
    private String hora_checkout;

    public HotelModel(Parcel p){
        this(p.readString(), p.readString(), p.readString(), p.readString(), p.readString(),
                p.readString(), p.readString());
    }

    public HotelModel(String nombre_hotel, String direccion_hotel, String fecha_checkin,
                      String fecha_checkout, String hora_checkin, String hora_checkout, String nombre_viaje){
        this(-1, nombre_hotel,direccion_hotel,fecha_checkin, fecha_checkout, hora_checkin, hora_checkout,
                nombre_viaje);
    }

    public HotelModel(int id, String nombre_hotel, String direccion_hotel, String fecha_checkin,
                      String fecha_checkout, String hora_checkin, String hora_checkout, String nombre_viaje){
        super(id, nombre_hotel, nombre_viaje);
        this.direccion_hotel = direccion_hotel;
        this.fecha_checkin = fecha_checkin;
        this.fecha_checkout = fecha_checkout;
        this.hora_checkin = hora_checkin;
        this.hora_checkout = hora_checkout;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(getNombre());
        dest.writeString(direccion_hotel);
        dest.writeString(fecha_checkin);
        dest.writeString(fecha_checkout);
        dest.writeString(hora_checkin);
        dest.writeString(hora_checkout);
        dest.writeString(getNombreViaje());
    }

    public static Parcelable.Creator<HotelModel> CREATOR = new Parcelable.Creator<HotelModel>(){
        public HotelModel createFromParcel(Parcel source){
            return new HotelModel(source);
        }
        public HotelModel[] newArray(int size){
            return new HotelModel[size];
        }
    };

    @Override
    public String getDireccion() {
        return direccion_hotel;
    }

    @Override
    public String getFecha1() {
        return fecha_checkin;
    }

    @Override
    public String getFecha2() {
        return fecha_checkout;
    }

    @Override
    public String getHora1() {
        return hora_checkin;
    }

    @Override
    public String getHora2() {
        return hora_checkout;
    }


}
