package com.a2travel.core;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class JSONParser {

    //Recibe JSONObject devuelve lista:
    public List<HashMap<String, String>> parse(JSONObject jsonObject){
        JSONArray jsonPlaces = null;
        try{
            //Coge todos los places del JSON
            jsonPlaces = jsonObject.getJSONArray("predictions");
        }catch (Exception e){
            e.printStackTrace();
        }
        //Pasa el array de places a getPlaces
        return getPlaces(jsonPlaces);
    }

    private List<HashMap<String, String >> getPlaces(JSONArray jsonPlaces){

        int placesCount = jsonPlaces.length();
        List<HashMap<String, String>> placesList = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> place = null;

        //Se añaden uno a uno los places a la lista:
        for (int i=0; i<placesCount; i++){
            try{
                //Se llama a getPlace para parsear el place del JSON:
                place = getPlace((JSONObject)jsonPlaces.get(i));
                placesList.add(place);
            }catch (JSONException e){
                e.printStackTrace();
            }
        }

        return placesList;
    }

    private HashMap<String, String> getPlace(JSONObject jsonPlace){

        HashMap<String, String> place = new HashMap<String,String>();

        String id = "";
        String reference = "";
        String description = "";

        try{
            description = jsonPlace.getString("description");
            id = jsonPlace.getString("id");
            reference = jsonPlace.getString("reference");

            place.put("description", description);
            place.put("id", id);
            place.put("reference", reference);
            // Para quitar el país y mostrar sólo la ciudad:
            description = description.split(", ")[0];

            Log.w("JSONParser description", description);
        }catch (JSONException e){
            e.printStackTrace();
        }

        return place;
    }

    //Petición de JSON para la API del tiempo:

    private static final String OPEN_WEATHER_MAP_API = "http://api.openweathermap.org/data/2.5/forecast/daily?q=%s&units=metric&cnt=6";

    public static JSONObject getJSON(Context context, String city){
        try{
            URL url = new URL(String.format(OPEN_WEATHER_MAP_API, city));
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();

            connection.addRequestProperty("x-api-key", "489cdd3311c449e7310f26cd78be2821");

            BufferedReader bf = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            StringBuffer json = new StringBuffer(1024);
            String tmp = "";
            while ((tmp=bf.readLine())!=null){
                json.append(tmp).append("\n");
            }
            bf.close();

            JSONObject data = new JSONObject(json.toString());

            return data;

        }catch (Exception e){
            return null;
        }

    }

}
