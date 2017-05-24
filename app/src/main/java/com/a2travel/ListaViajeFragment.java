package com.a2travel;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.a2travel.core.CustomListAdapter;
import com.a2travel.core.DatabaseAdapter;
import com.a2travel.core.Viaje;
import com.a2travel.models.Elemento;
import com.a2travel.models.ElementoAdapter;
import com.a2travel.models.MapaModel;
import com.a2travel.models.TareaModel;


public class ListaViajeFragment extends Fragment {
    private MainActivity mActivity;
    private Viaje viaje;

    private CustomListAdapter listAdapter;
    private ListView listView;

    AdapterView.AdapterContextMenuInfo adapterContext;
    private static String ACTION_ELIMINAR = "Eliminar";
    private static String ACTION_COMPLETAR = "Completar";
    public ListaViajeFragment(){
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (MainActivity)getActivity();
        // Instancia del viaje seleccionado:
        viaje = mActivity.viaje_seleccionado;

        // Obtiene la preferencia para saber si hay que mostrar el tutorial para añadir elementos.
        SharedPreferences sharedPref = mActivity.getPreferences(Context.MODE_PRIVATE);
        boolean primeraVez = sharedPref.getBoolean(getString(R.string.preference_primera_vez), true);

        if(primeraVez){
            AlertDialog.Builder dialog = new AlertDialog.Builder(mActivity);
            dialog.setTitle(R.string.dialog_title_tutorial);
            dialog.setMessage(R.string.dialog_content_tutorial);
            dialog.setPositiveButton(R.string.dialog_aceptar, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    SharedPreferences sharedPref = mActivity.getPreferences(Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putBoolean(getString(R.string.preference_primera_vez), false);
                    editor.apply();
                    dialog.cancel();
                }
            });
            dialog.show();
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if(v.getId() == R.id.lista_elementos){
            // AdapterContext se utiliza para guardar la información del item que se pulsó, así después
            // podremos saber qué índice tiene el item del listView desde el que se abrió el menú contextual.
            adapterContext = (AdapterView.AdapterContextMenuInfo) menuInfo;
            // Añade la opción de eliminar al menú contextual del listView:
            Elemento e = (Elemento)listAdapter.getItem(adapterContext.position);

            if(ElementoAdapter.getTipo(e) == Elemento.TIPO_TAREA){
                menu.add(ACTION_COMPLETAR);
            }

            menu.add(ACTION_ELIMINAR);

        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if(item.getTitle().equals(ACTION_ELIMINAR)){
            int position = adapterContext.position;
            // Obtenemos del CustomListAdapter el Elemento que se ha presionado:
            Elemento e = (Elemento)listAdapter.getItem(position);
            // Lo eliminamos de la base de datos:
            if(DatabaseAdapter.deleteElementoViaje(e.getId())) {
                // Lo eliminamos del adapter, finalmente llamando a notifyHasChanged para que se actualice la listView.
                // Esto indirectamente lo elimina también del array contenido en Viaje, ya que son la misma instancia.
                listAdapter.deleteItem(e);
            }else{
                Toast t = Toast.makeText(mActivity.getApplicationContext(), "No se pudo borrar el elemento", Toast.LENGTH_SHORT);
                t.show();
            }
        }else if(item.getTitle().equals(ACTION_COMPLETAR)){
            int position = adapterContext.position;
            TareaModel t = (TareaModel)listAdapter.getItem(position);
            t.setCompletada(true);
            DatabaseAdapter.updateTarea(t);
            listAdapter.deleteItem(t);
            listAdapter.insert(t, position);
            listAdapter.notifyDataSetChanged();
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
                int id = item.getItemId();
        if(id == R.id.btn_add_elemento){
            Intent i = new Intent(mActivity, NuevoElementoActivity.class);
            startActivity(i);
        }else if(id == R.id.btn_eliminar_viaje){
            AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
            dialog.setTitle(R.string.dialog_title_atencion);
            dialog.setMessage(R.string.dialog_content_eliminar_viaje);
            dialog.setPositiveButton(R.string.dialog_continuar, new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    DatabaseAdapter.deleteViaje(viaje);
                    mActivity.viaje_seleccionado = null;

                    // Volvemos a llamar a init_fragments para que actualice el fragment con un nuevo viaje, o cambie de fragment.
                    mActivity.init_fragments();
                }
            });
            dialog.setNegativeButton(R.string.dialog_cancelar, new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            dialog.show();
        }else if(id == R.id.btn_modificar_viaje){
            Toast toast = Toast.makeText(getContext(), "Función no disponible", Toast.LENGTH_SHORT);
            toast.show();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_lista_viaje, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView txt_destino = (TextView) getView().findViewById(R.id.txt_lv_destino);
        TextView txt_fecha_salida = (TextView) getView().findViewById(R.id.txt_lv_fechaSalida);
        TextView txt_fecha_regreso = (TextView) getView().findViewById(R.id.txt_lv_fechaRegreso);

        mActivity.setTitle(viaje.getNombre_viaje());
        txt_destino.setText(viaje.getPais() + " - " + viaje.getCiudad());
        txt_fecha_salida.setText(viaje.getFecha_salida());
        txt_fecha_regreso.setText(viaje.getFecha_regreso());

        // Guardamos la referencia a la ListView del layout:
        listView = (ListView)mActivity.findViewById(R.id.lista_elementos);

        // Añadimos los elementos del viaje (si tiene) al ListView
        if(viaje.getElementosCount() > 0) {
            listAdapter = new CustomListAdapter(viaje.getElementos(), getContext());
            listView.setAdapter(listAdapter);
        }

        // Hacemos que la listView tenga menú contextual
        registerForContextMenu(listView);

        // Estos dos tipos de item abren una activity al pulsarlos
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Elemento e = (Elemento)listAdapter.getItem(position);
                switch(ElementoAdapter.getTipo(e)){
                    case Elemento.TIPO_MAPA:
                        Intent i = new Intent(mActivity, VistaMapaActivity.class);
                        MapaModel m = (MapaModel)e;
                        i.putExtra("mapa", m);
                        startActivity(i);
                        break;
                    case Elemento.TIPO_PREDICCION:
                        i = new Intent(mActivity, PrediccionActivity.class);
                        startActivity(i);
                        break;
                    default:
                        break;
                }
            }
        });
    }

}
