package com.fcamposj.perumotos.include;



import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.fcamposj.perumotos.R;

public class MyToolbar {



    public static void show(AppCompatActivity activity, String title, boolean backButton){

        Toolbar toolbar = activity.findViewById(R.id.toolBar);
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setTitle(title);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(backButton);
    }
}
