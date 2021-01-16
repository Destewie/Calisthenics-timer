package com.example.timerpausecalisthenics;

import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.os.CountDownTimer;
import android.provider.Settings;
import android.service.notification.StatusBarNotification;
import android.text.Editable;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Random;

public class MainActivity extends AppCompatActivity
{
    private static final int UN_MINUTO = 60000;
    private static final int UN_MINUTO_E_MEZZO = 90000;
    private static final int DUE_MINUTI = 120000;
    private static final int DUE_MINUTI_E_MEZZO = 150000;


    private static final String TEMPO_IMPOSTATO = "tempoImpostato";
    private static final String TEMPO_RESTANTE = "tempoRestante";
    private static final String VOCE = "voce";
    private static final String NOTIFICA = "notifica";
    private static final String CONTANDO = "contando";


    private static final int NOTIF_ID_TEMPORESTANTE = 666;
    private static final String NOTIF_CHANNEL_ID_TEMPO = "777";
    NotificationManagerCompat notificationManager;

    TextView tvCountDown;
    Button btnAvviaPausa, btnReset, btnImpostazioni, btn1Min, btn130Min, btn2Min, btn230Min;
    FloatingActionButton btnCustom;
    public static CountDownTimer timer;

    boolean contando = false; //se il tempo sta scorrendo
    long tempoInMillis = 150000; // 2:30 in millisecondi. È il tempo del recupero
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

        btnAvviaPausa = findViewById(R.id.btnAvviaPausa);
        btnReset = findViewById(R.id.btnReset);
        btnImpostazioni = findViewById(R.id.btnImpostazioni);
        btn1Min = findViewById(R.id.btnUnMin);
        btn130Min = findViewById(R.id.btnUnMinEMezzo);
        btn2Min = findViewById(R.id.btnDueMin);
        btn230Min = findViewById(R.id.btnDueMinEMezzo);
        btnCustom = findViewById(R.id.fabCustom);

        Intent i = new Intent(this, SettingsActivity.class);

        tvCountDown.setText(calcolaTestoTimer());
        btnReset.setVisibility(View.INVISIBLE);

        rand = new Random();

        creazioneCanaleDiNotifica(NOTIF_CHANNEL_ID_TEMPO);

        // If we have a saved state then we can restore it now
        if (savedInstanceState != null)
        {
            tempoInMillis = savedInstanceState.getLong(TEMPO_IMPOSTATO, 150000);
            tempoRestanteInMillis = savedInstanceState.getLong(TEMPO_RESTANTE,  150000 );
            voce  = savedInstanceState.getBoolean(VOCE, true);
            notifica  = savedInstanceState.getBoolean(NOTIFICA, false);
            contando  = savedInstanceState.getBoolean(CONTANDO, false);

            tvCountDown.setText(calcolaTestoTimer());
            if(contando)
            {
                timer.cancel();
                avviaTimer();
            }

            if(!contando && tempoRestanteInMillis != tempoInMillis)
            {
                btnReset.setVisibility(View.VISIBLE);
                btnAvviaPausa.setText("RIPRENDI LA PAUSA");
            }
            else if (!contando && tempoRestanteInMillis == tempoInMillis)
                btnReset.setVisibility(View.INVISIBLE);

        }


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


