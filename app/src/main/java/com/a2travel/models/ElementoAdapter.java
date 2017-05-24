package com.a2travel.models;

public abstract class ElementoAdapter implements Elemento {
    private int ID;
    private String nombre;
    private String nombre_viaje;

    public ElementoAdapter(int id, String nombre, String nombre_viaje){
        this.ID = id;
        this.nombre_viaje = nombre_viaje;
        this.nombre = nombre;
    }

    @Override
    public String getNombreViaje() {
        return nombre_viaje;
    }

    @Override
    public int getId() {
        return ID;
    }

    @Override
    public void setId(int ID) {
        this.ID = ID;
    }

    @Override
    public String getNombre() {
        return nombre;
    }

    @Override
    public String getDescripcion() {
        return null;
    }

    @Override
    public String getDireccion() {
        return null;
    }

    @Override
    public String getFecha1() {
        return null;
    }

    @Override
    public String getFecha2() {
        return null;
    }

    @Override
    public String getHora1() {
        return null;
    }

    @Override
    public String getHora2() {
        return null;
    }

    @Override
    public String getPath() {
        return null;
    }

    public static int getTipo(Elemento e){
        if(e instanceof HotelModel)
            return TIPO_HOTEL;
        else if(e instanceof VueloModel)
            return TIPO_VUELO;
        else if(e instanceof MapaModel)
            return TIPO_MAPA;
        else if(e instanceof TareaModel)
            return ((TareaModel) e).isCompletada() ? TIPO_TAREA_COMPLETADA : TIPO_TAREA;
            //return TIPO_TAREA;
        else
            return TIPO_PREDICCION;
    };
}
