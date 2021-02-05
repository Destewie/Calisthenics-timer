package paccoTimer;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;

import android.os.CountDownTimer;
import android.service.notification.StatusBarNotification;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

public class MainActivity extends AppCompatActivity
{
    public static long tempo_bottone1 = 60000;   //questi 4 sono tutti in millisecondi chiaramente
    public static long tempo_bottone2 = 90000;
    public static long tempo_bottone3 = 120000;
    public static long tempo_bottone4 = 150000;

    public static final String PREFERENZE = "prefs";

    public static final String TEMPO_IMPOSTATO = "tempoImpostato";
    public static final String TEMPO_RESTANTE = "tempoRestante";
    public static final String TEMPO_FINE = "tempoFine";
    public static final String VOCE = "voce";
    public static final String NOTIFICA = "notifica";
    public static final String CONTANDO = "contando";
    public static final String BOTTONE1 = "bottone1";
    public static final String BOTTONE2 = "bottone2";
    public static final String BOTTONE3 = "bottone3";
    public static final String BOTTONE4 = "bottone4";


    NotificationManagerCompat notificationManager;

    TextView tvCountDown;
    public static Button btn1, btn2, btn3, btn4;
    Button btnAvviaPausa, btnReset, btnImpostazioni;
    FloatingActionButton btnCustom;
    public static CountDownTimer timer;

    boolean contando = false; //se il tempo sta scorrendo
    long tempoInMillis = 150000; // 2:30 in millisecondi. È il tempo del recupero
    long tempoRestanteInMillis = tempoInMillis, oraDiFine;

    Random rand;
    int orientamentoTelefono;

    MediaPlayer mp;
    public static boolean  vocePartita = false; //è per cercare di debuggare il fatto che ogni tanto non partono le voci alla fine del timer del servizio
    public static boolean voce = true, notifica = false; //se sono entrambe false, allora vuole il silenzioso


    //-------------------------------------------------------------------------------------------------------------------------------------------------------------------
    // CICLO DI VITA DELL'ACTIVITY
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
        btn1 = findViewById(R.id.btnUnMin);
        btn2 = findViewById(R.id.btnUnMinEMezzo);
        btn3 = findViewById(R.id.btnDueMin);
        btn4 = findViewById(R.id.btnDueMinEMezzo);
        btnCustom = findViewById(R.id.fabCustom);

        Intent i = new Intent(this, SettingsActivity.class);

        tvCountDown.setText(calcolaTestoTimer());
        btnReset.setVisibility(View.INVISIBLE);

        orientamentoTelefono = MainActivity.this.getResources().getConfiguration().orientation;

        rand = new Random();

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

