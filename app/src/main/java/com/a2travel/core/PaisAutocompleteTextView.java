package com.a2travel.core;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Filterable;
import android.widget.ListAdapter;


public class PaisAutocompleteTextView extends AutoCompleteTextView {
    String[] arrayPaises;
    private boolean isValid;

    // La instancia de ciudadAutoCompleteTextView, para activar su atributo focusable cuando se deba.
    CiudadAutoCompleteTextView ciudadAutoCompleteTextView;

    public PaisAutocompleteTextView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);

    }

    public void init(final String[] arrayPaises, final CiudadAutoCompleteTextView ciudadAutoCompleteTextView){
        this.arrayPaises = arrayPaises;
        this.ciudadAutoCompleteTextView = ciudadAutoCompleteTextView;
        ArrayAdapter<String> adapterPais = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, arrayPaises);
        setAdapter(adapterPais);

        //Para evitar que el usuario ponga un país que no esté en la lista:
        setOnFocusChangeListener(new View.OnFocusChangeListener(){
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                AutoCompleteTextView tv = (AutoCompleteTextView)v;
                String text = tv.getText().toString();
                //Si se quita el focus del elemento y no es ningún país de la lista, se vacía el TextView.
                if(!hasFocus && !isValid){
                        tv.setText("");
                }
            }
        });

        addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //Comprobamos que el país coincida con alguno de la bbdd
                checkPaisValido(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                //Si el país coincide con alguno de la bbdd, activamos el campo para buscar ciudad.
                if(isValid){
                    ciudadAutoCompleteTextView.setEnabled(true);
                }else{
                    ciudadAutoCompleteTextView.setEnabled(false);
                }
            }
        });
    }

    private boolean checkPaisValido(String text){
        isValid = false;
        for(String pais : arrayPaises){
            if(text.compareTo(pais) == 0){
                isValid = true;
            }
        }
        return isValid;
    }
}
