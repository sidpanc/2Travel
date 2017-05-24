package com.a2travel;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.a2travel.core.JSONParser;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class PrediccionActivity extends AppCompatActivity {

    Typeface fuenteIconos;

    //Prediccion1:
    TextView tiempo_ciudad;
    TextView tiempo_dia1;
    TextView tiempo_temp_actual1;
    TextView tiempo_humedad1;

    //Prediccion2:
    TextView tiempo_dia2;
    TextView tiempo_temp_actual2;
    TextView tiempo_humedad2;

    //Prediccion3:
    TextView tiempo_dia3;
    TextView tiempo_temp_actual3;
    TextView tiempo_humedad3;

    //Prediccion4:
    TextView tiempo_dia4;
    TextView tiempo_temp_actual4;
    TextView tiempo_humedad4;

    //Prediccion5:
    TextView tiempo_dia5;
    TextView tiempo_temp_actual5;
    TextView tiempo_humedad5;

    //Prediccion6:
    TextView tiempo_dia6;
    TextView tiempo_temp_actual6;
    TextView tiempo_humedad6;

    ArrayList<TextView> tiempo_iconos;

    Handler handler;

    public PrediccionActivity(){
        handler = new Handler();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tiempo);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setTitle(getString(R.string.title_activity_tiempo) + " " + MainActivity.viaje_seleccionado.getCiudad());

        fuenteIconos = Typeface.createFromAsset(this.getAssets(), "fonts/weather.ttf");

        //Para el botón de retroceder en el ActionBar:
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        tiempo_iconos = new ArrayList<TextView>(6);

        //Prediccion1:
        tiempo_ciudad = (TextView)findViewById(R.id.tiempo_ciudad);
        tiempo_dia1 = (TextView)findViewById(R.id.tiempo_dia1);
        tiempo_temp_actual1 = (TextView)findViewById(R.id.tiempo_temp_actual1);
        tiempo_humedad1 = (TextView)findViewById(R.id.tiempo_humedad1);
        tiempo_iconos.add((TextView)findViewById(R.id.tiempo_icono1));

        //Prediccion2:
        tiempo_dia2 = (TextView)findViewById(R.id.tiempo_dia2);
        tiempo_temp_actual2 = (TextView)findViewById(R.id.tiempo_temp_actual2);
        tiempo_humedad2 = (TextView)findViewById(R.id.tiempo_humedad2);
        tiempo_iconos.add((TextView)findViewById(R.id.tiempo_icono2));

        //Prediccion3:
        tiempo_dia3 = (TextView)findViewById(R.id.tiempo_dia3);
        tiempo_temp_actual3 = (TextView)findViewById(R.id.tiempo_temp_actual3);
        tiempo_humedad3 = (TextView)findViewById(R.id.tiempo_humedad3);
        tiempo_iconos.add((TextView)findViewById(R.id.tiempo_icono3));

        //Prediccion4:
        tiempo_dia4 = (TextView)findViewById(R.id.tiempo_dia4);
        tiempo_temp_actual4 = (TextView)findViewById(R.id.tiempo_temp_actual4);
        tiempo_humedad4 = (TextView)findViewById(R.id.tiempo_humedad4);
        tiempo_iconos.add((TextView)findViewById(R.id.tiempo_icono4));

        //Prediccion5:
        tiempo_dia5 = (TextView)findViewById(R.id.tiempo_dia5);
        tiempo_temp_actual5 = (TextView)findViewById(R.id.tiempo_temp_actual5);
        tiempo_humedad5 = (TextView)findViewById(R.id.tiempo_humedad5);
        tiempo_iconos.add((TextView)findViewById(R.id.tiempo_icono5));

        //Predicción6:
        tiempo_dia6 = (TextView)findViewById(R.id.tiempo_dia6);
        tiempo_temp_actual6 = (TextView)findViewById(R.id.tiempo_temp_actual6);
        tiempo_humedad6 = (TextView)findViewById(R.id.tiempo_humedad6);
        tiempo_iconos.add((TextView)findViewById(R.id.tiempo_icono6));

        for(int i = 0; i<tiempo_iconos.size(); i++){
            tiempo_iconos.get(i).setTypeface(fuenteIconos);
        }

        updateWeatherData(MainActivity.viaje_seleccionado.getCiudad());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(Activity.RESULT_CANCELED);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_guardar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.menu_guardar){
            setResult(Activity.RESULT_OK);
            finish();
        }else if(id == android.R.id.home){
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    private void renderWeather(JSONObject json){
        try {
            tiempo_ciudad.setText(json.getJSONObject("city").getString("name").toUpperCase(Locale.getDefault()));

            JSONObject objects[] = new JSONObject[6];
            TextView fechas[] = {tiempo_dia1,tiempo_dia2,tiempo_dia3,tiempo_dia4,tiempo_dia5,tiempo_dia6};
            TextView temperaturas[] = {tiempo_temp_actual1,tiempo_temp_actual2,tiempo_temp_actual3,tiempo_temp_actual4,tiempo_temp_actual5,tiempo_temp_actual6};
            TextView humedades[] = {tiempo_humedad1,tiempo_humedad2,tiempo_humedad3,tiempo_humedad4,tiempo_humedad5,tiempo_humedad6};
            for(int i=0; i<objects.length; i++){
                //JSONObject con la prediccion para cada dia.
                objects[i] = json.getJSONArray("list").getJSONObject(i);
                //Para cambiar el formato de la fecha:
                long unixSeconds = Long.parseLong(objects[i].getString("dt"));
                Date date = new Date(unixSeconds*1000L); // *1000 para convertir de segundos a milisegundos
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                String formattedDate = sdf.format(date);
                fechas[i].setText(formattedDate);
                //Para poner la temperatura máxima y mínima:
                temperaturas[i].setText("Min: " + objects[i].getJSONObject("temp").getInt("min") + "ºC\nMax: " + objects[i].getJSONObject("temp").getInt("max") + "ºC");
                //Para cargar el icono correspondiente:
                setWeatherIcon(objects[i].getJSONArray("weather").getJSONObject(0).getInt("id"),i);
                //Para cargar las humedades:
                humedades[i].setText(getString(R.string.txt_humedad) + objects[i].getString("humidity") + "%");
            }

        }catch(Exception e){
            Log.e("SimpleWeather", "One or more fields not found in the JSON data");
        }
    }

    private void updateWeatherData(final String city){
        new Thread(){
            public void run(){
                final JSONObject json = JSONParser.getJSON(getBaseContext(), city);
                if(json == null){
                    handler.post(new Runnable(){
                        public void run(){
                        }
                    });
                } else {
                    handler.post(new Runnable(){
                        public void run(){
                            renderWeather(json);
                        }
                    });
                }
            }
        }.start();
    }

    private void setWeatherIcon(int actualId, int predAct){
        int id = actualId / 100;
        String icon = "";
        if(actualId == 800){
            icon = this.getString(R.string.weather_sunny);
        } else {
            switch(id) {
                case 2 : icon = this.getString(R.string.weather_thunder);
                    break;
                case 3 : icon = this.getString(R.string.weather_drizzle);
                    break;
                case 7 : icon = this.getString(R.string.weather_foggy);
                    break;
                case 8 : icon = this.getString(R.string.weather_cloudy);
                    break;
                case 6 : icon = this.getString(R.string.weather_snowy);
                    break;
                case 5 : icon = this.getString(R.string.weather_rainy);
                    break;
            }
        }
        tiempo_iconos.get(predAct).setText(icon);
    }

}
