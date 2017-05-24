package com.a2travel.core;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.AutoCompleteTextView;
import android.widget.SimpleAdapter;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class CiudadAutoCompleteTextView extends AutoCompleteTextView {
    AppCompatActivity parent;
    PaisAutocompleteTextView paisAutocompleteTextView;

    PlacesTask placesTask;
    ParserTask parserTask;
    public CiudadAutoCompleteTextView(Context context, AttributeSet attributeSet){
        super(context,attributeSet);
    }

    public void init(AppCompatActivity parent, final PaisAutocompleteTextView paisAutocompleteTextView){
        //Para poder utilizar getBaseContext();
        this.parent = parent;
        this.paisAutocompleteTextView = paisAutocompleteTextView;

        setThreshold(3); //A partir de una letra ya predice

        addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                placesTask = new PlacesTask();
                placesTask.execute(s.toString(), paisAutocompleteTextView.getText().toString());
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });


    }

    @Override
    public CharSequence convertSelectionToString(Object selectedItem){
        HashMap<String, String> hm = (HashMap<String, String>) selectedItem;
        return hm.get("description");
    }

    //Método para descargar JSON de la URL
    public String downloadUrl(String strUrl) throws IOException {

        String data = "";
        InputStream inputStream = null;
        HttpURLConnection urlConnection = null;

        try{
            URL url = new URL(strUrl);

            //Crear conexion http:
            urlConnection = (HttpURLConnection) url.openConnection();
            //Conectar a url:
            urlConnection.connect();
            //Leer datos de la url:
            inputStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            StringBuffer sb = new StringBuffer();

            String line = "";
            while((line = br.readLine()) != null){
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        }catch(Exception e){
        }finally {
            inputStream.close();
            urlConnection.disconnect();
        }

        return data;
    }

    //Recupera todos los places del autocomplete:
    private class PlacesTask extends AsyncTask<String, Void ,String> {

        protected String doInBackground(String... params){
            //Para guardar los datos:
            String data = "";
            //KEY de GOOGLE:
            String key = "key=AIzaSyB1SS7N_0HusLZhoTjcVHKbJeq_BFKZyOc";
            String input = "";

            try{
                input = "input=" + URLEncoder.encode(params[0], "utf-8");
            }catch(UnsupportedEncodingException e1){
                e1.printStackTrace();
            }

            //Tipo de sitio que buscamos, en nuestro caso ciudades:
            String type = "types=(cities)";

            //Idioma en el que saldrán los nombres de los países:
            String idioma = "language="+ Locale.getDefault().getDisplayLanguage();
            //Pais:
            String pais = "components=country:" + DatabaseAdapter.readCodigoPais(params[1]);
            //Parametros de busqueda:
            String parameters = input+"&"+type+"&"+key+"&"+pais+"&"+idioma;
            //Formato de salida:
            String output = "json";
            //Construccion de la URL para el servidor:
            String url = "https://maps.googleapis.com/maps/api/place/autocomplete/"+output+"?"+parameters;

            try{
                //Se obtiene la info del servidor:
                data = downloadUrl(url);
            }catch (Exception e){
                e.printStackTrace();
            }

            return data;
        }

        @Override
        protected void onPostExecute(String result){
            super.onPostExecute(result);
            //Crear ParserTask:
            parserTask = new ParserTask();
            //Parsear el JSON obtenido del servidor:
            parserTask.execute(result);
        }

    }


    //Clase para parsear el JSON de google places:
    private class ParserTask extends AsyncTask<String, Integer, List<HashMap<String,String>>>{

        JSONObject jsonObject;

        @Override
        protected List<HashMap<String,String>> doInBackground (String... jsondata){

            List<HashMap<String,String>> places = null;

            JSONParser placeJSONParser = new JSONParser();

            try{
                jsonObject = new JSONObject(jsondata[0]);
                places = placeJSONParser.parse(jsonObject);
            }catch (Exception e){
            }
            return places;
        }

        @Override
        protected void onPostExecute(List<HashMap<String,String>> result){

            String [] from = new String[] {"description"};
            int[] to = new int[] {android.R.id.text1};

            //Se crea un adapter:
            SimpleAdapter adapter = new SimpleAdapter(parent.getBaseContext(), result, android.R.layout.simple_list_item_1, from, to);
            //Configurar el adapter:
            setAdapter(adapter);
        }

    }

}
