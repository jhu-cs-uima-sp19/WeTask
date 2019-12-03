package com.example.wetask;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class ConfirmDialog extends DialogFragment {

    private String action = "";

    ConfirmDialog(String action) {
        this.action = action;
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder( getActivity( ) );

        if (action.equals("complete")) {
            builder.setTitle( R.string.confirm_complete_title );
        } else if (action.equals("delete")) {
            builder.setMessage( R.string.confirm_delete_message ).setTitle( R.string.confirm_delete_title );
        } else {
            builder.setTitle(R.string.error);
        }

        // Add the buttons
        builder.setPositiveButton( R.string.ok, new DialogInterface.OnClickListener( ) {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                if(action.equals("complete")) {
                    ((ViewTaskActivity) getActivity( )).complete_task( );
                    //move to archive/completed queue, pop if it has reached capacity
                } else if (action.equals("delete")) {
                    ((ViewTaskActivity) getActivity( )).delete_task( );
                } else {
                    //error--should never be reached
                }
                dialog.dismiss();
            }
        } );
        builder.setNegativeButton( R.string.cancel, new DialogInterface.OnClickListener( ) {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
                dialog.dismiss();
            }
        } );

        // Create the AlertDialog
        AlertDialog dialog = builder.create( );
        return dialog;
    }
}
