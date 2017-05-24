package com.a2travel.core;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;

import com.a2travel.R;

/** Diálogo genérico para avisar de que hay campos vacíos en algún formulario.
 * */
public class DialogCamposVacios extends AlertDialog.Builder {
    public DialogCamposVacios(@NonNull Context context) {
        super(context);

        setTitle(R.string.dialog_title);
        setMessage(R.string.dialog_content_campos_vacios);
        setPositiveButton(R.string.dialog_aceptar, new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
    }

}
