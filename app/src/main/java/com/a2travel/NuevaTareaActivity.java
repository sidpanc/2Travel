package com.a2travel;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.a2travel.core.DialogCamposVacios;
import com.a2travel.models.MapaModel;

public class NuevaTareaActivity extends AppCompatActivity {
    EditText edit_nombre_tarea;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nueva_tarea);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
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
        }else if(id == R.id.menu_guardar){
            //Si pulsa el botón de guardar del action bar, termina la actividad con result OK.
            edit_nombre_tarea = (EditText)findViewById(R.id.edit_nombre_tarea);

            if(!edit_nombre_tarea.getText().toString().equals("")){
                Intent i = new Intent();
                i.putExtra("nombre_tarea", edit_nombre_tarea.getText().toString());
                setResult(Activity.RESULT_OK, i);
                finish();
            }else{
                DialogCamposVacios dialog = new DialogCamposVacios(this);
                dialog.show();
            }

        }
        return super.onOptionsItemSelected(item);
    }

}
