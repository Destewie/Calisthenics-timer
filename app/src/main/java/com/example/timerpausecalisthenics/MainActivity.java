package com.example.timerpausecalisthenics;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.os.CountDownTimer;
import android.provider.Settings;
import android.service.notification.StatusBarNotification;
import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

import static android.app.Notification.GROUP_ALERT_SUMMARY;

public class MainActivity extends AppCompatActivity
{
    private static final int NOTIF_ID_TEMPORESTANTE = 666;
    private static final String NOTIF_CHANNEL_ID_TEMPO = "777";
    NotificationManagerCompat notificationManager;


    TextView tvCountDown;
    Button btnAvviaPausa, btnReset, btnImpostazioni, btn1Min, btn130Min, btn2Min, btn230Min;
    FloatingActionButton floatingTimerSetter;
    boolean contando = false; //se il tempo sta scorrendo

    CountDownTimer timer;

    int tempoInMillis = 150000; // 2:30 in millisecondi. È il tempo del recupero
    long tempoRestanteInMillis = tempoInMillis;

    Random rand;

    MediaPlayer mp;
    public static boolean voce = true, notifica = false; //se sono entrambe false, allora vuole il silenzioso


    //-------------------------------------------------------------------------------------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvCountDown = findViewById(R.id.text_view_countdown);
        floatingTimerSetter = findViewById(R.id.floatingActionButton);

        btnAvviaPausa = findViewById(R.id.btnAvviaPausa);
        btnReset = findViewById(R.id.btnReset);
        btnImpostazioni = findViewById(R.id.btnImpostazioni);
        btn1Min = findViewById(R.id.btnUnMin);
        btn130Min = findViewById(R.id.btnUnMinEMezzo);
        btn2Min = findViewById(R.id.btnDueMin);
        btn230Min = findViewById(R.id.btnDueMinEMezzo);

        Intent i = new Intent(this, SettingsActivity.class);

        tvCountDown.setText(calcolaTestoTimer());
        btnReset.setVisibility(View.INVISIBLE);

        rand = new Random();

        creazioneCanaleDiNotifica(NOTIF_CHANNEL_ID_TEMPO);


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

        btnImpostazioni.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startActivity(i);
            }
        });

        floatingTimerSetter.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Toast.makeText(getApplicationContext(), "Devo ancora implementare questo tasto :(", Toast.LENGTH_LONG).show();
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
        timer = new CountDownTimer(tempoRestanteInMillis,10)
        {
            @Override
            public void onTick(long millisAllaFine)
            {
                tempoRestanteInMillis = millisAllaFine;
                tvCountDown.setText(calcolaTestoTimer());
                //creazioneNotifica(NOTIF_CHANNEL_ID_TEMPO, NOTIF_ID_TEMPORESTANTE,"Riposati pure :)", calcolaTestoTimer() , false, true, 2);
            }

            @Override
            public void onFinish()
            {
                resetTimer();
                tvCountDown.setText("POMPA!");

                if(voce && !notifica)
                {
                    int r = rand.nextInt(5);
                    switch (r)
                    {
                        case 0:
                            mp = MediaPlayer.create(getApplicationContext(), R.raw.durouomo);
                            break;
                        case 1:
                            mp = MediaPlayer.create(getApplicationContext(), R.raw.bastacincischiare);
                            break;
                        case 2:
                            mp = MediaPlayer.create(getApplicationContext(), R.raw.oradispingere);
                            break;
                        case 3:
                            mp = MediaPlayer.create(getApplicationContext(), R.raw.spingi);
                            break;
                        case 4:
                            mp = MediaPlayer.create(getApplicationContext(), R.raw.spingiunpochino);
                            break;
                        default:
                            mp = MediaPlayer.create(getApplicationContext(), R.raw.spingiunpochino);
                            break;
                    };

                    mp.start();
                }

                else if(!voce && notifica)
                {
                    creazioneNotifica(NOTIF_CHANNEL_ID_TEMPO, NOTIF_ID_TEMPORESTANTE,"Fine della pacchia...", "Torna a spingere!", true, false, 2);
                }

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

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        StatusBarNotification[] notifications = mNotificationManager.getActiveNotifications();
        for (StatusBarNotification notification : notifications)
        {
            if (notification.getId() == NOTIF_ID_TEMPORESTANTE)    //se la notifica è attiva in questo momento, allora cancellala
            {
                notificationManager.cancel( NOTIF_ID_TEMPORESTANTE ) ;
            }
        }


        tempoRestanteInMillis = tempoInMillis;

        tvCountDown.setText(calcolaTestoTimer());
        btnAvviaPausa.setText("Vai in pausa");
        btnReset.setVisibility(View.INVISIBLE);
    }

    //-------------------------------------------------------------------------------------------------------------------------------------------------------------------

    public String calcolaTestoTimer()
    {
         int minuti, secondi, centesimi;

         minuti = (int) tempoRestanteInMillis / 1000 / 60;
         secondi = (int) (tempoRestanteInMillis / 1000) % 60 ;
         centesimi = (int) (tempoRestanteInMillis % 1000) / 10; // non sono sicuro

        return String.format("%02d:%02d:%02d", minuti, secondi, centesimi);
    }

//----------------------------------------------------------------------------------------------------------------------------------------

    private void creazioneCanaleDiNotifica(String numCanale)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            CharSequence name = "Canale";
            String description = "Canale per i perditempo";

            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(numCanale, name, importance);
            channel.setDescription(description);

            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    //----------------------------------------------------------------------------------------------------------------------------------------

    @SuppressLint("WrongConstant")
    private void creazioneNotifica(String channelID, int notificationID, String titolo, String testo, boolean siCancellaSulClick, boolean nonEliminabile, int priorita)
    {
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);


        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, channelID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(titolo)
                .setContentText(testo)
                .setContentIntent(pendingIntent)
                .setPriority(priorita) //MAX = 2 //MIN = -2
                .setAutoCancel(siCancellaSulClick)
                .setOngoing(nonEliminabile)
                //.setStyle(new NotificationCompat.BigTextStyle().bigText(testo));
                .setVibrate(new long[]{0L}) //no vibrazione
                .setGroupAlertBehavior(GROUP_ALERT_SUMMARY)
                .setGroupSummary(false)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI); //prende le impostazioni di default per quanto riguarda le notifiche

        notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(notificationID, mBuilder.build());
    }

    //----------------------------------------------------------------------------------------------------------------------------------------

}