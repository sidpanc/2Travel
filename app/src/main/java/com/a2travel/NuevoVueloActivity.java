package com.a2travel;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import com.a2travel.core.DialogCamposVacios;
import com.a2travel.models.VueloModel;

import java.util.Calendar;

public class NuevoVueloActivity extends AppCompatActivity {

    private EditText edit_id_vuelo;
    private EditText edit_aeropuerto_origen;
    private EditText edit_aeropuerto_destino;
    private DatePickerDialog dialogFecha;
    private EditText edit_fecha_vuelo;
    private TimePickerDialog dialogHora;
    private EditText edit_hora_vuelo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo_vuelo);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(getString(R.string.title_activity_vuelo));

        //Con estas dos lineas ponemos el botón de para retroceder
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        edit_id_vuelo = (EditText) findViewById(R.id.edit_id_vuelo);
        edit_aeropuerto_origen = (EditText) findViewById(R.id.edit_aeropuerto_origen);
        edit_aeropuerto_destino = (EditText) findViewById(R.id.edit_aeropuerto_destino);
        edit_fecha_vuelo = (EditText) findViewById(R.id.edit_fecha_vuelo);
        edit_hora_vuelo = (EditText) findViewById(R.id.edit_hora_vuelo);

        Calendar cal = Calendar.getInstance();
        int dia_actual = cal.get(Calendar.DAY_OF_MONTH);
        int mes_actual = cal.get(Calendar.MONTH);
        int anio_actual = cal.get(Calendar.YEAR);

        dialogFecha =  new DatePickerDialog(this, myDateListener, anio_actual, mes_actual, dia_actual);
        dialogHora = new TimePickerDialog(this, horaVueloListener, 12, 00, true);
    }

    public void onAddVueloClick(View v){

        if(validar_formulario()){
            String id_vuelo = edit_id_vuelo.getText().toString();
            String aeropuerto_origen = edit_aeropuerto_origen.getText().toString();
            String aeropuerto_destino = edit_aeropuerto_destino.getText().toString();
            String fecha_vuelo = edit_fecha_vuelo.getText().toString();
            String hora_vuelo = edit_hora_vuelo.getText().toString();

            VueloModel vuelo = new VueloModel(id_vuelo, "Origen: " + aeropuerto_origen +" / Destino: " + aeropuerto_destino, fecha_vuelo, hora_vuelo, MainActivity.viaje_seleccionado.getNombre_viaje());

            Intent i = new Intent();
            i.putExtra("vuelo", vuelo);

            setResult(Activity.RESULT_OK, i);
            finish();

        }else{
            DialogCamposVacios dialog = new DialogCamposVacios(this);
            dialog.show();
        }

    }

    /** Validamos que se hayan rellenado los campos*/
    private boolean validar_formulario(){
        if(edit_id_vuelo.getText().toString().equals("") || edit_fecha_vuelo.getText().toString().equals("")){
            return false;
        }
        return true;
    }

    public void showDatePickerVuelo(View view){
        dialogFecha.show();
    }

    private DatePickerDialog.OnDateSetListener myDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            edit_fecha_vuelo.setText(dayOfMonth + "/" + (month+1) + "/" + year);
        }

    };

    public void showTimePickerVuelo(View view){
        // Ocultamos el teclado para que la experiencia de uso sea más agradable:
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

        int id = view.getId();
        dialogHora.show();
    }

    private TimePickerDialog.OnTimeSetListener horaVueloListener = new TimePickerDialog.OnTimeSetListener(){
        @Override
        public void onTimeSet(TimePicker view, int hour, int minute){
            String min = minute == 0? "00" : Integer.toString(minute);
            String h = hour == 0? "00" : Integer.toString(hour);
            edit_hora_vuelo.setText(h + ":" + min);
        }
    };

}
