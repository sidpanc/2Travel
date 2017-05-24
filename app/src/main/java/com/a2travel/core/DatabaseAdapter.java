package com.a2travel.core;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.a2travel.models.Elemento;
import com.a2travel.models.HotelModel;
import com.a2travel.models.MapaModel;
import com.a2travel.models.TareaModel;
import com.a2travel.models.PrediccionModel;
import com.a2travel.models.VueloModel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class DatabaseAdapter {
    private static final int DATABASE_VERSION = 7;
    private static final String DATABASE_NAME = "2travel.db";

    //Tabla viajes
    public static final String TABLE_NAME_VIAJES = "VIAJES";
    private static final String COL_VIAJE_ID = "ID";
    private static final String COL_NOMBRE_VIAJE = "NOMBRE";
    private static final String COL_PAIS = "PAIS";
    private static final String COL_CIUDAD = "CIUDAD";
    private static final String COL_FECHA_SALIDA_VIAJE = "FECHA_SALIDA";
    private static final String COL_FECHA_REGRESO_VIAJE = "FECHA_REGRESO";
    private static final String[] ALL_COLUMNS_VIAJE = {COL_VIAJE_ID, COL_NOMBRE_VIAJE, COL_PAIS,
            COL_CIUDAD, COL_FECHA_SALIDA_VIAJE, COL_FECHA_REGRESO_VIAJE};

    //Tabla paises
    public static final String TABLE_NAME_PAISES = "PAISES";
    private static final String COL_NOMBRE_PAIS = "NOMBRE";
    private static final String COL_CODIGO_PAIS = "CODIGO";
    // private static final String COL_IDIOMA = "IDIOMA";
    private static final String COL_MONEDA = "MONEDA";

    //Tabla elemento
    public static final String TABLE_NAME_ELEMENTOS = "ELEMENTOS";
    private static final String COL_ELEMENTO_ID = "ID";
    private static final String COL_ELEMENTO_NOMBRE_VIAJE = "NOMBRE_VIAJE";
    private static final String COL_ELEMENTO_TIPO = "TIPO";
    private static final String COL_ELEMENTO_NOMBRE = "NOMBRE";
    private static final String COL_ELEMENTO_DESCRIPCION = "DESCRIPCION";
    private static final String COL_ELEMENTO_DIRECCION= "DIRECCION";
    private static final String COL_ELEMENTO_FECHA_1 = "FECHA_1";
    private static final String COL_ELEMENTO_FECHA_2 = "FECHA_2";
    private static final String COL_ELEMENTO_HORA_1 = "HORA_1";
    private static final String COL_ELEMENTO_HORA_2 = "HORA_2";
    private static final String COL_ELEMENTO_PATH = "PATH";
    private static final String COL_ELEMENTO_ID_VUELO = "ID_VUELO";
    private static final String COL_ELEMENTO_COMPLETADO = "COMPLETADO";
    private static final String[] ALL_COLUMNS_ELEMENTO = {COL_ELEMENTO_ID, COL_ELEMENTO_NOMBRE_VIAJE,
            COL_ELEMENTO_TIPO, COL_ELEMENTO_NOMBRE, COL_ELEMENTO_DESCRIPCION, COL_ELEMENTO_DIRECCION,
            COL_ELEMENTO_FECHA_1, COL_ELEMENTO_FECHA_2, COL_ELEMENTO_HORA_1, COL_ELEMENTO_HORA_2,
            COL_ELEMENTO_PATH, COL_ELEMENTO_ID_VUELO, COL_ELEMENTO_COMPLETADO};

    private static Context ctx;
    private static SQLiteDatabase db;
    private static ViajesDatabase viajesDatabase;

    public static void open(Context context) throws IOException {
        ctx = context;
        // Si no hay una instancia de la base de datos, crea una. Si la hay, la obtiene en modo RW.
        if(viajesDatabase == null){
            // Crea una instancia de la base de datos y comprueba si existe en /data/data/...
            // Si no existe, la copia desde assets. Si existe, la abre.
            viajesDatabase = new ViajesDatabase(context);

            File database = context.getDatabasePath(DATABASE_NAME);
            if(!database.exists()){
                db = viajesDatabase.getReadableDatabase();
                copyDatabase();
            }

            db = viajesDatabase.openDataBase();
        }else{
            db = viajesDatabase.getWritableDatabase();
        }
    }

    private static boolean copyDatabase(){
        try{
            InputStream inputStream = ctx.getAssets().open(DATABASE_NAME);
            String outputFileName = "data/data/" + ctx.getPackageName() + "/databases/" + DATABASE_NAME;
            OutputStream outputStream = new FileOutputStream(outputFileName);
            // Va copiando kilobyte a kilobyte la base de datos.
            byte []buffer = new byte[1024];
            int length = 0;
            while((length = inputStream.read(buffer)) > 0){
                outputStream.write(buffer, 0, length);

            }
            outputStream.flush();
            outputStream.close();
            return true;
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }

    }

    /** Devuelve un objeto tipo Cursor con los nombres de los viajes en cada fila.
     * Útil para añadir los items del navigation drawer.
     * */
    public static Cursor readNombresViajes(){
        Cursor result = db.query(TABLE_NAME_VIAJES, new String[]{COL_NOMBRE_VIAJE}, null, null, null, null, null);

        if(result != null)
            result.moveToFirst();

        return result;
    }

    /** Obtiene un viaje a partir de la ID. Útil para obtener el viaje añadido en determinada posición.
     * Utilizar getLastId(TABLE_NAME_VIAJES) para obtener el último. */
    public static Viaje readViaje(int viaje_ID){
        Cursor result = db.query(TABLE_NAME_VIAJES, ALL_COLUMNS_VIAJE, COL_VIAJE_ID + " = " + viaje_ID, null, null, null, null, null);
        result.moveToFirst();
        Viaje viaje = new Viaje(result.getInt(0), result.getString(1), result.getString(2), result.getString(3), result.getString(4), result.getString(5));
        return viaje;
    }

    /** Obtiene un viaje a partir del nombre del viaje. Útil para obtenerlo desde el navigation drawer. */
    public static Viaje readViaje(String nombre){
        Cursor result = db.query(TABLE_NAME_VIAJES, ALL_COLUMNS_VIAJE, COL_NOMBRE_VIAJE + "='"+nombre+"'", null, null, null, null, null);
        Viaje viaje = null;
        // Si existe el viaje, lo crea. De esta forma se devuelve null cuando no hay viaje y se puede validar que al añadir uno nuevo no exista ya.
        if(result.getCount() > 0) {
            result.moveToFirst();
            viaje = new Viaje(result.getInt(0), result.getString(1), result.getString(2), result.getString(3), result.getString(4), result.getString(5));
        }
        return viaje;
    }

    /** Añade un viaje a la base de datos.
     * @param v es el viaje que se desea guardar */
    public static boolean insertarViaje(Viaje v){
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_NOMBRE_VIAJE, v.getNombre_viaje());
        contentValues.put(COL_PAIS, v.getPais());
        contentValues.put(COL_CIUDAD, v.getCiudad());
        contentValues.put(COL_FECHA_SALIDA_VIAJE, v.getFecha_salida());
        contentValues.put(COL_FECHA_REGRESO_VIAJE, v.getFecha_regreso());

        long result = db.insert(TABLE_NAME_VIAJES, null, contentValues);
        if(result == -1){   //-1 = error:
            return false;
        }else{
            v.setID((int)result);
            return true;
        }
    }

    /** Elimina el viaje de la tabla VIAJES y todos sus elementos de la tabla ELEMENTOS */
    public static boolean deleteViaje(Viaje viaje){
        boolean deleted = db.delete(TABLE_NAME_VIAJES, COL_NOMBRE_VIAJE + " ='"+viaje.getNombre_viaje()+"'", null) > 0;
        if(deleted && viaje.getElementosCount() > 0){
            deleteElementosViaje(viaje.getNombre_viaje());
        }
        return deleted;
    }

    /** Devuelve un objeto tipo Cursor con los nombres de los países en cada fila.
     * Útil para añadir los items del AutocompleteTextView.
     * */
    public static Cursor readNombresPaises(){
        Cursor result = db.query(TABLE_NAME_PAISES, new String[]{COL_NOMBRE_PAIS}, null, null, null, null, null);

        if(result != null)
            result.moveToFirst();

        return result;
    }

    /** Devuelve como String el código del país donde
     * @param nombre_pais es el identificador del país para el que se quiere obtener el código.
     * */
    public static String readCodigoPais(String nombre_pais){
        Cursor result = db.query(TABLE_NAME_PAISES, new String[]{COL_CODIGO_PAIS}, COL_NOMBRE_PAIS + "='" + nombre_pais + "'", null, null, null, null);
        result.moveToFirst();

        return result.getString(0);
    }

    /**
     * Devuelve el código de la divisa del país pasado como parámetro
     * @param nombre_pais
     * @return
     */
    public static String readMonedaPais(String nombre_pais){
        Cursor result = db.query(TABLE_NAME_PAISES, new String[]{COL_MONEDA}, COL_NOMBRE_PAIS + "='" + nombre_pais + "'", null, null, null, null);
        result.moveToFirst();

        return result.getString(0);
    }

    public static boolean insertarHotel(HotelModel hotel){
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_ELEMENTO_NOMBRE_VIAJE, hotel.getNombreViaje());
        contentValues.put(COL_ELEMENTO_TIPO, Elemento.TIPO_HOTEL);
        contentValues.put(COL_ELEMENTO_NOMBRE, hotel.getNombre());
        contentValues.put(COL_ELEMENTO_DIRECCION, hotel.getDireccion());
        contentValues.put(COL_ELEMENTO_FECHA_1, hotel.getFecha1());
        contentValues.put(COL_ELEMENTO_FECHA_2, hotel.getFecha2());
        contentValues.put(COL_ELEMENTO_HORA_1, hotel.getHora1());
        contentValues.put(COL_ELEMENTO_HORA_2, hotel.getHora2());

        return insertarElemento(hotel, contentValues);
    }

    public static boolean insertarVuelo(VueloModel vuelo){
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_ELEMENTO_NOMBRE_VIAJE, vuelo.getNombreViaje());
        contentValues.put(COL_ELEMENTO_TIPO, Elemento.TIPO_VUELO);
        contentValues.put(COL_ELEMENTO_ID_VUELO, vuelo.getId_vuelo());
        contentValues.put(COL_ELEMENTO_NOMBRE, vuelo.getNombre());
        contentValues.put(COL_ELEMENTO_FECHA_1, vuelo.getFecha_vuelo());
        contentValues.put(COL_ELEMENTO_HORA_1, vuelo.getHora_vuelo());

        return insertarElemento(vuelo, contentValues);
    }

    public static boolean insertarMapa(MapaModel mapa){
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_ELEMENTO_NOMBRE_VIAJE, mapa.getNombreViaje());
        contentValues.put(COL_ELEMENTO_TIPO, Elemento.TIPO_MAPA);
        contentValues.put(COL_ELEMENTO_NOMBRE, mapa.getNombre());
        contentValues.put(COL_ELEMENTO_PATH, mapa.getPath());

        return insertarElemento(mapa, contentValues);
    }

    public static boolean insertarTarea(TareaModel tarea) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_ELEMENTO_NOMBRE_VIAJE, tarea.getNombreViaje());
        contentValues.put(COL_ELEMENTO_TIPO, Elemento.TIPO_TAREA);
        contentValues.put(COL_ELEMENTO_NOMBRE, tarea.getNombre());
        int completada = tarea.isCompletada()? 1 : 0; //La base de datos trabaja con int
        contentValues.put(COL_ELEMENTO_COMPLETADO, completada);

        return insertarElemento(tarea, contentValues);
    }

    public static boolean insertarTiempo(PrediccionModel tiempo){
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_ELEMENTO_NOMBRE_VIAJE, tiempo.getNombreViaje());
        contentValues.put(COL_ELEMENTO_TIPO, Elemento.TIPO_PREDICCION);
        contentValues.put(COL_ELEMENTO_NOMBRE, tiempo.getNombre());

        return insertarElemento(tiempo, contentValues);
    }

    /** Inserta el conjunto de contentValues en la tabla ELEMENTOS.
     * @param e es el elemento añadido, para añadirle la ID.
     * @param contentValues es el conjunto de columnas a insertar en la fila. */
    private static boolean insertarElemento(Elemento e, ContentValues contentValues){
        long id = db.insert(TABLE_NAME_ELEMENTOS, null, contentValues);
        if(id == -1){
            return false;
        }else{
            // De este modo la ID del elemento se actualiza nada más insertarlo, no es necesaria antes,
            // tampoco es necesario hacer nada más ya que es la misma instancia que el elemento que está en
            // el array del viaje.
            e.setId((int)id);
            return true;
        }
    }

    public static boolean updateTarea(TareaModel tarea){
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_ELEMENTO_NOMBRE, tarea.getNombre());
        int completada = tarea.isCompletada()? 1 : 0; //La base de datos trabaja con int
        contentValues.put(COL_ELEMENTO_COMPLETADO, completada);

        return db.update(TABLE_NAME_ELEMENTOS, contentValues, COL_ELEMENTO_ID + "=" + tarea.getId(), null) > 0;
    }

    /** Lee proceduralmente todos los elementos pertenecientes a un viaje y se los asigna
     * @param viaje es el viaje que contendrá dichos elementos, cuyo atributo nombre actúa
     *  como clave secundaria de la tabla de elementos.
     */
    public static void readElementosViaje(Viaje viaje){
        String viaje_nombre = viaje.getNombre_viaje();

        Cursor result = db.query(TABLE_NAME_ELEMENTOS, ALL_COLUMNS_ELEMENTO, COL_ELEMENTO_NOMBRE_VIAJE
                + "='"+viaje_nombre+"'", null, null, null, null, null);

        // Si el viaje tiene elementos, se le añaden progresivamente, diferenciando el tipo según la columna no nula TIPO_ELEMENTO
        if(result != null && result.getCount() > 0){
            result.moveToFirst();

            do {
                int tipo_elemento = result.getInt(2); //Está hardcodeado pero esto sólo lo necesitamos nosotros así que...
                // Según el tipo de elemento, se leerán diferentes columnas.
                switch(tipo_elemento){
                    case Elemento.TIPO_HOTEL:
                        HotelModel hotel = new HotelModel(result.getInt(0), result.getString(3),
                                result.getString(5), result.getString(6),
                                result.getString(7), result.getString(8), result.getString(9), result.getString(1));
                        viaje.addElemento(hotel);
                        break;
                    case Elemento.TIPO_MAPA:
                        MapaModel mapa = new MapaModel(result.getInt(0), result.getString(3), result.getString(10),
                                result.getString(1));
                        viaje.addElemento(mapa);
                        break;
                    case Elemento.TIPO_VUELO:
                        VueloModel vuelo = new VueloModel(result.getInt(0), result.getString(3), result.getString(11),
                                result.getString(6), result.getString(8), result.getString(1));
                        viaje.addElemento(vuelo);
                        break;
                    case Elemento.TIPO_TAREA:
                        TareaModel tarea = new TareaModel(result.getInt(0),result.getString(3), result.getString(1), result.getInt(12));
                        viaje.addElemento(tarea);
                        break;
                    case Elemento.TIPO_PREDICCION:
                        PrediccionModel tiempo = new PrediccionModel(result.getInt(0), result.getString(3), result.getString(1));
                        viaje.addElemento(tiempo);
                    default:
                        break;
                }
            }while(result.moveToNext());
        }
    }

    /** Elimina todos los elementos de un viaje. Esto se utilizará cuando se elimine un viaje. */
    public static boolean deleteElementosViaje(String nombre_viaje){
        return db.delete(TABLE_NAME_ELEMENTOS, COL_ELEMENTO_NOMBRE_VIAJE + " = '" + nombre_viaje + "'", null) > 0;
    }

    /** Elimina un elemento concreto de un viaje*/
    public static boolean deleteElementoViaje(int ID){
        return db.delete(TABLE_NAME_ELEMENTOS, COL_ELEMENTO_ID + " = " + ID, null) > 0;
    }


    /** Obtiene la id de la última fila añadida en la tabla. Útil
     * para obtener la ID del elemento recién añadido o para obtener
     * el último viaje que se creó.
     * @param TABLE_NAME es el nombre de la tabla en la que buscar, y puede ser TABLE_NAME_ELEMENTOS
     * o TABLE_NAME_VIAJES */
    public static int getLastID(String TABLE_NAME){
        Cursor c = db.rawQuery("select MAX(ID) from " + TABLE_NAME, null);
        c.moveToFirst();
        return c.getInt(0);
    }

    private static class ViajesDatabase extends SQLiteOpenHelper {
        private Context mContext;
        private SQLiteDatabase mDatabase;

        public ViajesDatabase(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            mContext = context;
        }

        public SQLiteDatabase openDataBase() throws IOException{

            String dbPath = mContext.getDatabasePath(DATABASE_NAME).getPath();
            if(mDatabase != null && mDatabase.isOpen()) {
                return null;
            }
            mDatabase = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READWRITE);
            return mDatabase;
        }

        public void closeDatabase(){
            if(mDatabase!=null)
                mDatabase.close();
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DELETE * FROM " + TABLE_NAME_VIAJES);
        }
    }
}