        btn1.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                cambiaTempoInMillis(tempo_bottone1);
            }
        });


        btn2.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                cambiaTempoInMillis(tempo_bottone2);
            }
        });


        btn3.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                cambiaTempoInMillis(tempo_bottone3);
            }
        });


        btn4.setOnClickListener(new View.OnClickListener()
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

        aggiornaBottoniRapidi(prefs.getLong(BOTTONE1, 60), prefs.getLong(BOTTONE2, 90), prefs.getLong(BOTTONE3, 120), prefs.getLong(BOTTONE4, 150));

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

        SharedPreferences prefs = getSharedPreferences(PREFERENZE, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        // Save your own state now
        editor.putLong(TEMPO_IMPOSTATO, tempoInMillis);
        editor.putLong(TEMPO_RESTANTE, tempoRestanteInMillis);
        editor.putLong(TEMPO_FINE, oraDiFine);
        editor.putBoolean(VOCE, voce);
        editor.putBoolean(NOTIFICA, notifica);
        editor.putBoolean(CONTANDO, contando);

        editor.putLong(BOTTONE1, tempo_bottone1/1000);
        editor.putLong(BOTTONE2, tempo_bottone2/1000);
        editor.putLong(BOTTONE3, tempo_bottone3/1000);
        editor.putLong(BOTTONE4, tempo_bottone4/1000);

        editor.apply();

        try
        {
            timer.cancel();
        }
        catch (Exception e) {}
    }

    //-------------------------------------------------------------------------------------------------------------------------------------------------------------------
    // CAMBI AL TIMER
    //-------------------------------------------------------------------------------------------------------------------------------------------------------------------

    public void cambiaTempoInMillis(long t)
    {
        tempoInMillis = t;

        resetTimer();
    }

    //-------------------------------------------------------------------------------------------------------------------------------------------------------------------

    public void avviaTimer()
    {
        oraDiFine = System.currentTimeMillis() + tempoRestanteInMillis;

        if(!servizioAttivo())
            startService(oraDiFine - 300); //tolgo quei 3 decimi di secondo dal timer del servizio per provare a debuggare il fatto che ogni tanto non partono gli audio alla fine del servizio

        contando = true;
        timer = new CountDownTimer(tempoRestanteInMillis,10)
        {
            @Override
            public void onTick(long millisAllaFine)
            {
                tempoRestanteInMillis = millisAllaFine;
                tvCountDown.setText(calcolaTestoTimer());
            }

            @Override
            public void onFinish()
            {
                resetTimer();
                tvCountDown.setText("POMPA!");

                 if(!vocePartita && voce) //in teoria questo timer dovrebbe scadere dopo il timer del servizio, per questo motivo questo controllo ha senso
                 {
                     rand = new Random();

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
                     }

                     mp.start();
                 }
            }

        }.start();

        btnAvviaPausa.setText("Ferma Timer");
        btnReset.setVisibility(View.INVISIBLE);
    }

    //-------------------------------------------------------------------------------------------------------------------------------------------------------------------

    public void stoppaTimer()
    {
        stopService();
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
            stopService();
            timer.cancel();
            contando = false;
        }

        //distruggiNotifica();


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
    // BOTTONI RAPIDI
    //-------------------------------------------------------------------------------------------------------------------------------------------------------------------
    
    public static void aggiornaBottoniRapidi(long sec1, long sec2, long sec3, long sec4) //i 4 parametri vanno in secondi e non in millisecondi
    {
        int minuti, secondi;

        tempo_bottone1 = sec1 * 1000;
        minuti = (int) tempo_bottone1 / 1000 / 60;
        secondi = (int) (tempo_bottone1 / 1000) % 60 ;
        btn1.setText(String.format("%01d:%02d", minuti, secondi));

        tempo_bottone2 = sec2 * 1000;
        minuti = (int) tempo_bottone2 / 1000 / 60;
        secondi = (int) (tempo_bottone2 / 1000) % 60 ;
        btn2.setText(String.format("%01d:%02d", minuti, secondi));

        tempo_bottone3 = sec3 * 1000;
        minuti = (int) tempo_bottone3 / 1000 / 60;
        secondi = (int) (tempo_bottone3 / 1000) % 60 ;
        btn3.setText(String.format("%01d:%02d", minuti, secondi));

        tempo_bottone4 = sec4 * 1000;
        minuti = (int) tempo_bottone4 / 1000 / 60;
        secondi = (int) (tempo_bottone4 / 1000) % 60 ;
        btn4.setText(String.format("%01d:%02d", minuti, secondi));

    }

    //-------------------------------------------------------------------------------------------------------------------------------------------------------------------
    // NOTIFICHE
    //-------------------------------------------------------------------------------------------------------------------------------------------------------------------

    //-------------------------------------------------------------------------------------------------------------------------------------------------------------------


    //-------------------------------------------------------------------------------------------------------------------------------------------------------------------

    public void distruggiNotifica()
    {
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        StatusBarNotification[] notifications = mNotificationManager.getActiveNotifications();
        for (StatusBarNotification notification : notifications)
        {
            if (notification.getId() == ServizioTimer.NOTIF_ID_TEMPORESTANTE)    //se la notifica è attiva in questo momento, allora cancellala
            {
                notificationManager.cancel( ServizioTimer.NOTIF_ID_TEMPORESTANTE ) ;
            }
        }
    }

    //-------------------------------------------------------------------------------------------------------------------------------------------------------------------
    // SERVIZIO TIMER
    //-------------------------------------------------------------------------------------------------------------------------------------------------------------------

    public void startService (long oraFine)
    {
        Intent serviceIntent = new Intent(this, ServizioTimer.class);
        serviceIntent.putExtra(TEMPO_FINE, oraFine);

        startService(serviceIntent);
    }

    //-------------------------------------------------------------------------------------------------------------------------------------------------------------------

    public void stopService()
    {
        Intent serviceIntent = new Intent(this, ServizioTimer.class);
        stopService(serviceIntent);
    }

    //-------------------------------------------------------------------------------------------------------------------------------------------------------------------

    public boolean servizioAttivo()
    {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);

        for (ActivityManager.RunningServiceInfo s : manager.getRunningServices(Integer.MAX_VALUE))
        {
            if (s.service.getClassName().equals(ServizioTimer.class.getName())) {
                return true;
            }
        }
        return false;
    }


    //-------------------------------------------------------------------------------------------------------------------------------------------------------------------
    //-------------------------------------------------------------------------------------------------------------------------------------------------------------------

}