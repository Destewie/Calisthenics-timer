package com.example.timerpausecalisthenics;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class SettingsActivity extends AppCompatActivity
{

    Button btnIndietro;
    RadioGroup rgAudio;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        btnIndietro = findViewById(R.id.btnIndietro);
        rgAudio = (RadioGroup) findViewById(R.id.rgAudio);
        RadioButton radio;

        //checko il radio button giusto
        if(MainActivity.voce == true)
            radio = findViewById(R.id.rbVoci);
        else if(MainActivity.notifica == true)
            radio = findViewById(R.id.rbNotifica);
        else
            radio = findViewById(R.id.rbSilenzioso);
        radio.setChecked(true);


        btnIndietro.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });


        rgAudio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) // checkedId is the RadioButton selected
            {
                switch (checkedId)
                {
                    case R.id.rbVoci:
                        MainActivity.voce = true;
                        MainActivity.notifica = false;
                        break;

                    case R.id.rbNotifica:
                        MainActivity.voce = false;
                        MainActivity.notifica = true;
                        break;

                    case R.id.rbSilenzioso:
                        MainActivity.voce = false;
                        MainActivity.notifica = false;
                        break;
                }
            }
        });
        
    }
}