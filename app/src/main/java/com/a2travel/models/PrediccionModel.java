package com.a2travel.models;

import com.a2travel.core.Viaje;

public class PrediccionModel extends ElementoAdapter{

    public PrediccionModel(Viaje v){
        this(-1, "Predicción de " + v.getCiudad(), v.getNombre_viaje());
    }

    public PrediccionModel(int id, String nombre, String nombre_viaje){
        super(id, nombre, nombre_viaje);
    }



}
