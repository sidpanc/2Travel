package com.a2travel;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;

import com.a2travel.models.MapaModel;

import java.io.File;

public class VistaMapaActivity extends AppCompatActivity {
    private MapaModel m;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vista_mapa);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Intent i = getIntent();

        //Para el botón de retroceder en el ActionBar:
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        m = (MapaModel)i.getParcelableExtra("mapa");

        setTitle(m.getNombre());

        ImageView imageView = (ImageView)findViewById(R.id.img_vista_mapa);

        File img = new File(m.getPath());
        if(img.exists()){
            Bitmap bitmap = BitmapFactory.decodeFile(img.getAbsolutePath());
            imageView.setImageBitmap(bitmap);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            onBackPressed();

        }
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
