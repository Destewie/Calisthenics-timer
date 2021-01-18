package com.example.timerpausecalisthenics;

import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
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
    public static int tempo_bottone1 = 60000;
    public static int tempo_bottone2 = 90000;
    public static int tempo_bottone3 = 120000;
    public static int tempo_bottone4 = 150000;

    public static final String PREFERENZE = "prefs";

    public static final String TEMPO_IMPOSTATO = "tempoImpostato";
    public static final String TEMPO_RESTANTE = "tempoRestante";
    public static final String TEMPO_FINE = "tempoFine";
    public static final String VOCE = "voce";
    public static final String NOTIFICA = "notifica";
    public static final String CONTANDO = "contando";


    private static final int NOTIF_ID_TEMPORESTANTE = 666;
    private static final String NOTIF_CHANNEL_ID_TEMPO = "777";
    NotificationManagerCompat notificationManager;

    TextView tvCountDown;
    Button btnAvviaPausa, btnReset, btnImpostazioni, btn1Min, btn130Min, btn2Min, btn230Min;
    FloatingActionButton btnCustom;
    public static CountDownTimer timer;

    boolean contando = false; //se il tempo sta scorrendo
    long tempoInMillis = 150000; // 2:30 in millisecondi. È il tempo del recupero
    long tempoRestanteInMillis = tempoInMillis, oraDiFine;

    Random rand;
    int orientamentoTelefono;

    MediaPlayer mp;
    public static boolean voce = true, notifica = false; //se sono entrambe false, allora vuole il silenzioso


    //-------------------------------------------------------------------------------------------------------------------------------------------------------------------
    //CICLO DI VITA DELL'ACTIVITY
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

        orientamentoTelefono = MainActivity.this.getResources().getConfiguration().orientation;

        rand = new Random();

        creazioneCanaleDiNotifica(NOTIF_CHANNEL_ID_TEMPO);

        // ------ BOTTONI -------

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
                cambiaTempoInMillis(tempo_bottone1);
            }
        });


        btn130Min.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                cambiaTempoInMillis(tempo_bottone2);
            }
        });


        btn2Min.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                cambiaTempoInMillis(tempo_bottone3);
            }
        });


        btn230Min.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                cambiaTempoInMillis(tempo_bottone4);
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
                            Toast toast=Toast.makeText(MainActivity.this,"C'è stato un errore con il tuo inserimento :(", Toast.LENGTH_SHORT);
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

    @Override
    protected void onStart() //quando l'activity viene ricreata
    {
        super.onStart();

        SharedPreferences prefs = getSharedPreferences(PREFERENZE, MODE_PRIVATE);

        tempoInMillis = prefs.getLong(TEMPO_IMPOSTATO, 150000);
        tempoRestanteInMillis = prefs.getLong(TEMPO_RESTANTE,  150000);
        voce  = prefs.getBoolean(VOCE, true);
        notifica  = prefs.getBoolean(NOTIFICA, false);
        contando  = prefs.getBoolean(CONTANDO, false);

        if(contando)
        {
            oraDiFine = prefs.getLong(TEMPO_FINE, 150000);
            tempoRestanteInMillis = oraDiFine - System.currentTimeMillis();

            if(tempoRestanteInMillis < 0) //il timer è scaduto mentre la activity era morta
            {
                tempoRestanteInMillis = tempoInMillis;
                contando = false;
            }
            else
            {
                avviaTimer();
            }
        }

        if(!contando && tempoRestanteInMillis != tempoInMillis) //se il timer è stato avviato ed è attualmente in pausa
        {
            btnReset.setVisibility(View.VISIBLE);

            if(orientamentoTelefono == Configuration.ORIENTATION_PORTRAIT)
                btnAvviaPausa.setText("RIPRENDI LA PAUSA");
            else
                btnAvviaPausa.setText("TORNA IN PAUSA");
        }
        else if (!contando && tempoRestanteInMillis == tempoInMillis)  //se il timer è fermo e al punto di partenza
            btnReset.setVisibility(View.INVISIBLE);

        tvCountDown.setText(calcolaTestoTimer());
    }

    //-------------------------------------------------------------------------------------------------------------------------------------------------------------------

    @Override
    protected void onStop() //quando, per un motivo o per l'altro, la activity viene distrutta
    {
        super.onStop();

        distruggiNotifica();

        SharedPreferences prefs = getSharedPreferences(PREFERENZE, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        // Save your own state now
        editor.putLong(TEMPO_IMPOSTATO, tempoInMillis);
        editor.putLong(TEMPO_RESTANTE, tempoRestanteInMillis);
        editor.putLong(TEMPO_FINE, oraDiFine);
        editor.putBoolean(VOCE, voce);
        editor.putBoolean(NOTIFICA, notifica);
        editor.putBoolean(CONTANDO, contando);

        editor.apply();
    }

    //-------------------------------------------------------------------------------------------------------------------------------------------------------------------
    // CAMBI AL TIMER
    //-------------------------------------------------------------------------------------------------------------------------------------------------------------------

    public void cambiaTempoInMillis(int t)
    {
        tempoInMillis = t;

        resetTimer();
    }

    //-------------------------------------------------------------------------------------------------------------------------------------------------------------------

    public void avviaTimer()
    {
        oraDiFine = System.currentTimeMillis() + tempoRestanteInMillis;

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

    public void stoppaTimer()
    {
        timer.cancel();

        contando = false;

        if(orientamentoTelefono == Configuration.ORIENTATION_PORTRAIT)
            btnAvviaPausa.setText("RIPRENDI LA PAUSA");
        else
            btnAvviaPausa.setText("TORNA IN PAUSA");

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

    //-------------------------------------------------------------------------------------------------------------------------------------------------------------------
    // NOTIFICHE
    //-------------------------------------------------------------------------------------------------------------------------------------------------------------------


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

    //-------------------------------------------------------------------------------------------------------------------------------------------------------------------

    private void creazioneNotifica(String channelID, int notificationID, String titolo, String testo, boolean siCancellaSulClick, boolean nonEliminabile, int priorita, boolean suoni) {
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);


        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, channelID)
                .setSmallIcon(R.mipmap.bicep_icon)
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
    //-------------------------------------------------------------------------------------------------------------------------------------------------------------------


}