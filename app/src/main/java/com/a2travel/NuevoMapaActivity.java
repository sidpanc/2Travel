package com.a2travel;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.a2travel.models.MapaModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class NuevoMapaActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private String filePath;
    private boolean mapLoaded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo_mapa);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Para el botón de retroceder en el ActionBar:
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        //Añade el fragment de maps
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_fragment);
        mapFragment.getMapAsync(this);

        mapLoaded = false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_guardar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home){
            onBackPressed();
        }else if(id == R.id.menu_guardar && mapLoaded == true){ // No se intenta guardar el mapa hasta que haya cargado.
            // Muestra el AlertDialog con el edittext para añadir un nombre al mapa.
            dialogGuardarMapa();
        }
        return super.onOptionsItemSelected(item);
    }

    private void dialogGuardarMapa(){
        AlertDialog.Builder nombreMapa = new AlertDialog.Builder(this);
        nombreMapa.setTitle("Nombre del mapa");
        // El EditText en el que se guardará el nombre del mapa.
        final EditText edit_nombre = new EditText(this);
        edit_nombre.setInputType(InputType.TYPE_CLASS_TEXT);
        // Añade el edittext al dialog.
        nombreMapa.setView(edit_nombre);
        nombreMapa.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mMap.snapshot(new GoogleMap.SnapshotReadyCallback() {
                    @Override
                    public void onSnapshotReady(Bitmap bitmap) {
                        try{
                            filePath = Environment.getExternalStorageDirectory().getAbsolutePath().toString() + "/" + System.currentTimeMillis() + ".jpeg";
                            //File file = new File(filePath);
                            FileOutputStream fileOutputStream = new FileOutputStream(filePath);
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fileOutputStream);

                            Intent i = new Intent();
                            MapaModel mapa = new MapaModel(edit_nombre.getText().toString(), filePath, MainActivity.viaje_seleccionado.getNombre_viaje());
                            i.putExtra("mapa", mapa);
                            setResult(Activity.RESULT_OK, i);
                            finish();
                        }catch(IOException e){
                            e.printStackTrace();
                        }
                    }
                });
            }
        });

        nombreMapa.show();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback(){
            @Override
            public void onMapLoaded() {
                mapLoaded = true;
            }
        });

        if(Geocoder.isPresent()){
            try {
                String location = MainActivity.viaje_seleccionado.getCiudad();
                Geocoder gc = new Geocoder(this);
                List<Address> address = gc.getFromLocationName(location,1);

                LatLng city = new LatLng(address.get(0).getLatitude(),address.get(0).getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(address.get(0).getLatitude(),address.get(0).getLongitude()),12.0f));

            } catch (IOException e) {

            }
        }
    }



}
