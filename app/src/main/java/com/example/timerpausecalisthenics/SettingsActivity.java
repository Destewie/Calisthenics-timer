package com.example.timerpausecalisthenics;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

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

        txtSecondiBottone1.setText(Long.toString( MainActivity.tempo_bottone1 / 1000));
        txtSecondiBottone2.setText(Long.toString( MainActivity.tempo_bottone2 / 1000));
        txtSecondiBottone3.setText(Long.toString( MainActivity.tempo_bottone3 / 1000));
        txtSecondiBottone4.setText(Long.toString( MainActivity.tempo_bottone4 / 1000));


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

        int int1 = Integer.parseInt(txtSecondiBottone1.getText().toString()), int2 = Integer.parseInt(txtSecondiBottone2.getText().toString()), int3 = Integer.parseInt(txtSecondiBottone3.getText().toString()), int4 = Integer.parseInt(txtSecondiBottone4.getText().toString());
        MainActivity.aggiornaBottoniRapidi(int1, int2, int3, int4);

        editor.putBoolean(MainActivity.VOCE, voceLocale);
        editor.putBoolean(MainActivity.NOTIFICA, notificaLocale);

        editor.apply();
    }

    //-----------------------------------------------------------------------------------------------------------------------------------------------


}