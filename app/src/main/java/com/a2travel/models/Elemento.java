package com.a2travel.models;

public interface Elemento {
    int TIPO_HOTEL = 1;
    int TIPO_VUELO = 2;
    int TIPO_MAPA = 3;
    int TIPO_TAREA = 4;
    int TIPO_PREDICCION = 5;
    int TIPO_TAREA_COMPLETADA = 6;

    String getNombreViaje();

    /** El valor de la columna ID del elemento, con este método obligamos a que todas las clases lo implementen
     * Lo usaremos para manejar fácilmente updates y deletes de la base de datos */
    int getId();
    void setId(int ID);

    /** Campo dinámico, será el nombre del hotel, el origen-destino del vuelo, la ciudad del mapa guardado, etc */
    String getNombre();
    String getDescripcion();
    /** Será nulo en todos los elementos salvo en hoteles*/
    String getDireccion();
    /** Fecha 1 es dinámico, si es un hotel será fecha de check_in, si es de un vuelo, será salida,
     * si es una tarea o un mapa, será nulo, etc.*/
    String getFecha1();
    /** Fecha 1 es dinámico, si es un hotel será fecha de check_out,
     * si es un vuelo, tarea o un mapa, será nulo, etc.*/
    String getFecha2();

    /**
     * Corresponde a la hora de check_in en hoteles.
     * @return
     */
    String getHora1();

    /**
     * Corresponde a la hora de check_out de los hoteles.
     * @return
     */
    String getHora2();

    /**
     * La ruta en la que está guardada la imagen del mapa.
     * @return
     */
    String getPath();

}
