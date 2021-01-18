package com.example.timerpausecalisthenics;

import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class SettingsActivity extends AppCompatActivity
{

    EditText txtSecondiBottone1, txtSecondiBottone2, txtSecondiBottone3, txtSecondiBottone4;
    Button btnIndietro;
    RadioGroup rgAudio;

    RadioButton radio; //temp
    boolean voceLocale, notificaLocale; //le variabili di riferimento sono quelle statiche in mainactivity, queste sono locali alla settingsactivity e hanno durata pari a quella di questa activity

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        voceLocale = MainActivity.voce;
        notificaLocale = MainActivity.notifica;

        txtSecondiBottone1 = findViewById(R.id.txtSecondiBottone1);
        txtSecondiBottone2 = findViewById(R.id.txtSecondiBottone2);
        txtSecondiBottone3 = findViewById(R.id.txtSecondiBottone3);
        txtSecondiBottone4 = findViewById(R.id.txtSecondiBottone4);
        btnIndietro = findViewById(R.id.btnIndietro);
        rgAudio = (RadioGroup) findViewById(R.id.rgAudio);

        //setto il testo giusto

        txtSecondiBottone1.setText(Integer.toString( MainActivity.tempo_bottone1 / 1000));
        txtSecondiBottone1.setHint(Integer.toString( MainActivity.tempo_bottone1 / 1000));
        txtSecondiBottone2.setText(Integer.toString( MainActivity.tempo_bottone2 / 1000));
        txtSecondiBottone2.setHint(Integer.toString( MainActivity.tempo_bottone2 / 1000));
        txtSecondiBottone3.setText(Integer.toString( MainActivity.tempo_bottone3 / 1000));
        txtSecondiBottone3.setHint(Integer.toString( MainActivity.tempo_bottone3 / 1000));
        txtSecondiBottone4.setText(Integer.toString( MainActivity.tempo_bottone4 / 1000));
        txtSecondiBottone4.setHint(Integer.toString( MainActivity.tempo_bottone4 / 1000));


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
                        voceLocale = true;
                        notificaLocale = false;
                        break;

                    case R.id.rbNotifica:
                        voceLocale = false;
                        notificaLocale = true;
                        break;

                    case R.id.rbSilenzioso:
                        voceLocale = false;
                        notificaLocale = false;
                        break;
                }
            }
        });
        
    }

    //-----------------------------------------------------------------------------------------------------------------------------------------------

    @Override
    protected void onStop() //quando, per un motivo o per l'altro, la activity viene distrutta
    {
        super.onStop();

        SharedPreferences prefs = getSharedPreferences(MainActivity.PREFERENZE, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        MainActivity.voce = voceLocale;
        MainActivity.notifica = notificaLocale;

        editor.putBoolean(MainActivity.VOCE, voceLocale);
        editor.putBoolean(MainActivity.NOTIFICA, notificaLocale);

        editor.apply();
    }

    //-----------------------------------------------------------------------------------------------------------------------------------------------


}