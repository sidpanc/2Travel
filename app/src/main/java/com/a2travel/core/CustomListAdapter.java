package com.a2travel.core;


import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.a2travel.R;
import com.a2travel.models.Elemento;
import com.a2travel.models.ElementoAdapter;
import com.a2travel.models.HotelModel;
import com.a2travel.models.MapaModel;
import com.a2travel.models.PrediccionModel;
import com.a2travel.models.TareaModel;
import com.a2travel.models.VueloModel;

import java.util.ArrayList;


public class CustomListAdapter extends ArrayAdapter {

    private ArrayList<Elemento> dataSet;
    Context mContext;

    /**
     * @param data el array con los items
     * @param context el context de la activity en la que está.
     */
    public CustomListAdapter(ArrayList<Elemento> data, Context context){
        super(context, R.layout.item_hotel, data);
        dataSet = data;
        mContext = context;
    }

    public boolean deleteItem(Elemento e){
        // Se elimina el elemento del array y se notifica al ListView que tiene que actualizar
        // La información.
        boolean deleted = dataSet.remove(e);
        if(deleted){
            notifyDataSetChanged();
        }
        return deleted;
    }

    @Override
    public int getCount() {
        return dataSet.size();
    }

    @Override
    public Object getItem(int position) {
        return dataSet.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        //hotel, vuelo, mapa, tarea, tiempo. Pero debe ser N+1, ya que esto entiende que empieza en 0
        // y el último tipo (6) daría ArrayIndexOutOfBounds (una semana hasta saber por qué crasheaba)
        return 7;
    }

    @Override
    public int getItemViewType(int position) {
        // Devuelve el tipo de elemento que hay en determinada posición
        Elemento item = (Elemento)getItem(position);
        return ElementoAdapter.getTipo(item);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        int type = getItemViewType(position);

        //Esto se hace una vez por tipo, después, el convertView se "recicla" entre items iguales
        if(convertView == null){
            holder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(mContext);
            // Selecciona el layout correspondiente al item y setea los campos en un holder con todos los campos.
            switch(type){
                case Elemento.TIPO_VUELO:
                    convertView = inflater.inflate(R.layout.item_vuelo, null);
                    holder.txt_nombre = (TextView)convertView.findViewById(R.id.txt_list_vuelo_id);
                    holder.txt_descripcion = (TextView)convertView.findViewById(R.id.txt_list_vuelo_descripcion);
                    break;
                case Elemento.TIPO_HOTEL:
                    convertView = inflater.inflate(R.layout.item_hotel, null);
                    holder.txt_nombre = (TextView)convertView.findViewById(R.id.txt_list_hotel_nombre);
                    holder.txt_direccion = (TextView)convertView.findViewById(R.id.txt_list_hotel_direccion);
                    holder.txt_fecha1 = (TextView)convertView.findViewById(R.id.txt_list_hotel_fechain);
                    holder.txt_fecha2 = (TextView)convertView.findViewById(R.id.txt_list_hotel_fechaout);
                    break;
                case Elemento.TIPO_MAPA:
                    convertView = inflater.inflate(R.layout.item_mapa, null);
                    holder.txt_nombre = (TextView)convertView.findViewById(R.id.txt_list_mapa_nombre);
                    break;
                case Elemento.TIPO_TAREA:
                case Elemento.TIPO_TAREA_COMPLETADA:
                    convertView = inflater.inflate(R.layout.item_tarea, null);
                    holder.txt_nombre = (TextView)convertView.findViewById(R.id.txt_list_tarea_nombre);
                    break;
                case Elemento.TIPO_PREDICCION:
                    convertView = inflater.inflate(R.layout.item_tiempo, null);
                    holder.txt_nombre = (TextView)convertView.findViewById(R.id.txt_list_tiempo_nombre);
                    break;
                default:
                    break;

            }

            //Guarda el tag para poder reciclar la convertView en caso de que se añada otro listItem del mismo tipo
            convertView.setTag(holder);
        }else{
            //Si ya había un convertView (ya se ha añadido un item de ese tipo), lee el tag.
            holder = (ViewHolder)convertView.getTag();
        }

        Object item = getItem(position);
        if(item != null){
            // Completa los campos del holder asociados a los del layout del item.
            switch(type) {
                case Elemento.TIPO_VUELO:
                    VueloModel vuelo = (VueloModel) item;
                    holder.txt_descripcion.setText(vuelo.getId_vuelo().toString() + " (ID: " + vuelo.getNombre() + ")");
                    holder.txt_nombre.setText(vuelo.getFecha_vuelo().toString() + " (" + vuelo.getHora_vuelo() + ")");
                    break;
                case Elemento.TIPO_HOTEL:
                    HotelModel hotel = (HotelModel) item;
                    holder.txt_nombre.setText(hotel.getNombre().toString());
                    holder.txt_direccion.setText(hotel.getDireccion().toString());
                    holder.txt_fecha1.setText(hotel.getFecha1().toString() + " (" + hotel.getHora1().toString() + ")");
                    holder.txt_fecha2.setText(hotel.getFecha2().toString() + " (" + hotel.getHora2().toString() + ")");
                    break;
                case Elemento.TIPO_MAPA:
                    MapaModel mapa = (MapaModel) item;
                    holder.txt_nombre.setText(mapa.getNombre());
                    break;
                case Elemento.TIPO_TAREA_COMPLETADA:
                    // Si la tarea está completada, pone el fondo de distinto color.
                    // Es necesario convertir la cadena de color a int.
                    String bg_color = mContext.getResources().getString(0+R.color.colorAccent);
                    int mahColorInt = Color.parseColor(bg_color);
                    convertView.setBackgroundColor(mahColorInt);
                case Elemento.TIPO_TAREA:
                    TareaModel tarea = (TareaModel) item;
                    holder.txt_nombre.setText(tarea.getNombre());
                    break;
                case Elemento.TIPO_PREDICCION:
                    PrediccionModel tiempo = (PrediccionModel) item;
                    holder.txt_nombre.setText(tiempo.getNombre());
                    break;
                default:
                    break;
            }

        }
        return convertView;
    }

    public class ViewHolder{
        TextView txt_nombre, txt_direccion, txt_descripcion, txt_fecha1, txt_fecha2;
    }

}