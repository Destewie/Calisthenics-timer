package com.example.timerpausecalisthenics;

import android.media.MediaPlayer;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.CountDownTimer;
import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import java.util.Random;

public class MainActivity extends AppCompatActivity
{

    TextView tvCountDown;
    Button btnAvviaPausa, btnReset, btn1Min, btn130Min, btn2Min, btn230Min;
    boolean contando = false; //se il tempo sta scorrendo

    CountDownTimer timer;

    int tempoInMillis = 150000; // 2:30 in millisecondi. È il tempo del recupero
    long tempoRestanteInMillis = tempoInMillis;

    Random rand;
    MediaPlayer mp;

    //-------------------------------------------------------------------------------------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvCountDown = findViewById(R.id.text_view_countdown);
        btnAvviaPausa = findViewById(R.id.btnAvviaPausa);
        btnReset = findViewById(R.id.btnReset);
        btn1Min = findViewById(R.id.btnUnMin);
        btn130Min = findViewById(R.id.btnUnMinEMezzo);
        btn2Min = findViewById(R.id.btnDueMin);
        btn230Min = findViewById(R.id.btnDueMinEMezzo);

        aggiornaTestoTimer();
        btnReset.setVisibility(View.INVISIBLE);

        rand = new Random();


        btnAvviaPausa.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(contando)
                {
                    stoppaTimer();
                }
                else
                {
                    avviaTimer();
                }
            }
        });


        btnReset.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                resetTimer();
            }
        });


        btn1Min.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                cambiaTempoInMillis(60000);
            }
        });


        btn130Min.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                cambiaTempoInMillis(90000);
            }
        });


        btn2Min.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                cambiaTempoInMillis(120000);
            }
        });


        btn230Min.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                cambiaTempoInMillis(150000);
            }
        });

    }

    //-------------------------------------------------------------------------------------------------------------------------------------------------------------------

    public void cambiaTempoInMillis(int t)
    {
        tempoInMillis = t;

        resetTimer();
    }

    //-------------------------------------------------------------------------------------------------------------------------------------------------------------------

    public void avviaTimer()
    {
        contando = true;
        timer = new CountDownTimer(tempoRestanteInMillis,1)
        {
            @Override
            public void onTick(long millisAllaFine)
            {
                tempoRestanteInMillis = millisAllaFine;
                aggiornaTestoTimer();
            }

            @Override
            public void onFinish()
            {
                resetTimer();
                tvCountDown.setText("POMPA!");

                int r = rand.nextInt(4);
                switch (r)
                {
                    case 0:
                        mp = MediaPlayer.create(getApplicationContext(), R.raw.durouomo);
                        break;
                    case 1:
                        mp = MediaPlayer.create(getApplicationContext(), R.raw.forzabro);
                        break;
                    case 2:
                        mp = MediaPlayer.create(getApplicationContext(), R.raw.pompare);
                        break;
                    case 3:
                        mp = MediaPlayer.create(getApplicationContext(), R.raw.spingi);
                        break;
                    default:
                        mp = MediaPlayer.create(getApplicationContext(), R.raw.pompare);
                        break;
                };

                mp.start();
            }

        }.start();

        btnAvviaPausa.setText("Ferma Timer");
        btnReset.setVisibility(View.INVISIBLE);
    }

    //-------------------------------------------------------------------------------------------------------------------------------------------------------------------

    public void stoppaTimer()
    {
        timer.cancel();

        contando = false;
        btnAvviaPausa.setText("Vai in pausa");
        btnReset.setVisibility(View.VISIBLE);
    }

    //-------------------------------------------------------------------------------------------------------------------------------------------------------------------

    public void resetTimer()
    {
        if(contando)
        {
            timer.cancel();
            contando = false;
        }

        tempoRestanteInMillis = tempoInMillis;

        aggiornaTestoTimer();
        btnAvviaPausa.setText("Vai in pausa");
        btnReset.setVisibility(View.INVISIBLE);
    }

    //-------------------------------------------------------------------------------------------------------------------------------------------------------------------

    public void aggiornaTestoTimer()
    {
         int minuti, secondi, centesimi;

         minuti = (int) tempoRestanteInMillis / 1000 / 60;
         secondi = (int) (tempoRestanteInMillis / 1000) % 60 ;
         centesimi = (int) (tempoRestanteInMillis % 1000) / 10; // non sono sicuro

        String tempoFormattato = String.format("%02d:%02d:%02d", minuti, secondi, centesimi);
        tvCountDown.setText(tempoFormattato);
    }

    //-------------------------------------------------------------------------------------------------------------------------------------------------------------------


}