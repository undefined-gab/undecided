package com.my_note.com.my_note_app;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Toast;

public class MyDialogFragment extends DialogFragment implements DialogInterface.OnClickListener{
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder =
                new AlertDialog.Builder(getActivity())
                        .setTitle("My Note")
                        .setMessage("By Einstein奕韬\n" +
                                "Version: V0.0.4\n" +
                                "Updated: \n" +
                                "Increase the note sharing function, you can share the major social software, including the title and content of your notes, as well as sharing time.\n" +
                                "June 6, 2019 0:02:55")
                        .setPositiveButton("ok", this)
                        .setCancelable(false);
        // 这里不能调用show方法
        return builder.create();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case DialogInterface.BUTTON_POSITIVE:
                //Toast.makeText(getActivity(), "ok", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }
}