        btn1Min.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                cambiaTempoInMillis(UN_MINUTO);
            }
        });


        btn130Min.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                cambiaTempoInMillis(UN_MINUTO_E_MEZZO);
            }
        });


        btn2Min.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                cambiaTempoInMillis(DUE_MINUTI);
            }
        });


        btn230Min.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                cambiaTempoInMillis(DUE_MINUTI_E_MEZZO);
            }
        });


        btnCustom.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);

                alert.setTitle("Pausa custom");
                alert.setMessage("Inserisci i secondi di pausa che vuoi fare");

                // Set an EditText view to get user input
                final EditText input = new EditText(MainActivity.this);
                alert.setView(input);

                alert.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int whichButton)
                    {
                        // Do something with value!
                        try
                        {
                            int value = Integer.parseInt(input.getText().toString());
                            cambiaTempoInMillis(value * 1000);
                        }
                        catch(Exception e)
                        {
                            Toast toast=Toast.makeText(MainActivity.this,"C'è stato un errore con il tuo inserimento :(",Toast.LENGTH_SHORT);
                            toast.show();
                        }

                    }
                });

                alert.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Canceled.
                    }
                });

                alert.show();
            }

        });

    }

    //-------------------------------------------------------------------------------------------------------------------------------------------------------------------

    public void onDestroy()
    {

        super.onDestroy();

        distruggiNotifica();

    }

    //-------------------------------------------------------------------------------------------------------------------------------------------------------------------

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        // Make sure to call the super method so that the states of our views are saved
        super.onSaveInstanceState(outState);

        // Save your own state now
        outState.putLong(TEMPO_IMPOSTATO, tempoInMillis);
        outState.putLong(TEMPO_RESTANTE, tempoRestanteInMillis);
        outState.putBoolean(VOCE, voce);
        outState.putBoolean(NOTIFICA, notifica);
        outState.putBoolean(CONTANDO, contando);

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
                    int r = rand.nextInt(15);
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
                        case 5:
                            mp = MediaPlayer.create(getApplicationContext(), R.raw.cazzeggiare);
                            break;
                        case 6:
                            mp = MediaPlayer.create(getApplicationContext(), R.raw.pump);
                            break;
                        case 7:
                            mp = MediaPlayer.create(getApplicationContext(), R.raw.secco);
                            break;
                        case 8:
                            mp = MediaPlayer.create(getApplicationContext(), R.raw.vaiuomo);
                            break;
                        case 9:
                            mp = MediaPlayer.create(getApplicationContext(), R.raw.microbo);
                            break;
                        case 10:
                            mp = MediaPlayer.create(getApplicationContext(), R.raw.soffrire);
                            break;
                        case 11:
                            mp = MediaPlayer.create(getApplicationContext(), R.raw.spingivise);
                            break;
                        case 12:
                            mp = MediaPlayer.create(getApplicationContext(), R.raw.spugna);
                            break;
                        default:
                            mp = MediaPlayer.create(getApplicationContext(), R.raw.vaiuomo);
                            break;
                    };

                    mp.start();
                    //creazioneNotifica(NOTIF_CHANNEL_ID_TEMPO, NOTIF_ID_TEMPORESTANTE,"Fine della pacchia...", "Torna a spingere!", true, false, 2, false);

                }

                else if(!voce && notifica)
                {
                    creazioneNotifica(NOTIF_CHANNEL_ID_TEMPO, NOTIF_ID_TEMPORESTANTE,"Fine della pacchia...", "Torna a spingere!", true, false, 2, true);
                }

            }

        }.start();

        btnAvviaPausa.setText("Ferma Timer");
        btnReset.setVisibility(View.INVISIBLE);
    }

    //-------------------------------------------------------------------------------------------------------------------------------------------------------------------

    public void distruggiNotifica()
    {
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        StatusBarNotification[] notifications = mNotificationManager.getActiveNotifications();
        for (StatusBarNotification notification : notifications)
        {
            if (notification.getId() == NOTIF_ID_TEMPORESTANTE)    //se la notifica è attiva in questo momento, allora cancellala
            {
                notificationManager.cancel( NOTIF_ID_TEMPORESTANTE ) ;
            }
        }
    }

    //-------------------------------------------------------------------------------------------------------------------------------------------------------------------

    public void stoppaTimer()
    {
        timer.cancel();

        contando = false;
        btnAvviaPausa.setText("Riprendi la pausa");
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

        distruggiNotifica();


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

    private void creazioneNotifica(String channelID, int notificationID, String titolo, String testo, boolean siCancellaSulClick, boolean nonEliminabile, int priorita, boolean suoni) {
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
                .setVibrate(new long[]{1000, 1000, 1000});

        if (suoni)
            mBuilder.setSound(Settings.System.DEFAULT_NOTIFICATION_URI) //prende le impostazioni di default per quanto riguarda le notifiche
                    .setVibrate(new long[]{1000, 1000, 1000});

        notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(notificationID, mBuilder.build());
    }

    //----------------------------------------------------------------------------------------------------------------------------------------

}