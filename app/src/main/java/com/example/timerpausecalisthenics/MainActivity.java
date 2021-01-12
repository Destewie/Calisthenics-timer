package com.example.timerpausecalisthenics;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity
{

    TextView tvCountDown;
    Button btnAvviaPausa, btn2Min, btn230Min;

    int tempoInMillis = 150000; // 2:30 in millisecondi. È il tempo del recupero

    //-------------------------------------------------------------------------------------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvCountDown = findViewById(R.id.tvTimer);
        btnAvviaPausa = findViewById(R.id.btnAvviaPausa);
        btn2Min = findViewById(R.id.btnDueMin);
        btn230Min = findViewById(R.id.btnDueMinEMezzo);


    }

}