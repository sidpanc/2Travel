package com.a2travel.models;

public class TareaModel extends ElementoAdapter {
    private boolean completada;


//    public TareaModel(String nombre, String nombre_viaje){
//        this(-1, nombre,nombre_viaje);
//    }

//    public TareaModel(int id,String nombre, String nombre_viaje){
//        super(id, nombre, nombre_viaje);
//    }

    public TareaModel(String nombre, String nombre_viaje, int completada){
        this(-1, nombre,nombre_viaje, completada);
    }

    public TareaModel(int id,String nombre, String nombre_viaje, int completada){
        super(id, nombre, nombre_viaje);
        this.completada = completada==1? true : false; //sqlite no tiene getBoolean, así que en la tabla es un int
    }

    public void setCompletada(boolean completada) { this.completada = completada; }
    public boolean isCompletada() { return completada; }
}
