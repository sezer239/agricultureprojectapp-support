package com.example.kadir.agricultureprojectsupportside.ShortCut;

import android.content.Context;
import android.widget.Toast;

public class ShortCut {

    public static void displayMessageToast(Context context, String displayMessage) {
        Toast.makeText(context, displayMessage, Toast.LENGTH_LONG).show();
    }

}
