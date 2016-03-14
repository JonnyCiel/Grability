package com.jonny.grability.ui;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.jonny.grability.R;

public class AppsDetails extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apps_details);

        setTitle(getString(R.string.tituloAppsDetails));

        if ((getResources().getConfiguration().screenLayout &      Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE) {
            setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        }
        else if ((getResources().getConfiguration().screenLayout &      Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_NORMAL) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        }
        else if ((getResources().getConfiguration().screenLayout &      Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_SMALL) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        else {
            setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }


        TextView mTextViewTitle = (TextView) findViewById(R.id.detailsTitle);
        ImageView imageView = (ImageView) findViewById(R.id.detailsImage);
        TextView textViewDescrip = (TextView) findViewById(R.id.detailsDescrip);
        TextView textViewCategoria = (TextView) findViewById(R.id.detailsCategoria);
        TextView textViewAutor = (TextView) findViewById(R.id.detailsAutor);

        Intent intent = getIntent();


        String title = intent.getStringExtra("Title");
        String descrip = intent.getStringExtra("Descrip");
        String categoria = intent.getStringExtra("Categoria");
        String autor = intent.getStringExtra("Autor");


        imageView.setImageBitmap((Bitmap) intent.getParcelableExtra("Imagen"));
        mTextViewTitle.setText(title);
        textViewDescrip.setText(descrip);
        textViewCategoria.setText(categoria);
        textViewAutor.setText(autor);


    }
}
