package com.a2travel;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.a2travel.core.DialogCamposVacios;
import com.a2travel.models.HotelModel;

import java.sql.Time;
import java.util.Calendar;

public class NuevoHotelActivity extends AppCompatActivity {

    private TextView txt_nombre_hotel;
    private TextView txt_direccion_hotel;
    private EditText edit_fecha_cin_hotel;
    private EditText edit_fecha_cout_hotel;
    private EditText edit_hora_cin_hotel;
    private EditText edit_hora_cout_hotel;

    private DatePickerDialog dialog_fecha_cin;
    private DatePickerDialog dialog_fecha_cout;
    private TimePickerDialog dialog_hora_cin;
    private TimePickerDialog dialog_hora_cout;

    int day_salida, month_salida, year_salida, day_regreso, month_regreso, year_regreso, hora_salida, minuto_salida, hora_regreso, minuto_regreso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo_hotel);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Para el botón de retroceder en el ActionBar:
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        txt_nombre_hotel = (TextView) findViewById(R.id.edit_hotel_nombre);
        txt_direccion_hotel = (TextView) findViewById(R.id.edit_hotel_direccion);
        edit_fecha_cin_hotel = (EditText) findViewById(R.id.edit_hotel_fecha_cin);
        edit_fecha_cout_hotel = (EditText) findViewById(R.id.edit_hotel_fecha_cout);
        edit_hora_cin_hotel = (EditText) findViewById(R.id.edit_hotel_hora_cin);
        edit_hora_cout_hotel = (EditText) findViewById(R.id.edit_hotel_hora_cout);

        Calendar cal = Calendar.getInstance();
        int dia_actual = cal.get(Calendar.DAY_OF_MONTH);
        int mes_actual = cal.get(Calendar.MONTH);
        int anio_actual = cal.get(Calendar.YEAR);

        dialog_fecha_cin =  new DatePickerDialog(this, fechaCinListener, anio_actual, mes_actual, dia_actual);
        dialog_fecha_cout = new DatePickerDialog(this, fechaCoutListener, anio_actual, mes_actual, dia_actual);
        dialog_hora_cin = new TimePickerDialog(this, horaCinListener, 12, 00, true);
        dialog_hora_cout = new TimePickerDialog(this, horaCoutListener, 12, 00, true);

    }

    public void onAddHotelClick(View v){
        if(validar_formulario()){
            String nombre_hotel = txt_nombre_hotel.getText().toString();
            String direccion_hotel = txt_direccion_hotel.getText().toString();
            String fecha_cin_hotel = edit_fecha_cin_hotel.getText().toString();
            String fecha_cout_hotel = edit_fecha_cout_hotel.getText().toString();
            String hora_cin_hotel = edit_hora_cin_hotel.getText().toString();
            String hora_cout_hotel = edit_hora_cout_hotel.getText().toString();

            HotelModel hotel = new HotelModel(nombre_hotel,direccion_hotel,fecha_cin_hotel,fecha_cout_hotel,hora_cin_hotel,hora_cout_hotel, MainActivity.viaje_seleccionado.getNombre_viaje());

            Intent i = new Intent();
            i.putExtra("hotel", hotel);

            setResult(Activity.RESULT_OK, i);
            finish();

        }
    }

    /** Validamos que se hayan rellenado los campos*/
    private boolean validar_formulario(){
        if(!validar_campos()){
            DialogCamposVacios dialog = new DialogCamposVacios(this);
            dialog.show();

            return false;

        }else if(!validar_fecha()){
            Toast avisoFecha = Toast.makeText(this, "La fecha de check-in es posterior a la de check-out.", Toast.LENGTH_SHORT);
            avisoFecha.show();
            return false;
        }
        return true;
    }

    /** Validamos que se hayan rellenado los campos*/
    private boolean validar_campos(){
        if(txt_nombre_hotel.getText().toString().equals("")
                || txt_direccion_hotel.getText().toString().equals("")
                || edit_fecha_cin_hotel.getText().toString().equals("")
                || edit_fecha_cout_hotel.getText().toString().equals("")){
            return false;
        }
        return true;
    }

    /** Verifica que la fecha de regreso sea posterior a la de salida. */
    private boolean validar_fecha(){
        if(year_salida > year_regreso){
            return true;
        }else if(year_salida == year_regreso){
            if(month_salida > month_regreso){
                return true;
            }else if(month_salida == month_regreso){
                if(day_salida >= day_regreso){
                    return true;
                }
            }
        }

        return false;
    }

    /** Muestra el diálogo de selección de fecha */
    public void showDatePickerHotel(View view){
        // Ocultamos el teclado para que la experiencia de uso sea más agradable:
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

        int id = view.getId();
        if(id == R.id.edit_hotel_fecha_cout) {
            dialog_fecha_cout.show();
        }else if(id == R.id.edit_hotel_fecha_cin){
            dialog_fecha_cin.show();
        }
    }

    public void showTimePickerHotel(View view){
        // Ocultamos el teclado para que la experiencia de uso sea más agradable:
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

        int id = view.getId();
        if(id == R.id.edit_hotel_hora_cout) {
            dialog_hora_cout.show();
        }else if(id == R.id.edit_hotel_hora_cin){
            dialog_hora_cin.show();
        }
    }

    private DatePickerDialog.OnDateSetListener fechaCoutListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            day_salida = dayOfMonth;
            month_salida = month;
            year_salida = year;
            edit_fecha_cout_hotel.setText(dayOfMonth + "/" + (month+1) + "/" + year);
        }
    };

    private DatePickerDialog.OnDateSetListener fechaCinListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            day_regreso = dayOfMonth;
            month_regreso = month;
            year_regreso = year;
            edit_fecha_cin_hotel.setText(dayOfMonth + "/" + (month+1) + "/" + year);
        }
    };

    private TimePickerDialog.OnTimeSetListener horaCinListener = new TimePickerDialog.OnTimeSetListener(){
        @Override
        public void onTimeSet(TimePicker view, int hour, int minute){
            String min = minute == 0? "00" : Integer.toString(minute);
            String h = hour == 0? "00" : Integer.toString(hour);
            edit_hora_cin_hotel.setText(h + ":" + min);
        }
    };

    private TimePickerDialog.OnTimeSetListener horaCoutListener = new TimePickerDialog.OnTimeSetListener(){
        @Override
        public void onTimeSet(TimePicker view, int hour, int minute){
            String min = minute == 0? "00" : Integer.toString(minute);
            String h = hour == 0? "00" : Integer.toString(hour);
            edit_hora_cout_hotel.setText(h + ":" + min);
        }
    };

}
