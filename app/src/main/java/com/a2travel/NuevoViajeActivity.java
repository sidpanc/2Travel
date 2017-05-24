package com.a2travel;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.a2travel.core.CiudadAutoCompleteTextView;
import com.a2travel.core.DatabaseAdapter;
import com.a2travel.core.DialogCamposVacios;
import com.a2travel.core.PaisAutocompleteTextView;
import com.a2travel.core.Viaje;
import com.a2travel.models.TareaModel;

import java.util.Calendar;
import java.util.Locale;

public class NuevoViajeActivity extends AppCompatActivity {
    private EditText edit_nombre_viaje;

    private PaisAutocompleteTextView autocomplete_pais;
    private DatePickerDialog dialogSalida;
    private DatePickerDialog dialogRegreso;

    int day_salida, month_salida, year_salida, day_regreso, month_regreso, year_regreso;
    private EditText edit_fecha_salida;
    private EditText edit_fecha_regreso;

    private String arrayPaises[];

    //Para el autocompletado de la ciudad:
    CiudadAutoCompleteTextView autocomplete_ciudad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo_viaje);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Con estas dos lineas ponemos el botón de para retroceder
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        // Cargamos los países en el spinner:
        Cursor paises = DatabaseAdapter.readNombresPaises();
        arrayPaises= new String[paises.getCount()];
        int i = 0;
        while(!paises.isAfterLast()){
            arrayPaises[i] = paises.getString(0);
            i++;
            paises.moveToNext();
        }

        // Instanciamos los elementos del formulario:
        edit_nombre_viaje = (EditText) findViewById(R.id.edit_nombre_viaje);


        //Para el autocompletado de ciudad:
        autocomplete_ciudad = (CiudadAutoCompleteTextView) findViewById(R.id.autocomplete_ciudad);
        autocomplete_pais = (PaisAutocompleteTextView)findViewById(R.id.autocomplete_pais);

        autocomplete_ciudad.init(this, autocomplete_pais);

        //Para el autocompletado de país, necesita la instancia de CiudadAutoCompleteTextView
        // para activar su focus cuando se deba
        autocomplete_pais.init(arrayPaises, autocomplete_ciudad);


        //edit_ciudad_viaje = (EditText) findViewById(R.id.edit_ciudad_viaje);
        edit_fecha_salida = (EditText) findViewById(R.id.edit_nuevoViaje_fecha_salida);
        edit_fecha_regreso = (EditText) findViewById(R.id.edit_nuevoViaje_fecha_regreso);

        Calendar cal = Calendar.getInstance();
        int dia_actual = cal.get(Calendar.DAY_OF_MONTH);
        int mes_actual = cal.get(Calendar.MONTH);
        int anio_actual = cal.get(Calendar.YEAR);

        dialogSalida =  new DatePickerDialog(this, fechaSalidaListener, anio_actual, mes_actual, dia_actual);
        dialogRegreso = new DatePickerDialog(this, fechaRegresoListener, anio_actual, mes_actual, dia_actual);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(Activity.RESULT_CANCELED);
        finish();
    }

    public void onAddViajeClick(View v){
        // Guardamos la información introducida en el formulario y generamos el viaje:

        if(validar_formulario()){
            String nombre_viaje = edit_nombre_viaje.getText().toString();
            String nombre_pais = autocomplete_pais.getText().toString();
            String ciudad_viaje = autocomplete_ciudad.getText().toString();
            String fecha_salida = edit_fecha_salida.getText().toString();
            String fecha_regreso = edit_fecha_regreso.getText().toString();

            ciudad_viaje = ciudad_viaje.split(",")[0];

            /*Si el nombre del viaje ya está en uso, se añade un (1) al nombre en lugar de interrumpir
            el flujo de la aplicación.*/
            if(DatabaseAdapter.readViaje(nombre_viaje) != null){
                nombre_viaje = nombre_viaje + " (1)";
            }

            Viaje viaje = new Viaje(nombre_viaje, nombre_pais, ciudad_viaje, fecha_salida, fecha_regreso);

            Intent intent = new Intent();
            intent.putExtra("viaje", viaje);

            setResult(Activity.RESULT_OK, intent);
            finish();
        }
    }

    private boolean validar_formulario(){
        if(!validar_campos()){
            DialogCamposVacios dialog = new DialogCamposVacios(this);
            dialog.show();

            return false;

        }else if(!validar_fecha()){
            Toast avisoFecha = Toast.makeText(this, "La fecha de salida es posterior a la de regreso.", Toast.LENGTH_SHORT);
            avisoFecha.show();
            return false;
        }
        return true;
    }

    /** Validamos que se hayan rellenado los campos*/
    private boolean validar_campos(){
        if(edit_nombre_viaje.getText().toString().equals("")
                || autocomplete_pais.getText().toString().equals(getString(R.string.hint_selecciona_pais))
                || autocomplete_ciudad.getText().toString().equals("")
                || edit_fecha_salida.getText().toString().equals("")
                || edit_fecha_regreso.getText().toString().equals("")){
            return false;
        }
        return true;
    }

    /** Verifica que la fecha de regreso sea posterior a la de salida. */
    private boolean validar_fecha(){
        if(year_salida < year_regreso){
            return true;
        }else if(year_salida == year_regreso){
            if(month_salida < month_regreso){
                return true;
            }else if(month_salida == month_regreso){
                if(day_salida <= day_regreso){
                    return true;
                }
            }
        }
        return false;
    }

    /** Muestra el diálogo de selección de fecha */
    public void showDatePickerViaje(View view){
        // Ocultamos el teclado para que la experiencia de uso sea más agradable:
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

        int id = view.getId();
        if(id == R.id.edit_nuevoViaje_fecha_salida) {
            dialogSalida.show();
        }else if(id == R.id.edit_nuevoViaje_fecha_regreso){
            dialogRegreso.show();
        }
    }

    /** Muestra el datepicker para la fecha de salida, guarda las fechas en variables para poder
     * comparar después que la fecha de regreso sea posterior */
    private DatePickerDialog.OnDateSetListener fechaSalidaListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            day_salida = dayOfMonth;
            month_salida = month;
            year_salida = year;
            edit_fecha_salida.setText(dayOfMonth + "/" + (month+1) + "/" + year);
        }
    };

    /** Muestra el datepicker para la fecha de regreso, guarda las fechas en variables para poder
     * comparar después que la fecha de regreso sea posterior */
    private DatePickerDialog.OnDateSetListener fechaRegresoListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            day_regreso = dayOfMonth;
            month_regreso = month;
            year_regreso = year;
            edit_fecha_regreso.setText(dayOfMonth + "/" + (month+1) + "/" + year);
        }
    };


}
