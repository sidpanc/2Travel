package com.a2travel;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.a2travel.core.DatabaseAdapter;
import com.a2travel.core.Viaje;
import com.a2travel.models.TareaModel;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    static final int ADD_VIAJE_RESULT = 0;

    private static int postResult; // Para el código que no funciona si no está en onPostResume()
    private Menu menu; //La referencia del menú lateral para añadir los items
    public static Viaje viaje_seleccionado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        //Guardamos navigationView.getMenu() en el menu declarado y lo inicializamos
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        menu = navigationView.getMenu();

        try {
            DatabaseAdapter.open(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Inicializa los fragments después de cargar la base de datos.
        init_fragments();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.lista_viaje_menu, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START))
            drawer.closeDrawer(GravityCompat.START);
        else
            super.onBackPressed();
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        String itemName = (String)item.getTitle();
        if (id == R.id.action_nuevo_viaje) {
            startNuevoViajeActivity();
        }else if(id == R.id.action_sobre_desarrolladores){
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle(R.string.dialog_title_desarrolladores);
            dialog.setMessage(R.string.dialog_content_desarrolladores);
            dialog.setPositiveButton(R.string.dialog_aceptar, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            dialog.show();

        } else{
            viaje_seleccionado = DatabaseAdapter.readViaje(itemName);
            DatabaseAdapter.readElementosViaje(viaje_seleccionado);
            startListaViajeFragment();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /** Inicializa la vista principal, según haya que abrir un viaje o el menú de añadir viaje */
    public void init_fragments(){
        Cursor cursor_viajes = DatabaseAdapter.readNombresViajes();
        //Si la base de datos está vacía, mostramos el fragment inicial, si no, mostramos el último viaje leído de la bbdd.
        if(cursor_viajes.getCount() == 0){
            setTitle(R.string.app_name);
            menu.removeGroup(R.id.menu_viajes);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            Fragment fragment = new PrimerViajeFragment();
            ft.replace(R.id.fragment_viajes, fragment).commit();
        }else{
            // Si no hay ningún viaje seleccionado, setea el último añadido en la base de datos:
            if(viaje_seleccionado == null){
                viaje_seleccionado = DatabaseAdapter.readViaje(DatabaseAdapter.getLastID(DatabaseAdapter.TABLE_NAME_VIAJES));
            }

            viaje_seleccionado.resetElementos();
            DatabaseAdapter.readElementosViaje(viaje_seleccionado);

            startListaViajeFragment();
            init_navigation_drawer_menu(cursor_viajes);
        }
    }

    public void init_navigation_drawer_menu(Cursor cursor_viajes){
        menu.removeGroup(R.id.menu_viajes);
        do{
            addMenuItem(cursor_viajes.getString(0));
        }while(cursor_viajes.moveToNext());

    }

    /** Añade cada viaje en el subgrupo correspondiente del menú, en la posición que le corresponda */
    public void addMenuItem(String item_name){
        menu.add(R.id.menu_viajes, Menu.NONE, menu.size(), item_name);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        //Cuando vuelve de la actividad NuevoViajeActivity
        if(requestCode == ADD_VIAJE_RESULT){
            if(resultCode == Activity.RESULT_OK){
                postResult = Activity.RESULT_OK;
                // Obtiene el viaje recién creado en NuevoViajeActivity, añade su entrada al menú y lo guarda en la base de datos.
                Viaje v = data.getParcelableExtra("viaje");
                addMenuItem(v.getNombre_viaje());
                DatabaseAdapter.insertarViaje(v);

                // Si la moneda es diferente a euros, añade una tarea para cambiar a la moneda del país.
                String moneda = DatabaseAdapter.readMonedaPais(v.getPais());
                if(!moneda.equals("EUR")){
                    TareaModel tarea_moneda = new TareaModel("Cambiar moneda de EUR a " + moneda, v.getNombre_viaje(), 0);
                    DatabaseAdapter.insertarTarea(tarea_moneda);
                    v.addElemento(tarea_moneda);
                }

                // Interesa que el viaje seleccionado sea el que se acaba de añadir.
                viaje_seleccionado = v;

            }else if(resultCode == Activity.RESULT_CANCELED){
                return;
            }
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        //Debería ir al final de ADD_VIAJE_RESULT pero crashea al haber elementos no inicializados.
        if(postResult == Activity.RESULT_OK){
            startListaViajeFragment();
        }
    }

    public void onNuevoViajeClick(View view){
        startNuevoViajeActivity();
    }

    public void startNuevoViajeActivity(){
        Intent intent = new Intent(this, NuevoViajeActivity.class);
        startActivityForResult(intent, ADD_VIAJE_RESULT);
    }

    public void startListaViajeFragment(){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment fragment = new ListaViajeFragment();
        ft.replace(R.id.fragment_viajes, fragment).commit();
    }
}
