package com.a2travel;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.a2travel.core.DatabaseAdapter;
import com.a2travel.core.Viaje;
import com.a2travel.models.HotelModel;
import com.a2travel.models.MapaModel;
import com.a2travel.models.TareaModel;
import com.a2travel.models.PrediccionModel;
import com.a2travel.models.VueloModel;

public class NuevoElementoActivity extends AppCompatActivity {

    private static final int ADD_MAP_RESULT = 1;
    private static final int ADD_VUELO_RESULT = 2;
    private static final int ADD_HOTEL_RESULT = 3;
    private static final int ADD_TAREA_RESULT = 4;
    private static final int ADD_TIEMPO_RESULT = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo_elemento);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Para el botón de retroceder en el ActionBar:
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    public void onBtnVueloClick(View view){
        Intent intent = new Intent(this, NuevoVueloActivity.class);
        startActivityForResult(intent, ADD_VUELO_RESULT);
    }

    public void onBtnHotelClick(View view){
        Intent intent = new Intent(this, NuevoHotelActivity.class);
        startActivityForResult(intent, ADD_HOTEL_RESULT);
    }

    public void onBtnNuevaTareaClick(View view){
        Intent intent = new Intent(this, NuevaTareaActivity.class);
        startActivityForResult(intent, ADD_TAREA_RESULT);
    }

    public void onBtnMapaClick(View view){
        Intent intent = new Intent(this, NuevoMapaActivity.class);
        startActivityForResult(intent, ADD_MAP_RESULT);
    }

    public void onBtnNuevoTransporteClick(View view){
        Toast toast = Toast.makeText(this, "Función no disponible", Toast.LENGTH_SHORT);
        toast.show();
    }

    public void onBtnNuevaPrediccionClick(View view){
        Intent intent = new Intent(this, PrediccionActivity.class);
        startActivityForResult(intent, ADD_TIEMPO_RESULT);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Comprueba de qué activity viene el resultado.
        switch(requestCode){
            case ADD_MAP_RESULT:
                //Coger la imagen del mapa, guardarla en un intent, y llamar a MainActivity.
                if(resultCode == Activity.RESULT_OK){
                    //Vuelve a main activity añadiendo un mapa al viaje seleccionado.
                    MapaModel mapa = data.getParcelableExtra("mapa");
                    DatabaseAdapter.insertarMapa(mapa);
                    Intent intent = new Intent(this, MainActivity.class);
                    startActivity(intent);
                    Toast toast = Toast.makeText(this, "Mapa guardado", Toast.LENGTH_SHORT);
                    toast.show();
                }else{
                    return;
                }
                break;
            case ADD_VUELO_RESULT:
                if(resultCode == Activity.RESULT_OK){
                    VueloModel vuelo = data.getParcelableExtra("vuelo");
                    DatabaseAdapter.insertarVuelo(vuelo);
                    Intent intent = new Intent(this, MainActivity.class);
                    startActivity(intent);
                    Toast toast = Toast.makeText(this, "Vuelo guardado", Toast.LENGTH_SHORT);
                    toast.show();
                }else{
                    return;
                }
                break;
            case ADD_HOTEL_RESULT:
                if(resultCode == Activity.RESULT_OK){
                    HotelModel hotel = data.getParcelableExtra("hotel");
                    DatabaseAdapter.insertarHotel(hotel);
                    Intent intent = new Intent(this, MainActivity.class);
                    startActivity(intent);
                    Toast toast = Toast.makeText(this, "Hotel guardado", Toast.LENGTH_SHORT);
                    toast.show();
                }else{
                    return;
                }
                break;
            case ADD_TAREA_RESULT:
                if(resultCode == Activity.RESULT_OK){
                    TareaModel tarea = new TareaModel(data.getStringExtra("nombre_tarea"), MainActivity.viaje_seleccionado.getNombre_viaje(), 0);

                    DatabaseAdapter.insertarTarea(tarea);
                    Intent intent = new Intent(this, MainActivity.class);
                    startActivity(intent);
                    Toast toast = Toast.makeText(this, "Tarea guardada", Toast.LENGTH_SHORT);
                    toast.show();
                }else{
                    return;
                }
                break;
            case ADD_TIEMPO_RESULT:
                if(resultCode == Activity.RESULT_OK){
                    PrediccionModel tiempo = new PrediccionModel(MainActivity.viaje_seleccionado);

                    if(!Viaje.hasTiempo(MainActivity.viaje_seleccionado)){
                        DatabaseAdapter.insertarTiempo(tiempo);
                        Toast toast = Toast.makeText(this, "Tiempo guardado", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                    Intent intent = new Intent(this, MainActivity.class);
                    startActivity(intent);
                }else{
                    return;
                }

            default:

        }

    }
}
