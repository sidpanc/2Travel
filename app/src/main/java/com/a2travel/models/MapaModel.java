package com.a2travel.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Aitor on 10/04/2017.
 */

public class MapaModel extends ElementoAdapter implements Parcelable {
    private String path;

    public MapaModel(Parcel p){
        this(p.readString(), p.readString(), p.readString());
    }

    public MapaModel(String nombre, String path, String nombre_viaje) {
        this(-1, nombre, path, nombre_viaje);
        this.path = path;
    }

    public MapaModel(int id, String nombre, String path, String nombre_viaje) {
        super(id, nombre, nombre_viaje);
        this.path = path;
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(getNombre());
        dest.writeString(path);
        dest.writeString(getNombreViaje());
    }

    public static Parcelable.Creator<MapaModel> CREATOR = new Parcelable.Creator<MapaModel>(){
        public MapaModel createFromParcel(Parcel source){ return new MapaModel(source); }
        public MapaModel[] newArray(int size) {
            return new MapaModel[size];
        }

    };

}
